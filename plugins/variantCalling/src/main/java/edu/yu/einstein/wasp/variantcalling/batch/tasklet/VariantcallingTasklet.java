/**
 * Created by Wasp System Eclipse Plugin
 * @author
 */
package edu.yu.einstein.wasp.variantcalling.batch.tasklet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.daemon.batch.tasklets.WaspRemotingTasklet;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.grid.work.WorkUnitGridConfiguration;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.service.JobService;

/**
 * @author 
 * 
 */
public class VariantcallingTasklet extends WaspRemotingTasklet {
	
	@Autowired
	private JobService jobService;
	
	private Job job;
	
	@Autowired
	private GridHostResolver gridHostResolver;
	

	public VariantcallingTasklet() {
		// proxy
	}

	public VariantcallingTasklet(String jobId) {
		this.job = jobService.getJobByJobId(Integer.parseInt(jobId));
	}
	
	/**
	 * Setup work to be run remotely. This method is called during execution of the super.execute(contrib, context) method. 
	 * You must either call the super.execute method from the locally overridden method (as is the default below) or remove the local method 
	 * below to use the method in the parent class.
	 * @param context
	 * @throws Exception
	 */
	@Override
	@Transactional("entityManager")
	public GridResult doExecute(ChunkContext context) throws Exception {
		//configure
		WorkUnitGridConfiguration c = new WorkUnitGridConfiguration();
		c.setResultsDirectory(WorkUnitGridConfiguration.RESULTS_DIR_PLACEHOLDER + "/" + job.getId());
		WorkUnit w = new WorkUnit(c);
		return gridHostResolver.execute(w);
	}
	
	/**
	 * After remote task is finished you may need to execute some further business logic. Such work is specified here.
	 */
	@Override
	@Transactional("entityManager")
	public void doPreFinish(ChunkContext context) throws Exception {
		// do work post completion of remote task. 
		// e.g. get stored result to access output of task (GridResult result = getStartedResult(context);)
	}
	
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public void beforeStep(StepExecution stepExecution){
		super.beforeStep(stepExecution);
		// any pre-step logic goes here
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		ExitStatus exitStatus = super.afterStep(stepExecution);
		exitStatus = exitStatus.and(stepExecution.getExitStatus());
		// any post-step logic goes here
	
		return exitStatus;
	}

	@Override
	public void doCleanupBeforeRestart(StepExecution stepExecution) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
