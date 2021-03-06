
/**
 *
 * Software.java 
 * @author echeng (table2type.pl)
 *  
 * the Software
 *
 *
 */

package edu.yu.einstein.wasp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(name="software")
public class Software extends WaspModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5520002461442259763L;

	/**
	 * setSoftwareId(Integer softwareId)
	 *
	 * @param softwareId
	 *
	 */
	@Deprecated
	public void setSoftwareId (Integer softwareId) {
		setId(softwareId);
	}

	/**
	 * getSoftwareId()
	 *
	 * @return softwareId
	 *
	 */
	@Deprecated
	public Integer getSoftwareId () {
		return getId();
	}




	/** 
	 * resourceTypeId
	 *
	 */
	@Column(name="resourcetypeid")
	protected int resourceTypeId;

	/**
	 * setResourceTypeId(int resourceTypeId)
	 *
	 * @param resourceTypeId
	 *
	 */
	
	public void setResourceTypeId (int resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	/**
	 * getResourceTypeId()
	 *
	 * @return resourceTypeId
	 *
	 */
	public int getResourceTypeId () {
		return this.resourceTypeId;
	}




	/** 
	 * iName
	 *
	 */
	@Column(name="iname")
	protected String iName;

	/**
	 * setIName(String iName)
	 *
	 * @param iName
	 *
	 */
	
	public void setIName (String iName) {
		this.iName = iName;
	}

	/**
	 * getIName()
	 *
	 * @return iName
	 *
	 */
	public String getIName () {
		return this.iName;
	}




	/** 
	 * name
	 *
	 */
	@Column(name="name")
	protected String name;

	/**
	 * setName(String name)
	 *
	 * @param name
	 *
	 */
	
	public void setName (String name) {
		this.name = name;
	}

	/**
	 * getName()
	 *
	 * @return name
	 *
	 */
	public String getName () {
		return this.name;
	}
	
	
	/** 
	 * description
	 *
	 */
	@Column(name="description")
	protected String description;

	/**
	 * setDescription(String description)
	 *
	 * @param description
	 *
	 */
	
	public void setDescription (String description) {
		this.description = description;
	}

	/**
	 * getDescription()
	 *
	 * @return description
	 *
	 */
	public String getDescription () {
		return this.description;
	}
	

	/** 
	 * isActive
	 *
	 */
	@Column(name="isactive")
	protected Integer isActive = 1;

	/**
	 * setIsActive(Integer isActive)
	 *
	 * @param isActive
	 *
	 */
	
	public void setIsActive (Integer isActive) {
		this.isActive = isActive;
	}

	/**
	 * getIsActive()
	 *
	 * @return isActive
	 *
	 */
	public Integer getIsActive () {
		return this.isActive;
	}

	/**
	 * resourceType
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="resourcetypeid", insertable=false, updatable=false)
	protected ResourceType resourceType;

	/**
	 * setResourceType (ResourceType resourceType)
	 *
	 * @param resourceType
	 *
	 */
	public void setResourceType (ResourceType resourceType) {
		this.resourceType = resourceType;
		this.resourceTypeId = resourceType.getId();
	}

	/**
	 * getResourceType ()
	 *
	 * @return resourceType
	 *
	 */
	
	public ResourceType getResourceType () {
		return this.resourceType;
	}


	/** 
	 * softwareMeta
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="softwareid", insertable=false, updatable=false)
	protected List<SoftwareMeta> softwareMeta;


	/** 
	 * getSoftwareMeta()
	 *
	 * @return softwareMeta
	 *
	 */
	@JsonIgnore
	public List<SoftwareMeta> getSoftwareMeta() {
		return this.softwareMeta;
	}


	/** 
	 * setSoftwareMeta
	 *
	 * @param softwareMeta
	 *
	 */
	public void setSoftwareMeta (List<SoftwareMeta> softwareMeta) {
		this.softwareMeta = softwareMeta;
	}



	/** 
	 * jobSoftware
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="softwareid", insertable=false, updatable=false)
	protected List<JobSoftware> jobSoftware;


	/** 
	 * getJobSoftware()
	 *
	 * @return jobSoftware
	 *
	 */
	@JsonIgnore
	public List<JobSoftware> getJobSoftware() {
		return this.jobSoftware;
	}


	/** 
	 * setJobSoftware
	 *
	 * @param jobSoftware
	 *
	 */
	public void setJobSoftware (List<JobSoftware> jobSoftware) {
		this.jobSoftware = jobSoftware;
	}



	/** 
	 * jobDraftSoftware
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="softwareid", insertable=false, updatable=false)
	protected List<JobDraftSoftware> jobDraftSoftware;


	/** 
	 * getJobDraftSoftware()
	 *
	 * @return jobDraftSoftware
	 *
	 */
	@JsonIgnore
	public List<JobDraftSoftware> getJobDraftSoftware() {
		return this.jobDraftSoftware;
	}


	/** 
	 * setJobDraftSoftware
	 *
	 * @param jobDraftSoftware
	 *
	 */
	public void setJobDraftSoftware (List<JobDraftSoftware> jobDraftSoftware) {
		this.jobDraftSoftware = jobDraftSoftware;
	}



	/** 
	 * workflowSoftware
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="softwareid", insertable=false, updatable=false)
	protected List<WorkflowSoftware> workflowSoftware;


	/** 
	 * getWorkflowSoftware()
	 *
	 * @return workflowSoftware
	 *
	 */
	@JsonIgnore
	public List<WorkflowSoftware> getWorkflowSoftware() {
		return this.workflowSoftware;
	}


	/** 
	 * setWorkflowSoftware
	 *
	 * @param workflowSoftware
	 *
	 */
	public void setWorkflowSoftware (List<WorkflowSoftware> workflowSoftware) {
		this.workflowSoftware = workflowSoftware;
	}



}
