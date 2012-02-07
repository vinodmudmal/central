
/**
 *
 * UserpasswordauthDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the Userpasswordauth Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.Userpasswordauth;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class UserpasswordauthDaoImpl extends WaspDaoImpl<Userpasswordauth> implements edu.yu.einstein.wasp.dao.UserpasswordauthDao {

	/**
	 * UserpasswordauthDaoImpl() Constructor
	 *
	 *
	 */
	public UserpasswordauthDaoImpl() {
		super();
		this.entityClass = Userpasswordauth.class;
	}


	/**
	 * getUserpasswordauthByUserId(final int UserId)
	 *
	 * @param final int UserId
	 *
	 * @return userpasswordauth
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Userpasswordauth getUserpasswordauthByUserId (final int UserId) {
    		HashMap m = new HashMap();
		m.put("UserId", UserId);

		List<Userpasswordauth> results = this.findByMap(m);

		if (results.size() == 0) {
			Userpasswordauth rt = new Userpasswordauth();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getUserpasswordauthByAuthcode(final String authcode)
	 *
	 * @param final String authcode
	 *
	 * @return userpasswordauth
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Userpasswordauth getUserpasswordauthByAuthcode (final String authcode) {
    		HashMap m = new HashMap();
		m.put("authcode", authcode);

		List<Userpasswordauth> results = this.findByMap(m);

		if (results.size() == 0) {
			Userpasswordauth rt = new Userpasswordauth();
			return rt;
		}
		return results.get(0);
	}



}
