<html>

<head> 
<%@ include file="/WEB-INF/jsp/include.jsp" %>
 
  <link rel="stylesheet" type="text/css" media="screen" href="/wasp/css/jquery/jquery-ui.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="/wasp/css/jquery/ui.jqgrid.css" />
 <style>

html, body {

	margin: 0;			/* Remove body margin/padding */

	padding: 0;

	overflow: hidden;	/* Remove scroll bars on browser window */	

    font-size: 75%;
}

 </style>	
  
  <script src="/wasp/scripts/jquery/jquery-1.6.2.js" type="text/javascript"></script>
  <script src="/wasp/scripts/jqgrid/grid.locale-en.js" type="text/javascript"></script>
  <script src="/wasp/scripts/jqgrid/jquery.jqGrid.min.js" type="text/javascript"></script>
  
  <script type="text/javascript">

  $.jgrid.no_legacy_api = true;

  $.jgrid.useJSON = true;

  
  function _noop(value, colname) {
	  return [true,""];
  }
  
  function _validate_required(value, colname) {
	 
	  if (value)  
	     return [true,""];
	  else { 
		
		 var errIdx=colNames.indexOf(colname);
		
		 var errMsg=colErrors[errIdx];
		
	     return [false,errMsg];
	  }
   }
 
  //these will be populated by the wasp:field tags below
  var colNames=[];  
  var colModel=[];  
  var colErrors=[];
  
  <wasp:field name="login" object="user" />  
  <wasp:field name="firstName" object="user" />
  <wasp:field name="lastName" object="user" />
  <wasp:field name="email" object="user" />
  <wasp:field name="locale" object="user" />
  <wasp:field name="isActive" object="user" />

  
  isActive.jq['edittype']='checkbox';
  isActive.jq['editoptions']={value:"1:0"}; 
  isActive.jq['formatter']='checkbox';
  isActive.jq['formatoptions']={disabled : true};
  isActive.jq['align']='center';
  
  
  locale.jq['edittype']='select';
  locale.jq['editoptions']={value:{}};
 
  <c:forEach var="localeEntry" items="${locales}">     
  locale.jq['editoptions']['value']['${localeEntry.key}']='${localeEntry.value}';
</c:forEach>

  
  //adjust fieldsName.jq.* properties hear to alter default look
  //value:{1:'One',2:'Two'} 
   //edittype:'checkbox', editoptions: { value:"True:False"}, 
  //formatter: "checkbox", formatoptions: {disabled : false}
  
  //get meta fiel definitions from servlet
  var _wasp_meta_fields=${fieldsArr};
  
  
  //add to jqGrid definitions
  for (key in _wasp_meta_fields) {
	  	var _wasp_field=_wasp_meta_fields[key];
	  	var _wasp_prop=_wasp_field['property'];
	  	var _field_name=_wasp_field['k'];
	  	
	  	colNames.push(_wasp_prop['label']);
	  	var constraint = _wasp_prop['constraint'];
	  	var required=constraint=='NotEmpty';
	  	var formoptions=required?{elmsuffix:'<font color=red>*</font>'}:{};
	  
	  	colModel.push(
	  			{name:_field_name, width:80, align:'right',hidden:true,editable:true,editrules:{edithidden:true,custom:true,custom_func:_validate_required},formoptions:formoptions,editoptions:{size:10}}
	  	);
	  	
	  	colErrors.push(_wasp_prop['error']);

  }
  
  _errorTextFormat = function (data) {
	   return data.responseText;
	};
	
  _del_function = function (id) {
	   alert("Deleting users is not allowed. Use 'edit' button to mark user '"+$("#grid_id").getRowData(id).login+"' as inactive.");
	   return false;
	};	
  
$(function(){ 
  $("#grid_id").jqGrid({
    url:'/wasp/user/listJSON.do',
    editurl:'/wasp/user/detail_rw/updateJSON.do',
    datatype: 'json',
    mtype: 'GET',
    colNames:colNames,
    colModel : colModel,
    pager: '#gridpager',
    rowNum:100,    
    rowList:[5,10,20],
    sortname: 'login',
    sortorder: 'desc',
    viewrecords: true,
    gridview: true,
	caption: '',
    autowidth: true,
	loadui: 'block',
	scroll: true,
	emptyrecords: 'No users',
	height: 'auto',
	caption: "User List",
    ondblClickRow: function(rowid) {
    	$("#grid_id").jqGrid('editGridRow',rowid,{errorTextFormat:_errorTextFormat,closeAfterEdit:true,closeOnEscape:true});
   }
  }).navGrid('#gridpager',{view:true, del:true,delfunc:_del_function}, 
		  {closeAfterEdit:true,closeOnEscape:true,errorTextFormat:_errorTextFormat}, // use default settings for edit
		  {closeAfterEdit:true,closeOnEscape:true,errorTextFormat:_errorTextFormat}, // use default settings for add
		  {},  // delete instead that del:false we need this
		  {multipleSearch : true}, // enable the advanced searching
		  {closeAfterEdit:true,closeOnEscape:true,errorTextFormat:_errorTextFormat} /* allow the view dialog to be closed when user press ESC key*/
		  );

}); 



</script>
  
</head>
<body>


<table id="grid_id"></table> 
<div id="gridpager"></div>

</script>

</table>
</body>
</html>