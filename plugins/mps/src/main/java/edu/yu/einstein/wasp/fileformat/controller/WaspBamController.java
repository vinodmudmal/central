package edu.yu.einstein.wasp.fileformat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.yu.einstein.wasp.controller.WaspController;

@Controller
@RequestMapping("/wasp-bam")
public class WaspBamController extends WaspController {

	@RequestMapping(value="/description", method=RequestMethod.GET)
	public String displayDescription(ModelMap m){
		return "bam/description";
	}
	
	@RequestMapping(value="/details", method=RequestMethod.GET)
	public String displayDetails(ModelMap m){
		return "bam/details";
	}
	

}