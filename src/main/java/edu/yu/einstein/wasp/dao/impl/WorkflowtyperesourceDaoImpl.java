
/**
 *
 * WorkflowtyperesourceDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the Workflowtyperesource Dao Impl
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

import edu.yu.einstein.wasp.model.Workflowtyperesource;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class WorkflowtyperesourceDaoImpl extends WaspDaoImpl<Workflowtyperesource> implements edu.yu.einstein.wasp.dao.WorkflowtyperesourceDao {

	/**
	 * WorkflowtyperesourceDaoImpl() Constructor
	 *
	 *
	 */
	public WorkflowtyperesourceDaoImpl() {
		super();
		this.entityClass = Workflowtyperesource.class;
	}


	/**
	 * getWorkflowtyperesourceByWorkflowtyperesourceId(final Integer workflowtyperesourceId)
	 *
	 * @param final Integer workflowtyperesourceId
	 *
	 * @return workflowtyperesource
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Workflowtyperesource getWorkflowtyperesourceByWorkflowtyperesourceId (final Integer workflowtyperesourceId) {
    		HashMap m = new HashMap();
		m.put("workflowtyperesourceId", workflowtyperesourceId);

		List<Workflowtyperesource> results = (List<Workflowtyperesource>) this.findByMap((Map) m);

		if (results.size() == 0) {
			Workflowtyperesource rt = new Workflowtyperesource();
			return rt;
		}
		return (Workflowtyperesource) results.get(0);
	}



	/**
	 * getWorkflowtyperesourceByWorkflowIdTypeResourceId(final Integer workflowId, final Integer typeResourceId)
	 *
	 * @param final Integer workflowId, final Integer typeResourceId
	 *
	 * @return workflowtyperesource
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Workflowtyperesource getWorkflowtyperesourceByWorkflowIdTypeResourceId (final Integer workflowId, final Integer typeResourceId) {
    		HashMap m = new HashMap();
		m.put("workflowId", workflowId);
		m.put("typeResourceId", typeResourceId);

		List<Workflowtyperesource> results = (List<Workflowtyperesource>) this.findByMap((Map) m);

		if (results.size() == 0) {
			Workflowtyperesource rt = new Workflowtyperesource();
			return rt;
		}
		return (Workflowtyperesource) results.get(0);
	}



}

