<%@ include file="/WEB-INF/jsp/taglib.jsp" %>
caption:"<fmt:message key="platformunitInstance.platformunitinstance_list.label" />",

ondblClickRow: function(rowid) {
	$("#grid_id").jqGrid('viewGridRow',rowid,_viewAttr);
}