
/**
 *
 * RolesetDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the Roleset Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.Roleset;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class RolesetDaoImpl extends WaspDaoImpl<Roleset> implements edu.yu.einstein.wasp.dao.RolesetDao {

	/**
	 * RolesetDaoImpl() Constructor
	 *
	 *
	 */
	public RolesetDaoImpl() {
		super();
		this.entityClass = Roleset.class;
	}


	/**
	 * getRolesetByRolesetId(final int rolesetId)
	 *
	 * @param final int rolesetId
	 *
	 * @return roleset
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Roleset getRolesetByRolesetId (final int rolesetId) {
    		HashMap m = new HashMap();
		m.put("rolesetId", rolesetId);

		List<Roleset> results = this.findByMap(m);

		if (results.size() == 0) {
			Roleset rt = new Roleset();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getRolesetByParentroleIdChildroleId(final int parentroleId, final int childroleId)
	 *
	 * @param final int parentroleId, final int childroleId
	 *
	 * @return roleset
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Roleset getRolesetByParentroleIdChildroleId (final int parentroleId, final int childroleId) {
    		HashMap m = new HashMap();
		m.put("parentroleId", parentroleId);
		m.put("childroleId", childroleId);

		List<Roleset> results = this.findByMap(m);

		if (results.size() == 0) {
			Roleset rt = new Roleset();
			return rt;
		}
		return results.get(0);
	}



}
