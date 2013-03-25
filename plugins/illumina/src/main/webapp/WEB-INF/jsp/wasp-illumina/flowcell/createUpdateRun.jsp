<%@ page session="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wasp" uri="http://einstein.yu.edu/wasp" %>
 <br />
<title><fmt:message key="pageTitle./wasp-illumina/flowcell/createUpdateRun.label"/></title>
<c:choose>
	<c:when test='${runId == "0"}'>
		<h1><fmt:message key="runInstance.headerCreate.label"/></h1>
	</c:when>
	<c:otherwise>
		<h1><fmt:message key="runInstance.headerUpdate.label"/></h1>
	</c:otherwise>
</c:choose> 

<div id="containerForTables" style="width:100%;overflow:hidden" >

<div id="left" style="float:left; margin-right:10px">
<table class="EditTable ui-widget ui-widget-content">
<tr class="FormData"><td class="CaptionTD"><fmt:message key="platformunitShow.typeOfPlatformUnit.label"/>:</td><td class="DataTD"><c:out value="${typeOfPlatformUnit}" /></td></tr>
<tr class="FormData"><td class="CaptionTD"><fmt:message key="platformunitShow.barcodeName.label"/>:</td><td class="DataTD"><c:out value="${barcodeName}" /></td></tr>
<tr class="FormData"><td class="CaptionTD"><fmt:message key="platformunitShow.readType.label"/>:</td><td class="DataTD"><c:out value="${readType}" /></td></tr>
<tr class="FormData"><td class="CaptionTD"><fmt:message key="platformunitShow.readLength.label"/>:</td><td class="DataTD"><c:out value="${readLength}" /></td></tr>
<tr class="FormData"><td class="CaptionTD"><fmt:message key="platformunitShow.numberOfCellsOnThisPlatformUnit.label"/>:</td><td class="DataTD"><c:out value="${numberOfCellsOnThisPlatformUnit}" /></td></tr>
<tr class="FormData"><td class="CaptionTD"><fmt:message key="platformunitShow.comment.label"/>:</td><td class="DataTD"><textarea style='font-size:9px' READONLY cols='30' rows='4' wrap='virtual'><c:out value="${comment}" /></textarea></td></tr>
</table>
</div>


<div id="right" style="overflow:hidden">

<table class="EditTable ui-widget ui-widget-content">

<tr class="FormData">
		
	<td class="CaptionTD"><fmt:message key="runInstance.chooseResource.label"/>:</td>
	<td class="DataTD">
	<form method="GET" action="<c:url value="/wasp-illumina/flowcell/createUpdateRun.do" />">
		<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="runId" value="<c:out value="${runId}" />" />
		<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="platformUnitId" value="<c:out value="${platformUnitId}" />" />
		<select class="FormElement ui-widget-content ui-corner-all" id="resourceId" name="resourceId" size="1" onchange="this.parentNode.submit()">
			<c:if test='${runId <= 0}'>
				<option value="0"><fmt:message key="wasp.default_select.label"/>
			</c:if>			
			<c:forEach items="${waspResources}" var="waspResource">
				<c:set var="selectedFlag2" value=""/>
				<c:if test='${resourceId == waspResource.getId()}'>
					<c:set var="selectedFlag2" value="SELECTED"/>
				</c:if>
				<option value='<c:out value="${waspResource.getId()}" />'    <c:out value="${selectedFlag2}" />     ><c:out value="${waspResource.getName()}" /> - <c:out value="${waspResource.getResourceCategory().getName()}" /></option>
			</c:forEach>
		 </select>		 
		 <%-- <span class="requiredField">*</span>--%>
	</form>
	</td>
	<td class="CaptionTD error"></td>
	 	
</tr>

<c:if test='${resourceId > 0}'>

  	<form:form  cssClass="FormGrid" commandName="run" action="/wasp/wasp-illumina/flowcell/createUpdateRun.do">
  	
  	<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="runId" id="runId" value="<c:out value="${runId}" />" />
  	<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="platformUnitId" id="platformUnitId" value="<c:out value="${platformUnitId}" />" />
  	<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="resourceId" id="resourceId" value="<c:out value="${resourceId}" />" />
  	<input class="FormElement ui-widget-content ui-corner-all" type="hidden" name="dateRunEnded" id="dateRunEnded" value="<c:out value="${dateRunEnded}" />" />
  
	<tr class="FormData">
        <td class="CaptionTD"><fmt:message key="runInstance.name.label" />:</td>
        <td class="DataTD"><input class="FormElement ui-widget-content ui-corner-all"  type="text" name = "name" id="name" size="25" maxlength="250" value="${run.name}" /><span class="requiredField">*</span></td>
        <td class="CaptionTD error"><form:errors path="name" /></td> 
	</tr>

	<c:set var="_area" value = "run" scope="request"/>
	<c:set var="_metaArea" value = "runInstance" scope="request"/>
    <c:set var="_metaList" value = "${run.runMeta}" scope="request" />
    <c:import url="/WEB-INF/jsp/meta_rw.jsp"/>

	<tr class="FormData">
        <td class="CaptionTD"><fmt:message key="runInstance.technician.label" />:</td>
        <td class="DataTD">        	
        	<form:select class="FormElement ui-widget-content ui-corner-all" path="UserId" size="1">
				<option value="0"><fmt:message key="wasp.default_select.label"/> <%-- --select-- --%>
				<c:forEach items="${technicians}" var="technician">
					<c:set var="selectedFlag3" value=""/>
					<c:if test='${technician.getUserId()==run.getUserId()}'>
						<c:set var="selectedFlag3" value="SELECTED"/>
					</c:if>
					<option value="<c:out value="${technician.getUserId()}"/>"  <c:out value="${selectedFlag3}" /> /> <c:out value="${technician.getFirstName()}" /><c:out value=" " /><c:out value="${technician.getLastName()}" />
				</c:forEach>
		 	</form:select>        
        	<span class="requiredField">*</span></td>
        <td class="CaptionTD error"><c:out value="${technicianError}" /> </td>
	</tr>

	<tr class="FormData"><%--<c:out value="${dateRunStarted}" />  --%>
        <td nowrap class="CaptionTD"><fmt:message key="runInstance.dateRunStarted.label" />:</td>
        <td class="DataTD"><input class="FormElement ui-widget-content ui-corner-all"  type="text" name = "dateRunStarted" id="dateRunStarted" value="${dateRunStarted}" /><span class="requiredField">*</span></td>
        <td class="CaptionTD error"><c:out value="${dateRunStartedError}" /></td>
	</tr>
	<tr class="FormData"><%--<c:out value="${dateRunEnded}" />  --%>
        <td nowrap class="CaptionTD"><fmt:message key="runInstance.dateRunEnded.label" />:</td>
        <td class="DataTD"><c:out value="${dateRunEnded}" /><span class="requiredField"></span></td>
        <td class="CaptionTD error"><c:out value="${dateRunEndedError}" /></td>
	</tr>

	<tr><td colspan="3">
    	<div class="submit">
   	    	<input class="fm-button" type="button" onClick="submit();" value="<fmt:message key='runInstance.submit.label'/>" /> 
    		<c:if test="${runId > 0}">
    			&nbsp;<input class="fm-button" type="button" onClick="location.href='createUpdateRun.do?reset=reset&platformUnitId=${platformUnitId}&runId=${runId}&resourceId=${resourceId}';" value="<fmt:message key='runInstance.reset.label'/>" /> 
    		</c:if>
    		&nbsp;<input class="fm-button" type="button" onClick="location.href='/wasp-illumina/flowcell//showFlowcell/${platformUnitId}.do';" value="<fmt:message key='runInstance.cancel.label'/>" /> 
    	</div>
    </td></tr>

	</form:form>

</c:if>


</table>

</div>

</div>