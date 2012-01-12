package edu.yu.einstein.wasp.controller;

import edu.yu.einstein.wasp.model.*;
import edu.yu.einstein.wasp.service.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@Transactional
@RequestMapping("/jobsubmit/chipSeq")
public class ChipSeqJobSubmissionController extends JobSubmissionController {

	@Autowired
	protected JobDraftService jobDraftService;

	@Autowired
	protected SampleDraftService sampleDraftService;

	@RequestMapping(value="/pair/{jobDraftId}.do", method=RequestMethod.GET)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String showChipSeqPairForm (@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {

		JobDraft jobDraft = jobDraftService.getJobDraftByJobDraftId(jobDraftId);

		List<SampleDraft> samples=sampleDraftService.getSampleDraftByJobId(jobDraftId);

    // detect old paired samplestring
		m.put("jobDraftDb", jobDraft);
		m.put("jobDraft", jobDraft);
		m.put("samples", samples);

		return "jobsubmit/chipseqform";
	}

	@RequestMapping(value="/pair/{jobDraftId}.do", method=RequestMethod.POST)
	@PreAuthorize("hasRole('jd-' + #jobDraftId)")
	public String updateChipSeqPair(@PathVariable("jobDraftId") Integer jobDraftId, ModelMap m) {

    Map params = request.getParameterMap();

    JobDraft jobDraftDb = jobDraftService.getJobDraftByJobDraftId(jobDraftId);
    List<SampleDraft> samples =  jobDraftDb.getSampleDraft();

    String pairMetaString = ""; 

    for (SampleDraft sd: samples) {
      String controlId = "";
      try {
        controlId = ((String[])params.get("sd_" + sd.getSampleDraftId()))[0];

      } catch (Exception e) {
      }

      if (controlId.equals("")) { continue; }
      pairMetaString += sd.getSampleDraftId() + ":" + controlId + ";" ;

    }

    // TODO, remove old paired sample for jobdraft

    JobDraftMeta jdm = new JobDraftMeta(); 
    jdm.setJobdraftId(jobDraftDb.getJobDraftId());
    jdm.setK("chipSeq.pairedSamples");
    jdm.setV(pairMetaString); 
    jobDraftMetaService.save(jdm);

    return nextPage(jobDraftDb);
  }


}

