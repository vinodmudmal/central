/**
 * 
 */
package edu.yu.einstein.wasp.plugin.bwa.batch.tasklet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.daemon.batch.tasklets.WaspRemotingTasklet;
import edu.yu.einstein.wasp.filetype.service.FileTypeService;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.grid.work.WorkUnitGridConfiguration;
import edu.yu.einstein.wasp.grid.work.WorkUnitGridConfiguration.ExecutionMode;
import edu.yu.einstein.wasp.grid.work.WorkUnitGridConfiguration.ProcessMode;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.SampleSource;
import edu.yu.einstein.wasp.plugin.bwa.software.AbstractBWASoftwareComponent;
import edu.yu.einstein.wasp.plugin.bwa.software.BWABacktrackSoftwareComponent;
import edu.yu.einstein.wasp.plugin.bwa.software.BWAMemSoftwareComponent;
import edu.yu.einstein.wasp.plugin.fileformat.plugin.BamFileTypeAttribute;
import edu.yu.einstein.wasp.plugin.fileformat.service.FastqService;
import edu.yu.einstein.wasp.plugin.mps.grid.software.Samtools;
import edu.yu.einstein.wasp.plugin.picard.software.Picard;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.SampleService;


/**
 * @author calder
 *
 */
public class BWAMergeSortDedupTasklet extends WaspRemotingTasklet implements StepExecutionListener {
	
	private static int MEMORY_GB_4 = 4;
	
	private final String BWA_BACKTRACK_BAM_TAG_INDICATING_UNIQUE_ALIGNMENT = "XT:A:U";
	
	private final String BWA_MEM_BAM_TAG_INDICATING_UNIQUE_ALIGNMENT = "";
	
	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private FileTypeService bamServiceImpl;
	
	@Autowired
	private FastqService fastqService;
	
	@Autowired
	private FileType fastqFileType;
	
	@Autowired
	private FileType bamFileType;
	
	@Autowired
	private FileType baiFileType;
	
	@Autowired
	private FileType textFileType;
	
	@Autowired
	private GridHostResolver gridHostResolver;
	
	@Autowired
	private BWABacktrackSoftwareComponent bwaBacktrack;
	
	@Autowired
	private BWAMemSoftwareComponent bwaMem;
	
	public BWAMergeSortDedupTasklet() {
		// proxy
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	@Transactional("entityManager")
	public GridResult doExecute(ChunkContext context) throws Exception {
		StepExecution stepExecution = context.getStepContext().getStepExecution();
		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
		ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
		
		// retrieve attributes persisted in jobExecutionContext
		String scratchDirectory = jobExecutionContext.get("scrDir").toString();
		Integer cellLibId = jobExecutionContext.getInt("cellLibId");
		AbstractBWASoftwareComponent bwa = bwaMem;
		String bamTagIndicatingUniqueAlignment = BWA_MEM_BAM_TAG_INDICATING_UNIQUE_ALIGNMENT;
		if (jobExecutionContext.getString("method").equals("backtrack")){
			bwa = bwaBacktrack;
			bamTagIndicatingUniqueAlignment = BWA_BACKTRACK_BAM_TAG_INDICATING_UNIQUE_ALIGNMENT;
		}
		
		Picard picard = (Picard) bwa.getSoftwareDependencyByIname("picard");
		Samtools samtools = (Samtools) bwa.getSoftwareDependencyByIname("samtools");
		
		SampleSource cellLib = sampleService.getSampleSourceDao().findById(cellLibId);
		Set<SampleSource> cellLibraries = new HashSet<>();
		cellLibraries.add(cellLib);

		Job job = sampleService.getJobOfLibraryOnCell(cellLib);

		logger.debug("Beginning sort/merge of BAM files for " + cellLib.getId() + " from job " + job.getId());
		logger.debug("Starting from previously aln'd scratch directory " + scratchDirectory);
		
		Set<FileGroup> fastqFileGroups = fileService.getFilesForCellLibraryByType(cellLib, fastqFileType);

		Map<String,Object> jobParameters = context.getStepContext().getJobParameters();
		boolean markDuplicates = false;
		if (!jobParameters.containsKey("markDuplicates") || jobParameters.get("markDuplicates").equals("yes"))
			markDuplicates = true;
		WorkUnitGridConfiguration c = new WorkUnitGridConfiguration();
		c.setMode(ExecutionMode.PROCESS);
		c.setProcessMode(ProcessMode.FIXED);
		c.setMemoryRequirements(MEMORY_GB_4);
		
		c.setSoftwareDependencies(bwa.getSoftwareDependencies());
		c.setWorkingDirectory(scratchDirectory);
		c.setResultsDirectory(WorkUnitGridConfiguration.RESULTS_DIR_PLACEHOLDER + "/" + job.getId() + "/bwa");
		
		WorkUnit w = new WorkUnit(c);
		w.setSecureResults(true);
		
		String bamOutput = fileService.generateUniqueBaseFileName(cellLib) + "bwa.bam";
		LinkedHashSet<FileHandle> files = new LinkedHashSet<FileHandle>();
		FileGroup bamG = new FileGroup();
		FileHandle bam = new FileHandle();
		bam.setFileName(bamOutput);
		bam.setFileType(bamFileType);
		bam = fileService.addFile(bam);
		bamG.setIsActive(0);
		bamG.addFileHandle(bam);
		files.add(bam);
		bamG.setFileType(bamFileType);
		bamG.setDerivedFrom(fastqFileGroups);
		bamG.setDescription(bamOutput);
		bamG.setSoftwareGeneratedById(bwa.getId());
		
		bamG = fileService.addFileGroup(bamG);
		bamServiceImpl.addAttribute(bamG, BamFileTypeAttribute.SORTED);
		Integer bamGId = bamG.getId();
		
		fileService.setSampleSourceFile(bamG, cellLib);
		// save in step context  for use later
		stepExecutionContext.put("bamGID", bamGId);
		
		String baiOutput = fileService.generateUniqueBaseFileName(cellLib) + "bwa.bai";
		FileGroup baiG = new FileGroup();
		FileHandle bai = new FileHandle();
		bai.setFileName(baiOutput);
		bai.setFileType(baiFileType);
		bai = fileService.addFile(bai);
		baiG.setIsActive(0);
		baiG.addFileHandle(bai);
		files.add(bai);
		baiG.setFileType(baiFileType);
		baiG.setDescription(baiOutput);
		baiG.addDerivedFrom(bamG);
		baiG.setSoftwareGeneratedById(bwa.getId());
		baiG = fileService.addFileGroup(baiG);
		Integer baiGId = baiG.getId();
		
		// save in step context for use later
		stepExecutionContext.put("baiGID", baiGId);
		
		String metricsOutput = fileService.generateUniqueBaseFileName(cellLib) + "alignmentMetrics.txt";
		FileGroup metricsG = new FileGroup();
		FileHandle metrics = new FileHandle();
		metrics.setFileName(metricsOutput);
		metrics.setFileType(textFileType);
		metrics = fileService.addFile(metrics);
		metricsG.setIsActive(0);
		metricsG.addFileHandle(metrics);
		files.add(metrics);
		metricsG.setFileType(textFileType);
		metricsG.setDescription(metricsOutput);
		metricsG.setSoftwareGeneratedById(bwa.getId());
		metricsG.addDerivedFrom(bamG);
		metricsG = fileService.addFileGroup(metricsG);
		Integer metricsGId = metricsG.getId();
	
		// save in step context for use later
		stepExecutionContext.put("metricsGID", metricsGId);
		w.setCommand("rm -f *." + WorkUnit.PROCESSING_INCOMPLETE_FILENAME); // we're working in existing scratch subfolder so start by removing old markers
		w.setCommand("shopt -s nullglob\n");
		w.addCommand("for x in sam.*.out; do ln -sf ${x} ${x/*-/}.sam ; done\n");
		String outputBamFilename = "${" + WorkUnit.OUTPUT_FILE + "[0]}";
		String outputBaiFilename = "${" + WorkUnit.OUTPUT_FILE + "[1]}";			
		String dedupMetricsFilename = "${" + WorkUnit.OUTPUT_FILE + "[2]}";
		
		if (markDuplicates){
			bamServiceImpl.addAttribute(bamG, BamFileTypeAttribute.DEDUP);			

			// save in step context for use later
			stepExecutionContext.put("metricsGID", metricsGId);

			String tempMergedBamFilename = "merged.${" + WorkUnit.OUTPUT_FILE + "[0]}";
			
			w.addCommand(picard.getMergeBamCmd("*.out.sam", tempMergedBamFilename, null, MEMORY_GB_4));
			w.addCommand(picard.getMarkDuplicatesCmd(tempMergedBamFilename, outputBamFilename, outputBaiFilename, dedupMetricsFilename, MEMORY_GB_4));
			
			//List<String> nfrCommandList = samtools.getCommandsForNonRedundantFraction(outputBamFilename, bamTagIndicatingUniqueAlignment);
			List<String> nfrCommandList = samtools.getCommandsForNonRedundantFraction(outputBamFilename);
			for(String nrfCommand : nfrCommandList){
				w.addCommand(nrfCommand);
			}
			
			w.addCommand("ln -sf " + dedupMetricsFilename + " " + metricsOutput);//permits reading of file metricsOutput from scratch/dedupMetricsFilename 
			
		} else {
			String tempMarkedDupOutputBamNotToBeSaved = "tempMarkedDupOutputBamNotToBeSaved.bam";
			String tempBaiNotToBeSaved = "tempBaiNotToBeSaved.bai";
			
			w.addCommand(picard.getMergeBamCmd("*.out.sam", outputBamFilename, outputBaiFilename, MEMORY_GB_4));
			//in this way, the final .bam file is NOT marked for duplicates, but the alignement stats are still obtained
			w.addCommand(picard.getMarkDuplicatesCmd(outputBamFilename, tempMarkedDupOutputBamNotToBeSaved, tempBaiNotToBeSaved, dedupMetricsFilename, MEMORY_GB_4));
			
			//List<String> nfrCommandList = samtools.getCommandsForNonRedundantFraction(tempMarkedDupOutputBamNotToBeSaved, bamTagIndicatingUniqueAlignment);
			List<String> nfrCommandList = samtools.getCommandsForNonRedundantFraction(tempMarkedDupOutputBamNotToBeSaved);
			for(String nrfCommand : nfrCommandList){
				w.addCommand(nrfCommand);
			}
			
			w.addCommand("ln -sf " + dedupMetricsFilename + " " + metricsOutput);//permits reading of file metricsOutput from scratch/dedupMetricsFilename 

		}	
		w.setSecureResults(true);
		
		w.setResultFiles(files);
		
		return gridHostResolver.execute(w);
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	@Transactional("entityManager")
	public void doPreFinish(ChunkContext context) throws Exception {
		StepExecution stepExecution = context.getStepContext().getStepExecution();
		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
		ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
		Integer bamGId = stepExecutionContext.getInt("bamGID");
		Integer baiGId = stepExecutionContext.getInt("baiGID");
		Integer metricsGId = null; 
		if (stepExecutionContext.containsKey("metricsGID"))
			metricsGId = stepExecutionContext.getInt("metricsGID");
		
		Picard picard = (Picard) bwaMem.getSoftwareDependencyByIname("picard");
		
		// retrieve attributes persisted in jobExecutionContext
		String scratchDirectory = jobExecutionContext.get("scrDir").toString();
		Integer cellLibId = jobExecutionContext.getInt("cellLibId");		
		SampleSource cellLib = sampleService.getSampleSourceDao().findById(cellLibId);

		// register .bam and .bai file groups as active to make them available to views
		if (bamGId != null)
			fileService.getFileGroupById(bamGId).setIsActive(1);
		if (baiGId != null)
			fileService.getFileGroupById(baiGId).setIsActive(1);	
		if (metricsGId != null){
			//fileService.getFileGroupById(metricsGId).setIsActive(1);
			FileGroup metricsG = fileService.getFileGroupById(metricsGId);
			metricsG.setIsActive(1);
			List<FileHandle> fileHandleList = new ArrayList<FileHandle>(metricsG.getFileHandles());
			if(fileHandleList.size()==1 && bamGId != null){	//save the metrics information with the BamFileGroupMeta			
				picard.saveAlignmentMetrics(bamGId, fileHandleList.get(0).getFileName(), scratchDirectory, this.gridHostResolver);				
			}
		}
	}
	

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return super.afterStep(stepExecution);
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		super.beforeStep(stepExecution);
	}

	@Override
	@Transactional("entityManager")
	public void doCleanupBeforeRestart(StepExecution stepExecution) throws Exception {
		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
		Integer bamGId = null;
		if (stepExecutionContext.containsKey("bamGID"))
			bamGId = stepExecutionContext.getInt("bamGID");
		Integer baiGId = null;
		if (stepExecutionContext.containsKey("baiGID"))
			baiGId = stepExecutionContext.getInt("baiGID");
		Integer metricsGId = null; 
		if (stepExecutionContext.containsKey("metricsGID"))
			metricsGId = stepExecutionContext.getInt("metricsGID");
		logger.debug("Cleaning filegroup entries");
		// remove .bam and .bai file group entries
		if (bamGId != null)
			fileService.removeWithAllAssociatedFilehandles(fileService.getFileGroupById(bamGId));
		if (baiGId != null)
			fileService.removeWithAllAssociatedFilehandles(fileService.getFileGroupById(baiGId));
		if (metricsGId != null)
			fileService.removeWithAllAssociatedFilehandles(fileService.getFileGroupById(metricsGId));
	}

}
