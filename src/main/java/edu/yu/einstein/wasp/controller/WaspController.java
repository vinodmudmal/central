package edu.yu.einstein.wasp.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.ResourceBundle;
import java.util.Locale;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import edu.yu.einstein.wasp.model.User;
import edu.yu.einstein.wasp.service.UserService;

import edu.yu.einstein.wasp.model.MetaAttribute;
import edu.yu.einstein.wasp.model.MetaAttribute.Country;
import edu.yu.einstein.wasp.model.MetaAttribute.State;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import edu.yu.einstein.wasp.taglib.MessageTag;

@Controller
public class WaspController {

  protected final Logger logger = Logger.getLogger(getClass());

  protected static final Map<String, String> LOCALES=new TreeMap<String, String>();
  static { LOCALES.put("en_US","English");
                LOCALES.put("iw_IL","Hebrew");
                LOCALES.put("ru_RU","Russian");
                LOCALES.put("ja_JA","Japanese");
                }

  protected static final ResourceBundle BASE_BUNDLE=ResourceBundle.getBundle("messages", Locale.ENGLISH);


  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  protected UserService userService;

  @Autowired
  HttpServletRequest request;

  @Autowired
  private BeanValidator validator;

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.setValidator(validator);
  }


  public User getAuthenticatedUser() { 
    Authentication authentication = SecurityContextHolder.getContext()
                                .getAuthentication();

    User user = this.userService.getUserByLogin(authentication.getName());

    return user;
  }

  public String[] getRoles() {
    Authentication authentication = SecurityContextHolder.getContext()
                                .getAuthentication();

    // java.util.Collection<GrantedAuthority> col = authentication.getAuthorities();
    java.util.Collection col = authentication.getAuthorities();
    String[] roles = new String[col.size()];
    int i = 0;
    for (Iterator<GrantedAuthority> it = col.iterator(); it.hasNext();) {

      GrantedAuthority grantedAuthority = it.next();
      roles[i] = grantedAuthority.getAuthority();
      i++;
    }
    return roles;
  }


  protected void prepareSelectListData(ModelMap m) {
    m.addAttribute("countries", Country.getList());
    m.addAttribute("states", State.getList());
    m.addAttribute("locales", LOCALES);
  }

  protected String getMessage(String key) {
    String message=(String)getBundle().getObject(key);
    return message;
  }

  protected ResourceBundle getBundle() {
    Locale locale=(Locale)request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);

    if (locale==null) locale=Locale.ENGLISH;

    ResourceBundle bundle=ResourceBundle.getBundle("messages",locale);

    return bundle;
  }


  public void doReauth() {
    SecurityContext securityContext= SecurityContextHolder.getContext();
    Authentication currentUser = securityContext.getAuthentication();
    UserDetails currentUserDetails = (UserDetails) currentUser.getPrincipal();

    UserDetails u = userDetailsService.loadUserByUsername(currentUserDetails.getUsername());

    UsernamePasswordAuthenticationToken newToken = new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword());

    SecurityContextHolder.getContext().setAuthentication(newToken);

  }

  public void waspMessage(String propertyString) {
    MessageTag.addMessage(request.getSession(), propertyString);
  }


}
