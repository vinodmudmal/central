<%@ include file="/WEB-INF/jsp/taglib.jsp" %>

<br /><br />
<h1><a  href="<c:url value="/sampleDnaToLibrary/listJobSamples/${job.jobId}.do" />">JobID J<c:out value="${job.jobId}" /></a>: Sample View</h1>
<div style="overflow:auto">
<table class="data">

<c:forEach items="${submittedObjectList}" var="submittedObject" varStatus="statusSubmittedObject">
	<c:if test="${statusSubmittedObject.first}">
		<tr class="FormData">
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Macromolecule</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Species</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Library</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Adaptor</td>	
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Index-Tag</td>
			
			<%-- 
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Run (length:type)</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Lane</td>	
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Stats</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">PF</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Aligned</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">RefGenome</td>
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">SeqFiles</td>	
			<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">AlignFiles</td>--%>
		</tr>	
	</c:if>
	
	<c:choose>
		<c:when test="${submittedMacromoleculeList.contains(submittedObject)}">
			<c:set value="${submittedMacromoleculeFacilityLibraryListMap.get(submittedObject)}" var="libraryList"/>
		</c:when>
		<c:otherwise>
			<c:set value="${submittedLibrarySubmittedLibraryListMap.get(submittedObject)}" var="libraryList"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${fn:length(libraryList)==0 || fn:length(libraryList)==1}">
			<c:set value="1" var="libraryRowspan"/>
		</c:when>
		<c:otherwise>
			<c:set value="${fn:length(libraryList)}" var="libraryRowspan"/>
		</c:otherwise>
	</c:choose>
		
	<tr>
		<td class="DataTD"  rowspan="${libraryRowspan}" style="text-align:center; white-space:nowrap;">
			<c:choose>
				<c:when test="${submittedMacromoleculeList.contains(submittedObject)}">
					<c:out value="${submittedObject.getName()}" />
				</c:when>
				<c:otherwise>
					N/A
				</c:otherwise>
			</c:choose>
		</td>
		<td class="DataTD" rowspan="${libraryRowspan}" style="text-align:center; white-space:nowrap;">
			<c:out value="${submittedObjectOrganismMap.get(submittedObject)}" />
		</td>
		
		<c:choose>
		<c:when test="${fn:length(libraryList)==0}">
			<td class="DataTD" style="text-align:center; white-space:nowrap;">&nbsp;</td>			
			<td class="DataTD" style="text-align:center; white-space:nowrap;">&nbsp;</td>			
			<td class="DataTD" style="text-align:center; white-space:nowrap;">&nbsp;</td></tr>
		</c:when>
		<c:otherwise>
		    <c:forEach items="${libraryList}" var="library" varStatus="statusLibrary">	
				<c:if test="${!statusLibrary.first}"><tr></c:if>
				<td class="DataTD" style="text-align:center; white-space:nowrap;"><c:out value="${library.getName()}" /></td>
				<td class="DataTD" style="text-align:center; white-space:nowrap;">
					<c:set value="${libraryAdaptorsetMap.get(library)}" var="adaptorSet"/>
					<c:out value="${adaptorSet.getName()}" />					
				</td>
				<td class="DataTD" style="text-align:center; white-space:nowrap;">
					<c:set value="${libraryAdaptorMap.get(library)}" var="adaptor"/>
					<c:out value="${adaptor.getBarcodenumber()}" /> - <c:out value="${adaptor.getBarcodesequence()}" />
				</td>
				</tr>
		    </c:forEach>
		</c:otherwise>
		</c:choose>
	



</c:forEach>
<%-- 
<tr class="FormData">

	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Macromolecule</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Library</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Species</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Adaptor</td>	
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Index-Tag</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Run (length:type)</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Lane</td>	
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Stats</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">PF</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">Aligned</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">RefGenome</td>
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">SeqFiles</td>	
	<td class="label-centered" style="background-color:#FAF2D6; white-space:nowrap;">AlignFiles</td>
</tr>

<tr>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">rob's first DNA sample</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">rob's first DNA sample_lib1</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">Mus musculus</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">TruSeq Indexed DNA</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">1-AAGCCT</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">130408_SN7001401_0117_AX123YY (100:Single)</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">1</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">fqc | stats</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">10000000</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">9876543</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">mm9</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">fastq</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">bam<br/>bam-i<br>sam</td>
</tr>

<tr>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">the second DNA sample</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">the second DNA sample_lib1</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">Mus musculus</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">TruSeq Indexed DNA</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">2-AGCTAT</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">130408_SN7001401_0117_AX123YY (100:Single)</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">1</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">fqc | stats</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">12430000</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">10002500</td>
	<td class="DataTD" style="text-align:center; white-space:nowrap;">mm9</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">fastq</td>	
	<td class="DataTD" style="text-align:center; white-space:nowrap;">bam<br/>bam-i<br>sam</td>
</tr>
--%>		
</table>
</div>