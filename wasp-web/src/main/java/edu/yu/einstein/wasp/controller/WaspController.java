package edu.yu.einstein.wasp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import edu.yu.einstein.wasp.dao.DepartmentDao;
import edu.yu.einstein.wasp.dao.UserDao;
import edu.yu.einstein.wasp.model.Department;
import edu.yu.einstein.wasp.model.MetaAttribute.Country;
import edu.yu.einstein.wasp.model.MetaAttribute.State;
import edu.yu.einstein.wasp.taglib.ErrorMessageTag;
import edu.yu.einstein.wasp.taglib.MessageTag;

@Controller
public class WaspController {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  
 public  static final Map<String, String> LOCALES=new LinkedHashMap<String, String>();
  static { 
	  LOCALES.put("en_US","English");
      LOCALES.put("iw_IL","Hebrew");
      LOCALES.put("ru_RU","Russian");
      LOCALES.put("ja_JA","Japanese");
  }


  @Autowired
  private DepartmentDao departmentDao;
    
  public DepartmentDao getDepartmentDao() {
	return departmentDao;
  }
  
@Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  protected UserDao userDao;

  @Autowired
  protected HttpServletRequest request;

  @Autowired
  private BeanValidator validator;


  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.setValidator(validator);
  }


  protected void prepareSelectListData(ModelMap m) {
    m.addAttribute("countries", Country.getList());
    m.addAttribute("states", State.getList());
    m.addAttribute("locales", LOCALES);
    m.addAttribute("departments", strip(departmentDao.findAll()));
    //m.addAttribute("deptDao", departmentDao);
   
  }

  private List<Department> strip(List<Department> in) {
	  if (in==null) return null;
	  
	  List<Department> result=new ArrayList<Department>();
	  for(Department d:in) {
		  Department dep=new Department();
		  dep.setDepartmentId(d.getDepartmentId());
		  dep.setName(d.getName());
		  result.add(dep);
	  }
	  return result;
	  
  }

  public void doReauth() {
    SecurityContext securityContext= SecurityContextHolder.getContext();
    Authentication currentUser = securityContext.getAuthentication();
    UserDetails currentUserDetails = (UserDetails) currentUser.getPrincipal();

    UserDetails u = userDetailsService.loadUserByUsername(currentUserDetails.getUsername());

    UsernamePasswordAuthenticationToken newToken = new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword());

    SecurityContextHolder.getContext().setAuthentication(newToken);

  }

  public void waspMessage(String propertyString)   {
    MessageTag.addMessage(request.getSession(), propertyString);
  }
  
  public void waspErrorMessage(String propertyString)   {
	    ErrorMessageTag.addMessage(request.getSession(), propertyString);
  }

  protected String outputJSON(Map jqgridMap, HttpServletResponse response) throws JsonMappingException, IOException {
	  
	  	ObjectMapper mapper = new ObjectMapper();
	  
	  	String json=mapper.writeValueAsString(jqgridMap);
		 
		response.setContentType("application/json;charset=UTF-8");
		
		response.getWriter().print(json);
		 
		return null;		
  }
  protected String outputJSON(List list, HttpServletResponse response) throws JsonMappingException, IOException {
	  
	  	ObjectMapper mapper = new ObjectMapper();
	  
	  	String json=mapper.writeValueAsString(list);
		 
		response.setContentType("application/json;charset=UTF-8");
		
		response.getWriter().print(json);
		 
		return null;		
}
  
  protected String outputJSON(String string, HttpServletResponse response) throws JsonMappingException, IOException {
	  
	  	ObjectMapper mapper = new ObjectMapper();
	  
	  	String json=mapper.writeValueAsString(string);
		 
		response.setContentType("application/json;charset=UTF-8");
		
		response.getWriter().print(json);
		 
		return null;		
}

}
