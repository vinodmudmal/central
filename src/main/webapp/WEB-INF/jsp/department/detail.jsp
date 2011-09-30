<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
  <head>
  
  
  
   <title><fmt:message key="department.detail_page_title.label" /></title>
   
   <script type="text/javascript" src="/wasp/scripts/jquery/jquery-1.6.2.js"></script>
   <link rel="stylesheet" type="text/css" href="/wasp/css/jquery/jquery-ui.css"/>
  <script type="text/javascript" src="/wasp/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script> 
  
  <!-- link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/-->
  <!-- script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script-->
  <!-- script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script-->
 
    <script type="text/javascript">
        $(document).ready(function() {            
	       $("#adminName").keyup(function(){getAuthNames();});//this works well
	       // $("input#autocomplete").autocomplete({source: ["c++", "java", "php", "coldfusion", "javascript", "asp", "ruby"]}); 
        });
         
        function getAuthNames(){        
        	if( $("#adminName").val().length == 1){
	        	$.getJSON("/wasp/department/getUserNamesAndLoginForDisplay.do", { adminNameFragment: $("#adminName").val() }, function(data) { $("input#adminName").autocomplete(data);} );
        	}
	    }
    </script>
    <script language="JavaScript">
		<!--
		function validate(){
			//alert("test123"); return false;
			var error = false;
			var message = '<fmt:message key="department.detail_missingparam.error" />';
			if(document.f.adminName.value == ""){
				error = true;
				document.f.adminName.focus();
			}
			if(error){ alert(message); return false; }
  			return true;
		}
		//-->
	</script>  
  <head>
  <body>
  <br />
      
    <h1><c:out value="${department.name}"/></h1>

    <h2><fmt:message key="department.detail_administrators.label" /></h2>
    
    <div>
    <h3><fmt:message key="department.detail_createadmin.label" /></h3>
    <font color="red"><wasp:message /></font>
    <form name="f" action="<c:url value='/department/user/roleAdd.do'/>" method="POST" onsubmit='return validate();'>
       
      <input type='hidden' name='departmentId' value='<c:out value="${department.departmentId}" />'/>
      <fmt:message key="department.detail_administrator_name.label" />: <input id="adminName" name='adminName' value='' />
      <br/>
      
      <input type="submit" value="<fmt:message key="department.detail_submit.label" />" /> 
    </form>
    </div>
    <h3><fmt:message key="department.detail_existingadmin.label" /></h3>
    <c:forEach items="${departmentuser}" var="u">
      <p>
      <a href="/wasp/user/detail/<c:out value="${u.user.userId}" />.do"><c:out value="${u.user.login}" /></a>
        <c:out value="${u.user.firstName}" />
        <c:out value="${u.user.lastName}" />
        <a href="/wasp/department/user/roleRemove/<c:out value="${department.departmentId}" />/<c:out value="${u.user.userId}" />.do">
          <fmt:message key="department.detail_remove.label" />
        </a>
      </p>
    </c:forEach>

    

    <h2><fmt:message key="department.detail_labs.label" /></h2>
    <c:forEach items="${lab}" var="l">
      <div>
        <a href="/wasp/lab/detail_ro/<c:out value="${department.departmentId}" />/<c:out value="${l.labId}" />.do">
        <c:out value="${l.name}" />
        </a>
      </div>
    </c:forEach>

    <h2><fmt:message key="department.detail_pendinglabs.label" /></h2>
    <c:forEach items="${labpending}" var="lp">
      <div>
      	<a href="/wasp/lab/pending/detail_ro/<c:out value="${department.departmentId}" />/<c:out value="${lp.labPendingId}" />.do">
      	<c:out value="${lp.name}" />
      	</a>
      </div>
    </c:forEach>

  </body>
</html>
