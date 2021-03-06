<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<script type="text/javascript">
	(function($){ 
		$(document).ready(function(){
			//$("div[id^='divToDisplayPluginSpecificSamplePairs']").each(function(){ 
			$("#divToDisplayPluginSpecificSamplePairs").each(function(){//only one
				var theDiv = $( this );
				$.ajax({
			        type: "GET",
			        //url: '<wasp:relativeUrl value="job/${job.getId()}/basic.do" />',
			        url: '<wasp:relativeUrl value="${job.getWorkflow().getIName()}/${job.getId()}/plugInSpecificSamplePairingDataForDisplay.do" />',
			        async:   false, //MUST BE SYNCHRONOUS!!!
			        //dataType: "json",
			        success: function (response) {
			        	theDiv.append(response);
			        	//$.each( response, function( key, val ) {			        		
			        	//	theDiv.append("<label>"+key +":</label> "+val+"<br />");						 
						 // });	
			        },
			        error: function (response) {
			        	;
			        }
			    });				
			});
		});
	})(jQuery);
</script>

<%--  TODO: Declare style in css file (e.g. /src/main/webapp/css/base.css), not in .jsp and reuse where possible !!!! --%>

<div id="user_requested_coverage_data" >
	<c:choose>
		<c:when test="${totalNumberCellsRequested==0}"><%--   value will be >=0    --%>
			<h2 style="font-weight:bold"><fmt:message key="listJobSamples.cellUsageRequestedNone.label"/></h2>	
		</c:when>
		<c:when test="${totalNumberCellsRequested>0}">
		    <h2 style="font-weight:bold"><fmt:message key="listJobSamples.cellUsageRequested.label"/>:</h2>		
			<table class="data">
			<tr class="FormData">
				<td class="label-centered" style="background-color:#FAF2D6">&nbsp;</td><c:forEach var="i" begin="0" end="${totalNumberCellsRequested - 1}" ><td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.cell.label" /> <c:out value="${i + 1}" /></td></c:forEach>
			</tr>
			<c:forEach items="${cellsRequestedMap.keySet()}" var="theSample">
				<tr class="FormData">
					<td class="label-centered" style="background-color:#FAF2D6" >
						<c:out value="${theSample.getName()}" />
					</td>
					<c:set var="string" value="${cellsRequestedMap.get(theSample)}" scope="page" />
					<c:forEach var="i" begin="0" end="${fn:length(string)-1}" step="1">
			   			<td  class="value-centered" style="text-align: center; vertical-align: middle;"> 
			   				<c:choose>
			   					<c:when test='${fn:substring(string, i, i + 1)=="0"}'><input type="checkbox" DISABLED /></c:when>
			   					<c:otherwise><input type="checkbox" DISABLED checked="checked" /></c:otherwise>
			   				</c:choose>   
			  			</td>   
					</c:forEach>
				</tr>
			</c:forEach>
			</table>
		</c:when>
	</c:choose>
</div>

<c:if test='${onlyDisplayCellsRequested != "true"}'>

	<!-- for displaying plugin specific pairing information; currently implemented only for HELP and ChIP-Seq -->
	<div id="divToDisplayPluginSpecificSamplePairs" ></div>

	<%-- 
	<div>
		<c:if test="${not empty controlList}">
			<h2 style="font-weight:bold"><fmt:message key="listJobSamples.samplePairingRequested.label"/>:</h2>		
			<table class="data">
				<tr class="FormData">
					<td class="label-centered" style="background-color:#FAF2D6"><c:out value="${controlLabel}" /></td>
					<td class="label-centered" style="background-color:#FAF2D6"><c:out value="${testLabel}" /></td>
				</tr>
			<c:forEach var="controlSample" items="${controlList}">
				<tr class="FormData">
					<td class="value-centered" ><c:out value="${controlSample.getName()}" /></td>   	  				
				 	<c:set var="testList" value="${samplePairsMap.get(controlSample)}" />
				 	<td class="value-centered" >
				 	<c:forEach var="testSample" items="${testList}">
				 		<c:out value="${testSample.getName()}" /><br />
				 	</c:forEach>
				 	</td>
				 </tr>
			</c:forEach>
			</table>
		</c:if>
	</div>
	
	--%>
	
	<%--  currently not used; should be moved to a chipseq specific ajax call
	<div>
		<c:if test="${not empty replicatesListOfLists}">
			<h2 style="font-weight:bold"><fmt:message key="listJobSamples.sampleReplicatesRequested.label"/>:</h2>		
			<table class="data">
				<tr class="FormData">
					<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.replicateSet.label"/></td>
					<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="listJobSamples.samples.label"/></td>
				</tr>
				<c:forEach items="${replicatesListOfLists}" var="replicateList" varStatus="replicateStatus">
				<tr class="FormData">				
					<td class="value-centered" ><c:out value="${replicateStatus.count}" /></td>		 	
				 	<td>
				 		<c:forEach items="${replicateList}" var="sample" varStatus="sampleStatus">
				 			<c:if test="${not sampleStatus.first}"><br /></c:if>
				 			<c:out value="${sample.name}" />
				 		</c:forEach>
				 	</td>  
				 </tr>
				</c:forEach>
			</table>
		</c:if>
	</div>

	--%>
	
	<div>
		<c:choose>
			<c:when test="${empty softwareList}">
				<h2 style="font-weight:bold"><fmt:message key="analysisParameters.no_software_requested.label"/></h2>
			</c:when>
			<c:otherwise>
				<h2 style="font-weight:bold"><fmt:message key="analysisParameters.software_requested.label"/>:</h2>
				<table class="EditTable ui-widget ui-widget-content">
	    			<c:forEach items="${softwareList}" var="software">
	   	  				<tr class="FormData"><td colspan="2" class="label-centered" style="font-weight:bold;"><c:out value="${software.getResourceType().getName()}" />: <c:out value="${software.name}" /></td></tr>
	   	  				<c:set var="_area" value = "${parentArea}" scope="request"/>
	   	  				<c:set value="${softwareAndSyncdMetaMap.get(software)}" var="_metaList" scope="request" />
		  				<c:import url="/WEB-INF/jsp/meta_ro.jsp" />
	  				</c:forEach> 
				</table>
			</c:otherwise>
		</c:choose>
	</div>
	
	<div>
		<h2 style="font-weight:bold"><fmt:message key="jobHomeRequests.alignmentsRequested.label" />:</h2>
		<table class="data">
			<tr class="FormData">
				<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="jobHomeRequests.submittedSample.label" /></td>
				<td class="label-centered" style="background-color:#FAF2D6"><fmt:message key="jobHomeRequests.alignmentsRequested.label" /></td>
			</tr>
   			<c:forEach items="${submittedSamplesList}" var="submittedSample">
   				<c:set value="${sampleGenomesForAlignmentListMap.get(submittedSample)}" var="genomesForAlignmentList" scope="request" />
  	  				<tr class="FormData">
  	  					<td class="value-centered">
  	  						<label><fmt:message key="jobHomeRequests.name.label" />:</label> <c:out value="${submittedSample.getName()}" /><br />
  	  						<label><fmt:message key="jobHomeRequests.species.label" />:</label> <c:out value="${genomesForAlignmentList.get(0)}" />
  	  					</td>
  	  					<td class="value-centered">
  	  						<label><fmt:message key="jobHomeRequests.species.label" />:</label> <c:out value="${genomesForAlignmentList.get(1)}" /><br />
  	  						<label><fmt:message key="jobHomeRequests.genome.label" />:</label> <c:out value="${genomesForAlignmentList.get(2)}" /><br />
  	  						<label><fmt:message key="jobHomeRequests.build.label" />:</label> <c:out value="${genomesForAlignmentList.get(3)}" /><br />
  	  					</td>
  	  				</tr>
 				</c:forEach> 
		</table>
	</div>	
</c:if>
<br />