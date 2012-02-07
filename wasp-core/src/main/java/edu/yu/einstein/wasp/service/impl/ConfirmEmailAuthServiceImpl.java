
/**
*
* ConfirmEmailAuthServiceImpl.java 
* @author echeng (table2type.pl)
*  
* the ConfirmEmailAuthService Implmentation 
*
*
**/

package edu.yu.einstein.wasp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.yu.einstein.wasp.dao.ConfirmEmailAuthDao;
import edu.yu.einstein.wasp.model.ConfirmEmailAuth;
import edu.yu.einstein.wasp.model.User;
import edu.yu.einstein.wasp.model.UserPending;
import edu.yu.einstein.wasp.service.ConfirmEmailAuthService;
import edu.yu.einstein.wasp.util.AuthCode;

@Service
public class ConfirmEmailAuthServiceImpl extends WaspServiceImpl<ConfirmEmailAuth> implements ConfirmEmailAuthService {
    
    /**
    * confirmEmailAuthDao;
    *
    */
    private ConfirmEmailAuthDao confirmEmailAuthDao;
    
    /**
    * setConfirmEmailAuthDao(ConfirmEmailAuthDao confirmEmailAuthDao)
    *
    * @param confirmEmailAuthDao
    *
    */
    @Override
	@Autowired
    public void setConfirmEmailAuthDao(ConfirmEmailAuthDao confirmEmailAuthDao) {
        this.confirmEmailAuthDao = confirmEmailAuthDao;
        this.setWaspDao(confirmEmailAuthDao);
    }
    
    /**
    * getConfirmEmailAuthDao();
    *
    * @return confirmEmailAuthDao
    *
    */
    @Override
	public ConfirmEmailAuthDao getConfirmEmailAuthDao() {
        return this.confirmEmailAuthDao;
    }
    
    @Override
	public ConfirmEmailAuth getConfirmEmailAuthByConfirmEmailAuthId (final int confirmEmailAuthId) {
        return this.getConfirmEmailAuthDao().getConfirmEmailAuthByConfirmEmailAuthId(confirmEmailAuthId);
    }
    
    @Override
	public ConfirmEmailAuth getConfirmEmailAuthByAuthcode (final String authcode) {
        return this.getConfirmEmailAuthDao().getConfirmEmailAuthByAuthcode(authcode);
    }

	@Override
	public ConfirmEmailAuth getConfirmEmailAuthByUserpendingId(int userpendingId) {
		return this.getConfirmEmailAuthDao().getConfirmEmailAuthByUserpendingId(userpendingId);
	}

	@Override
	public ConfirmEmailAuth getConfirmEmailAuthByUserId(int userId) {
		return this.getConfirmEmailAuthDao().getConfirmEmailAuthByUserId(userId);
	}

	@Override
	public String getNewAuthcodeForUser(User user) {
		String authcode = AuthCode.create(20);
		ConfirmEmailAuth confirmEmailAuth = this.getConfirmEmailAuthByUserId(user.getUserId());
		confirmEmailAuth.setAuthcode(authcode);
		confirmEmailAuth.setUserId(user.getUserId());
		this.save(confirmEmailAuth);
		return authcode;
	}
	
	@Override
	public String getNewAuthcodeForUserPending(UserPending userpending) {
		String authcode = AuthCode.create(20);
		ConfirmEmailAuth confirmEmailAuth = this.getConfirmEmailAuthByUserpendingId(userpending.getUserPendingId());
		confirmEmailAuth.setAuthcode(authcode);
		confirmEmailAuth.setUserpendingId(userpending.getUserPendingId());
		this.save(confirmEmailAuth);
		return authcode;
	}
        
}
