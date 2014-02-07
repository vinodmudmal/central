


package edu.yu.einstein.wasp.gatk.batch.tasklet;




/**
 * 
 */

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import edu.yu.einstein.wasp.daemon.batch.tasklets.WaspRemotingTasklet;
import edu.yu.einstein.wasp.gatk.software.GATKSoftwareComponent;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.SampleSource;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.SampleService;


/**
 * 
 */

public class RecaliTableTasklet extends WaspRemotingTasklet implements StepExecutionListener {

	private String scratchDirectory;
	private String localAlignJobName;
	private Integer cellLibId;

	private StepExecution stepExecution;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private FileService fileService;

	@Autowired
	private GridHostResolver gridHostResolver;

	@Autowired
	private FileType bamFileType;

	@Autowired
	private GATKSoftwareComponent gatk;
	
	public RecaliTableTasklet() {
		// proxy
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doExecute(ChunkContext context) throws Exception {
		SampleSource cellLib = sampleService.getSampleSourceDao().findById(cellLibId);

		Job job = sampleService.getJobOfLibraryOnCell(cellLib);

		logger.debug("Beginning GATK calculate recalibration table step for cellLibrary " + cellLib.getId() + " from job " + job.getId());
		logger.debug("Starting from previously local re-align'd scratch directory " + scratchDirectory);

		//Set<FileGroup> fileGroups = fileService.getFilesForCellLibraryByType(cellLib, bamFileType);

		//Assert.assertTrue(fileGroups.size() == 1);
		//FileGroup fg = fileGroups.iterator().next();

		//logger.debug("file group: " + fg.getId() + ":" + fg.getDescription());

		//Map<String, Object> jobParameters = context.getStepContext().getJobParameters();
		WorkUnit w = gatk.getRecaliTable(cellLib, scratchDirectory, localAlignJobName);
		w.setResultsDirectory(WorkUnit.RESULTS_DIR_PLACEHOLDER + "/" + job.getId());

		GridResult result = gridHostResolver.execute(w);

		// place the grid result in the step context
		storeStartedResult(context, result);

		// place scratch directory in execution context, to be promoted
		// to the job context at run time.
		ExecutionContext stepContext = this.stepExecution.getExecutionContext();
		stepContext.put("cellLibId", cellLib.getId()); //place in the step context
		stepContext.put("localAlignName", this.localAlignJobName);

		stepContext.put("scrDir", result.getWorkingDirectory());
		stepContext.put("recaliTableName", result.getId());

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
		logger.debug("Lulu StepExecutionListener beforeStep saving StepExecution");
		this.stepExecution = stepExecution;
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobContext = jobExecution.getExecutionContext();
		
		logger.debug("Lulu START StepExecutionListener beforeStep saving StepExecution");

		this.scratchDirectory = jobContext.get("scrDir").toString();
		
		logger.debug("Lulu JOB StepExecutionListener beforeStep saving StepExecution");

		this.localAlignJobName = jobContext.get("localAlignName").toString();
		
		logger.debug("Lulu ID StepExecutionListener beforeStep saving StepExecution");

		this.cellLibId = (Integer) jobContext.get("cellLibId");
		logger.debug("Lulu END StepExecutionListener beforeStep saving StepExecution");

		
		
	}
}

