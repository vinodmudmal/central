package edu.yu.einstein.wasp.chipseq.batch.tasklet;

/**
 * R. Dubin
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.Assert;
import edu.yu.einstein.wasp.chipseq.integration.messages.ChipSeqSoftwareJobParameters;
import edu.yu.einstein.wasp.chipseq.service.ChipSeqService;
import edu.yu.einstein.wasp.daemon.batch.tasklets.LaunchManyJobsTasklet;
import edu.yu.einstein.wasp.exception.SoftwareConfigurationException;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.integration.messages.WaspJobParameters;
import edu.yu.einstein.wasp.integration.messages.WaspSoftwareJobParameters;
import edu.yu.einstein.wasp.integration.messages.tasks.BatchJobTask;
import edu.yu.einstein.wasp.interfacing.plugin.BatchJobProviding;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.ResourceType;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleMeta;
import edu.yu.einstein.wasp.model.SampleSource;
import edu.yu.einstein.wasp.plugin.WaspPluginRegistry;
import edu.yu.einstein.wasp.plugin.mps.service.SequencingService;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.GenomeService;
import edu.yu.einstein.wasp.service.JobService;
import edu.yu.einstein.wasp.service.SampleService;
import edu.yu.einstein.wasp.util.SoftwareConfiguration;
import edu.yu.einstein.wasp.util.WaspJobContext;

@Transactional("entityManager")
//public class PeakCallerTasklet extends WaspRemotingTasklet implements StepExecutionListener {
public class PeakCallerTasklet extends LaunchManyJobsTasklet {
	
	@Autowired
	private ChipSeqService chipSeqService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private SequencingService sequencingService;
	
	@Autowired
	private JobService jobService;
	
	@Autowired
	private GenomeService genomeService;
	
	@Autowired
	private WaspPluginRegistry waspPluginRegistry;

	@Autowired
	private GridHostResolver gridHostResolver;
	
	private ResourceType softwareResourceType;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public PeakCallerTasklet() {
		// proxy
	}

	public PeakCallerTasklet(ResourceType softwareResourceType) {
		this.softwareResourceType = softwareResourceType;
	}

	@Override
	@Transactional("entityManager")
	public void doExecute() {
		
		Map<String,JobParameter> jobParametersMap = getStepExecution().getJobParameters().getParameters();	
		Integer jobIdFromJobParameter = null;
		for (String key : getStepExecution().getJobParameters().getParameters().keySet()) {
			if(key.equalsIgnoreCase(WaspJobParameters.JOB_ID)){
				JobParameter jp = jobParametersMap.get(key);
				jobIdFromJobParameter = new Integer(jp.toString());
			}
		}
		
		Assert.assertTrue(jobIdFromJobParameter>0);
		Job job = jobService.getJobByJobId(jobIdFromJobParameter);
		Assert.assertTrue(job.getId()>0);
		
		List<SampleSource> approvedCellLibraryList = null;
		try{
			approvedCellLibraryList = sampleService.getCellLibrariesPassQCAndNoAggregateAnalysis(job);	
		}catch(Exception e){
			logger.debug("unable to obtain approvedCellLibraryList in PeakCallerTasklet; message = " + e.getMessage());
		}
		Assert.assertTrue( approvedCellLibraryList!=null && ! approvedCellLibraryList.isEmpty() );		
		Assert.assertTrue(this.sequencingService.confirmCellLibrariesAssociatedWithBamFiles(approvedCellLibraryList));

		Map<Sample, List<SampleSource>> approvedSampleApprovedCellLibraryListMap = sampleService.associateUppermostSampleWithCellLibraries(approvedCellLibraryList);
		//the Sample object is as high up as you can go (library if no macromolecule submitted; macromolecule otherwise).
		//This is needed since the samplePairs and the replicateSet reference the Sample as high up as it can go
		//and we also need to check the organism of each
		
		Set<Sample> setOfApprovedSamples = new HashSet<Sample>();//for a specific job (note: this really could have been a list)
		for (Sample approvedSample : approvedSampleApprovedCellLibraryListMap.keySet()) {
			setOfApprovedSamples.add(approvedSample);
		}
		
		Set<Sample> ipOfRequestedIPControlPairWithFilesForBoth = new HashSet<Sample>();
		for(Sample approvedSample : setOfApprovedSamples){
			if(chipSeqService.isIP(approvedSample)){//as of 6-17-14, only call peaks for IP samples 
				List<SampleSource> cellLibraryListForIP = approvedSampleApprovedCellLibraryListMap.get(approvedSample);
				//if(isPunctate(approvedSample){
					for(SampleSource ss : sampleService.getSamplePairsByJob(job)){//each chipseq IP sample (ipFromSamplePair) will appear only once in this samplePair set
						Sample ipFromSamplePair = ss.getSample();//IP 
						Sample controlFromSamplePair = ss.getSourceSample();//input 
						if(approvedSample.getId().intValue() == ipFromSamplePair.getId().intValue()){
							if(setOfApprovedSamples.contains(controlFromSamplePair)){
								Assert.assertTrue(sampleService.confirmSamplePairIsOfSameSpecies(ipFromSamplePair, controlFromSamplePair));
								List<SampleSource> cellLibraryListForControl = approvedSampleApprovedCellLibraryListMap.get(controlFromSamplePair);
								ipOfRequestedIPControlPairWithFilesForBoth.add(approvedSample);
								prepareAndLaunchMessage(job, sampleService.convertCellLibraryListToIdList(cellLibraryListForIP), sampleService.convertCellLibraryListToIdList(cellLibraryListForControl));
								break;//breaks out of: for(SampleSource ss : sampleService.getSamplePairsByJob(job))
							}
						}
					}
					if(!ipOfRequestedIPControlPairWithFilesForBoth.contains(approvedSample)){//not on samplePair List, or it is on samplePair List but there are no BAM files for the requested control 
						prepareAndLaunchMessage(job, sampleService.convertCellLibraryListToIdList(cellLibraryListForIP), new ArrayList<Integer>());						
					}
				//}
			}
		}		
	}

	private void prepareAndLaunchMessage(Job job, List<Integer> testCellLibraryIdList, List<Integer> controlCellLibraryIdList){
		try{
			//this used to work in next line, but it no longer functions; instead we get error: lazy load problem    new WaspJobContext(jobId, jobService)
			WaspJobContext waspJobContext = new WaspJobContext(jobService.getJobAndSoftware(job));
			SoftwareConfiguration softwareConfig = waspJobContext.getConfiguredSoftware(this.softwareResourceType);
			if (softwareConfig == null){
				throw new SoftwareConfigurationException("No software could be configured for jobId=" + job.getId() + " with resourceType iname=" + softwareResourceType.getIName());
			}
			BatchJobProviding softwarePlugin = waspPluginRegistry.getPlugin(softwareConfig.getSoftware().getIName(), BatchJobProviding.class);
			String flowName = softwarePlugin.getBatchJobName(BatchJobTask.GENERIC);
			if (flowName == null){
				logger.warn("No generic flow found for plugin so cannot launch software : " + softwareConfig.getSoftware().getIName());
			}
			Assert.assertTrue(flowName != null && !flowName.isEmpty());
			logger.warn("Flowname : " + flowName);//for macstwo, flowname will be: edu.yu.einstein.wasp.macstwo.mainFlow
			Map<String, String> jobParameters = softwareConfig.getParameters();
			jobParameters.put(ChipSeqSoftwareJobParameters.TEST_LIBRARY_CELL_ID_LIST, WaspSoftwareJobParameters.getCellLibraryListAsParameterValue(testCellLibraryIdList));
			jobParameters.put(ChipSeqSoftwareJobParameters.CONTROL_LIBRARY_CELL_ID_LIST, WaspSoftwareJobParameters.getCellLibraryListAsParameterValue(controlCellLibraryIdList));
			jobParameters.put(ChipSeqSoftwareJobParameters.JOB_ID, job.getId().toString());
			
			requestLaunch(flowName, jobParameters);
		}	
		catch(Exception e){
			logger.warn("e.message()   =   " + e.getMessage());
			throw new MessagingException(e.getLocalizedMessage(), e);
		}
	}


}