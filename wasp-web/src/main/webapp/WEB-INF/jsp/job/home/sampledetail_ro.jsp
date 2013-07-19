<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<c:import url="/WEB-INF/jsp/job/home/fadingMessage.jsp" />

<a class="button" href="javascript:void(0);"  onclick='loadNewPageWithAjax("<c:url value="/job/${job.getId()}/samples.do" />");' >Back To: Samples, Libraries &amp; Runs</a>
<br />
<br />
<table class="EditTable ui-widget ui-widget-content">
  	<tr class="FormData"><td class="CaptionTD"><fmt:message key="sampledetail_ro.sampleName.label" />:</td><td class="DataTD"><c:out value="${sample.name}" /></td></tr>
  	<tr class="FormData"><td class="CaptionTD"><fmt:message key="sampledetail_ro.sampleType.label" />:</td><td class="DataTD"><c:out value="${sample.sampleType.name}" /></td></tr>
     <c:set var="_area" value = "sample" scope="request"/>
	 <c:set var="_metaList" value = "${normalizedSampleMeta}" scope="request" />		
     <c:import url="/WEB-INF/jsp/job/home/meta_ro.jsp"/>
    <tr class="FormData"><td colspan="2" class="submitBottom"><a class="button" href="javascript:void(0);" onclick='loadNewPageWithAjax("<c:url value="/job/${job.getId()}/samples.do" />");' ><fmt:message key="sampledetail_ro.cancel.label" /></a>&nbsp;
		<sec:authorize access="hasRole('su') or hasRole('ft')"> 
	  		<a class="button" href="javascript:void(0);" onclick='loadNewPageWithAjax("<c:url value="/job/${job.getId()}/sample/${sample.getId()}/sampledetail_rw.do" />");' ><fmt:message key="sampledetail_ro.edit.label" /></a>
	 	</sec:authorize>	
	</td></tr>
</table>
<br /><br />

