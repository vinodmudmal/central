/**
 * Created by Wasp System Eclipse Plugin
 * @author 
 */
package edu.yu.einstein.wasp.macstwo.plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import edu.yu.einstein.wasp.exception.PanelException;
import edu.yu.einstein.wasp.exception.WaspMessageBuildingException;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.grid.file.GridFileService;
import edu.yu.einstein.wasp.integration.messages.tasks.BatchJobTask;
import edu.yu.einstein.wasp.integration.messaging.MessageChannelRegistry;
import edu.yu.einstein.wasp.interfacing.Hyperlink;
import edu.yu.einstein.wasp.interfacing.plugin.BatchJobProviding;
import edu.yu.einstein.wasp.interfacing.plugin.WebInterfacing;
import edu.yu.einstein.wasp.interfacing.plugin.cli.ClientMessageI;
import edu.yu.einstein.wasp.macstwo.service.MacstwoService;
import edu.yu.einstein.wasp.macstwo.web.service.impl.MacstwoWebServiceImpl;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.Software;
import edu.yu.einstein.wasp.plugin.WaspPlugin;
import edu.yu.einstein.wasp.service.WaspMessageHandlingService;
import edu.yu.einstein.wasp.viewpanel.FileDataTabViewing;
import edu.yu.einstein.wasp.viewpanel.JobDataTabViewing;
import edu.yu.einstein.wasp.viewpanel.PanelTab;

/**
 * @author 
 */
public class MacstwoPlugin extends WaspPlugin 
		implements 
			BatchJobProviding,
			WebInterfacing,
			FileDataTabViewing,
			ClientMessageI,
			JobDataTabViewing{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1910399102604365552L;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("waspMessageHandlingServiceImpl") // more than one class of type WaspMessageHandlingService so must specify
	private WaspMessageHandlingService waspMessageHandlingService;

	@Autowired
	private GridHostResolver waspGridHostResolver;

	@Autowired
	private GridFileService waspGridFileService;

	@Autowired
	private MessageChannelRegistry messageChannelRegistry;
	
	@Autowired
	@Qualifier("macstwo")
	private Software macstwo;
	
	@Autowired 
	private MacstwoService macstwoService;

	
	public static final String FLOW_NAME = "edu.yu.einstein.wasp.macstwo.mainFlow";

	public MacstwoPlugin(String iName, Properties waspSiteProperties, MessageChannel channel) {
		super(iName, waspSiteProperties, channel);
	}

	/**
	 * Methods with the signature: Message<String> methodname(Message<String> m)
	 * are automatically accessible to execution by the command line.  Messages sent are generally
	 * free text or JSON formatted data.  These methods should not implement their own functionality,
	 * rather, they should either return information in a message (text) or trigger events through
	 * integration messaging (e.g. launch a job).
	 * 
	 * @param m
	 * @return
	 */
	public Message<String> helloWorld(Message<String> m) {
		if (m.getPayload() == null || m.getHeaders().containsKey("help") || m.getPayload().toString().equals("help"))
			return helloWorldHelp();

		logger.info("Hello World!");
			
		return (Message<String>) MessageBuilder.withPayload("sent a Hello World").build();
	}
	
	private Message<String> helloWorldHelp() {
		String mstr = "\nMacstwo plugin: hello world!\n" +
				"wasp -T macstwo -t helloWorld\n";
		return MessageBuilder.withPayload(mstr).build();
	}

	public Message<String> launchTestFlow(Message<String> m) {
		if (m.getPayload() == null || m.getHeaders().containsKey("help") || m.getPayload().toString().equals("help"))
			return launchTestFlowHelp();
		
		logger.info("launching test flow");
		
		try {
			//Integer id = getIDFromMessage(m);
			//if (id == null)
			//	return MessageBuilder.withPayload("Unable to determine id from message: " + m.getPayload().toString()).build();
			
			Map<String, String> jobParameters = new HashMap<String, String>();
			jobParameters.put("test", new Date().toString());
			//jobParameters.put(ChipSeqSoftwareJobParameters.TEST_LIBRARY_CELL_ID_LIST, "39");
			//wrong job jobParameters.put(WaspSoftwareJobParameters.TEST_LIBRARY_CELL_ID_LIST, "22");
			//jobParameters.put(ChipSeqSoftwareJobParameters.CONTROL_LIBRARY_CELL_ID_LIST, "37");
			//logger.info("Sending launch message with flow " + FLOW_NAME + " and id: " + id);
			//jobParameters.put(WaspJobParameters.TEST_ID, id.toString());
			waspMessageHandlingService.launchBatchJob(FLOW_NAME, jobParameters);
			//return (Message<String>) MessageBuilder.withPayload("Initiating test flow on id " + id).build();
			return (Message<String>) MessageBuilder.withPayload("Initiating test macstwo flow ").build();
		} catch (WaspMessageBuildingException e1) {
			logger.warn("unable to build message to launch batch job " + FLOW_NAME);
			return MessageBuilder.withPayload("Unable to launch batch job " + FLOW_NAME).build();
		}
		
	}
	
	private Message<String> launchTestFlowHelp() {
		String mstr = "\nMacstwo plugin: launch the test flow.\n" +
				"wasp -T macstwo -t launchTestFlow -m \'{id:\"1\"}\'\n";
		return MessageBuilder.withPayload(mstr).build();
	}
	
	/**
	 * 
	 * @param m
	 * @return 
	 */
	public Integer getIDFromMessage(Message<String> m) {
		Integer id = null;
		
		JSONObject jo;
		try {
			jo = new JSONObject(m.getPayload().toString());
			if (jo.has("id")) {
				id = new Integer(jo.get("id").toString());
			} 
		} catch (JSONException e) {
			logger.warn("unable to parse JSON");
		}
		return id;
	}
	

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getBatchJobName(String batchJobType) {
		if (batchJobType.equals(BatchJobTask.GENERIC)) 
			return FLOW_NAME;
		return null;
	}
	
	/** 
	 * {@inheritDoc} 
	 */
	@Override
	public Hyperlink getDescriptionPageHyperlink() {
		return new Hyperlink("macstwo.hyperlink.label", "/macstwo/displayDescription.do");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status getStatus(FileGroup fileGroup) {
		return Status.UNKNOWN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PanelTab getViewPanelTab(FileGroup fileGroup) throws PanelException {
		return null;
	}
	
	
	/**
	 * Wasp plugins implement InitializingBean to give authors an opportunity to initialize at runtime.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * Wasp plugins implement DisposableBean to give authors the ability to tear down on shutdown.
	 */
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}
	@Override
	public Set<PanelTab> getViewPanelTabs(Job job) throws PanelException{
		return ((MacstwoWebServiceImpl)macstwoService).getMacstwoDataToDisplay(job);
	}

	@Override
	public Status getStatus(Job job) {
		// TODO Auto-generated method stub
		return null;
	}
}
