package edu.yu.einstein.wasp.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/*
 * Builds jqGrid-compatible JavaScript to delete selected row on server side
 * 
 * @Author Sasha Levchuk
 */
public class JQDeleteTag extends BodyTagSupport {
	
	private String url;

	public void setUrl(String url) {
		this.url = url;
	}



	public void release() {
		url=null;
	}
	


	public int doStartTag() throws javax.servlet.jsp.JspException {
		
		
		String buf=
			"_navAttr.delfunc=null;\n"+
		"_delAttr=\n"+
		"{ // Delete parameters\n"+
		 "       ajaxDelOptions: { contentType: 'application/json' },\n"+
		 "       mtype: 'DELETE',\n"+
		 "       serializeDelData: function () {\n"+
		 "           return ''; \n"+
		 "       },\n"+
		 "       onclickSubmit: function (params, postdata) {\n"+        	
		 "           params.url = '"+url+"?id=' + encodeURIComponent(postdata);\n"+
		 "       },\n"+
		 "       afterSubmit: _afterSubmit\n"+
		"};\n";
		
		try {
			this.pageContext.getOut().print(buf);	
		} catch (Throwable e) {			
			throw new JspTagException(e.getMessage(),e);
		}
		
		
		return BodyTagSupport.EVAL_PAGE;
	}
	

}

