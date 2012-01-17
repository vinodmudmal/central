package edu.yu.einstein.wasp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import edu.yu.einstein.wasp.controller.validator.MetaHelper;
import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.model.AcctGrant;
import edu.yu.einstein.wasp.model.Department;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.Lab;
import edu.yu.einstein.wasp.model.LabMeta;
import edu.yu.einstein.wasp.model.LabPending;
import edu.yu.einstein.wasp.model.LabPendingMeta;
import edu.yu.einstein.wasp.model.LabUser;
import edu.yu.einstein.wasp.model.MetaBase;
import edu.yu.einstein.wasp.model.Project;
import edu.yu.einstein.wasp.model.Role;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleLab;
import edu.yu.einstein.wasp.model.User;
import edu.yu.einstein.wasp.model.UserMeta;
import edu.yu.einstein.wasp.model.UserPending;
import edu.yu.einstein.wasp.model.UserPendingMeta;
import edu.yu.einstein.wasp.service.AuthenticationService;
import edu.yu.einstein.wasp.service.DepartmentService;
import edu.yu.einstein.wasp.service.EmailService;
import edu.yu.einstein.wasp.service.LabMetaService;
import edu.yu.einstein.wasp.service.LabPendingMetaService;
import edu.yu.einstein.wasp.service.LabPendingService;
import edu.yu.einstein.wasp.service.LabService;
import edu.yu.einstein.wasp.service.LabUserService;
import edu.yu.einstein.wasp.service.MessageService;
import edu.yu.einstein.wasp.service.PasswordService;
import edu.yu.einstein.wasp.service.RoleService;
import edu.yu.einstein.wasp.service.UserMetaService;
import edu.yu.einstein.wasp.service.UserPendingMetaService;
import edu.yu.einstein.wasp.service.UserPendingService;
import edu.yu.einstein.wasp.taglib.JQFieldTag;

@Controller
@Transactional
@RequestMapping("/lab")
public class LabController extends WaspController {

	@Autowired
	private LabService labService;

	@Autowired
	private LabUserService labUserService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private LabMetaService labMetaService;

	@Autowired
	private DepartmentService deptService;

	@Autowired
	private UserMetaService userMetaService;

	@Autowired
	private UserPendingService userPendingService;

	@Autowired
	private UserPendingMetaService userPendingMetaService;

	@Autowired
	private LabPendingService labPendingService;

	@Autowired
	private LabPendingMetaService labPendingMetaService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MessageService messageService; 
	  
	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private PasswordService passwordService;
	

	/**
	 * get a @{link MetaHelper} instance for working with LabMeta metadata
	 * 
	 * @return
	 */
	private final MetaHelper getMetaHelper() {
		return new MetaHelper("lab", LabMeta.class, request.getSession());
	}

	/**
	 * get a @{link MetaHelper} instance for working with labPending metadata
	 * 
	 * @return
	 */
	private final MetaHelper getLabPendingMetaHelper() {
		return new MetaHelper("labPending", LabPendingMeta.class,request.getSession());
	}

	/**
	 * Return list of labs in JGrid
	 * 
	 * @param m
	 * @return
	 */
	@RequestMapping("/list")
	@PreAuthorize("hasRole('god')")
	public String list(ModelMap m) {

		m.addAttribute("_metaList",	getMetaHelper().getMasterList(MetaBase.class));
		m.addAttribute(JQFieldTag.AREA_ATTR, getMetaHelper().getArea());

		prepareSelectListData(m);

		return "lab/list";
	}

	/**
	 * Returns list of labs
	 * 
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/listJSON", method = RequestMethod.GET)
	public String getListJSON(HttpServletResponse response) {

		// result
		Map<String, Object> jqgrid = new HashMap<String, Object>();

		List<Lab> labList;

		if (request.getParameter("_search") == null	|| StringUtils.isEmpty(request.getParameter("searchString"))) {
			labList = this.labService.findAll();
		} else {

			Map<String, String> m = new HashMap<String, String>();

			m.put(request.getParameter("searchField"), request.getParameter("searchString"));

			labList = this.labService.findByMap(m);

			if ("ne".equals(request.getParameter("searchOper"))) {
				List<Lab> allLabs = new ArrayList<Lab>(this.labService.findAll());
				for (Iterator<Lab> it = labList.iterator(); it.hasNext();) {
					Lab excludeLab = it.next();
					allLabs.remove(excludeLab);
				}
				labList = allLabs;
			}
		}

		ObjectMapper mapper = new ObjectMapper();

		try {
			Map<Integer, String> allDepts = new TreeMap<Integer, String>();
			for (Department dept : (List<Department>) deptService.findAll()) {
				allDepts.put(dept.getDepartmentId(), dept.getName());
			}

			Map<Integer, String> allUsers = new TreeMap<Integer, String>();
			for (User user : (List<User>) userService.findAll()) {
				allUsers.put(user.getUserId(),	user.getFirstName() + " " + user.getLastName());
			}

			// String labs = mapper.writeValueAsString(labList);
			int pageIndex = Integer.parseInt(request.getParameter("page"));		// index of page
			int pageRowNum = Integer.parseInt(request.getParameter("rows"));	// number of rows in one page
			int rowNum = labList.size();										// total number of rows
			int pageNum = (rowNum + pageRowNum - 1) / pageRowNum;				// total number of pages
			
			jqgrid.put("records", rowNum + "");
			jqgrid.put("total", pageNum + "");
			jqgrid.put("page", pageIndex + "");
			
			Map<String, String> userData = new HashMap<String, String>();
			userData.put("page", pageIndex + "");
			userData.put("selId", StringUtils.isEmpty(request.getParameter("selId")) ? "" : request.getParameter("selId"));
			jqgrid.put("userdata", userData);

			List<Map> rows = new ArrayList<Map>();

			int frId = pageRowNum * (pageIndex - 1);
			int toId = pageRowNum * pageIndex;
			toId = toId <= rowNum ? toId : rowNum;

			/* if the selId is set, change the page index to the one contains the selId */
			if(!StringUtils.isEmpty(request.getParameter("selId")))
			{
				int selLabId = Integer.parseInt(request.getParameter("selId"));
				int selLabIndex = labList.indexOf(labService.findById(selLabId));
				frId = selLabIndex;
				toId = frId + 1;

				jqgrid.put("records", "1");
				jqgrid.put("total", "1");
				jqgrid.put("page", "1");
			}				
			
			List<Lab> labPage = labList.subList(frId, toId);
			for (Lab lab : labPage) {
				Map cell = new HashMap();
				cell.put("id", lab.getLabId());

				List<LabMeta> labMeta = getMetaHelper().syncWithMaster(
						lab.getLabMeta());

				List<String> cellList = new ArrayList<String>(
						Arrays.asList(new String[] {
								lab.getName(),
								"<a href=/wasp/user/list.do?selId="
										+ lab.getPrimaryUserId() + ">"
										+ allUsers.get(lab.getPrimaryUserId())
										+ "</a>",
								allDepts.get(lab.getDepartmentId()),

								lab.getIsActive().intValue() == 1 ? "yes" : "no" }));

				for (LabMeta meta : labMeta) {
					cellList.add(meta.getV());
				}

				int l = cellList.size();
				cell.put("cell", cellList);

				rows.add(cell);
			}

			jqgrid.put("rows", rows);

			return outputJSON(jqgrid, response);

		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON " + labList, e);
		}

	}

	@RequestMapping(value = "/subgridJSON.do", method = RequestMethod.GET)
	public String subgridJSON(@RequestParam("id") Integer labId, ModelMap m, HttpServletResponse response) {

		Map<String, Object> jqgrid = new HashMap<String, Object>();

		Lab labDb = this.labService.getById(labId);

		List<LabUser> users = labDb.getLabUser();

		List<Project> projects = labDb.getProject();

		List<Sample> samples = labDb.getSample();

		List<AcctGrant> accGrants = labDb.getAcctGrant();

		List<SampleLab> sampleLabs = labDb.getSampleLab();

		List<Job> jobs = labDb.getJob();

		// get max lenth of the previous 4 lists
		int max = Math.max(Math.max(users.size(), projects.size()),	Math.max(samples.size(), accGrants.size()));
		max = Math.max(max, Math.max(sampleLabs.size(), jobs.size()));

		if (max == 0) {
			LabUser lUser = new LabUser();
			lUser.setUser(new User());
			users.add(lUser);

			projects.add(new Project());

			samples.add(new Sample());

			accGrants.add(new AcctGrant());

			SampleLab sampleLab = new SampleLab();
			sampleLab.setLab(new Lab());
			sampleLabs.add(sampleLab);

			jobs.add(new Job());

			max = 1;
		}

		String[][] mtrx = new String[max][6];

		ObjectMapper mapper = new ObjectMapper();

		String text;
		try {
			// String labs = mapper.writeValueAsString(labList);
			jqgrid.put("page", "1");
			jqgrid.put("records", max + "");
			jqgrid.put("total", max + "");

			int i = 0;
			int j = 0;
			for (LabUser user : users) {

				text = user.getUserId() == null ? "No Users"
						: "<a href=/wasp/user/list.do?selId="
								+ user.getUserId() + ">"
								+ user.getUser().getFirstName() + " "
								+ user.getUser().getLastName() + "</a>";
				mtrx[j][i] = text;
				j++;
			}
			i++;
			j = 0;
			for (Project project : projects) {
				text = project.getProjectId() == null ? "No Projects" : project.getName();
				mtrx[j][i] = text;
				j++;
			}
			i++;
			j = 0;
			for (Sample sample : samples) {
				text = sample.getSampleId() == null ? "No Samples" : sample.getName();
				mtrx[j][i] = text;
				j++;
			}
			i++;
			j = 0;
			for (AcctGrant acc : accGrants) {
				text = acc.getGrantId() == null ? "No Acc Grants" : acc.getName();
				mtrx[j][i] = text;
				j++;
			}
			i++;
			j = 0;
			for (SampleLab sampleLab : sampleLabs) {
				text = sampleLab.getLab().getLabId() == null ? "No Sample Labs" : sampleLab.getLab().getName();
				mtrx[j][i] = text;
				j++;
			}
			i++;
			j = 0;
			for (Job job : jobs) {
				text = job.getJobId() == null ? "No Jobs" : job.getName();
				mtrx[j][i] = text;
				j++;
			}

			List<Map> rows = new ArrayList<Map>();

			for (j = 0; j < max; j++) {
				Map cell = new HashMap();
				rows.add(cell);
				cell.put("id", j + "");
				List<String> cellList = Arrays.asList(mtrx[j]);
				cell.put("cell", cellList);
			}

			jqgrid.put("rows", rows);

			return outputJSON(jqgrid, response);

		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON " + labDb,e);
		}
	}

	@RequestMapping(value = "/detail_rw/updateJSON.do", method = RequestMethod.POST)
	public String updateDetailJSON(@RequestParam("id") Integer labId,
			Lab labForm, ModelMap m, HttpServletResponse response) {

		List<LabMeta> labMetaList = getMetaHelper().getFromJsonForm(request, LabMeta.class);

		labForm.setLabMeta(labMetaList);
		
		if (labId == null || labId == 0) {

			labForm.setLastUpdTs(new Date());
			labForm.setIsActive(1);

			Lab labDb = this.labService.save(labForm);

			labId = labDb.getLabId();
		} else {
			Lab labDb = this.labService.getById(labId);
			labDb.setName(labForm.getName());
			labDb.setIsActive(labForm.getIsActive());
			labDb.setDepartmentId(labForm.getDepartmentId());
			labDb.setPrimaryUserId(labForm.getPrimaryUserId());
			this.labService.merge(labDb);
		}

		labMetaService.updateByLabId(labId, labMetaList);

		// MimeMessageHelper a;

		try {
			response.getWriter().println(messageService.getMessage("lab.updated_success.label"));
			return null;
		} catch (Throwable e) {
			throw new IllegalStateException("Cant output success message ", e);
		}
	}

	@RequestMapping(value = "/pending/detail_rw/updateJSON.do", method = RequestMethod.POST)
	public String updatePendingDetailJSON(
			@RequestParam("id") Integer labPendingId, LabPending labPendingForm, ModelMap m, HttpServletResponse response) {

		List<LabPendingMeta> labPendingMetaList = getLabPendingMetaHelper().getFromJsonForm(request, LabPendingMeta.class);

		labPendingForm.setLabPendingMeta(labPendingMetaList);
		if (labPendingId == null || labPendingId == 0) {

			labPendingForm.setLastUpdTs(new Date());
			// labPendingForm.setIsActive(1);

			LabPending labPendingDb = this.labPendingService.save(labPendingForm);

			labPendingId = labPendingDb.getLabPendingId();
		} else {
			LabPending labPendingDb = this.labPendingService.getById(labPendingId);
			labPendingDb.setName(labPendingForm.getName());
			// labPendingDb.setIsActive(labPendingForm.getIsActive());
			labPendingDb.setDepartmentId(labPendingForm.getDepartmentId());
			labPendingDb.setPrimaryUserId(labPendingForm.getPrimaryUserId());

			this.labPendingService.merge(labPendingDb);
		}

		labPendingMetaService.updateByLabpendingId(labPendingId, labPendingMetaList);

		// MimeMessageHelper a;

		try {
			response.getWriter().println(messageService.getMessage("labPending.updated_success.label"));
			return null;
		} catch (Throwable e) {
			throw new IllegalStateException("Cant output success message ", e);
		}
	}

	@RequestMapping(value = "/detail_rw/{deptId}/{labId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('sa') or hasRole('ga') or hasRole('da-' + #deptId) or hasRole('lu-' + #labId)")
	public String detailRW(@PathVariable("deptId") Integer deptId, @PathVariable("labId") Integer labId, ModelMap m) {
		return detail(labId, m, true);
	}

	@RequestMapping(value = "/detail_ro/{deptId}/{labId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('sa') or hasRole('ga') or hasRole('da-' + #deptId) or hasRole('lu-' + #labId)")
	public String detailRO(@PathVariable("deptId") Integer deptId, @PathVariable("labId") Integer labId, ModelMap m) {
		return detail(labId, m, false);
	}

	private String detail(Integer labId, ModelMap m, boolean isRW) {

		Lab lab = this.labService.getById(labId);

		lab.setLabMeta(getMetaHelper().syncWithMaster(lab.getLabMeta()));

		List<LabUser> labUserList = lab.getLabUser();
		labUserList.size();

		List<Job> jobList = lab.getJob();
		jobList.size();

		m.addAttribute("lab", lab);

		prepareSelectListData(m);
		if (isRW) {
			return "lab/detail_rw";
		} else {
			m.addAttribute("puserFullName", getPiFullNameFromLabId(labId));
			return "lab/detail_ro";
		}
	}

	@RequestMapping(value = "/pending/detail_rw/{deptId}/{labPendingId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('sa') or hasRole('ga') or hasRole('da-' + #deptId)")
	public String pendingDetailRW(@PathVariable("deptId") Integer deptId,
			@PathVariable("labPendingId") Integer labPendingId, ModelMap m) {
		return pendingDetail(labPendingId, m, true);
	}

	@RequestMapping(value = "/pending/detail_ro/{deptId}/{labPendingId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('sa') or hasRole('ga') or hasRole('da-' + #deptId)")
	public String pendingDetailRO(@PathVariable("deptId") Integer deptId,
			@PathVariable("labPendingId") Integer labPendingId, ModelMap m) {
		LabPending labPending = this.labPendingService.getLabPendingByLabPendingId(labPendingId);
		if (labPending.getLabPendingId() == null || labPending.getLabPendingId() == 0) {// labpendingId doesn't exist
			waspMessage("labPending.labpendingid_notexist.error");
			return "redirect:/dashboard.do";
		}
		if (deptId.intValue() != labPending.getDepartmentId().intValue()) {
			waspMessage("labPending.departmentid_mismatch.error");
			return "redirect:/dashboard.do";
		} else if (!labPending.getStatus().equalsIgnoreCase("PENDING")) {
			waspMessage("labPending.status_mismatch.error");
			return "redirect:/dashboard.do";
		} else {
			return pendingDetail(labPendingId, m, false);
		}
	}

	private String getPiFullNameFromLabPendingId(int labPendingId) {
		LabPending labPending = this.labPendingService.getById(labPendingId);
		String puserFullName = "";
		if (labPending.getUserpendingId() != null) {
			// this PI is currently a pending user.
			UserPending userPending = userPendingService.getUserPendingByUserPendingId(labPending.getUserpendingId());
			puserFullName = userPending.getFirstName() + " " + userPending.getLastName();
		} else if (labPending.getPrimaryUserId() != null){
			// the referenced PI of this lab exists in the user table already
			User user = userService.getUserByUserId(labPending.getPrimaryUserId());
			puserFullName = user.getFirstName() + " " + user.getLastName();
		} else {
			// shouldn't get here
		}
		return puserFullName;
	}

	private String getPiFullNameFromLabId(int labId) {
		Lab lab = this.labService.getById(labId);
		String puserFullName = "";
		User user = userService.getUserByUserId(lab.getPrimaryUserId());
		puserFullName = user.getFirstName() + " " + user.getLastName();
		return puserFullName;
	}

	private String pendingDetail(Integer labPendingId, ModelMap m, boolean isRW) {

		LabPending labPending = this.labPendingService.getById(labPendingId);

		labPending.setLabPendingMeta(getLabPendingMetaHelper().syncWithMaster(
				labPending.getLabPendingMeta()));

		// List<LabUser> labUserList = labPending.getLabUser();
		// labUserList.size();

		// List<Job> jobList = labPending.getJob();
		// jobList.size();
		m.addAttribute("puserFullName",	getPiFullNameFromLabPendingId(labPendingId));
		m.addAttribute("labPending", labPending);

		prepareSelectListData(m);

		return isRW ? "lab/pending/detail_rw" : "lab/pending/detail_ro";
	}

	@RequestMapping(value = "/create/form.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god')")
	public String showEmptyForm(ModelMap m) {

		Lab lab = new Lab();
		lab.setLabMeta(getMetaHelper().getMasterList(LabMeta.class));

		m.addAttribute("lab", lab);

		prepareSelectListData(m);

		return "lab/detail_rw";
	}

	@RequestMapping(value = "/create/form.do", method = RequestMethod.POST)
	@PreAuthorize("hasRole('god')")
	public String create(@Valid Lab labForm, BindingResult result,
			SessionStatus status, ModelMap m) {

		// read properties from form

		List<LabMeta> labMetaList = getMetaHelper().getFromRequest(request,	LabMeta.class);
		getMetaHelper().validate(labMetaList, result);

		labForm.setLabMeta(labMetaList);

		if (result.hasErrors()) {
			waspMessage("lab.created.error");
			return "lab/detail_rw";
		}

		labForm.setLastUpdTs(new Date());

		Lab labDb = this.labService.save(labForm);
		labMetaService.updateByLabId(labDb.getLabId(), labMetaList);

		status.setComplete();

		waspMessage("lab.created_success.label");

		return "redirect:/lab/detail_rw/" + labDb.getLabId() + ".do";
	}

	@RequestMapping(value = "/detail_rw/{deptId}/{labId}.do", method = RequestMethod.POST)
	@PreAuthorize("hasRole('god') or hasRole('da-' + #deptId) or hasRole('lm-' + #labId)")
	public String updateDetail(@PathVariable("deptId") Integer deptId,
			@PathVariable("labId") Integer labId, @Valid Lab labForm,
			BindingResult result, SessionStatus status, ModelMap m) {
		
		// return read only version of page if cancel button pressed
		String submitValue = request.getParameter("submit");
		if ( submitValue.equals(messageService.getMessage("labDetail.cancel.label")) ){
			return "redirect:/lab/detail_ro/" + deptId + "/" + labId + ".do";
		}
		
		List<LabMeta> labMetaList = getMetaHelper().getFromRequest(request,	LabMeta.class);

		for (LabMeta meta : labMetaList) {
			meta.setLabId(labId);
		}

		labForm.setLabMeta(labMetaList);

		getMetaHelper().validate(labMetaList, result);

		if (result.hasErrors()) {
			prepareSelectListData(m);
			waspMessage("lab.updated.error");
			return "lab/detail_rw";
		}

		Lab labDb = this.labService.getById(labId);
		labDb.setName(labForm.getName());
		labDb.setDepartmentId(labForm.getDepartmentId());
		labDb.setPrimaryUserId(labForm.getPrimaryUserId());

		labDb.setLastUpdTs(new Date());

		this.labService.merge(labDb);

		labMetaService.updateByLabId(labId, labMetaList);

		status.setComplete();

		waspMessage("lab.updated_success.label");

		// return "redirect:" + labId + ".do";
		return "redirect:/lab/detail_ro/"
				+ Integer.toString(labDb.getDepartmentId()) + "/" + labId
				+ ".do";
	}

	@RequestMapping(value = "/pending/detail_rw/{deptId}/{labPendingId}.do", method = RequestMethod.POST)
	@PreAuthorize("hasRole('god') or hasRole('da-' + #deptId) or hasRole('lm-' + #labPendingId)")
	public String updatePendingDetail(@PathVariable("deptId") Integer deptId,
			@PathVariable("labPendingId") Integer labPendingId,
			@Valid LabPending labPendingForm, BindingResult result,
			SessionStatus status, ModelMap m) {
		
		// return read only version of page if cancel button pressed
		String submitValue = request.getParameter("submit");
		if ( submitValue.equals(messageService.getMessage("labPending.cancel.label")) ){
			return "redirect:/lab/pending/detail_ro/" + deptId + "/" + labPendingId + ".do";
		}
		
		List<LabPendingMeta> labPendingMetaList = getLabPendingMetaHelper().getFromRequest(request, LabPendingMeta.class);

		for (LabPendingMeta meta : labPendingMetaList) {
			meta.setLabpendingId(labPendingId);
		}

		labPendingForm.setLabPendingMeta(labPendingMetaList);

		getLabPendingMetaHelper().validate(labPendingMetaList, result);

		if (result.hasErrors()) {
			waspMessage("labPending.updated.error");
			prepareSelectListData(m);
			m.addAttribute("puserFullName",	getPiFullNameFromLabPendingId(labPendingId));
			return "lab/pending/detail_rw";
		}

		LabPending labPendingDb = this.labPendingService.getById(labPendingId);
		labPendingDb.setName(labPendingForm.getName());
		labPendingDb.setDepartmentId(labPendingForm.getDepartmentId());
		labPendingDb.setPrimaryUserId(labPendingForm.getPrimaryUserId());

		labPendingDb.setLastUpdTs(new Date());

		this.labPendingService.merge(labPendingDb);

		labPendingMetaService.updateByLabpendingId(labPendingId, labPendingMetaList);

		status.setComplete();

		waspMessage("labPending.updated_success.label");

		// return "redirect:" + labId + ".do";
		return "redirect:/lab/pending/detail_ro/"
				+ Integer.toString(labPendingDb.getDepartmentId()) + "/"
				+ labPendingId + ".do";
	}

	@RequestMapping(value = "/user_manager/{labId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lu-' + #labId)")
	public String userManager(@PathVariable("labId") Integer labId, ModelMap m) {
		Lab lab = this.labService.getById(labId);
		List<LabUser> labUsers = new ArrayList();
		for (LabUser lu: lab.getLabUser()){
			if (!lu.getRole().getRoleName().equals("lp")){
				labUsers.add(lu);
			}
		}

		m.addAttribute("labuser", labUsers);
		// add pending users applying to lab
		pendingUserList(labId, m);

		return "lab/user_manager";
	}
	
	@RequestMapping(value = "/user_list/{labId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lu-' + #labId)")
	public String userList(@PathVariable("labId") Integer labId, ModelMap m) {
		Lab lab = this.labService.getById(labId);
		List<User> labManagers = new ArrayList();
		List<User> labMembers= new ArrayList();
		User pi = new User();
		for(LabUser labUser : lab.getLabUser() ){
			User currentUser = userService.getUserByUserId(labUser.getUserId());
			if (currentUser.getUserId().intValue() == lab.getPrimaryUserId().intValue()){
				pi = currentUser;
			} else if (labUser.getRole().getRoleName().equals("lm")){
				labManagers.add(currentUser);
			} else {
				labMembers.add(currentUser);
			}
		}
		m.addAttribute("lab", lab);
		m.addAttribute("pi", pi);
		m.addAttribute("labManagers", labManagers);
		m.addAttribute("labMembers", labMembers);
		return "lab/user_list";
	}

	@RequestMapping(value = "/pendinguser/list/{labId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lu-' + #labId)")
	public String pendingUserList(@PathVariable("labId") Integer labId,	ModelMap m) {
		Lab lab = this.labService.getById(labId);

		Map userPendingQueryMap = new HashMap();
		userPendingQueryMap.put("labId", labId);
		userPendingQueryMap.put("status", "PENDING");
		List<UserPending> userPending = userPendingService.findByMap(userPendingQueryMap);
		
		Map labUserPendingQueryMap = new HashMap();
		labUserPendingQueryMap.put("labId", labId);
		labUserPendingQueryMap.put("roleId", roleService.getRoleByRoleName("lp").getRoleId());
		List<LabUser> labUserPending = labUserService.findByMap(labUserPendingQueryMap);

		m.addAttribute("lab", lab);
		m.addAttribute("userpending", userPending);
		m.addAttribute("labuserpending", labUserPending);

		return "lab/pendinguser/list";
	}

	@RequestMapping(value = "/user/role/{labId}/{userId}/{roleName}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lm-' + #labId)")
	public String userDetail(@PathVariable("labId") Integer labId,
			@PathVariable("userId") Integer userId,
			@PathVariable("roleName") String roleName, ModelMap m) {

		// TODO CHECK VALID LABUSER
		LabUser labUser = labUserService.getLabUserByLabIdUserId(labId, userId);

		if (roleName.equals("xx")) {
			// TODO CONFIRM ROLE WAS "LP"
			labUserService.remove(labUser);

			waspMessage("hello.error");
			return "redirect:/lab/user_manager/" + labId + ".do";
		}

		// TODO CHECK VALID ROLE NAME
		Role role = roleService.getRoleByRoleName(roleName);

		// TODO CHECK VALID ROLE FLOW

		labUser.setRoleId(role.getRoleId());
		labUserService.merge(labUser);

		// TODO ADD MESSAGE

		// if i am the user, reauth
		User me = authenticationService.getAuthenticatedUser();
		if (me.getUserId().intValue() == userId.intValue()) {
			doReauth();
		}

		waspMessage("hello.error");
		return "redirect:/lab/user_manager/" + labId + ".do";
	}

	/**
	 * Creates a new Lab from a LabPending object. If principal investigator is
	 * pending, her {@link User} account is generated first.
	 * 
	 * @param labPending
	 * @return {@link Lab} the created lab
	 * @throws MetadataException
	 */
	public Lab createLabFromLabPending(LabPending labPending) throws MetadataException {
		Lab lab = new Lab();
		User user;

		if (labPending.getUserpendingId() != null) {
			// this PI is currently a pending user. Make them a full user before
			// creating lab
			UserPending userPending = userPendingService.getUserPendingByUserPendingId(labPending.getUserpendingId());
			user = createUserFromUserPending(userPending);
		} else if (labPending.getPrimaryUserId() != null) {
			// the referenced PI of this lab exists in the user table already so
			// get their record
			user = userService.getUserByUserId(labPending.getPrimaryUserId());
		} else {
			// shouldn't get here
			return lab; // return empty lab
		}

		lab.setPrimaryUserId(user.getUserId());
		lab.setName(labPending.getName());
		lab.setDepartmentId(labPending.getDepartmentId());
		lab.setIsActive(1);

		Lab labDb = labService.save(lab);

		// copies meta data from labPendingMeta to labMeta.
		MetaHelper labMetaHelper = new MetaHelper("lab", LabMeta.class,	request.getSession());
		labMetaHelper.getMasterList(LabMeta.class);
		MetaHelper labPendingMetaHelper = new MetaHelper("labPending", LabPendingMeta.class, request.getSession());
		List<LabPendingMeta> labPendingMetaList = labPendingMetaHelper.syncWithMaster(labPending.getLabPendingMeta());

		for (LabPendingMeta lpm : labPendingMetaList) {
			// get name from prefix by removing area
			String name = lpm.getK().replaceAll("^.*?\\.", "");
			try {
				labMetaHelper.setMetaValueByName(name, lpm.getV());
			} catch (MetadataException e) {
				// no match for 'name' in labMeta
				logger.debug("No match for labPendingMeta property with name '"	+ name + "' in labMeta properties");
			}
		}
		labMetaService.updateByLabId(labDb.getLabId(), (List<LabMeta>) labMetaHelper.getMetaList());
		// set pi role
		Role role = roleService.getRoleByRoleName("pi");

		LabUser labUser = new LabUser();
		labUser.setUserId(user.getUserId());
		labUser.setLabId(lab.getLabId());
		labUser.setRoleId(role.getRoleId());
		labUserService.save(labUser);

		// set status to 'CREATED' for any other pending labs of the same name
		// (user may have attempted to apply for their
		// lab account more than once)
		Map pendingLabQueryMap = new HashMap();
		pendingLabQueryMap.put("primaryUserId", user.getUserId());
		pendingLabQueryMap.put("name", labDb.getName());
		for (LabPending lp : (List<LabPending>) labPendingService.findByMap(pendingLabQueryMap)) {
			lp.setStatus("CREATED");
			labPendingService.save(lp);
		}

		// if i am the p.i. reauth
		User me = authenticationService.getAuthenticatedUser();

		if (me.getUserId().intValue() == user.getUserId().intValue()) {
			doReauth();
		}

		return labDb;
	}

	/**
	 * Creates and returns a new {@link User} object from the supplied
	 * {@link UserPending} object.
	 * 
	 * @param userPending
	 *            the pending user
	 * @return {@link User} the created user
	 * @throws MetadataException
	 */
	public User createUserFromUserPending(UserPending userPending) throws MetadataException {
		boolean isPiPending = (userPending.getLabId() == null) ? true : false;
		User user = new User();
		user.setFirstName(userPending.getFirstName());
		user.setLastName(userPending.getLastName());
		user.setEmail(userPending.getEmail());
		user.setPassword(userPending.getPassword());
		user.setLocale(userPending.getLocale());
		user.setIsActive(1);
		user.setLogin(userPending.getLogin());
		User userDb = userService.save(user);
		int userId = userDb.getUserId();

		/*
		 * List<UserPendingMeta> userPendingMetaList =
		 * userPendingMetaService.getUserPendingMetaByUserPendingId
		 * (userPending.getUserPendingId()); copies meta data
		 */
		MetaHelper userMetaHelper = new MetaHelper("user", UserMeta.class, request.getSession());
		userMetaHelper.getMasterList(UserMeta.class);
		MetaHelper userPendingMetaHelper = new MetaHelper("userPending",
				UserPendingMeta.class, request.getSession());
		if (isPiPending)
			userPendingMetaHelper.setArea("piPending");
		List<UserPendingMeta> userPendingMetaList = userPendingMetaHelper.syncWithMaster(userPending.getUserPendingMeta());

		for (UserPendingMeta upm : userPendingMetaList) {
			// convert prefix
			String name = upm.getK().replaceAll("^.*?\\.", "");
			try {
				userMetaHelper.setMetaValueByName(name, upm.getV());
			} catch (MetadataException e) {
				// no match for 'name' in userMeta data
				logger.debug("No match for userPendingMeta property with name '" + name + "' in userMeta properties");
			}
		}
		// if this user is not a PI, copy address information from the PI's User
		// data.
		if (!isPiPending) {
			/*
			 * not a PI application request create a metahelper object to work
			 * with metadata for PI.
			 */
			String piUserLogin = userPendingMetaHelper.getMetaByName("primaryuserid").getV();
			MetaHelper piMetaHelper = new MetaHelper("user", UserMeta.class, request.getSession());
			piMetaHelper.syncWithMaster(userService.getUserByLogin(piUserLogin).getUserMeta()); // get PI meta from database and sync with
										// current properties
			try {
				userMetaHelper.setMetaValueByName("institution", piMetaHelper.getMetaByName("institution").getV());
				userMetaHelper.setMetaValueByName("departmentId", piMetaHelper.getMetaByName("departmentId").getV());
				userMetaHelper.setMetaValueByName("state", piMetaHelper.getMetaByName("state").getV());
				userMetaHelper.setMetaValueByName("city", piMetaHelper.getMetaByName("city").getV());
				userMetaHelper.setMetaValueByName("country", piMetaHelper.getMetaByName("country").getV());
				userMetaHelper.setMetaValueByName("zip", piMetaHelper.getMetaByName("zip").getV());
			} catch (MetadataException e) {
				// should never get here because of sync
				throw new MetadataException("Metadata user / pi meta name mismatch", e);
			}
		}
		userMetaService.updateByUserId(userId, (List<UserMeta>) userMetaHelper.getMetaList());

		// userDb doesn't have associated metadata so add it
		userDb.setUserMeta((List<UserMeta>) userMetaHelper.getMetaList());

		/*
		 * Set status of any other applications from user with the same email
		 * address to 'CREATED' If the new user is also pending in other labs
		 * but not yet confirmed by the PI of each of those labs, add the user
		 * to that lab (via entry into the labUser table) and set their status
		 * as 'lp' (lab-pending).
		 */
		Map userPendingQueryMap = new HashMap();
		userPendingQueryMap.put("email", userPending.getEmail());
		userPendingQueryMap.put("status", "PENDING");
		List<UserPending> userPendingList = userPendingService.findByMap(userPendingQueryMap);
		userPendingQueryMap.put("status", "WAIT_EMAIL");
		userPendingList.addAll(userPendingService.findByMap(userPendingQueryMap));
		Role roleLabPending = roleService.getRoleByRoleName("lp");

		for (UserPending userPendingCurrent : userPendingList) {
			userPendingCurrent.setStatus("CREATED");
			userPendingService.save(userPendingCurrent);

			if (userPendingCurrent.getLabId() != null) {
				// not a PI application request
				LabUser labUserCurrent = labUserService.getLabUserByLabIdUserId(userPendingCurrent.getLabId(),	userDb.getUserId());
				//NV 12132011
				//if (labUserCurrent.getLabUserId() > 0) {
				if (labUserCurrent.getLabUserId() != null) {
					// already registered as a user of the requested lab
					continue;
				}
				// add user to requested lab with lab-pending role
				labUserCurrent.setUserId(userId);
				labUserCurrent.setLabId(userPendingCurrent.getLabId());
				labUserCurrent.setRoleId(roleLabPending.getRoleId());
				labUserService.save(labUserCurrent);

			}
			/*
			 * iterate through list of pending labs. If this user was previously
			 * registered as 'userPending' in a lab, remove reference to her
			 * userPendingId and insert reference to her new userId instead
			 */
			Map labPendingQueryMap = new HashMap();
			labPendingQueryMap.put("userpendingId",	userPendingCurrent.getUserPendingId());

			List<LabPending> labPendingList = labPendingService.findByMap(labPendingQueryMap);
			for (LabPending labPending : labPendingList) {
				labPending.setUserpendingId((Integer) null);
				labPending.setPrimaryUserId(userId);
				labPendingService.save(labPending);
			}
		}

		return userDb;
	}

	/**
	 * Request-mapped function that allows a principal investigator or lab
	 * manager to accept or reject an application from a pending lab user (already
	 * an active WASP user) to join their lab.
	 * @param labId
	 * @param labUserId
	 * @param action
	 * @param m
	 * @return
	 */
	@RequestMapping(value = "/labuserpending/{action}/{labId}/{labUserId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lm-' + #labId)")
	public String labUserPendingDetail(@PathVariable("labId") Integer labId,
									   @PathVariable("labUserId") Integer labUserId,
									   @PathVariable("action") String action, ModelMap m){
		if (!(action.equals("approve") || action.equals("reject"))) {
			waspMessage("userPending.action.error");
			return "redirect:/dashboard.do";
		}
		LabUser labUserPending = labUserService.getLabUserByLabUserId(labUserId);
		if (labUserPending.getLabId().intValue() != labId.intValue()){
			waspMessage("labuser.labUserNotFoundInLab.error");
			return "redirect:/dashboard.do";
		}
		if (labUserPending.getRoleId().intValue() != roleService.getRoleByRoleName("lp").getRoleId().intValue() ) {
			waspMessage("userPending.status_not_pending.error");
			return "redirect:/dashboard.do";
		}

		if ("approve".equals(action)) {
			// add user to lab (labUser table) with role 'lu' (lab-user) and
			// email pending user notification of acceptance
			Role roleLabUser = roleService.getRoleByRoleName("lu");
			// createUserFromUserPending, should have made this.
			labUserPending.setRoleId(roleLabUser.getRoleId());
			labUserService.save(labUserPending);
			emailService.sendPendingLabUserNotifyAccepted(labUserPending.getUser(), labUserPending.getLab());
			waspMessage("userPending.approved.label");
		} else {
			labUserService.remove(labUserPending);
			// email pending user notification of rejection
			emailService.sendPendingLabUserNotifyRejected(labUserPending.getUser(),	labUserPending.getLab());
			waspMessage("userPending.rejected.label");
		}

		String referer = request.getHeader("Referer");
		return "redirect:"+ referer;
	}
	
	/**
	 * Request-mapped function that allows a principal investigator or lab
	 * manager to accept or reject an application from a pending user (not yet
	 * an active WASP user) to join their lab.
	 * 
	 * @param labId
	 * @param userPendingId
	 * @param action
	 * @param m
	 * @return view
	 * @throws MetadataException
	 */
	@RequestMapping(value = "/userpending/{action}/{labId}/{userPendingId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lm-' + #labId)")
	public String userPendingDetail(@PathVariable("labId") Integer labId,
			@PathVariable("userPendingId") Integer userPendingId,
			@PathVariable("action") String action, ModelMap m)
			throws MetadataException {

		if (!(action.equals("approve") || action.equals("reject"))) {
			waspMessage("userPending.action.error");
			return "redirect:/dashboard.do";
		}

		UserPending userPending = userPendingService.getUserPendingByUserPendingId(userPendingId);
		if (! userPending.getStatus().equals("PENDING") ) {
			waspMessage("userPending.status_not_pending.error");
			return "redirect:/dashboard.do";
		}
		
		if (userPending.getLabId().intValue() != labId.intValue()) {
			waspMessage("userPending.labid_mismatch.error");
			return "redirect:/dashboard.do";
		}
		User user;

		if ("approve".equals(action)) {
			// add user to lab (labUser table) with role 'lu' (lab-user) and
			// email pending user notification of acceptance
			user = createUserFromUserPending(userPending);
			Role roleLabUser = roleService.getRoleByRoleName("lu");
			// createUserFromUserPending, should have made this.
			LabUser labUser = labUserService.getLabUserByLabIdUserId(labId,	user.getUserId());
			labUser.setRoleId(roleLabUser.getRoleId());
			labUserService.merge(labUser);
			emailService.sendPendingUserNotifyAccepted(user, labService.getLabByLabId(labId));
			waspMessage("userPending.approved.label");
		} else {
			// email pending user notification of rejection
			emailService.sendPendingUserNotifyRejected(userPending,	labService.getLabByLabId(labId));
			waspMessage("userPending.rejected.label");
		}
		userPending.setStatus(action);
		userPendingService.save(userPending);
		String referer = request.getHeader("Referer");
		return "redirect:"+ referer;
	}

	/**
	 * Request-mapped function that allows a department administrator to accept
	 * or reject an application for a new lab within their department.
	 * 
	 * @param departmentId
	 * @param labPendingId
	 * @param action
	 * @param m
	 * @return
	 * @throws MetadataException
	 */

	@RequestMapping(value = "/pending/{action}/{deptId}/{labPendingId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('da-' + #deptId) or hasRole('ga-*')")
	public String labPendingDetail(@PathVariable("deptId") Integer deptId,
			@PathVariable("labPendingId") Integer labPendingId,
			@PathVariable("action") String action, ModelMap m)
			throws MetadataException {

		if (!(action.equals("approve") || action.equals("reject"))) {
			waspMessage("labPending.action.error");
			//return "redirect:/department/detail/" + deptId + ".do";
			return "redirect:/department/dapendingtasklist.do";
		}
		LabPending labPending = labPendingService.getLabPendingByLabPendingId(labPendingId);
		if (! labPending.getStatus().equals("PENDING") ) {
			waspMessage("labPending.status_not_pending.error");
			//return "redirect:/department/detail/" + deptId + ".do";
			return "redirect:/department/dapendingtasklist.do";
		}
		
		if (labPending.getDepartmentId().intValue() != deptId.intValue()) {
			waspMessage("labPending.departmentid_mismatch.error");
			//return "redirect:/department/detail/" + deptId + ".do";
			return "redirect:/department/dapendingtasklist.do";
		}

		if ("approve".equals(action)) {
			Lab lab = createLabFromLabPending(labPending);
			if (lab.getLabId() == null || lab.getLabId() == 0){
				waspMessage("labPending.could_not_create_lab.error");
				//return "redirect:/department/detail/" + deptId + ".do";
				return "redirect:/department/dapendingtasklist.do";
			}
			emailService.sendPendingLabNotifyAccepted(lab);
			waspMessage("labPending.approved.label");
		} else {
			if (labPending.getUserpendingId() != null) {
				// this PI is currently a pending user. Reject their pending
				// user application too
				UserPending userPending = userPendingService.getUserPendingByUserPendingId(labPending.getUserpendingId());
				userPending.setStatus(action);
				userPendingService.save(userPending);
				waspMessage("labPending.rejected.label");
			} else if (labPending.getPrimaryUserId() == null){
				waspMessage("labPending.could_not_create_lab.error");
				//return "redirect:/department/detail/" + deptId + ".do";
				return "redirect:/department/dapendingtasklist.do";
			}
			emailService.sendPendingLabNotifyRejected(labPending);
		}
		labPending.setStatus(action);
		labPendingService.save(labPending);
		//return "redirect:/department/detail/" + deptId + ".do";
		return "redirect:/department/dapendingtasklist.do";
	}
	
	/**
	 * Handles request to create new laboratory by GET. 
	 * Pre-populates as much of the form as possible given current user information.
	 * @param m model
	 * @return view
	 * @throws MetadataException
	 */
	@RequestMapping(value = "/newrequest", method = RequestMethod.GET)
	public String showRequestForm(ModelMap m) throws MetadataException {
		MetaHelper labPendingMetaHelper = new MetaHelper("labPending",	LabPendingMeta.class, request.getSession());
		labPendingMetaHelper.getMasterList(LabPendingMeta.class);
		MetaHelper userMetaHelper = new MetaHelper("user", UserMeta.class, request.getSession());

		// Pre-populate some metadata from user's current information
		User me = authenticationService.getAuthenticatedUser();
		userMetaHelper.syncWithMaster(me.getUserMeta()); // get user meta from database and sync with current properties
		LabPending labPending = new LabPending();
		try {
			String departmentId = userMetaHelper.getMetaByName("departmentId").getV();
			if (departmentId != null && !departmentId.isEmpty()){
				labPendingMetaHelper.setMetaValueByName("billing_departmentId",departmentId);
				labPending.setDepartmentId(Integer.valueOf(departmentId));
				String internalExternal = (deptService.findById( Integer.valueOf(departmentId) ).getIsInternal().intValue() == 1) ? "internal" : "external";
				labPendingMetaHelper.setMetaValueByName("internal_external_lab", internalExternal);
			}
			labPendingMetaHelper.setMetaValueByName("billing_institution", userMetaHelper.getMetaByName("institution").getV());
			labPendingMetaHelper.setMetaValueByName("billing_state", userMetaHelper.getMetaByName("state").getV());
			labPendingMetaHelper.setMetaValueByName("billing_city", userMetaHelper.getMetaByName("city").getV());
			labPendingMetaHelper.setMetaValueByName("billing_country", userMetaHelper.getMetaByName("country").getV());
			labPendingMetaHelper.setMetaValueByName("billing_zip", userMetaHelper.getMetaByName("zip").getV());
			labPendingMetaHelper.setMetaValueByName("billing_contact", me.getFirstName() + " " + me.getLastName());
			
		} catch (MetadataException e) {
			// report meta problem
			logger.warn("Meta data mismatch when pre-populating labMeta data from userMeta (" + e.getMessage() + ")");
		}
		
		labPending.setLabPendingMeta( (List<LabPendingMeta>) labPendingMetaHelper.getMetaList());
		m.addAttribute("labPending", labPending);
		prepareSelectListData(m);

		return "lab/newrequest";
	}
	
	/**
	 * Handles request to create new laboratory by POST.
	 * Validates LabPending form.
	 * @param labPendingForm
	 * @param result
	 * @param status
	 * @param m model
	 * @return view
	 */
	@RequestMapping(value = "/newrequest", method = RequestMethod.POST)
	public String createNewLabPending(@Valid LabPending labPendingForm,	BindingResult result, SessionStatus status, ModelMap m) {
		
		MetaHelper pendingMetaHelper = new MetaHelper("labPending",LabPendingMeta.class, request.getSession());

		List<LabPendingMeta> labPendingMetaList = pendingMetaHelper.getFromRequest(request, LabPendingMeta.class);
		pendingMetaHelper.validate(labPendingMetaList, result);

		User me = authenticationService.getAuthenticatedUser();

		labPendingForm.setPrimaryUserId(me.getUserId());
		labPendingForm.setStatus("PENDING");

		if (result.hasErrors()) {
			labPendingForm.setLabPendingMeta(labPendingMetaList);
			prepareSelectListData(m);
			waspMessage("user.created.error");

			return "lab/newrequest";
		}

		LabPending labPendingDb = labPendingService.save(labPendingForm);

		labPendingMetaService.updateByLabpendingId(labPendingDb.getLabPendingId(), labPendingMetaList);

		status.setComplete();

		emailService.sendPendingPrincipalConfirmRequest(labPendingDb);

		waspMessage("labuser.request_success.label");

		return "redirect:/dashboard.do";
	}
	
	/**
	 * Handles request to join an existing lab by logged in user.
	 * @param primaryUserLogin The login name of the PI of the lab which the user wishes to join
	 * @param m model
	 * @return view
	 */
	@RequestMapping(value = "/request.do", method = RequestMethod.POST)
	public String requestAccess(
			@RequestParam("primaryUserLogin") String primaryUserLogin,
			ModelMap m) {
		
		// check existence of primaryUser/lab
		if (primaryUserLogin == null || primaryUserLogin.isEmpty()){
			waspMessage("labuser.request_primaryuser.error");
			return "redirect:/lab/newrequest.do";
		}
		
		User primaryUser = userService.getUserByLogin(primaryUserLogin);
		if (primaryUser.getUserId() == null || primaryUser.getUserId() == 0) {
			waspMessage("labuser.request_primaryuser.error");
			return "redirect:/lab/newrequest.do";
		}

		Lab lab = labService.getLabByPrimaryUserId(primaryUser.getUserId());
		if (lab.getLabId() == null || lab.getLabId() == 0) {
			waspMessage("labuser.request_primaryuser.error");
			return "redirect:/lab/newrequest.do";
		}

		// check role of lab user
		User me = authenticationService.getAuthenticatedUser();
		LabUser labUser = labUserService.getLabUserByLabIdUserId(lab.getLabId(), me.getUserId());

		if (labUser.getLabUserId() != null) {
			ArrayList<String> alreadyPendingRoles = new ArrayList();
			alreadyPendingRoles.add("lp");
			if (alreadyPendingRoles.contains(labUser.getRole().getRoleName())) {
				waspMessage("labuser.request_alreadypending.error");
				return "redirect:/lab/newrequest.do";
			}
			
			ArrayList<String> alreadyAccessRoles = new ArrayList();
			alreadyAccessRoles.add("pi");
			alreadyAccessRoles.add("lm");
			alreadyAccessRoles.add("lu");
			if (alreadyAccessRoles.contains(labUser.getRole().getRoleName())) {
				waspMessage("labuser.request_alreadyaccess.error");
				return "redirect:/lab/newrequest.do";
			}
		}

		Role role = roleService.getRoleByRoleName("lp");

		labUser.setLabId(lab.getLabId());
		labUser.setUserId(me.getUserId());
		labUser.setRoleId(role.getRoleId());
		labUserService.save(labUser);

		labUserService.refresh(labUser);

		emailService.sendPendingLabUserConfirmRequest(labUser);

		waspMessage("labuser.request_success.label");

		return "redirect:/dashboard.do";
	}

	@Override
	protected void prepareSelectListData(ModelMap m) {
		Map userQueryMap = new HashMap();
		userQueryMap.put("isActive", 1);
		m.addAttribute("pusers", userService.findByMap(userQueryMap));
		super.prepareSelectListData(m);
		m.addAttribute("departments", deptService.findAll());
	}

	@RequestMapping(value = "/pendinglmapproval/list/{labId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lm-' + #labId) or hasRole('ga-*')")
	public String tasksPendingLmApproval(@PathVariable("labId") Integer labId, ModelMap m){
		
		//Map jobMetaMap = new HashMap();
		Lab lab = labService.getLabByLabId(labId);
		List<UserPending> newUsersPendingLmApprovalList = new ArrayList<UserPending>();
		List<LabUser> existingUsersPendingLmApprovalList = new ArrayList<LabUser>();
		List<Job> jobsPendingLmApprovalList = new ArrayList<Job>();
		labService.getLabManagerPendingTasks(labId, newUsersPendingLmApprovalList, existingUsersPendingLmApprovalList, jobsPendingLmApprovalList);
		//for(Job job : jobsPendingLmApprovalList){
		//	List<JobMeta> jobMetaList = job.getJobMeta();
		//	jobMetaMap.put(job.getJobId(), jobMetaList);
		//}
		//m.addAttribute("jobmetamap", jobMetaMap);
		m.addAttribute("lab", lab);
		m.addAttribute("newuserspendinglist", newUsersPendingLmApprovalList); 
		m.addAttribute("existinguserspendinglist", existingUsersPendingLmApprovalList); 
		m.addAttribute("jobspendinglist", jobsPendingLmApprovalList); 
		return "lab/pendinglmapproval/list";
	}

	@RequestMapping(value = "/allpendinglmapproval/list.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god') or hasRole('lm-' + #labId) or hasRole('ga-*')")
	public String allTasksPendingLmApproval(ModelMap m){
		
		List<UserPending> newUsersPendingLmApprovalList = new ArrayList<UserPending>();
		List<LabUser> existingUsersPendingLmApprovalList = new ArrayList<LabUser>();
		List<Job> jobsPendingLmApprovalList = new ArrayList<Job>();
		int count = labService.getAllLabManagerPendingTasks(newUsersPendingLmApprovalList, existingUsersPendingLmApprovalList, jobsPendingLmApprovalList);
		int count2 = newUsersPendingLmApprovalList.size() + existingUsersPendingLmApprovalList.size() + jobsPendingLmApprovalList.size();
		m.addAttribute("count", count);
		m.addAttribute("count2", count2);
		m.addAttribute("newuserspendinglist", newUsersPendingLmApprovalList); 
		m.addAttribute("existinguserspendinglist", existingUsersPendingLmApprovalList); 
		m.addAttribute("jobspendinglist", jobsPendingLmApprovalList); 
		return "lab/allpendinglmapproval/list";
	}

	
}
