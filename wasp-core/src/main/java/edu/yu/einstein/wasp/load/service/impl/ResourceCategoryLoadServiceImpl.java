package edu.yu.einstein.wasp.load.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.Assert;
import edu.yu.einstein.wasp.dao.ResourceCategoryDao;
import edu.yu.einstein.wasp.dao.ResourceCategoryMetaDao;
import edu.yu.einstein.wasp.dao.ResourceTypeDao;
import edu.yu.einstein.wasp.load.service.ResourceCategoryLoadService;
import edu.yu.einstein.wasp.model.ResourceCategory;
import edu.yu.einstein.wasp.model.ResourceCategoryMeta;
import edu.yu.einstein.wasp.model.ResourceType;

/**
 * 
 * @author asmclellan
 *
 */
@Service
@Transactional("entityManager")
public class ResourceCategoryLoadServiceImpl extends WaspLoadServiceImpl implements ResourceCategoryLoadService {

	@Autowired
	private ResourceCategoryDao resourceCategoryDao;

	@Autowired
	private ResourceCategoryMetaDao resourceCategoryMetaDao;

	@Autowired
	private ResourceTypeDao resourceTypeDao;
	
	private ResourceCategory addOrUpdateResourceCategory(ResourceType resourceType, String iname, String name, int isActive){
		ResourceCategory resourceCat = resourceCategoryDao.getResourceCategoryByIName(iname);
		if (resourceCat.getId() == null) { 
	      resourceCat = new ResourceCategory();

	      resourceCat.setIName(iname);
	      resourceCat.setName(name);
	      resourceCat.setIsActive(isActive);
	      resourceCat.setResourceTypeId(resourceType.getId());
	      resourceCat = resourceCategoryDao.save(resourceCat); 
	    } else {
	     if (resourceCat.getName() == null || !resourceCat.getName().equals(name)){
	    	  resourceCat.setName(name);
	      }
	      if (resourceCat.getIsActive().intValue() != isActive){
	    	  resourceCat.setIsActive(isActive);
	      }
	    }
		return resourceCat;
	}
	
	private void syncResourceCategoryMeta(List<ResourceCategoryMeta> meta, ResourceCategory resourceCat){
		int lastPosition = 0;
	    Map<String, ResourceCategoryMeta> oldResourceCatMetas  = new HashMap<String, ResourceCategoryMeta>();
	    for (ResourceCategoryMeta resourceCatMeta: safeList(resourceCat.getResourceCategoryMeta())) {
	    	oldResourceCatMetas.put(resourceCatMeta.getK(), resourceCatMeta);
	    } 
	    for (ResourceCategoryMeta resourceCatMeta: safeList(meta) ) {

	      // incremental position numbers. 
	      if ( resourceCatMeta.getPosition() == 0 ||
	    		  resourceCatMeta.getPosition() <= lastPosition
	        )  {
	    	  resourceCatMeta.setPosition(lastPosition +1); 
	      }
	      lastPosition = resourceCatMeta.getPosition();

	      if (oldResourceCatMetas.containsKey(resourceCatMeta.getK())) {
	        ResourceCategoryMeta old = oldResourceCatMetas.get(resourceCatMeta.getK());
	       if (old.getV() == null || !old.getV().equals(resourceCatMeta.getV()))
	        	old.setV(resourceCatMeta.getV());
	        if (old.getPosition().intValue() != resourceCatMeta.getPosition())
	        	old.setPosition(resourceCatMeta.getPosition());
	        oldResourceCatMetas.remove(old.getK()); // remove the meta from the old meta list as we're done with it
	        continue; 
	      }

	      resourceCatMeta.setResourcecategoryId(resourceCat.getId()); 
	      resourceCategoryMetaDao.save(resourceCatMeta); 
	    }

	    // delete the left overs
	    // The next block was commented out by Dubin; 10-07-2013 as it removes meta 
		//that is added any time after the initial data upload
		/*
	    for (String resourceMetaKey : oldResourceCatMetas.keySet()) {
	      ResourceCategoryMeta resourceCatMeta = oldResourceCatMetas.get(resourceMetaKey); 
	      resourceCategoryMetaDao.remove(resourceCatMeta); 
	      resourceCategoryMetaDao.flush(resourceCatMeta); 
	    }
	    */
	}
	

	@Override
	public ResourceCategory update(List<ResourceCategoryMeta> meta, ResourceType resourceType, String iname, String name, int isActive){
		Assert.assertParameterNotNull(resourceType, "ResourceType cannot be null");
		Assert.assertParameterNotNull(iname, "iname Cannot be null");
		Assert.assertParameterNotNull(name, "name Cannot be null");
	    ResourceCategory resourceCat = addOrUpdateResourceCategory(resourceType, iname, name, isActive);
	    syncResourceCategoryMeta(meta, resourceCat);
	    return resourceCat;
	    
	}
	
}
