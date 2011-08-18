
/**
 *
 * UserpasswordauthImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the Userpasswordauth object
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

import edu.yu.einstein.wasp.model.Userpasswordauth;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class UserpasswordauthDaoImpl extends WaspDaoImpl<Userpasswordauth> implements edu.yu.einstein.wasp.dao.UserpasswordauthDao {

  public UserpasswordauthDaoImpl() {
    super();
    this.entityClass = Userpasswordauth.class;
  }

  @SuppressWarnings("unchecked")
  @Transactional
  public Userpasswordauth getUserpasswordauthByUserId (final int UserId) {
    HashMap m = new HashMap();
    m.put("UserId", UserId);
    List<Userpasswordauth> results = (List<Userpasswordauth>) this.findByMap((Map) m);
    if (results.size() == 0) {
      Userpasswordauth rt = new Userpasswordauth();
      return rt;
    }
    return (Userpasswordauth) results.get(0);
  }


  @SuppressWarnings("unchecked")
  @Transactional
  public Userpasswordauth getUserpasswordauthByAuthcode (final String authcode) {
    HashMap m = new HashMap();
    m.put("authcode", authcode);
    List<Userpasswordauth> results = (List<Userpasswordauth>) this.findByMap((Map) m);
    if (results.size() == 0) {
      Userpasswordauth rt = new Userpasswordauth();
      return rt;
    }
    return (Userpasswordauth) results.get(0);
  }


}

