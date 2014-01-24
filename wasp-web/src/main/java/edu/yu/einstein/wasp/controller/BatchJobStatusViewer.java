package edu.yu.einstein.wasp.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.yu.einstein.wasp.controller.util.BatchJobTreeModel;
import edu.yu.einstein.wasp.service.BatchJobStatusViewerService;

@RequestMapping("/batchJobStatusViewer")
@Controller
public class BatchJobStatusViewer extends WaspController {
	
	@Autowired
	BatchJobStatusViewerService statusViewerService;

	@RequestMapping(value="/list.do", method = RequestMethod.GET)
	@PreAuthorize("hasRole('su') or hasRole('fm') or hasRole('ft')")
	public String getBatchJobStatusViewerView(ModelMap m){
		return "batchJobStatusViewer/list";
	}
	
	@RequestMapping(value="/getDetailsJson", method = RequestMethod.GET)
	public @ResponseBody String getNodeJson(@RequestParam("node") String node, HttpServletResponse response) {
		BatchJobTreeModel model = (BatchJobTreeModel) statusViewerService.getModel(node);
		if (node.equals("node_root"))
			model.addChildren(statusViewerService.getJobListAll());
		return model.getAsJSON().toString();
	}

}
