
/**
 *
 * UserroleDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the Userrole Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.Userrole;


@Transactional("entityManager")
@Repository
public class UserroleDaoImpl extends WaspDaoImpl<Userrole> implements edu.yu.einstein.wasp.dao.UserroleDao {

	/**
	 * UserroleDaoImpl() Constructor
	 *
	 *
	 */
	public UserroleDaoImpl() {
		super();
		this.entityClass = Userrole.class;
	}


	/**
	 * getUserroleByUserroleId(final int userroleId)
	 *
	 * @param final int userroleId
	 *
	 * @return userrole
	 */

	@Override
	@Transactional("entityManager")
	public Userrole getUserroleByUserroleId (final int userroleId) {
    		HashMap<String, Integer> m = new HashMap<String, Integer>();
		m.put("id", userroleId);

		List<Userrole> results = this.findByMap(m);

		if (results.size() == 0) {
			Userrole rt = new Userrole();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getUserroleByUserIdRoleId(final int UserId, final int roleId)
	 *
	 * @param final int UserId, final int roleId
	 *
	 * @return userrole
	 */

	@Override
	@Transactional("entityManager")
	public Userrole getUserroleByUserIdRoleId (final int userId, final int roleId) {
    		HashMap<String, Integer> m = new HashMap<String, Integer>();
		m.put("userId", userId);
		m.put("roleId", roleId);

		List<Userrole> results = this.findByMap(m);

		if (results.size() == 0) {
			Userrole rt = new Userrole();
			return rt;
		}
		return results.get(0);
	}



}

