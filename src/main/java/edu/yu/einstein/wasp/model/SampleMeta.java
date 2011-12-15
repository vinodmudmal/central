
/**
 *
 * SampleMeta.java 
 * @author echeng (table2type.pl)
 *  
 * the SampleMeta
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
@Table(name="samplemeta")
public class SampleMeta extends MetaBase {

	/** 
	 * sampleMetaId
	 *
	 */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer sampleMetaId;

	/**
	 * setSampleMetaId(Integer sampleMetaId)
	 *
	 * @param sampleMetaId
	 *
	 */
	
	public void setSampleMetaId (Integer sampleMetaId) {
		this.sampleMetaId = sampleMetaId;
	}

	/**
	 * getSampleMetaId()
	 *
	 * @return sampleMetaId
	 *
	 */
	public Integer getSampleMetaId () {
		return this.sampleMetaId;
	}




	/** 
	 * sampleId
	 *
	 */
	@Column(name="sampleid")
	protected Integer sampleId;

	/**
	 * setSampleId(Integer sampleId)
	 *
	 * @param sampleId
	 *
	 */
	
	public void setSampleId (Integer sampleId) {
		this.sampleId = sampleId;
	}

	/**
	 * getSampleId()
	 *
	 * @return sampleId
	 *
	 */
	public Integer getSampleId () {
		return this.sampleId;
	}




	/**
	 * sample
	 *
	 */
	@NotAudited
	@ManyToOne
	@JoinColumn(name="sampleid", insertable=false, updatable=false)
	protected Sample sample;

	/**
	 * setSample (Sample sample)
	 *
	 * @param sample
	 *
	 */
	public void setSample (Sample sample) {
		this.sample = sample;
		this.sampleId = sample.sampleId;
	}

	/**
	 * getSample ()
	 *
	 * @return sample
	 *
	 */
	
	public Sample getSample () {
		return this.sample;
	}


}
