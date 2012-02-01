package edu.yu.einstein.wasp.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.File;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.StateService;
import edu.yu.einstein.wasp.service.StatejobService;

/**
 * DoCommandStateProcessor
 * sends an invoice to proper party
 *
 * ** **	currently test to echo hello world via System.exec
 *
 * throws an retryable exception unless
 * a sibling state for that job has hit a status of property status 
 * if it does, update that sibling status w/ a new target status
 *
 * this only supports the first task of the job, anticipated
 * rewrite to take a mapping of types, list of statuses, etc.
 * 
 */

@Component
@Transactional
public class DoCommandFileTasklet extends org.springframework.batch.core.step.tasklet.SystemCommandTasklet {

	@Autowired
	StateService stateService;

	@Autowired
	FileService fileService;

	@Autowired
	StatejobService statejobService;

	/**
	 * filename key for stateService
	 *
	 */
	protected String filename;
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilename() {
		return this.filename;
	}


	/**
	 *
	 * list of command parameters to be evaluated
	 *
	 * @params list of parameters
	 *
	 */
	protected List<String> params;
	public void setParams(List<String> params) { 
		this.params = params; 
		setCommand("/tmp/abc.pl WRONG COMMAND");
	} 
	public List<String> getParams() { return this.params;}


	/**
	 * beforeStep
	 * compiles 'command' from evaluated params 
	 * 
	 * @param stepExecution
	 *
	 */
	@Override
	@Transactional
	public void beforeStep(StepExecution stepExecution) {
		Map m = new HashMap(); 
		File file = fileService.getFileByFilelocation(filename);
		m.put("file", file); 

		m.put("filename", filename); 

		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("m", m);

		List<String> parsedParams = new ArrayList<String>();
		ExpressionParser parser = new SpelExpressionParser();
		for (String s: this.params) {
			parsedParams.add((String) parser.parseExpression(s).getValue(context));
		}

		this.setCommand(org.springframework.util.StringUtils.collectionToDelimitedString(parsedParams, " "));
	}

}

