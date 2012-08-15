package edu.yu.einstein.wasp.controller;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.yu.einstein.wasp.controller.util.MetaHelperWebapp;
import edu.yu.einstein.wasp.dao.JobCellSelectionDao;
import edu.yu.einstein.wasp.dao.JobDao;
import edu.yu.einstein.wasp.dao.JobUserDao;
import edu.yu.einstein.wasp.dao.LabDao;
import edu.yu.einstein.wasp.dao.RoleDao;
import edu.yu.einstein.wasp.dao.StateDao;
import edu.yu.einstein.wasp.dao.TaskDao;
import edu.yu.einstein.wasp.dao.WorkflowresourcecategoryDao;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.JobCellSelection;
import edu.yu.einstein.wasp.model.JobFile;
import edu.yu.einstein.wasp.model.JobMeta;
import edu.yu.einstein.wasp.model.JobResourcecategory;
import edu.yu.einstein.wasp.model.JobSample;
import edu.yu.einstein.wasp.model.JobSoftware;
import edu.yu.einstein.wasp.model.JobUser;
import edu.yu.einstein.wasp.model.Lab;
import edu.yu.einstein.wasp.model.MetaAttribute;
import edu.yu.einstein.wasp.model.MetaBase;
import edu.yu.einstein.wasp.model.ResourceCategory;
import edu.yu.einstein.wasp.model.Role;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleJobCellSelection;
import edu.yu.einstein.wasp.model.Software;
import edu.yu.einstein.wasp.model.State;
import edu.yu.einstein.wasp.model.Statejob;
import edu.yu.einstein.wasp.model.Task;
import edu.yu.einstein.wasp.model.User;
import edu.yu.einstein.wasp.model.Workflowresourcecategory;
import edu.yu.einstein.wasp.model.WorkflowresourcecategoryMeta;
import edu.yu.einstein.wasp.service.AuthenticationService;
import edu.yu.einstein.wasp.service.JobService;
import edu.yu.einstein.wasp.service.SampleService;
import edu.yu.einstein.wasp.taglib.JQFieldTag;
import edu.yu.einstein.wasp.util.MetaHelper;
import edu.yu.einstein.wasp.util.StringHelper;

@Controller
@Transactional
@RequestMapping("/job")
public class JobController extends WaspController {

	private JobDao	jobDao;

	@Autowired
	public void setJobDao(JobDao jobDao) {
		this.jobDao = jobDao;
	}

	public JobDao getJobDao() {
		return this.jobDao;
	}

	private JobUserDao	jobUserDao;

	@Autowired
	public void setJobUserDao(JobUserDao jobUserDao) {
		this.jobUserDao = jobUserDao;
	}

	public JobUserDao getJobUserDao() {
		return this.jobUserDao;
	}

	private RoleDao	roleDao;

	@Autowired
	public void setJobUserDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public RoleDao getRoleUserDao() {
		return this.roleDao;
	}

	@Autowired
	private TaskDao		taskDao;
	@Autowired
	private StateDao	stateDao;
	@Autowired
	private LabDao		labDao;
	@Autowired
	private WorkflowresourcecategoryDao workflowresourcecategoryDao;
	@Autowired
	private JobCellSelectionDao jobCellSelectionDao;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private JobService jobService;
	@Autowired
	private AuthenticationService authenticationService;
	
	// list of baserolenames (da-department admin, lu- labuser ...)
	// see role table
	// higher level roles such as 'lm' or 'js' are used on the view
	public static enum DashboardEntityRolename {
		da, lu, jv, jd, su, ga
	};
	
	private final MetaHelperWebapp getMetaHelperWebapp() {
		return new MetaHelperWebapp(JobMeta.class, request.getSession());
	}

	@RequestMapping("/list")
	public String list(ModelMap m) {
		//List<Job> jobList = this.getJobDao().findAll();

		//m.addAttribute("job", jobList);

		m.addAttribute("_metaList",	getMetaHelperWebapp().getMasterList(MetaBase.class));
		m.addAttribute(JQFieldTag.AREA_ATTR, getMetaHelperWebapp().getArea());
		m.addAttribute("_metaDataMessages", MetaHelper.getMetadataMessages(request.getSession()));

		prepareSelectListData(m);
		
		String userId = request.getParameter("userId");
		String labId = request.getParameter("labId");
		
		return "job/list";
	}

	@RequestMapping(value="/listJSON", method=RequestMethod.GET)
	public String getListJSON(HttpServletResponse response) {
		
		Map <String, Object> jqgrid = new HashMap<String, Object>();
		
		List<Job> tempJobList = new ArrayList<Job>();
		List<Job> jobsFoundInSearch = new ArrayList<Job>();//not currently used
		List<Job> jobList = new ArrayList<Job>();

		String sord = request.getParameter("sord");//always has a value
		String sidx = request.getParameter("sidx");//always has a value
		String search = request.getParameter("_search");
		
		//System.out.println("sidx = " + sidx);System.out.println("sord = " + sord);System.out.println("search = " + search);

		//The jobGrid's toolbar's stringResult = false, so each parameter on the toolbar is sent as a key:value pair
		//If stringResult = true, the parameters containing values would have been sent as a key named filters in JSON format 
		//see http://www.trirand.com/jqgridwiki/doku.php?id=wiki:toolbar_searching
		//below capture parameters on job grid's search toolbar
		String jobIdAsString = request.getParameter("jobId");
		Integer jobId = null;
		if(jobIdAsString != null && !jobIdAsString.isEmpty() && !jobIdAsString.trim().isEmpty()){
			//parse out just the jobId, in case user entered J1001 or # J1001 for job with id of 1001
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<jobIdAsString.trim().length(); i++)
			{
				if(Character.isDigit(jobIdAsString.charAt(i))){
					sb.append(jobIdAsString.charAt(i));
				}
			}
			if(sb.length() > 0){
				int id = Integer.parseInt(sb.toString());
				jobId = new Integer(id);
			}
		}
		
		String jobname = request.getParameter("name");
		if(jobname != null && !jobname.isEmpty() && !jobname.trim().isEmpty()){
			jobname = jobname.trim();
		}
		else{
			jobname = null;
		}
		
		String submitterNameAndLogin = request.getParameter("submitter");
		User submitter = null;
		if(submitterNameAndLogin != null && !submitterNameAndLogin.isEmpty() && !submitterNameAndLogin.trim().isEmpty()){
			String submitterLogin = StringHelper.getLoginFromFormattedNameAndLogin(submitterNameAndLogin.trim());
			if(!submitterLogin.isEmpty() && submitterLogin.trim() != ""){
				submitter = userDao.getUserByLogin(submitterLogin);
			}
		}
		
		String piNameAndLogin = request.getParameter("pi");
		User pi = null;
		Lab piLab = null;
		if(piNameAndLogin != null && !piNameAndLogin.isEmpty() && !piNameAndLogin.trim().isEmpty()){
			String piLogin = StringHelper.getLoginFromFormattedNameAndLogin(piNameAndLogin.trim());
			if(!piLogin.isEmpty() && piLogin.trim() != ""){
				pi = userDao.getUserByLogin(piLogin);
				piLab = labDao.getLabByPrimaryUserId(pi.getUserId().intValue());
			}
		}
		//*** I cannot make the SQL work with date	
		String createDateAsString = request.getParameter("createts");
		Date createts = null;
		if(createDateAsString != null && !createDateAsString.isEmpty() && !createDateAsString.trim().isEmpty()){
			try{
				DateFormat formatter;
				formatter = new SimpleDateFormat("MM/dd/yyyy");
				createts = (Date)formatter.parse(createDateAsString); 
			}catch(Exception e){ }
		}
		//***/
				
		//viewer is a member of the facility
		if(authenticationService.hasRole("su")||authenticationService.hasRole("fm")||authenticationService.hasRole("ft")
				||authenticationService.hasRole("sa")||authenticationService.hasRole("ga")||authenticationService.hasRole("da")){
		
			if("true".equals(search)){
				Map m = new HashMap();
				if(jobId != null){
					m.put("jobId", jobId.intValue());
				}
				if(jobname != null){
					m.put("name", jobname);
				}
				if(submitter != null && submitter.getUserId().intValue() > 0){
					m.put("UserId", submitter.getUserId().intValue());
				}
				if(piLab != null && piLab.getLabId().intValue() > 0){
					m.put("labId", piLab.getLabId().intValue());
				}
				//if(createts != null){couldn't get this to work properly via SQL statement, so it's dealt with below on a job-by-job basis
				//	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				//	m.put("date_format(createts, 'yyyy-MM-dd')", "2012-06-18");
				//}
				List<String> orderByColumnNames = new ArrayList<String>();
				orderByColumnNames.add("jobId");
				
				tempJobList = this.jobDao.findByMapDistinctOrderBy(m, null, orderByColumnNames, "desc");//default order is by jobId/desc
			}
			else{
				tempJobList = this.jobDao.findAllOrderBy("jobId", "desc");//default order is by jobId/desc
			}
		}
		else { //viewer is NOT member of the facility; as of now, no searching capacity for this type of viewer - show all the jobs that person may see (note, if PI, (s)he see's all jobs in that lab)

			for (String role: authenticationService.getRoles()) {			
			
				String[] splitRole = role.split("-");
				if (splitRole.length != 2) { continue; }
				if (splitRole[1].equals("*")) { continue; }
		
				DashboardEntityRolename entityRolename; 
				int roleObjectId = 0;

				try { 
					entityRolename = DashboardEntityRolename.valueOf(splitRole[0]);
					roleObjectId = Integer.parseInt(splitRole[1]);
				} catch (Exception e)	{
					continue;
				}
			
				// adds the role object to the proper bucket
				switch (entityRolename) {
					case jv: tempJobList.add(jobDao.getJobByJobId(roleObjectId));  break;
				}
			}
			
			Collections.sort(tempJobList, new JobIdComparator());
			Collections.reverse(tempJobList);//default order is by jobId/desc
		}
		
		//deal with createts by examining each entry one by one
		if("true".equals(search) && createts != null){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateToSearchFor = formatter.format(createts);
			for (Job job : tempJobList){
				if(formatter.format(job.getCreatets()).equals(dateToSearchFor)){
					jobsFoundInSearch.add(job);
				}
			}
		}
		if(jobsFoundInSearch.size()>0){
			jobList.addAll(jobsFoundInSearch);
		}
		else{
			jobList.addAll(tempJobList);
		}
		
		//deal with sort
		if(sidx != null && !sidx.isEmpty() && sord != null && !sord.isEmpty() ){
			
			if(sidx.equals("jobId") && sord.equals("asc")){//recall that the result set is already sorted by jobId/desc
				Collections.sort(jobList, new JobIdComparator());
			}
			else if(sidx.equals("name")){
				Collections.sort(jobList, new JobNameComparator());	
				if(sord.equals("desc")){
					Collections.reverse(jobList);
				}
			}
			else if(sidx.equals("submitter")){
				Collections.sort(jobList, new SubmitterLastNameFirstNameComparator());
				if(sord.equals("desc")){
					Collections.reverse(jobList);
				}
			}
			else if(sidx.equals("pi")){
				Collections.sort(jobList, new PILastNameFirstNameComparator());	
				if(sord.equals("desc")){
					Collections.reverse(jobList);
				}
			}			
			else if(sidx.equals("createts")){
				Collections.sort(jobList, new JobCreatetsComparator());	
				if(sord.equals("desc")){
					Collections.reverse(jobList);
				}
			}				
		}
		
		try {
			int pageIndex = Integer.parseInt(request.getParameter("page"));		// index of page
			int pageRowNum = Integer.parseInt(request.getParameter("rows"));	// number of rows in one page
			int rowNum = jobList.size();										// total number of rows
			int pageNum = (rowNum + pageRowNum - 1) / pageRowNum;				// total number of pages
			
			jqgrid.put("records", rowNum + "");
			jqgrid.put("total", pageNum + "");
			jqgrid.put("page", pageIndex + "");
			 
			Map<String, String> userData=new HashMap<String, String>();
			userData.put("page", pageIndex + "");
			userData.put("selId",StringUtils.isEmpty(request.getParameter("selId"))?"":request.getParameter("selId"));
			jqgrid.put("userdata",userData);
					
			List<Map> rows = new ArrayList<Map>();
			
			int frId = pageRowNum * (pageIndex - 1);
			int toId = pageRowNum * pageIndex;
			toId = toId <= rowNum ? toId : rowNum;

			/* if the selId is set, change the page index to the one contains the selId */
			if(!StringUtils.isEmpty(request.getParameter("selId")))
			{
				int selId = Integer.parseInt(request.getParameter("selId"));
				int selIndex = jobList.indexOf(jobDao.findById(selId));
				frId = selIndex;
				toId = frId + 1;

				jqgrid.put("records", "1");
				jqgrid.put("total", "1");
				jqgrid.put("page", "1");
			}				

			List<Job> jobPage = jobList.subList(frId, toId);
			for (Job job:jobPage) {
				Map cell = new HashMap();
				cell.put("id", job.getJobId());
				 
				List<JobMeta> jobMeta = getMetaHelperWebapp().syncWithMaster(job.getJobMeta());
				
				User user = userDao.getById(job.getUserId());
				Format formatter = new SimpleDateFormat("MM/dd/yyyy");					
				List<String> cellList=new ArrayList<String>(Arrays.asList(new String[] {
							"J" + job.getJobId().intValue() + " (<a href=/wasp/sampleDnaToLibrary/listJobSamples/"+job.getJobId()+".do>details</a>)",
							job.getName(),
							user.getNameFstLst(),
							//job.getLab().getName() + " (" + pi.getNameLstCmFst() + ")",
							job.getLab().getUser().getNameFstLst(),
							formatter.format(job.getCreatets()),
							"<a href=/wasp/"+job.getWorkflow().getIName()+"/viewfiles/"+job.getJobId()+".do>View files</a>"
				}));
				 
				for (JobMeta meta:jobMeta) {
					cellList.add(meta.getV());
				}
				
				 
				cell.put("cell", cellList);
				 
				rows.add(cell);
			}

			 
			jqgrid.put("rows",rows);
			
			return outputJSON(jqgrid, response); 	
			 
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON " + jobList,e);
		}
	
	}

	@RequestMapping(value = "/subgridJSON.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('su') or hasRole('ft') or hasRole('jv-' + #jobId)")
	public String subgridJSON(@RequestParam("id") Integer jobId,ModelMap m, HttpServletResponse response) {
				
		Map <String, Object> jqgrid = new HashMap<String, Object>();
		
		Job job = this.jobDao.getById(jobId);
		
		//List<JobSample> jobSampleList = job.getJobSample();//don't do it this way; dubin 2-23-12		
	 	//ObjectMapper mapper = new ObjectMapper();//doesn't appear to be used

		//For a list of the macromolecule and library samples initially submitted to a job, pull from table jobcell and exclude duplicates
		//Note that table jobsample is not appropriate, as it will eventually contain records for libraries made by the facility 
		Set<Sample> samplesAsSet = new HashSet<Sample>();//used to store set of unique samples submitted by the user for a specific job
		Map filter = new HashMap();
		filter.put("jobId", job.getJobId());
		List<JobCellSelection> jobCellSelections = jobCellSelectionDao.findByMap(filter);
		for(JobCellSelection jobCellSelection : jobCellSelections){
			List<SampleJobCellSelection> sampleJobCellSelections = jobCellSelection.getSampleJobCellSelection();
			for(SampleJobCellSelection sampleJobCellSelection : sampleJobCellSelections){
				samplesAsSet.add(sampleJobCellSelection.getSample());
			}
		}
		List<Sample> samples = new ArrayList();//this List is needed in order to be able to sort the list (so that it appears the same each time it is displayed on the web; you can't sort a set)
		for(Sample sample : samplesAsSet){
			samples.add(sample);
		}
		class SampleNameComparator implements Comparator<Sample> {
		    @Override
		    public int compare(Sample arg0, Sample arg1) {
		        return arg0.getName().compareToIgnoreCase(arg1.getName());
		    }
		}
		Collections.sort(samples, new SampleNameComparator());//sort by sample's name using class SampleNameComparator immediately above this line (we needed a list, as you can't sort a set)

		try {
			List<Map> rows = new ArrayList<Map>();
			for (Sample sample:samples) {
				Map cell = new HashMap();
				cell.put("id", sample.getSampleId());
					 					
				List<String> cellList = new ArrayList<String>(
						Arrays.asList(
								new String[] {
										"<a href=/wasp/sample/list.do?selId=" + sample.getSampleId().intValue() + ">" + 
											sample.getName() + "</a>",
										sample.getSampleType().getName(),
										sample.getSampleSubtype().getName(), 
										sampleService.convertReceiveSampleStatusForWeb(sampleService.getReceiveSampleStatus(sample))
								}
						)
				);
					 
				cell.put("cell", cellList);
				rows.add(cell);
			}
			 
			jqgrid.put("rows",rows);
			 
			return outputJSON(jqgrid, response); 	
			
		 } catch (Throwable e) {
			 throw new IllegalStateException("Can't marshall to JSON " + samples, e);
		 }
	
	}

	@RequestMapping(value = "/detail/{jobId}", method = RequestMethod.GET)
	public String detail(@PathVariable("jobId") Integer jobId, ModelMap m) {
		String now = (new Date()).toString();

		Job job = this.getJobDao().getById(jobId);

		List<JobMeta> jobMetaList = job.getJobMeta();
		jobMetaList.size();

		List<JobSample> jobSampleList = job.getJobSample();
		jobSampleList.size();

		List<JobFile> jobFileList = job.getJobFile();
		jobFileList.size();

		List<JobUser> jobUserList = job.getJobUser();
		jobUserList.size();

		List<Statejob> stateJobList = job.getStatejob();
		stateJobList.size();

		m.addAttribute("now", now);
		m.addAttribute("job", job);
		m.addAttribute("jobmeta", jobMetaList);
		m.addAttribute("jobsample", jobSampleList);
		m.addAttribute("jobfile", jobFileList);
		m.addAttribute("jobuser", jobUserList);
		m.addAttribute("statejob", stateJobList);

		return "job/detail";
	}

  @RequestMapping(value="/user/roleAdd", method=RequestMethod.POST)
  @PreAuthorize("hasRole('su') or hasRole('lm-' + #labId) or hasRole('js-' + #jobId)")
  public String jobViewerUserRoleAdd (
      @RequestParam("labId") Integer labId,
      @RequestParam("jobId") Integer jobId,
      @RequestParam("login") String login, //10-11-11 changed from useremail to login, AND 10-20-11 changed login format from jgreally to the AJAX-generated and formatted login of John Greally (jgreally), so must now extract the login from the formatted string 
      ModelMap m) {
 
	Job job = this.jobDao.findById(jobId);
	if(job.getJobId() == null || job.getLabId().intValue() != labId.intValue()){
		waspErrorMessage("job.jobViewerUserRoleAdd.error1");//this job not found in database or the labId does not belong to this job
	}
	else{   
		String extractedLogin = StringHelper.getLoginFromFormattedNameAndLogin(login);
		User user = userDao.getUserByLogin(extractedLogin);
		if(user.getUserId() == null){
			waspErrorMessage("job.jobViewerUserRoleAdd.error2");//user login name does not exist
		}
		else{
			//check that login does not belong to the job submitter (or is not already a job-viewer)
			JobUser jobUser = this.jobUserDao.getJobUserByJobIdUserId(jobId, user.getUserId());
			if(jobUser.getJobUserId() != null){
				if( "js".equals( jobUser.getRole().getRoleName() ) ){
					waspErrorMessage("job.jobViewerUserRoleAdd.error3");//user is submitter (and thus is, by default, a job-viewer)
				}
				else if( "jv".equals( jobUser.getRole().getRoleName() ) ){
					waspErrorMessage("job.jobViewerUserRoleAdd.error4");//user is already a job-viewer
				}
			}
			else{
				Role role = roleDao.getRoleByRoleName("jv");
			    JobUser jobUser2 = new JobUser();
			    jobUser2.setJobId(jobId);
			    jobUser2.setUserId(user.getUserId());
			    jobUser2.setRoleId(role.getRoleId());
			    jobUserDao.save(jobUser2);
			}
		}
	}

    return "redirect:/job/detail/" + jobId + ".do";
  }


  @RequestMapping(value="/user/roleRemove/{labId}/{jobId}/{userId}", method=RequestMethod.GET)
  @PreAuthorize("hasRole('su') or hasRole('lm-' + #labId)")
  public String departmentUserRoleRemove (
      @PathVariable("labId") Integer labId,
      @PathVariable("jobId") Integer jobId,
      @PathVariable("userId") Integer userId,
      ModelMap m) {
  
    JobUser jobUser = jobUserDao.getJobUserByJobIdUserId(jobId, userId);

    // todo check job within lab
    // check user is just a job viewer

    jobUserDao.remove(jobUser);

    return "redirect:/job/detail/" + jobId + ".do";
  }

  @RequestMapping(value = "/pending/detail_ro/{deptId}/{labId}/{jobId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('su') or hasRole('sa') or hasRole('ga') or hasRole('da-' + #deptId) or hasRole('lm-' + #labId) or hasRole('pi-' + #labId)")
	public String pendingDetailRO(@PathVariable("deptId") Integer deptId,@PathVariable("labId") Integer labId,
			@PathVariable("jobId") Integer jobId, ModelMap m) {
	  
	  String now = (new Date()).toString();


	    Job job = this.getJobDao().getById(jobId);

	    List<JobMeta> jobMetaList = job.getJobMeta();
	    jobMetaList.size();

	    List<JobSample> jobSampleList = job.getJobSample();
	    jobSampleList.size();

	    List<JobFile> jobFileList = job.getJobFile();
	    jobFileList.size();

	    List<JobUser> jobUserList = job.getJobUser();
	    jobUserList.size();

	    List<Statejob> stateJobList = job.getStatejob();
	    stateJobList.size();

	    m.addAttribute("now", now);
	    m.addAttribute("job", job);
	    m.addAttribute("jobmeta", jobMetaList);
	    m.addAttribute("jobsample", jobSampleList);
	    m.addAttribute("jobfile", jobFileList);
	    m.addAttribute("jobuser", jobUserList);
	    m.addAttribute("statejob", stateJobList);
	   // m.addAttribute("actingasrole", actingAsRole);
	    
	    return "job/pendingjob/detail_ro";
  }
 
  /* 5/15/12 should not longer be user
  @RequestMapping(value = "/allpendinglmapproval/{action}/{labId}/{jobId}.do", method = RequestMethod.GET)
  @PreAuthorize("hasRole('su') or hasRole('sa') or hasRole('ga') or hasRole('lm-' + #labId) or hasRole('pi-' + #labId)")
	public String allPendingLmApproval(@PathVariable("action") String action, @PathVariable("labId") Integer labId, @PathVariable("jobId") Integer jobId, ModelMap m) {
	  
	  pendingJobApproval(action, jobId, "LM");//could use PI instead of LM
	  
	  return "redirect:/lab/allpendinglmapproval/list.do";	
	}
  */
  
  @RequestMapping(value = "/pendinglmapproval/{action}/{labId}/{jobId}.do", method = RequestMethod.GET)
  @PreAuthorize("hasRole('su') or hasRole('sa') or hasRole('ga') or hasRole('fm') or hasRole('ft') or hasRole('lm-' + #labId) or hasRole('pi-' + #labId)")
	public String pendingLmApproval(@PathVariable("action") String action, @PathVariable("labId") Integer labId, @PathVariable("jobId") Integer jobId, ModelMap m) {
	  
	  pendingJobApproval(action, jobId, "LM");//could use PI instead of LM
	  String referer = request.getHeader("Referer");
	  return "redirect:"+ referer;
	  //return "redirect:/lab/pendinglmapproval/list.do";	
	}
  
  @RequestMapping(value = "/pendingdaapproval/{action}/{deptId}/{jobId}.do", method = RequestMethod.GET)
  @PreAuthorize("hasRole('su') or hasRole('sa') or hasRole('fm') or hasRole('ft') or hasRole('ga') or hasRole('da-' + #deptId)")
	public String pendingDaApproval(@PathVariable("action") String action, @PathVariable("deptId") Integer deptId, @PathVariable("jobId") Integer jobId, ModelMap m) {
	  
	  pendingJobApproval(action, jobId, "DA");//private method below
	  String referer = request.getHeader("Referer");
	  return "redirect:"+ referer;
	  //return "redirect:/department/dapendingtasklist.do";		  
	}
 
  private void pendingJobApproval(String action, Integer jobId, String approver){

	  Task taskOfInterest;
	  //confirm action is either approve or reject
	  Job job = jobDao.getJobByJobId(jobId);
	  if(job.getJobId()==null || job.getJobId().intValue() == 0){//confirm id > 0
		  waspErrorMessage("job.approval.error"); 
		  return;
	  }
	  if("DA".equals(approver)){
		  taskOfInterest = taskDao.getTaskByIName("DA Approval");//confirm task id > 0 is below
	  }
	  else if("LM".equals(approver) || "PI".equals(approver)){
		 taskOfInterest = taskDao.getTaskByIName("PI Approval");//confirm task id > 0 is below
	  }
	  else{
		  waspErrorMessage("job.approval.error"); 
		  return;
	  }
	  if(taskOfInterest.getTaskId()==null || taskOfInterest.getTaskId().intValue()==0){//confirm id > 0
		  waspErrorMessage("job.approval.error"); 
		  return;
	  }
	  List<Statejob> statejobList = job.getStatejob();
	  for(Statejob statejob : statejobList){
		  State state = statejob.getState();
		  if(taskOfInterest.getTaskId()==state.getTaskId()){
			  if("approve".equals(action)){
				  state.setStatus("COMPLETED");
				  stateDao.save(state);
				  waspMessage("job.approval.approved"); break;
			  }
			  else if("reject".equals(action)){
				  state.setStatus("ABANDONED");
				  stateDao.save(state); 
				  waspMessage("job.approval.rejected"); break;
			  }			 
		  }
	  }
	  return;	 // flow returns to calling method, either pendingDaApproval or pendingLmApproval
  }



	/**
	 * show job/resource data and meta information to be modified.
	 */
	@RequestMapping(value = "/meta/{jobId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('su') or hasRole('fm')") 
	public String showJobMetaForm(
		@PathVariable("jobId") Integer jobId, 
			ModelMap m) {
		Job job = jobDao.getJobByJobId(jobId);

		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();

		Map<ResourceCategory, List<JobMeta>> resourceMap = new HashMap<ResourceCategory, List<JobMeta>>();
		Map<ResourceCategory, Map<String, List<MetaAttribute.Control.Option>>> resourceOptionsMap = new HashMap<ResourceCategory, Map<String, List<MetaAttribute.Control.Option>>>();



		for (JobResourcecategory jobResourceCategory: job.getJobResourcecategory()) {
		  ResourceCategory resourceCategory = jobResourceCategory.getResourceCategory(); 
			metaHelperWebapp.setArea(resourceCategory.getIName());
			List<JobMeta> jobResourceCategoryMetas = metaHelperWebapp.syncWithMaster(job.getJobMeta());


			resourceMap.put(resourceCategory, jobResourceCategoryMetas); 

			Map<String, List<MetaAttribute.Control.Option>> resourceOptions = new HashMap<String, List<MetaAttribute.Control.Option>>();

			if (resourceCategory != null) {
				Workflowresourcecategory workflowresourcecategory = workflowresourcecategoryDao.getWorkflowresourcecategoryByWorkflowIdResourcecategoryId(job.getWorkflow().getWorkflowId(), resourceCategory.getResourceCategoryId());

				for (WorkflowresourcecategoryMeta wrm: workflowresourcecategory.getWorkflowresourcecategoryMeta()) {
					String key = wrm.getK();

//				if (! key.matches("^.*allowableUiField\\.")) { continue; }
					key = key.replaceAll("^.*allowableUiField\\.", "");
					List<MetaAttribute.Control.Option> options=new ArrayList<MetaAttribute.Control.Option>();
					for(String el: org.springframework.util.StringUtils.tokenizeToStringArray(wrm.getV(),";")) {
						String [] pair=StringUtils.split(el,":");
						MetaAttribute.Control.Option option = new MetaAttribute.Control.Option();
						option.setValue(pair[0]);
						option.setLabel(pair[1]);
						options.add(option);
					}
					resourceOptions.put(key, options);
				}
				resourceOptionsMap.put(resourceCategory, resourceOptions);
			}
		}



		Map<Software, List<JobMeta>> softwareMap = new HashMap<Software, List<JobMeta>>();
		for (JobSoftware jobSoftware: job.getJobSoftware()) {
		  Software software = jobSoftware.getSoftware(); 
			metaHelperWebapp.setArea(software.getIName());
			List<JobMeta> jobSoftwareMetas = metaHelperWebapp.syncWithMaster(job.getJobMeta());

			softwareMap.put(software, jobSoftwareMetas); 
		}

	
		metaHelperWebapp.setArea(job.getWorkflow().getIName());
		List<JobMeta> baseMetas = metaHelperWebapp.syncWithMaster(job.getJobMeta());


		m.put("job", job); 
		m.put("baseMetas", baseMetas); 
		m.put("resourceMap", resourceMap); 
		m.put("resourceOptionsMap", resourceOptionsMap); 
		m.put("softwareMap", softwareMap); 

		m.put("metaHelper", metaHelperWebapp); 

		return "job/metaform_rw";
	}
	
	@RequestMapping(value = "/jobsAwaitingLibraryCreation/jobsAwaitingLibraryCreationList.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('su') or hasRole('sa') or hasRole('fm') or hasRole('ft') or hasRole('ga')")
	public String jobsAwaitingLibraryCreation(ModelMap m) {
		  
		List<Job> jobsWithLibraryCreatedTask = jobService.getJobsWithLibraryCreatedTask();
		List<Job> jobsActive = jobService.getActiveJobs();
		    
		List<Job> jobsActiveAndWithLibraryCreatedTask = new ArrayList<Job>();
		for(Job jobActive : jobsActive){
		    for(Job jobAwaiting : jobsWithLibraryCreatedTask){
		    	if(jobActive.getJobId().intValue()==jobAwaiting.getJobId().intValue()){
		    		jobsActiveAndWithLibraryCreatedTask.add(jobActive);
		    		break;
		    	}
		    }
		}
		jobService.sortJobsByJobId(jobsActiveAndWithLibraryCreatedTask);
		m.put("jobList", jobsActiveAndWithLibraryCreatedTask);
		return "job/jobsAwaitingLibraryCreation/jobsAwaitingLibraryCreationList";	  
	}
}

class JobIdComparator implements Comparator<Job> {
	@Override
	public int compare(Job arg0, Job arg1) {
		return arg0.getJobId().intValue() >= arg1.getJobId().intValue()?1:0;
	}
}
class SubmitterLastNameFirstNameComparator implements Comparator<Job> {
	@Override
	public int compare(Job arg0, Job arg1) {
		return arg0.getUser().getLastName().concat(arg0.getUser().getFirstName()).compareToIgnoreCase(arg1.getUser().getLastName().concat(arg1.getUser().getFirstName()));
	}
}
class PILastNameFirstNameComparator implements Comparator<Job> {
	@Override
	public int compare(Job arg0, Job arg1) {
		return arg0.getLab().getUser().getLastName().concat(arg0.getLab().getUser().getFirstName()).compareToIgnoreCase(arg1.getLab().getUser().getLastName().concat(arg1.getLab().getUser().getFirstName()));
	}
}
class JobNameComparator implements Comparator<Job> {
	@Override
	public int compare(Job arg0, Job arg1) {
		return arg0.getName().compareToIgnoreCase(arg1.getName());
	}
}
class JobCreatetsComparator implements Comparator<Job> {
	@Override
	public int compare(Job arg0, Job arg1) {
		return arg0.getCreatets().compareTo(arg1.getCreatets());
	}
}

class Filters{//not used 

// inner class Rules
public class Rules{
private String field;
private String op;
private String data;

public String getField() {
return field;
}
public void setField(String field) {
this.field = field;
}
public String getOp() {
return op;
}
public void setOp(String op) {
this.op = op;
}
public String getData() {
return data;
}
public void setData(String data) {
this.data = data;
}
}

private String groupOp;

private List<Rules> rules;

public String getGroupOp() {
return groupOp;
}

public void setGroupOp(String groupOp) {
this.groupOp = groupOp;
}

public List<Rules> getRules() {
return rules;
}

public void setRules(List<Rules> rules) {
this.rules = rules;
}

}
