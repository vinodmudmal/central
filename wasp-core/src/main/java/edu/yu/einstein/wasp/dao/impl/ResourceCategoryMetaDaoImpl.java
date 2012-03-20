
/**
 *
 * ResourceCategoryMetaDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the ResourceCategoryMeta Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.ResourceCategoryMeta;
import edu.yu.einstein.wasp.model.UserPendingMeta;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class ResourceCategoryMetaDaoImpl extends WaspDaoImpl<ResourceCategoryMeta> implements edu.yu.einstein.wasp.dao.ResourceCategoryMetaDao {

	/**
	 * ResourceCategoryMetaDaoImpl() Constructor
	 *
	 *
	 */
	public ResourceCategoryMetaDaoImpl() {
		super();
		this.entityClass = ResourceCategoryMeta.class;
	}


	/**
	 * getResourceCategoryMetaByResourceCategoryMetaId(final Integer resourceCategoryMetaId)
	 *
	 * @param final Integer resourceCategoryMetaId
	 *
	 * @return resourceCategoryMeta
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public ResourceCategoryMeta getResourceCategoryMetaByResourceCategoryMetaId (final Integer resourceCategoryMetaId) {
    		HashMap m = new HashMap();
		m.put("resourceCategoryMetaId", resourceCategoryMetaId);

		List<ResourceCategoryMeta> results = this.findByMap(m);

		if (results.size() == 0) {
			ResourceCategoryMeta rt = new ResourceCategoryMeta();
			return rt;
		}
		return results.get(0);
	}



	/**
	 * getResourceCategoryMetaByKResourcecategoryId(final String k, final Integer resourcecategoryId)
	 *
	 * @param final String k, final Integer resourcecategoryId
	 *
	 * @return resourceCategoryMeta
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public ResourceCategoryMeta getResourceCategoryMetaByKResourcecategoryId (final String k, final Integer resourcecategoryId) {
    		HashMap m = new HashMap();
		m.put("k", k);
		m.put("resourcecategoryId", resourcecategoryId);

		List<ResourceCategoryMeta> results = this.findByMap(m);

		if (results.size() == 0) {
			ResourceCategoryMeta rt = new ResourceCategoryMeta();
			return rt;
		}
		return results.get(0);
	}


	/**
	 * updateByResourcecategoryId (final int resourcecategoryId, final List<ResourceCategoryMeta> metaList)
	 *
	 * @param resourcecategoryId
	 * @param metaList
	 *
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateByResourcecategoryId (final int resourcecategoryId, final List<ResourceCategoryMeta> metaList) {
		for (ResourceCategoryMeta m:metaList) {
			ResourceCategoryMeta currentMeta = getResourceCategoryMetaByKResourcecategoryId(m.getK(), resourcecategoryId);
			if (currentMeta.getResourceCategoryMetaId() == null){
				// metadata value not in database yet
				m.setResourcecategoryId(resourcecategoryId);
				entityManager.persist(m);
			} else if (!currentMeta.getV().equals(m.getV())){
				// meta exists already but value has changed
				currentMeta.setV(m.getV());
				entityManager.merge(currentMeta);
			} else{
				// no change to meta so do nothing
			}
		}
	}



}

