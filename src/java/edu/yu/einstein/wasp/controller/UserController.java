package edu.yu.einstein.wasp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import edu.yu.einstein.wasp.model.MetaAttribute;
import edu.yu.einstein.wasp.model.MetaAttribute.Country;
import edu.yu.einstein.wasp.model.MetaAttribute.State;
import edu.yu.einstein.wasp.model.MetaUtil;
import edu.yu.einstein.wasp.model.User;
import edu.yu.einstein.wasp.model.Usermeta;
import edu.yu.einstein.wasp.service.EmailService;
import edu.yu.einstein.wasp.service.UserService;
import edu.yu.einstein.wasp.service.UsermetaService;
import edu.yu.einstein.wasp.taglib.MessageTag;

@Controller
@Transactional
@RequestMapping("/user")
public class UserController extends WaspController {

	@Autowired
	private UserService userService;

	@Autowired
	private UsermetaService usermetaService;

	@Autowired
	private BeanValidator validator;
	
	@Autowired
	private EmailService emailService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@Autowired
	HttpServletRequest request;

	public static final MetaAttribute.Area AREA = MetaAttribute.Area.user;

	private static final Map<String, String> LOCALES=new TreeMap<String, String>();
	static {
		LOCALES.put("en_US","English");
		LOCALES.put("iw_IL","Hebrew");	
		LOCALES.put("ru_RU","Russian");
		LOCALES.put("ja_JA","Japanese");
		}
	

	@RequestMapping("/list")
	@PreAuthorize("hasRole('god')")
	public String list(ModelMap m) {
		List<User> userList = this.userService.findAll();

		m.addAttribute(AREA.name(), userList);

		return "user/list";
	}

	@RequestMapping(value = "/create/form.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god')")
	public String showEmptyForm(ModelMap m) {
	
		User user = new User();

		user.setUsermeta(MetaUtil.getMasterList(Usermeta.class, AREA));
		
		m.addAttribute(AREA.name(), user);
		
		prepareSelectListData(m);		

		return AREA + "/detail_rw";
	}
	
	
	@RequestMapping(value = "/create/form.do", method = RequestMethod.POST)
	@PreAuthorize("hasRole('god')")
	public String create(@Valid User userForm, BindingResult result,
			SessionStatus status, ModelMap m) {

		List<Usermeta> usermetaList = MetaUtil.getMetaFromForm(request,
				AREA, Usermeta.class);
		
		MetaUtil.setAttributesAndSort(usermetaList, AREA);

		userForm.setUsermeta(usermetaList);

		// manually validate login and password
		Errors errors = new BindException(result.getTarget(), AREA.name());
		if (userForm.getLogin() == null || userForm.getLogin().isEmpty()) {
			errors.rejectValue("login", "user.login.error");
		} else {
			User user = userService.getUserByLogin(userForm.getLogin());
			if (user != null && user.getLogin() != null) {
				errors.rejectValue("login", "user.login.exists_error");
			}
		}

		if (userForm.getPassword() == null || userForm.getPassword().isEmpty()) {
			errors.rejectValue("password", "user.password.error");
		}

		result.addAllErrors(errors);

		List<String> validateList = new ArrayList<String>();

		validateList.add("login");
		validateList.add(MetaValidator.Constraint.NotEmpty.name());
		validateList.add("password");
		validateList.add(MetaValidator.Constraint.NotEmpty.name());

		for (Usermeta meta : usermetaList) {
			if (meta.getProperty() != null
					&& meta.getProperty().getConstraint() != null) {
				validateList.add(meta.getK());
				validateList.add(meta.getProperty().getConstraint());
			}
		}
		MetaValidator validator = new MetaValidator(
				validateList.toArray(new String[] {}));

		validator.validate(usermetaList, result, AREA);

		if (result.hasErrors()) {
			prepareSelectListData(m);
			MessageTag.addMessage(request.getSession(), "user.created.error");
			return "user/detail_rw";
		}

		userForm.setLastUpdTs(new Date());

		//PasswordEncoder encoder = new Md5PasswordEncoder();
		PasswordEncoder encoder = new ShaPasswordEncoder();
		String hashedPass = encoder.encodePassword(userForm.getPassword(), null);
		userForm.setPassword(hashedPass);
		
		userForm.setIsActive(1);
		
		User userDb = this.userService.save(userForm);
		
		for (Usermeta um : usermetaList) {
			um.setUserId(userDb.getUserId());
		}
		;

		usermetaService.updateByUserId(userDb.getUserId(), usermetaList);

		status.setComplete();

		MessageTag.addMessage(request.getSession(), "user.created.success");
		
		return "redirect:/user/detail_rw/" + userDb.getUserId() + ".do";
	}

	@RequestMapping(value = "/detail_rw/{userId}.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('god')")
	public String detailRW(@PathVariable("userId") Integer userId, ModelMap m) {		
		return detail(userId,m,true);
	}
	
	@RequestMapping(value = "/detail_ro/{userId}.do", method = RequestMethod.GET)
	
	public String detailRO(@PathVariable("userId") Integer userId, ModelMap m) {
		
		String lang=Locale.JAPANESE.getLanguage();
		String c=Locale.JAPANESE.getCountry();
		return detail(userId,m,false);
	}
	
	private String detail(Integer userId, ModelMap m,boolean isRW) {

		User user = this.userService.getById(userId);

		user.setUsermeta(MetaUtil.syncWithMaster(user.getUsermeta(), AREA, Usermeta.class));
		
		MetaUtil.setAttributesAndSort(user.getUsermeta(), AREA);

		m.addAttribute(AREA.name(), user);
		
		prepareSelectListData(m);
		
		return isRW?"user/detail_rw":"user/detail_ro";
	}
	
	@RequestMapping(value = "/me.do", method = RequestMethod.GET)
	public String myDetail(ModelMap m) {
		User user = this.getAuthenticatedUser();		
		return this.detailRW(user.getUserId(), m);
	}

	@RequestMapping(value = "/me.do", method = RequestMethod.POST)
	public String updateDetail(@Valid User userForm, BindingResult result,
			SessionStatus status, ModelMap m) {
		User user = this.getAuthenticatedUser();		

		updateDetail(user.getUserId(), userForm, result, status, m);

		return "redirect:" + "me.do";
	}
	
	@RequestMapping(value = "/detail_rw/{userId}.do", method = RequestMethod.POST)
	@PreAuthorize("hasRole('god') or User.login == principal.name")
	public String updateDetail(@PathVariable("userId") Integer userId,
			@Valid User userForm, BindingResult result, SessionStatus status,
			ModelMap m) {

		List<Usermeta> usermetaList = MetaUtil.getMetaFromForm(request,
				AREA, Usermeta.class);

		for (Usermeta meta : usermetaList) {
			meta.setUserId(userId);
		}

		MetaUtil.setAttributesAndSort(usermetaList, AREA);
		
		userForm.setUsermeta(usermetaList);

		List<String> validateList = new ArrayList<String>();

		for (Usermeta meta : usermetaList) {
			if (meta.getProperty() != null
					&& meta.getProperty().getConstraint() != null) {
				validateList.add(meta.getK());
				validateList.add(meta.getProperty().getConstraint());
			}
		}

		MetaValidator validator = new MetaValidator(
				validateList.toArray(new String[] {}));

		validator.validate(usermetaList, result, AREA);

		if (result.hasErrors()) {
			userForm.setUserId(userId);
			prepareSelectListData(m);
			MessageTag.addMessage(request.getSession(), "user.updated.error");
			return "user/detail_rw";
		}

		User userDb = this.userService.getById(userId);
		userDb.setFirstName(userForm.getFirstName());
		userDb.setLastName(userForm.getLastName());
		if (userForm.getPassword()!=null && !userForm.getPassword().trim().isEmpty()) {
			PasswordEncoder encoder = new ShaPasswordEncoder();
			String hashedPass = encoder.encodePassword(userForm.getPassword(), null);
			userForm.setPassword(hashedPass);
		}
		userDb.setEmail(userForm.getEmail());
		userDb.setLocale(userForm.getLocale());

		userDb.setLastUpdTs(new Date());

		this.userService.merge(userDb);

		usermetaService.updateByUserId(userId, usermetaList);

		MimeMessageHelper a;
		
		status.setComplete();

		MessageTag.addMessage(request.getSession(), "user.updated.success");
		
		//emailService.sendNewPassword(userDb, "new pass");
		
		return "redirect:" + userId + ".do";
	}

	@RequestMapping(value = "/mypassword.do", method = RequestMethod.GET)
	public String myPasswordForm(ModelMap m) {
		return "user/mypassword";
	}

	@RequestMapping(value = "/mypassword.do", method = RequestMethod.POST)
	public String myPassword(
			@RequestParam(value = "passwordold") String passwordold,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "password2") String password2, ModelMap m) {
		return "redirect:/dashboard.do";
	}
	
	private void prepareSelectListData(ModelMap m) {
		m.addAttribute("countries", Country.getList());
		m.addAttribute("states", State.getList());
		m.addAttribute("locales", LOCALES);		
	}
	

}