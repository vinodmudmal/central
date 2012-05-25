
/**
 *
 * SampleDraftJobDraftCellSelection.java 
 * @author echeng (table2type.pl)
 *  
 * the SampleDraftJobDraftCellSelection
 *
 *
 */

package edu.yu.einstein.wasp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(name="sampledraftjobdraftcellselection")
public class SampleDraftJobDraftCellSelection extends WaspModel {

	/** 
	 * sampleDraftJobDraftCellSelectionId
	 *
	 */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer sampleDraftJobDraftCellSelectionId;

	/**
	 * setSampleDraftJobDraftCellSelectionId(Integer sampleDraftJobDraftCellSelectionId)
	 *
	 * @param sampleDraftJobDraftCellSelectionId
	 *
	 */
	
	public void setSampleDraftJobDraftCellSelectionId (Integer sampleDraftJobDraftCellSelectionId) {
		this.sampleDraftJobDraftCellSelectionId = sampleDraftJobDraftCellSelectionId;
	}

	/**
	 * getSampleDraftJobDraftCellSelectionId()
	 *
	 * @return sampleDraftJobDraftCellSelectionId
	 *
	 */
	public Integer getSampleDraftJobDraftCellSelectionId () {
		return this.sampleDraftJobDraftCellSelectionId;
	}




	/** 
	 * sampledraftId
	 *
	 */
	@Column(name="sampledraftid")
	protected Integer sampledraftId;

	/**
	 * setSampledraftId(Integer sampledraftId)
	 *
	 * @param sampledraftId
	 *
	 */
	
	public void setSampledraftId (Integer sampledraftId) {
		this.sampledraftId = sampledraftId;
	}

	/**
	 * getSampledraftId()
	 *
	 * @return sampledraftId
	 *
	 */
	public Integer getSampledraftId () {
		return this.sampledraftId;
	}




	/** 
	 * jobDraftCellSelectionId
	 *
	 */
	@Column(name="jobdraftcellselectionid")
	protected Integer jobDraftCellSelectionId;

	/**
	 * setJobDraftCellSelectionId(Integer jobDraftCellSelectionId)
	 *
	 * @param jobDraftCellSelectionId
	 *
	 */
	
	public void setJobDraftCellSelectionId (Integer jobDraftCellSelectionId) {
		this.jobDraftCellSelectionId = jobDraftCellSelectionId;
	}

	/**
	 * getJobDraftCellSelectionId()
	 *
	 * @return jobDraftCellSelectionId
	 *
	 */
	public Integer getJobDraftCellSelectionId () {
		return this.jobDraftCellSelectionId;
	}




	/** 
	 * libraryIndex
	 *
	 */
	@Column(name="libraryindex")
	protected Integer libraryIndex;

	/**
	 * setLibraryIndex(Integer libraryIndex)
	 *
	 * @param libraryIndex
	 *
	 */
	
	public void setLibraryIndex (Integer libraryIndex) {
		this.libraryIndex = libraryIndex;
	}

	/**
	 * getLibraryIndex()
	 *
	 * @return libraryIndex
	 *
	 */
	public Integer getLibraryIndex () {
		return this.libraryIndex;
	}




	/**
	 * sampleDraft
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="sampledraftid", insertable=false, updatable=false)
	protected SampleDraft sampleDraft;

	/**
	 * setSampleDraft (SampleDraft sampleDraft)
	 *
	 * @param sampleDraft
	 *
	 */
	public void setSampleDraft (SampleDraft sampleDraft) {
		this.sampleDraft = sampleDraft;
		this.sampledraftId = sampleDraft.sampleDraftId;
	}

	/**
	 * getSampleDraft ()
	 *
	 * @return sampleDraft
	 *
	 */
	
	public SampleDraft getSampleDraft () {
		return this.sampleDraft;
	}


	/**
	 * jobDraftCellSelection
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="jobdraftcellselectionid", insertable=false, updatable=false)
	protected JobDraftCellSelection jobDraftCellSelection;

	/**
	 * setJobDraftCell (JobDraftCellSelection jobDraftCellSelection)
	 *
	 * @param jobDraftCellSelection
	 *
	 */
	public void setJobDraftCellSelection (JobDraftCellSelection jobDraftCellSelection) {
		this.jobDraftCellSelection = jobDraftCellSelection;
		this.jobDraftCellSelectionId = jobDraftCellSelection.jobDraftCellSelectionId;
	}

	/**
	 * getJobDraftCell ()
	 *
	 * @return jobDraftCellSelection
	 *
	 */
	
	public JobDraftCellSelection getJobDraftCellSelection () {
		return this.jobDraftCellSelection;
	}


}