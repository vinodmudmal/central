<%@ include file="/WEB-INF/jsp/taglib.jsp" %>
  <p><font color="red"><wasp:message /></font>  </p>
  <form:form commandName="labPending">
   <table>
     	  <tr><td colspan=2 align=left></br><b>Pending Lab Details:</b></td></tr>
           <tr>
              <td><fmt:message key="labPending.name.label" />:</td>
              <td><form:input path="name" /></td>
              <td><form:errors path="name"/></td>
          </tr>
          <tr>
              <td><fmt:message key="labPending.primaryUserId.label"/>:</td>
              <td>
              <c:out value="${puserFullName}"/>
              </td>
              <td>&nbsp;</td>
          </tr>
          <tr>
              <td><fmt:message key="labPending.departmentId.label"/>:</td>
              <td>
              <select name=departmentId>
                <option value='-1'>-- select --</option>
              	<c:forEach var="dept" items="${departments}">
                	<option value="${dept.departmentId}" <c:if test="${dept.departmentId == labPending.departmentId}"> selected</c:if>><c:out value="${dept.name}"/></option>
               	</c:forEach>     
              </select>
              </td>
              <td><form:errors path="departmentId" /></td>
          </tr>
          <c:set var="_area" value = "labPending" scope="request"/>	
		  <c:set var="_metaList" value = "${labPending.labPendingMeta}" scope="request" />		
          <c:import url="/WEB-INF/jsp/meta_rw.jsp"/>
          <tr>
              <td colspan="2" align=right>
                  <input type="submit" value="Save Changes" />
              </td>
          </tr>    
            	      
   </table> 
   </form:form>
