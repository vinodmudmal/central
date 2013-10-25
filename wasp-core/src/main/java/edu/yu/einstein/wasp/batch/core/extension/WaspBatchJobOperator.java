/*
* Copyright 2006-2013 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package edu.yu.einstein.wasp.batch.core.extension;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.util.Assert;

import edu.yu.einstein.wasp.integration.endpoints.BatchJobHibernationManager;

/**
 * Largely derived from {@link SimpleJobOperator} with modifications
 * @author asmclellan
 *
 */
public class WaspBatchJobOperator extends SimpleJobOperator implements JobOperatorWasp {
	
	private static final String ILLEGAL_STATE_MSG = "Illegal state (only happens on a race condition): %s with name=%s and parameters=%s";

    private Logger logger = LoggerFactory.getLogger(getClass());

	public WaspBatchJobOperator() {
		super();
	}
	
	/**
     * Check mandatory properties.
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
            Assert.notNull(getJobLauncher(), "JobLauncher must be provided");
            Assert.notNull(getJobRegistry(), "JobLocator must be provided");
            Assert.notNull(getJobExplorer(), "JobExplorer must be provided");
            Assert.notNull(getJobRepository(), "JobRepository must be provided");
    }

	@Override
	public Long wake(long executionId) throws JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobParametersInvalidException {
		logger.info("Checking status of job execution with id=" + executionId);
        JobExecution jobExecution = findExecutionById(executionId);

        String jobName = jobExecution.getJobInstance().getJobName();
        WaspBatchJob job = new WaspBatchJob((FlowJob) getJobRegistry().getJob(jobName));
        JobParameters parameters = jobExecution.getJobParameters();

        logger.info(String.format("Attempting to resume job with name=%s and parameters=%s", jobName, parameters));
        try {
                return getJobLauncher().wake(job, parameters).getId();
        }
        catch (JobExecutionAlreadyRunningException e) {
                throw new UnexpectedJobExecutionException(String.format(ILLEGAL_STATE_MSG, "job execution already running", jobName, parameters), e);
        }
	}

	@Override
	public boolean hibernate(long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
		return stop(executionId);
	}
	
	 private JobExecution findExecutionById(long executionId) throws NoSuchJobExecutionException {
         JobExecution jobExecution = getJobExplorer().getJobExecution(executionId);
         
         if (jobExecution == null) {
                 throw new NoSuchJobExecutionException("No JobExecution found for id: [" + executionId + "]");
         }
         return jobExecution;

	 }
	 
	 /**
      * No getter was declared in SimpleJobOperator so we need to use reflection to extract the private value
      * @return
      */
      private JobRepository getJobRepository(){
    	  	Field jobRepositoryField = null;
			try {
				jobRepositoryField = SimpleJobOperator.class.getDeclaredField("jobRepository");
				jobRepositoryField.setAccessible(true);
	        	return (JobRepository) jobRepositoryField.get((SimpleJobOperator) this);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.debug("Unable to obtain JobRepository from super via reflection");
				e.printStackTrace();
			}
			return null;
     }
      
      /**
       * No getter was declared in SimpleJobOperator so we need to use reflection to extract the private value
       * @return
       */
       private WaspBatchJobLauncher getJobLauncher(){
     	  	Field jobLauncherField = null;
 			try {
 				jobLauncherField = SimpleJobOperator.class.getDeclaredField("jobLauncher");
 				jobLauncherField.setAccessible(true);
 	        	return (WaspBatchJobLauncher) jobLauncherField.get((SimpleJobOperator) this);
 			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
 				logger.debug("Unable to obtain JobLauncher from super via reflection");
 				e.printStackTrace();
 			}
 			return null;
      }
       
       /**
        * No getter was declared in SimpleJobOperator so we need to use reflection to extract the private value
        * @return
        */
        private JobRegistry getJobRegistry(){
      	  	Field jobRegistryField = null;
  			try {
  				jobRegistryField = SimpleJobOperator.class.getDeclaredField("jobRegistry");
  				jobRegistryField.setAccessible(true);
  	        	return (JobRegistry) jobRegistryField.get((SimpleJobOperator) this);
  			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
  				logger.debug("Unable to obtain JobRegistry from super via reflection");
  				e.printStackTrace();
  			}
  			return null;
       }
        
        /**
         * No getter was declared in SimpleJobOperator so we need to use reflection to extract the private value
         * @return
         */
         private JobExplorer getJobExplorer(){
       	  	Field jobExplorerField = null;
   			try {
   				jobExplorerField = SimpleJobOperator.class.getDeclaredField("jobExplorer");
   				jobExplorerField.setAccessible(true);
   	        	return (JobExplorer) jobExplorerField.get((SimpleJobOperator) this);
   			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
   				logger.debug("Unable to obtain JobExplorer from super via reflection");
   				e.printStackTrace();
   			}
   			return null;
        }

}
