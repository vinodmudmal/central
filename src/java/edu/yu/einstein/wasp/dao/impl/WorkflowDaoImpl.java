
/**
 *
 * WorkflowImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the Workflow object
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.Workflow;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class WorkflowDaoImpl extends WaspDaoImpl<Workflow> implements edu.yu.einstein.wasp.dao.WorkflowDao {

  public WorkflowDaoImpl() {
    super();
    this.entityClass = Workflow.class;
  }

  @SuppressWarnings("unchecked")
  @Transactional
  public Workflow getWorkflowByWorkflowId (final int workflowId) {
    HashMap m = new HashMap();
    m.put("workflowId", workflowId);
    List<Workflow> results = (List<Workflow>) this.findByMap((Map) m);
    if (results.size() == 0) {
      Workflow rt = new Workflow();
      return rt;
    }
    return (Workflow) results.get(0);
  }


  @SuppressWarnings("unchecked")
  @Transactional
  public Workflow getWorkflowByIName (final String iName) {
    HashMap m = new HashMap();
    m.put("iName", iName);
    List<Workflow> results = (List<Workflow>) this.findByMap((Map) m);
    if (results.size() == 0) {
      Workflow rt = new Workflow();
      return rt;
    }
    return (Workflow) results.get(0);
  }


  @SuppressWarnings("unchecked")
  @Transactional
  public Workflow getWorkflowByName (final String name) {
    HashMap m = new HashMap();
    m.put("name", name);
    List<Workflow> results = (List<Workflow>) this.findByMap((Map) m);
    if (results.size() == 0) {
      Workflow rt = new Workflow();
      return rt;
    }
    return (Workflow) results.get(0);
  }


}

