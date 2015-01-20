<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<%--  TODO: Declare style in css file (e.g. /src/main/webapp/css/base.css), not in .jsp and reuse where possible !!!! --%>

<h1><fmt:message key="task.libraryqc_title.label" /></h1>

<c:choose>
<c:when test="${jobList.size()==0}">
<h2><fmt:message key="task.libraryqc_subtitle_none.label" /></h2>
</c:when>
<c:otherwise>

<c:set var="currentJobId" value="-1" scope="page" />
<table class="EditTable ui-widget ui-widget-content">
<c:forEach items="${jobList}" var="job">
	<c:if test='${currentJobId != "-1"}'>
 		<tr><td colspan="7" style='background-color:black'></td></tr>
 	</c:if>
	<tr class="FormData">
		<th class="label-centered" style="font-weight:bold; background-color:#FAF2D6"><fmt:message key="task.samplereceive_jobId.label" /></th>
		<th class="label-centered" style="font-weight:bold; background-color:#FAF2D6"><fmt:message key="task.samplereceive_jobName.label" /></th>
		<th class="label-centered" style="font-weight:bold; background-color:#FAF2D6"><fmt:message key="task.samplereceive_submitter.label" /></th>
		<th class="label-centered" style="font-weight:bold; background-color:#FAF2D6"><fmt:message key="task.samplereceive_molecule.label" /></th>
		<th class="label-centered" style="font-weight:bold; background-color:#FAF2D6"><fmt:message key="task.samplereceive_sample.label" /></th>
		<th class="label-centered" style="font-weight:bold; background-color:#FAF2D6"><fmt:message key="task.samplereceive_sampleinternalid.label" /></th>
		<th class="label-centered" style="font-weight:bold; background-color:#FAF2D6"><fmt:message key="task.samplereceive_action.label" /></th>
	</tr>
	<c:set var="samplesList" value="${jobAndSamplesMap.get(job)}" scope="page" />
	<form action="<wasp:relativeUrl value="task/libraryqc/qc.do"/>" name="theForm<c:out value="${job.getJobId()}" />" id="theForm<c:out value="${job.getJobId()}" />" method="POST" onsubmit="return validateqcform(this);">
	<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="jobId" value="${job.getJobId()}"> 
	<c:forEach items="${samplesList}" var="sample" varStatus="status">	<%--NOTE THAT THESE ARE LIBRARIES!!!!! --%>
		<tr class="FormData">
			<c:choose>
				<c:when test="${currentJobId !=  job.getJobId()}">
					<td style='text-align:center;'><a style="color: #801A00;" href="<wasp:relativeUrl value="job/${job.getJobId()}/homepage.do" />">J<c:out value="${job.getJobId()}" /></a></td>          
					<td style='text-align:center'><c:out value="${job.getName()}" /></td>
					<td style='text-align:center'><c:out value="${job.getUser().getFirstName()}" /> <c:out value="${job.getUser().getLastName()}" /></td>
				</c:when>
				<c:otherwise>
					<td style='text-align:center'>&nbsp;</td>
					<td style='text-align:center'>&nbsp;</td>
					<td style='text-align:center'>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<td style='text-align:center'><c:out value="${sample.getSampleType().getName()}" /></td>
			<td style='text-align:center'><c:out value="${sample.getName()}" /> </td>
			<td style='text-align:center'><c:out value="${sample.getId()}" /> </td>
			<td style='text-align:center'>
				<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="sampleId" value="${sample.getSampleId()}"> <%-- THIS IS A LIBRARY!!! --%>
	 			<select class="FormElement ui-widget-content ui-corner-all" id="qcStatus<c:out value="${sample.getSampleId()}" />" name="qcStatus<c:out value="${sample.getSampleId()}" />" size="1" >
	 				<option value=""><fmt:message key="task.samplereceive_select.label" />
	 				<option value="PASSED"><fmt:message key="task.sampleqc_passed.label" />
	 				<option value="FAILED"><fmt:message key="task.sampleqc_failed.label" />
				</select>
				<textarea style='vertical-align:middle' id="comment<c:out value="${sample.getSampleId()}" />" name="comment<c:out value="${sample.getSampleId()}" />" cols="20" rows="2" placeholder="provide reason if failed"></textarea>
			</td>
		</tr>
		
		<c:if test="${status.last}">
			<tr class="FormData">
				<td style='text-align:center'>&nbsp;</td>
				<td style='text-align:center'>&nbsp;</td>
				<td style='text-align:center'>&nbsp;</td>
				<td style='text-align:center'>&nbsp;</td>
				<td style='text-align:center'>&nbsp;</td>
				<td style='text-align:center'>&nbsp;</td>
				<td style='text-align:center'>
					<span style="font-size:10px"><a href="javascript:void(0)" onclick='set("theForm<c:out value="${job.getJobId()}" />", "PASSED");'>set all passed</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" onclick='set("theForm<c:out value="${job.getJobId()}" />", "FAILED");'>set all failed</a><br /></span>
					<input class="FormElement ui-widget-content ui-corner-all" type="reset" value="<fmt:message key="task.samplereceive_reset.label" />">
					<input class="FormElement ui-widget-content ui-corner-all" type="submit" value="<fmt:message key="task.samplereceive_submit.label" />">
				</td>
			</tr>
		</c:if>
		
		<c:set var="currentJobId" value="${job.getJobId()}" scope="page" />	
	</c:forEach>
	</form>	
</c:forEach>

</table>
</c:otherwise>
</c:choose>