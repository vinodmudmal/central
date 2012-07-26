package edu.yu.einstein.wasp.load;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import edu.yu.einstein.wasp.load.service.WorkflowLoadService;
import edu.yu.einstein.wasp.model.WorkflowMeta;


/**
 * update/inserts db copy of subtype sample from bean definition
 * takes in  properties
 *   - iname / internalname
 *   - name / label
 *   - uifields (List<UiFields>)
 *   - samplesubtype (Set<SampleSubtype>)  allowable samples for workflow
 *   - pageFlow (List<String>)
 *   - meta (List<WorkflowMeta>)
 *
 */

@Component
public class WorkflowLoader extends WaspResourceLoader {

  @Autowired
  private WorkflowLoadService workflowLoadService;

  private List<String> pageFlowOrder; 
  public void setPageFlowOrder(List<String> pageFlowOrder) {this.pageFlowOrder = pageFlowOrder; }

  private Set<String> sampleSubtypes;
  public void setSampleSubtypes(Set<String> sampleSubtypes) {this.sampleSubtypes = sampleSubtypes; }

  private List<WorkflowMeta> meta; 
  public void setMeta(List<WorkflowMeta> workflowMeta) {this.meta = workflowMeta; }
  
  private Integer isActive;
  
  public Integer getIsActive() {
	return isActive;
  }
	
  public void setIsActive(Integer isActive) {
	this.isActive = isActive;
  }

 
  @Override 
  public void afterPropertiesSet() throws Exception {
	  String pageFlowString = StringUtils.collectionToDelimitedString(pageFlowOrder, ";");

	  // Add meta for Inserting UiPageflow fields
	  WorkflowMeta pageFlowWorkflowMeta = new WorkflowMeta();
	  pageFlowWorkflowMeta.setK("workflow.submitpageflow");
	  pageFlowWorkflowMeta.setV(pageFlowString);
	  pageFlowWorkflowMeta.setPosition(0);
	  meta.add(pageFlowWorkflowMeta);
	  
	  workflowLoadService.update(iname, name, isActive, meta, pageFlowOrder, sampleSubtypes);
	  workflowLoadService.updateUiFields(uiFields);
  }
}

