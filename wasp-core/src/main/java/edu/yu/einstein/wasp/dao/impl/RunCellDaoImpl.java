
/**
 *
 * RunCellDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the RunCell Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.RunCell;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class RunCellDaoImpl extends WaspDaoImpl<RunCell> implements edu.yu.einstein.wasp.dao.RunCellDao {

	/**
	 * RunCellDaoImpl() Constructor
	 *
	 *
	 */
	public RunCellDaoImpl() {
		super();
		this.entityClass = RunCell.class;
	}


	/**
	 * getRunCellByRunCellId(final int runCellId)
	 *
	 * @param final int runCellId
	 *
	 * @return runCell
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public RunCell getRunCellByRunCellId (final int runCellId) {
    		HashMap m = new HashMap();
		m.put("runCellId", runCellId);

		List<RunCell> results = this.findByMap(m);

		if (results.size() == 0) {
			RunCell rt = new RunCell();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getRunCellByRunIdResourcelaneId(final int runId, final int resourcecellId)
	 *
	 * @param final int runId, final int resourcecellId
	 *
	 * @return runCell
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public RunCell getRunCellByRunIdResourcelaneId (final int runId, final int resourcecellId) {
    		HashMap m = new HashMap();
		m.put("runId", runId);
		m.put("resourcecellId", resourcecellId);

		List<RunCell> results = this.findByMap(m);

		if (results.size() == 0) {
			RunCell rt = new RunCell();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getRunCellBySampleIdRunId(final int sampleId, final int runId)
	 *
	 * @param final int sampleId, final int runId
	 *
	 * @return runCell
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public RunCell getRunCellBySampleIdRunId (final int sampleId, final int runId) {
    		HashMap m = new HashMap();
		m.put("sampleId", sampleId);
		m.put("runId", runId);

		List<RunCell> results = this.findByMap(m);

		if (results.size() == 0) {
			RunCell rt = new RunCell();
			return rt;
		}
		return results.get(0);
	}



}
