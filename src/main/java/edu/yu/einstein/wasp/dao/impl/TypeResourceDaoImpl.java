
/**
 *
 * TypeResourceDaoImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the TypeResource Dao Impl
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.TypeResource;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class TypeResourceDaoImpl extends WaspDaoImpl<TypeResource> implements edu.yu.einstein.wasp.dao.TypeResourceDao {

	/**
	 * TypeResourceDaoImpl() Constructor
	 *
	 *
	 */
	public TypeResourceDaoImpl() {
		super();
		this.entityClass = TypeResource.class;
	}


	/**
	 * getTypeResourceByTypeResourceId(final Integer typeResourceId)
	 *
	 * @param final Integer typeResourceId
	 *
	 * @return typeResource
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public TypeResource getTypeResourceByTypeResourceId (final Integer typeResourceId) {
    		HashMap m = new HashMap();
		m.put("typeResourceId", typeResourceId);

		List<TypeResource> results = (List<TypeResource>) this.findByMap((Map) m);

		if (results.size() == 0) {
			TypeResource rt = new TypeResource();
			return rt;
		}
		return (TypeResource) results.get(0);
	}



	/**
	 * getTypeResourceByIName(final String iName)
	 *
	 * @param final String iName
	 *
	 * @return typeResource
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public TypeResource getTypeResourceByIName (final String iName) {
    		HashMap m = new HashMap();
		m.put("iName", iName);

		List<TypeResource> results = (List<TypeResource>) this.findByMap((Map) m);

		if (results.size() == 0) {
			TypeResource rt = new TypeResource();
			return rt;
		}
		return (TypeResource) results.get(0);
	}



	/**
	 * getTypeResourceByName(final String name)
	 *
	 * @param final String name
	 *
	 * @return typeResource
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public TypeResource getTypeResourceByName (final String name) {
    		HashMap m = new HashMap();
		m.put("name", name);

		List<TypeResource> results = (List<TypeResource>) this.findByMap((Map) m);

		if (results.size() == 0) {
			TypeResource rt = new TypeResource();
			return rt;
		}
		return (TypeResource) results.get(0);
	}



}

