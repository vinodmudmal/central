package edu.yu.einstein.wasp.controller;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import edu.yu.einstein.wasp.controller.util.MetaHelperWebapp;
import edu.yu.einstein.wasp.controller.util.SampleAndSampleDraftMetaHelper;
import edu.yu.einstein.wasp.dao.AdaptorDao;
import edu.yu.einstein.wasp.dao.AdaptorsetDao;
import edu.yu.einstein.wasp.dao.AdaptorsetResourceCategoryDao;
import edu.yu.einstein.wasp.dao.FileDao;
import edu.yu.einstein.wasp.dao.JobCellSelectionDao;
import edu.yu.einstein.wasp.dao.JobDao;
import edu.yu.einstein.wasp.dao.JobDraftCellSelectionDao;
import edu.yu.einstein.wasp.dao.JobDraftDao;
import edu.yu.einstein.wasp.dao.JobDraftFileDao;
import edu.yu.einstein.wasp.dao.JobDraftMetaDao;
import edu.yu.einstein.wasp.dao.JobDraftSoftwareDao;
import edu.yu.einstein.wasp.dao.JobDraftresourcecategoryDao;
import edu.yu.einstein.wasp.dao.JobMetaDao;
import edu.yu.einstein.wasp.dao.JobResourcecategoryDao;
import edu.yu.einstein.wasp.dao.JobSampleDao;
import edu.yu.einstein.wasp.dao.JobSoftwareDao;
import edu.yu.einstein.wasp.dao.JobUserDao;
import edu.yu.einstein.wasp.dao.LabDao;
import edu.yu.einstein.wasp.dao.ResourceCategoryDao;
import edu.yu.einstein.wasp.dao.ResourceDao;
import edu.yu.einstein.wasp.dao.RoleDao;
import edu.yu.einstein.wasp.dao.SampleJobCellSelectionDao;
import edu.yu.einstein.wasp.dao.SampleDao;
import edu.yu.einstein.wasp.dao.SampleDraftJobDraftCellSelectionDao;
import edu.yu.einstein.wasp.dao.SampleDraftDao;
import edu.yu.einstein.wasp.dao.SampleDraftMetaDao;
import edu.yu.einstein.wasp.dao.SampleFileDao;
import edu.yu.einstein.wasp.dao.SampleMetaDao;
import edu.yu.einstein.wasp.dao.SoftwareDao;
import edu.yu.einstein.wasp.dao.StateDao;
import edu.yu.einstein.wasp.dao.StatejobDao;
import edu.yu.einstein.wasp.dao.SampleSubtypeDao;
import edu.yu.einstein.wasp.dao.TaskDao;
import edu.yu.einstein.wasp.dao.ResourceTypeDao;
import edu.yu.einstein.wasp.dao.SampleTypeDao;
import edu.yu.einstein.wasp.dao.WorkflowDao;
import edu.yu.einstein.wasp.dao.WorkflowResourceTypeDao;
import edu.yu.einstein.wasp.dao.WorkflowSoftwareDao;
import edu.yu.einstein.wasp.dao.WorkflowresourcecategoryDao;
import edu.yu.einstein.wasp.dao.impl.DBResourceBundle;
import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.exception.MetadataTypeException;
import edu.yu.einstein.wasp.exception.ModelCopyException;
import edu.yu.einstein.wasp.exception.ModelDetachException;
import edu.yu.einstein.wasp.exception.NullResourceTypeException;
import edu.yu.einstein.wasp.model.Adaptor;
import edu.yu.einstein.wasp.model.Adaptorset;
import edu.yu.einstein.wasp.model.AdaptorsetResourceCategory;
import edu.yu.einstein.wasp.model.File;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.JobCellSelection;
import edu.yu.einstein.wasp.model.JobDraft;
import edu.yu.einstein.wasp.model.JobDraftCellSelection;
import edu.yu.einstein.wasp.model.JobDraftFile;
import edu.yu.einstein.wasp.model.JobDraftMeta;
import edu.yu.einstein.wasp.model.JobDraftSoftware;
import edu.yu.einstein.wasp.model.JobDraftresourcecategory;
import edu.yu.einstein.wasp.model.JobFile;
import edu.yu.einstein.wasp.model.JobMeta;
import edu.yu.einstein.wasp.model.JobResourcecategory;
import edu.yu.einstein.wasp.model.JobSample;
import edu.yu.einstein.wasp.model.JobSoftware;
import edu.yu.einstein.wasp.model.JobUser;
import edu.yu.einstein.wasp.model.Lab;
import edu.yu.einstein.wasp.model.LabUser;
import edu.yu.einstein.wasp.model.MetaAttribute;
import edu.yu.einstein.wasp.model.MetaBase;
import edu.yu.einstein.wasp.model.ResourceCategory;
import edu.yu.einstein.wasp.model.ResourceType;
import edu.yu.einstein.wasp.model.Role;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleJobCellSelection;
import edu.yu.einstein.wasp.model.SampleDraft;
import edu.yu.einstein.wasp.model.SampleDraftJobDraftCellSelection;
import edu.yu.einstein.wasp.model.SampleDraftMeta;
import edu.yu.einstein.wasp.model.SampleFile;
import edu.yu.einstein.wasp.model.SampleMeta;
import edu.yu.einstein.wasp.model.SampleSubtypeMeta;
import edu.yu.einstein.wasp.model.State;
import edu.yu.einstein.wasp.model.Statejob;
import edu.yu.einstein.wasp.model.SampleSubtype;
import edu.yu.einstein.wasp.model.Task;
import edu.yu.einstein.wasp.model.User;
import edu.yu.einstein.wasp.model.Workflow;
import edu.yu.einstein.wasp.model.WorkflowMeta;
import edu.yu.einstein.wasp.model.WorkflowResourceType;
import edu.yu.einstein.wasp.model.WorkflowSoftware;
import edu.yu.einstein.wasp.model.Workflowresourcecategory;
import edu.yu.einstein.wasp.model.WorkflowresourcecategoryMeta;
import edu.yu.einstein.wasp.model.WorkflowsoftwareMeta;
import edu.yu.einstein.wasp.service.AuthenticationService;
import edu.yu.einstein.wasp.service.MessageService;
import edu.yu.einstein.wasp.service.SampleService;
import edu.yu.einstein.wasp.taglib.JQFieldTag;
import edu.yu.einstein.wasp.util.MetaHelper;

@Controller
@Transactional
@RequestMapping("/jobsubmit")
public class JobSubmissionController extends WaspController {

	@Autowired
	protected JobDraftDao jobDraftDao;

	@Autowired
	protected JobDraftMetaDao jobDraftMetaDao;

	@Autowired
	protected JobDraftCellSelectionDao jobDraftCellSelectionDao;

	@Autowired
	protected JobDraftresourcecategoryDao jobDraftresourcecategoryDao;

	@Autowired
	protected JobDraftSoftwareDao jobDraftSoftwareDao;
	
	@Autowired
	protected JobDraftFileDao jobDraftFileDao;

	@Autowired
	protected JobResourcecategoryDao jobResourcecategoryDao;

	@Autowired
	protected JobSoftwareDao jobSoftwareDao;

	@Autowired
	protected SampleDraftDao sampleDraftDao;

	@Autowired
	protected SampleDraftMetaDao sampleDraftMetaDao;

	@Autowired
	protected SampleDraftJobDraftCellSelectionDao sampleDraftJobDraftCellSelectionDao;


	@Autowired
	protected JobDao jobDao;

	@Autowired
	protected LabDao labDao;

	@Autowired
	protected JobUserDao jobUserDao;

	@Autowired
	protected RoleDao roleDao;

	@Autowired
	protected ResourceDao resourceDao;

	@Autowired
	protected ResourceCategoryDao resourceCategoryDao;

	@Autowired
	protected SoftwareDao softwareDao;

	@Autowired
	protected ResourceTypeDao resourceTypeDao;

	@Autowired
	protected JobMetaDao jobMetaDao;

	@Autowired
	protected SampleDao sampleDao;

	@Autowired
	protected SampleMetaDao sampleMetaDao;

	@Autowired
	protected SampleFileDao sampleFileDao;

	@Autowired
	protected JobSampleDao jobSampleDao;
	
	@Autowired
	protected SampleTypeDao sampleTypeDao;
	
	@Autowired
	protected SampleSubtypeDao sampleSubtypeDao;

	@Autowired
	protected StatejobDao statejobDao;

	@Autowired
	protected StateDao stateDao;

	@Autowired
	protected TaskDao taskDao;
	
	@Autowired
	protected SampleSubtypeDao subSampleTypeDao;
	
	@Autowired
	protected WorkflowDao workflowDao;

	@Autowired
	protected WorkflowresourcecategoryDao workflowresourcecategoryDao;
	
	@Autowired
	protected WorkflowResourceTypeDao workflowResourceTypeDao;

	@Autowired
	protected WorkflowSoftwareDao workflowSoftwareDao;
	
	@Autowired
	protected AdaptorsetDao adaptorsetDao;
	
	@Autowired
	protected AdaptorDao adaptorDao;
	
	@Autowired
	protected AdaptorsetResourceCategoryDao adaptorsetResourceCategoryDao;

	
	@Autowired
	protected FileDao fileDao;

	@Autowired
	protected JobCellSelectionDao jobCellSelectionDao;

	@Autowired
	protected java.net.URI jobRunnerHost;
	
	@Autowired
	protected SampleJobCellSelectionDao sampleJobCellSelectionDao;
	
	@Autowired
	protected MessageService messageService;
	
	@Autowired
	protected SampleService sampleService;
		
	@Autowired
	protected AuthenticationService authenticationService;
	
	@Value("${wasp.download.folder}")
	protected String downloadFolder;


	protected final MetaHelperWebapp getMetaHelperWebapp() {
		return new MetaHelperWebapp(JobDraftMeta.class, request.getSession());
	}
	
	final public String defaultPageFlow = "/jobsubmit/modifymeta/{n};/jobsubmit/samples/{n};/jobsubmit/cells/{n};/jobsubmit/verify/{n};/jobsubmit/submit/{n};/jobsubmit/ok";

	public String nextPage(JobDraft jobDraft) {
		String pageFlow = this.defaultPageFlow;

		try {
			List<WorkflowMeta> wfmList = jobDraft.getWorkflow().getWorkflowMeta();
			for (WorkflowMeta wfm : wfmList) {
				if (wfm.getK().equals("workflow.submitpageflow")) {
					pageFlow = wfm.getV();
					break;
			}
		}
		} catch (Exception e) {
		}

		String context = request.getContextPath();
		String uri = request.getRequestURI();
	
		// strips context, lead slash ("/"), spring mapping
		String currentMapping = uri.replaceFirst(context, "").replaceFirst("\\.do.*$", "");


		String pageFlowArray[] = pageFlow.split(";");

		int found = -1;
		for (int i=0; i < pageFlowArray.length -1; i++) {
			String page = pageFlowArray[i];
			page = page.replaceAll("\\{n\\}", ""+jobDraft.getJobDraftId());
	
			if (currentMapping.equals(page)) {
				found = i;
				break;
			}
		}


		String targetPage = pageFlowArray[found+1] + ".do"; 

		targetPage = targetPage.replaceAll("\\{n\\}", ""+jobDraft.getJobDraftId());

		return "redirect:" + targetPage;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@PreAuthorize("hasRole('lu-*')")
	public String list(ModelMap m) {

		m.addAttribute("_metaList",	getMetaHelperWebapp().getMasterList(MetaBase.class));
		m.addAttribute(JQFieldTag.AREA_ATTR, getMetaHelperWebapp().getArea());
		m.addAttribute("_metaDataMessages", MetaHelper.getMetadataMessages(request.getSession()));

		return "jobsubmit/list";
	}

	@RequestMapping(value="/listJSON", method=RequestMethod.GET)	
	public String getListJSON(HttpServletResponse response) {
		
		String search = request.getParameter("_search");
		String searchStr = request.getParameter("searchString");
	
		String sord = request.getParameter("sord");
		String sidx = request.getParameter("sidx");
		
		String userId = request.getParameter("userId");
		String labId = request.getParameter("labId");
		
		//result
		Map <String, Object> jqgrid = new HashMap<String, Object>();
		
		List<JobDraft> jobDraftList;
		
		if (!search.equals("true")	&& userId.isEmpty()	&& labId.isEmpty()) {
			jobDraftList = sidx.isEmpty() ? this.jobDraftDao.getPendingJobDrafts() : this.jobDraftDao.getPendingJobDraftsOrderBy(sidx, sord);
		} else {
			  Map m = new HashMap();
			  
			  if (search.equals("true") && !searchStr.isEmpty())
				  m.put(request.getParameter("searchField"), request.getParameter("searchString"));
			  
			  if (!userId.isEmpty())
				  m.put("UserId", Integer.parseInt(userId));
			  
			  if (!labId.isEmpty())
				  m.put("labId", Integer.parseInt(labId));
			  
			  m.put("status", "PENDING");
			  jobDraftList = this.jobDraftDao.findByMap(m);
		}

		try {
			int pageIndex = Integer.parseInt(request.getParameter("page"));		// index of page
			int pageRowNum = Integer.parseInt(request.getParameter("rows"));	// number of rows in one page
			int rowNum = jobDraftList.size();										// total number of rows
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
				int selIndex = jobDraftList.indexOf(jobDao.findById(selId));
				frId = selIndex;
				toId = frId + 1;

				jqgrid.put("records", "1");
				jqgrid.put("total", "1");
				jqgrid.put("page", "1");
			}				

			List<JobDraft> itemPage = jobDraftList.subList(frId, toId);
			for (JobDraft item : itemPage) {
				Map cell = new HashMap();
				cell.put("id", item.getJobDraftId());
				 
				List<JobDraftMeta> itemMeta = getMetaHelperWebapp().syncWithMaster(item.getJobDraftMeta());
				
				User user = userDao.getById(item.getUserId());
				 					
				List<String> cellList=new ArrayList<String>(Arrays.asList(new String[] {
							"<a href='/wasp/jobsubmit/modify/"+item.getJobDraftId()+".do'>"+item.getName()+"</a>",
							user.getNameFstLst(),
							item.getLab().getName(),
							this.userDao.getUserByUserId(item.getLastUpdUser()).getNameFstLst(),
							item.getLastUpdTs().toString()
				}));
				 
				for (JobDraftMeta meta:itemMeta) {
					cellList.add(meta.getV());
				}
				 
				cell.put("cell", cellList);
				 
				rows.add(cell);
			}

			 
			jqgrid.put("rows",rows);
			 
			return outputJSON(jqgrid, response); 	
			 
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON " + jobDraftList,e);
		}
	
	}

	protected String generateCreateForm(ModelMap m) {
		User me = authenticationService.getAuthenticatedUser();

		List <LabUser> labUserAllRoleList = me.getLabUser();

		List <Lab> labList = new ArrayList();
		for (LabUser lu: labUserAllRoleList) {
			String roleName =	lu.getRole().getRoleName();

			if (roleName.equals("lu") ||
					roleName.equals("lm") ||
					roleName.equals("pi")) {
				labList.add(lu.getLab());
			}
		}
		if (labList.isEmpty()){
			waspErrorMessage("jobDraft.no_lab.error");
			return "redirect:/dashboard.do";
		}

		List <Workflow> workflowList = workflowDao.getActiveWorkflows();
		if (workflowList.isEmpty()){
			waspErrorMessage("jobDraft.no_workflows.error");
			return "redirect:/dashboard.do";
		}
		
		m.put("labs", labList); 
		m.put("workflows", workflowList); 
		return "jobsubmit/create";
	}

	@RequestMapping(value="/create.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('lu-*')")
	public String showCreateForm(ModelMap m) {
		// if we have been tracking another jobDraft remove the session variable
		if (request.getSession().getAttribute("jobDraftId") != null){
			request.getSession().removeAttribute("jobDraftId");
		}
		m.put("jobDraft", new JobDraft()); 
		return generateCreateForm(m);
	}

	@RequestMapping(value="/create.do", method=RequestMethod.POST)
	@PreAuthorize("hasRole('lu-*')")
	public String create (
			@Valid JobDraft jobDraftForm,
			BindingResult result,
			SessionStatus status,
			ModelMap m) {
		
		if (request.getSession().getAttribute("jobDraftId") != null){
			// user has pressed submit twice or used the back button and potentially modified the form
			return modify((Integer) request.getSession().getAttribute("jobDraftId"), jobDraftForm, result, status, m);
		}
		
		User me = authenticationService.getAuthenticatedUser();
		
		Errors errors = new BindException(result.getTarget(), "jobDraft");
		if (jobDraftForm.getLabId() == null || jobDraftForm.getLabId().intValue() < 1){
			errors.rejectValue("labId", "jobDraft.labId.error", "jobDraft.labId.error (no message has been defined for this property");
		}
		
		Map<String, String> jobDraftQuery = new HashMap<String, String>();
		String name = jobDraftForm.getName();
		if (name != null && !name.isEmpty()){
			// check we don't already have a job with this name
			jobDraftQuery.put("name", name);
			if (!jobDraftDao.findByMap(jobDraftQuery).isEmpty()){
				errors.rejectValue("name", "jobDraft.name_exists.error", "jobDraft.name_exists.error (no message has been defined for this property");
			}
		}
		
		result.addAllErrors(errors);
		if (result.hasErrors()) {
			waspErrorMessage("jobDraft.form.error");
			return generateCreateForm(m);
		}
		
		jobDraftForm.setUserId(me.getUserId());
		jobDraftForm.setStatus("PENDING");
		jobDraftForm.setCreatets(new Date());
		JobDraft jobDraftDb = jobDraftDao.save(jobDraftForm); 
		// sometimes if user presses submit button twice a job is created but on re-submission it complains
		// that the job name already exists. Also happens if the back button is used on job creation
		// Check session to see if we have already submitted job
		request.getSession().setAttribute("jobDraftId", (Integer) jobDraftDb.getJobDraftId());
		
		// Adds the jobdraft to authorized list
 		doReauth();

		return nextPage(jobDraftDb);
	}
	
	/**
	 * Returns true if the current logged in user is the job drafter, the jobDraft status is pending
	 * and the jobDraft object is not null and has a not-null jobDraftId
	 * @param jobDraft
	 * @return boolean
	 */
	protected boolean isJobDraftEditable(JobDraft jobDraft){
		if (jobDraft == null || jobDraft.getJobDraftId() == null){
			waspErrorMessage("jobDraft.jobDraft_null.error");
			return false;
		}
		
		// check if i am the drafter
		User me = authenticationService.getAuthenticatedUser();
		if (me.getUserId().intValue() != jobDraft.getUserId().intValue()) {
			waspErrorMessage("jobDraft.user_incorrect.error");
			return false;
		}
		
		// check that the status is PENDING
		if (! jobDraft.getStatus().equals("PENDING")) {
			waspErrorMessage("jobDraft.not_pending.error");
			return false;
		}
		return true;
	}

	@RequestMapping(value="/modify/{jobDraftId}.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String modify(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		// if we have been tracking another jobDraft remove the session variable
		if (request.getSession().getAttribute("jobDraftId") != null){
			request.getSession().removeAttribute("jobDraftId");
		}
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);

		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";

		m.put("jobDraft", jobDraft);
		return generateCreateForm(m);
	}



	@RequestMapping(value="/modify/{jobDraftId}.do", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String modify (
			@PathVariable Integer jobDraftId,
			@Valid JobDraft jobDraftForm,
			BindingResult result,
			SessionStatus status,
			ModelMap m) {

		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);

		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		
		Errors errors = new BindException(result.getTarget(), "jobDraft");
		
		Map<String, String> jobDraftQuery = new HashMap<String, String>();
		String name = jobDraftForm.getName();
		if (name != null && !name.isEmpty() && !name.equals(jobDraft.getName())){
			// check we don't already have a job with this name
			jobDraftQuery.put("name", name);
			if (!jobDraftDao.findByMap(jobDraftQuery).isEmpty()){
				errors.rejectValue("name", "jobDraft.name_exists.error", "jobDraft.name_exists.error (no message has been defined for this property");
			}
		}
		
		if (jobDraftForm.getLabId() == null || jobDraftForm.getLabId().intValue() < 1){
			errors.rejectValue("labId", "jobDraft.labId.error", "jobDraft.labId.error (no message has been defined for this property");
		}
		result.addAllErrors(errors);
		if (result.hasErrors()) {
			waspErrorMessage("jobDraft.form.error");
			m.put("jobDraft", jobDraftForm);
			return generateCreateForm(m);
		}

		jobDraft.setName(jobDraftForm.getName());
		jobDraft.setWorkflowId(jobDraftForm.getWorkflowId());
		jobDraft.setLabId(jobDraftForm.getLabId());

		JobDraft jobDraftDb = jobDraftDao.save(jobDraft); 

		return nextPage(jobDraftDb);
	}

	public String doModify (
			@Valid JobDraft jobDraftForm,
			BindingResult result,
			SessionStatus status,
			ModelMap m) {

		// TODO CHECK ACCESS OF LABUSER
		
		Errors errors = new BindException(result.getTarget(), "jobDraft");
		if (jobDraftForm.getLabId() == null || jobDraftForm.getLabId().intValue() < 1){
			errors.rejectValue("labId", "jobDraft.labId.error", "jobDraft.labId.error (no message has been defined for this property");
		}
		result.addAllErrors(errors);
		
		if (result.hasErrors()) {
			waspErrorMessage("jobDraft.form.error");
			m.put("jobDraft", jobDraftForm);
			return generateCreateForm(m);
		}

		JobDraft jobDraftDb = jobDraftDao.save(jobDraftForm); 

		return nextPage(jobDraftDb);
	}


	@RequestMapping(value="/modifymeta/{jobDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)") 
	public String showModifyMetaForm(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		 
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";

		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		metaHelperWebapp.setArea(jobDraft.getWorkflow().getIName());
		List<JobDraftMeta> jobDraftMeta = metaHelperWebapp.getMasterList(JobDraftMeta.class);
		if (jobDraftMeta.isEmpty()){
			// no metadata to capture
			return nextPage(jobDraft);
		}
			
		jobDraft.setJobDraftMeta(jobDraftMeta);
		// jobDraft.setJobDraftMeta(metaHelperWebapp.syncWithMaster(jobDraft.getJobDraftMeta()));


		m.put("jobDraft", jobDraft);
		m.put("area", metaHelperWebapp.getArea());
		m.put("parentarea", metaHelperWebapp.getParentArea());
		m.put("pageFlowMap", getPageFlowMap(jobDraft));
		
		return "jobsubmit/metaform";
	}


	
	
	@RequestMapping(value="/modifymeta/{jobDraftId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String modifyMeta(
			@PathVariable Integer jobDraftId,
			@Valid JobDraft jobDraftForm,
			BindingResult result,
			SessionStatus status,
			ModelMap m) {

		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";

		jobDraftForm.setJobDraftId(jobDraftId);
		jobDraftForm.setUserId(jobDraft.getUserId());
		jobDraftForm.setLabId(jobDraft.getLabId());
		jobDraftForm.setWorkflowId(jobDraft.getWorkflowId());

		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		
		metaHelperWebapp.setArea(jobDraft.getWorkflow().getIName());

		List<JobDraftMeta> jobDraftMetaList = metaHelperWebapp.getFromRequest(request, JobDraftMeta.class);

		jobDraftForm.setJobDraftMeta(jobDraftMetaList);

		metaHelperWebapp.validate(result);

		if (result.hasErrors()) {
			waspErrorMessage("jobDraft.form.error");

			m.put("jobDraft", jobDraft);
			m.put("area", metaHelperWebapp.getArea());
			m.put("parentarea", metaHelperWebapp.getParentArea());
	        m.put("pageFlowMap", getPageFlowMap(jobDraft));
	
			return "jobsubmit/metaform";
		}

		jobDraftMetaDao.replaceByJobdraftId(metaHelperWebapp.getArea(), jobDraftId, jobDraftMetaList);

		return nextPage(jobDraft);
	}
	
	@RequestMapping(value="/resource/{resourceTypeIName}/{jobDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showResourceMetaForm(
			@PathVariable("resourceTypeIName") String resourceTypeIName, 
			@PathVariable("jobDraftId") Integer jobDraftId, 
			ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		// check at least one resource exists of the requested resource type
		WorkflowResourceType wrt = workflowResourceTypeDao.getWorkflowResourceTypeByWorkflowIdResourceTypeId(jobDraft.getWorkflow().getWorkflowId(), 
				resourceTypeDao.getResourceTypeByIName(resourceTypeIName).getResourceTypeId());
		if (wrt.getResourceTypeId() == null){
			waspErrorMessage("jobDraft.no_resources.error");
			return "redirect:/dashboard.do";
		}
		return showResourceMetaForm(resourceTypeIName, jobDraftId, null, m);
	}
	
	public String showResourceMetaForm(String resourceTypeIName, Integer jobDraftId, JobDraft jobDraftForm, ModelMap m){
		// make list of available resources
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		List<Workflowresourcecategory> allWorkflowResourceCategories = jobDraft.getWorkflow().getWorkflowresourcecategory();
		List<Workflowresourcecategory> workflowResourceCategories = new ArrayList();
		for (Workflowresourcecategory w: allWorkflowResourceCategories) {
			if (! w.getResourceCategory().getResourceType().getIName().equals(resourceTypeIName)) { continue; }
			workflowResourceCategories.add(w); 
		}
		if (workflowResourceCategories.isEmpty()){
			waspErrorMessage("jobDraft.resourceCategories_not_configured.error");
			return "redirect:/dashboard.do";
		}

		// get selected resource
		JobDraftresourcecategory jobDraftResourceCategory = null; 
		String resourceCategoryArea = ""; 
		String resourceCategoryName = ""; 
		for (JobDraftresourcecategory jdrc: jobDraft.getJobDraftresourcecategory()) {
			if (resourceTypeIName.equals( jdrc.getResourceCategory().getResourceType().getIName())) {
				jobDraftResourceCategory = jdrc;
				resourceCategoryArea = jdrc.getResourceCategory().getIName(); 
				resourceCategoryName = jdrc.getResourceCategory().getName();
				break;
			}
		}

		// Resource Options loading
		Map<String, List<MetaAttribute.Control.Option>> resourceOptions = new HashMap<String, List<MetaAttribute.Control.Option>>();

		if (jobDraftResourceCategory != null) {
			Workflowresourcecategory workflowresourcecategory = workflowresourcecategoryDao.getWorkflowresourcecategoryByWorkflowIdResourcecategoryId(jobDraft.getWorkflow().getWorkflowId(), jobDraftResourceCategory.getResourcecategoryId());
			for (WorkflowresourcecategoryMeta wrm: workflowresourcecategory.getWorkflowresourcecategoryMeta()) {
				String key = wrm.getK(); 
	
	//			if (! key.matches("^.*allowableUiField\\.")) { continue; }
				key = key.replaceAll("^.*allowableUiField\\.", "");
				List<MetaAttribute.Control.Option> options=new ArrayList<MetaAttribute.Control.Option>();
				for(String el: org.springframework.util.StringUtils.tokenizeToStringArray(wrm.getV(),";")) {
					String [] pair=StringUtils.split(el,":");
					MetaAttribute.Control.Option option = new MetaAttribute.Control.Option();
					option.setValue(pair[0]);
					option.setLabel(pair[1]);
					options.add(option);
				}
				if (options.isEmpty()){
					waspErrorMessage("jobDraft.resourceCategories_not_configured.error");
					return "redirect:/dashboard.do";
				}
				resourceOptions.put(key, options);
			}
		}


		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		metaHelperWebapp.setArea(resourceCategoryArea);
		
		if (jobDraftForm == null){
			jobDraftForm = jobDraft; // shallow copy
			jobDraftForm.setJobDraftMeta(metaHelperWebapp.syncWithMaster(jobDraft.getJobDraftMeta()));
		}

		m.put("workflowResourceCategories", workflowResourceCategories);
		m.put("jobDraft", jobDraftForm);
		m.put("name", resourceCategoryName);
		m.put("area", metaHelperWebapp.getArea());
		m.put("jobDraftResourceCategory", jobDraftResourceCategory);
		m.put("resourceOptions", resourceOptions);
		m.put("parentarea", metaHelperWebapp.getParentArea());
		m.put("pageFlowMap", getPageFlowMap(jobDraft));
		
		return "jobsubmit/resource";
	}

	@RequestMapping(value="/resource/{resourceTypeIName}/{jobDraftId}", method=RequestMethod.POST)
	public String modifyResourceMeta (
			@PathVariable String resourceTypeIName,
			@PathVariable Integer jobDraftId,
			@Valid JobDraft jobDraftForm,
			BindingResult result,
			SessionStatus status,
			ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		// check at least one resource exists of the requested resource type
		WorkflowResourceType wrt = workflowResourceTypeDao.getWorkflowResourceTypeByWorkflowIdResourceTypeId(jobDraft.getWorkflow().getWorkflowId(), 
				resourceTypeDao.getResourceTypeByIName(resourceTypeIName).getResourceTypeId());
		if (wrt.getResourceTypeId() == null){
			waspErrorMessage("jobDraft.no_resources.error");
			return "redirect:/dashboard.do";
		}
		Map params = request.getParameterMap();
		Integer changeResource = null;
		try {
			changeResource = Integer.parseInt(((String[])params.get("changeResource"))[0]);
		} catch (Exception e) {
		}

		// The resource is changing
		// set the resource and reload the page.
		// todo: consider wiping out old meta values?
		if (changeResource != null){
			List<JobDraftresourcecategory> oldJdrs = jobDraft.getJobDraftresourcecategory();
			for (JobDraftresourcecategory jdr: oldJdrs) {
				if (jdr.getResourceCategory().getResourceType().getIName().equals(resourceTypeIName)){
					jobDraftresourcecategoryDao.remove(jdr);
					jobDraftresourcecategoryDao.flush(jdr);
				}
			}
			if (changeResource.intValue() == -1) // nothing selected
				return "redirect:/jobsubmit/resource/" + resourceTypeIName + "/" + jobDraftId + ".do";
			JobDraftresourcecategory newJdr = new JobDraftresourcecategory();
			newJdr.setJobdraftId(jobDraftId);
			newJdr.setResourcecategoryId(changeResource);
			jobDraftresourcecategoryDao.save(newJdr);

			return "redirect:/jobsubmit/resource/" + resourceTypeIName + "/" + jobDraftId + ".do";
		}


		// get selected resource
		String resourceCategoryArea = ""; 
		for (JobDraftresourcecategory jdr: jobDraft.getJobDraftresourcecategory()) {
			if (! resourceTypeIName.equals( jdr.getResourceCategory().getResourceType().getIName())) { continue; }
			resourceCategoryArea = jdr.getResourceCategory().getIName();
		}
		
		if (resourceCategoryArea.isEmpty()){
			waspErrorMessage("jobDraft.changeResource.error");
			return "redirect:/jobsubmit/resource/" + resourceTypeIName + "/" + jobDraftId + ".do";
		}
		
		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		metaHelperWebapp.setArea(resourceCategoryArea);

		List<JobDraftMeta> jobDraftMetaList = metaHelperWebapp.getFromRequest(request, JobDraftMeta.class);
		
		metaHelperWebapp.validate(result);
		jobDraftForm.setJobDraftMeta(jobDraftMetaList);
		
		if (result.hasErrors()) {
			waspErrorMessage("jobDraft.form.error");
			jobDraftForm.setName(jobDraft.getName());
			jobDraftForm.setWorkflowId(jobDraft.getWorkflowId());
			jobDraftForm.setLabId(jobDraft.getLabId());
			return showResourceMetaForm(resourceTypeIName, jobDraftId, jobDraftForm, m);
		}


		jobDraftMetaDao.replaceByJobdraftId(metaHelperWebapp.getArea(), jobDraftId, jobDraftMetaList);

		return nextPage(jobDraft);
	}


  /**
   * show software form
   */
	
	public String showSoftwareForm(String resourceTypeIName, Integer jobDraftId, JobDraft jobDraftForm, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		// make list of available resources
		List<WorkflowSoftware> allWorkflowSoftwares = jobDraft.getWorkflow().getWorkflowSoftware();
		List<WorkflowSoftware> workflowSoftwares = new ArrayList<WorkflowSoftware>();
		for (WorkflowSoftware w: allWorkflowSoftwares) {
			if (! w.getSoftware().getResourceType().getIName().equals(resourceTypeIName)) { continue; }
			workflowSoftwares.add(w); 
		}
		if (workflowSoftwares.isEmpty()){
			waspErrorMessage("jobDraft.software_not_configured.error");
			return "redirect:/dashboard.do";
		}

		// get selected resource
		JobDraftSoftware jobDraftSoftware = null; 
		String softwareArea = ""; 
		String softwareName = ""; 
		for (JobDraftSoftware jds: jobDraft.getJobDraftSoftware()) {
			if (resourceTypeIName.equals( jds.getSoftware().getResourceType().getIName())) { 
				jobDraftSoftware = jds;
				softwareArea = jds.getSoftware().getIName(); 
				softwareName = jds.getSoftware().getName(); 
				break;
			}
		}
		

		// Resource Options loading
		Map<String, List<MetaAttribute.Control.Option>> resourceOptions = new HashMap<String, List<MetaAttribute.Control.Option>>();

		if (jobDraftSoftware != null) {
			WorkflowSoftware workflowSoftware = workflowSoftwareDao.getWorkflowSoftwareByWorkflowIdSoftwareId(jobDraft.getWorkflow().getWorkflowId(), jobDraftSoftware.getSoftwareId());
			for (WorkflowsoftwareMeta wrm: workflowSoftware.getWorkflowsoftwareMeta()) {
				String key = wrm.getK(); 
	
	//			if (! key.matches("^.*allowableUiField\\.")) { continue; }
				key = key.replaceAll("^.*allowableUiField\\.", "");
				List<MetaAttribute.Control.Option> options=new ArrayList<MetaAttribute.Control.Option>();
				for(String el: org.springframework.util.StringUtils.tokenizeToStringArray(wrm.getV(),";")) {
					String [] pair=StringUtils.split(el,":");
					MetaAttribute.Control.Option option = new MetaAttribute.Control.Option();
					option.setValue(pair[0]);
					option.setLabel(pair[1]);
					options.add(option);
				}
				if (options.isEmpty()){
					waspErrorMessage("jobDraft.software_not_configured.error");
					return "redirect:/dashboard.do";
				}
				resourceOptions.put(key, options);
			}
		}

		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		metaHelperWebapp.setArea(softwareArea);

		if (jobDraftForm == null){
			jobDraftForm = jobDraft; // shallow copy
			jobDraftForm.setJobDraftMeta(metaHelperWebapp.syncWithMaster(jobDraft.getJobDraftMeta()));
		}

		m.put("workflowSoftwares", workflowSoftwares);
		m.put("jobDraft", jobDraftForm);
		m.put("name", softwareName);
		m.put("area", metaHelperWebapp.getArea());
		m.put("jobDraftSoftware", jobDraftSoftware);
		m.put("resourceOptions", resourceOptions);
		m.put("parentarea", metaHelperWebapp.getParentArea());
		m.put("pageFlowMap", getPageFlowMap(jobDraft));
		
		return "jobsubmit/software";
	}
	
	

	@RequestMapping(value="/software/{resourceTypeIName}/{jobDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showSoftwareForm(
			@PathVariable("resourceTypeIName") String resourceTypeIName, 
			@PathVariable("jobDraftId") Integer jobDraftId, 
			ModelMap m) {
		// check at least one resource exists of the requested resource type
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		WorkflowResourceType wrt = workflowResourceTypeDao.getWorkflowResourceTypeByWorkflowIdResourceTypeId(jobDraft.getWorkflow().getWorkflowId(), 
				resourceTypeDao.getResourceTypeByIName(resourceTypeIName).getResourceTypeId());
		if (wrt.getResourceTypeId() == null){
			waspErrorMessage("jobDraft.no_resources.error");
			return "redirect:/dashboard.do";
		}
		return showSoftwareForm(resourceTypeIName, jobDraftId, null ,m);
	}

	@RequestMapping(value="/software/{resourceTypeIName}/{jobDraftId}", method=RequestMethod.POST)
	public String modifySoftwareMeta (
			@PathVariable String resourceTypeIName,
			@PathVariable Integer jobDraftId,
			@Valid JobDraft jobDraftForm,
			BindingResult result,
			SessionStatus status,
			ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		WorkflowResourceType wrt = workflowResourceTypeDao.getWorkflowResourceTypeByWorkflowIdResourceTypeId(jobDraft.getWorkflow().getWorkflowId(), 
				resourceTypeDao.getResourceTypeByIName(resourceTypeIName).getResourceTypeId());
		if (wrt.getResourceTypeId() == null){
			waspErrorMessage("jobDraft.no_resources.error");
			return "redirect:/dashboard.do";
		}
		
		Map params = request.getParameterMap();
		Integer changeResource = null;
		try {
			changeResource = Integer.parseInt(((String[])params.get("changeResource"))[0]);
		} catch (Exception e) {
		}

		// The resource is changing
		// set the resource and reload the page.
		// todo: consider wiping out old meta values?
		if (changeResource != null) {
			List<JobDraftSoftware> oldJds = jobDraft.getJobDraftSoftware();
			for (JobDraftSoftware jds: oldJds) {
				if (jds.getSoftware().getResourceType().getIName().equals(resourceTypeIName))
				jobDraftSoftwareDao.remove(jds);
				jobDraftSoftwareDao.flush(jds);
			}
			if (changeResource.intValue() == -1) // nothing selected
				return "redirect:/jobsubmit/software/" + resourceTypeIName + "/" + jobDraftId + ".do";
			JobDraftSoftware newJdr = new JobDraftSoftware();
			newJdr.setJobdraftId(jobDraftId);
			newJdr.setSoftwareId(changeResource);
			jobDraftSoftwareDao.save(newJdr);

			return "redirect:/jobsubmit/software/" + resourceTypeIName + "/" + jobDraftId + ".do";
		}

		// get selected resource
		String softwareArea = ""; 
		for (JobDraftSoftware jobDraftSoftware: jobDraft.getJobDraftSoftware()) {
			if (! resourceTypeIName.equals( jobDraftSoftware.getSoftware().getResourceType().getIName())) { continue; }
			softwareArea = jobDraftSoftware.getSoftware().getIName();
		}
		
		if (softwareArea.isEmpty()){
			waspErrorMessage("jobDraft.changeSoftwareResource.error");
			return "redirect:/jobsubmit/software/" + resourceTypeIName + "/" + jobDraftId + ".do";
		}
		
		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		metaHelperWebapp.setArea(softwareArea);

		List<JobDraftMeta> jobDraftMetaList = metaHelperWebapp.getFromRequest(request, JobDraftMeta.class);
		jobDraftForm.setJobDraftMeta(jobDraftMetaList);
		metaHelperWebapp.validate(result);
		
		
		
		if (result.hasErrors()) {
			jobDraftForm.setName(jobDraft.getName());
			jobDraftForm.setWorkflowId(jobDraft.getWorkflowId());
			jobDraftForm.setLabId(jobDraft.getLabId());
			waspErrorMessage("jobDraft.form.error");
			String returnPage = showSoftwareForm(resourceTypeIName, jobDraftId, jobDraftForm, m); 
			return returnPage;
		}

		jobDraftMetaDao.replaceByJobdraftId(metaHelperWebapp.getArea(), jobDraftId, jobDraftMetaList);

		return nextPage(jobDraft);
	}


	@RequestMapping(value="/additionalMeta/{meta}/{jobDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showAdditionalMetaForm(@PathVariable("meta") String additionalMetaArea, @PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		
		MetaHelperWebapp workflowMetaHelperWebapp = getMetaHelperWebapp();
		workflowMetaHelperWebapp.setArea(jobDraft.getWorkflow().getIName());

		List<JobDraftMeta> jobDraftMeta = workflowMetaHelperWebapp.syncWithMaster(jobDraft.getJobDraftMeta()); 

		JobDraftMeta ametaJdm = new JobDraftMeta();
		String ametaArea = "";
		for (JobDraftMeta jdm: jobDraftMeta) {
			if (! jdm.getK().equals(workflowMetaHelperWebapp.getArea() + "." + additionalMetaArea)) { continue; }
			ametaArea = jdm.getV();
			ametaJdm = jdm;
		}
		if (ametaArea.isEmpty()){
			// no additional meta found with supplied meta area
			return nextPage(jobDraft);
		}

		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		metaHelperWebapp.setArea(ametaArea);
		jobDraft.setJobDraftMeta(metaHelperWebapp.syncWithMaster(jobDraft.getJobDraftMeta()));


		m.put("jobDraft", jobDraft);
		m.put("area", metaHelperWebapp.getArea());
		m.put("parentarea", metaHelperWebapp.getParentArea());
		
        m.put("pageFlowMap", getPageFlowMap(jobDraft));
		
		return "jobsubmit/metaform";
	}

	@RequestMapping(value="/additionalMeta/{meta}/{jobDraftId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String modifyAdditionalMeta (
			@PathVariable String additionalMetaArea,
			@PathVariable Integer jobDraftId,
			@Valid JobDraft jobDraftForm,
			BindingResult result,
			SessionStatus status,
			ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		MetaHelperWebapp workflowMetaHelperWebapp = getMetaHelperWebapp();
		workflowMetaHelperWebapp.setArea(jobDraft.getWorkflow().getIName());

		List<JobDraftMeta> jobDraftMeta = workflowMetaHelperWebapp.syncWithMaster(jobDraft.getJobDraftMeta()); 

		JobDraftMeta ametaJdm = new JobDraftMeta();
		String ametaArea = "";
		for (JobDraftMeta jdm: jobDraftMeta) {
			if (! jdm.getK().equals(workflowMetaHelperWebapp.getArea() + "." + additionalMetaArea)) { continue; }
			ametaArea = jdm.getV();
			ametaJdm = jdm;
		}
		if (ametaArea.isEmpty()){
			// no additional meta found with supplied meta area
			return nextPage(jobDraft);
		}

		MetaHelperWebapp metaHelperWebapp = getMetaHelperWebapp();
		metaHelperWebapp.setArea(ametaArea);
		List<JobDraftMeta> jobDraftMetaList = metaHelperWebapp.getFromRequest(request, JobDraftMeta.class);

		jobDraftForm.setJobDraftMeta(jobDraftMetaList);
		metaHelperWebapp.validate(result);

		if (result.hasErrors()) {
			waspErrorMessage("jobDraft.form.error");
			m.put("jobDraft", jobDraft);
			m.put("area", metaHelperWebapp.getArea());
			m.put("parentarea", metaHelperWebapp.getParentArea());
			m.put("pageFlowMap", getPageFlowMap(jobDraft));
	
			return "jobsubmit/metaform";
		}


		// sync meta data in DB (e.g.removes old aligners)
		for (MetaAttribute.Control.Option opt: ametaJdm.getProperty().getControl().getOptions()) {
			jobDraftMetaDao.replaceByJobdraftId(opt.getValue(), jobDraftId, new ArrayList());
		}

		jobDraftMetaDao.replaceByJobdraftId(metaHelperWebapp.getArea(), jobDraftId, jobDraftMetaList);

		return nextPage(jobDraft);

	}
	
	@RequestMapping(value="/samples/{jobDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showSampleDraftList(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		List<SampleDraft> sampleDraftList = jobDraft.getSampleDraft();
		String[] roles = new String[1];
		roles[0] = "lu";
		List<SampleSubtype> sampleSubtypeList = sampleService.getSampleSubtypesForWorkflowByRole(jobDraft.getWorkflowId(), roles);
		List<File> files = new ArrayList<File>();
		for(JobDraftFile jdf: jobDraft.getJobDraftFile())
			files.add(jdf.getFile());
		m.addAttribute("jobDraft", jobDraft);
		m.addAttribute("sampleDraftList", sampleDraftList);
		m.addAttribute("sampleSubtypeList", sampleSubtypeList);
		m.addAttribute("pageFlowMap", getPageFlowMap(jobDraft));
		m.addAttribute("files", files);
		return "jobsubmit/sample";
	}
	
	@RequestMapping(value="/samples/{jobDraftId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String submitSampleDraftList(
			@PathVariable("jobDraftId") Integer jobDraftId, 
			@RequestParam("file_description") List<String> fileDescriptions,
			@RequestParam("file_upload") List<MultipartFile> mpFiles) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		if (jobDraft.getSampleDraft().isEmpty()){
			waspErrorMessage("jobDraft.noSamples.error");
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do"; 
		}
		
		if (mpFiles != null){
			int fileCount = -1;
			for (MultipartFile mpFile: mpFiles){
				fileCount++;
				if (mpFile.isEmpty())
					continue;
				String path = downloadFolder+"/"+jobDraftId;
				String absolutePath = path+"/"+mpFile.getOriginalFilename(); 
				java.io.File pathFile = new java.io.File(path);
				if (!pathFile.exists()){
					try{
						pathFile.mkdir();
					} catch(Exception e){
						logger.error("File upload failure trying to create '"+path+"': "+e.getMessage());
						waspErrorMessage("jobDraft.upload_file.error");
						break;
					}
				}
								
				String md5Hash = "";
				try {
					md5Hash = DigestUtils.md5Hex(mpFile.getInputStream());
				} catch (IOException e) {
					logger.warn("Cannot generate MD5 Hash for '"+mpFile.getOriginalFilename()+"': "+ e.getMessage());
				}
				String fileName = mpFile.getOriginalFilename();
				Integer fileSizeK = (int)(mpFile.getSize()/1024);
				String contentType = mpFile.getContentType();
				logger.debug("Uploading file '"+fileName+"' to '"+absolutePath+"' (type="+contentType+", size="+fileSizeK+"Kb, md5Hash="+md5Hash+")");
				java.io.File newFile = new java.io.File(absolutePath);
				try{
					mpFile.transferTo(newFile);
				} catch(Exception e){
					logger.error("File upload failure trying to save '"+absolutePath+"': "+e.getMessage());
					waspErrorMessage("jobDraft.upload_file.error");
					continue;
				}
				File file = new File();
				file.setDescription(fileDescriptions.get(fileCount));
				file.setAbsolutePath(absolutePath);
				file.setIsActive(1);
				file.setContentType(contentType);
				file.setMd5hash(md5Hash);
				file.setSizek(fileSizeK);
				
									
				fileDao.persist(file);
				JobDraftFile jobDraftFile = new JobDraftFile();
				jobDraftFile.setFile(file);
				jobDraftFile.setJobDraft(jobDraft);
				jobDraftFileDao.persist(jobDraftFile);
			}
		}
		return nextPage(jobDraft);
	}
	
	@RequestMapping(value="/samples/view/{jobDraftId}/{sampleDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String viewSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, @PathVariable("sampleDraftId") Integer sampleDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleDraft sampleDraft = sampleDraftDao.getSampleDraftBySampleDraftId(sampleDraftId);
		if (sampleDraft.getSampleDraftId() == null){
			waspErrorMessage("jobDraft.jobDraft_null.error");
			return "redirect:/jobsubmit/sample.do";
		}
		List<SampleDraftMeta> normalizedMeta = new ArrayList<SampleDraftMeta>();
		try {
			normalizedMeta.addAll(SampleAndSampleDraftMetaHelper.templateMetaToSubtypeAndSynchronizeWithMaster(sampleDraft.getSampleSubtype(), sampleDraft.getSampleDraftMeta(), SampleDraftMeta.class));
		} catch (MetadataTypeException e) {
			logger.warn("Could not get meta for class 'SampleDraftMeta':" + e.getMessage());
		}
		if (sampleDraft.getSampleType().getIName().equals("library")){
			// library specific functionality
			prepareAdaptorsetsAndAdaptors(jobDraft, normalizedMeta, m);
		}
		m.addAttribute("normalizedMeta", normalizedMeta);
		m.addAttribute("sampleDraft", sampleDraft);
		m.addAttribute("jobDraft", jobDraft);
		return "jobsubmit/sample/sampledetail_ro";
	}
	
	
	@RequestMapping(value="/samples/remove/{jobDraftId}/{sampleDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String removeSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, @PathVariable("sampleDraftId") Integer sampleDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleDraft sampleDraft = sampleDraftDao.getSampleDraftBySampleDraftId(sampleDraftId);
		if (sampleDraft.getSampleDraftId() == null){
			waspErrorMessage("jobDraft.jobDraft_null.error");
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
		}
		Map<String, Integer> query = new HashMap<String, Integer>();
		query.put("sampledraftId", sampleDraftId);
		for (SampleDraftMeta sdm : sampleDraftMetaDao.findByMap(query)){
			sampleDraftMetaDao.remove(sdm);
		}
		sampleDraftDao.remove(sampleDraft);
		waspMessage("sampleDetail.updated_success.label");
		return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
	}
	

	
	@RequestMapping(value="/samples/edit/{jobDraftId}/{sampleDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String editSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, @PathVariable("sampleDraftId") Integer sampleDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleDraft sampleDraft = sampleDraftDao.getSampleDraftBySampleDraftId(sampleDraftId);
		if (sampleDraft.getSampleDraftId() == null){
			waspErrorMessage("jobDraft.jobDraft_null.error");
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
		}
		List<SampleDraftMeta> normalizedMeta = new ArrayList<SampleDraftMeta>();
		try {
			normalizedMeta.addAll(SampleAndSampleDraftMetaHelper.templateMetaToSubtypeAndSynchronizeWithMaster(sampleDraft.getSampleSubtype(), sampleDraft.getSampleDraftMeta(), SampleDraftMeta.class));
		} catch (MetadataTypeException e) {
			logger.warn("Could not get meta for class 'SampleDraftMeta':" + e.getMessage());
		}
		if (sampleDraft.getSampleType().getIName().equals("library")){
			prepareAdaptorsetsAndAdaptors(jobDraft, normalizedMeta, m);
		}
		m.addAttribute("heading", messageService.getMessage("jobDraft.sample_edit_heading.label"));
		m.addAttribute("normalizedMeta", normalizedMeta);
		m.addAttribute("sampleDraft", sampleDraft);
		m.addAttribute("jobDraft", jobDraft);
		return "jobsubmit/sample/sampledetail_rw";
	}
	
	@RequestMapping(value="/samples/edit/{jobDraftId}/{sampleDraftId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String updateSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, 
			@PathVariable("sampleDraftId") Integer sampleDraftId,
			@Valid SampleDraft sampleDraftForm, BindingResult result, SessionStatus status, ModelMap m) {
		if ( request.getParameter("submit").equals("Cancel") ){//equals(messageService.getMessage("userDetail.cancel.label")
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
		}
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleDraft sampleDraft = sampleDraftDao.getSampleDraftBySampleDraftId(sampleDraftId);
		if (sampleDraft.getSampleDraftId() == null){
			waspErrorMessage("jobDraft.sampleDraft_null.error");
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
		}
		SampleSubtype sampleSubtype = sampleSubtypeDao.getSampleSubtypeBySampleSubtypeId(sampleDraftForm.getSampleSubtypeId());
		List<SampleDraftMeta> metaFromForm = new ArrayList<SampleDraftMeta>();
		try {
			metaFromForm.addAll(SampleAndSampleDraftMetaHelper.getValidatedMetaFromRequestAndTemplateToSubtype(request, sampleSubtype, result, SampleDraftMeta.class));
		} catch (MetadataTypeException e) {
			logger.warn("Could not get meta for class 'SampleDraftMeta':" + e.getMessage());
		}
		sampleDraftForm.setName(sampleDraftForm.getName().trim());//from the form
		validateSampleDraftNameUnique(sampleDraftForm.getName(), sampleDraftId, jobDraft, result);
		if (result.hasErrors()){
			waspErrorMessage("sampleDetail.updated.error");
			if (sampleDraftForm.getSampleType().getIName().equals("library")){
				// library specific functionality
				prepareAdaptorsetsAndAdaptors(jobDraft, metaFromForm, m);
			}
			m.addAttribute("heading", messageService.getMessage("jobDraft.sample_edit_heading.label"));
			m.addAttribute("normalizedMeta", metaFromForm);
			m.addAttribute("sampleDraft", sampleDraftForm);
			m.addAttribute("jobDraft", jobDraft);
			return "jobsubmit/sample/sampledetail_rw";
		}
		// all ok so save
		if (!sampleDraft.getName().equals(sampleDraftForm.getName()))
			sampleDraft.setName(sampleDraftForm.getName());
		sampleDraftDao.save(sampleDraft);
		sampleDraftMetaDao.updateBySampledraftId(sampleDraft.getSampleDraftId(), metaFromForm);
		waspMessage("sampleDetail.updated_success.label");
		return "redirect:/jobsubmit/samples/view/"+jobDraftId+"/"+sampleDraftId+".do";
	}
	
	@RequestMapping(value="/samples/clone/{jobDraftId}/{sampleDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String cloneSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, @PathVariable("sampleDraftId") Integer sampleDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleDraft sampleDraft = sampleDraftDao.getSampleDraftBySampleDraftId(sampleDraftId);
		if (sampleDraft.getSampleDraftId() == null){
			waspErrorMessage("jobDraft.jobDraft_null.error");
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
		}
		SampleDraft clone = sampleService.cloneSampleDraft(sampleDraft);
		clone.setName("");
		List<SampleDraftMeta> normalizedMeta = new ArrayList<SampleDraftMeta>();
		try {
			normalizedMeta.addAll(SampleAndSampleDraftMetaHelper.templateMetaToSubtypeAndSynchronizeWithMaster(clone.getSampleSubtype(), clone.getSampleDraftMeta(), SampleDraftMeta.class));
		} catch (MetadataTypeException e) {
			logger.warn("Could not get meta for class 'SampleDraftMeta':" + e.getMessage());
		}
		if (clone.getSampleType().getIName().equals("library")){
			prepareAdaptorsetsAndAdaptors(jobDraft, clone.getSampleDraftMeta(), m);
		}
		m.addAttribute("heading", messageService.getMessage("jobDraft.sample_clone_heading.label"));
		m.addAttribute("normalizedMeta", normalizedMeta);
		m.addAttribute("sampleDraft", clone);
		m.addAttribute("jobDraft", jobDraft);
		return "jobsubmit/sample/sampledetail_rw";
	}
	
	@RequestMapping(value="/samples/clone/{jobDraftId}/{sdi}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String updateCloneSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId,
			@PathVariable("sdi") Integer sdi, 
			@Valid SampleDraft cloneForm, BindingResult result, SessionStatus status, ModelMap m){
		String viewString = this.updateNewSampleDraft(jobDraftId, Integer.parseInt(request.getParameter("sampleSubtypeId")), cloneForm, result, status, m);
		m.addAttribute("heading", messageService.getMessage("jobDraft.sample_clone_heading.label"));
		return viewString;
	}
	
	@RequestMapping(value="/samples/add/{jobDraftId}/{sampleSubtypeId}.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String newSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, @PathVariable("sampleSubtypeId") Integer sampleSubtypeId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleDraft sampleDraft = new SampleDraft();
		SampleSubtype sampleSubtype = sampleSubtypeDao.getSampleSubtypeBySampleSubtypeId(sampleSubtypeId);
		if (sampleSubtype.getSampleSubtypeId() == null){
			waspErrorMessage("jobDraft.sampleSubtype_null.error");
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
		}
		sampleDraft.setSampleSubtypeId(sampleSubtypeId);
		sampleDraft.setSampleSubtype(sampleSubtype);
		sampleDraft.setSampleTypeId(sampleSubtype.getSampleType().getSampleTypeId());
		sampleDraft.setSampleType(sampleSubtype.getSampleType());
		List<SampleDraftMeta> normalizedMeta = new ArrayList<SampleDraftMeta>();
		try {
			normalizedMeta.addAll(SampleAndSampleDraftMetaHelper.templateMetaToSubtypeAndSynchronizeWithMaster(sampleSubtype, SampleDraftMeta.class));
		} catch (MetadataTypeException e) {
			logger.warn("Could not get meta for class 'SampleDraftMeta':" + e.getMessage());
		}
		if (sampleDraft.getSampleType().getIName().equals("library")){
			prepareAdaptorsetsAndAdaptors(jobDraft, normalizedMeta, m);
		}
		m.addAttribute("heading", messageService.getMessage("jobDraft.sample_add_heading.label"));
		m.addAttribute("normalizedMeta", normalizedMeta);
		m.addAttribute("sampleDraft", sampleDraft);
		m.addAttribute("jobDraft", jobDraft);
		return "jobsubmit/sample/sampledetail_rw";
	}
	
	
	@RequestMapping(value="/samples/add/{jobDraftId}/{sampleSubtypeId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String updateNewSampleDraft(
			@PathVariable("jobDraftId") Integer jobDraftId, 
			@PathVariable("sampleSubtypeId") Integer sampleSubtypeId,
			@Valid SampleDraft sampleDraftForm, BindingResult result, SessionStatus status, ModelMap m) {
		if ( request.getParameter("submit").equals("Cancel") ){//equals(messageService.getMessage("userDetail.cancel.label")
			return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
		}
		
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleSubtype sampleSubtype = sampleSubtypeDao.getSampleSubtypeBySampleSubtypeId(sampleDraftForm.getSampleSubtypeId());
		List<SampleDraftMeta> metaFromForm = new ArrayList<SampleDraftMeta>();
		try {
			metaFromForm.addAll(SampleAndSampleDraftMetaHelper.getValidatedMetaFromRequestAndTemplateToSubtype(request, sampleSubtype, result, SampleDraftMeta.class));
		} catch (MetadataTypeException e) {
			logger.warn("Could not get meta for class 'SampleDraftMeta':" + e.getMessage());
		}
		sampleDraftForm.setName(sampleDraftForm.getName().trim());//from the form
		sampleDraftForm.setSampleSubtype(sampleSubtypeDao.getSampleSubtypeBySampleSubtypeId(sampleDraftForm.getSampleSubtypeId()));
		sampleDraftForm.setSampleType(sampleTypeDao.getSampleTypeBySampleTypeId(sampleDraftForm.getSampleTypeId()));
		validateSampleDraftNameUnique(sampleDraftForm.getName(), 0, jobDraft, result);
		
		if (result.hasErrors()){
			if (sampleDraftForm.getSampleType().getIName().equals("library")){
				// library specific functionality
				prepareAdaptorsetsAndAdaptors(jobDraft, metaFromForm, m);
			}
			waspErrorMessage("sampleDetail.updated.error");
			m.addAttribute("heading", messageService.getMessage("jobDraft.sample_add_heading.label"));
			m.addAttribute("normalizedMeta", metaFromForm);
			m.addAttribute("sampleDraft", sampleDraftForm);
			m.addAttribute("jobDraft", jobDraft);
			return "jobsubmit/sample/sampledetail_rw";
		}
		// all ok so save
		sampleDraftForm.setLabId(jobDraft.getLabId());
		sampleDraftForm.setUserId(jobDraft.getUserId());
		sampleDraftForm.setJobdraftId(jobDraftId);
		SampleDraft sampleDraftDb = sampleDraftDao.save(sampleDraftForm);
		sampleDraftMetaDao.updateBySampledraftId(sampleDraftDb.getSampleDraftId(), metaFromForm);
		waspMessage("sampleDetail.updated_success.label");
		return "redirect:/jobsubmit/samples/"+jobDraftId+".do";
	}
	
	@RequestMapping(value="/samples/addExisting/{jobDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String addExistingSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		SampleDraft newSampleDraft = new SampleDraft();
		
		// TODO: functionality here
		
		return "redirect:/jobsubmit/samples/view/"+jobDraftId+"/"+newSampleDraft.getSampleDraftId()+".do";
	}
	
	/**
	   * See if SampleDraft name has changed between sampleDraft objects and if so check if the new name is unique within the jobDraft.
	   * @param formSample
	   * @param originalSample
	   * @param job
	   * @param result
	   */
	  private void validateSampleDraftNameUnique(String sampleDraftName, Integer sampleDraftId, JobDraft jobDraft, BindingResult result){
		  //confirm that, if a new sample.name was supplied on the form, it is different from all other sample.name in this job
		  List<SampleDraft> sampleDraftsInThisJob = jobDraft.getSampleDraft();
		  for(SampleDraft eachSampleDraftInThisJob : sampleDraftsInThisJob){
			  if(eachSampleDraftInThisJob.getSampleDraftId().intValue() != sampleDraftId.intValue()){
				  if( sampleDraftName.equals(eachSampleDraftInThisJob.getName()) ){
					  // adding an error to 'result object' linked to the 'name' field as the name chosen already exists
					  Errors errors=new BindException(result.getTarget(), "sampleDraft");
					  // reject value on the 'name' field with the message defined in sampleDetail.updated.nameClashError
					  // usage: errors.rejectValue(field, errorString, default errorString)
					  errors.rejectValue("name", "sampleDetail.nameClash.error", "sampleDetail.nameClash.error (no message has been defined for this property)");
					  result.addAllErrors(errors);
					  break;
				  }
			  }
		  }
	  }
	  
	  /**
	   * get adaptorsets and adaptors for populating model. If a selected adaptor is found in the provided SampleDraftMeta
	   * it is used to find appropriate adaptors
	   * @param jobDraft
	   * @param sampleDraftMeta
	   * @param m
	   */
		private void prepareAdaptorsetsAndAdaptors(JobDraft jobDraft, List<SampleDraftMeta> sampleDraftMeta, ModelMap m){
			List<Adaptorset> adaptorsets = new ArrayList<Adaptorset>();
			for (JobDraftresourcecategory jdrc: jobDraft.getJobDraftresourcecategory()){
				Map<String, Integer> adaptorsetRCQuery = new HashMap<String, Integer>();
				adaptorsetRCQuery.put("resourcecategoryId", jdrc.getResourcecategoryId());
				for (AdaptorsetResourceCategory asrc: adaptorsetResourceCategoryDao.findByMap(adaptorsetRCQuery))
					adaptorsets.add(asrc.getAdaptorset());
			}
			m.addAttribute("adaptorsets", adaptorsets); // required for adaptorsets metadata control element (select:${adaptorsets}:adaptorsetId:name)
			
			List<Adaptor> adaptors = new ArrayList<Adaptor>();
			Adaptorset selectedAdaptorset = null;
			try{	
	  			selectedAdaptorset = adaptorsetDao.getAdaptorsetByAdaptorsetId(Integer.valueOf( MetaHelper.getMetaValue("genericLibrary", "adaptorset", sampleDraftMeta)) );
	  		} catch(MetadataException e){
	  			logger.debug("Cannot get metadata genericLibrary.adaptorset. Presumably not be defined: " + e.getMessage());
	  		} catch(NumberFormatException e){
	  			logger.warn("Cannot convert to numeric value for metadata " + e.getMessage());
	  		}
			if (selectedAdaptorset != null){
				adaptors = selectedAdaptorset.getAdaptor();
			} else if (adaptorsets.size() == 1){
				adaptors = adaptorsets.get(0).getAdaptor();
			}
			m.addAttribute("adaptors", adaptors); // required for adaptors metadata control element (select:${adaptors}:adaptorId:barcodenumber)
		}

/*
	
	 * Prepares page to manage sample drafts
	 * 
	 * @Author Sasha Levchuk
	 	
	@RequestMapping(value="/samples/{jobDraftId}", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showSampleDraft(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
	
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		//get list of meta fields that are 'allowed' for the given workflowId 
		int workflowId=jobDraftDao.findById(jobDraftId).getWorkflow().getWorkflowId();
		Map<SampleSubtype,List<SampleDraftMeta>>allowedMetaFields=sampleDraftMetaDao.getAllowableMetaFields(workflowId);
				
		Map<String,SampleDraftMeta> allowedMetaFieldsMap = new LinkedHashMap<String,SampleDraftMeta>();
		
		for(SampleSubtype sampleSubtype: allowedMetaFields.keySet()){
			sampleSubtype.setSampleType(sampleTypeDao.getSampleTypeBySampleTypeId(sampleSubtype.getSampleTypeId()));
			for (SampleDraftMeta subTypeMeta : allowedMetaFields.get(sampleSubtype)){
				if (!allowedMetaFieldsMap.containsKey(subTypeMeta.getK())){
					allowedMetaFieldsMap.put(subTypeMeta.getK(), subTypeMeta);
				}
			}
		}
		Set<SampleDraftMeta> allowedMetaFieldsSet = new LinkedHashSet<SampleDraftMeta>();
		allowedMetaFieldsSet.addAll(allowedMetaFieldsMap.values());
		
		m.addAttribute("_metaList", allowedMetaFieldsSet); // all field metadata for all sybtypes associated with this workflow combined
		m.addAttribute("_metaBySubtypeList", allowedMetaFields); // all sample subtypes associated with this workflow and field metadata
		
		
		Map<Integer,Map<Integer,String>> jobsBySampleSubtype=new LinkedHashMap<Integer,Map<Integer,String>>();
		for(Map.Entry<Integer, List<Job>> e:jobDao.getJobSamplesByWorkflow(workflowId).entrySet()) {
			Map<Integer,String> jm = new LinkedHashMap<Integer,String>();
			jobsBySampleSubtype.put(e.getKey(),jm);
			for(Job j:e.getValue()) {
				jm.put(j.getJobId(), j.getName());
			}		
		}
		m.addAttribute("_jobsBySampleSubtype",jobsBySampleSubtype);
		m.addAttribute(JQFieldTag.AREA_ATTR, "sampleDraft");	
		prepareSelectListData(m);
		m.addAttribute("jobdraftId",jobDraftId);
		m.addAttribute("jobDraft",jobDraft);
		m.addAttribute("uploadStartedMessage",messageService.getMessage("sampleDraft.fileupload_wait.data"));
		m.addAttribute("_metaDataMessages", MetaHelper.getMetadataMessages(request.getSession()));
		m.addAttribute("pageFlowMap", getPageFlowMap(jobDraft));
		m.addAttribute("adaptorsets", adaptorsetDao.findAll()); // required for adaptorsets metadata control element (select:${adaptorsets}:adaptorsetId:name)
		m.addAttribute("adaptors",new ArrayList<Adaptor>()); // required for adaptors metadata control element (select:${adaptors}:adaptorId:barcodenumber)
		
	
		return "jobsubmit/sample";
		

	}
*/

	@RequestMapping(value="/cells/{jobDraftId}.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showSampleCellDraft(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		
		List<SampleDraft> samples=sampleDraftDao.getSampleDraftByJobId(jobDraftId);

		Set<String> selectedSampleCell = new HashSet<String>();
		//Map<Integer, Integer> cellMap = new HashMap<Integer, Integer>();
		//int cellindexCount = 0;

		for (SampleDraft sd: samples) {
 			for (SampleDraftJobDraftCellSelection sdc: sd.getSampleDraftJobDraftCellSelection()) {
/*
				int cellId = sdc.getJobdraftcellId();
				if (! cellMap.containsKey(cellId)) {
					cellindexCount++;
					cellMap.put(cellId, cellindexCount); 
				}
				int cellIndex = cellMap.get(cellId);
*/
				int cellIndex = sdc.getJobDraftCellSelection().getCellIndex();

				String key = sd.getSampleDraftId() + "_" + cellIndex;

				selectedSampleCell.add(key);
			}
		}


		
		getMetaHelperWebapp().setArea(jobDraft.getWorkflow().getIName());

		jobDraft.setJobDraftMeta(getMetaHelperWebapp().getMasterList(JobDraftMeta.class));

		m.put("jobDraft", jobDraft);
		//m.put("area", getMetaHelperWebapp().getArea());
		//m.put("parentarea", getMetaHelperWebapp().getParentArea());

		m.put("sampleDrafts", samples);
		m.put("selectedSampleCell", selectedSampleCell);

        m.put("pageFlowMap", getPageFlowMap(jobDraft));

		return "jobsubmit/cell";
		
	}

	@RequestMapping(value="/cells/{jobDraftId}.do", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String updateSampleCellDraft(
			@PathVariable("jobDraftId") Integer jobDraftId, 
			ModelMap m) {

		//	Removes Old Entries, premature?
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		
		List<JobDraftCellSelection> oldJobDraftCellSelections = jobDraft.getJobDraftCellSelection();

		for (JobDraftCellSelection jdc: oldJobDraftCellSelections) {
			List<SampleDraftJobDraftCellSelection> oldSampleDraftJobDraftCellSelections = jdc.getSampleDraftJobDraftCellSelection();
			for (SampleDraftJobDraftCellSelection sdc: oldSampleDraftJobDraftCellSelections) {
				sampleDraftJobDraftCellSelectionDao.remove(sdc);
				sampleDraftJobDraftCellSelectionDao.flush(sdc);
			}
			jobDraftCellSelectionDao.remove(jdc);
			jobDraftCellSelectionDao.flush(jdc);
		}


		List<SampleDraft> samples=sampleDraftDao.getSampleDraftByJobId(jobDraftId);

		Map params = request.getParameterMap();
		int maxColumns = 10;
		try {
			maxColumns = Integer.parseInt(((String[])params.get("jobcells"))[0]);
		} catch (Exception e) {
		}

		//List<String> checkedList = new ArrayList<String>();

		int cellindex = 0;

		for (int i = 1; i <= maxColumns; i++) {
			int libraryindex = 0;
			boolean cellfound = false;

			JobDraftCellSelection thisJobDraftCellSelection = new JobDraftCellSelection();
			thisJobDraftCellSelection.setJobdraftId(jobDraftId);
			thisJobDraftCellSelection.setCellIndex(cellindex + 1);

			for (SampleDraft sd: samples) {
				String checked = "0";
				try {
					checked = ((String[])params.get("sdc_" + sd.getSampleDraftId() + "_" + i ))[0];
				} catch (Exception e) {
				}

				if (checked == null || checked.equals("0")) {
					continue;
				}

				if (! cellfound) {
					cellfound = true;
					cellindex++;

					JobDraftCellSelection jobDraftCellSelectionDb = jobDraftCellSelectionDao.save(thisJobDraftCellSelection);
					thisJobDraftCellSelection = jobDraftCellSelectionDb;

					jobDraftCellSelectionDao.flush(thisJobDraftCellSelection);
				}

				libraryindex++;

				SampleDraftJobDraftCellSelection sampleDraftJobDraftCellSelection = new SampleDraftJobDraftCellSelection();

				sampleDraftJobDraftCellSelection.setJobDraftCellSelectionId(thisJobDraftCellSelection.getJobDraftCellSelectionId());
				sampleDraftJobDraftCellSelection.setSampledraftId(sd.getSampleDraftId());
				sampleDraftJobDraftCellSelection.setLibraryIndex(libraryindex);

				sampleDraftJobDraftCellSelectionDao.save(sampleDraftJobDraftCellSelection);

				// checkedList.add("sdc_" + sd.getSampleDraftId() + "_" + i + " " + cellindex + " " + libraryindex);

			}
		}

		return nextPage(jobDraft);
	}

	@RequestMapping(value="/verify/{jobDraftId}.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showJobDraft(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		m.put("jobDraft", jobDraft);

		List <SampleDraft> sampleDraftList = jobDraft.getSampleDraft();
		m.put("sampleDraft", sampleDraftList);
        m.put("pageFlowMap", getPageFlowMap(jobDraft));

		return "jobsubmit/verify";
	}

	@RequestMapping(value="/verify/{jobDraftId}.do", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String verifyJob(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";

		// TODO ClassLoader for validateJobDraft 

		// JobDraftValidator jdv = getDefaultJobDraftValidator();
		try {
			List<WorkflowMeta> wfmList = jobDraft.getWorkflow().getWorkflowMeta(); 
			for (WorkflowMeta wfm: wfmList) {
				if (wfm.getK().equals("workflow.validatorClass")) {
					ClassLoader cl = JobSubmissionController.class.getClassLoader();
					// jdv = (JobDraftValidator) cl.loadClass(wfm.getV()).newInstance();
					Object o = cl.loadClass(wfm.getV()).newInstance();


					break;
				}
			}
			// JobDraftValidator 
		} catch (Exception e) {
		}

		// jdv.validate(jobDraft);


		return nextPage(jobDraft);
	}


	@RequestMapping(value="/submit/{jobDraftId}.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String submitJob(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {
		User me = authenticationService.getAuthenticatedUser();

		JobDraft jobDraft = jobDraftDao.getJobDraftByJobDraftId(jobDraftId);
		if (! isJobDraftEditable(jobDraft))
			return "redirect:/dashboard.do";
		
		getMetaHelperWebapp().setArea(jobDraft.getWorkflow().getIName());

		// no sync w/ master
		// jobDraft.setJobDraftMeta(getMetaHelper().syncWithMaster(jobDraft.getJobDraftMeta()));


		// Copies JobDraft to a new Job
		Job job = new Job();
		job.setUserId(me.getUserId());
		job.setLabId(jobDraft.getLabId());
		job.setName(jobDraft.getName());
		job.setWorkflowId(jobDraft.getWorkflowId());
		job.setIsActive(1);
		job.setCreatets(new Date());

		job.setViewablebylab(0); // Todo: get from lab? // really not being used yet

		Job jobDb = jobDao.save(job); 

		// Saves the metadata
		for (JobDraftMeta jdm: jobDraft.getJobDraftMeta()) {
			JobMeta jobMeta = new JobMeta();
			jobMeta.setJobId(jobDb.getJobId());
			jobMeta.setK(jdm.getK());
			jobMeta.setV(jdm.getV());

			jobMetaDao.save(jobMeta); 
		}

		// save the software selected
		for (JobDraftSoftware jdr: jobDraft.getJobDraftSoftware()) {
			JobSoftware jobSoftware = new JobSoftware();
			jobSoftware.setJobId(jobDb.getJobId());
			jobSoftware.setSoftwareId(jdr.getSoftwareId());

			jobSoftwareDao.save(jobSoftware); 
		}

		// save the resource category selected
		for (JobDraftresourcecategory jdr: jobDraft.getJobDraftresourcecategory()) {
			JobResourcecategory jobResourceCategory = new JobResourcecategory();
			jobResourceCategory.setJobId(jobDb.getJobId());
			jobResourceCategory.setResourcecategoryId(jdr.getResourcecategoryId());

			jobResourcecategoryDao.save(jobResourceCategory); 
		}


		// Creates the JobUser Permission
		JobUser jobUser = new JobUser(); 
		jobUser.setUserId(me.getUserId());
		jobUser.setJobId(jobDb.getJobId());
		Role role = roleDao.getRoleByRoleName("js");
		jobUser.setRoleId(role.getRoleId());
		jobUserDao.save(jobUser);
		
		// added 10-20-11 by rob dubin: with job submission, add lab PI as job viewer ("jv")
		//note: could use similar logic in loop to assign jv to all the lab members
		Lab lab = labDao.getLabByLabId(jobDb.getLabId());		
		// if the pi is different from the job user
		if (jobUser.getUserId().intValue() != lab.getPrimaryUserId().intValue()) {
			JobUser jobUser2 = new JobUser();		
			jobUser2.setUserId(lab.getPrimaryUserId());//the lab PI
			jobUser2.setJobId(jobDb.getJobId());
			Role role2 = roleDao.getRoleByRoleName("jv");
			jobUser2.setRoleId(role2.getRoleId());
			jobUserDao.save(jobUser2);
		}

		// Job Cells (oldid, newobj)
		Map<Integer,JobCellSelection> jobDraftCellMap = new HashMap<Integer,JobCellSelection>();

		for (JobDraftCellSelection jdc: jobDraft.getJobDraftCellSelection()) {
			JobCellSelection jobCellSelection = new JobCellSelection();
			jobCellSelection.setJobId(jobDb.getJobId());
			jobCellSelection.setCellIndex(jdc.getCellIndex());

			JobCellSelection jobCellSelectionDb =	jobCellSelectionDao.save(jobCellSelection);	

			jobDraftCellMap.put(jdc.getJobDraftCellSelectionId(), jobCellSelectionDb);
		}

		// Create Samples
		for (SampleDraft sd: jobDraft.getSampleDraft()) {
			// existing sample...
			Sample sampleDb;

			if (sd.getSourceSampleId() != null) {
				sampleDb = sampleDao.getSampleBySampleId(sd.getSourceSampleId());
			} else { 

				Sample sample = new Sample();
				sample.setName(sd.getName()); 
				sample.setSampleTypeId(sd.getSampleTypeId()); 
				sample.setSampleSubtypeId(sd.getSampleSubtypeId()); 
				sample.setSubmitterLabId(jobDb.getLabId()); 
				sample.setSubmitterUserId(me.getUserId()); 
				sample.setSubmitterJobId(jobDb.getJobId()); 
				sample.setIsReceived(0);
				sample.setIsActive(1);
	
				sampleDb = sampleDao.save(sample); 
	
				// sample file
				if (sd.getFileId() != null) {
					SampleFile sampleFile = new SampleFile();
					sampleFile.setSampleId(sampleDb.getSampleId());
					sampleFile.setFileId(sd.getFileId());
	
					sampleFile.setIsActive(1);
	
					// TODO ADD NAME AND INAME
	
					sampleFileDao.save(sampleFile);
				}
	
				// Sample Draft Meta Data
				for (SampleDraftMeta sdm: sd.getSampleDraftMeta()) {
					SampleMeta sampleMeta = new SampleMeta();
	
					sampleMeta.setSampleId(sampleDb.getSampleId());	
					sampleMeta.setK(sdm.getK());	
					sampleMeta.setV(sdm.getV());	
					sampleMeta.setPosition(sdm.getPosition());	
	
					SampleMeta sampleMetaDb = sampleMetaDao.save(sampleMeta); 
				}
			}

			// Job Sample
			JobSample jobSample = new JobSample();
			jobSample.setJobId(jobDb.getJobId());
			jobSample.setSampleId(sampleDb.getSampleId());

			jobSampleDao.save(jobSample);

			for (SampleDraftJobDraftCellSelection sdc: sd.getSampleDraftJobDraftCellSelection()) {
				SampleJobCellSelection sampleJobCellSelection = new SampleJobCellSelection();
				sampleJobCellSelection.setSampleId(sampleDb.getSampleId());
				sampleJobCellSelection.setJobCellSelectionId(jobDraftCellMap.get(sdc.getJobDraftCellSelectionId()).getJobCellSelectionId());
				sampleJobCellSelection.setLibraryIndex(sdc.getLibraryIndex());
				sampleJobCellSelectionDao.save(sampleJobCellSelection);
			}
		}

		// something like this:
		State state = new State(); 

		Task jobCreateTask = taskDao.getTaskByIName("Start Job");
		state.setTaskId(jobCreateTask.getTaskId());
		state.setName(jobCreateTask.getName());
		state.setStartts(new Date());
		state.setStatus("CREATED"); 
		stateDao.save(state);
		
		Statejob statejob = new Statejob();
		statejob.setStateId(state.getStateId());
		statejob.setJobId(job.getJobId());
		statejobDao.save(statejob);

		// update the jobdraft
		jobDraft.setStatus("SUBMITTED");
		jobDraft.setSubmittedjobId(jobDb.getJobId());
		jobDraftDao.save(jobDraft); 

		// Adds new Job to Authorized List
		doReauth();

		return nextPage(jobDraft);
	}


/*	
	 * Returns sample drafts by job draft ID 
	 * 
	 * @Author Sasha Levchuk
	 	
	@RequestMapping(value="/listSampleDraftsJSON", method=RequestMethod.GET)	
	public String getSampleDraftListJSON(@RequestParam("jobdraftId") Integer jobdraftId, HttpServletResponse response) {
	
		//result
		Map <String, Object> jqgrid = new HashMap<String, Object>();
		
		List<SampleDraft> drafts=sampleDraftDao.getSampleDraftByJobId(jobdraftId);
		
		int workflowId=jobDraftDao.findById(jobdraftId).getWorkflow().getWorkflowId();
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			//String users = mapper.writeValueAsString(userList);
			jqgrid.put("page","1");
			jqgrid.put("records",drafts.size()+"");
			jqgrid.put("total",drafts.size()+"");
			
			
			List<Map> rows = new ArrayList<Map>();
			
			Map<Integer, String> allSampleSubTypes=new TreeMap<Integer, String>();
			for(SampleSubtype type:subSampleTypeDao.findAll()) {
				allSampleSubTypes.put(type.getSampleSubtypeId(),type.getName());
			}
			
			Set<SampleDraftMeta> allowedMetaFields=new LinkedHashSet<SampleDraftMeta>();
			
			for(List<SampleDraftMeta> metaList:sampleDraftMetaDao.getAllowableMetaFields(workflowId).values()) {
				allowedMetaFields.addAll(metaList);
			}
			
			for (SampleDraft draft : drafts) {
				Map cell = new HashMap();
				cell.put("id", draft.getSampleDraftId());
			
				MetaHelperWebapp sampleMetaHelperWebapp = new MetaHelperWebapp(SampleDraftMeta.class, request.getSession());
								
				List<SampleDraftMeta> draftMeta=sampleMetaHelperWebapp.syncWithMaster(draft.getSampleDraftMeta(),new ArrayList<SampleDraftMeta>(allowedMetaFields));
				
				String fileCell=getFileCell(draft.getFile());
				
				List<String> cellList=new ArrayList<String>(Arrays.asList(new String[] {
						draft.getName(),
						allSampleSubTypes.get(draft.getSampleSubtypeId()),							
						draft.getStatus(),						
						fileCell,
						"",
						draft.getSourceSampleId()+"",
						draft.getSourceSampleId()==null?"No":"Yes"
				})); 
			
				for(SampleDraftMeta meta:draftMeta) {
					cellList.add(meta.getV());
				}
			
				cell.put("cell", cellList);
			 
				rows.add(cell);
			}

			jqgrid.put("rows",rows);
		
			return outputJSON(jqgrid, response); 	
			
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON "+drafts,e);
		}
	}


	
	 * Returns samples by Job ID 
	 * 
	 * @Author Sasha Levchuk
	 	
	@RequestMapping(value="/samplesByJobId", method=RequestMethod.GET)	
	public String samplesByJobId(@RequestParam("jobId") Integer jobId, HttpServletResponse response) {
	
		//result
		Map <Integer, String> samplesMap = new LinkedHashMap<Integer, String>();
		for(Sample sample:sampleDao.getSamplesByJobId(jobId)) {
			samplesMap.put(sample.getSampleId(), sample.getName());
		}

		try {
			
			return outputJSON(samplesMap, response); 	
			
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON "+samplesMap,e);
		}
	}
	

	

	
	 * The call just checks if sampleDraft has non-null sourceSampleId field
	 * I can't get sourceSampleId value while editing form due to JQGrid specifics
	 * 
	 * @Author Sasha Levchuk
	 	
	@RequestMapping(value = "/getOldSample", method = RequestMethod.GET)
	public String getOldSample(@RequestParam("id") Integer sampleDraftId, HttpServletResponse response) {

		SampleDraft samplDraft = sampleDraftDao.findById(sampleDraftId);

		try {
			
			List<Integer> result = new ArrayList<Integer>();

			if (samplDraft.getSourceSampleId() == null) return outputJSON(result, response);

			result.add(samplDraft.getSourceSampleId());
			
			return outputJSON(result, response);
		} catch (Throwable e) {
			
			throw new IllegalStateException("Can't marshall to JSON ", e);

		}

	}

	
	 * Renders meta info by smaple ID
	 * @Author Sasha Levchuk
	 
	@RequestMapping(value="/sampleMetaBySampleId", method=RequestMethod.GET)	
	public String sampleMetaBySampleId(@RequestParam("sampleId") Integer sampleId, HttpServletResponse response) {
	
		//result
		Map <String, String> metaMap = new LinkedHashMap<String, String>();
		for(SampleMeta meta:sampleMetaDao.getSamplesMetaBySampleId(sampleId)) {
			metaMap.put(meta.getK(), meta.getV());
		}

		try {
			
			return outputJSON(metaMap, response); 	
			
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON "+metaMap,e);
		}
	
	}*/
	
	/*
	 * Returns adaptors by adaptorsetID 
	 * 
	 * @Author asmclellan
	 */	
	@RequestMapping(value="/adaptorsByAdaptorsetId", method=RequestMethod.GET)	
	public String adaptorsByAdaptorId(@RequestParam("adaptorsetId") Integer adaptorsetId, HttpServletResponse response) {
	
		//result
		Map <Integer, String> adaptorsMap = new LinkedHashMap<Integer, String>();
		for(Adaptor adaptor:adaptorsetDao.getAdaptorsetByAdaptorsetId(adaptorsetId).getAdaptor()) {
			adaptorsMap.put(adaptor.getAdaptorId(), adaptor.getName());
		}

		try {
			
			return outputJSON(adaptorsMap, response); 	
			
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON "+adaptorsMap,e);
		}
	}

	/*
	 * Renders URL to download draft file
	 * @Author Sasha Levchuk
	 */
	protected String getFileCell( edu.yu.einstein.wasp.model.File file) {
		if (file==null) return "";
		String fileName=file.getAbsolutePath();
		if (fileName!=null && ( fileName.indexOf('/')>-1 || fileName.indexOf('\\')>-1)) {
			int idx = fileName.lastIndexOf('/');
			if (idx==-1) idx = fileName.lastIndexOf('\\');
			fileName=fileName.substring(idx+1);						
		}
		
		if (fileName!=null && fileName.indexOf('.')>-1) {
			int idx = fileName.lastIndexOf('.');
			fileName=fileName.substring(0,idx);		
		}
		
		return "<a href='/wasp/jobsubmit/downloadFile.do?id="+file.getFileId()+"'>"+fileName+"</a> "+file.getSizek()+"kB";
	}
	
	
	/*
	 * Uploads sample draft file
	 * @Author Sasha Levchuk
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)		
	public String uploadFile(@RequestParam("id") Integer sampleDraftId,SampleDraft sampleDraftForm, ModelMap m, HttpServletResponse response) {
		 	
		String jsCallback="<html>\n"+
"<head>\n"+
"<script type='text/javascript'>\n"+
"function init() {\n"+
	"if(top.uploadDone) top.uploadDone('{message}'); \n"+
"}\n"+
"window.onload=init;\n"+
"</script>\n"+
"<body>\n"+
"</body>\n";
 
			
		try {
			
			if (sampleDraftForm.getFileData()!=null) {//uploading just file				
					
					CommonsMultipartFile fileData=sampleDraftForm.getFileData();
					
					SampleDraft samplDraft=sampleDraftDao.findById(sampleDraftId);
					
					Lab lab=samplDraft.getLab();
					
					User piUser=userDao.getById(lab.getPrimaryUserId());
					
					final String login = 
					((org.springframework.security.core.userdetails.User)
							SecurityContextHolder.getContext().getAuthentication().getPrincipal()
					).getUsername();
				 				 
					String[] path=new String[] {
							this.downloadFolder,
							piUser.getLogin(),
							login,
							"jobdraft_"+sampleDraftForm.getJobdraftId(),
							fileData.getOriginalFilename()+"."+System.currentTimeMillis()
							};
					
					String destStr=StringUtils.join(path, System.getProperty("file.separator"));
					
					java.io.File dest=new java.io.File(destStr);
					
					FileUtils.forceMkdir(dest);
					
					fileData.transferTo(dest);
								 	
					edu.yu.einstein.wasp.model.File file = new edu.yu.einstein.wasp.model.File();
					 
					file.setAbsolutePath(dest.getAbsolutePath());
					file.setIsActive(1);
					file.setContentType("?");
					file.setMd5hash("xxx");
					file.setSizek((int)(fileData.getSize()/1024));
										
					fileDao.persist(file);
					
					samplDraft.setFileId(file.getFileId());
					
					sampleDraftDao.merge(samplDraft);
								
					//submit file for processing by Spring Batch / Spring Intgeration instance 
					RestTemplate template = new RestTemplate();
					try {
						template.postForLocation(jobRunnerHost+"/bee/jobs/launchBeeJob.do?file={file}",String.class, dest.getAbsolutePath());
					} catch (Throwable ee) {
						//ignoring until we have "no downtime" instance to run jobs
					}
					
					jsCallback=jsCallback.replace("{message}", messageService.getMessage("sampleDraft.fileupload_done.data"));
					response.setContentType( "text/html; charset=UTF-8" );
					response.getWriter().print(jsCallback);
				
			} else {
				jsCallback=jsCallback.replace("{message}",messageService.getMessage("sampleDraft.fileupload_nofile.data"));
					response.setContentType( "text/html; charset=UTF-8" );
					response.getWriter().print(jsCallback);				
			}
		} catch(Throwable e) {
			throw new IllegalStateException("cant get file to upload",e);
		}		
		return null;
	}
	
	
	/*
	 * Updates sample draft record
	 * @Author Sasha Levchuk
	 */
	@RequestMapping(value = "/updateSampleDraft", method = RequestMethod.POST)
	public String updateSampleDraft(@RequestParam("id") Integer sampleDraftId,
			SampleDraft sampleDraftForm, ModelMap m,
			HttpServletResponse response) {

		
		boolean adding = sampleDraftId == 0;

		// get from jobdraft table
		JobDraft jd = jobDraftDao.findById(sampleDraftForm.getJobdraftId());
		sampleDraftForm.setUserId(jd.getUserId());
		sampleDraftForm.setLabId(jd.getLabId());
		
		SampleSubtype subtype=subSampleTypeDao.findById(sampleDraftForm.getSampleSubtypeId());
		
		if (adding) {
						
			int sampleTypeId=subtype.getSampleType().getSampleTypeId();
			sampleDraftForm.setSampleTypeId(sampleTypeId);
			
			SampleDraft sampleDraftDb = this.sampleDraftDao
					.save(sampleDraftForm);
			
			sampleDraftId = sampleDraftDb.getSampleDraftId();
		} else {

			SampleDraft sampleDraftDb = this.sampleDraftDao
					.getById(sampleDraftId);

			sampleDraftDb.setName(sampleDraftForm.getName());
			sampleDraftDb.setStatus(sampleDraftForm.getStatus());
			sampleDraftDb.setSampleSubtypeId(sampleDraftForm.getSampleSubtypeId());
			sampleDraftDb.setSourceSampleId(sampleDraftForm.getSourceSampleId());

			this.sampleDraftDao.merge(sampleDraftDb);
		}
		
		List<SampleDraftMeta> sampleDraftMetaList = new ArrayList<SampleDraftMeta>();
		
		for (String area: subtype.getComponentMetaAreas()){
			MetaHelperWebapp sampleMetaHelperWebapp = new MetaHelperWebapp(area,SampleDraftMeta.class, request.getSession());
			sampleDraftMetaList.addAll(sampleMetaHelperWebapp.getFromJsonForm(request, SampleDraftMeta.class));
		}
		sampleDraftForm.setSampleDraftMeta(sampleDraftMetaList);
		
		for (Iterator<SampleDraftMeta> it=sampleDraftMetaList.iterator();it.hasNext();) {
			SampleDraftMeta meta = it.next();
			if (StringUtils.isEmpty(meta.getV())) it.remove();//remove blank entries
			else meta.setSampledraftId(sampleDraftId);
		}

		sampleDraftMetaDao.updateBySampledraftId(sampleDraftId,
				sampleDraftMetaList);

		try {
			response.getWriter()
					.println(
							sampleDraftId
									+ "|"
									+ (adding ? messageService.getMessage("sampleDraft.created.data")
											: messageService.getMessage("sampleDraft.updated.data")));
			return null;

		} catch (Throwable e) {
			throw new IllegalStateException("Cant output success message ", e);
		}
	}
	
	
	/*
	 * Deletes sample draft record
	 * @Author Sasha Levchuk
	 */
	@RequestMapping(value = "/deleteSampleDraftJSON", method = RequestMethod.DELETE)	
	public String deleteSampleDraftJSON(@RequestParam("id") Integer sampleDraftId,HttpServletResponse response) {

		sampleDraftMetaDao.updateBySampledraftId(sampleDraftId, new ArrayList<SampleDraftMeta>());
		this.sampleDraftDao.remove(sampleDraftDao.findById(sampleDraftId));
		
		try {
			response.getWriter().println(messageService.getMessage("sampleDraft.removed.data"));
			return null;
		} catch (Throwable e) {
			throw new IllegalStateException("Cant output success message ",e);
		}
	}
	
	/*
	 * Downloads sample draft file
	 * 
	 * @Author Sasha Levchuk
	 */
	@RequestMapping(value = "/downloadFile.do", method = RequestMethod.GET)	
	public String downloadSampleDraftFile(@RequestParam("id") Integer fileId,HttpServletResponse response) throws IOException {
		
		int FILEBUFFERSIZE=1000000;//megabyte
		
		File file=fileDao.findById(fileId);
		
		if (file==null) {
				String html="<html>\n"+
				"<head>\n"+				
				"</script>\n"+
				"<body>\n"+
				"<script>alert('Error: file id "+fileId+" not foud');</script>\n"+
				"</body>\n";
				response.setContentType( "text/html; charset=UTF-8" );
				try {
					response.getWriter().print(html);
				} catch (Throwable e) {
					throw new IllegalStateException("Cant output error message ",e);
				}
				return null;
		}
		ServletOutputStream out = null;
		InputStream in = null;
		try {
			out = response.getOutputStream();
			
			java.io.File diskFile=new java.io.File(file.getAbsolutePath());
			in = new FileInputStream(diskFile);
			
			String mimeType = "application/octet-stream";
			byte[] bytes = new byte[FILEBUFFERSIZE];
			int bytesRead;

			response.setContentType(mimeType);
			
			response.setContentLength( (int)diskFile.length() );
			
			String fileName=diskFile.getName();
				
			if (fileName!=null && fileName.indexOf('.')>-1) {
				int idx = fileName.lastIndexOf('.');
				fileName=fileName.substring(0,idx);		
			}
			
			response.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );

			while ((bytesRead = in.read(bytes)) != -1) {
				out.write(bytes, 0, bytesRead);
			}
			
		} catch (Throwable e) {
			throw new IllegalStateException("Cant download file id "+fileId,e);
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
		return null;
	}
	
	@Override
	protected void prepareSelectListData(ModelMap m) {
		super.prepareSelectListData(m);
		m.addAttribute("sampleSubtypes",subSampleTypeDao.findAll());
		Map<String, String> statuses=new TreeMap<String, String>();
		for(SampleDraft.Status status:SampleDraft.Status.values()) {
			statuses.put(status.name(), status.name());
		}
		m.addAttribute("statuses",statuses);
		
	}

	/**
	 * getPageFlowMap
	 * @param jobDraft - jobdraft (used to get workflow)
	 *
	 * requires request to stop user from going on future screens
	 *
	 * sets request attribute "forcePageTitle" to current page title
	 * returns the pageflow map for nav bar
	 *
	 */

	protected List getPageFlowMap(JobDraft jobDraft) {
		String pageFlow = this.defaultPageFlow;

		try{
			pageFlow = MetaHelper.getMetaValue("workflow", "submitpageflow", jobDraft.getWorkflow().getWorkflowMeta());
		} catch(MetadataException e){
			logger.debug("No page flow defined (workflowMeta workflow.submitpageflow) so using default page flow");
		}
		

		String context = request.getContextPath();
		String uri = request.getRequestURI();
	
		// strips context, lead slash ("/"), spring mapping
		String currentMapping = uri.replaceFirst(context, "").replaceFirst("\\.do.*$", "");


		String pageFlowArray[] = pageFlow.split(";");

		List<String[]> rt = new ArrayList<String[]>(); 

		int found = -1;
		for (int i=0; i < pageFlowArray.length -1; i++) {
			String page = pageFlowArray[i];
			String mapPage = page.replaceAll("^/", "");
			mapPage = mapPage.replaceAll("/\\{n\\}", "");


			String expandPage = page.replaceAll("\\{n\\}", ""+jobDraft.getJobDraftId());
			if (currentMapping.equals(expandPage)) {
				request.setAttribute("forcePageTitle", getPageTitle(mapPage, jobDraft.getWorkflow().getIName()));
				break;

			}

			String[] r = {expandPage, getPageTitle(mapPage, jobDraft.getWorkflow().getIName())};
			rt.add(r);
	
		}

		return rt; 
	}

	/**
	 * getPageTitle gets page title for jobsubmission page corresponding to workflow
	 * 
	 * @param pageDef 
	 * @parm workflowIname
	 *
	 * getPageTitle expect [workflowIName].[pageDef].label
	 * where page is in w/o leading slash or jobDraftId
	 *
	 */
	
	private String getPageTitle(String pageDef, String workflowIName) {
		Locale locale=(Locale)request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		
		String code=workflowIName+"."+pageDef+".label";
			
		try {	
		
		String pageTitle=DBResourceBundle.MESSAGE_SOURCE.getMessage(code, null, locale);
		
		if (pageTitle!=null) {		
			return pageTitle;
		}
		
		} catch (Throwable e) {
			//log.error("Cant get page title from uifield "+tilesDef+"|"+workflowIName+". Falling back to default page name ",e);
		}
		
		return pageDef;
	}

/*
	private void setPageTitle(String tilesDef, String workflowIName) {
		
		Locale locale=(Locale)request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		
		String code=workflowIName+"."+tilesDef+".label";
			
		String pageTitle=DBResourceBundle.MESSAGE_SOURCE.getMessage(code, null, locale);
		
		if (pageTitle!=null) {		
			request.setAttribute("forcePageTitle", pageTitle);		
		}
	}
*/
}
