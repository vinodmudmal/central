<%@ page session="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wasp" uri="http://einstein.yu.edu/wasp" %>
<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<c:import url="/WEB-INF/jsp/bioanalyzer/fadingMessage.jsp" />

<%-- What was used was: from http://hmkcode.com/spring-mvc-upload-file-ajax-jquery-formdata/ --%>
<%--Apparently need onsubmit='return false' to suppress hitting the event when the ENTER key is pressed with the cursor in the description input box --%>
<br /><h1>Reality check: this is the bioanalyzer file upload page: must remove this </h1><br />
<%--  TODO: Declare style in css file (e.g. /src/main/webapp/css/base.css), not in .jsp and reuse where possible !!!! --%>
<br />
<form id="fileUploadFormId" action="<wasp:relativeUrl value="bioanalyzer/job/${job.getId()}/fileUploadManager.do" />" method="POST"  enctype="multipart/form-data" onsubmit='return false;' >
	<table class="data" style="margin: 0px 0px">
		<tr class="FormData">
			<td colspan="3" class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.fileUploadUploadNewFile.label"/></td>
		</tr>
		<tr class="FormData">
			<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.fileUploadSelectFileToUpload.label"/></td>
			<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.fileUploadProvideBriefDescription.label"/></td>
			<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.file_action.label"/></td>
		</tr>
		<tr>
			<td class="DataTD value-centered"><input type="file" name="file_upload" /></td>
			<td class="DataTD value-centered" ><input type="text" maxlength="30" name="file_description" value="${userProvidedFileDescription}"/></td>
			<td align="center">
				<c:if test="${userIsFacilityPersonel==true}">
					<br />
					<select class="FormElement ui-widget-content ui-corner-all" name="fileIsFromBioanalyzer">
	      				<option value='-1'><fmt:message key="wasp.default_select.label"/></option>
	      				<option value="yes"  <c:if test="${userSelectedFileIsFromBioanalyzer == 'yes'}"> SELECTED</c:if>  ><fmt:message key="bioanalyzer.fileUploadFileIsBioanalyzerFile.label"/></option>      				      		
	      				<option value="no"  <c:if test="${userSelectedFileIsFromBioanalyzer == 'no'}"> SELECTED</c:if>  ><fmt:message key="bioanalyzer.fileUploadFileIsNotBioanalyzerFile.label"/></option>      				      		
	      			</select>
	      			<span class="requiredField">*</span>
	      			<br />
				</c:if>
				<input type="reset" name="reset" value="<fmt:message key="listJobSamples.file_reset.label" />" />
				<%--I don't know why, but having uploadJqueryForm("fileUploadFormId") wired through onsubmit via a regular submit button makes for Major problems!!!! use the button below --%>
				<a class="button" href="javascript:void(0);"  onclick='uploadJqueryForm("fileUploadFormId")' ><fmt:message key="listJobSamples.file_upload.label" /></a>
			</td>
		</tr>
		<c:if test="${not empty errorMessageList}">
				<tr><td colspan="3" align="center" style="color:red;font-weight:bold">
				<c:forEach items="${errorMessageList}" var="errorMessage">
					<c:out value="${errorMessage}" /><br />
				</c:forEach>
				</td></tr>
		</c:if>
		<c:forEach items="${fileGroups}" var="fileGroup" varStatus="fileGroupCounter">
			<c:if test="${fileGroupCounter.first}">
				<tr class="FormData">
					<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.file_name.label"/></td>
					<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.file_description.label"/></td>
					<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.file_action.label"/></td>
				</tr>		
			</c:if>
		 	<c:set value="${fileGroupFileHandlesMap.get(fileGroup)}" var="fileHandles"/>
		 	<c:choose>
		 		<c:when test="${fn:length(fileHandles)==1}">
		 		  	<c:forEach items="${fileHandles}" var="fileHandle" >
		 		  		<tr>
		 		  			<td class="DataTD value-centered"><c:out value="${fileHandle.getFileName()}" /></td>
		 		  			<td class="DataTD value-centered"><c:out value="${fileGroup.getDescription()}" /></td>
		 		  			<!--  <a href="<wasp:url fileAccessor="${fileHandle}" />" > -->
		 		  			<td class="DataTD value-centered"><a href="<wasp:relativeUrl value="file/fileHandle/${fileHandle.getId()}/download.do" />" ><fmt:message key="listJobSamples.file_download.label"/></a> 
		 		  				<c:if test="${fileHandlesThatCanBeViewedList.contains(fileHandle)}">
	 		  					| <a href="javascript:void(0);" onclick='parent.showModalessDialog("<wasp:relativeUrl value="file/fileHandle/${fileHandle.getId()}/view.do" />");' ><fmt:message key="listJobSamples.file_view.label"/></a>
	 		  				</c:if>
		 		  			</td>
		 		  		</tr>
		 			</c:forEach>
		 		</c:when>			 		  
		 		<c:otherwise>
		 			<tr>
		 		  		<td class="DataTD value-centered"><c:out value="${fn:length(fileHandles)}" /> <fmt:message key="listJobSamples.file_download_grouped_files.label"/></td>
		 		  		<td class="DataTD value-centered"><c:out value="${fileGroup.getDescription()}" /></td>			 		  			
		 		  		<td class="DataTD value-centered"><a href="<wasp:relativeUrl value="file/fileGroup/${fileGroup.getId()}/download.do" />" ><fmt:message key="listJobSamples.file_download.label"/></a></td>
		 		  	</tr>
		 		</c:otherwise>			 		
		 	</c:choose>
		</c:forEach>
	</table>
</form>
<br />
<!--  need to incorporate this at some point
<c:forEach items="${fileHandles}" var="fileHandle" >
<c:if test="${fileHandle.id==1180}">
Robert <c:out value="${fileHandle.id}" /> Dubin<br />
	<img src='<wasp:url fileAccessor='${fileHandle.getId().toString()}' />'height='400' width='400'><br />
</c:if>
</c:forEach>
--> 
