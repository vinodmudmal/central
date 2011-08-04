package edu.yu.einstein.wasp.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/*
 * Displays localized messages using list of keys stored in session under name "_feedback"
 * 
 * @Author Sasha Levchuk
 */
public class MessageTag extends BodyTagSupport {
	
	Logger log=Logger.getLogger(MessageTag.class);
	
	public static final String FEEDBACK_SESSION_ATTRIBUTE_NAME="_feedback";
	
	public static final void addMessage(HttpSession session,String key) {
		
		List<String> messageKeys=(List<String>)session.getAttribute(FEEDBACK_SESSION_ATTRIBUTE_NAME);		
		
		if (messageKeys==null) {
			messageKeys=new ArrayList<String>();
		} 
		 messageKeys.add(key);		
		 
		 session.setAttribute(FEEDBACK_SESSION_ATTRIBUTE_NAME,messageKeys);
	}
	
	public int doStartTag() throws javax.servlet.jsp.JspException {
	
		HttpSession session=((HttpServletRequest)this.pageContext.getRequest()).getSession();
		
		if (session==null) return BodyTagSupport.SKIP_BODY;
		
		List<String> messageKeys=(List<String>)session.getAttribute(FEEDBACK_SESSION_ATTRIBUTE_NAME);
		
		if (messageKeys==null || messageKeys.isEmpty()) return BodyTagSupport.SKIP_BODY;
		
		Locale locale=(Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		
		if (locale==null) locale=Locale.ENGLISH;
		
		ResourceBundle BASE_BUNDLE=ResourceBundle.getBundle("messages",locale);
		
		StringBuffer buf=new StringBuffer("");

		for(String key:messageKeys) {
			try {
				String message=BASE_BUNDLE.getString(key);
				if (buf.length()>0) buf.append("</br>\n");
				buf.append(message);
			} catch (Throwable e) {
				log.error("Cant get message by key "+key,e);
			}
		}
		
		session.removeAttribute(FEEDBACK_SESSION_ATTRIBUTE_NAME);
		
		try {
			this.pageContext.getOut().print(buf.toString());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		
		
		return BodyTagSupport.EVAL_PAGE;
	}
	
}
