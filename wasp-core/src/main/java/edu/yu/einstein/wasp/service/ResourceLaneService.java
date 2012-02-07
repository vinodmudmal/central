
/**
 *
 * ResourceLaneService.java 
 * @author echeng (table2type.pl)
 *  
 * the ResourceLaneService
 *
 *
 **/

package edu.yu.einstein.wasp.service;

import org.springframework.stereotype.Service;

import edu.yu.einstein.wasp.dao.ResourceLaneDao;
import edu.yu.einstein.wasp.model.ResourceLane;

@Service
public interface ResourceLaneService extends WaspService<ResourceLane> {

	/**
	 * setResourceLaneDao(ResourceLaneDao resourceLaneDao)
	 *
	 * @param resourceLaneDao
	 *
	 */
	public void setResourceLaneDao(ResourceLaneDao resourceLaneDao);

	/**
	 * getResourceLaneDao();
	 *
	 * @return resourceLaneDao
	 *
	 */
	public ResourceLaneDao getResourceLaneDao();

  public ResourceLane getResourceLaneByResourceLaneId (final Integer resourceLaneId);

  public ResourceLane getResourceLaneByINameResourceId (final String iName, final Integer resourceId);

  public ResourceLane getResourceLaneByNameResourceId (final String name, final Integer resourceId);


}
