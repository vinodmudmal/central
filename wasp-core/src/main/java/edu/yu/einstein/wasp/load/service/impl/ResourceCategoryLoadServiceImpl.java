package edu.yu.einstein.wasp.load.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.dao.ResourceCategoryDao;
import edu.yu.einstein.wasp.dao.ResourceCategoryMetaDao;
import edu.yu.einstein.wasp.dao.ResourceTypeDao;
import edu.yu.einstein.wasp.exception.NullResourceTypeException;
import edu.yu.einstein.wasp.load.service.ResourceCategoryLoadService;
import edu.yu.einstein.wasp.model.ResourceCategory;
import edu.yu.einstein.wasp.model.ResourceCategoryMeta;
import edu.yu.einstein.wasp.model.ResourceType;

@Service
@Transactional("entityManager")
public class ResourceCategoryLoadServiceImpl extends WaspLoadServiceImpl implements ResourceCategoryLoadService {

	@Autowired
	private ResourceCategoryDao resourceCategoryDao;

	@Autowired
	private ResourceCategoryMetaDao resourceCategoryMetaDao;

	@Autowired
	private ResourceTypeDao resourceTypeDao;
	
	private ResourceCategory addOrUpdateResourceCategory(ResourceType resourceType, String iname, String name, Integer isActive){
		ResourceCategory resourceCat = resourceCategoryDao.getResourceCategoryByIName(iname);
		if (resourceCat.getResourceCategoryId() == null) { 
	      resourceCat = new ResourceCategory();

	      resourceCat.setIName(iname);
	      resourceCat.setName(name);
	      resourceCat.setIsActive(isActive.intValue());
	      resourceCat.setResourceTypeId(resourceType.getResourceTypeId());
	      resourceCategoryDao.save(resourceCat); 

	      // refreshes
	      resourceCat = resourceCategoryDao.getResourceCategoryByIName(iname); 

	    } else {
	      boolean changed = false;	
	      if (!resourceCat.getName().equals(name)){
	    	  resourceCat.setName(name);
	    	  changed = true;
	      }
	      if (resourceCat.getIsActive().intValue() != isActive.intValue()){
	    	  resourceCat.setIsActive(isActive.intValue());
	    	  changed = true;
	      }
	      if (changed)
	    	  resourceCategoryDao.save(resourceCat); 
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
	        boolean changed = false;
	        if (!old.getV().equals(resourceCatMeta.getV())){
	        	old.setV(resourceCatMeta.getV());
	        	changed = true;
	        }
	        if (old.getPosition().intValue() != resourceCatMeta.getPosition()){
	        	old.setPosition(resourceCatMeta.getPosition());
	        	changed = true;
	        }
	        if (changed)
	        	resourceCategoryMetaDao.save(old);

	        oldResourceCatMetas.remove(old.getK()); // remove the meta from the old meta list as we're done with it
	        continue; 
	      }

	      resourceCatMeta.setResourcecategoryId(resourceCat.getResourceCategoryId()); 
	      resourceCategoryMetaDao.save(resourceCatMeta); 
	    }

	    // delete the left overs
	    for (String resourceMetaKey : oldResourceCatMetas.keySet()) {
	      ResourceCategoryMeta resourceCatMeta = oldResourceCatMetas.get(resourceMetaKey); 
	      resourceCategoryMetaDao.remove(resourceCatMeta); 
	      resourceCategoryMetaDao.flush(resourceCatMeta); 
	    }
	}
	

	@Override
	public void update(List<ResourceCategoryMeta> meta, String resourceTypeString, String iname, String name, Integer isActive){
	   ResourceType resourceType = resourceTypeDao.getResourceTypeByIName(resourceTypeString); 
	    if (resourceType == null){
	    	throw new NullResourceTypeException();
	    }

	    
	    
	    if (isActive == null)
	    	  isActive = 1;
	    ResourceCategory resourceCat = addOrUpdateResourceCategory(resourceType, iname, name, isActive);
	    syncResourceCategoryMeta(meta, resourceCat);
	    
	}
	
}
