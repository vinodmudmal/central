<%@ include file="/WEB-INF/jsp/taglib.jsp" %>
    <font color="blue"><wasp:message /></font>

    <h1>Create a Resource</h1>

  <form:form commandName="resourceDraft">

    <table>
      <tr>
         <td><fmt:message key="resource.name.label"/>:</td>
         <td><form:input path="name" /></td>
         <td><form:errors path="name" /></td>
      </tr>
      <tr>
         <td><fmt:message key="resource.type.label"/>:</td>
         <td><form:input path="name" /></td>
         <td><form:errors path="name" /></td>
      </tr>
      <tr>
              <td><fmt:message key="jobDraft.labId.label"/>:</td>
              <td>
              <select name="labId">
                <option value='-1'><fmt:message key="wasp.default_select.label"/></option>
                <c:forEach var="lab" items="${labs}">
                        <option value="${lab.labId}" <c:if test="${lab.labId == jobDraft.labId}"> selected</c:if>><c:out value="${lab.name}"/></option>
                </c:forEach>
              </select>
              </td>
         <td><form:errors path="labId" /></td>
      </tr>
      <tr>
        <td><fmt:message key="jobDraft.workflowId.label"/>:</td>
        <td>
        <c:forEach var="workflow" items="${workflows}">
          <div>
          <input type="radio" name="workflowId" value="${workflow.workflowId}"
 <c:if test="${workflow.workflowId == jobDraft.workflowId}"> checked</c:if>
          >
          <fmt:message key="${workflow.IName}.workflow.label"/>
          </div>
        </c:forEach>

        </td>
        <td><form:errors path="workflowId" /></td>
      </tr>
    </table>
    <input type="submit">
  </form:form>