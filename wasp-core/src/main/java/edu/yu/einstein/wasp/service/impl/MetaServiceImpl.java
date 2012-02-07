
/**
 *
 * MetaServiceImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the MetaService Implmentation 
 *
 *
 **/

package edu.yu.einstein.wasp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.yu.einstein.wasp.dao.MetaDao;
import edu.yu.einstein.wasp.model.Meta;
import edu.yu.einstein.wasp.service.MetaService;

@Service
public class MetaServiceImpl extends WaspMetaServiceImpl<Meta> implements MetaService {

	/**
	 * metaDao;
	 *
	 */
	private MetaDao metaDao;

	/**
	 * setMetaDao(MetaDao metaDao)
	 *
	 * @param metaDao
	 *
	 */
	@Override
	@Autowired
	public void setMetaDao(MetaDao metaDao) {
		this.metaDao = metaDao;
		this.setWaspDao(metaDao);
	}

	/**
	 * getMetaDao();
	 *
	 * @return metaDao
	 *
	 */
	@Override
	public MetaDao getMetaDao() {
		return this.metaDao;
	}


  @Override
public Meta getMetaByMetaId (final int metaId) {
    return this.getMetaDao().getMetaByMetaId(metaId);
  }

  @Override
public Meta getMetaByPropertyK (final String property, final String k) {
    return this.getMetaDao().getMetaByPropertyK(property, k);
  }

  @Override
public Meta getMetaByPropertyV (final String property, final String v) {
    return this.getMetaDao().getMetaByPropertyV(property, v);
  }

}
