/**
 * Created by Wasp System Eclipse Plugin
 * @author 
 */
package ___package___.___pluginname___.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ___package___.___pluginname___.service.___Pluginname___Service;

import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.service.impl.WaspServiceImpl;

@Service
@Transactional("entityManager")
public class ___Pluginname___ServiceImpl extends WaspServiceImpl implements ___Pluginname___Service {
	
	@Autowired
	private GridHostResolver hostResolver;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String performAction() {
		// do something
		return "done";
	}


}