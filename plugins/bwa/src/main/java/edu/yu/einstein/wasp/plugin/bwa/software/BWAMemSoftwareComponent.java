/**
 * 
 */
package edu.yu.einstein.wasp.plugin.bwa.software;

import java.util.Map;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.explore.wasp.ParameterValueRetrievalException;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.grid.work.WorkUnitGridConfiguration;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.SampleSource;

/**
 * @author asmclellan
 *
 */
public class BWAMemSoftwareComponent extends AbstractBWASoftwareComponent{

	private static final long serialVersionUID = -5244640940549905144L;

	public BWAMemSoftwareComponent() {
		super();
	}
	
	@Transactional("entityManager")
	public WorkUnit getMem(SampleSource cellLibrary, FileGroup fg, Map<String,JobParameter> jobParameters) throws ParameterValueRetrievalException {
		WorkUnit w = buildWorkUnit(fg);
		
		String alnOpts = getOptString("mem", jobParameters);
		
		String checkIndex = "if [ ! -e " + getGenomeIndexPath(getGenomeBuild(cellLibrary)) + ".bwt ]; then\n  exit 101;\nfi";

		w.setCommand(checkIndex);
		
		String command = "bwa mem " + alnOpts  + " -R " + this.getReadGroupString(cellLibrary) + " -t ${" + WorkUnitGridConfiguration.NUMBER_OF_THREADS + "} " + 
				getGenomeIndexPath(getGenomeBuild(cellLibrary)) + " " +
				"${" + WorkUnit.INPUT_FILE + "[" + WorkUnitGridConfiguration.ZERO_TASK_ARRAY_ID + "]} " +
				"> sam.${" + WorkUnitGridConfiguration.TASK_OUTPUT_FILE + "}"; 
		
		logger.debug("Will conduct bwa mem with string: " + command);
		
		w.addCommand(command);
		
		return w;
	}
	
}
