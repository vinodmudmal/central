
/**
 *
 * WorkflowresourcecategoryDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the Workflowresourcecategory Dao Impl
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
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.Workflowresourcecategory;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class WorkflowresourcecategoryDaoImpl extends WaspDaoImpl<Workflowresourcecategory> implements edu.yu.einstein.wasp.dao.WorkflowresourcecategoryDao {

	/**
	 * WorkflowresourcecategoryDaoImpl() Constructor
	 *
	 *
	 */
	public WorkflowresourcecategoryDaoImpl() {
		super();
		this.entityClass = Workflowresourcecategory.class;
	}


	/**
	 * getWorkflowresourcecategoryByWorkflowresourcecategoryId(final Integer workflowresourcecategoryId)
	 *
	 * @param final Integer workflowresourcecategoryId
	 *
	 * @return workflowresourcecategory
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Workflowresourcecategory getWorkflowresourcecategoryByWorkflowresourcecategoryId (final Integer workflowresourcecategoryId) {
    		HashMap m = new HashMap();
		m.put("workflowresourcecategoryId", workflowresourcecategoryId);

		List<Workflowresourcecategory> results = (List<Workflowresourcecategory>) this.findByMap((Map) m);

		if (results.size() == 0) {
			Workflowresourcecategory rt = new Workflowresourcecategory();
			return rt;
		}
		return (Workflowresourcecategory) results.get(0);
	}



	/**
	 * getWorkflowresourcecategoryByWorkflowIdResourcecategoryId(final Integer workflowId, final Integer resourcecategoryId)
	 *
	 * @param final Integer workflowId, final Integer resourcecategoryId
	 *
	 * @return workflowresourcecategory
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Workflowresourcecategory getWorkflowresourcecategoryByWorkflowIdResourcecategoryId (final Integer workflowId, final Integer resourcecategoryId) {
    		HashMap m = new HashMap();
		m.put("workflowId", workflowId);
		m.put("resourcecategoryId", resourcecategoryId);

		List<Workflowresourcecategory> results = (List<Workflowresourcecategory>) this.findByMap((Map) m);

		if (results.size() == 0) {
			Workflowresourcecategory rt = new Workflowresourcecategory();
			return rt;
		}
		return (Workflowresourcecategory) results.get(0);
	}



}
