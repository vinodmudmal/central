package edu.yu.einstein.wasp.controller;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

import edu.yu.einstein.wasp.controller.PlatformUnitController.SelectOptionsMeta;
import edu.yu.einstein.wasp.controller.util.MetaHelperWebapp;
import edu.yu.einstein.wasp.dao.RunCellDao;
import edu.yu.einstein.wasp.dao.RunDao;
import edu.yu.einstein.wasp.dao.RunMetaDao;
import edu.yu.einstein.wasp.dao.SampleDao;
import edu.yu.einstein.wasp.dao.SampleSubtypeResourceCategoryDao;
import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.model.MetaBase;
import edu.yu.einstein.wasp.model.Resource;
import edu.yu.einstein.wasp.model.ResourceCategory;
import edu.yu.einstein.wasp.model.ResourceCategoryMeta;
import edu.yu.einstein.wasp.model.Run;
import edu.yu.einstein.wasp.model.RunCell;
import edu.yu.einstein.wasp.model.RunCellFile;
import edu.yu.einstein.wasp.model.RunFile;
import edu.yu.einstein.wasp.model.RunMeta;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleBarcode;
import edu.yu.einstein.wasp.model.SampleMeta;
import edu.yu.einstein.wasp.model.SampleSubtypeResourceCategory;
import edu.yu.einstein.wasp.model.User;
import edu.yu.einstein.wasp.model.Userrole;
import edu.yu.einstein.wasp.service.MessageServiceWebapp;
import edu.yu.einstein.wasp.service.ResourceService;
import edu.yu.einstein.wasp.service.SampleService;
import edu.yu.einstein.wasp.service.UserService;
import edu.yu.einstein.wasp.taglib.JQFieldTag;

@Controller
@Transactional
@RequestMapping("/run")
public class RunController extends WaspController {

	@Autowired
	private SampleService sampleService;

	private RunDao	runDao;

	@Autowired
	public void setRunDao(RunDao runDao) {
		this.runDao = runDao;
	}

	public RunDao getRunDao() {
		return this.runDao;
	}

	@Autowired
	private RunMetaDao runMetaDao;

	private RunCellDao	runCellDao;

	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public ResourceService getResourceService() {
		return this.resourceService;
	}

	private ResourceService	resourceService;

	@Autowired
	public void setSampleDao(SampleDao sampleDao) {
		this.sampleDao = sampleDao;
	}

	public SampleDao getSampleDao() {
		return this.sampleDao;
	}

	private SampleDao	sampleDao;

	@Autowired
	public void setSampleSubtypeResourceCategoryDao(SampleSubtypeResourceCategoryDao sampleSubtypeResourceCategoryDao) {
		this.sampleSubtypeResourceCategoryDao = sampleSubtypeResourceCategoryDao;
	}

	public SampleSubtypeResourceCategoryDao getSampleSubtypeResourceCategoryDao() {
		return this.sampleSubtypeResourceCategoryDao;
	}

	private SampleSubtypeResourceCategoryDao	sampleSubtypeResourceCategoryDao;

	@Autowired
	public void setRunCellDao(RunCellDao runCellDao) {
		this.runCellDao = runCellDao;
	}

	public RunCellDao getRunCellDao() {
		return this.runCellDao;
	}

	@Autowired
	private MessageServiceWebapp messageService;
	
	@Autowired
	private UserService userService;

	private final MetaHelperWebapp getMetaHelperWebapp() {
		return new MetaHelperWebapp(RunMeta.class, request.getSession());
	}

	private final MetaHelperWebapp getMetaHelperWebappPlatformUnitInstance() {
		return new MetaHelperWebapp("platformunitInstance", SampleMeta.class, request.getSession());
	}

	private final MetaHelperWebapp getMetaHelperWebappRunInstance() {
		return new MetaHelperWebapp("runInstance", RunMeta.class, request.getSession());
	}

	@RequestMapping("/list")
	public String list(ModelMap m) {
//		List<Run> runList = this.getRunDao().findAll();
//
//		m.addAttribute("run", runList);

		m.addAttribute("_metaList",	getMetaHelperWebapp().getMasterList(MetaBase.class));
		m.addAttribute(JQFieldTag.AREA_ATTR, getMetaHelperWebapp().getArea());
		m.addAttribute("_metaDataMessages", MetaHelperWebapp.getMetadataMessages(request.getSession()));

		prepareSelectListData(m);

		return "run/list";
	}

	@Override
	protected void prepareSelectListData(ModelMap m) {
		super.prepareSelectListData(m);

		m.addAttribute("machines", this.resourceService.getResourceDao().findAll());
		
		m.addAttribute("flowcells", this.sampleDao.findAllPlatformUnits());
		
		List <User> allUsers = this.userDao.getActiveUsers();
		Map <Integer, String> facUsers = new HashMap<Integer, String> ();
		for (User u : allUsers) {
			List<Userrole> urs = u.getUserrole();
			for (Userrole ur : urs) {
				String rn = ur.getRole().getRoleName();
				if ("fm".equals(rn) || "ft".equals(rn)) {
					facUsers.put(u.getUserId(), u.getNameFstLst());
					break;
				}
			}
		}
		m.addAttribute("techs", facUsers);
	}

	/*
	 * Returns compatible flowcells by given machine
	 * 
	 * @Author AJ Jing
	 */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/samplesByResourceId", method=RequestMethod.GET)	
	public String samplesByResourceId(@RequestParam("resourceId") Integer resourceId, HttpServletResponse response) {

		Map <Integer, String> resultsMap = new LinkedHashMap<Integer, String>();
		
		if (resourceId.intValue() == -1) {
			
			for (Sample sample:sampleDao.findAllPlatformUnits()) {
				resultsMap.put(sample.getSampleId(), sample.getName());
			}
			
		} else {
		
			//first get the resourceCategoryId by resourceId
			Resource machine = this.resourceService.getResourceDao().getById(resourceId);
			//then get all the sampleSubtypeId by resourceCatgegoryId 
			Map queryMap = new HashMap();
			queryMap.put("resourcecategoryId", machine.getResourcecategoryId());
			List <SampleSubtypeResourceCategory> ssrcList = this.sampleSubtypeResourceCategoryDao.findByMap(queryMap);
			List <Integer> idList = new ArrayList<Integer> ();
			for (SampleSubtypeResourceCategory ssrc : ssrcList) {
				idList.add(ssrc.getSampleSubtypeId());
			}
			
			//last filter all platform units by the list of sampleSubtypeId
			for(Sample sample:sampleDao.findAllPlatformUnits()) {
				if (idList.contains(sample.getSampleSubtypeId()))
					resultsMap.put(sample.getSampleId(), sample.getName());
			}
		}

		try {
			return outputJSON(resultsMap, response); 	
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON "+resultsMap,e);
		}
	}
	/*
	 * Returns compatible machines by given flowcell
	 * 
	 * @Author AJ Jing
	 */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/resourcesBySampleId", method=RequestMethod.GET)	
	public String resourcesBySampleId(@RequestParam("sampleId") Integer sampleId, HttpServletResponse response) {

		Map <Integer, String> resultsMap = new LinkedHashMap<Integer, String>();
		
		if (sampleId.intValue() == -1) {
			
			for (Resource resource:resourceService.getResourceDao().findAll()) {
				resultsMap.put(resource.getResourceId(), resource.getName());
			}
			
		} else {
		
			//first get the sampleSubtypeId by sampleId
			Sample flowcell = sampleDao.getById(sampleId);
			//then get all the resourceCategoryId by resourceCatgegoryId 
			Map queryMap = new HashMap();
			queryMap.put("sampleSubtypeId", flowcell.getSampleSubtypeId());
			List <SampleSubtypeResourceCategory> ssrcList = this.sampleSubtypeResourceCategoryDao.findByMap(queryMap);
			List <Integer> idList = new ArrayList<Integer> ();
			for (SampleSubtypeResourceCategory ssrc : ssrcList) {
				idList.add(ssrc.getResourcecategoryId());
			}
			
			//last filter all platform units by the list of sampleSubtypeId
			for(Resource resource:resourceService.getResourceDao().findAll()) {
				if (idList.contains(resource.getResourcecategoryId()))
					resultsMap.put(resource.getResourceId(), resource.getName());
			}
		}

		try {
			return outputJSON(resultsMap, response); 	
		} catch (Throwable e) {
			throw new IllegalStateException("Can't marshall to JSON "+resultsMap,e);
		}
	}

	@RequestMapping(value="/listJSON", method=RequestMethod.GET)	
	public String getListJSON(HttpServletResponse response) {
		
		//result
		Map <String, Object> jqgrid = new HashMap<String, Object>();

		//Parameters coming from the jobGrid
		String sord = request.getParameter("sord");//grid is set so that this always has a value
		String sidx = request.getParameter("sidx");//grid is set so that this always has a value
		String search = request.getParameter("_search");//from grid (will return true or false, depending on the toolbar's parameters)
		logger.debug("sidx = " + sidx);logger.debug("sord = " + sord);logger.debug("search = " + search);
//String selIdAsString = request.getParameter("selId");
//logger.debug("selIdAsString = " + selIdAsString);
		//Parameters coming from grid's toolbar
		//The jobGrid's toolbar's is it's search capability. The toolbar's attribute stringResult is currently set to false, 
		//meaning that each parameter on the toolbar is sent as a key:value pair
		//If stringResult = true, the parameters containing values would have been sent as a key named filters in JSON format 
		//see http://www.trirand.com/jqgridwiki/doku.php?id=wiki:toolbar_searching
		//below we capture parameters on job grid's search toolbar by name (key:value).
		String nameFromGrid = request.getParameter("name")==null?null:request.getParameter("name").trim();//if not passed,  will be null
		String platformUnitBarcodeFromGrid = request.getParameter("platformUnitBarcode")==null?null:request.getParameter("platformUnitBarcode").trim();//if not passed, will be null
		String machineAndMachineTypeFromGrid = request.getParameter("machine")==null?null:request.getParameter("machine").trim();//if not passed, will be null
		String readTypeFromGrid = request.getParameter("readType")==null?null:request.getParameter("readType").trim();//if not passed, will be null
		String readlengthFromGrid = request.getParameter("readlength")==null?null:request.getParameter("readlength").trim();//if not passed, will be null
		String dateRunStartedFromGridAsString = request.getParameter("dateRunStarted")==null?null:request.getParameter("dateRunStarted").trim();//if not passed, will be null
		String dateRunEndedFromGridAsString = request.getParameter("dateRunEnded")==null?null:request.getParameter("dateRunEnded").trim();//if not passed, will be null
		String statusForRunFromGrid = request.getParameter("statusForRun")==null?null:request.getParameter("statusForRun").trim();//if not passed, will be null
		logger.debug("nameFromGrid = " + nameFromGrid);logger.debug("platformUnitBarcodeFromGrid = " + platformUnitBarcodeFromGrid);
		logger.debug("machineAndMachineTypeFromGrid = " + machineAndMachineTypeFromGrid); 
		logger.debug("readTypeFromGrid = " + readTypeFromGrid);logger.debug("readlengthFromGrid = " + readlengthFromGrid);
		logger.debug("dateRunStartedFromGridAsString = " + dateRunStartedFromGridAsString);logger.debug("dateRunEndedFromGridAsString = " + dateRunEndedFromGridAsString);
		logger.debug("statusForRunFromGrid = " + statusForRunFromGrid);
	
		List<Run> tempRunList =  new ArrayList<Run>();
		List<Run> runsFoundInSearch = new ArrayList<Run>();
		List<Run> runList = new ArrayList<Run>();

		//convert dates (as string) to datatype Date
		Date dateRunStartedFromGridAsDate = null;
		if(dateRunStartedFromGridAsString != null){//this is yyyy/MM/dd coming from grid
			DateFormat formatter;
			formatter = new SimpleDateFormat("yyyy/MM/dd");
			try{				
				dateRunStartedFromGridAsDate = (Date)formatter.parse(dateRunStartedFromGridAsString); 
			}
			catch(Exception e){ 
				dateRunStartedFromGridAsDate = new Date(0);//fake it; parameter of 0 sets date to 01/01/1970 which is NOT in this database. So result set will be empty
			}
		}		
		Date dateRunEndedFromGridAsDate = null;
		if(dateRunEndedFromGridAsString != null){//this is yyyy/MM/dd coming from grid
			DateFormat formatter;
			formatter = new SimpleDateFormat("yyyy/MM/dd");
			try{				
				dateRunEndedFromGridAsDate = (Date)formatter.parse(dateRunEndedFromGridAsString); 
			}
			catch(Exception e){ 
				dateRunEndedFromGridAsDate = new Date(0);//fake it; parameter of 0 sets date to 01/01/1970 which is NOT in this database. So result set will be empty
			}
		}
		
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("resource.resourceType.iName", "mps");//restrict to mps
		queryMap.put("resourceCategory.resourceType.iName", "mps");//restrict to mps
		//deal with those attributes that can be searched for directly in table sample (sample.name and sample.sampleSubtype)
		if(nameFromGrid != null){
			queryMap.put("name", nameFromGrid);//and restrict to the passed name
		}		
		
		Map<String, Date> dateMap = new HashMap<String, Date>();
		if(dateRunStartedFromGridAsDate != null){
			dateMap.put("startts", dateRunStartedFromGridAsDate);
		}
		if(dateRunEndedFromGridAsDate != null){
			dateMap.put("enDts", dateRunEndedFromGridAsDate);
		}

		List<String> orderByColumnAndDirection = new ArrayList<String>();		
		if(sidx!=null && !"".equals(sidx)){//sord is apparently never null; default is desc
			if(sidx.equals("dateRunStarted")){
				orderByColumnAndDirection.add("startts " + sord);
			}
			else if(sidx.equals("dateRunEnded")){
				orderByColumnAndDirection.add("enDts " + sord);
			}
			else if(sidx.equals("name")){//run.name
				orderByColumnAndDirection.add("name " + sord);
			}
		}
		else if(sidx==null || "".equals(sidx)){
			orderByColumnAndDirection.add("startts desc");
		}
		tempRunList = runDao.findByMapsIncludesDatesDistinctOrderBy(queryMap, dateMap, null, orderByColumnAndDirection);

		//deal with searching for attributes that cannot directly be dealt with by the SQL statement
		if(platformUnitBarcodeFromGrid != null || machineAndMachineTypeFromGrid != null || readTypeFromGrid != null 
				|| readlengthFromGrid != null || statusForRunFromGrid != null){
			
			if(platformUnitBarcodeFromGrid != null){
				for(Run run : tempRunList){
					List<SampleBarcode> sbList = run.getPlatformUnit().getSampleBarcode();
					if(sbList.get(0).getBarcode().getBarcode().equalsIgnoreCase(platformUnitBarcodeFromGrid)){
						runsFoundInSearch.add(run);
					}
				}
				tempRunList.retainAll(runsFoundInSearch);
				runsFoundInSearch.clear();
			}			
			if(readTypeFromGrid != null){
				for(Run run : tempRunList){
					List<RunMeta> runMetaList = run.getRunMeta();
					for(RunMeta rm : runMetaList){
						if(rm.getK().indexOf("readType") > -1){
							if(rm.getV().equalsIgnoreCase(readTypeFromGrid)){
								runsFoundInSearch.add(run);
							}
							break;//out of inner for loop
						}
					}					
				}
				tempRunList.retainAll(runsFoundInSearch);
				runsFoundInSearch.clear();
			}			
			if(readlengthFromGrid != null){
				for(Run run : tempRunList){
					List<RunMeta> runMetaList = run.getRunMeta();
					for(RunMeta rm : runMetaList){
						if(rm.getK().indexOf("readlength") > -1){
							if(rm.getV().equalsIgnoreCase(readlengthFromGrid)){
								runsFoundInSearch.add(run);
							}
							break;//out of inner for loop
						}
					}					
				}
				tempRunList.retainAll(runsFoundInSearch);
				runsFoundInSearch.clear();
			}		
		
			if(machineAndMachineTypeFromGrid != null){
				String[] tokens =  machineAndMachineTypeFromGrid.split("-");
				String machineName = tokens[0].trim();
				for(Run run: tempRunList){
					if(machineName.equalsIgnoreCase(run.getResource().getName())){
						runsFoundInSearch.add(run);
					}
				}
				tempRunList.retainAll(runsFoundInSearch);
				runsFoundInSearch.clear();
			}
						
			//must deal with status, but how?
			if(statusForRunFromGrid != null){
				//for(Run run : tempRunList){
					//if(run.status.equals(statusForRunFromGrid)){
					//	runsFoundInSearch.add(run);
					//}
				//}
				//tempRunList.retainAll(runsFoundInSearch);
				//runsFoundInSearch.clear();
			}
		}
		
		runList.addAll(tempRunList);
		
		//finally deal with sorting of items that cannot be sorted directly by the SQL statement
		if( sidx != null && !sidx.isEmpty() && !sidx.equals("dateRunStarted") && sord != null && !sord.isEmpty() ){//if sidx==dateRunStarted, it's taken care of above
			
			boolean indexSorted = false;
			
			if(sidx.equals("platformUnitBarcode")){Collections.sort(runList, new RunPlatformUnitBarcodeComparator()); indexSorted = true;}
			else if(sidx.equals("machine")){Collections.sort(runList, new MachineNameComparator()); indexSorted = true;}
			else if(sidx.equals("readlength")){Collections.sort(runList, new RunMetaIsStringComparator("readlength")); indexSorted = true;}
			else if(sidx.equals("readType")){Collections.sort(runList, new RunMetaIsStringComparator("readType")); indexSorted = true;}

			if(indexSorted == true && sord.equals("desc")){//must be last
				Collections.reverse(runList);
			}
		}
	
		try {
			int pageIndex = Integer.parseInt(request.getParameter("page"));		// index of page
			int pageRowNum = Integer.parseInt(request.getParameter("rows"));	// number of rows in one page
			int rowNum = runList.size();										// total number of rows
			int pageNum = (rowNum + pageRowNum - 1) / pageRowNum;				// total number of pages
			
			jqgrid.put("records", rowNum + "");
			jqgrid.put("total", pageNum + "");
			jqgrid.put("page", pageIndex + "");
			 
			Map<String, String> runData=new HashMap<String, String>();
			runData.put("page", pageIndex + "");
			runData.put("selId",StringUtils.isEmpty(request.getParameter("selId"))?"":request.getParameter("selId"));
			jqgrid.put("rundata",runData);
			 
			List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
			
			int frId = pageRowNum * (pageIndex - 1);
			int toId = pageRowNum * pageIndex;
			toId = toId <= rowNum ? toId : rowNum;

			/* if the selId is set, change the page index to the one contains the selId */
			if(!StringUtils.isEmpty(request.getParameter("selId")))
			{
				int selId = Integer.parseInt(request.getParameter("selId"));
				int selIndex = runList.indexOf(runDao.findById(selId));
				frId = selIndex;
				toId = frId + 1;

				jqgrid.put("records", "1");
				jqgrid.put("total", "1");
				jqgrid.put("page", "1");
			}				

			List<Run> runPage = runList.subList(frId, toId);
			for (Run run:runPage) {				
				
			  //10-17-12
				MetaHelperWebapp metaHelperWebapp = getMetaHelperWebappRunInstance();
				String area2 = metaHelperWebapp.getArea();
				Format formatter = new SimpleDateFormat("yyyy/MM/dd");
			
				String readlength = new String("unknown");
				try{
					readlength = MetaHelperWebapp.getMetaValue(area2, "readlength", run.getRunMeta());					
				}catch(Exception e){}
				
				String readType = new String("unknown");
				try{
					readType = MetaHelperWebapp.getMetaValue(area2, "readType", run.getRunMeta());
				}catch(Exception e){}
				
				String dateRunStarted = new String("not set");
				if(run.getStartts()!=null){
					try{				
						dateRunStarted = new String(formatter.format(run.getStartts()));//yyyy/MM/dd
					}catch(Exception e){}					
				}
				
				String dateRunEnded = new String("not set");
				if(run.getEnDts()!=null){					
					try{				
						dateRunEnded = new String(formatter.format(run.getEnDts()));//yyyy/MM/dd
					}catch(Exception e){}					
				}
				
				String statusForRun = new String("???");
				
				//deal with platformUnit and its barcode
				Sample platformUnit = null;
				String platformUnitBarcode = null;
				try{
					platformUnit = sampleService.getPlatformUnit(run.getSampleId());
					List<SampleBarcode> sampleBarcodeList = platformUnit.getSampleBarcode();
					platformUnitBarcode = sampleBarcodeList.size()>0 ? sampleBarcodeList.get(0).getBarcode().getBarcode() : new String("");
					
				}catch(Exception e){platformUnitBarcode = new String("???");}
				
								
				Map<String, Object> cell = new HashMap<String, Object>();
				cell.put("id", run.getRunId());	//used??			 
				List<String> cellList=new ArrayList<String>(Arrays.asList(new String[] {
						"<a href=/wasp/facility/platformunit/showPlatformUnit/"+platformUnit.getSampleId()+".do>"+run.getName()+"</a>",
						"<a href=/wasp/facility/platformunit/showPlatformUnit/"+platformUnit.getSampleId()+".do>"+platformUnitBarcode+"</a>",
						run.getResource().getName() + " - " + run.getResource().getResourceCategory().getName(),
						readlength,
						readType,
						dateRunStarted,
						dateRunEnded,
						statusForRun
				}));
				
				//for (RunMeta meta:runMeta) {//actually used??
				//	cellList.add(meta.getV());
				//}					 
				cell.put("cell", cellList);			 
				rows.add(cell);
			}
			 
			jqgrid.put("rows",rows);
			 
			return outputJSON(jqgrid, response); 	
			 
		} catch (Throwable e) {throw new IllegalStateException("Can't marshall to JSON " + runList,e);}
	
	}

	@RequestMapping(value = "/detail_rw/updateJSON.do", method = RequestMethod.POST)
	@PreAuthorize("hasRole('su') or hasRole('fm') or hasRole('ft')")
	public String updateDetailJSON(@RequestParam("id") Integer runId, Run runForm, ModelMap m, HttpServletResponse response) {
		boolean adding = (runId == null || runId.intValue() == 0);

		List<RunMeta> runMetaList = getMetaHelperWebapp().getFromJsonForm(request, RunMeta.class);

		runForm.setRunMeta(runMetaList);
			
		if (adding) {
			// To add new run to DB
			//check if Resource Name already exists in db; if 'true', do not allow to proceed.
			Map<String, String> queryMap = new HashMap<String, String>();
			queryMap.put("name", runForm.getName());
			List<Run> runList = this.runDao.findByMap(queryMap);
			if(runList!=null && runList.size()>0) {
				try{
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println(messageService.getMessage("run.run_exists.error"));
					return null;
				} catch (Throwable e) {
					throw new IllegalStateException("Cant output validation error "+messageService.getMessage("run.run_exists.error"),e);
				}
				
			}
			
			runForm.setUserId(Integer.parseInt(request.getParameter("start_esf_staff")));
			runForm.setLastUpdTs(new Date());
			runForm.setIsActive(1);
			
			Run runDb = this.runDao.save(runForm);
			runId = runDb.getRunId();
		} else {
			
			// editing run is not allowed
		}
		try {
			try {
				runMetaDao.setMeta(runMetaList, runId);
				response.getWriter().println(adding ? messageService.getMessage("run.created_success.label") 
						: messageService.getMessage("run.updated_success.label"));
				return null;
			} catch (MetadataException e1) {
				response.getWriter().println(messageService.getMessage("run.created_failure.label"));
				logger.warn(e1.getLocalizedMessage());
				return null;
			}

		} catch (Throwable e) {
			throw new IllegalStateException("Cant output success message ", e);
		}
	}

	@RequestMapping(value = "/detail/{strId}", method = RequestMethod.GET)
	public String detail(@PathVariable("strId") String strId, ModelMap m) {
		String now = (new Date()).toString();

		Integer i;
		try {
			i = new Integer(strId);
		} catch (Exception e) {
			return "default";
		}

		Run run = this.getRunDao().getById(i.intValue());

		List<RunMeta> runMetaList = run.getRunMeta();
		runMetaList.size();

		List<RunCell> runCellList = run.getRunCell();
		runCellList.size();

		List<RunFile> runFileList = run.getRunFile();
		runFileList.size();

		m.addAttribute("now", now);
		m.addAttribute("run", run);
		m.addAttribute("runmeta", runMetaList);
		m.addAttribute("runcell", runCellList);
		m.addAttribute("runfile", runFileList);

		return "run/detail";
	}

	@RequestMapping(value = "/lane/detail/{strRunId}/{strId}", method = RequestMethod.GET)
	public String laneDetail(@PathVariable("strRunId") String strRunId, @PathVariable("strId") String strId, ModelMap m) {
		String now = (new Date()).toString();

		Integer i;
		try {
			i = new Integer(strId);
		} catch (Exception e) {
			return "default";
		}

		@SuppressWarnings("unused")
		Integer runId;
		try {
			runId = new Integer(strRunId);
		} catch (Exception e) {
			return "default";
		}

		RunCell runCell = this.getRunCellDao().getById(i.intValue());

		//
		// TODO THROW EXCEPTION IF RUNID != RUNLANE.RUNID
		//

		List<RunCellFile> runCellFileList = runCell.getRunCellFile();
		runCellFileList.size();

		m.addAttribute("now", now);
		m.addAttribute("runcell", runCell);
		m.addAttribute("runcellfile", runCellFileList);

		return "run/lanedetail";
	}
	
	//helper method for createUpdateRun
	private List<SelectOptionsMeta> getResourceCategoryMetaList(ResourceCategory resourceCategory, String meta) {
		
		List<SelectOptionsMeta> list = new ArrayList<SelectOptionsMeta>();
		for(ResourceCategoryMeta rcm : resourceCategory.getResourceCategoryMeta()){
			if( rcm.getK().indexOf(meta) > -1 ){//such as readlength
				String[] tokens = rcm.getV().split(";");//rcm.getV() will be single:single;paired:paired
				for(String token : tokens){//token could be single:single
					String[] colonTokens = token.split(":");
					list.add(new SelectOptionsMeta(colonTokens[0], colonTokens[1]));							
				}
			}		
		}	
		return list;
	}
	
	//createUpdatePlatformunit - GET
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/createUpdateRun.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('su') or hasRole('ft')")
	public String createUpdateRun(@RequestParam("resourceId") Integer resourceId,
			@RequestParam("runId") Integer runId,
			@RequestParam("platformUnitId") Integer platformUnitId,
			@RequestParam(value="reset", defaultValue="") String reset,
			ModelMap m) {	
		
		if(platformUnitId.intValue()< 0){
			platformUnitId = new Integer(0);
		}
		if(resourceId.intValue()< 0){
			resourceId = new Integer(0);
		}
		if(runId.intValue()< 0){
			runId = new Integer(0);
		}
		
		
		try{
			Format formatter = new SimpleDateFormat("yyyy/MM/dd");
			String dateRunStarted = new String("");
			String dateRunEnded = new String("COMPLETED_BY_SYSTEM");
			Sample platformUnit = null; 
			platformUnit = sampleService.getPlatformUnit(platformUnitId);
			
			m.addAttribute("typeOfPlatformUnit", platformUnit.getSampleSubtype().getName());
			m.addAttribute("barcodeName", platformUnit.getSampleBarcode().get(0).getBarcode().getBarcode());
			m.addAttribute("numberOfCellsOnThisPlatformUnit", sampleService.getNumberOfIndexedCellsOnPlatformUnit(platformUnit).toString());
			
			String area = getMetaHelperWebappPlatformUnitInstance().getArea();
			String readlength = MetaHelperWebapp.getMetaValue(area, "readlength", platformUnit.getSampleMeta());
			m.addAttribute("readlength", readlength);
			String readType = MetaHelperWebapp.getMetaValue(area, "readType", platformUnit.getSampleMeta());
			m.addAttribute("readType", readType);	
			String comment = MetaHelperWebapp.getMetaValue(area, "comment", platformUnit.getSampleMeta());
			m.addAttribute("comment", comment);			
			
			List<Resource> resources = sampleService.getSequencingMachinesCompatibleWithPU(platformUnit);
			m.put("resources", resources);
			
			if(resourceId.intValue() > 0){
				
				
				Run runInstance = null;
				MetaHelperWebapp metaHelperWebapp = getMetaHelperWebappRunInstance();
				
				if(runId < 1){//most likely 0
					runInstance = new Run();
					//runInstance.setName("COMPLETED_BY_SYSTEM_" + platformUnit.getSampleBarcode().get(0).getBarcode().getBarcode());
					runInstance.setName("");
					//for testing the select box only runInstance.setUserId(new Integer(2));
					runInstance.setRunMeta(metaHelperWebapp.getMasterList(RunMeta.class));
				}
				else{
					
					runInstance = sampleService.getSequenceRun(runId);//throws exception if not valid mps Run in database 
					
					metaHelperWebapp.syncWithMaster(runInstance.getRunMeta());
					runInstance.setRunMeta((List<RunMeta>)metaHelperWebapp.getMetaList());
					
					dateRunStarted = new String(formatter.format(runInstance.getStartts()));//yyyy/MM/dd
					
					if(runInstance.getEnDts()!=null){
						try{
							dateRunEnded = new String(formatter.format(runInstance.getEnDts()));//yyyy/MM/dd
						}catch(Exception e){dateRunEnded=new String("UNEXPECTED PROBLEM WITH DATE");}
					}
					
					if(reset.equals("reset")){//reset permitted only when runId > 0
						resourceId = new Integer(runInstance.getResourceId().intValue());
					}
	
				}
				m.addAttribute(metaHelperWebapp.getParentArea(), runInstance);//metaHelperWebapp.getParentArea() is run
				
				Resource requestedSequencingMachine = sampleService.getSequencingMachineByResourceId(resourceId);
				m.addAttribute("readlengths", getResourceCategoryMetaList(requestedSequencingMachine.getResourceCategory(), "readlength"));
				m.addAttribute("readTypes", getResourceCategoryMetaList(requestedSequencingMachine.getResourceCategory(), "readType"));

				m.addAttribute("technicians", userService.getFacilityTechnicians());
				m.addAttribute("dateRunStarted", dateRunStarted);
				m.addAttribute("dateRunEnded", dateRunEnded);
			}
			
			m.addAttribute("runId", runId);
			m.addAttribute("resourceId", resourceId);
			m.addAttribute("platformUnitId", platformUnit.getSampleId().toString());			
			
		}catch(Exception e){logger.warn(e.getMessage());waspErrorMessage("wasp.unexpected_error.error"); return "redirect:/facility/platformunit/showPlatformUnit/"+platformUnitId.intValue()+".do";  /*return "redirect:/dashboard.do";*/}

		//return "redirect:/dashboard.do";
		return "run/createUpdateRun";

	}

	//createUpdatePlatformunit - Post
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/createUpdateRun.do", method=RequestMethod.POST)
	@PreAuthorize("hasRole('su') or hasRole('ft')")
	public String createUpdatePlatformUnitPost(
			@RequestParam("resourceId") Integer resourceId,
			@RequestParam("runId") Integer runId,
			@RequestParam("platformUnitId") Integer platformUnitId,
			@RequestParam("dateRunStarted") String dateRunStarted,
			@RequestParam("dateRunEnded") String dateRunEnded,
			@Valid Run runInstance, 
			 BindingResult result,
			 SessionStatus status, 		
			ModelMap m) throws MetadataException {
	
		logger.debug("Inside the createUpdateRun POST");
		//if(1==1){return "redirect:/dashboard.do";}
		
		try{
			
			String action = null;
			if(resourceId==null || resourceId.intValue()<0 || runId == null || runId.intValue()<0 || platformUnitId==null || platformUnitId.intValue()<=0){
				throw new Exception("Unexpected parameter problems 1: createUpdateRun - POST");
			}
			else if(runId.intValue()==0 && (runInstance.getRunId()==null || runInstance.getRunId().intValue()==0)){
				action = new String("create");
				logger.debug("create new run");
			}
			else if(runId.intValue()>0 && runInstance.getRunId()!=null && runInstance.getRunId().intValue()>0 && runId.intValue()==runInstance.getRunId().intValue()){
				action = new String("update");
				logger.debug("update existing run");
			}			
			else{
				throw new Exception("Unexpected parameter problems 2: createUpdateRun - POST");
			}
			
			MetaHelperWebapp metaHelperWebapp = getMetaHelperWebappRunInstance();
			metaHelperWebapp.getFromRequest(request, RunMeta.class);
			metaHelperWebapp.validate(result);

			boolean otherErrorsExist = false;
			
			 
			//note that @Valid should have  checked for name being the empty, 
			//but it doesn't appear to be working, so I'll test directly) 
			
			if(runInstance.getName().isEmpty() || runInstance.getName().trim().isEmpty()){
				Errors errors=new BindException(result.getTarget(), metaHelperWebapp.getParentArea());
				errors.rejectValue("name", metaHelperWebapp.getArea()+".name.error", metaHelperWebapp.getArea()+".name.error");
				result.addAllErrors(errors);
			}
			if (! result.hasFieldErrors("name")){//also check whether run's name has been used
				Map<String,String> queryMap = new HashMap<String,String>();
				queryMap.put("name", runInstance.getName().trim());
				List<Run> runList = runDao.findByMap(queryMap);
				if(runList.size()>0){
					Errors errors=new BindException(result.getTarget(), metaHelperWebapp.getParentArea());
					errors.rejectValue("name", metaHelperWebapp.getArea()+".name_exists.error", metaHelperWebapp.getArea()+".name_exists.error");
					result.addAllErrors(errors);
				}
			}
			
			
			Date dateRunStartedAsDateObject = null;
			
			//check for runInstance.UserId, which cannot be empty 		
			if(runInstance.getUserId()==null || runInstance.getUserId().intValue()<=0){
				String msg = messageService.getMessage(metaHelperWebapp.getArea()+".technician.error");//area here is runInstance
				m.put("technicianError", msg==null?new String("Technician cannot be empty."):msg);
				otherErrorsExist = true;
			}
			//check dateRunStarted
			if(dateRunStarted==null || "".equals(dateRunStarted.trim())){
				String msg = messageService.getMessage(metaHelperWebapp.getArea()+".dateRunStarted.error");//area here is runInstance
				m.put("dateRunStartedError", msg==null?new String("Cannot be empty."):msg);
				otherErrorsExist = true;
			}			
			else{
				try{
		
					Format formatter = new SimpleDateFormat("yyyy/MM/dd");
					dateRunStartedAsDateObject = (Date) formatter.parseObject(dateRunStarted.trim()); 				
				}catch(Exception e){
					String msg = messageService.getMessage(metaHelperWebapp.getArea()+".dateRunStartedFormat.error");//area here is runInstance
					m.put("dateRunStartedError", msg==null?new String("Incorrect Format"):msg);
					otherErrorsExist = true;
				}
			}
			
			
			if (result.hasErrors()||otherErrorsExist){
				
				logger.debug("We see some errors");
				if(otherErrorsExist){logger.debug("other errors exist");}else{logger.debug("other errors DO NOT exist");}
				
				//first deal with filling up info about the platformunit displayed on the left
				Sample platformUnit = null; 
				platformUnit = sampleService.getPlatformUnit(platformUnitId);
				
				m.addAttribute("typeOfPlatformUnit", platformUnit.getSampleSubtype().getName());
				m.addAttribute("barcodeName", platformUnit.getSampleBarcode().get(0).getBarcode().getBarcode());
				m.addAttribute("numberOfCellsOnThisPlatformUnit", sampleService.getNumberOfIndexedCellsOnPlatformUnit(platformUnit).toString());
				
				String area = getMetaHelperWebappPlatformUnitInstance().getArea();
				String readlength = MetaHelperWebapp.getMetaValue(area, "readlength", platformUnit.getSampleMeta());
				m.addAttribute("readlength", readlength);
				String readType = MetaHelperWebapp.getMetaValue(area, "readType", platformUnit.getSampleMeta());
				m.addAttribute("readType", readType);	
				String comment = MetaHelperWebapp.getMetaValue(area, "comment", platformUnit.getSampleMeta());
				m.addAttribute("comment", comment);			
							
				//now the run
				//fill up the metadata for the run
				runInstance.setRunMeta((List<RunMeta>) metaHelperWebapp.getMetaList());				
				//DO I NEED THIS Next line??? It seems to be sent back automagically, even if the next line is missing (next line added 10-10-12)
				m.addAttribute(metaHelperWebapp.getParentArea(), runInstance);//metaHelperWebapp.getParentArea() is run

				List<Resource> resources = sampleService.getSequencingMachinesCompatibleWithPU(platformUnit);
				m.put("resources", resources);

				Resource requestedSequencingMachine = sampleService.getSequencingMachineByResourceId(resourceId);
				m.addAttribute("readlengths", getResourceCategoryMetaList(requestedSequencingMachine.getResourceCategory(), "readlength"));
				m.addAttribute("readTypes", getResourceCategoryMetaList(requestedSequencingMachine.getResourceCategory(), "readType"));

				m.addAttribute("technicians", userService.getFacilityTechnicians());
				m.addAttribute("dateRunStarted",dateRunStarted);
				m.addAttribute("dateRunEnded",dateRunEnded);
								
				m.addAttribute("runId", runId);
				m.addAttribute("resourceId", resourceId);
				m.addAttribute("platformUnitId", platformUnit.getSampleId().toString());
				
				return "run/createUpdateRun";				
				
			}//end of if errors
			
			//should really confirm resource is OK, platformunit is ok, resource is OK for the flow cell
			if(action.equals("create")){
				//logger.debug("in create1");
				//if create, then set startts to the date in the parameter (currently that parameter does not exit)
				runInstance.setStartts(dateRunStartedAsDateObject);
				sampleService.createUpdateSequenceRun(runInstance, (List<RunMeta>)metaHelperWebapp.getMetaList(), platformUnitId, resourceId);
				waspMessage("runInstance.created_success.label");
			}
			else if(action.equals("update")){
				//logger.debug("in update1");
				runInstance.setStartts(dateRunStartedAsDateObject);
				sampleService.createUpdateSequenceRun(runInstance, (List<RunMeta>)metaHelperWebapp.getMetaList(), platformUnitId, resourceId);
				waspMessage("runInstance.updated_success.label");
			}
			else{//action == null
				//logger.debug("in Unexpectedly1");
				throw new Exception("Unexpectedly encountered action whose value is neither create or update in createUpdateRun");
			}
			//logger.debug("end of the POST method");	
			
		}catch(Exception e){logger.warn(e.getMessage());waspErrorMessage("wasp.unexpected_error.error"); return "redirect:/facility/platformunit/showPlatformUnit/"+platformUnitId.intValue()+".do";  /*return "redirect:/dashboard.do";*/}

		return "redirect:/facility/platformunit/showPlatformUnit/"+platformUnitId.intValue()+".do";  /*return "redirect:/dashboard.do";*/
	}
	
	//createUpdatePlatformunit - GET
	@RequestMapping(value="/deleteRun.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('su') or hasRole('ft')")
	public String deleteRun(@RequestParam("runId") Integer runId,
			ModelMap m) {
		try{
			Run run = sampleService.getSequenceRun(runId);//exception if not msp run or not in db
			sampleService.deleteSequenceRun(run);
			return "redirect:/facility/platformunit/showPlatformUnit/"+run.getSampleId().intValue()+".do";
		}catch(Exception e){logger.warn(e.getMessage());waspErrorMessage("wasp.unexpected_error.error");return "redirect:/dashboard.do";}
	}
}

//comparators - need to be moved
class RunNameComparator implements Comparator<Run> {
	@Override
	public int compare(Run arg0, Run arg1) {
		return arg0.getName().compareToIgnoreCase(arg1.getName());
	}
}
class RunStatusComparator implements Comparator<Run> {
	@Override
	public int compare(Run arg0, Run arg1) {
		////return arg0.getStatus().compareToIgnoreCase(arg1.getStatus());
		return 1;
	}
}
class MachineNameComparator implements Comparator<Run> {
	@Override
	public int compare(Run arg0, Run arg1) {
		return arg0.getResource().getName().compareToIgnoreCase(arg1.getResource().getName());
	}
}
class RunPlatformUnitBarcodeComparator implements Comparator<Run> {
	@Override
	public int compare(Run arg0, Run arg1) {
		return arg0.getPlatformUnit().getSampleBarcode().get(0).getBarcode().getBarcode().compareToIgnoreCase(arg1.getPlatformUnit().getSampleBarcode().get(0).getBarcode().getBarcode());
	}
}
class DateRunEndedComparator implements Comparator<Run> {
	@Override
	public int compare(Run arg0, Run arg1) {
		Date date0 = arg0.getEnDts()==null?new Date(0):arg0.getEnDts();
		Date date1 = arg1.getEnDts()==null?new Date(0):arg1.getEnDts();
		
		return date0.compareTo(date1);
	}
}
class RunMetaIsStringComparator implements Comparator<Run> {
	
	String metaKey;
	
	RunMetaIsStringComparator(String metaKey){
		this.metaKey = new String(metaKey);
	}
	@Override
	public int compare(Run arg0, Run arg1) {
		
		String metaValue0 = null;
		String metaValue1 = null;
		
		List<RunMeta> metaList0 = arg0.getRunMeta();
		for(RunMeta rm : metaList0){
			if(rm.getK().indexOf(metaKey) > -1){
				metaValue0 = new String(rm.getV());
				break;
			}
		}		
		
		List<RunMeta> metaList1 = arg1.getRunMeta();
		for(RunMeta rm : metaList1){
			if(rm.getK().indexOf(metaKey) > -1){
				metaValue1 = new String(rm.getV());
				break;
			}
		}
		
		if(metaValue0==null){metaValue0=new String("");} 
		if(metaValue1==null){metaValue1=new String("");}
		
		return metaValue0.compareToIgnoreCase(metaValue1);
	}
}

