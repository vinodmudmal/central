
/**
 *
 * UserImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the User object
 *
 *
 **/
 
package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.User;

@Transactional("entityManager")
@Repository
public class UserDaoImpl extends WaspDaoImpl<User> implements edu.yu.einstein.wasp.dao.UserDao {

  public UserDaoImpl() {
    super();
    this.entityClass = User.class;
  }

  @Override
  @Transactional("entityManager")
  public User getUserByUserId (final int userId) {
    HashMap<String, Integer> m = new HashMap<String, Integer>();
    m.put("id", userId);
    List<User> results = this.findByMap(m);
    if (results.size() == 0) {
      User rt = new User();
      return rt;
    }
    return results.get(0);
  }


  @Override
  @Transactional("entityManager")
  public User getUserByLogin (final String login) {
    HashMap<String, String> m = new HashMap<String, String>();
    m.put("login", login);
    List<User> results = this.findByMap(m);
    if (results.size() == 0) {
      User rt = new User();
      return rt;
    }
    return results.get(0);
  }


  @Override
  @Transactional("entityManager")
  public User getUserByEmail (final String email) {
    HashMap<String, String> m = new HashMap<String, String>();
    m.put("email", email);
    List<User> results = this.findByMap(m);
    if (results.size() == 0) {
      User rt = new User();
      return rt;
    }
    return results.get(0);
  }
  
 
  @Override
  public List<User> getActiveUsers(){
	  Map<String, Integer> queryMap = new HashMap<String, Integer>();
	  queryMap.put("isActive", 1);
	  return this.findByMap(queryMap);
  }
}

