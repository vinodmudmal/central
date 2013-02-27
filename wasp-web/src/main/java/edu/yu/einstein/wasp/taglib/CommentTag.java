package edu.yu.einstein.wasp.taglib;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import edu.yu.einstein.wasp.resourcebundle.DBResourceBundle;
import edu.yu.einstein.wasp.web.Tooltip;

/**
 * Displays comment icon and provides comment
 * @author asmclellan
 *
 */
public class CommentTag extends BodyTagSupport {
	
	
	private static final long serialVersionUID = -7313415827087355691L;

	Logger log = LoggerFactory.getLogger(CommentTag.class);
	
	private String commentText;
	
	public void setKey(String key){
		HttpSession session=((HttpServletRequest) this.pageContext.getRequest()).getSession();
		if (session == null)
			return;
		Locale locale=(Locale) session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		if (locale==null) 
			locale=Locale.US;
		this.commentText = DBResourceBundle.MESSAGE_SOURCE.getMessage(key, null,locale);
	}
	
	public void setValue(String value){
		this.commentText = value;
	}
	
		
	@Override
	public int doStartTag() throws javax.servlet.jsp.JspException {
		if (commentText == null) 
			return Tag.SKIP_BODY;
		
		StringBuffer buf=new StringBuffer("");
		buf.append(Tooltip.getCommentHtmlString(commentText));
		
		try {
			this.pageContext.getOut().print(buf.toString());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		return Tag.EVAL_PAGE;
	}
	
}