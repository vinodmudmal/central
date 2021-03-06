
/**
 *
 * AdaptorMetaDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the AdaptorMeta Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.AdaptorMeta;


@Transactional("entityManager")
@Repository
public class AdaptorMetaDaoImpl extends WaspMetaDaoImpl<AdaptorMeta> implements edu.yu.einstein.wasp.dao.AdaptorMetaDao {

	/**
	 * AdaptorMetaDaoImpl() Constructor
	 *
	 *
	 */
	public AdaptorMetaDaoImpl() {
		super();
		this.entityClass = AdaptorMeta.class;
	}


	/**
	 * getAdaptorMetaByAdaptorMetaId(final Integer adaptorMetaId)
	 *
	 * @param final Integer adaptorMetaId
	 *
	 * @return adaptorMeta
	 */

	@Override
	@Transactional("entityManager")
	public AdaptorMeta getAdaptorMetaByAdaptorMetaId (final Integer adaptorMetaId) {
    		HashMap<String, Integer> m = new HashMap<String, Integer>();
		m.put("id", adaptorMetaId);

		List<AdaptorMeta> results = this.findByMap(m);

		if (results.size() == 0) {
			AdaptorMeta rt = new AdaptorMeta();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getAdaptorMetaByKAdaptorId(final String k, final Integer adaptorId)
	 *
	 * @param final String k, final Integer adaptorId
	 *
	 * @return adaptorMeta
	 */

	@Override
	@Transactional("entityManager")
	public AdaptorMeta getAdaptorMetaByKAdaptorId (final String k, final Integer adaptorId) {
    		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("k", k);
		m.put("adaptorId", adaptorId);

		List<AdaptorMeta> results = this.findByMap(m);

		if (results.size() == 0) {
			AdaptorMeta rt = new AdaptorMeta();
			return rt;
		}
		return results.get(0);
	}




}

