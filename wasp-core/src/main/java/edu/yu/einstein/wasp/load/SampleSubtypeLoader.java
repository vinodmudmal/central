
package edu.yu.einstein.wasp.load;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.yu.einstein.wasp.load.service.SampleSubtypeLoadService;
import edu.yu.einstein.wasp.model.SampleSubtypeMeta;


/**
 * update/inserts db copy of subtype sample from bean definition
 * takes in  properties
 *   - iname
 *   - name
 *   - uifields (List<UiFields)
 *   - sampleType (string to sampletype iname table)
 *
 */

@Component
public class SampleSubtypeLoader extends WaspLoader implements InitializingBean {

  @Autowired
  private SampleSubtypeLoadService sampleSubtypeLoadService;
	
  private String sampleTypeString; 
  public void setSampleType(String sampleTypeString) {this.sampleTypeString = sampleTypeString; }

  private List<SampleSubtypeMeta> meta;
  public void setMeta(List<SampleSubtypeMeta> sampleSubtypeMeta) {this.meta = sampleSubtypeMeta; }
  
  public void setMetaFromWrapper(MetaLoadWrapper metaLoadWrapper){
	  meta = metaLoadWrapper.getMeta(SampleSubtypeMeta.class);
  }
  
  private Integer isActive;
  
  public Integer getIsActive() {
	return isActive;
  }
	
  public void setIsActive(Integer isActive) {
	this.isActive = isActive;
  }
  
  private List<String> compatibleResourcesByIName; 
  public void setCompatibleResourcesByIName(List<String> compatibleResourcesByIName) {this.compatibleResourcesByIName = compatibleResourcesByIName; }

  
  public SampleSubtypeLoader(){}
  
  public SampleSubtypeLoader(SampleSubtypeLoader inheritFromLoadService){
	  super(inheritFromLoadService);
  }
  
  private String applicableRoles;
  
  public String getApplicableRoles(){
	  return this.applicableRoles;
  }
  
  public void setApplicableRoles(String applicableRolesString){
	  sampleSubtypeLoadService.validateApplicableRoles(applicableRolesString); // throws runtime exception if not valid
	  this.applicableRoles = applicableRolesString;
  }


  @Override 
  public void afterPropertiesSet() throws Exception {
	  sampleSubtypeLoadService.update(sampleTypeString, meta, iname, name, isActive,  uiFields, applicableRoles, compatibleResourcesByIName);
	  sampleSubtypeLoadService.updateUiFields(uiFields); 

  }

}

