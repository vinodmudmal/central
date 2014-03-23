package edu.yu.einstein.wasp.chipseq.webpanels;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.yu.einstein.wasp.Strategy;
import edu.yu.einstein.wasp.exception.ChartException;
import edu.yu.einstein.wasp.exception.PanelException;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.service.MessageService;
import edu.yu.einstein.wasp.service.impl.WaspServiceImpl;
import edu.yu.einstein.wasp.viewpanel.Panel;
import edu.yu.einstein.wasp.viewpanel.PanelTab;
import edu.yu.einstein.wasp.viewpanel.WebContent;
import edu.yu.einstein.wasp.viewpanel.WebPanel;
import edu.yu.einstein.wasp.viewpanel.DataTabViewing.Status;

public class ChipSeqWebPanels {
	
	//see this webpage for model grid http://docs.sencha.com/extjs/4.2.1/#!/guide/grid
	
	static protected  Logger logger = LoggerFactory.getLogger(WaspServiceImpl.class);
	
	public static PanelTab getSummaryPanelTab(Status jobStatus, Job job, Strategy strategy,	String softwareName) {

		String name = "Summary";
		Map<String,Map<String,String>> fieldMap = new LinkedHashMap<String, Map<String,String>>();//iteration order maintains the order in which keys were inserted into the map
		Map<String,String> strategyMap = new HashMap<String,String>();
		strategyMap.put("data", strategy.getDisplayStrategy());
		strategyMap.put("width", "150");
		fieldMap.put("Strategy", strategyMap);		
		Map<String,String> descriptionMap = new HashMap<String,String>();
		descriptionMap.put("data", strategy.getDescription());
		descriptionMap.put("flex", "1");
		fieldMap.put("Description", descriptionMap);		
		Map<String,String> workflowMap = new HashMap<String,String>();
		workflowMap.put("data", job.getWorkflow().getName());
		workflowMap.put("width", "150");
		fieldMap.put("Workflow", workflowMap);
		Map<String,String> softwareMap = new HashMap<String,String>();
		softwareMap.put("data", softwareName);
		softwareMap.put("width", "200");
		fieldMap.put("Software", softwareMap);
		Map<String,String> statusMap = new HashMap<String,String>();
		statusMap.put("data", jobStatus.toString());//status of the aggregateAnalysis	
		statusMap.put("width", "150");
		fieldMap.put("Status", statusMap);

		
		Map<String,String> fieldDataMap = new LinkedHashMap<String,String>();//iteration order is the order in which keys were inserted into the map
		fieldDataMap.put("Strategy", strategy.getDisplayStrategy());
		fieldDataMap.put("Description", strategy.getDescription());
		fieldDataMap.put("Workflow", job.getWorkflow().getName());
		fieldDataMap.put("Software", softwareName);
		fieldDataMap.put("Status", jobStatus.toString());//status of the aggregateAnalysis	
		String dataModel = defineDataModel(name, fieldDataMap, fieldMap); 
		if(dataModel==null||dataModel.isEmpty()){
			return null;//do this with all
		}
		logger.debug("***********getSummaryPanelTab dataModel: "+dataModel);
		String dataStore = createDataStore(name,  fieldDataMap, fieldMap);
		logger.debug("***********getSummaryPanelTab dataStore: "+dataStore);
		//String gridPanel = createGridPanel(name, fieldDataMap);
		//logger.debug("***********getSummaryPanelTab gridPanel: "+gridPanel.toString());
		
		
		PanelTab panelTab = new PanelTab();

		panelTab.setName("Summary");
		//panelTab.setDescription("testDescription");
		WebPanel panel = new WebPanel();
		panel.setTitle("Summary");
		panel.setDescription("Summary");
		panel.setResizable(false);
		panel.setMaximizable(false);	

		panel.setOrder(1);
		WebContent content = new WebContent();
		content.setHtmlCode("<div id=\"summary-grid\"></div>");
		panel.setContent(content);
		//String script = "Ext.define('Summary',{ extend: 'Ext.data.Model', fields: [ 'Strategy', 'Description', 'Workflow', 'Software', 'Status' ] }); var store = Ext.create('Ext.data.Store', { model: 'Summary', data : [{Strategy: '"+strategy.getDisplayStrategy()+"', Description: '"+strategy.getDescription()+"', Workflow: '"+job.getWorkflow().getName()+"', Software: '" + softwareName+"', Status: '"+jobStatus.toString()+"'}] }); Ext.create('Ext.grid.Panel', { id:'summary-panel', store: store,  columns: [ {text: \"Strategyee\", width:150, dataIndex: 'Strategy'}, {text: \"Description\", flex:1, dataIndex: 'Description'}, {text: \"Workflow\", width: 150, dataIndex: 'Workflow'}, {text: \"Main Software\", width: 200, dataIndex: 'Software'}, {text: \"Status\", width: 150, dataIndex: 'Status'} ], renderTo:'summary-grid', height:500 }); ";
		//String script = dataModel + " var store = Ext.create('Ext.data.Store', { model: 'Summary', data : [{Strategy: '"+strategy.getDisplayStrategy()+"', Description: '"+strategy.getDescription()+"', Workflow: '"+job.getWorkflow().getName()+"', Software: '" + softwareName+"', Status: '"+jobStatus.toString()+"'}] }); Ext.create('Ext.grid.Panel', { id:'summary-panel', store: store,  columns: [ {text: \"Strategy\", width:150, dataIndex: 'Strategy'}, {text: \"Description\", flex:1, dataIndex: 'Description'}, {text: \"Workflow\", width: 150, dataIndex: 'Workflow'}, {text: \"Main Software\", width: 200, dataIndex: 'Software'}, {text: \"Status\", width: 150, dataIndex: 'Status'} ], renderTo:'summary-grid', height:500 }); ";
		String script = dataModel+" "+dataStore+" "+"Ext.create('Ext.grid.Panel', { id:'summary-panel', store: store,  columns: [ {text: \"Strategy\", width:150, dataIndex: 'Strategy'}, {text: \"Description\", flex:1, dataIndex: 'Description'}, {text: \"Workflow\", width: 150, dataIndex: 'Workflow'}, {text: \"Main Software\", width: 200, dataIndex: 'Software'}, {text: \"Status\", width: 150, dataIndex: 'Status'} ], renderTo:'summary-grid', height:500 }); ";
		logger.debug("***********getSummaryPanelTab script: "+script);
		panel.setExecOnRenderCode(script);
		panel.setExecOnExpandCode("var theDiv = $('summary-grid'); Ext.getCmp('summary-panel').setSize(theDiv.offsetWidth, undefined);");
		//panel.setExecOnResizeCode("Ext.getCmp('summary-panel').setSize(this.width, undefined);");
		panel.setExecOnResizeCode("var theDiv = $('summary-grid'); Ext.getCmp('summary-panel').setSize(theDiv.offsetWidth, undefined);");
		// does nothing: content.setScriptCode(script);
		panelTab.addPanel(panel);
		panelTab.setNumberOfColumns(1);

		return panelTab;
	}
	private static String createGridPanel(String name, Map<String,String> fieldDataMap, Map<String,Map<String,String>> fieldMap){
		if(name==null || name.isEmpty() || fieldDataMap==null || fieldDataMap.isEmpty()){
			return null;
		}
		
		StringBuffer stringBuffer = new StringBuffer();
		//Ext.create('Ext.grid.Panel', { id:'summary-panel', store: store,  columns: [ {text: \"Strategy\", width:150, dataIndex: 'Strategy'}, {text: \"Description\", flex:1, dataIndex: 'Description'}, {text: \"Workflow\", width: 150, dataIndex: 'Workflow'}, {text: \"Main Software\", width: 200, dataIndex: 'Software'}, {text: \"Status\", width: 150, dataIndex: 'Status'} ], renderTo:'summary-grid', height:500 }); ";		
		stringBuffer.append("Ext.create('Ext.grid.Panel', { id:'summary-panel', store: store,  columns: [ ");
		int counter = 0;
		for(String key : fieldMap.keySet()){//insert order guaranteed as this is a LinkedHashMap
			if(counter++ > 0){stringBuffer.append(",");}
			Map<String, String> internalMap = fieldMap.get(key);
			if(internalMap==null||internalMap.isEmpty()){
				return null;
			}
			
			stringBuffer.append(" " +key+": '"+internalMap.get("data")+"'");
		}
		stringBuffer.append(" }]});");
		logger.debug("***********createGridPanel: "+stringBuffer.toString());		
		
		return new String(stringBuffer);
	}
	
	private static String createDataStore(String name, Map<String,String> fieldDataMap, Map<String,Map<String,String>> fieldMap){
		if(name==null || name.isEmpty() || fieldDataMap==null || fieldDataMap.isEmpty()){
			return null;
		}
		
		StringBuffer stringBuffer = new StringBuffer();
		// var store = Ext.create('Ext.data.Store', { model: 'Summary', data : [{Strategy: '"+strategy.getDisplayStrategy()+"', Description: '"+strategy.getDescription()+"', Workflow: '"+job.getWorkflow().getName()+"', Software: '" + softwareName+"', Status: '"+jobStatus.toString()+"'}] });
		stringBuffer.append("var store = Ext.create('Ext.data.Store', { model: '"+name+"', data : [{ ");
		int counter = 0;
		//for(String key : fieldDataMap.keySet()){//insert order guaranteed as this is a LinkedHashMap
		//	if(counter++ > 0){stringBuffer.append(",");}
		//	stringBuffer.append(" " +key+": '"+fieldDataMap.get(key)+"'");
		//}
		for(String key : fieldMap.keySet()){//insert order guaranteed as this is a LinkedHashMap
			if(counter++ > 0){stringBuffer.append(",");}
			Map<String, String> internalMap = fieldMap.get(key);
			if(internalMap==null||internalMap.isEmpty()||!internalMap.containsKey("data")){
				return null;
			}
			stringBuffer.append(" " +key+": '"+internalMap.get("data")+"'");
		}
		stringBuffer.append(" }]});");
		logger.debug("***********createStore via fieldMap: "+stringBuffer.toString());		
		
		return new String(stringBuffer);
	}
	private static String defineDataModel(String name, Map<String,String> fieldDataMap, Map<String,Map<String,String>> fieldMap){
		if(name==null || name.isEmpty() || fieldDataMap==null || fieldDataMap.isEmpty()){
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer();
		//"Ext.define('Summary',{ extend: 'Ext.data.Model', fields: [ 'Strategy', 'Description', 'Workflow', 'Software', 'Status' ] });"
		stringBuffer.append("Ext.define('"+name+"', {extend: 'Ext.data.Model', fields: [ ");
		int counter = 0;
		//for(String key : fieldDataMap.keySet()){//insert order guaranteed as this is a LinkedHashMap
		for(String key : fieldMap.keySet()){//insert order guaranteed as this is a LinkedHashMap
			if(counter++ > 0){stringBuffer.append(",");}
			stringBuffer.append("'"+key+"'");
		}
		stringBuffer.append(" ]});");
		logger.debug("***********createModel via fieldMap: "+stringBuffer.toString());
		return new String(stringBuffer);
	}
	
	public static PanelTab getSamplePairsPanelTab(List<Sample> testSampleList, Map<Sample, List<Sample>> testSampleControlSampleListMap, Map<String,String> sampleIdControlIdCommandLineMap){
		
		PanelTab panelTab = new PanelTab();
		
		panelTab.setName("Sample Pairs");
		//panelTab.setDescription("testDescription");
		WebPanel panel = new WebPanel();
		panel.setTitle("Sample Pairs Used For Aggregate Analysis");
		panel.setDescription("Sample Pairs Used For Aggregate Analysis");
		panel.setResizable(true);
		panel.setMaximizable(true);	

		panel.setOrder(1);
		WebContent content = new WebContent();
		content.setHtmlCode("<div id=\"samplePairs-grid\"></div>");
		panel.setContent(content);
		/* for testing only
		String string1 = "the_test_sample";
		String string2 = "control_SAMPLE";
		String script = "Ext.define('SamplePairs',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample' ] }); var store = Ext.create('Ext.data.Store', { model: 'SamplePairs', data : [{TestSample: '"+string1+"', ControlSample: '"+string2+"'}, {TestSample: '"+string1+"', ControlSample: '"+string2+"'}] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:300, dataIndex: 'TestSample'}, {text: \"Control Sample\",  flex: 1, dataIndex: 'ControlSample'} ], renderTo:'samplePairs-grid', height: 300 });";
		*/
		StringBuffer stringBuffer = new StringBuffer();
		for(Sample testSample : testSampleList){
			List<Sample> controlSampleList = testSampleControlSampleListMap.get(testSample);
			for(Sample controlSample : controlSampleList){
				if(stringBuffer.length()>0){
					stringBuffer.append(", ");
				}
				String command = sampleIdControlIdCommandLineMap.get(testSample.getId().toString()+"::"+controlSample.getId().toString());
				if(command==null || command.isEmpty()){
					command = " ";
				}
				else{
					command = command.replaceAll("\\n", "");//the workunit may put a newline at the end, which is incompatible with Extjs grids
				}
				stringBuffer.append("{TestSample: '"+testSample.getName()+"', ControlSample: '"+controlSample.getName()+"', Command: '"+command+"'}");
			}
		}
		String theData = new String(stringBuffer);
		String script = "Ext.define('SamplePairs',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample', 'Command' ] }); var store = Ext.create('Ext.data.Store', { model: 'SamplePairs', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:250, dataIndex: 'TestSample'}, {text: \"Control Sample\",  width:250, dataIndex: 'ControlSample'}, {text: \"Command Line\",  width: 2000, dataIndex: 'Command'} ], renderTo:'samplePairs-grid', height: 300 });";
		panel.setExecOnRenderCode(script);
		panel.setExecOnExpandCode(" ");
		panel.setExecOnResizeCode(" ");
		// does nothing: content.setScriptCode(script);
		panelTab.addPanel(panel);
		panelTab.setNumberOfColumns(1);

		return panelTab;
	}

	public static PanelTab getSampleLibraryRunsPanelTab(List<Sample> testSampleList, Map<Sample, List<Sample>> sampleLibraryListMap, Map<Sample, List<String>> libraryRunInfoListMap){
		
		PanelTab panelTab = new PanelTab();
		
		panelTab.setName("Runs");
		//panelTab.setDescription("testDescription");
		WebPanel panel = new WebPanel();
		panel.setTitle("Runs Used For Aggregate Analysis");
		panel.setDescription("Runs Used For Aggregate Analysis");
		panel.setResizable(true);
		panel.setMaximizable(true);	

		panel.setOrder(1);
		WebContent content = new WebContent();
		content.setHtmlCode("<div id=\"sampleLibraryRuns-grid\"></div>");
		panel.setContent(content);
		/* for testing only
		String string1 = "the_test_sample";
		String string2 = "the run info";
		String script = "Ext.define('SampleRuns',{ extend: 'Ext.data.Model', fields: [ 'Sample', 'Run' ] }); var store = Ext.create('Ext.data.Store', { model: 'SampleRuns', data : [{Sample: '"+string1+"', Run: '"+string2+"'}, {Sample: '"+string1+"', Run: '"+string2+"'}] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Sample\",  width:300, dataIndex: 'Sample'}, {text: \"Run\",  flex: 1, dataIndex: 'Run'} ], renderTo:'sampleRuns-grid', height: 300 });";
		*/
		StringBuffer stringBuffer = new StringBuffer();
		for(Sample testSample : testSampleList){
			List<Sample> libraryList = sampleLibraryListMap.get(testSample);
			for(Sample library : libraryList){
				if(stringBuffer.length()>0){
					stringBuffer.append(", ");
				}
				List<String> runInfoList = libraryRunInfoListMap.get(library);
				StringBuffer completeRunInfoAsStringBuffer = new StringBuffer();
				for(String runInfo: runInfoList){
					if(completeRunInfoAsStringBuffer.length() > 0){
						completeRunInfoAsStringBuffer.append("<br />");
					}
					completeRunInfoAsStringBuffer.append(runInfo);
				}
				String completeRunInfoAsString = new String(completeRunInfoAsStringBuffer);
				stringBuffer.append("{Sample: '"+testSample.getName()+"', Library: '"+library.getName()+"', Runs: '"+completeRunInfoAsString+"'}");			
			}
		}
		String theData = new String(stringBuffer);
		String script = "Ext.define('SampleLibraryRuns',{ extend: 'Ext.data.Model', fields: [ 'Sample', 'Library', 'Runs' ] }); var store = Ext.create('Ext.data.Store', { model: 'SampleLibraryRuns', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Submitted Sample\",  width:250, dataIndex: 'Sample'}, {text: \"Library\",  width:250, dataIndex: 'Library'}, {text: \"Runs\",  flex: 1, dataIndex: 'Runs'} ], renderTo:'sampleLibraryRuns-grid', height: 300 });";
		
		panel.setExecOnRenderCode(script);
		panel.setExecOnExpandCode(" ");
		panel.setExecOnResizeCode(" ");
		// does nothing: content.setScriptCode(script);
		panelTab.addPanel(panel);
		panelTab.setNumberOfColumns(1);

		return panelTab;
	}
	public static PanelTab getFileTypeDefinitionsPanelTab(List<FileType> fileTypeList){
		
		PanelTab panelTab = new PanelTab();
		
		panelTab.setName("FileTypes");
		//panelTab.setDescription("testDescription");
		WebPanel panel = new WebPanel();
		panel.setTitle("FileTypes Generated By Aggregate Analysis");
		panel.setDescription("FileTypes Generated By Aggregate Analysis");
		panel.setResizable(true);
		panel.setMaximizable(true);	

		panel.setOrder(1);
		WebContent content = new WebContent();
		content.setHtmlCode("<div id=\"fileTypeDescription-grid\"></div>");
		panel.setContent(content);
		/* for testing only
		String string1 = "the_test_sample_filepage";
		String string2 = "control_SAMPLE_filepage";
		String script = "Ext.define('SamplePairs',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample' ] }); var store = Ext.create('Ext.data.Store', { model: 'SamplePairs', data : [{TestSample: '"+string1+"', ControlSample: '"+string2+"'}, {TestSample: '"+string1+"', ControlSample: '"+string2+"'}] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:300, dataIndex: 'TestSample'}, {text: \"Control Sample\",  flex: 1, dataIndex: 'ControlSample'} ], renderTo:'fileTypeDescription-grid', height: 300 });";
		*/
		
		StringBuffer stringBuffer = new StringBuffer();
		for(FileType fileType : fileTypeList){
			if(stringBuffer.length()>0){
				stringBuffer.append(", ");
			}
			stringBuffer.append("{FileType: '"+fileType.getName()+"', Description: '"+fileType.getDescription()+"'}");
		}
		String theData = new String(stringBuffer);
		String script = "Ext.define('FileTypeDescriptions',{ extend: 'Ext.data.Model', fields: [ 'FileType', 'Description', ] }); var store = Ext.create('Ext.data.Store', { model: 'FileTypeDescriptions', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"File Type\",  width:200, dataIndex: 'FileType'}, {text: \"Description\",  width:2000, dataIndex: 'Description'} ], renderTo:'fileTypeDescription-grid', height: 300 });";
		
		panel.setExecOnRenderCode(script);
		panel.setExecOnExpandCode(" ");
		panel.setExecOnResizeCode(" ");
		// does nothing: content.setScriptCode(script);
		panelTab.addPanel(panel);
		panelTab.setNumberOfColumns(1);

		return panelTab;
	}
	public static PanelTab getFilePanelTab(List<Sample> testSampleList, Map<Sample, List<Sample>> testSampleControlSampleListMap, FileType fileType, Map<String, FileHandle>  sampleIdControlIdFileTypeIdFileHandleMap, Map<FileHandle, String> fileHandleResolvedURLMap, Map<String, FileGroup> sampleIdControlIdFileTypeIdFileGroupMap){

		PanelTab panelTab = new PanelTab();
		
		panelTab.setName(fileType.getName());
		//panelTab.setDescription("testDescription");
		WebPanel panel = new WebPanel();
		panel.setTitle(fileType.getName());
		panel.setDescription(fileType.getName());
		panel.setResizable(true);
		panel.setMaximizable(true);	

		panel.setOrder(1);
		WebContent content = new WebContent();
		content.setHtmlCode("<div id=\""+fileType.getIName()+"-grid\"></div>");
		panel.setContent(content);
		/* for testing only
		String string1 = "the_test_sample_filepage";
		String string2 = "control_SAMPLE_filepage";
		String script = "Ext.define('SamplePairs',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample' ] }); var store = Ext.create('Ext.data.Store', { model: 'SamplePairs', data : [{TestSample: '"+string1+"', ControlSample: '"+string2+"'}, {TestSample: '"+string1+"', ControlSample: '"+string2+"'}] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:300, dataIndex: 'TestSample'}, {text: \"Control Sample\",  flex: 1, dataIndex: 'ControlSample'} ], renderTo:'"+fileType.getIName()+"-grid', height: 300 });";
		*/
		
		StringBuffer stringBuffer = new StringBuffer();
		for(Sample testSample : testSampleList){
			List<Sample> controlSampleList = testSampleControlSampleListMap.get(testSample);
			for(Sample controlSample : controlSampleList){
				if(stringBuffer.length()>0){
					stringBuffer.append(", ");
				}
				FileHandle fileHandle = sampleIdControlIdFileTypeIdFileHandleMap.get(testSample.getId().toString() + "::" + controlSample.getId().toString() + "::" + fileType.getId().toString());
				String resolvedURL = fileHandleResolvedURLMap.get(fileHandle);
				if(fileHandle==null){
					stringBuffer.append("{TestSample: '"+testSample.getName()+"', ControlSample: '"+controlSample.getName()+"', File: 'no file', MD5: ' ', Download: ' '}");
				}
				else{
					//stringBuffer.append("{TestSample: '"+testSample.getName()+"', ControlSample: '"+controlSample.getName()+"', File: '"+fileHandle.getFileName()+"', MD5: '"+fileHandle.getMd5hash()+"', Download: '"+"<a href=\""+resolvedURL+"\"><img src=\"ext/images/icons/fam/disk.png\" /></a>"+"'}");
					stringBuffer.append("{TestSample: '"+testSample.getName()+"', ControlSample: '"+controlSample.getName()+"', File: '"+fileHandle.getFileName()+"', MD5: '"+fileHandle.getMd5hash()+"', Download: '"+resolvedURL+"'}");
				}
			}
		}
		String theData = new String(stringBuffer);
		
		String script = "Ext.require(['Ext.grid.*','Ext.data.*','Ext.form.field.Number','Ext.form.field.Date','Ext.tip.QuickTipManager','Ext.selection.CheckboxModel','Wasp.RowActions']); Ext.tip.QuickTipManager.init(); Ext.define('SampleControlFile',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample', 'File', 'MD5', 'Download' ] }); var store = Ext.create('Ext.data.Store', { model: 'SampleControlFile', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:110, dataIndex: 'TestSample'}, {text: \"Control Sample\",  width:110, dataIndex: 'ControlSample'}, {text: \"File\",  flex: 1, dataIndex: 'File'}, {text: \"MD5\",  width:100, dataIndex: 'MD5', renderer: function(val, meta, record){var tip = record.get('MD5'); meta.tdAttr = 'data-qtip=\"' + tip + '\"'; return val; } }, {header:\"Download\", width: 500, xtype: 'rowactions', actions: [{iconCls: 'icon-clear-group', qtip: 'Download', callback: function(grip, record, action, idx, col, e, target){window.location=record.get('Download');}}], keepSelection: true     }, ], renderTo:'"+fileType.getIName()+"-grid', height: 300 });";
		
		
		//String script = "Ext.require(['Ext.grid.*','Ext.data.*','Ext.form.field.Number','Ext.form.field.Date','Ext.tip.QuickTipManager','Ext.selection.CheckboxModel','Wasp.RowActions']); Ext.define('SampleControlFile',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample', 'File', 'MD5', 'Download' ] }); var store = Ext.create('Ext.data.Store', { model: 'SampleControlFile', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:110, dataIndex: 'TestSample'}, {text: \"Control Sample\",  width:110, dataIndex: 'ControlSample'}, {text: \"File\",  flex: 1, dataIndex: 'File'}, {text: \"MD5\",  width:100, dataIndex: 'MD5'}, {text: \"Download\",  width:100, dataIndex: 'Download'}, ], renderTo:'"+fileType.getIName()+"-grid', height: 300 });";
		
		//String script = "Ext.define('SampleControlFile',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample', 'File', 'MD5', 'Download' ] }); var store = Ext.create('Ext.data.Store', { model: 'SampleControlFile', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:110, dataIndex: 'TestSample'}, {text: \"Control Sample\",  width:110, dataIndex: 'ControlSample'}, {text: \"File\",  flex: 1, dataIndex: 'File'}, {text: \"MD5\",  width:100, dataIndex: 'MD5'}, {text: \"Download\",  width:100, dataIndex: 'Download',  renderer: function(val, meta, record){return '<a href=\"'+val+'\">robert dubin</a>';} }, ], renderTo:'"+fileType.getIName()+"-grid', height: 300 });";
		
		//String script = "Ext.define('SampleControlFile',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample', 'File', 'MD5', 'Download' ] }); var store = Ext.create('Ext.data.Store', { model: 'SampleControlFile', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:110, dataIndex: 'TestSample'}, {text: \"Control Sample\",  width:110, dataIndex: 'ControlSample'}, {text: \"File\",  flex: 1, dataIndex: 'File'}, {text: \"MD5\",  width:100, dataIndex: 'MD5'}, {text: \"Download\",  width:100, dataIndex: 'Download', renderer: function(val, meta, record){meta.tdCls = 'icon-clear-group'} }, ], renderTo:'"+fileType.getIName()+"-grid', height: 300 });";
		//String script = "Ext.define('SampleControlFile',{ extend: 'Ext.data.Model', fields: [ 'TestSample', 'ControlSample', 'File', 'MD5', 'Download' ] }); var store = Ext.create('Ext.data.Store', { model: 'SampleControlFile', data : ["+theData+"] }); Ext.create('Ext.grid.Panel', { store: store, columns: [ {text: \"Test Sample\",  width:110, dataIndex: 'TestSample'}, {text: \"Control Sample\",  width:110, dataIndex: 'ControlSample'}, {text: \"File\",  flex: 1, dataIndex: 'File'}, {text: \"MD5\",  width:100, dataIndex: 'MD5'}, {text: \"Download\",  width:100, dataIndex: 'Download', renderer: function(value){return Ext.String.format('<a href=\"{0}\">lesliedownload</a>', value);} }, ], renderTo:'"+fileType.getIName()+"-grid', height: 300 });";
		
		panel.setExecOnRenderCode(script);
		panel.setExecOnExpandCode(" ");
		panel.setExecOnResizeCode(" ");
		// does nothing: content.setScriptCode(script);
		panelTab.addPanel(panel);
		panelTab.setNumberOfColumns(1);

		return panelTab;
	}
}
