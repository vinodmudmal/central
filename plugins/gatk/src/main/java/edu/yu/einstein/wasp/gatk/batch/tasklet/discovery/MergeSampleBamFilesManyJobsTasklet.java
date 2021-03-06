package edu.yu.einstein.wasp.gatk.batch.tasklet.discovery;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.Assert;
import edu.yu.einstein.wasp.daemon.batch.tasklets.LaunchManyJobsTasklet;
import edu.yu.einstein.wasp.exception.SampleTypeException;
import edu.yu.einstein.wasp.exception.WaspMessageBuildingException;
import edu.yu.einstein.wasp.exception.WaspRuntimeException;
import edu.yu.einstein.wasp.filetype.service.FileTypeService;
import edu.yu.einstein.wasp.gatk.service.GatkService;
import edu.yu.einstein.wasp.gatk.software.GATKSoftwareComponent;
import edu.yu.einstein.wasp.integration.messages.WaspSoftwareJobParameters;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleSource;
import edu.yu.einstein.wasp.plugin.fileformat.plugin.BamFileTypeAttribute;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.JobService;
import edu.yu.einstein.wasp.service.SampleService;

/**
 * Once pre-processing of each lane individually, lanes belonging to the same sample are merged into a single BAM file.
 * @author asmclellan
 *
 */
public class MergeSampleBamFilesManyJobsTasklet extends LaunchManyJobsTasklet {
	
	private static Logger logger = LoggerFactory.getLogger(MergeSampleBamFilesManyJobsTasklet.class);
		
	@Autowired
	private JobService jobService;
	
	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private GatkService gatkService;
	
	@Autowired
	@Qualifier("bamServiceImpl")
	private FileTypeService fileTypeService;
	
	@Autowired
	private FileType bamFileType;
	
	@Autowired
	private FileType baiFileType;
	
	@Autowired
	private FileType textFileType;
	
	@Autowired
	private GATKSoftwareComponent gatk;
	
	private Integer jobId;

	public MergeSampleBamFilesManyJobsTasklet(Integer jobId) {
		this.jobId = jobId;
	}
	
	@Override
	@Transactional("entityManager")
	public void doExecute() {
		Job job = jobService.getJobByJobId(jobId);
		Assert.assertTrue(job.getId() > 0);
		Map<Sample, FileGroup> mergedSampleFileGroupsForNextStep = new HashMap<>();
		Map<Sample, FileGroup> passThroughSampleFileGroupsForNextStep = new HashMap<>();
		Map<Sample, LinkedHashSet<FileGroup>> sampleFileGroups = new HashMap<>();
		Map<Sample, LinkedHashSet<SampleSource>> sampleCellLibraries = new HashMap<>();
		LinkedHashSet<FileGroup> temporaryFileSet = new LinkedHashSet<>();
		try {
			List<SampleSource> cellLibraries = sampleService.getCellLibrariesThatPassedQCForJob(job);
			logger.debug("Cell libraries passing QC to process " + cellLibraries.size());
			if (cellLibraries.isEmpty())
				throw new WaspRuntimeException("Unable to run GATK as no cell-libraries to process");
			for (SampleSource cl: cellLibraries){
				Sample sample = sampleService.getLibrary(cl);
				while (sample.getParent() != null)
					sample = sample.getParent();
				for (FileGroup fg : fileService.getFilesForCellLibraryByType(cl, bamFileType, gatkService.getCompleteGatkPreprocessBamFileAttributeSet(false), false)){
					if (!sampleFileGroups.containsKey(sample)){
						sampleFileGroups.put(sample, new LinkedHashSet<FileGroup>());
						sampleCellLibraries.put(sample, new LinkedHashSet<SampleSource>());
					}
					logger.debug("sample id=" + sample.getId() + " <- FileGroup id=" + fg.getId());
					logger.debug("sample id=" + sample.getId() + " <- cellLibrary id=" + cl.getId());
					sampleFileGroups.get(sample).add(fg);
					sampleCellLibraries.get(sample).add(cl);
				}
			}
		} catch (SampleTypeException e1) {
			e1.printStackTrace();
		}
		for (Sample sample: sampleFileGroups.keySet()){
			LinkedHashSet<FileGroup> inputFileGroups = new LinkedHashSet<FileGroup>(sampleFileGroups.get(sample));
			LinkedHashSet<FileGroup> outputFileGroups = new LinkedHashSet<>();
			if (inputFileGroups.size() <= 1){
				logger.debug("Sample id=" + sample.getId() + ": has only " + inputFileGroups.size() + " file group. No merge required");
				if (inputFileGroups.size() == 1)
					passThroughSampleFileGroupsForNextStep.put(sample, sampleFileGroups.get(sample).iterator().next());
			} else{
				boolean isDedup = false;
				for (FileGroup ifg : inputFileGroups){
					if (fileTypeService.hasAttribute(ifg, BamFileTypeAttribute.DEDUP)){
						isDedup = true;
						break;
					}
				}
				logger.debug("Sample id=" + sample.getId() + ": going to merge " + inputFileGroups.size() + " file groups.");
				Map<String, String> jobParameters = new HashMap<>();
				String fileNameSuffix = "gatk_preproc_merged";
				if (isDedup)
					fileNameSuffix += "_dedup";
				String bamOutput = fileService.generateUniqueBaseFileName(sample) + fileNameSuffix + ".bam";
				String baiOutput = fileService.generateUniqueBaseFileName(sample) + fileNameSuffix + ".bai";
				FileGroup bamG = new FileGroup();
				FileHandle bam = new FileHandle();
				bam.setFileName(bamOutput);
				bam.setFileType(bamFileType);
				bamG.setIsActive(0);
				bamG.addFileHandle(bam);
				bamG.setFileType(bamFileType);
				bamG.setDescription(bamOutput);
				bamG.setSoftwareGeneratedById(gatk.getId());
				bamG.setDerivedFrom(inputFileGroups);
				bamG = fileService.saveInDiscreteTransaction(bamG, gatkService.getCompleteGatkPreprocessBamFileAttributeSet(isDedup));
				outputFileGroups.add(bamG);
				mergedSampleFileGroupsForNextStep.put(sample, bamG);
	
				FileGroup baiG = new FileGroup();
				FileHandle bai = new FileHandle();
				bai.setFileName(baiOutput);
				bai.setFileType(baiFileType);
				baiG.setIsActive(0);
				baiG.addFileHandle(bai);
				baiG.setFileType(baiFileType);
				baiG.setDescription(baiOutput);
				baiG.setSoftwareGeneratedById(gatk.getId());
				baiG.addDerivedFrom(bamG);
				baiG = fileService.saveInDiscreteTransaction(baiG, null);
				outputFileGroups.add(baiG);
				if (isDedup){
					String metricsOutput = fileService.generateUniqueBaseFileName(sample) + "gatk_preproc_merged_dedupMetrics.txt";
					FileGroup metricsG = new FileGroup();
					FileHandle metrics = new FileHandle();
					metrics.setFileName(metricsOutput);
					metrics.setFileType(textFileType);
					metricsG.setIsActive(0);
					metricsG.addFileHandle(metrics);
					metricsG.setFileType(textFileType);
					metricsG.setDescription(metricsOutput);
					metricsG.setSoftwareGeneratedById(gatk.getId());
					metricsG.addDerivedFrom(bamG);
					metricsG = fileService.saveInDiscreteTransaction(metricsG, null);
					outputFileGroups.add(metricsG);
				}
				temporaryFileSet.addAll(outputFileGroups);
				//jobParameters.put("uniqCode", Long.toString(Calendar.getInstance().getTimeInMillis())); // overcomes limitation of job being run only once
				jobParameters.put(WaspSoftwareJobParameters.FILEGROUP_ID_LIST_INPUT, AbstractGatkTasklet.getModelIdsAsCommaDelimitedString(inputFileGroups));
				jobParameters.put(WaspSoftwareJobParameters.FILEGROUP_ID_LIST_OUTPUT, AbstractGatkTasklet.getModelIdsAsCommaDelimitedString(outputFileGroups));
				jobParameters.put(WaspSoftwareJobParameters.JOB_ID, jobId.toString());
				jobParameters.put(WaspSoftwareJobParameters.IS_DEDUP, Boolean.toString(isDedup));
				try {
					requestLaunch("gatk.variantDiscovery.mergeSampleBamFiles.jobFlow", jobParameters);
				} catch (WaspMessageBuildingException e) {
					e.printStackTrace();
				}
			} 
		}
		// put files needed for next step into step execution context to be promoted to job context
		getStepExecution().getExecutionContext().put("mergedSampleFgMap", AbstractGatkTasklet.getSampleFgMapAsJsonString(mergedSampleFileGroupsForNextStep));
		getStepExecution().getExecutionContext().put("passThroughSampleFgMap", AbstractGatkTasklet.getSampleFgMapAsJsonString(passThroughSampleFileGroupsForNextStep));
		getStepExecution().getExecutionContext().put("temporaryFileSet", AbstractGatkTasklet.getModelIdsAsCommaDelimitedString(temporaryFileSet));
	}

}
