
/**
 *
 * UserMetaService.java 
 * @author echeng (table2type.pl)
 *  
 * the UserMetaService
 *
 *
 **/

package edu.yu.einstein.wasp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.yu.einstein.wasp.dao.UserMetaDao;
import edu.yu.einstein.wasp.model.UserMeta;

@Service
public interface UserMetaService extends WaspMetaService<UserMeta> {

	/**
	 * setUserMetaDao(UserMetaDao userMetaDao)
	 *
	 * @param userMetaDao
	 *
	 */
	public void setUserMetaDao(UserMetaDao userMetaDao);

	/**
	 * getUserMetaDao();
	 *
	 * @return userMetaDao
	 *
	 */
	public UserMetaDao getUserMetaDao();

  public UserMeta getUserMetaByUserMetaId (final int userMetaId);

  public UserMeta getUserMetaByKUserId (final String k, final int UserId);


  public void updateByUserId (final String area, final int UserId, final List<UserMeta> metaList);

  public void updateByUserId (final int UserId, final List<UserMeta> metaList);


}
