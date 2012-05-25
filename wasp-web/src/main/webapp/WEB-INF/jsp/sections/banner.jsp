<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

  <!-- top -->
 
  <sec:authorize access="isAuthenticated()">
    <header id="pageHeaderLoggedIn">
      <nav>
      	<div class="header_nav">
      		<a href="/wasp/dashboard.do" >Dashboard</a> | 
        	<a href="/wasp/j_spring_security_logout" >Logout</a>
        </div>
        <a href="/wasp/dashboard.do"><img src="/wasp/images/waspSystemLogo_150x111.png" alt="WASP" /></a>
      </nav>
    </header>
  </sec:authorize>
  <sec:authorize access="! isAuthenticated()">
    <header id="pageHeaderNotLoggedIn">
      <nav>
        <a href="/wasp/"><img src="/wasp/images/waspSystemLogo_150x111.png" alt="WASP" /></a>
      </nav>
    </header>
  </sec:authorize>
  <wasp:errorMessage />
  <wasp:message />

  <!-- /top -->

