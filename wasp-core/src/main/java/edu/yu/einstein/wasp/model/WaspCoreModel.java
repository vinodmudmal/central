/**
 * 
 */
package edu.yu.einstein.wasp.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.yu.einstein.wasp.exception.ModelCopyException;

/**
 * @author calder
 * 
 */
@MappedSuperclass
public abstract class WaspCoreModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4136728312593640956L;
	
	/**
	 * generic logger included with every class.
	 */
	protected static Logger logger = LoggerFactory.getLogger(WaspCoreModel.class);

	/**
	 * Returns a DEEP copy of the supplied model object. Use example: Sample
	 * mySampleCopy = Sample.getDeepCopy(mySample); A deep copy creates a new
	 * instance of the same class type as the provided object and sets all
	 * attributes of the new object to also equal NEW objects whose data is the
	 * same as that for the equivalent attribute within the object being copied.
	 * NOTE: even if the model object is lazy loaded, this method will ensure
	 * all fields in the copy are fully populated
	 * 
	 * @param waspModelObj
	 * @return
	 * @throws ModelCopyException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends WaspModel> T getDeepCopy(T waspModelObj) throws ModelCopyException {
		return (T) getDeepCopy(waspModelObj, waspModelObj.getClass());
	}

	/**
	 * Internal static method for performing a deep copy
	 * 
	 * @param waspModelObj
	 * @param clazz
	 * @return
	 * @throws ModelCopyException
	 */
	@SuppressWarnings("unchecked")
	private static <T extends WaspCoreModel> Object getDeepCopy(Object waspModelObj, Class<T> clazz) throws ModelCopyException {
		Object clonedObj = null;
		try {
			clonedObj = clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Field> allClassFields = new ArrayList<Field>();
		Class<?> clazzToReap = clazz;
		do {
			for (Field f : clazzToReap.getDeclaredFields())
				allClassFields.add(f);
			clazzToReap = clazzToReap.getSuperclass();
		} while (clazzToReap != null);
		for (Field field : allClassFields) {
			String getterMethodName = "get" + WordUtils.capitalize(field.getName());
			String setterMethodName = "set" + WordUtils.capitalize(field.getName());
			// if (!field.isAnnotationPresent(javax.persistence.Id.class)){
			Class<?> fieldType = field.getType();
			Class<?> fieldGenericType = null;
			Type genericType = field.getGenericType();
			if (genericType instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) genericType; // cast
				fieldGenericType = (Class<?>) pType.getActualTypeArguments()[0]; // e.g.
																					// for
																					// List<String>,
																					// fieldGenericType
																					// should
																					// be
																					// java.lang.String
			}

			// get from source object
			Object objFromGet = null;
			try {
				objFromGet = clazz.getMethod(getterMethodName).invoke(waspModelObj);
			} catch (NoSuchMethodException e) {
				// ignore
				continue;
			} catch (Exception e) {
				logger.debug("DeepCopy failed to work with method '" + getterMethodName + "'");
				continue;
			}
			if (objFromGet == null)
				continue;
			// set to destination object
			Object objToSet = null;
			if (java.util.List.class.equals(fieldType)) {
				// is a List
				objToSet = new ArrayList<Class<?>>();
				try {
					for (Object o : (List<?>) objFromGet) {
						if (WaspCoreModel.class.isInstance(o)) {
							((List<Object>) objToSet).add(WaspCoreModel.getDeepCopy(o, (Class<? extends WaspCoreModel>) fieldGenericType));
						} else if (fieldGenericType.toString().contains("java.lang")) {
							// java.lang package wrapper classes are all
							// immutable
							((List<Object>) objToSet).add(o);
						} else if (java.util.Date.class.equals(fieldGenericType)) {
							objToSet = new Date();
							((Date) objToSet).setTime(((Date) o).getTime());
						} else {
							throw new ModelCopyException("Deep copy cannot render an arrayList of type '" + fieldGenericType.getName() + "' to parameratized type WaspCoreModel or a java.lang class");
						}
					}
				} catch (ClassCastException e) {
					logger.debug("Deep copy cannot cast object returned by " + getterMethodName + " to ArrayList");
				}
			} else {
				if (WaspCoreModel.class.isInstance(objFromGet)) {
					objToSet = WaspCoreModel.getDeepCopy(objFromGet, (Class<? extends WaspCoreModel>) fieldType);
				} else if (fieldType.toString().contains("java.lang")) {
					// java.lang package wrapper classes are all immutable
					objToSet = objFromGet;
				} else if (java.util.Date.class.equals(fieldType)) {
					objToSet = new Date();
					((Date) objToSet).setTime(((Date) objFromGet).getTime());
				} else {
					throw new ModelCopyException("Deep copy cannot render a field of type '" + fieldType.getName() + "'  to type WaspCoreModel or a java.lang class");
				}
			}
			if (objToSet == null)
				continue;
			try {
				clazz.getMethod(setterMethodName, field.getType()).invoke(clonedObj, objToSet);
			} catch (Exception e) {
				logger.debug("DeepCopy failed to work with method '" + setterMethodName + "'");
				continue;
			}
			// }
		}
		return clonedObj;
	}

	/**
	 * Returns a SHALLOW copy of the supplied model object. Use example: Sample
	 * mySampleCopy = Sample.getShallowCopy(mySample); A shallow copy creates a
	 * new instance of the same class type as the provided object and sets all
	 * attributes of the new object to equal the exact SAME objects referenced
	 * within the object being copied. NOTE: even if the model object is lazy
	 * loaded, this method will ensure all fields in the copy are fully
	 * populated
	 * 
	 * @param waspModelObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends WaspCoreModel> T getShallowCopy(T waspModelObj) {
		Object clonedObj = null;
		Class<T> clazz = (Class<T>) waspModelObj.getClass();
		try {
			clonedObj = clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Field> allClassFields = new ArrayList<Field>();
		Class<?> clazzToReap = clazz;
		do {
			for (Field f : clazzToReap.getDeclaredFields())
				allClassFields.add(f);
			clazzToReap = clazzToReap.getSuperclass();
		} while (clazzToReap != null);
		for (Field field : allClassFields) {
			String getterMethodName = "get" + WordUtils.capitalize(field.getName());
			String setterMethodName = "set" + WordUtils.capitalize(field.getName());

			// get from source object
			Object objFromGet = null;
			try {
				objFromGet = clazz.getMethod(getterMethodName).invoke(waspModelObj);
			} catch (NoSuchMethodException e) {
				// ignore
				continue;
			} catch (Exception e) {
				logger.debug("ShallowCopy failed to work with method '" + getterMethodName + "'");
				continue;
			}
			if (objFromGet == null)
				continue;
			// set to destination object
			Object objToSet = objFromGet;
			try {
				clazz.getMethod(setterMethodName, field.getType()).invoke(clonedObj, objToSet);
			} catch (Exception e) {
				logger.debug("ShallowCopy failed to work with method '" + setterMethodName + "'");
				continue;
			}
		}
		return (T) clonedObj;
	}
	
	/** 
	 * updated
	 *
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated")
	protected Date updated;
	
	/**
	 * @param updated
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	/**
	 * @return
	 */
	public Date getUpdated() {
		return this.updated;
	}
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created")
	protected Date created = new Date();

	/**
	 * setCreated(Date created)
	 *
	 * @param created
	 *
	 */
	private void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return created date
	 */
	public Date getCreated() {
		return created;
	}
	
	@PreUpdate
	@PrePersist
	public void doUpdate() {
		this.updated = new Date();
		if (this.created == null)
			setCreated(new Date());
		if (this.uuid == null)
			setUUID(UUID.randomUUID());
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="lastupdatebyuser", insertable=false, updatable=false)
	protected User lastUpdatedByUser;

	/**
	 * @return the lastUpdatedByUser
	 */
	public User getLastUpdatedByUser() {
		return lastUpdatedByUser;
	}

	/**
	 * @param lastUpdatedByUser the lastUpdatedByUser to set
	 */
	public void setLastUpdatedByUser(User lastUpdatedByUser) {
		this.lastUpdatedByUser = lastUpdatedByUser;
	}
	
//	// TODO: Remove, incorrect usage
//	@Column(name = "lastupduser")
//	protected Integer lastUpdUser;
//
//	public void setLastUpdUser(Integer lastUpdUser) {
//		this.lastUpdUser = lastUpdUser;
//	}
//
//	@JsonIgnore
//	public Integer getLastUpdUser() {
//		return this.lastUpdUser;
//	}

	@Column(name="uuid", length=16)
	private UUID uuid = UUID.randomUUID();
	
	/**
	 * private value for identity
	 * @return the resultID
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @param resultID the resultID to set
	 */
	private void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
	        .append(uuid)
	        .toHashCode();
	}


	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof WaspCoreModel){
	        final WaspCoreModel other = (WaspCoreModel) obj;
	        return new EqualsBuilder()
	            .append(uuid, other.uuid)
	            .isEquals();
	    } else{
	        return false;
	    }
	}

}
