<%@ include file="/WEB-INF/jsp/taglib.jsp" %>
<script type="text/JavaScript">
		<!--
		function validate(){
			
			var error = false;
			var message = '<fmt:message key="user.auth_login_validate.error" />';
			if(document.f.j_username.value == "" || document.f.j_password.value == ""){
				error = true;
			}
			if(error){waspFade("waspErrorMessage", message); return false; }
  			return true;
		}
		
		waspOnLoad=function() {
			document.f.j_username.focus();
		}
		//-->
</script>