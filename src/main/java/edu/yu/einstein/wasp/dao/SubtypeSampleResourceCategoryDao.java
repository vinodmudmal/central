
/**
 *
 * SubypeSampleResourceCategoryDao.java 
 * @author echeng (table2type.pl)
 *  
 * the SubtypeSampleResourceCategory Dao 
 *
 *
 **/

package edu.yu.einstein.wasp.dao;

import edu.yu.einstein.wasp.model.SubtypeSampleResourceCategory;


public interface SubtypeSampleResourceCategoryDao extends WaspDao<SubtypeSampleResourceCategory> {

	public SubtypeSampleResourceCategory getSubtypeSampleResourceCategoryBySubtypeSampleResourceCategoryId (final Integer subtypeSampleResourCecategoryId);

	public SubtypeSampleResourceCategory getSubtypeSampleResourceCategoryByIName (final String iName);

	public SubtypeSampleResourceCategory getSubtypeSampleResourceCategoryByName (final String name);
	
}
