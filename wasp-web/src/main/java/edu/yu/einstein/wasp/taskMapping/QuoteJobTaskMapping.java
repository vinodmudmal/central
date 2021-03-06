package edu.yu.einstein.wasp.taskMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.yu.einstein.wasp.exception.WaspException;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.service.JobService;

/**
 * 
 * @author asmclellan
 *
 */
public class QuoteJobTaskMapping extends WaspTaskMapping {
	
	private JobService jobService;

	@Autowired
	public void setTaskService(JobService jobService) {
		this.jobService = jobService;
	}
	
	public QuoteJobTaskMapping(String localizedLabelKey, String targetLink, String permission) {
		super(localizedLabelKey, targetLink, permission);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequirementToShowLink(Object o) throws WaspException {
		@SuppressWarnings("unchecked")
		List<Job> jobList = (List<Job>) o;
		return jobService.isJobsAwaitingQuote(jobList);
	}

}
