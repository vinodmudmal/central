
/**
 *
 * JobDraftSoftwareDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the JobDraftSoftware Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.JobDraftSoftware;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class JobDraftSoftwareDaoImpl extends WaspDaoImpl<JobDraftSoftware> implements edu.yu.einstein.wasp.dao.JobDraftSoftwareDao {

	/**
	 * JobDraftSoftwareDaoImpl() Constructor
	 *
	 *
	 */
	public JobDraftSoftwareDaoImpl() {
		super();
		this.entityClass = JobDraftSoftware.class;
	}


	/**
	 * getJobDraftSoftwareByJobDraftSoftwareId(final Integer jobDraftSoftwareId)
	 *
	 * @param final Integer jobDraftSoftwareId
	 *
	 * @return jobDraftSoftware
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public JobDraftSoftware getJobDraftSoftwareByJobDraftSoftwareId (final Integer jobDraftSoftwareId) {
    		HashMap m = new HashMap();
		m.put("jobDraftSoftwareId", jobDraftSoftwareId);

		List<JobDraftSoftware> results = this.findByMap(m);

		if (results.size() == 0) {
			JobDraftSoftware rt = new JobDraftSoftware();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getJobDraftSoftwareBySoftwareIdJobdraftId(final Integer softwareId, final Integer jobdraftId)
	 *
	 * @param final Integer softwareId, final Integer jobdraftId
	 *
	 * @return jobDraftSoftware
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public JobDraftSoftware getJobDraftSoftwareBySoftwareIdJobdraftId (final Integer softwareId, final Integer jobdraftId) {
    		HashMap m = new HashMap();
		m.put("softwareId", softwareId);
		m.put("jobdraftId", jobdraftId);

		List<JobDraftSoftware> results = this.findByMap(m);

		if (results.size() == 0) {
			JobDraftSoftware rt = new JobDraftSoftware();
			return rt;
		}
		return results.get(0);
	}



}
