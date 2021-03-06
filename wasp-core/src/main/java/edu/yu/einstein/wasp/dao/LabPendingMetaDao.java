
/**
 *
 * LabPendingMetaDao.java 
 * @author echeng (table2type.pl)
 *  
 * the LabPendingMeta Dao 
 *
 *
 **/

package edu.yu.einstein.wasp.dao;

import edu.yu.einstein.wasp.model.LabPendingMeta;


public interface LabPendingMetaDao extends WaspMetaDao<LabPendingMeta> {

  public LabPendingMeta getLabPendingMetaByLabPendingMetaId (final int labPendingMetaId);

  public LabPendingMeta getLabPendingMetaByKLabpendingId (final String k, final int labpendingId);




}

