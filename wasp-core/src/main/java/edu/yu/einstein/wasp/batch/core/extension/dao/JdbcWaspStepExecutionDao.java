package edu.yu.einstein.wasp.batch.core.extension.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JdbcStepExecutionDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import edu.yu.einstein.wasp.batch.exceptions.BatchDaoDataRetrievalException;


@Repository
public class JdbcWaspStepExecutionDao extends JdbcStepExecutionDao implements WaspStepExecutionDao, InitializingBean{
	
	private WaspJobInstanceDao waspJobInstanceDao;
	
	private JobExecutionDao jobExecutionDao;
	
	private static final Logger logger = Logger.getLogger(JdbcWaspStepExecutionDao.class);
	
	public void setWaspJobInstanceDao(WaspJobInstanceDao waspJobInstanceDao){
		Assert.notNull(waspJobInstanceDao, "waspJobInstanceDao cannot be null");
		this.waspJobInstanceDao = waspJobInstanceDao;
	}
	
	public void setJobExecutionDao(JobExecutionDao jobExecutionDao){
		Assert.notNull(jobExecutionDao, "jobExecutionDao cannot be null");
		this.jobExecutionDao = jobExecutionDao;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<StepExecution> getStepExecutions(String name, Map<String, String> parameterMap, Boolean exclusive, BatchStatus batchStatus, ExitStatus exitStatus){
		Assert.notNull(parameterMap, "parameterMap must not be null");
		Assert.notNull(waspJobInstanceDao, "waspJobInstanceDao cannot be  null");
		Assert.notNull(jobExecutionDao, "jobExecutionDao cannot be null");
		if (exclusive == null)
			exclusive = false;
		List<Long> jobInstanceIds = null;
		if (exclusive){
			jobInstanceIds = waspJobInstanceDao.getJobInstanceIdsByExclusivelyMatchingParameters(parameterMap);
		} else {
			jobInstanceIds = waspJobInstanceDao.getJobInstanceIdsByMatchingParameters(parameterMap);
		}
		if (jobInstanceIds == null || jobInstanceIds.isEmpty())
			return null;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		String sql = "select SE.JOB_EXECUTION_ID, STEP_EXECUTION_ID from %PREFIX%STEP_EXECUTION SE, %PREFIX%JOB_EXECUTION JE where STEP_NAME LIKE :name ";
		if (name == null)
			name = "";
		parameterSource.addValue("name", "%"+name);
		if (batchStatus != null){
			sql += "and STATUS = :status ";
			parameterSource.addValue("status", batchStatus);
		}
		if (exitStatus != null){
			sql += "and EXIT_CODE = :exitStatus ";
			parameterSource.addValue("exitStatus", exitStatus.getExitCode());
		}
		
		sql += "and JE.JOB_EXECUTION_ID = SE.JOB_EXECUTION_ID and JE.JOB_INSTANCE_ID in ( ";
		
		int index = 1;
		for (Long id: jobInstanceIds){
			parameterSource.addValue("id"+index, id);
			if (index > 1)
				sql += ",";
			sql += " :id" + index;
			index++;
		}
		sql += " )";
		logger.debug("Built SQL string: " + getQuery(sql));
		for (String key: parameterSource.getValues().keySet())
			logger.debug("Parameter: " + key + "=" + parameterSource.getValues().get(key).toString());
		final List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
		RowMapper<StepExecution> mapper = new RowMapper<StepExecution>() {
			
			@Override
			public StepExecution mapRow(ResultSet rs, int rowNum) throws SQLException {
				JobExecution jobExecution = jobExecutionDao.getJobExecution(rs.getLong(1));
				if (jobExecution == null)
					throw new SQLException("Failed to map result for row number " + rowNum + "(jobExecutionId=" + rs.getLong(1) + ") to a JobExecution: ");
				Long stepExecutionId = rs.getLong(2);
				StepExecution stepExecution = getStepExecution(jobExecution, stepExecutionId);
				if (stepExecution == null)
					throw new BatchDaoDataRetrievalException("Failed to map result for row number " + rowNum + "(jobExecutionId=" + rs.getLong(1) 
							+ ", stepExecutionId=" + rs.getLong(2) + ") to a StepExecution: ");
				stepExecutions.add(stepExecution);
				return stepExecution;
			}
		};
		
		getJdbcTemplate().query(getQuery(sql), mapper, parameterSource);
		return stepExecutions;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JobParameters getJobParametersGivenStepExecution(StepExecution stepExecution){
		return (waspJobInstanceDao.getJobInstance(jobExecutionDao.getJobExecution(stepExecution.getJobExecutionId()))).getJobParameters();
	}
	
	
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}
}
