<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ include file="/WEB-INF/jsp/taglib.jsp" %>
<div><p><font color="red"><wasp:message /></font></p></div>
<div><p>Welcome back ${me.firstName} ${me.lastName}</p></div>
<div><a href="<c:url value="/user/me_ro.do"/>">My Profile</a></div>
<sec:authorize access="not hasRole('ldap')">
	<div><a href="<c:url value="/user/mypassword.do"/>">My Password</a></div>
</sec:authorize>
<div><a href="<c:url value="/auth/reauth.do"/>">Refresh Auth</a></div>
<div><a href="<c:url value="/lab/newrequest.do"/>">Request Access to a Lab</a> (Note: requests subject to verification)</div>
<br />


<sec:authorize access="hasRole('su')">
  <div>
  <h1>Super User Utils</h1>
  <div><a href="<c:url value="/user/list.do"/>">User Utils</a></div>
  <div><a href="<c:url value="/sysrole/list.do"/>">System Users</a></div>
 <!-- <div><a href="<c:url value="/department/list.do"/>">Department Utils</a></div> -->
  <div><a href="<c:url value="/lab/list.do"/>">- Lab Utils</a></div>
  <div><a href="<c:url value="/sample/list.do"/>">Sample Utils</a></div>
  <div><a href="<c:url value="/resource/list.do"/>">Resource Utils</a></div>
  <div><a href="<c:url value="/run/list.do"/>">- Run Utils</a></div>
  <div><a href="<c:url value="/task/list.do"/>">Task Utils</a></div>
  <div><a href="<c:url value="/workflow/list.do"/>">Workflow Utils</a></div>
  
  
  <!--  
  <div>
  <a href="<c:url value="/lab/allpendinglmapproval/list.do"/>">Lab Management</a>&nbsp;
  <c:choose>
   <c:when test='${allLabManagerPendingTasks == 0}'>(No Pending PI/Lab Manager Tasks)
   </c:when>
   <c:otherwise>
   <span style="color:red">
   (<c:out value="${allLabManagerPendingTasks}" /> Pending PI/Lab Manager Task<c:if test='${allLabManagerPendingTasks != 1}'>s</c:if>)
   </span>
   </c:otherwise>
     </c:choose>
  </div>
  -->  
  
  </div>
</sec:authorize>

<br />

<sec:authorize access="hasRole('da-*') or hasRole('su') or hasRole('ga')">
  <div>
  <h1>Department Admin</h1>

<p><a href="<c:url value="/department/list.do"/>">Department Management</a>&nbsp;
  <c:choose>
   <c:when test='${departmentAdminPendingTasks == 0}'>(No Pending Departmental Tasks)
   </c:when>
   <c:otherwise>
   <span style="color:red">
   (<c:out value="${departmentAdminPendingTasks}" /> Pending Department Administrator Task<c:if test='${departmentAdminPendingTasks != 1}'>s</c:if>)
   </span>
   </c:otherwise>
     </c:choose>
</p>
   
  <c:forEach items="${departments}" var="d">
    <div>
    <b><c:out value="${d.name}" /></b>
    <c:set var="departmentId" value="${d.departmentId}" />

    <div><a href="<c:url value="/department/detail/${departmentId}.do"/>">Department Detail</a></div>
    <div><a href="<c:url value="/department/pendinglab/list/${departmentId}.do"/>">Pending Lab Approval</a></div>
    <div><a href="<c:url value="/task/daapproval/list/${departmentId}.do"/>">Pending Department Admin Job Approval</a></div>
  </c:forEach>
  </div>
  <br />
</sec:authorize>



<sec:authorize access="hasRole('lu-*') or hasRole('su') or hasRole('ga')">
  <div>
  <h1>Lab Utils</h1>
  
  <sec:authorize access="hasRole('su') or hasRole('ga')">
  <div>
  <a href="<c:url value="/lab/allpendinglmapproval/list.do"/>">All Labs Management</a>&nbsp;
  <c:choose>
   <c:when test='${allLabManagerPendingTasks == 0}'>(No Pending PI/Lab Manager Tasks)
   </c:when>
   <c:otherwise>
   <span style="color:red">
   (<c:out value="${allLabManagerPendingTasks}" /> Pending PI/Lab Manager Task<c:if test='${allLabManagerPendingTasks != 1}'>s</c:if>)
   </span>
   </c:otherwise>
     </c:choose>
  </div>
  <br />
  </sec:authorize>
  
  
  <c:forEach items="${labs}" var="l">
    <div>
    <b><c:out value="${l.name}" /></b>
    <c:set var="labId" value="${l.labId}" />
    <div><a href="<c:url value="/lab/detail_ro/${l.departmentId}/${l.labId}.do"/>">Lab Details</a></div>
    <sec:authorize access="not hasRole('lm-${l.labId}' )">
	<div><a href="<c:url value="/lab/user_list/${l.labId}.do"/>">Lab Members</a></div>
	</sec:authorize>
    <sec:authorize access="hasRole('lm-${l.labId}' )">
  <!--     <div><a href="<c:url value="/lab/pendinguser/list/${l.labId}.do"/>">Pending User Approval</a></div> -->
      <div><a href="<c:url value="/lab/user_manager/${l.labId}.do"/>">User Manager</a></div>
      <!-- div><a href="<c:url value="/task/lmapproval/list/${l.labId}.do"/>">Pending Lab Manager Approval</a></div-->
    <!--  <div><a href="<c:url value="/task/lmapproval/list/${l.labId}.do"/>">Tasks Pending PI/Lab Manager Approval</a> -->
       <div><a href="<c:url value="/lab/pendinglmapproval/list/${l.labId}.do"/>">Tasks Pending PI/Lab Manager Approval</a> 
    <c:choose>
   <c:when test='${labmap.get(l.labId) == 0}'>(No Pending PI/Lab Manager Tasks)
   </c:when>
   <c:otherwise>
   <span style="color:red">
   (<c:out value="${labmap.get(l.labId)}" /> Pending PI/Lab Manager Task<c:if test='${labmap.get(l.labId) != 1}'>s</c:if>)
   </span>
   </c:otherwise>
     </c:choose>
    </div>
    </sec:authorize>

    </div>
  </c:forEach>

  </div>
</sec:authorize>

<br />

<sec:authorize access="hasRole('jv-*')">
  <div>
  <h1>Viewable Jobs</h1>
  <c:forEach items="${jobs}" var="j">
    <c:set var="jobId" value="${j.jobId}" />

    <div>
      <b><c:out value="${j.lab.name}" />&nbsp;[<c:out value="${j.user.login}" />]</b> - 
      <a href="<c:url value="/job/detail/${jobId}.do"/>">
      <b><c:out value="${j.name}" /></b>
      </a>
    </div>
  </c:forEach>
  </div>
  <br />
</sec:authorize>



<sec:authorize access="hasRole('jd-*')">
  <div>
  <h1>Drafted Jobs</h1>
  <c:forEach items="${jobdrafts}" var="j">
    <c:set var="jobDraftId" value="${j.jobDraftId}" />

    <div>
      <b><c:out value="${j.lab.name}" />&nbsp;[<c:out value="${j.user.login}" />]</b> - 
      <a href="<c:url value="/jobsubmit/verify/${jobDraftId}.do"/>">
      <b><c:out value="${j.name}" /></b>
      </a>
    </div>
  </c:forEach>
  </div>
  <br />
</sec:authorize>



<sec:authorize access="hasRole('lu-*')">
  <a href="<c:url value="/jobsubmit/create.do" />">Submit a Job</a>
<br /><br />
</sec:authorize>



<sec:authorize access="hasRole('fm')">
  <div>
  <h1>Facility Manager Utils</h1>
  <div><a href="<c:url value="/task/fmrequote/list.do"/>">Requote Pending Jobs</a></div>
  <div><a href="<c:url value="/task/fmpayment/list.do"/>">Receive Payment for Jobs</a></div>
  </div>
  <br />
</sec:authorize>



<sec:authorize access="hasRole('fm') or hasRole('ft')">
  <div>
  <h1>Incoming Sample Manager</h1>
  <div><a href="<c:url value="/task/samplereceive/list.do"/>">Sample Receiver</a></div>
  
  <div><a href="<c:url value="/sampleDnaToLibrary/listJobSamples/10006.do"/>">List Samples for Job 10006</a></div>
  <div><a href="<c:url value="/sampleDnaToLibrary/listJobSamples/10001.do"/>">List Samples for Job 10001</a></div>
  <div><a href="<c:url value="/sampleDnaToLibrary/listJobSamples/10100.do"/>">List Samples for Job 10100</a></div>
  <div><a href="<c:url value="/sampleDnaToLibrary/listJobSamples/10216.do"/>">List Samples for New Job 10216</a></div>
  
  </div>
  <br />
</sec:authorize>



<sec:authorize access="hasRole('ft')">
  <div>
  <h1>Platform Unit</h1>
  <div><a href="<c:url value="/facility/platformunit/list.do"/>">List / Create</a></div>
  <div><a href="<c:url value="/facility/platformunit/assign.do" />">Platform Unit assignment</a></div>
  <div><a href="<c:url value="/facility/platformunit/limitPriorToAssign.do" />">Platform Unit assignment</a></div>
  </div>
  <br />
</sec:authorize>

<hr>


<h1>Task List</h1>
<div>
<c:forEach items="${tasks}" var="task"> 	
  
 <div><a href="<c:url value="${task.listMap}"/>">${task.task.name}/${task.status}</a>  (${task.stateCount})</div>
  
</c:forEach>
</div>
<hr>







