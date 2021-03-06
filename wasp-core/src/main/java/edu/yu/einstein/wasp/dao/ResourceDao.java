
/**
 *
 * ResourceDao.java 
 * @author echeng (table2type.pl)
 *  
 * the Resource Dao 
 *
 *
 **/

package edu.yu.einstein.wasp.dao;

import edu.yu.einstein.wasp.model.Resource;


public interface ResourceDao extends WaspDao<Resource> {

  public Resource getResourceByResourceId (final Integer resourceId);

  public Resource getResourceByIName (final String iName);

  public Resource getResourceByName (final String name);


}

