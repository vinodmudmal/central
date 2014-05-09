package edu.yu.einstein.wasp.gatk.batch.tasklet.preprocess;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.Assert;
import edu.yu.einstein.wasp.daemon.batch.tasklets.WaspRemotingTasklet;
import edu.yu.einstein.wasp.filetype.service.FileTypeService;
import edu.yu.einstein.wasp.gatk.service.GatkService;
import edu.yu.einstein.wasp.gatk.software.GATKSoftwareComponent;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.grid.work.WorkUnit.ExecutionMode;
import edu.yu.einstein.wasp.grid.work.WorkUnit.ProcessMode;
import edu.yu.einstein.wasp.integration.messages.WaspSoftwareJobParameters;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.SampleSource;
import edu.yu.einstein.wasp.plugin.fileformat.plugin.BamFileTypeAttribute;
import edu.yu.einstein.wasp.plugin.supplemental.organism.Build;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.GenomeService;
import edu.yu.einstein.wasp.service.JobService;
import edu.yu.einstein.wasp.service.SampleService;
import edu.yu.einstein.wasp.software.SoftwarePackage;

/**
 * @author jcai
 * @author asmclellan
 */
public class BamPreProcessingTasklet extends WaspRemotingTasklet implements StepExecutionListener {

	private Integer cellLibraryId;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	@Qualifier("bamServiceImpl")
	private FileTypeService fileTypeService;

	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private JobService jobService;
	
	@Autowired
	private GridHostResolver gridHostResolver;
	
	@Autowired
	private FileType bamFileType;
	
	@Autowired
	private FileType baiFileType;
	
	@Autowired
	private GenomeService genomeService;
	
	@Autowired
	private GatkService gatkService;
	
	@Autowired
	private GATKSoftwareComponent gatk;

	public BamPreProcessingTasklet() {
		// proxy
	}

	public BamPreProcessingTasklet(String cellLibraryIds) {
		List<Integer> cids = WaspSoftwareJobParameters.getCellLibraryIdList(cellLibraryIds);
		Assert.assertTrue(cids.size() == 1);
		this.cellLibraryId = cids.get(0);
	}

	@Override
	@Transactional("entityManager")
	public void doExecute(ChunkContext context) throws Exception {
		SampleSource cellLib = sampleService.getSampleSourceDao().findById(cellLibraryId);
		Build build = genomeService.getGenomeBuild(cellLib);
		StepExecution stepExecution = context.getStepContext().getStepExecution();
		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
		
		Job job = sampleService.getJobOfLibraryOnCell(cellLib);
		
		logger.debug("Beginning GATK preprocessing for cellLibrary " + cellLib.getId() + " from Wasp job " + job.getId());
		
		Set<BamFileTypeAttribute> attributes = new HashSet<>();
		attributes.add(BamFileTypeAttribute.SORTED);
		attributes.add(BamFileTypeAttribute.DEDUP);
		Set<FileGroup> sourceBamFileGroups = fileService.getFilesForCellLibraryByType(cellLib, bamFileType, attributes, true);
		logger.debug("# bam FileGroups (sorted and dedup) for cell library id=" + cellLib.getId() + " is " +sourceBamFileGroups.size());
		Assert.assertTrue(sourceBamFileGroups.size() == 1, "The number of filegroups (" + sourceBamFileGroups.size() + ") is not equal to 1");
		FileGroup fg = sourceBamFileGroups.iterator().next();
		List<FileHandle> fhlist = new ArrayList<FileHandle>();
		fhlist.addAll(fg.getFileHandles());
		
		logger.debug("Bam File group: " + fg.getId() + ": " + fg.getDescription());
		
		WorkUnit w = new WorkUnit();
		w.setMode(ExecutionMode.PROCESS);
		w.setMemoryRequirements(GATKSoftwareComponent.MEMORY_REQUIRED_8);
		w.setProcessMode(ProcessMode.MAX);
		w.setMemoryRequirements(GATKSoftwareComponent.MEMORY_REQUIRED_8);
		w.setProcessorRequirements(GATKSoftwareComponent.NUM_THREADS);
		w.setWorkingDirectory(WorkUnit.SCRATCH_DIR_PLACEHOLDER);
		w.setRequiredFiles(fhlist);
		List<SoftwarePackage> sd = new ArrayList<SoftwarePackage>();
		sd.add(gatk);
		w.setSoftwareDependencies(sd);
		w.setSecureResults(true);
		String bamOutput = fileService.generateUniqueBaseFileName(cellLib) + "gatk_dedup_realn_recal.bam";
		String baiOutput = fileService.generateUniqueBaseFileName(cellLib) + "gatk_dedup_realn_recal.bai";
		FileGroup bamG = new FileGroup();
		FileHandle bam = new FileHandle();
		bam.setFileName(bamOutput);
		bam = fileService.addFile(bam);
		bamG.addFileHandle(bam);
		bamG.setFileType(bamFileType);
		bamG.setDescription(bamOutput);
		bamG.setSoftwareGeneratedById(gatk.getId());
		bamG = fileService.addFileGroup(bamG);
		fileTypeService.setAttributes(bamG, gatkService.getCompleteGatkPreprocessBamFileAttributeSet());
		Integer bamGId = bamG.getId();
		// save in step context  for use later
		stepExecutionContext.put("bamGID", bamGId);
		
		
		FileGroup baiG = new FileGroup();
		FileHandle bai = new FileHandle();
		bai.setFileName(baiOutput);
		bai = fileService.addFile(bai);
		baiG.addFileHandle(bai);
		baiG.setFileType(baiFileType);
		baiG.setDescription(baiOutput);
		baiG.setSoftwareGeneratedById(gatk.getId());
		baiG = fileService.addFileGroup(baiG);
		Integer baiGId = baiG.getId();
		// save in step context for use later
		stepExecutionContext.put("baiGID", baiGId);
		
		w.getResultFiles().add(bamG);
		w.getResultFiles().add(baiG);
		w.setResultsDirectory(WorkUnit.RESULTS_DIR_PLACEHOLDER + "/" + job.getId());

		String inputBamFilename = "${" + WorkUnit.INPUT_FILE + "}";
		String intervalFileName = "gatk.${" + WorkUnit.JOB_NAME + "}.realign.intervals";
		String realignBamFilename = "gatk.${" + WorkUnit.JOB_NAME + "}.realign.bam";
		String recaliGrpFilename = "gatk.${" + WorkUnit.JOB_NAME + "}.recali.grp";
		String recaliBamFilename = "${" + WorkUnit.OUTPUT_FILE + "[0]}";
		String recaliBaiFilename = "${" + WorkUnit.OUTPUT_FILE + "[1]}";
		Set<String> inputFilenames = new HashSet<>();
		inputFilenames.add(inputBamFilename);
		w.addCommand(gatk.getCreateTargetCmd(build, inputFilenames, intervalFileName));
		w.addCommand(gatk.getLocalAlignCmd(build, inputFilenames, intervalFileName, realignBamFilename, null));
		w.addCommand(gatk.getRecaliTableCmd(build, realignBamFilename, recaliGrpFilename));
		w.addCommand(gatk.getPrintRecaliCmd(build, realignBamFilename, recaliGrpFilename, recaliBamFilename, recaliBaiFilename));

		GridResult result = gridHostResolver.execute(w);
		
		//place the grid result in the step context
		storeStartedResult(context, result);
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	@Transactional("entityManager")
	public void doPreFinish(ChunkContext context) throws Exception {
		StepExecution stepExecution = context.getStepContext().getStepExecution();
		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
		Integer bamGId = stepExecutionContext.getInt("bamGID");
		Integer baiGId = stepExecutionContext.getInt("baiGID");
		
		// register .bam and .bai file groups with cellLib so as to make available to views
		SampleSource cellLib = sampleService.getSampleSourceDao().findById(cellLibraryId);
		if (bamGId != null && cellLib.getId() != 0)
			fileService.setSampleSourceFile(fileService.getFileGroupById(bamGId), cellLib);
		if (baiGId != null && cellLib.getId() != 0)
			fileService.setSampleSourceFile(fileService.getFileGroupById(baiGId), cellLib);	
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

}
