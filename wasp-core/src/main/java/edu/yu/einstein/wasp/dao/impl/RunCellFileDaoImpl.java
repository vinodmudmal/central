
/**
 *
 * RunCellfileDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the RunCellFile Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.RunCellFile;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class RunCellFileDaoImpl extends WaspDaoImpl<RunCellFile> implements edu.yu.einstein.wasp.dao.RunCellFileDao {

	/**
	 * RunCellfileDaoImpl() Constructor
	 *
	 *
	 */
	public RunCellFileDaoImpl() {
		super();
		this.entityClass = RunCellFile.class;
	}


	/**
	 * getRunCellfileByRunCellfileId(final int runCellfileId)
	 *
	 * @param final int runCellfileId
	 *
	 * @return runCellFile
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public RunCellFile getRunCellFileByRunCellfileId (final int runCellfileId) {
    		HashMap m = new HashMap();
		m.put("runCellfileId", runCellfileId);

		List<RunCellFile> results = this.findByMap(m);

		if (results.size() == 0) {
			RunCellFile rt = new RunCellFile();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getRunCellfileByFileId(final int fileId)
	 *
	 * @param final int fileId
	 *
	 * @return runCellFile
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public RunCellFile getRunCellFileByFileId (final int fileId) {
    		HashMap m = new HashMap();
		m.put("fileId", fileId);

		List<RunCellFile> results = this.findByMap(m);

		if (results.size() == 0) {
			RunCellFile rt = new RunCellFile();
			return rt;
		}
		return results.get(0);
	}



}
