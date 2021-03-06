<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<%--  TODO: Declare style in css file (e.g. /src/main/webapp/css/base.css), not in .jsp and reuse where possible !!!! --%>

<c:import url="/WEB-INF/jsp/job/home/fadingMessage.jsp" />

<br />
<form  method='post' name='commentForm' id='commentFormId' onsubmit='postFormWithAjax("commentFormId","<wasp:relativeUrl value="job/${job.getId()}/comments.do" />"); return false;'>
	<table class="data" style="margin: 0px 0; width:650px" >
		<c:if test='${permissionToAddEditComment==true}'>
			<tr ><th class="label" nowrap colspan="3"><fmt:message key="jobComment.addNewJobComment.label" /></th></tr>
			<tr><td align="center" colspan="3">
				<textarea id="comment" name="comment" cols="80" rows="5"></textarea><br />
			</td></tr>
			<c:if test="${fn:length(errorMessage)>0}">
				<tr><td colspan="3" align="center" style="color:red;font-weight:bold"><c:out value="${errorMessage}" /></td></tr>
			</c:if>
			<tr><td align="center" colspan="3">
				<br />
				<%--<input type="checkbox" name="emailThisComment" value="facility">Email This Comment To Sequencing Facility &amp; Job Submitter<br />--%>
				<span style="font-weight:bold"><fmt:message key="jobComment.jobCommentSendEmail.label"/></span><br />
				<input type="radio" name="emailThisComment" value="yes"><fmt:message key="jobComment.jobCommentSendEmailYes.label"/> &nbsp; <input type="radio" name="emailThisComment" checked value="no"><fmt:message key="jobComment.jobCommentSendEmailNo.label"/><br />
				<input type='submit' value='Submit Comment'/>
			</td></tr>
		</c:if>
<%--		
		<c:if test='${fn:length(userSubmittedJobCommentsList) > 0}'>
			<tr>
				<th class="label" nowrap><fmt:message key="jobComment.jobCommentDate.label"/></th>
				<th class="label" nowrap><fmt:message key="jobComment.jobCommentSubmittedBy.label"/></th>
				<th class="label" nowrap><fmt:message key="jobComment.jobSubmitterComment.label"/></th>
			</tr>	
			<c:forEach items="${userSubmittedJobCommentsList}" var="userSubmittedJobComment">
				<tr>
					<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" nowrap><fmt:formatDate value="${userSubmittedJobComment.getDate()}" pattern="yyyy-MM-dd" /></td>
					<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" ><c:out value="${userSubmittedJobComment.getUser().getNameFstLst()}" /></td>
					<%-- for potential problems with escapeXML in next c:out see: http://www.coderanch.com/t/535302/JSP/java/Keeping-line-breaks-String --%><%--escapeXml="false"--%>
<%--				<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" ><c:out value="${userSubmittedJobComment.getValue()}"  escapeXml="false" /> </td> 
				</tr>
			</c:forEach>
		</c:if>
		
		<c:if test='${fn:length(facilityJobCommentsList) > 0}'>
			<tr>
				<th class="label" nowrap><fmt:message key="jobComment.jobCommentDate.label"/></th>
				<th class="label" nowrap><fmt:message key="jobComment.jobCommentSubmittedBy.label"/></th>
				<th class="label" nowrap><fmt:message key="jobComment.facilityComment.label"/></th>
			</tr>	
			<c:forEach items="${facilityJobCommentsList}" var="facilityJobComment">
			<tr>
				<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" nowrap><fmt:formatDate value="${facilityJobComment.getDate()}" pattern="yyyy-MM-dd" /></td>
				<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" ><c:out value="${facilityJobComment.getUser().getNameFstLst()}" /></td>
				<%-- for potential problems with escapeXML in next c:out see: http://www.coderanch.com/t/535302/JSP/java/Keeping-line-breaks-String --%><%--escapeXml="false"--%>
<%--			<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" ><c:out value="${facilityJobComment.getValue()}"  escapeXml="false" /> </td> 
			</tr>
			</c:forEach>
		</c:if>
--%>
		<c:if test='${fn:length(allJobCommentsList) > 0}'>
			<tr>
				<th class="label" nowrap><fmt:message key="jobComment.jobCommentDate.label"/></th>
				<th class="label" nowrap><fmt:message key="jobComment.jobCommentSubmittedBy.label"/></th>
				<th class="label" nowrap><fmt:message key="jobComment.comments.label"/></th>
			</tr>	
			<c:forEach items="${allJobCommentsList}" var="theComment">
			<tr>
				<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" nowrap><fmt:formatDate value="${theComment.getDate()}" pattern="yyyy-MM-dd" /></td>
				<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" nowrap><c:out value="${theComment.getUser().getNameFstLst()}" /></td>
				<%-- for potential problems with escapeXML in next c:out see: http://www.coderanch.com/t/535302/JSP/java/Keeping-line-breaks-String --%><%--escapeXml="false"--%>
				<td style="padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;" >
					<c:if test="${facilityJobCommentsList.contains(theComment) }"><fmt:message key="jobComment.sequencingFacilityComment.label"/>: </c:if>
					<c:out value="${theComment.getValue()}"  escapeXml="false" /> 
				</td> 
			</tr>
			</c:forEach>
		</c:if>
	</table>
</form>
<br />