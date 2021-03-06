package edu.yu.einstein.wasp.daemon.batch.tasklets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import edu.yu.einstein.wasp.batch.SimpleManyJobRecipient;
import edu.yu.einstein.wasp.exception.WaspBatchJobExecutionReadinessException;
import edu.yu.einstein.wasp.exception.WaspRuntimeException;
import edu.yu.einstein.wasp.integration.endpoints.BatchJobHibernationManager;
import edu.yu.einstein.wasp.integration.endpoints.BatchJobHibernationManager.LockType;
import edu.yu.einstein.wasp.interfacing.batch.ManyJobRecipient;

/**
 * Listens on the provided subscribable channel for a message with a task and
 * status specified in the provided message template. Will try and hibernate,
 * but will keep responding to messages whilst waiting.
 * 
 * @author asmclellan
 */
/**
 * @author calder
 * 
 */
@Transactional
public class ListenForManyStatusMessagesTasklet extends WaspHibernatingTasklet implements ManyJobRecipient {

    private static final Logger logger = LoggerFactory.getLogger(ListenForManyStatusMessagesTasklet.class);

    private List<Message<?>> messageQueue = new ArrayList<>();

    protected StepExecution stepExecution;
    protected JobExecution jobExecution;

    protected UUID parentId;
    protected List<String> children;

    @Autowired
    @Qualifier("wasp.channel.reply")
    private SubscribableChannel replyChannel;

    @Autowired
    @Qualifier("wasp.channel.notification.batch")
    private SubscribableChannel subscribeChannel;

    @Autowired
    private BatchJobHibernationManager batchJobHibernationManager;

    public ListenForManyStatusMessagesTasklet() {
        // proxy
    }

    @Override
    protected void doHibernate(StepExecution stepExecution) throws WaspBatchJobExecutionReadinessException {
        super.doHibernate(stepExecution);
    }

    @Override
    public RepeatStatus execute(StepContribution contrib, ChunkContext context) throws Exception {
    	stepExecution = context.getStepContext().getStepExecution();
    	jobExecution = stepExecution.getJobExecution();
        Long stepExecutionId = stepExecution.getId();
        Long jobExecutionId = jobExecution.getId();
        logger.trace(name + "execute() invoked on stepId=" + stepExecutionId);
        
        // if we are woken by a message, it means that the BatchJobHibernationManager has concluded
        // that all of the messages have been received.  
        if (wasWokenOnMessage(stepExecution)) {
            logger.info("StepExecution (id=" + stepExecutionId + ", JobExecution id=" + jobExecutionId
                    + ") was woken up from hibernation on completion.");
            removeWokenOnMessageStatus(stepExecution);
            if (!stepExecution.getExecutionContext().containsKey(BatchJobHibernationManager.ABANDONED_CHILD_IDS)) {
                logger.debug(stepExecution.getStepName() + ":" + stepExecutionId + "appears to be complete, returning FINISHED");
            
                // does not currently deal with abandonment issues.
                setStepStatusInJobExecutionContext(stepExecution, BatchStatus.COMPLETED);
                BatchJobHibernationManager.unlockJobExecution(stepExecution.getJobExecution(), LockType.WAKE);
                return RepeatStatus.FINISHED;
            } else {
                List<String> abandoned = Arrays.asList(StringUtils.delimitedListToStringArray(
                        stepExecution.getExecutionContext().get(BatchJobHibernationManager.ABANDONED_CHILD_IDS).toString(), 
                        BatchJobHibernationManager.PARENT_JOB_CHILD_LIST_DELIMITER));
                
                String message = "Found " + abandoned.size() + " abandoned child jobs for parent step " + parentId.toString() + 
                        " {" + StringUtils.collectionToCommaDelimitedString(abandoned) + "}";
                logger.warn(message);
                throw new WaspRuntimeException(message);
            }
        }
        if (children.isEmpty()){
        	logger.info("StepExecution (id=" + stepExecutionId + ", JobExecution id=" + jobExecutionId
                    + ") will end immediately as no child jobs to listen for");
        	return RepeatStatus.FINISHED;
        }
        
        // handle hibernation.  If the tasklet was not woken, then it should simply
        // put itself to sleep and allow the BatchJobHibernationManager handle its
        // messages.
        if (isHibernationRequestedForJob(context.getStepContext().getStepExecution().getJobExecution())) {
            logger.trace("This JobExecution (id=" + jobExecutionId + ") is already undergoing hibernation. Awaiting hibernation...");
        } else if (!wasHibernationRequested) {
            logger.info("Going to request hibernation from StepExecution (id=" + stepExecutionId + ", JobExecution id=" + jobExecutionId
                    + ") as not previously requested");
            requestHibernation(context);
        } else if (!wasHibernationRequestGranted) {
            logger.debug("Previous hibernation request made by this StepExecution (id=" + stepExecutionId + ", JobExecution id=" + jobExecutionId
                    + ") but we were still waiting for all steps to be ready. Going to retry request.");
            requestHibernation(context);
            logger.debug("Hibernate request made by this StepExecution (id=" + stepExecutionId + ", JobExecution id=" + jobExecutionId
                    + ") but JobExecution is not yet ready to hibernate");
        } else {
            logger.debug("Hibernate request was granted to this StepExecution (id=" + stepExecutionId + ", JobExecution id=" + jobExecutionId
                    + "). Awaiting hibernation...");
        }

        logger.trace(stepExecution.getStepName() + ":" + stepExecutionId + " returning CONTINUABLE");
        return RepeatStatus.CONTINUABLE;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.jobExecution = stepExecution.getJobExecution();

        // obtain the list of messages to wait for and place it into our list
        // for comparison by the BatchJobHibernationManager
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        boolean many = jobContext.containsKey(BatchJobHibernationManager.PARENT_JOB_ID_KEY);

        if (!many) {
            String message = "Expected to find parent launch tasklet id to listen for child messages, but it does not exist.  "
                    + "This tasklet needs to follow a LaunchManyJobsTasklet.";
            logger.error(message);
            throw new MessagingException(message);
        }

        parentId = UUID.fromString(jobContext.getString(BatchJobHibernationManager.PARENT_JOB_ID_KEY));
        String[] child = StringUtils.delimitedListToStringArray(jobContext.getString(BatchJobHibernationManager.PARENT_JOB_CHILD_LIST_KEY),
                BatchJobHibernationManager.PARENT_JOB_CHILD_LIST_DELIMITER);

        children = Arrays.asList(child);
        if (!children.isEmpty())
        	registerManyStepCompletionListener(stepExecution);

        super.beforeStep(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
    	ExitStatus exitStatus = super.afterStep(stepExecution);
    	exitStatus = exitStatus.and(getExitStatus(stepExecution));
        if (stepExecution.getExitStatus().isHibernating()) {
            // do stuff immediately before hibernating
            logger.trace(stepExecution.getStepName() + " afterStep going into hibernation");
            return ExitStatus.HIBERNATING;
        } else if (!stepExecution.getExitStatus().isRunning()) { // ExitStatus not "STARTED", "HIBERNATING" or "UNKNOWN"
            exitStatus = exitStatus.and(getExitStatus(stepExecution));
            // set exit status to equal the most severe outcome of all received messages
            this.messageQueue.clear(); // clean up in case of restart
            this.abandonMessageQueue.clear(); // clean up in case of restart
            logger.debug(stepExecution.getStepName() + " going to exit step with ExitStatus=" + exitStatus);
            if (!children.isEmpty())
            	batchJobHibernationManager.unregisterManyStepCompletionListener(this);
            jobExecution.getExecutionContext().remove(BatchJobHibernationManager.PARENT_JOB_ID_KEY);
        }
        logger.debug(stepExecution.getStepName() + " going to exit step with ExitStatus=" + exitStatus);
        return exitStatus;
    }
    
    private void registerManyStepCompletionListener(StepExecution stepExecution){
    	SimpleManyJobRecipient jobRecipient = new SimpleManyJobRecipient(this); // otherwise trouble when making a json object later
    	// persist jobRecipient object in stepExecutionContext in Json format. This is required if re-registering the jobRecipient after
        // a batch restart.
        ObjectMapper jsonObjectmapper = new ObjectMapper();
		try {
			String jsonText = jsonObjectmapper.writeValueAsString(jobRecipient);
			stepExecution.getExecutionContext().putString(BatchJobHibernationManager.MANY_JOB_RECIPIENT_KEY, jsonText);
			logger.debug("Updated StepExecutionContext with json for jobRecipient where StepExecution id=" + stepExecution.getId() +  ": " + jsonText);
		} catch (IOException e) {
			logger.warn("Unable to convert jobRecipient to json for persistance in StepExecution Context: " + e.getLocalizedMessage());
		}
    	batchJobHibernationManager.registerManyStepCompletionListener(jobRecipient);
    }

    private ExitStatus getExitStatus(StepExecution stepExecution) {
        logger.debug("exec: " + stepExecution.getExecutionContext());
        if (stepExecution.getExecutionContext().containsKey(BatchJobHibernationManager.ABANDONED_CHILD_IDS)){
	        List<String> abandoned = Arrays.asList(StringUtils.delimitedListToStringArray(
	                stepExecution.getExecutionContext().getString(BatchJobHibernationManager.ABANDONED_CHILD_IDS),
	                BatchJobHibernationManager.PARENT_JOB_CHILD_LIST_DELIMITER));
	        if (abandoned.size() > 0){
	        	logger.debug("returning ExitStatus.FAILED as received " + abandoned.size() + " abandoned child notifications");
	            return ExitStatus.FAILED;
	        }
        }
        return stepExecution.getExitStatus();
    }

    // @Override
    public StepExecution getStepExecution() {
        return stepExecution;
    }

    // @Override
    public UUID getParentUuid() {
        return parentId;
    }

    // @Override
    public List<String> getChildren() {
        return children;
    }
    
    @Override
    public UUID getParentID() {
        return parentId;
    }

    @Override
    public List<String> getChildIDs() {
        return children;
    }

    @Override
    public Long getJobExecutionId() {
        return jobExecution.getId();
    }

    @Override
    public String getStepName() {
        return this.getStepExecution().getStepName();
    }

}
