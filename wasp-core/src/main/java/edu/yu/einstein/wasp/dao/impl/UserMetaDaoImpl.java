
/**
 *
 * UserMetaDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the UserMeta Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.UserMeta;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class UserMetaDaoImpl extends WaspDaoImpl<UserMeta> implements edu.yu.einstein.wasp.dao.UserMetaDao {

	/**
	 * UserMetaDaoImpl() Constructor
	 *
	 *
	 */
	public UserMetaDaoImpl() {
		super();
		this.entityClass = UserMeta.class;
	}


	/**
	 * getUserMetaByUserMetaId(final int userMetaId)
	 *
	 * @param final int userMetaId
	 *
	 * @return userMeta
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public UserMeta getUserMetaByUserMetaId (final int userMetaId) {
    		HashMap m = new HashMap();
		m.put("userMetaId", userMetaId);

		List<UserMeta> results = this.findByMap(m);

		if (results.size() == 0) {
			UserMeta rt = new UserMeta();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getUserMetaByKUserId(final String k, final int UserId)
	 *
	 * @param final String k, final int UserId
	 *
	 * @return userMeta
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public UserMeta getUserMetaByKUserId (final String k, final int UserId) {
    		HashMap m = new HashMap();
		m.put("k", k);
		m.put("UserId", UserId);

		List<UserMeta> results = this.findByMap(m);

		if (results.size() == 0) {
			UserMeta rt = new UserMeta();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * updateByUserId (final string area, final int UserId, final List<UserMeta> metaList)
	 *
	 * @param UserId
	 * @param metaList
	 *
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateByUserId (final String area, final int UserId, final List<UserMeta> metaList) {
		entityManager.createNativeQuery("delete from usermeta where UserId=:UserId and k like :area").setParameter("UserId", UserId).setParameter("area", area + ".%").executeUpdate();

		for (UserMeta m:metaList) {
			m.setUserId(UserId);
			entityManager.persist(m);
		}
 	}


	/**
	 * updateByUserId (final int UserId, final List<UserMeta> metaList)
	 *
	 * @param UserId
	 * @param metaList
	 *
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateByUserId (final int UserId, final List<UserMeta> metaList) {
		entityManager.createNativeQuery("delete from usermeta where UserId=:UserId").setParameter("UserId", UserId).executeUpdate();

		for (UserMeta m:metaList) {
			m.setUserId(UserId);
			entityManager.persist(m);
		}
	}



}
