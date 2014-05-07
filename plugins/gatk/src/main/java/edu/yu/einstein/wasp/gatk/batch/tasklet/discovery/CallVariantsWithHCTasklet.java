package edu.yu.einstein.wasp.gatk.batch.tasklet.discovery;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.Strategy;
import edu.yu.einstein.wasp.Strategy.StrategyType;
import edu.yu.einstein.wasp.gatk.service.GatkService;
import edu.yu.einstein.wasp.gatk.software.GATKSoftwareComponent;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.grid.work.WorkUnit.ExecutionMode;
import edu.yu.einstein.wasp.grid.work.WorkUnit.ProcessMode;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.plugin.supplemental.organism.Build;
import edu.yu.einstein.wasp.service.JobService;
import edu.yu.einstein.wasp.service.StrategyService;
import edu.yu.einstein.wasp.software.SoftwarePackage;

/**
 *  @author asmclellan
 *  @author jcai
 */
public class CallVariantsWithHCTasklet extends AbstractGatkTasklet implements StepExecutionListener {
	
	@Autowired
	private JobService jobService;
	
	@Autowired
	private GatkService gatkService;
	
	@Autowired
	private StrategyService strategyService;
	
	public CallVariantsWithHCTasklet(String inputFilegroupIds, String outputFilegroupIds) {
		super(inputFilegroupIds, outputFilegroupIds);
	}

	@Override
	@Transactional("entityManager")
	public void doExecute(ChunkContext context) throws Exception {
		Build build = null;
		WorkUnit w = new WorkUnit();
		w.setMode(ExecutionMode.PROCESS);
		w.setProcessMode(ProcessMode.MAX);
		w.setMemoryRequirements(GATKSoftwareComponent.MEMORY_REQUIRED_8);
		w.setProcessorRequirements(GATKSoftwareComponent.NUM_THREADS);
		w.setSecureResults(true);
		w.setWorkingDirectory(WorkUnit.SCRATCH_DIR_PLACEHOLDER);
		w.setResultsDirectory(WorkUnit.RESULTS_DIR_PLACEHOLDER + "/" + jobId);
		LinkedHashSet<FileGroup> fglist = new LinkedHashSet<FileGroup>();
		for (Integer fgId : this.getOutputFilegroupIds()){
			fglist.add(fileService.getFileGroupById(fgId));
		}
		w.setResultFiles(fglist);
		List<FileHandle> fhlist = new ArrayList<FileHandle>();
		for (Integer fgId : this.getInputFilegroupIds()){
			FileGroup fg = fileService.getFileGroupById(fgId);
			if (fhlist.isEmpty()) // first entry not yet entered
				build = getBuildForFg(fg);
			fhlist.addAll(fg.getFileHandles());
		}
		w.setRequiredFiles(fhlist);
		List<SoftwarePackage> sd = new ArrayList<SoftwarePackage>();
		sd.add(gatk);
		w.setSoftwareDependencies(sd);
		
		Map<String,Object> jobParameters = context.getStepContext().getJobParameters();
		for (String key : jobParameters.keySet()) {
			logger.trace("Key: " + key + " Value: " + jobParameters.get(key).toString());
		}
		Job job = jobService.getJobByJobId(jobId);
		Strategy strategy = strategyService.getThisJobsStrategy(StrategyType.LIBRARY_STRATEGY, job);
		String wxsIntervalFile = null;
		if (strategy.getStrategy().equals("WXS"))
			wxsIntervalFile = gatkService.getWxsIntervalFile(job, build);
		String gatkOpts = gatk.getCallVariantOpts(jobParameters);
		String outputGvcfFileName = "gatk.${" + WorkUnit.JOB_NAME + "}.gvcf.vcf";
		String referenceGenomeFile = genomeService.getReferenceGenomeFastaFile(build);
		String snpFile = gatkService.getReferenceSnpsVcfFile(build);
		LinkedHashSet<String> inputBamFilenames = new LinkedHashSet<>();
		for (int i=0; i < fhlist.size(); i++)
			inputBamFilenames.add("${" + WorkUnit.INPUT_FILE + "[" + i + "]}");
		w.setCommand(gatk.getCallVariantsByHaplotypeCaller(inputBamFilenames, outputGvcfFileName, referenceGenomeFile, snpFile, wxsIntervalFile, gatkOpts));
		// w.addCommand(gatk.genotypeGVCFs(<params>)); TODO: implement this
		GridResult result = gridHostResolver.execute(w);
		
		//place the grid result in the step context
		storeStartedResult(context, result);
	}

}
