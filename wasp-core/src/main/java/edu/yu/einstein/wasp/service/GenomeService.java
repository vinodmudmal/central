/**
 *
 * SampleService.java 
 * 
 * the SampleService
 *
 *
 **/

package edu.yu.einstein.wasp.service;


import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.exception.ParameterValueRetrievalException;
import edu.yu.einstein.wasp.grid.work.GridWorkService;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleDraft;
import edu.yu.einstein.wasp.plugin.supplemental.organism.Build;
import edu.yu.einstein.wasp.plugin.supplemental.organism.Organism;


@Service
@Transactional("entityManager")
public interface GenomeService extends WaspService {

	public Set<Organism> getOrganisms();
	
	public boolean exists(GridWorkService workService, Build build, String index);

	/**
	 * Returns registered organism who's organism ID matches that given, or null if no match
	 * @param name
	 * @return
	 */
	public Organism getOrganismById(Integer organismId);
	
	public Map<String, Build> getBuilds(Integer organism, String genome) throws ParameterValueRetrievalException;
	
	public Build getBuild(Integer organism, String genome, String build) throws ParameterValueRetrievalException;
	
	/**
	 * Gets the build based on a delimited string generated by this service ({@link getDelimitedParameterString})
	 * @param delimitedParameterString
	 * @return
	 * @throws ParameterValueRetrievalException
	 */
	public Build getBuild(String delimitedParameterString) throws ParameterValueRetrievalException;
	
	/**
	 * Creates a string containing genome build information which can be later used to re-create a Build object with {@link getBuild}
	 * @param build
	 * @return
	 */
	public String getDelimitedParameterString(Build build);
	
	public void setBuild(Sample sample, Build build) throws MetadataException;
	
	/**
	 * applies build to all samples in the supplied set
	 * @param sampleDraftSet
	 * @param build
	 */
	public void setBuildToAllSamples(Set<Sample> sampleSet, Build build);
	
	public void setBuild(SampleDraft sampleDraft, Build build) throws MetadataException;
	
	/**
	 * applies build to all sampleDrafts in the supplied set
	 * @param sampleDraftSet
	 * @param build
	 */
	public void setBuildToAllSampleDrafts(Set<SampleDraft> sampleDraftSet, Build build);
	
	public Build getBuild(SampleDraft sampleDraft) throws ParameterValueRetrievalException;
	
	public Build getBuild(Sample sample) throws ParameterValueRetrievalException;

	  
}
