
/**
 *
 * Sample.java 
 * @author echeng (table2type.pl)
 *  
 * the Sample
 *
 *
 */

package edu.yu.einstein.wasp.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import org.hibernate.validator.constraints.*;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Audited
@Table(name="sample")
public class Sample extends WaspModel {

	/** 
	 * sampleId
	 *
	 */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	protected int sampleId;

	/**
	 * setSampleId(int sampleId)
	 *
	 * @param sampleId
	 *
	 */
	
	public void setSampleId (int sampleId) {
		this.sampleId = sampleId;
	}

	/**
	 * getSampleId()
	 *
	 * @return sampleId
	 *
	 */
	public int getSampleId () {
		return this.sampleId;
	}




	/** 
	 * typeSampleId
	 *
	 */
	@Column(name="typesampleid")
	protected int typeSampleId;

	/**
	 * setTypeSampleId(int typeSampleId)
	 *
	 * @param typeSampleId
	 *
	 */
	
	public void setTypeSampleId (int typeSampleId) {
		this.typeSampleId = typeSampleId;
	}

	/**
	 * getTypeSampleId()
	 *
	 * @return typeSampleId
	 *
	 */
	public int getTypeSampleId () {
		return this.typeSampleId;
	}




	/** 
	 * subtypeSampleId
	 *
	 */
	@Column(name="subtypesampleid")
	protected Integer subtypeSampleId;

	/**
	 * setSubtypeSampleId(Integer subtypeSampleId)
	 *
	 * @param subtypeSampleId
	 *
	 */
	
	public void setSubtypeSampleId (Integer subtypeSampleId) {
		this.subtypeSampleId = subtypeSampleId;
	}

	/**
	 * getSubtypeSampleId()
	 *
	 * @return subtypeSampleId
	 *
	 */
	public Integer getSubtypeSampleId () {
		return this.subtypeSampleId;
	}




	/** 
	 * submitterLabId
	 *
	 */
	@Column(name="submitter_labid")
	protected int submitterLabId;

	/**
	 * setSubmitterLabId(int submitterLabId)
	 *
	 * @param submitterLabId
	 *
	 */
	
	public void setSubmitterLabId (int submitterLabId) {
		this.submitterLabId = submitterLabId;
	}

	/**
	 * getSubmitterLabId()
	 *
	 * @return submitterLabId
	 *
	 */
	public int getSubmitterLabId () {
		return this.submitterLabId;
	}




	/** 
	 * submitterUserId
	 *
	 */
	@Column(name="submitter_userid")
	protected int submitterUserId;

	/**
	 * setSubmitterUserId(int submitterUserId)
	 *
	 * @param submitterUserId
	 *
	 */
	
	public void setSubmitterUserId (int submitterUserId) {
		this.submitterUserId = submitterUserId;
	}

	/**
	 * getSubmitterUserId()
	 *
	 * @return submitterUserId
	 *
	 */
	public int getSubmitterUserId () {
		return this.submitterUserId;
	}




	/** 
	 * submitterJobId
	 *
	 */
	@Column(name="submitter_jobid")
	protected Integer submitterJobId;

	/**
	 * setSubmitterJobId(Integer submitterJobId)
	 *
	 * @param submitterJobId
	 *
	 */
	
	public void setSubmitterJobId (Integer submitterJobId) {
		this.submitterJobId = submitterJobId;
	}

	/**
	 * getSubmitterJobId()
	 *
	 * @return submitterJobId
	 *
	 */
	public Integer getSubmitterJobId () {
		return this.submitterJobId;
	}




	/** 
	 * isReceived
	 *
	 */
	@Column(name="isreceived")
	protected int isReceived;

	/**
	 * setIsReceived(int isReceived)
	 *
	 * @param isReceived
	 *
	 */
	
	public void setIsReceived (int isReceived) {
		this.isReceived = isReceived;
	}

	/**
	 * getIsReceived()
	 *
	 * @return isReceived
	 *
	 */
	public int getIsReceived () {
		return this.isReceived;
	}




	/** 
	 * receiverUserId
	 *
	 */
	@Column(name="receiver_userid")
	protected Integer receiverUserId;

	/**
	 * setReceiverUserId(Integer receiverUserId)
	 *
	 * @param receiverUserId
	 *
	 */
	
	public void setReceiverUserId (Integer receiverUserId) {
		this.receiverUserId = receiverUserId;
	}

	/**
	 * getReceiverUserId()
	 *
	 * @return receiverUserId
	 *
	 */
	public Integer getReceiverUserId () {
		return this.receiverUserId;
	}




	/** 
	 * receiveDts
	 *
	 */
	@Column(name="receivedts")
	protected Date receiveDts;

	/**
	 * setReceiveDts(Date receiveDts)
	 *
	 * @param receiveDts
	 *
	 */
	
	public void setReceiveDts (Date receiveDts) {
		this.receiveDts = receiveDts;
	}

	/**
	 * getReceiveDts()
	 *
	 * @return receiveDts
	 *
	 */
	public Date getReceiveDts () {
		return this.receiveDts;
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
	 * isGood
	 *
	 */
	@Column(name="isgood")
	protected Integer isGood;

	/**
	 * setIsGood(Integer isGood)
	 *
	 * @param isGood
	 *
	 */
	
	public void setIsGood (Integer isGood) {
		this.isGood = isGood;
	}

	/**
	 * getIsGood()
	 *
	 * @return isGood
	 *
	 */
	public Integer getIsGood () {
		return this.isGood;
	}




	/** 
	 * isActive
	 *
	 */
	@Column(name="isactive")
	protected int isActive;

	/**
	 * setIsActive(int isActive)
	 *
	 * @param isActive
	 *
	 */
	
	public void setIsActive (int isActive) {
		this.isActive = isActive;
	}

	/**
	 * getIsActive()
	 *
	 * @return isActive
	 *
	 */
	public int getIsActive () {
		return this.isActive;
	}




	/** 
	 * lastUpdTs
	 *
	 */
	@Column(name="lastupdts")
	protected Date lastUpdTs;

	/**
	 * setLastUpdTs(Date lastUpdTs)
	 *
	 * @param lastUpdTs
	 *
	 */
	
	public void setLastUpdTs (Date lastUpdTs) {
		this.lastUpdTs = lastUpdTs;
	}

	/**
	 * getLastUpdTs()
	 *
	 * @return lastUpdTs
	 *
	 */
	public Date getLastUpdTs () {
		return this.lastUpdTs;
	}




	/** 
	 * lastUpdUser
	 *
	 */
	@Column(name="lastupduser")
	protected int lastUpdUser;

	/**
	 * setLastUpdUser(int lastUpdUser)
	 *
	 * @param lastUpdUser
	 *
	 */
	
	public void setLastUpdUser (int lastUpdUser) {
		this.lastUpdUser = lastUpdUser;
	}

	/**
	 * getLastUpdUser()
	 *
	 * @return lastUpdUser
	 *
	 */
	public int getLastUpdUser () {
		return this.lastUpdUser;
	}




	/**
	 * typeSample
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="typesampleid", insertable=false, updatable=false)
	protected TypeSample typeSample;

	/**
	 * setTypeSample (TypeSample typeSample)
	 *
	 * @param typeSample
	 *
	 */
	public void setTypeSample (TypeSample typeSample) {
		this.typeSample = typeSample;
		this.typeSampleId = typeSample.typeSampleId;
	}

	/**
	 * getTypeSample ()
	 *
	 * @return typeSample
	 *
	 */
	
	public TypeSample getTypeSample () {
		return this.typeSample;
	}


	/**
	 * subtypeSample
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="subtypesampleid", insertable=false, updatable=false)
	protected SubtypeSample subtypeSample;

	/**
	 * setSubtypeSample (SubtypeSample subtypeSample)
	 *
	 * @param subtypeSample
	 *
	 */
	public void setSubtypeSample (SubtypeSample subtypeSample) {
		this.subtypeSample = subtypeSample;
		this.subtypeSampleId = subtypeSample.subtypeSampleId;
	}

	/**
	 * getSubtypeSample ()
	 *
	 * @return subtypeSample
	 *
	 */
	
	public SubtypeSample getSubtypeSample () {
		return this.subtypeSample;
	}


	/**
	 * job
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="submitter_jobid", insertable=false, updatable=false)
	protected Job job;

	/**
	 * setJob (Job job)
	 *
	 * @param job
	 *
	 */
	public void setJob (Job job) {
		this.job = job;
		this.submitterJobId = job.jobId;
	}

	/**
	 * getJob ()
	 *
	 * @return job
	 *
	 */
	
	public Job getJob () {
		return this.job;
	}


	/**
	 * lab
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="submitter_labid", insertable=false, updatable=false)
	protected Lab lab;

	/**
	 * setLab (Lab lab)
	 *
	 * @param lab
	 *
	 */
	public void setLab (Lab lab) {
		this.lab = lab;
		this.submitterLabId = lab.labId;
	}

	/**
	 * getLab ()
	 *
	 * @return lab
	 *
	 */
	
	public Lab getLab () {
		return this.lab;
	}


	/**
	 * user
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="submitter_userid", insertable=false, updatable=false)
	protected User user;

	/**
	 * setUser (User user)
	 *
	 * @param user
	 *
	 */
	public void setUser (User user) {
		this.user = user;
		this.submitterUserId = user.UserId;
	}

	/**
	 * getUser ()
	 *
	 * @return user
	 *
	 */
	
	public User getUser () {
		return this.user;
	}


	/** 
	 * sampleMeta
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<SampleMeta> sampleMeta;


	/** 
	 * getSampleMeta()
	 *
	 * @return sampleMeta
	 *
	 */
	public List<SampleMeta> getSampleMeta() {
		return this.sampleMeta;
	}


	/** 
	 * setSampleMeta
	 *
	 * @param sampleMeta
	 *
	 */
	public void setSampleMeta (List<SampleMeta> sampleMeta) {
		this.sampleMeta = sampleMeta;
	}



	/** 
	 * sampleSource
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<SampleSource> sampleSource;


	/** 
	 * getSampleSource()
	 *
	 * @return sampleSource
	 *
	 */
	public List<SampleSource> getSampleSource() {
		return this.sampleSource;
	}


	/** 
	 * setSampleSource
	 *
	 * @param sampleSource
	 *
	 */
	public void setSampleSource (List<SampleSource> sampleSource) {
		this.sampleSource = sampleSource;
	}



	/** 
	 * sampleSourceViaSourceSampleId
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="source_sampleid", insertable=false, updatable=false)
	protected List<SampleSource> sampleSourceViaSourceSampleId;


	/** 
	 * getSampleSourceViaSourceSampleId()
	 *
	 * @return sampleSourceViaSourceSampleId
	 *
	 */
	public List<SampleSource> getSampleSourceViaSourceSampleId() {
		return this.sampleSourceViaSourceSampleId;
	}


	/** 
	 * setSampleSourceViaSourceSampleId
	 *
	 * @param sampleSource
	 *
	 */
	public void setSampleSourceViaSourceSampleId (List<SampleSource> sampleSource) {
		this.sampleSourceViaSourceSampleId = sampleSource;
	}



	/** 
	 * sampleBarcode
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<SampleBarcode> sampleBarcode;


	/** 
	 * getSampleBarcode()
	 *
	 * @return sampleBarcode
	 *
	 */
	public List<SampleBarcode> getSampleBarcode() {
		return this.sampleBarcode;
	}


	/** 
	 * setSampleBarcode
	 *
	 * @param sampleBarcode
	 *
	 */
	public void setSampleBarcode (List<SampleBarcode> sampleBarcode) {
		this.sampleBarcode = sampleBarcode;
	}



	/** 
	 * sampleLab
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<SampleLab> sampleLab;


	/** 
	 * getSampleLab()
	 *
	 * @return sampleLab
	 *
	 */
	public List<SampleLab> getSampleLab() {
		return this.sampleLab;
	}


	/** 
	 * setSampleLab
	 *
	 * @param sampleLab
	 *
	 */
	public void setSampleLab (List<SampleLab> sampleLab) {
		this.sampleLab = sampleLab;
	}



	/** 
	 * jobSample
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<JobSample> jobSample;


	/** 
	 * getJobSample()
	 *
	 * @return jobSample
	 *
	 */
	public List<JobSample> getJobSample() {
		return this.jobSample;
	}


	/** 
	 * setJobSample
	 *
	 * @param jobSample
	 *
	 */
	public void setJobSample (List<JobSample> jobSample) {
		this.jobSample = jobSample;
	}



	/** 
	 * sampleFile
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<SampleFile> sampleFile;


	/** 
	 * getSampleFile()
	 *
	 * @return sampleFile
	 *
	 */
	public List<SampleFile> getSampleFile() {
		return this.sampleFile;
	}


	/** 
	 * setSampleFile
	 *
	 * @param sampleFile
	 *
	 */
	public void setSampleFile (List<SampleFile> sampleFile) {
		this.sampleFile = sampleFile;
	}



	/** 
	 * run
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<Run> run;


	/** 
	 * getRun()
	 *
	 * @return run
	 *
	 */
	public List<Run> getRun() {
		return this.run;
	}


	/** 
	 * setRun
	 *
	 * @param run
	 *
	 */
	public void setRun (List<Run> run) {
		this.run = run;
	}



	/** 
	 * runLane
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<RunLane> runLane;


	/** 
	 * getRunLane()
	 *
	 * @return runLane
	 *
	 */
	public List<RunLane> getRunLane() {
		return this.runLane;
	}


	/** 
	 * setRunLane
	 *
	 * @param runLane
	 *
	 */
	public void setRunLane (List<RunLane> runLane) {
		this.runLane = runLane;
	}



	/** 
	 * statesample
	 *
	 */
	@NotAudited
	@OneToMany
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected List<Statesample> statesample;


	/** 
	 * getStatesample()
	 *
	 * @return statesample
	 *
	 */
	public List<Statesample> getStatesample() {
		return this.statesample;
	}


	/** 
	 * setStatesample
	 *
	 * @param statesample
	 *
	 */
	public void setStatesample (List<Statesample> statesample) {
		this.statesample = statesample;
	}



}
