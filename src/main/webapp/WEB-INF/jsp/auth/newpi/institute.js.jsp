<script type="text/javascript" src="/wasp/scripts/jquery/jquery-1.6.2.js"></script>
<link rel="stylesheet" type="text/css" href="/wasp/css/jquery/jquery-ui.css"/>
<script type="text/javascript" src="/wasp/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script> 


 <script type="text/javascript">
     $(document).ready(function() {            
	       $("#instituteOther").keyup(function(){getAuthNames();});
     });
      
     function getAuthNames(){        
     	if( $("#instituteOther").val().length == 1){
	        	$.getJSON("/wasp/autocomplete/getInstitutesForDisplay.do", { instituteNameFragment: $("#instituteOther").val() }, function(data) { $("input#instituteOther").autocomplete(data);} );
     	}
	 }
 </script>

<script language="JavaScript">
	<!--
	function selectChange(){
		if (document.f.instituteSelect.options[document.f.instituteSelect.selectedIndex].value == "other"){
			document.f.instituteOther.readOnly = false;
			document.f.instituteOther.focus();
		}
		else{
			document.f.instituteOther.value = "";
			document.f.instituteOther.readOnly = true;
		}
		return true;
	}
	//->
</script> 