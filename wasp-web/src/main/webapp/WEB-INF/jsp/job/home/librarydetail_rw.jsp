<%@ include file="/WEB-INF/jsp/taglib.jsp" %>
<br />

<%--  TODO: Declare style in css file (e.g. /src/main/webapp/css/base.css), not in .jsp and reuse where possible !!!! --%>

<a class="button" href="javascript:void(0);"  onclick='loadNewPageWithAjax("<wasp:relativeUrl value="job/${job.getId()}/samples.do" />");' ><fmt:message key="jobHomeLibraryDetailRW.backTo.label" /></a>
<br /><br /><br />
<form:form   cssClass="FormGrid" commandName="sample" method='post' name='editLibraryForm' id='editLibraryFormId' action="" >
	<table class="EditTable ui-widget ui-widget-content">
	 	<tr class="FormData"><td colspan="3" class="label-centered" style="font-weight:bold;text-decoration:underline"><fmt:message key="librarydetail_rw.libraryDetails.label" /></td></tr>
		<form:hidden path='sampleSubtypeId' />
		<form:hidden path='sampleTypeId' />	 
		 <tr class="FormData">
	      <td class="CaptionTD"><fmt:message key="librarydetail_rw.libraryName.label" />:</td>
	      <td class="DataTD"><form:input cssClass="FormElement ui-widget-content ui-corner-all" path="name" /><span class="requiredField">*</span></td>
	      <td class="CaptionTD error"><form:errors path="name" /></td>
	     </tr>
	     <tr class="FormData"><td class="CaptionTD"><fmt:message key="librarydetail_rw.id.label" />:</td><td class="DataTD"><c:out value="${sample.getId()}" /></td><td class="CaptionTD error">&nbsp;</td></tr>
	     <tr class="FormData"><td class="CaptionTD"><fmt:message key="librarydetail_rw.libraryType.label" />:</td><td class="DataTD">Library</td><td>&nbsp;</td></tr>
	     <c:set var="_area" value = "sample" scope="request"/>
		 <c:set var="_metaList" value = "${sample.getSampleMeta()}" scope="request" />		
	     <c:import url="/WEB-INF/jsp/job/home/meta_rw.jsp"/>
	     <sec:authorize access="hasRole('su') or hasRole('ft')">
	    	<tr class="FormData">
			    <td colspan="3" align="left" class="submitBottom">
		        	<a class="button" href="javascript:void(0);"  onclick='loadNewPageWithAjax("<wasp:relativeUrl value="job/${job.getId()}/library/${sample.getId()}/librarydetail_ro.do" />");' ><fmt:message key="sampledetail_rw.cancel.label" /></a>
		        	<a class="button" href="javascript:void(0);"  onclick='loadNewPageWithAjax("<wasp:relativeUrl value="job/${job.getId()}/library/${sample.getId()}/librarydetail_rw.do" />");' ><fmt:message key="jobHomeLibraryDetailRW.reset.label" /></a>
					<a class="button" href="javascript:void(0);"  onclick='postFormWithAjaxJson("editLibraryFormId","<wasp:relativeUrl value="job/${job.getId()}/library/${sample.getId()}/librarydetail_rw.do" />"); return false;' ><fmt:message key="sampledetail_rw.save.label" /></a>	
			    </td>
			</tr>	    
	     </sec:authorize>
	</table>
</form:form>
<br />
