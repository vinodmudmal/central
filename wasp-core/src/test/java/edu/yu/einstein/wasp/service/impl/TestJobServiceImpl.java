package edu.yu.einstein.wasp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.EasyMock;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import edu.yu.einstein.wasp.batch.core.extension.JobExplorerWasp;
import edu.yu.einstein.wasp.dao.JobSampleDao;
import edu.yu.einstein.wasp.dao.SampleDao;
import edu.yu.einstein.wasp.dao.TaskMappingDao;
import edu.yu.einstein.wasp.dao.impl.JobSampleDaoImpl;
import edu.yu.einstein.wasp.dao.impl.SampleDaoImpl;
import edu.yu.einstein.wasp.dao.impl.TaskMappingDaoImpl;
import edu.yu.einstein.wasp.exception.ParameterValueRetrievalException;
import edu.yu.einstein.wasp.integration.messages.WaspJobParameters;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.JobSample;
import edu.yu.einstein.wasp.model.Sample;


public class TestJobServiceImpl {

  TaskMappingDao mockTaskMappingDao;
  JobSampleDao mockJobSampleDao;
  SampleDao mockSampleDao;
  JobExplorerWasp mockJobExplorerWasp;

  JobExplorer mockJobExplorer;
	
  JobServiceImpl jobServiceImpl = new JobServiceImpl();
  TaskServiceImpl taskServiceImpl = new TaskServiceImpl();
 
  //Test when sample.getParent() is NOT null (a facility-generated library)
  @Test
  public void getSubmittedSamples() {
	  
	  List<JobSample> jobSamples = new ArrayList<JobSample>();
	 
	  Job job = new Job();
	  job.setJobId(1);
	  Sample sample = new Sample();
	  sample.setSampleId(1);
	  Sample sampleParent  = new Sample();
	  sample.setParent(sampleParent);
	  sample.setParentId(2);
	    
	  JobSample jobSample = new JobSample();
	  jobSample.setJobSampleId(0001);
	  jobSample.setJobId(1);
	  jobSample.setSampleId(1);
	  jobSample.setSample(sample);
	  jobSamples.add(jobSample);
	  job.setJobSample(jobSamples);
	  
	  //expected
	  List<Sample> submittedSamplesList = new ArrayList<Sample>();
	  jobServiceImpl.setJobSampleDao(mockJobSampleDao);
	  jobServiceImpl.setSampleDao(mockSampleDao);
	  expect(mockJobSampleDao.getJobSampleByJobId(job.getJobId())).andReturn(jobSamples);
	  expect(mockSampleDao.getSampleBySampleId(jobSample.getSampleId())).andReturn(sample);
	    
	  replay(mockJobSampleDao);
	  replay(mockSampleDao);
	  
	  Assert.assertEquals(submittedSamplesList, jobServiceImpl.getSubmittedSamples(job));
	  
	  verify(mockJobSampleDao);
	  verify(mockSampleDao);
	  
  }
  
   //Test when sample.getParent() is null (not a facility-generated library)
   @Test
  public void getSubmittedSamples2() {
		  List<JobSample> jobSamples = new ArrayList<JobSample>();
			 
		  Job job = new Job();
		  job.setJobId(1);
		  Sample sample = new Sample();
		  sample.setSampleId(1);
		    
		  JobSample jobSample = new JobSample();
		  jobSample.setJobSampleId(0001);
		  jobSample.setJobId(1);
		  jobSample.setSampleId(1);
		  jobSample.setSample(sample);
		  jobSamples.add(jobSample);
		  job.setJobSample(jobSamples);
		  
		  //expected
		  List<Sample> submittedSamplesList = new ArrayList<Sample>();
		  submittedSamplesList.add(sample);
		  
		  jobServiceImpl.setJobSampleDao(mockJobSampleDao);
		  jobServiceImpl.setSampleDao(mockSampleDao);
		  
		  expect(mockJobSampleDao.getJobSampleByJobId(job.getJobId())).andReturn(jobSamples);
		  expect(mockSampleDao.getSampleBySampleId(jobSample.getSampleId())).andReturn(sample);
		    
		  replay(mockJobSampleDao);
		  replay(mockSampleDao);
		  
		  Assert.assertEquals(submittedSamplesList, jobServiceImpl.getSubmittedSamples(job));
		  
		  verify(mockJobSampleDao);
		  verify(mockSampleDao);
	  
  }

  //Test when job is null
  @Test
  public void getSubmittedSamples3() {

	  //expected
	  try {
		  jobServiceImpl.getSubmittedSamples(null);
	  }
	  catch (RuntimeException e) {
		  Assert.assertEquals(e.getMessage(), "No Job provided");
	  }
	  
	  
  }
  
  //Test when bad job is passed
  @Test
  public void getSubmittedSamples4() {

	  Job job = new Job();	  
	  //expected
	  try {
		  jobServiceImpl.getSubmittedSamples(job);
	  }
	  catch (RuntimeException e) {
		  Assert.assertEquals(e.getMessage(), "Invalid Job Provided");
	  }
  }
  
  @Test
  public void getSubmittedSamplesNotYetReceived() {
	  //jobServiceImpl.setJobExplorer(mockJobExplorer);
	  jobServiceImpl.setJobExplorer(mockJobExplorerWasp);

	  Job job = new Job();
	  job.setJobId(1);
	  
	  Job job2 = new Job();
	  
	  Sample sample = new Sample();
	  sample.setSampleId(123);
	  
	  //Map<String, String> parameterMap = new HashMap<String, String>();
	  //parameterMap.put(WaspJobParameters.JOB_ID, job.getJobId().toString());
	  
	  Map<String, Set<String>> parameterMap = new HashMap<String, Set<String>>();
	  Set<String> jobIdStringSet = new HashSet<String>();
	  jobIdStringSet.add(job.getJobId().toString());
	  parameterMap.put(WaspJobParameters.JOB_ID, jobIdStringSet);
	  
	  StepExecution stepExecution;
	  JobExecution jobExecution;
	  JobInstance jobInstance;
	  JobParameters jobParameters;
	  //JobParameter jobParameter;	  
	  //jobParameter = new JobParameter("Param1");
	  jobParameters = new JobParameters();
	  
	  jobInstance = new JobInstance(new Long(12345), jobParameters, "Job Name1");
	  jobExecution = new JobExecution(jobInstance, new Long(12345));
	  stepExecution = new StepExecution("Step Name1", jobExecution, new Long(12345));
	  
	  List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
	  stepExecutions.add(stepExecution);
	  
	  //stepExecution.setId(new Long(123));
	  expect(mockJobExplorerWasp.getStepExecutions("wasp.sample.step.listenForSampleReceived", parameterMap, false, BatchStatus.STARTED)).andReturn(stepExecutions);
	  
	  try {
		expect(mockJobExplorerWasp.getJobParameterValueByKey(stepExecution, WaspJobParameters.SAMPLE_ID)).andReturn("123");
	  } catch (ParameterValueRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  
	  expect(mockSampleDao.getSampleBySampleId(123)).andReturn(sample);
	  
	  replay(mockJobExplorerWasp);
	  replay(mockSampleDao);
	  
	  //expected to return
	  List<Sample> submittedSamplesNotYetReceivedList = new ArrayList<Sample>();
	  submittedSamplesNotYetReceivedList.add(sample);
	  
	  //Test case: 1: Job is not null
	  Assert.assertEquals(jobServiceImpl.getSubmittedSamplesNotYetReceived(job), submittedSamplesNotYetReceivedList);
	  
	  //Test case: 2: Job is null
	  try {
		  Assert.assertEquals(jobServiceImpl.getSubmittedSamplesNotYetReceived(null), submittedSamplesNotYetReceivedList);
	  }
	  catch (RuntimeException e) {
		  Assert.assertEquals(e.getMessage(), "No Job provided");
	  }
	  
	  //Test case: 3: Job is invalid (jobId is not set or null)
	  try {
		  Assert.assertEquals(jobServiceImpl.getSubmittedSamplesNotYetReceived(job2), submittedSamplesNotYetReceivedList);
	  }
	  catch (RuntimeException e) {
		  Assert.assertEquals(e.getMessage(), "Invalid Job Provided");
	  }
	  verify(mockJobExplorerWasp);
	  verify(mockSampleDao);

  }
  
  
  @Test //when Sample is null
  public void getSubmittedSamplesNotYetReceived2() {
	  jobServiceImpl.setJobExplorer(mockJobExplorerWasp);

	  Job job = new Job();
	  job.setJobId(1);
	  
	  Job job2 = new Job();
	  
	  Sample sample = new Sample();
	  sample.setSampleId(123);
	  
	  Map<String, Set<String>> parameterMap = new HashMap<String, Set<String>>();
	  Set<String> jobIdStringSet = new HashSet<String>();
	  jobIdStringSet.add(job.getJobId().toString());
	  parameterMap.put(WaspJobParameters.JOB_ID, jobIdStringSet);
	  
	  StepExecution stepExecution;
	  JobExecution jobExecution;
	  JobInstance jobInstance;
	  JobParameters jobParameters;
	  //JobParameter jobParameter;	  
	  //jobParameter = new JobParameter("Param1");
	  jobParameters = new JobParameters();
	  
	  jobInstance = new JobInstance(new Long(12345), jobParameters, "Job Name1");
	  jobExecution = new JobExecution(jobInstance, new Long(12345));
	  stepExecution = new StepExecution("Step Name1", jobExecution, new Long(12345));
	  
	  List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
	  stepExecutions.add(stepExecution);
	  
	  //stepExecution.setId(new Long(123));
	  expect(mockJobExplorerWasp.getStepExecutions("wasp.sample.step.listenForSampleReceived", parameterMap, false, BatchStatus.STARTED)).andReturn(stepExecutions);
	  
	  try {
		expect(mockJobExplorerWasp.getJobParameterValueByKey(stepExecution, WaspJobParameters.SAMPLE_ID)).andReturn("123");
	  } catch (ParameterValueRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  
	  expect(mockSampleDao.getSampleBySampleId(123)).andReturn(null);
	  
	  replay(mockJobExplorerWasp);
	  replay(mockSampleDao);
	  
	  //expected to return
	  List<Sample> submittedSamplesNotYetReceivedList = new ArrayList<Sample>();
	  
	  //Test case: 1: Sample is null
	  Assert.assertEquals(jobServiceImpl.getSubmittedSamplesNotYetReceived(job), submittedSamplesNotYetReceivedList);
  
	  verify(mockJobExplorerWasp);
	  verify(mockSampleDao);

  }
 
  @Test
  public void getSubmittedSamplesNotYetReceived3() {
	  //jobServiceImpl.setJobExplorer(mockJobExplorer);
	  jobServiceImpl.setJobExplorer(mockJobExplorerWasp);

	  Job job = new Job();
	  job.setJobId(1);
	  
	  Job job2 = new Job();
	  
	  Sample sample = new Sample();
	  sample.setSampleId(123);
	  
	  Sample sample2= new Sample();
	  sample2.setSampleId(456);
	  
	  Map<String, Set<String>> parameterMap = new HashMap<String, Set<String>>();
	  Set<String> jobIdStringSet = new HashSet<String>();
	  jobIdStringSet.add(job.getJobId().toString());
	  parameterMap.put(WaspJobParameters.JOB_ID, jobIdStringSet);
	  
	  StepExecution stepExecution;
	  JobExecution jobExecution;
	  JobInstance jobInstance;
	  JobParameters jobParameters;
	  //JobParameter jobParameter;	  
	  //jobParameter = new JobParameter("Param1");
	  jobParameters = new JobParameters();
	  
	  jobInstance = new JobInstance(new Long(12345), jobParameters, "Job Name1");
	  jobExecution = new JobExecution(jobInstance, new Long(12345));
	  stepExecution = new StepExecution("Step Name1", jobExecution, new Long(12345));
	  
	  List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
	  stepExecutions.add(stepExecution);
	  
	  
	  JobParameters jobParameters2 = new JobParameters();
	  JobInstance jobInstance2 = new JobInstance(new Long(12345), jobParameters2, "Job Name1");
	  JobExecution jobExecution2= new JobExecution(jobInstance2, new Long(12345));
	  StepExecution stepExecution2= new StepExecution("Step Name1", jobExecution2, new Long(12345));
	  stepExecutions.add(stepExecution2);
	  
	  expect(mockJobExplorerWasp.getStepExecutions("wasp.sample.step.listenForSampleReceived", parameterMap, false, BatchStatus.STARTED)).andReturn(stepExecutions);
	  
	  try {
		expect(mockJobExplorerWasp.getJobParameterValueByKey(stepExecution, WaspJobParameters.SAMPLE_ID)).andReturn("123");

	  } catch (ParameterValueRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  expect(mockSampleDao.getSampleBySampleId(123)).andReturn(sample);
	  try {
			expect(mockJobExplorerWasp.getJobParameterValueByKey(stepExecution2, WaspJobParameters.SAMPLE_ID)).andReturn("456");

	  } catch (ParameterValueRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  expect(mockSampleDao.getSampleBySampleId(456)).andReturn(sample2);
	  
	  replay(mockJobExplorerWasp);
	  replay(mockSampleDao);
	  
	  //expected to return
	  List<Sample> submittedSamplesNotYetReceivedList = new ArrayList<Sample>();
	  submittedSamplesNotYetReceivedList.add(sample);
	  submittedSamplesNotYetReceivedList.add(sample2);
	  
	  //Test case: 1: 
	  Assert.assertEquals(jobServiceImpl.getSubmittedSamplesNotYetReceived(job), submittedSamplesNotYetReceivedList);
	  
	  verify(mockJobExplorerWasp);
	  verify(mockSampleDao);

  }

  
  /*	REMOVE TEMPORARILY UNTIL FIXED UP FOR NEW WAY OF TASK HANDLING (A S MCLELLAN)
  @Test
  public void getSubmittedSamplesAwaitingSubmission() {
	  
	  List<JobSample> jobSamples = new ArrayList<JobSample>();
	  List<Statesample> stateSamples = new ArrayList<Statesample>();
	  Statesample stateSample = new Statesample();
	  State state = new State();
	  state.setStatus("CREATED");
	  Task task = new Task();
	  task.setIName("abc");
	  state.setTask(task);
	  stateSample.setState(state);
	  stateSamples.add(stateSample);
	  
	  Sample sample = new Sample();
	  //Sample sampleParent  = new Sample();
	  //sample.setParent(sampleParent);
	  sample.setStatesample(stateSamples);
	  
	  JobSample jobSample = new JobSample();
	  jobSample.setSample(sample);
	  jobSamples.add(jobSample);
	  Job job = new Job();
	  job.setJobId(0001);
	  job.setJobSample(jobSamples);
	  	  
	  Task receiveSampleTask = new Task();
	  receiveSampleTask.setIName("abc");
	  
	  jobServiceImpl.setTaskDao(mockTaskDao);
	  expect(mockTaskDao.getTaskByIName("Receive Sample")).andReturn(receiveSampleTask);
	  replay(mockTaskDao);
 
	  //expected
	  List<Sample> submittedSamplesAwaitingSubmissionList = new ArrayList<Sample>();
	  submittedSamplesAwaitingSubmissionList.add(sample);
	  
	  Assert.assertEquals(submittedSamplesAwaitingSubmissionList, jobServiceImpl.getSubmittedSamplesAwaitingSubmission(job));
	  
	  verify(mockTaskDao);
	  
  }
  
  
  @Test
  public void getActiveJobs() {
	  
	  
	  //mockDao returns
	  List<State> states = new ArrayList<State>();
	  List<Statejob> stateJobs = new ArrayList<Statejob>();
	  Statejob stateJob = new Statejob();
	  Job job = new Job();
	  stateJob.setJob(job);
	  stateJobs.add(stateJob);
	  State state = new State();
	  state.setStatejob(stateJobs);
	  states.add(state);
	  
	  taskServiceImpl.setTaskDao(mockTaskDao);
	  jobServiceImpl.setTaskService(taskServiceImpl);
	  expect(mockTaskDao.getStatesByTaskIName("Start Job", "CREATED")).andReturn(states);
	  replay(mockTaskDao);
	  
	  //expected
	  List<Job> activeJobList = new ArrayList<Job>();
	  activeJobList.add(job);
	  Assert.assertEquals(activeJobList, jobServiceImpl.getActiveJobs());
	  verify(mockTaskDao);
	  
  }
  
  //Test when state is set to null
  @Test
  public void getActiveJobs_StateIsNull() {
	  
	  List<State> states = null;
	  
	  taskServiceImpl.setTaskDao(mockTaskDao);
	  jobServiceImpl.setTaskService(taskServiceImpl);
	  expect(mockTaskDao.getStatesByTaskIName("Start Job", "CREATED")).andReturn(states);
	  replay(mockTaskDao);
	  
	  //expected to return empty activeJobList if states==null
	  List<Job> activeJobList = new ArrayList<Job>();
	  Assert.assertEquals(activeJobList, jobServiceImpl.getActiveJobs());
	  verify(mockTaskDao);
	  
  }
  
  @Test
  public void getJobsAwaitingSubmittedSamples() {
	  
	  List<State> states = new ArrayList<State>();
	  List<Statesample> stateSamples = new ArrayList<Statesample>();
	  List<JobSample> jobSamples = new ArrayList<JobSample>();

	  State state = new State();
	  Sample sample = new Sample();
	  Job job = new Job();
	  JobSample jobSample = new JobSample();
	  jobSample.setJob(job);
	  jobSamples.add(jobSample);
	  sample.setJobSample(jobSamples);
	  Statesample stateSample = new Statesample();
	  stateSample.setSample(sample);
	  stateSamples.add(stateSample);
	  state.setStatesample(stateSamples);
	  states.add(state);
	  
	  taskServiceImpl.setTaskDao(mockTaskDao);
	  jobServiceImpl.setTaskService(taskServiceImpl);
	  expect(mockTaskDao.getStatesByTaskIName("Receive Sample", "CREATED")).andReturn(states);
	  replay(mockTaskDao);
	  
	  //
	  List<Job> jobsAwaitingSubmittedSamplesList = new ArrayList<Job>();
	  jobsAwaitingSubmittedSamplesList.add(job);
	  Assert.assertEquals(jobsAwaitingSubmittedSamplesList, jobServiceImpl.getJobsAwaitingSubmittedSamples());
	  verify(mockTaskDao);
	  
  }
  
  @Test
  public void getJobsAwaitingSubmittedSamples_StateIsNull() {
	  List<State> states = null;
	  
	  taskServiceImpl.setTaskDao(mockTaskDao);
	  jobServiceImpl.setTaskService(taskServiceImpl);
	  expect(mockTaskDao.getStatesByTaskIName("Start Job", "CREATED")).andReturn(states);
	  replay(mockTaskDao);
	  
	  //expected to return empty jobsAwaitingSubmittedSamplesList if states==null
	  List<Job> jobsAwaitingSubmittedSamplesList = new ArrayList<Job>();
	  Assert.assertEquals(jobsAwaitingSubmittedSamplesList, jobServiceImpl.getActiveJobs());
	  verify(mockTaskDao);
	  
  }
  
  @Test
  public void getExtraJobDetails() {

	  JobMeta jobMeta = new JobMeta();
	  jobMeta.setK("readLength");
	  jobMeta.setV("");
	  List <JobMeta> jobMetas = new ArrayList<JobMeta>();
	  jobMetas.add(jobMeta);
	  List<JobResourcecategory> jobResourceCategories = new ArrayList <JobResourcecategory>();
	  JobResourcecategory jobResourceCategory = new JobResourcecategory();
	  ResourceCategory resourceCategory = new ResourceCategory();
	  ResourceType resourceType = new ResourceType();
	  resourceType.setIName("mps");
	  resourceType.setResourceTypeId(1);
	  resourceCategory.setResourceType(resourceType);
	  resourceCategory.setName("Illumina HiSeq 2000");
	  jobResourceCategory.setResourceCategory(resourceCategory);
	  jobResourceCategories.add(jobResourceCategory);
	  
	  //Test case:1
	  Task task = new Task();
	  task.setIName("DA Approval");
	  //Test case:2
	  Task task2 = new Task();
	  task2.setIName("PI Approval");
	  //Test case:3
	  Task task3 = new Task();
	  task3.setIName("Quote Job");
	  
	  //Test case: 1a: status="CREATED"
	  Job job = new Job();
	  job.setJobMeta(jobMetas);
	  job.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob = new Statejob();
	  List<Statejob> statejobs = new ArrayList<Statejob>();
	  State state = new State();
	  state.setTask(task);
	  state.setStatus("CREATED");
	  stateJob.setState(state);
	  statejobs.add(stateJob);
	  job.setStatejob(statejobs);
	  
	  //Test case: 1b status="COMPLETED"
	  Job job2 = new Job();
	  job2.setJobMeta(jobMetas);
	  job2.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob2 = new Statejob();
	  List<Statejob> statejobs2 = new ArrayList<Statejob>();
	  State state2 = new State();
	  state2.setTask(task);
	  state2.setStatus("COMPLETED");
	  stateJob2.setState(state2);
	  statejobs2.add(stateJob2);
	  job2.setStatejob(statejobs2);
	  
	  //Test case: 1c: status="ABANDONED"
	  Job job3 = new Job();
	  job3.setJobMeta(jobMetas);
	  job3.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob3 = new Statejob();
	  List<Statejob> statejobs3 = new ArrayList<Statejob>();
	  State state3 = new State();
	  state3.setTask(task);
	  state3.setStatus("ABANDONED");
	  stateJob3.setState(state3);
	  statejobs3.add(stateJob3);
	  job3.setStatejob(statejobs3);
	  
	  //Test case: 1:  If task="DA Approval"
	  //Test case: 1a: status="CREATED"
	  Map<String, String> extraJobDetailsMap = new HashMap<String, String>();
	  extraJobDetailsMap.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap.put("DA Approval", "Awaiting Response");
	  extraJobDetailsMap.put("PI Approval", "Not Yet Set");
	  extraJobDetailsMap.put("Quote Job Price", "Not Yet Set");
	  
	  //Test case: 1b: status="COMPLETED" OR  status="FINALIZED"
	  Map<String, String> extraJobDetailsMap2 = new HashMap<String, String>();
	  extraJobDetailsMap2.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap2.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap2.put("DA Approval", "Approved");
	  extraJobDetailsMap2.put("PI Approval", "Not Yet Set");
	  extraJobDetailsMap2.put("Quote Job Price", "Not Yet Set");
	  
	  //Test case: 1c: status="ABANDONED"
	  Map<String, String> extraJobDetailsMap3 = new HashMap<String, String>();
	  extraJobDetailsMap3.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap3.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap3.put("DA Approval", "Rejected");
	  extraJobDetailsMap3.put("PI Approval", "Not Yet Set");
	  extraJobDetailsMap3.put("Quote Job Price", "Not Yet Set");

	  Assert.assertEquals(extraJobDetailsMap, jobServiceImpl.getExtraJobDetails(job));
	  Assert.assertEquals(extraJobDetailsMap2, jobServiceImpl.getExtraJobDetails(job2));
	  Assert.assertEquals(extraJobDetailsMap3, jobServiceImpl.getExtraJobDetails(job3));

	  
  }
  
  @Test
  public void getExtraJobDetails2() {

	  JobMeta jobMeta = new JobMeta();
	  jobMeta.setK("readLength");
	  jobMeta.setV("");
	  List <JobMeta> jobMetas = new ArrayList<JobMeta>();
	  jobMetas.add(jobMeta);
	  List<JobResourcecategory> jobResourceCategories = new ArrayList <JobResourcecategory>();
	  JobResourcecategory jobResourceCategory = new JobResourcecategory();
	  ResourceCategory resourceCategory = new ResourceCategory();
	  ResourceType resourceType = new ResourceType();
	  resourceType.setIName("mps");
	  resourceType.setResourceTypeId(1);
	  resourceCategory.setResourceType(resourceType);
	  resourceCategory.setName("Illumina HiSeq 2000");
	  jobResourceCategory.setResourceCategory(resourceCategory);
	  jobResourceCategories.add(jobResourceCategory);
	  
	  //Test case:2
	  Task task = new Task();
	  task.setIName("PI Approval");
	  	  
	  //Test case: 1a: status="CREATED"
	  Job job = new Job();
	  job.setJobMeta(jobMetas);
	  job.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob = new Statejob();
	  List<Statejob> statejobs = new ArrayList<Statejob>();
	  State state = new State();
	  state.setTask(task);
	  state.setStatus("CREATED");
	  stateJob.setState(state);
	  statejobs.add(stateJob);
	  job.setStatejob(statejobs);
	  
	  //Test case: 1b status="COMPLETED"
	  Job job2 = new Job();
	  job2.setJobMeta(jobMetas);
	  job2.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob2 = new Statejob();
	  List<Statejob> statejobs2 = new ArrayList<Statejob>();
	  State state2 = new State();
	  state2.setTask(task);
	  state2.setStatus("COMPLETED");
	  stateJob2.setState(state2);
	  statejobs2.add(stateJob2);
	  job2.setStatejob(statejobs2);
	  
	  //Test case: 1c: status="ABANDONED"
	  Job job3 = new Job();
	  job3.setJobMeta(jobMetas);
	  job3.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob3 = new Statejob();
	  List<Statejob> statejobs3 = new ArrayList<Statejob>();
	  State state3 = new State();
	  state3.setTask(task);
	  state3.setStatus("ABANDONED");
	  stateJob3.setState(state3);
	  statejobs3.add(stateJob3);
	  job3.setStatejob(statejobs3);
	  
	  //Test case: 2:  If task="PI Approval"
	  //Test case: 2a: status="CREATED"
	  Map<String, String> extraJobDetailsMap = new HashMap<String, String>();
	  extraJobDetailsMap.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap.put("PI Approval", "Awaiting Response");
	  extraJobDetailsMap.put("DA Approval", "Not Yet Set");
	  extraJobDetailsMap.put("Quote Job Price", "Not Yet Set");
	  
	  //Test case: 2b: status="COMPLETED" OR  status="FINALIZED"
	  Map<String, String> extraJobDetailsMap2 = new HashMap<String, String>();
	  extraJobDetailsMap2.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap2.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap2.put("PI Approval", "Approved");
	  extraJobDetailsMap2.put("DA Approval", "Not Yet Set");
	  extraJobDetailsMap2.put("Quote Job Price", "Not Yet Set");
	  
	  //Test case: 2c: status="ABANDONED"
	  Map<String, String> extraJobDetailsMap3 = new HashMap<String, String>();
	  extraJobDetailsMap3.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap3.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap3.put("PI Approval", "Rejected");
	  extraJobDetailsMap3.put("DA Approval", "Not Yet Set");
	  extraJobDetailsMap3.put("Quote Job Price", "Not Yet Set");

	  Assert.assertEquals(extraJobDetailsMap, jobServiceImpl.getExtraJobDetails(job));
	  Assert.assertEquals(extraJobDetailsMap2, jobServiceImpl.getExtraJobDetails(job2));
	  Assert.assertEquals(extraJobDetailsMap3, jobServiceImpl.getExtraJobDetails(job3));

	  
  }

  @Test
  public void getExtraJobDetails3() {

	  JobMeta jobMeta = new JobMeta();
	  jobMeta.setK("readLength");
	  jobMeta.setV("");
	  List <JobMeta> jobMetas = new ArrayList<JobMeta>();
	  jobMetas.add(jobMeta);
	  List<JobResourcecategory> jobResourceCategories = new ArrayList <JobResourcecategory>();
	  JobResourcecategory jobResourceCategory = new JobResourcecategory();
	  ResourceCategory resourceCategory = new ResourceCategory();
	  ResourceType resourceType = new ResourceType();
	  resourceType.setIName("mps");
	  resourceType.setResourceTypeId(1);
	  resourceCategory.setResourceType(resourceType);
	  resourceCategory.setName("Illumina HiSeq 2000");
	  jobResourceCategory.setResourceCategory(resourceCategory);
	  jobResourceCategories.add(jobResourceCategory);
	  
	  //Test case:3
	  Task task = new Task();
	  task.setIName("Quote Job");
	  	  
	  //Test case: 3a: status="CREATED"
	  Job job = new Job();
	  job.setJobMeta(jobMetas);
	  job.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob = new Statejob();
	  List<Statejob> statejobs = new ArrayList<Statejob>();
	  State state = new State();
	  state.setTask(task);
	  state.setStatus("CREATED");
	  stateJob.setState(state);
	  statejobs.add(stateJob);
	  job.setStatejob(statejobs);
	  
	  //Test case: 3b status="COMPLETED"
	  Job job2 = new Job();
	  job2.setJobMeta(jobMetas);
	  job2.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob2 = new Statejob();
	  List<Statejob> statejobs2 = new ArrayList<Statejob>();
	  State state2 = new State();
	  state2.setTask(task);
	  state2.setStatus("COMPLETED");
	  stateJob2.setState(state2);
	  statejobs2.add(stateJob2);
	  job2.setStatejob(statejobs2);
	  
	  //Test case: 3c: status="ABANDONED"
	  Job job3 = new Job();
	  job3.setJobMeta(jobMetas);
	  job3.setJobResourcecategory(jobResourceCategories);
	  Statejob stateJob3 = new Statejob();
	  List<Statejob> statejobs3 = new ArrayList<Statejob>();
	  State state3 = new State();
	  state3.setTask(task);
	  state3.setStatus("ABANDONED");
	  stateJob3.setState(state3);
	  statejobs3.add(stateJob3);
	  job3.setStatejob(statejobs3);
	  
	  //Test case: 3:  If task="Quote Job"
	  //Test case: 3a: status="CREATED"
	  Map<String, String> extraJobDetailsMap = new HashMap<String, String>();
	  extraJobDetailsMap.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap.put("Quote Job Price", "Awaiting Quote");
	  extraJobDetailsMap.put("DA Approval", "Not Yet Set");
	  extraJobDetailsMap.put("PI Approval", "Not Yet Set");
	  
	  //Test case: 3b: status="COMPLETED" OR  status="FINALIZED"
	  AcctJobquotecurrent acctJobQuoteCurrent = new AcctJobquotecurrent();
	  AcctQuote acctQuote = new AcctQuote();
	  //TO DO: need to add more test cases to test different amounts
	  Float price = new Float(123.45);
	  acctQuote.setAmount(new Float(123.45));
	  acctJobQuoteCurrent.setAcctQuote(acctQuote);
	  List <AcctJobquotecurrent> acctJobQuoteCurrentList = new ArrayList <AcctJobquotecurrent>();
	  acctJobQuoteCurrentList.add(0, acctJobQuoteCurrent);
	  job2.setAcctJobquotecurrent(acctJobQuoteCurrentList);
	  //Float price = new Float(job.getAcctJobquotecurrent().get(0).getAcctQuote().getAmount());

	  Map<String, String> extraJobDetailsMap2 = new HashMap<String, String>();
	  extraJobDetailsMap2.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap2.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap2.put("Quote Job Price", "$"+String.format("%.2f", price));
	  extraJobDetailsMap2.put("DA Approval", "Not Yet Set");
	  extraJobDetailsMap2.put("PI Approval", "Not Yet Set");
	  
	  //Test case: 3c: other status
	  Map<String, String> extraJobDetailsMap3 = new HashMap<String, String>();
	  extraJobDetailsMap3.put("Machine", jobResourceCategory.getResourceCategory().getName());
	  extraJobDetailsMap3.put("Read Length", jobMeta.getV());
	  extraJobDetailsMap3.put("Quote Job Price", "Unknown");
	  extraJobDetailsMap3.put("DA Approval", "Not Yet Set");
	  extraJobDetailsMap3.put("PI Approval", "Not Yet Set");

	  Assert.assertEquals(extraJobDetailsMap, jobServiceImpl.getExtraJobDetails(job));
	  Assert.assertEquals(extraJobDetailsMap2, jobServiceImpl.getExtraJobDetails(job2));
	  Assert.assertEquals(extraJobDetailsMap3, jobServiceImpl.getExtraJobDetails(job3));

	  
  }

  */
  @BeforeMethod
  public void beforeMethod() {
  }

  @AfterMethod
  public void afterMethod() {
	  EasyMock.reset(mockTaskMappingDao);
	  EasyMock.reset(mockJobSampleDao);
	  EasyMock.reset(mockSampleDao);
	  EasyMock.reset(mockJobExplorerWasp);

  }

  @BeforeClass
  public void beforeClass() {
  }

  @AfterClass
  public void afterClass() {
  }

  @BeforeTest
  public void beforeTest() {
	  mockTaskMappingDao = createMockBuilder(TaskMappingDaoImpl.class).addMockedMethods(TaskMappingDaoImpl.class.getMethods()).createMock();
	  mockJobSampleDao = createMockBuilder(JobSampleDaoImpl.class).addMockedMethods(JobSampleDaoImpl.class.getMethods()).createMock();
	  mockSampleDao = createMockBuilder(SampleDaoImpl.class).addMockedMethods(SampleDaoImpl.class.getMethods()).createMock();	  
	  mockJobExplorerWasp = EasyMock.createNiceMock(JobExplorerWasp.class);
	  
	  Assert.assertNotNull(mockTaskMappingDao);
	  Assert.assertNotNull(mockJobSampleDao);
	  Assert.assertNotNull(mockSampleDao);
	  Assert.assertNotNull(mockJobExplorerWasp);



  }

  @AfterTest
  public void afterTest() {
  }

}
