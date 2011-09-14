
/**
 *
 * ConfirmEmailAuthDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the ConfirmEmailAuth Dao Impl
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
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.ConfirmEmailAuth;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class ConfirmEmailAuthDaoImpl extends WaspDaoImpl<ConfirmEmailAuth> implements edu.yu.einstein.wasp.dao.ConfirmEmailAuthDao {

	/**
	 * ConfirmEmailAuthDaoImpl() Constructor
	 *
	 *
	 */
	public ConfirmEmailAuthDaoImpl() {
		super();
		this.entityClass = ConfirmEmailAuth.class;
	}


	/**
	 * getConfirmEmailAuthByUserpendingId(final int userpendingId)
	 *
	 * @param final int userpendingId
	 *
	 * @return confirmEmailAuth
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public ConfirmEmailAuth getConfirmEmailAuthByUserpendingId (final int userpendingId) {
    		HashMap m = new HashMap();
		m.put("userpendingId", userpendingId);

		List<ConfirmEmailAuth> results = (List<ConfirmEmailAuth>) this.findByMap((Map) m);

		if (results.size() == 0) {
			ConfirmEmailAuth rt = new ConfirmEmailAuth();
			return rt;
		}
		return (ConfirmEmailAuth) results.get(0);
	}



	/**
	 * getConfirmEmailAuthByAuthcode(final String authcode)
	 *
	 * @param final String authcode
	 *
	 * @return confirmEmailAuth
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public ConfirmEmailAuth getConfirmEmailAuthByAuthcode (final String authcode) {
    		HashMap m = new HashMap();
		m.put("authcode", authcode);

		List<ConfirmEmailAuth> results = (List<ConfirmEmailAuth>) this.findByMap((Map) m);

		if (results.size() == 0) {
			ConfirmEmailAuth rt = new ConfirmEmailAuth();
			return rt;
		}
		return (ConfirmEmailAuth) results.get(0);
	}



}
