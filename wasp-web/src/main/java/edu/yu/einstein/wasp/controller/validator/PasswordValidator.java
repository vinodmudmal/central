package edu.yu.einstein.wasp.controller.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import edu.yu.einstein.wasp.service.AuthenticationService;

/**
 * 
 * @author asmclellan
 *
 */
@Component
public class PasswordValidator{
	
	protected final Logger logger =LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AuthenticationService authenticationService;
	
	public void validate(BindingResult result, String password1, String password2, String area, String passwordFieldName) {
				    
	    // validate password
	    if (! result.hasFieldErrors(passwordFieldName)){
		Errors errors = new BindException(result.getTarget(), area);
	    	if (password1 == null || password1.isEmpty()){
	    		errors.rejectValue(passwordFieldName, area +".password.error", area +".password.error (no message has been defined for this property)");
	    	}
	    	if (! authenticationService.validatePassword(password1) ){ 
		    	errors.rejectValue(passwordFieldName, area +".password_invalid.error", area +".password_invalid.error (no message has been defined for this property)");
		    }
		    else if (! authenticationService.matchPassword(password1, password2) ){
		    	errors.rejectValue(passwordFieldName, area +".password_mismatch.error", area +".password_mismatch.error (no message has been defined for this property)");
		    }
	    	result.addAllErrors(errors);
	    }
	}
	
}
