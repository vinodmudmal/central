package edu.yu.einstein.wasp.gatk.batch.tasklet.discovery;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.gatk.software.GATKSoftwareComponent;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.grid.work.WorkUnit.ExecutionMode;
import edu.yu.einstein.wasp.grid.work.WorkUnit.ProcessMode;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.plugin.picard.software.Picard;
import edu.yu.einstein.wasp.plugin.supplemental.organism.Build;
import edu.yu.einstein.wasp.software.SoftwarePackage;

/**
 * 
 * @author asmclellan
 *
 */
public class RealignTasklet extends AbstractGatkTasklet {
	
	public RealignTasklet(String inputFilegroupIds, String outputFilegroupIds) {
		super(inputFilegroupIds, outputFilegroupIds);
	}
	
	@Override
	@Transactional("entityManager")
	public void doExecute(ChunkContext context) throws Exception {
		WorkUnit w = new WorkUnit();
		w.setMode(ExecutionMode.PROCESS);
		w.setProcessMode(ProcessMode.SINGLE);
		w.setMemoryRequirements(GATKSoftwareComponent.MEMORY_REQUIRED_4);
		w.setWorkingDirectory(WorkUnit.SCRATCH_DIR_PLACEHOLDER);
		w.setResultsDirectory(WorkUnit.RESULTS_DIR_PLACEHOLDER + "/" + jobId);
		w.setSecureResults(true);
		Build build = null;
		List<FileHandle> fhlist = new ArrayList<FileHandle>();
		for (Integer fgId : this.getInputFilegroupIds()){
			FileGroup fg = fileService.getFileGroupById(fgId);
			if (fhlist.isEmpty()) // first entry not yet entered
				build = getBuildForFg(fg);
			fhlist.addAll(fg.getFileHandles());
		}
		w.setRequiredFiles(fhlist);
		
		LinkedHashSet<FileGroup> fglist = new LinkedHashSet<FileGroup>();
		for (Integer fgId : this.getOutputFilegroupIds()){
			fglist.add(fileService.getFileGroupById(fgId));
		}
		w.setResultFiles(fglist);
		List<SoftwarePackage> dependencies = new ArrayList<>();
		Picard picard = (Picard) gatk.getSoftwareDependencyByIname("picard");
		dependencies.add(gatk);
		dependencies.add(picard);
		w.setSoftwareDependencies(dependencies);
		LinkedHashSet<String> inputBamFilenames = new LinkedHashSet<>();
		for (int i=0; i < fhlist.size(); i++)
			inputBamFilenames.add("${" + WorkUnit.INPUT_FILE + "[" + i + "]}");
		String intervalFileName = "gatk.${" + WorkUnit.OUTPUT_FILE + "}.realign.intervals";
		String realnBamFilename = "${" + WorkUnit.OUTPUT_FILE + "[0]}";
		String realnBaiFilename = "${" + WorkUnit.OUTPUT_FILE + "[1]}";
		w.addCommand(gatk.getCreateTargetCmd(build, inputBamFilenames, intervalFileName));
		w.addCommand(gatk.getLocalAlignCmd(build, inputBamFilenames, intervalFileName, realnBamFilename, realnBaiFilename));
		GridResult result = gridHostResolver.execute(w);

		// place the grid result in the step context
		storeStartedResult(context, result);
	}

}
