<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<wasp:message />
<h1><fmt:message key="pageTitle.sysrole/list.label" /></h1>

<div class="instructions">
  <fmt:message key="sysrole.list_current.label" />
</div>

<table class="EditTable ui-widget ui-widget-content">
<tr class="FormData">
  	<td class="CaptionTD label-centered">Name (Login Name)</td>
  	<td class="CaptionTD label-centered">Role</td>
  	<td class="CaptionTD label-centered">Action</td>
</tr>

  <c:forEach items="${userRoleMap}" var="rs">
	  <c:forEach items="${rs.value}" var="r">
	  	<tr class="FormData">
		    <td class="DataTD"><c:out value="${rs.key}" /></td>
		    <td class="DataTD"><c:out value="${r.role.name}" /></td>
		    <td class="submit value-centered"><a href="<c:url value="/sysrole/remove/${r.userId}/${r.role.roleName}.do" />">Remove Role</a></td>
	    </tr>
	  </c:forEach>
	</c:forEach>
  </table>

<div>

<h1><fmt:message key="sysrole.list_create.label" /></h1>
<div class="instructions">
  <%@ include file="/WEB-INF/jsp/lorem.jsp" %>
</div>

  <form method="POST" action="<c:url value="/sysrole/add.do"/>" onsubmit='return validate();'>
  <table class="EditTable ui-widget ui-widget-content">
    <tr class="FormData">
    	<td class="CaptionTD"><fmt:message key="sysrole.list_sysuser_role.label" />: </td>
    	<td class="DataTD"><select class="FormElement ui-widget-content ui-corner-all" name="roleName">
		    <option value="" SELECTED><fmt:message key="wasp.default_select.label"/></option>
		    <c:forEach items="${role}" var="r">
		      <option value="<c:out value="${r.roleName}"/>">
		         <c:out value="${r.name}"/>
		      </option>
		    </c:forEach>
		    </select>
		</td>
    </tr>
    <tr class="FormData">
	    <td class="CaptionTD"><fmt:message key="sysrole.list_sysuser_name.label" />: </td>
	    <td class="CaptionTD"><input class="FormElement ui-widget-content ui-corner-all" type="text" id="userHook" name="userHook" /></td>
    </tr>
 
  </table>
  <div class="submit">
    <input class="FormElement ui-widget-content ui-corner-all" type="submit" value="<fmt:message key="sysrole.list_submit.label" />" />
  </div>
</form>

  
</div>






