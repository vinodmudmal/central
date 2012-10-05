/**
 *
 * SampleService.java 
 * @author echeng (table2type.pl)
 *  
 * the SampleService
 *
 *
 **/

package edu.yu.einstein.wasp.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.dao.SampleDao;
import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.exception.SampleException;
import edu.yu.einstein.wasp.exception.SampleIndexException;
import edu.yu.einstein.wasp.exception.SampleMultiplexException;
import edu.yu.einstein.wasp.exception.SampleParentChildException;
import edu.yu.einstein.wasp.exception.SampleSubtypeException;
import edu.yu.einstein.wasp.exception.SampleTypeException;
import edu.yu.einstein.wasp.model.Adaptor;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.Run;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleDraft;
import edu.yu.einstein.wasp.model.SampleMeta;
import edu.yu.einstein.wasp.model.SampleSubtype;
import edu.yu.einstein.wasp.model.SampleType;

@Service
public interface SampleService extends WaspService {

	/**
	 * setSampleDao(SampleDao sampleDao)
	 * 
	 * @param sampleDao
	 * 
	 */
	public void setSampleDao(SampleDao sampleDao);

	/**
	 * getSampleDao();
	 * 
	 * @return sampleDao
	 * 
	 */
	public SampleDao getSampleDao();

	public Sample getSampleByName(final String name);

	List<Sample> findAllPlatformUnits();

	  /**
	   * Gets a list of {@link SampleSubtype} objects associated with given workflow which are specified as viewable
	   * to the currently logged in user (role dependent)
	   * If no role annotation is provided for the SampleSubtype it is also returned
	   * @param workflowId
	   * @return List<{@link SampleSubtype}>
	   */
	  public List<SampleSubtype> getSampleSubtypesForWorkflowByLoggedInUserRoles(Integer workflowId);

	  /**
	   * Gets a list of {@link SampleSubtype} objects associated with given workflowId and sampleTypeId which are specified as viewable
	   * to the currently logged in user (role dependent)
	   * If no role annotation is provided for the SampleSubtype it is also returned
	   * @param workflowId
	   * @return List<{@link SampleSubtype}>
	   */
	  public List<SampleSubtype> getSampleSubtypesForWorkflowByLoggedInUserRoles(Integer workflowId, String sampleTypeIName);


	  /**
	   * Gets a list of {@link SampleSubtype} objects associated with given workflowId and sampleTypeId which are specified as viewable
	   * to the users with given roles (role dependent). 
	   * If no role annotation is provided for the SampleSubtype it is also returned
	   * @param workflowId
	   * @param roles
	   * @param sampleTypeIName
	   * @return
	   */
	  public List<SampleSubtype> getSampleSubtypesForWorkflowByRole(Integer workflowId,	String[] roles, String sampleTypeIName);

	  /**
	   * Gets a list of {@link SampleSubtype} objects associated with given workflowId which are specified as viewable
	   * to the users with given roles (role dependent)
	   * If no role annotation is provided for the SampleSubtype it is also returned
	   * @param workflowId
	   * @param roles
	   * @return
	   */
	  public List<SampleSubtype> getSampleSubtypesForWorkflowByRole(Integer workflowId,	String[] roles);

	  /**
	   * Returns true unless a sample exists in job of the same sampleType with the same name as the provided Sample
	   * @param Sample
	   * @param sampleType
	   * @param job
	   * @return boolean
	   */
	  public boolean isSampleNameUniqueWithinJob(Sample sampleIn, SampleType sampleType, Job job);

	  /**
	   * Saves a sample and all the metadata associated with it
	   * @param sample
	   */
	  public void saveSampleWithAssociatedMeta(Sample sample);

	  /**
	   * Returns string containing the status of a sample's Receive Sample state
	   * @param Sample
	   * @return String
	   */
	  public String getReceiveSampleStatus(final Sample sample);
	  
	  /**
	   * Accepts and (in-situ) sorts list of samples by sample name 
	   * @param List<Sample>
	   * @return void
	   */
	  public void sortSamplesBySampleName(List<Sample> samples);
	  
	  /**
	   * Converts sample's Receive Sample status from database storage meaning to human-comprehensible meaning for viewing on the web
	   * @param String status
	   * @return String 
	   */
	  public String convertReceiveSampleStatusForWeb(String internalStatus);

	  /**
	   * Converts sample's Receive Sample status from web meaning (human-comprehensible meaning) to enum consistent value for internal storage
	   * @param String webStatus
	   * @return String 
	   */
	  public String convertReceiveSampleStatusForInternalStorage(String webStatus);

	  /**
	   * Gets list of Receive Sample options for web display
	   * @param none
	   * @return List<String> containing the list of Receive Sample Options for web display 
	   */
	  public List<String> getReceiveSampleStatusOptionsForWeb();
	  
	  /**
	   * Updates sample's Receive Sample status
	   * @param Sample sample
	   * @param String status (from web)
	   * @return boolean
	   */
	  public boolean updateSampleReceiveStatus(final Sample sample, final String status);
	  
	  /**
	   * Returns boolean informing whether a sample has been processed by the facility
	   * If a macromolecule was submitted to the facility and a library was created from it, then return true
	   * If a library was submitted to the facility and it has been added to a flowcell, then return true
	   * Else return false
	   * @param Sample sample
	   * @return boolean
	   */
	  public boolean submittedSampleHasBeenProcessedByFacility(final Sample sample);
	  
	  /**
	   * Returns a list of facility-generated libraries created from a user-submitted macromolecule
	   * @param Sample
	   * @return List<Sample>
	   */
	  public List<Sample> getFacilityGeneratedLibraries(Sample sample);
	 
	  /**
	   * Returns Adaptor for any library
	   * @param Sample
	   * @return Adaptor
	   */
	  public Adaptor getLibraryAdaptor(Sample library);
	  
	  /**
	   * Returns list of samples that are flow cells to which libraries can be added [meaning whose task-status is CREATED])
	   * @param void
	   * @return List<Sample>
	   */
	  public List<Sample> getAvailableFlowCells();
	  
	  /**
	   * Returns list of samples that are flow cells to which libraries can be added AND are compatible with the parameter job
	   * @param Job job
	   * @return List<Sample>
	   * 
	   */
	  public List<Sample> getAvailableAndCompatibleFlowCells(Job job);

	  
	  /**
	   * Returns Map of Cells according to their index for a platform unit
	   * @param platformUnit
	   * @return Map<Integer, Sample> indexedCells
	   * @throws SampleTypeException 
	   */
	  public Map<Integer, Sample> getIndexedCellsOnPlatformUnit(Sample platformUnit) throws SampleTypeException;

	  /**
	   * Returns Number Of Indexed Cells (lanes) on a platform unit
	   * @param platformUnit
	   * @return Integer numberOfIndexedCells
	   * @throws SampleTypeException 
	   */
	  public Integer getNumberOfIndexedCellsOnPlatformUnit(Sample platformUnit) throws SampleTypeException;

	  /**
	   * Adds a cell to a platform unit. Ensures that the index is unique.
	   * @param platformUnit
	   * @param cell
	   * @param Index
	   * @throws SampleTypeException
	   * @throws SampleIndexException
	   */
	  public void addCellToPlatformUnit(Sample platformUnit, Sample cell, Integer Index) throws SampleTypeException, SampleIndexException;

	  /**
	   * Returns list of Samples on a cell. If control libraries are spiked in, these are also returned.
	   * @param cell
	   * @return list of libraries (including spike-in controls)
	   * @throws SampleTypeException
	   */
	  public List<Sample> getLibrariesOnCell(Sample cell) throws SampleTypeException;

	  /**
	   * Returns list of Samples on a cell. If control libraries are spiked in, these are ignored and NOT returned.
	   * @param cell
	   * @return list of libraries (without spike-in controls)
	   * @throws SampleTypeException
	   */
	  public List<Sample> getLibrariesOnCellWithoutControls(Sample cell) throws SampleTypeException;

	  /**
	   * Returns a list of spiked-in libraries on a cell
	   * @param cell
	   * @return list of control libraries
	   * @throws SampleTypeException
	   */
	  public List<Sample> getControlLibrariesOnCell(Sample cell) throws SampleTypeException;

	  /**
	   * Set the authentication service
	   * @param authenticationService
	   */
	  public void setAuthenticationService(AuthenticationService authenticationService);

	  /**
	   * Get a cell for a given platform unit
	   * @param cell
	   * @return
	   * @throws SampleParentChildException 
	   * @throws SampleTypeException 
	   */
	  public Sample getPlatformUnitForCell(Sample cell) throws SampleTypeException, SampleParentChildException;

	  /**
	   * Adds a given library to the given cell
	   * @param cell
	   * @param library
	   * @param libConcInLanePicoM
	   * @throws SampleTypeException
	   * @throws SampleException
	   * @throws SampleMultiplexException
	   * @throws MetadataException
	   */
	  public void addLibraryToCell(Sample cell, Sample library,	Float libConcInLanePicoM) throws SampleTypeException, SampleException, SampleMultiplexException, MetadataException;

	  /**
	   * Clones a sampleDraft object
	   * @param sampleDraft
	   * @return
	   */
	  public SampleDraft cloneSampleDraft(SampleDraft sampleDraft);

	  /**
	   * Returns list of samples that are platformUnits with task assignLibraryToPlatformUnit and status = CREATED (so, it's not yet part of a sequence run)
	   * @param none
	   * @return List<Sample>
	   */
	  public List<Sample> platformUnitsAwaitingLibraries();
	  
	  /**
	   * Get the run on which a given platform unit has been placed. If the platform unit is not currently associated with an active run or is not associated with a run 
	   * this method returns null
	   * @param platformUnit
	   * @return
	   */
	  public Run getCurrentRunForPlatformUnit(Sample platformUnit);
	  
	  
	  /**
	   * Determine whether a barcodeName is already in the database. 
	   * @param String barcodeName
	   * @return boolean
	   */
	  public boolean barcodeNameExists(String barcodeName);

	  /**
	   * Returns List of SampleSubtypes where SampleType.iname = parameter sampleTypeIName. List ordered ascending by samplesubtype.name. 
	   * If the SampleType is not found, throw SampleTypeException. If SampleType is valid but not entries in SampleSubtype, then
	   * return is an empty list (list.size()=0)
	   * @param String sampleTypeIName
	   * @return List<SampleSubtype>
	   */
	  public List<SampleSubtype> getSampleSubtypesBySampleTypeIName(String sampleTypeIName) throws SampleTypeException;

	  /**
	   * Returns a SampleSubtype with id of sampleSubtypeId. 
	   * If the id not in database, return empty object (sampleSubtype.getSampleSubtypeId is null).
	   * @param Integer sampleSubtypeId
	   * @return SampleSubtype
	   */
	  public SampleSubtype getSampleSubtypeById(Integer sampleSubtypeId);

	  /**
	   * Returns true if SampleSubtype's SampleType has iname of sampleTypeIName, else return false. 
	   * @param SampleSubtype sampleSubtype
	   * @param String sampleTypeIName
	   * @return boolean
	   */
	  public boolean sampleSubtypeIsSpecificSampleType(SampleSubtype sampleSubtype, String sampleTypeIName);
	  
	  /**
	   * Returns a Sample with id of sampleId. 
	   * If the id is not in the database, return an empty object (sample.getSampleId is null). 
	   * @param Integer sampleId
	   * @return Sample
	   */
	  public Sample getSampleById(Integer sampleId);

	  /**
	   * Returns true if sample.getSampleType().getIName.equals(sampleTypeIName). 
	   * If sampleTypeIName==null or sample==null or sample.getSampleType()==null or sample.getSampleType().getIName == null, return false.
	   * @param Sample sample
	   * @param String sampleTypeIName (such as "platformunit")
	   * @return boolean
	   */
	  public boolean sampleIsSpecificSampleType(Sample sample, String sampleTypeIName);

	  /**
	   * Returns true if sample.getSampleSubtype().getIName.equals(sampleSubtypeIName). 
	   * If sampleSubtypeIName==null or sample==null or sample.getSampleSubtype()==null or sample.getSampleSubtype().getIName == null, return false.
	   * @param Sample sample
	   * @param String sampleSubtypeIName (such as "platformunit")
	   * @return boolean
	   */
	  public boolean sampleIsSpecificSampleSubtype(Sample sample, String sampleSubtypeIName);

	  /**
	   * Returns true if Sample is in database, else returns false
	   * True defined as sample != null && sample.getSampleId() != null && sample.getSampleId().intVal() > 0
	   * then use sample.getSampleId().intVal() to pull a sample from the database and the 
	   * sampleReturnedFromDatabase.getSampleId() != null && sampleReturnedFromDatabase.getSampleId().intVal > 0 
	   * @param Sample sample
	   * @return boolean
	   */
	  public boolean sampleIsInDatabase(Sample sample);

	  /**
	   * Returns true if SampleId represents a sample in database, else returns false
	   * True defined as sampleId != null && sampleId.intVal() > 0
	   * then use sampleId().intVal() to pull a sample from the database and the 
	   * sampleReturnedFromDatabase.getSampleId() != null && sampleReturnedFromDatabase.getSampleId().intVal > 0 
	   * @param Integer sampleId
	   * @return boolean
	   */
	  public boolean sampleIdIsInDatabase(Integer sampleId);

	  /**
	   * Returns true if sample != null && sample.getSampleId() != null && sample.getSampleId().intVal() > 0, else false
	   * @param Sample sample
	   * @return boolean
	   */
	  public boolean sampleIdIsValid(Sample sample);

	  /**
	   * Returns true if Sample is a platform unit (checking both SampleType and SampleSubtype) 
	   * @param Sample sample
	   * @return boolean
	   */
	  public boolean sampleIsPlatformUnit(Sample sample);

	  /**
	   * Returns Sample if it exists in database and if it is a platform unit (its sampletype and samplesubtype both have inames of platformunit) 
	   * If sample not found in database or it is not a platformunit in sampletype and samplesubtype, throw NumberFormatException, SampleException, SampleTypeException, or SampleSubtype Exception. 
	   * @param Sample sample
	   * @return Sample
	   */
	  public Sample getPlatformUnit(Integer sampleId) throws NumberFormatException, SampleException, SampleTypeException, SampleSubtypeException;

	  /**
	   * Returns SampleSubtype if it exists in database and if its sampletype is a platform unit 
	   * If sampleSubtype not found in database or if it is not of sampletype platformunit, throws NumberFormatException or SampleSubtype Exception. 
	   * @param SampleSubtype sampleSubtype
	   * @return SampleSubtype
	   */
	  public SampleSubtype getSampleSubtypeConfirmedForPlatformunit(Integer sampleSubtypeId) throws NumberFormatException, SampleSubtypeException;

	  
	  /**
	   * Returns an ordered (ascending) List Of Integers of the number of lanes that are available on a particular type of platformunit (flowcell). 
	   * If the SampleSubtype is not in the database or is not of type platformunit, throw SampleSubtypeException or SampleTypeException, respectively.
	   * If the SampleSubtypeMetadata for maxCellNumber is not found throw a SampleSubtypeException.
	   * If the values for maxCellNumber, and if found multiplicationFactor, are not convertable to numbers, throw NumberFormatException.
	   * If the values for maxCellNumber and if found multiplicationFactor, are unable to generate a list throw a SampleSubtypeException
	   * @param SampleSubtype sampleSubtype
	   * @return List<Integer>
	   */
	  public List<Integer> getNumberOfCellsListForThisTypeOfPlatformUnit(SampleSubtype sampleSubtype) throws SampleTypeException, SampleSubtypeException;

	  /**
	   * Returns true if requested reduction in number of cells of a platformunit will lead to loss of lanes containing libraries 
	   * @param Sample sample
	   * @param Integer numberOfLanesRequested
	   * @return boolean
	   */
	  public boolean requestedReductionInCellNumberIsProhibited(Sample platformUnitInDatabase, Integer numberOfLanesRequested) throws SampleException, SampleTypeException;
	 
	  /**
	   * Create or update platform unit. If platformUnitId==null or platformUnitId.intVal()<=0, create new platformunit, otherwise update.
	   * If create/update is unsuccessful, throw exception, else return void. Under transactional control. 
	   * If this is an update and numberOfLanesRequested > numberOfLanesInDatabase, then add additional lanes.
	   * If this is an update and  numberOfLanesRequested < numberOfLanesInDatabase, then remove extra lanes ONLY IF THE LANES TO BE REMOVED DO NOT CONTAIN LIBRARIES.
	   * @param Sample platformUnit
	   * @param Sample barcodeName
	   * @param Integer numberOfLanesRequested
	   * @param List<SampleMeta> sampleMetaList
	   * @param SampleSubtype sampleSubtype
	   * @return void
	   */
	  public void createUpdatePlatformUnit(Sample platformUnit, SampleSubtype sampleSubtype, String barcodeName, Integer numberOfLanesRequested, List<SampleMeta> sampleMetaList) throws SampleException, SampleTypeException, SampleSubtypeException;
		
	  /**
	   * Deletes platform unit 
	   * @param Sample sample
	   * @return void
	   */
	  public void deletePlatformUnit(Integer platformUnitId) throws NumberFormatException, SampleException, SampleTypeException, SampleSubtypeException;

}
