<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<sec:authorize access="hasRole('da-*') or hasRole('su') or hasRole('ga-*') or hasRole('ft-*') or hasRole('fm-*')">
<title><fmt:message key="dapendingtask.title.label"/></title>
<h1><fmt:message key="dapendingtask.title.label"/></h1>

 <c:choose>
    <c:when test="${labspendinglist.size()==0}">
    	<h2><fmt:message key="dapendingtask.subtitle1_none.label" /></h2>
    </c:when>
    <c:otherwise>   
       <h2><fmt:message key="dapendingtask.subtitle1.label" /></h2> 
	   <div id="accordion">
      		<c:forEach items="${labspendinglist}" var="labPending">
      			<c:set var="piPending" value="${labPending.getUserPending()}" />
      		 	<h4><a href="#">New PI: <c:out value="${piPending.getFirstName()}" /> <c:out value="${piPending.getLastName()}" /> (Email: <c:out value="${piPending.getEmail()}" />)</a> </h4>     
            	<div> 
            		PI: <c:out value="${piPending.getFirstName()}" /> <c:out value="${piPending.getLastName()}" /><br />
            		Email: <c:out value="${piPending.getEmail()}" /><br /> 
            		Department: <c:out value="${labPending.getDepartment().getName()}" /><br />          		
            		<c:set var="departmentId" value="0" />           	         	
      	      		<c:forEach items="${piPending.getUserPendingMeta()}" var="meta">      	      			
      	      			
        	 			<c:if test="${meta.k == 'piPending.institution' || meta.k == 'piPending.building_room' || meta.k == 'piPending.address' || meta.k == 'piPending.city' || meta.k == 'piPending.state' || meta.k == 'piPending.country' || meta.k == 'piPending.phone'}">
 							<c:set var="optionName" value="${meta.k}.label" /><fmt:message key="${optionName}" />:&nbsp;<c:out value="${meta.v}" /><br />
        	 			</c:if>
      				</c:forEach>  
      				<br /><div class="submit"><a href="<c:url value="/lab/pending/approve/${labPending.getDepartmentId()}/${labPending.getLabPendingId()}.do"/>">APPROVE</a> <a href="<c:url value="/lab/pending/reject/${labPending.getDepartmentId()}/${labPending.getLabPendingId()}.do"/>">REJECT</a></div>    
      			</div>       
     		</c:forEach>     		
    	</div>   
    </c:otherwise>
</c:choose> 
    
<c:choose>
    <c:when test="${jobspendinglist.size()==0}">
    	<h2><fmt:message key="dapendingtask.subtitle2_none.label" /></h2>    
    </c:when>
    <c:otherwise>
    	<h2><fmt:message key="dapendingtask.subtitle2.label" /></h2>  
		<div id="accordion2">         
    	<c:forEach items="${jobspendinglist}" var="job">      
      		<h4><a href="#">Job ID: J<c:out value="${job.jobId}" /> (Submitter: <c:out value="${job.getUser().getNameFstLst()}" />; PI: <c:out value="${job.getLab().getUser().getNameFstLst()}" />)</a></h4>
      		<div>
      			Job ID: J<c:out value="${job.jobId}" /><br />
      			Job Name: <c:out value="${job.getName()}" /><br />
      			Submitter: <c:out value="${job.getUser().getNameFstLst()}" /><br />
      			PI: <c:out value="${job.getLab().getUser().getNameFstLst()}" /><br />
      			<c:set var="extraJobDetailsMap" value="${jobExtraJobDetailsMap.get(job)}" />
      			<c:forEach items="${extraJobDetailsMap}" var="detail">
      				<c:out value="${detail.getKey()}" />: <c:out value="${detail.getValue()}" /><br />
      			</c:forEach>      			
    			Workflow: <c:out value="${job.getWorkflow().getName()}" /><br />      			
      			<c:set var="submittedSamplesList" value="${jobSubmittedSamplesMap.get(job)}" />
      			Samples [<c:out value="${submittedSamplesList.size()}" />]:<br />	
      			<c:forEach items="${submittedSamplesList}" var="sample">
      				--<c:out value="${sample.getName()}" /> (<c:out value="${sample.getSampleType().getName()}" />, <c:out value="${sampleSpeciesMap.get(sample)}" />)<br />      				
      			</c:forEach>
      			
      			<br /> <div class="submit"><a href="<c:url value="/job/pendingdaapproval/approve/${job.lab.departmentId}/${job.jobId}.do"/>">APPROVE</a> <a href="<c:url value="/job/pendingdaapproval/reject/${job.lab.departmentId}/${job.jobId}.do"/>">REJECT</a></div>    
      		</div>      
   		 </c:forEach>
    	</div> 
	</c:otherwise>
</c:choose>
</sec:authorize>
