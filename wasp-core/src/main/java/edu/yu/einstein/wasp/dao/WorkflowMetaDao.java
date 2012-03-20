
/**
 *
 * WorkflowMetaDao.java 
 * @author echeng (table2type.pl)
 *  
 * the WorkflowMeta Dao 
 *
 *
 **/

package edu.yu.einstein.wasp.dao;

import java.util.List;

import edu.yu.einstein.wasp.model.WorkflowMeta;


public interface WorkflowMetaDao extends WaspDao<WorkflowMeta> {

  public WorkflowMeta getWorkflowMetaByWorkflowMetaId (final Integer workflowMetaId);

  public WorkflowMeta getWorkflowMetaByKWorkflowId (final String k, final Integer workflowId);

  public void updateByWorkflowId (final int workflowId, final List<WorkflowMeta> metaList);




}

