
/**
 *
 * FileService.java 
 * @author echeng (table2type.pl)
 *  
 * the FileService
 *
 *
 **/

package edu.yu.einstein.wasp.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.yu.einstein.wasp.dao.FileGroupDao;
import edu.yu.einstein.wasp.dao.FileHandleDao;
import edu.yu.einstein.wasp.exception.FileDownloadException;
import edu.yu.einstein.wasp.exception.FileUploadException;
import edu.yu.einstein.wasp.exception.GridException;
import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.exception.SampleTypeException;
import edu.yu.einstein.wasp.filetype.FileTypeAttribute;
import edu.yu.einstein.wasp.grid.GridUnresolvableHostException;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.interfacing.Hyperlink;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileGroupMeta;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.FileHandleMeta;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.JobDraft;
import edu.yu.einstein.wasp.model.JobDraftFile;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleSource;
import edu.yu.einstein.wasp.model.Software;
import edu.yu.einstein.wasp.viewpanel.FileDataTabViewing;

@Service
public interface FileService extends WaspService {

	/**
	 * setFileHandleDao(FileHandleDao fileDao)
	 *
	 * @param fileDao
	 *
	 */
	public void setFileHandleDao(FileHandleDao fileDao);

	/**
	 * getFileDao();
	 *
	 * @return fileDao
	 *
	 */
	public FileHandleDao getFileHandleDao();
	
	/**
	 * @param fileDao
	 */
	public void setFileGroupDao(FileGroupDao fileDao);
	
	/**
	 * @return
	 */
	public FileGroupDao getFileGroupDao();

	/**
	 * Return a file object with specified file id
	 * @param fileId
	 * @return
	 */
    public FileHandle getFileHandleById (final int id);
    
    /**
     * Return a file group
     * @param id
     * @return
     */
    public FileGroup getFileGroupById (final int id);

	/**
	 * 
	 * @param jobdraft
	 * @param mpFile
	 * @param destPath
	 * @param description
	 * @param Random randomNumberGenerator
	 * @return entity-managed file object
	 * @throws FileUploadException
	 */
	public FileGroup processUploadedFile(MultipartFile mpFile, JobDraft jobDraft, String description, Random randomNumberGenerator) throws FileUploadException;
	
	public FileGroup promoteJobDraftFileGroupToJob(Job job, FileGroup filegroup) throws GridUnresolvableHostException, IOException;

	
	/**
	 * links a file object to a specified jobDraft
	 * @param file
	 * @param jobDraft
	 * @return the entity-managed JobDraftFile object created
	 */
	public JobDraftFile linkFileGroupWithJobDraft(FileGroup file, JobDraft jobDraft);

	/**
	 * Returns a list of files of specified fileType or an empty list if none
	 * @param fileType
	 * @return
	 */
	public Set<FileGroup> getFilesByType(FileType fileType);
	
	/**
	 * Returns a list of files of specified fileType and attributes or an empty list if none
	 * @param fileType
	 * @param attributes
	 * @param hasOnlyAttributesSpecified
	 * @return
	 */
	public Set<FileGroup> getFilesByType(FileType fileType, Set<? extends FileTypeAttribute> attributes, boolean hasOnlyAttributesSpecified);

	
	/**
	 * Returns a list of files of specified fileType for the given library or an empty list if none.
	 * @param fileType
	 * @param library
	 * @return
	 * @throws SampleTypeException
	 */
	public Set<FileGroup> getFilesForLibraryByType(Sample library, FileType fileType) throws SampleTypeException;
	
	/**
	 * Returns a list of files of specified fileType with the provided attributes for the given library or an empty list if none.
	 * @param fileType
	 * @param library
	 * @param attributes
	 * @param hasOnlyAttributesSpecified
	 * @return
	 * @throws SampleTypeException
	 */
	Set<FileGroup> getFilesForLibraryByType(Sample library, FileType fileType, Set<? extends FileTypeAttribute> attributes, boolean hasOnlyAttributesSpecified) throws SampleTypeException;
	
	/**
	 * @param cellLibrary
	 * @param fileType
	 * @return
	 */
	public Set<FileGroup> getFilesForCellLibraryByType(SampleSource cellLibrary, FileType fileType);
	
	/**
	 * @param cellLibrary
	 * @return
	 */
	public Set<FileGroup> getFilesForCellLibrary(SampleSource cellLibrary);

	/**
	 * Returns a list of files for the given library or an empty list if none.
	 * @param library
	 * @return
	 * @throws SampleTypeException
	 */
	public Set<FileGroup> getFilesForLibrary(Sample library) throws SampleTypeException;

	/**
	 * Returns a Map of files for a given library associated by FileType
	 * @param library
	 * @return
	 * @throws SampleTypeException
	 */
	public Map<FileType, Set<FileGroup>> getFilesForLibraryMappedToFileType(Sample library) throws SampleTypeException;
	
	/**
	 * Returns a list of files of specified fileType for the given platformUnit or an empty list if none.
	 * @param fileType
	 * @param platformUnit
	 * @return
	 * @throws SampleTypeException
	 */
	public Set<FileGroup> getFilesForPlatformUnitByType(Sample platformUnit, FileType fileType) throws SampleTypeException;
	
	/**
	 * Returns a list of files of specified fileType and attributes for the given platformUnit or an empty list if none.
	 * @param fileType
	 * @param platformUnit
	 * @param attributes
	 * @param hasOnlyAttributesSpecified
	 * @return
	 * @throws SampleTypeException
	 */
	public Set<FileGroup> getFilesForPlatformUnitByType(Sample platformUnit, FileType fileType, Set<? extends FileTypeAttribute> attributes, boolean hasOnlyAttributesSpecified) throws SampleTypeException;

	/**
	 * Returns a list of files for the given platformUnit or an empty list if none.
	 * @param platformUnit
	 * @return
	 * @throws SampleTypeException
	 */
	public Set<FileGroup> getFilesForPlatformUnit(Sample platformUnit) throws SampleTypeException;

	/**
	 * Returns a Map of files for a given platformUnit associated by FileType
	 * @param platformUnit
	 * @return
	 * @throws SampleTypeException
	 */
	public Map<FileType, Set<FileGroup>> getFilesForPlatformUnitMappedToFileType(Sample platformUnit) throws SampleTypeException;
	
	
	
	public FileHandle addFile(FileHandle file);
	
	public FileGroup addFileGroup(FileGroup group);
	
	public void setSampleFile(FileGroup file, Sample sample);
	
	public void setSampleSourceFile(FileGroup group, SampleSource sampleSource);

	public Set<FileType> getFileTypes();
	
	public FileType getFileType(String iname);
	
	/**
	 * @param group
	 * @throws FileNotFoundException
	 * @throws GridException
	 */
	public void register(Collection<FileHandle> fileHandles) throws FileNotFoundException, GridException;
	
	public GridResult register(Collection<FileHandle> fileHandles, GridResult result) throws FileNotFoundException, GridException;
	
	public FileHandle getFileHandle(UUID uuid) throws FileNotFoundException;

	public void removeUploadedFileFromJobDraft(Integer jobDraftId, Integer fileGroupId, Integer fileHandleId) throws FileNotFoundException;

	public Map<String, Hyperlink> getFileDetailsByFileType(FileGroup filegroup);
	
	List<FileDataTabViewing> getTabViewProvidingPluginsByFileGroup(FileGroup fileGroup);

	public FileType getFileType(Integer id);

	public Map<FileType, Set<FileGroup>> getFilesForCellLibraryMappedToFileType(Sample cell, Sample library) throws SampleTypeException;
	
	public Set<FileGroup> getFilesForCellLibraryByType(SampleSource cellLibrary, FileType fileType, Set<? extends FileTypeAttribute> attributes, boolean hasOnlyAttributesSpecified);

	public Set<FileGroup> getFilesForCellLibraryByType(Sample cell, Sample library, FileType fileType) throws SampleTypeException;
	
	public Set<FileGroup> getFilesForCellLibraryByType(Sample cell, Sample library, FileType fileType, Set<? extends FileTypeAttribute> attributes, boolean hasOnlyAttributesSpecified) throws SampleTypeException;

	/**
	 * @param group
	 * @throws FileNotFoundException
	 * @throws GridException
	 */
	void registerWithoutMD5(Collection<FileHandle> fileHandles) throws FileNotFoundException, GridException;


	/**
	 * 
	 */
	public FileGroup uploadJobFile(MultipartFile mpFile, Job job, String fileDescription, Random randomNumberGenerator) throws FileUploadException;
	
	/**
	 * just uploads the file, saves it in remote loaction, and returns file group. DOES NOT add entry to jobfile table; job is only used to set the directory, using jobId.
	 */
	public FileGroup uploadFileAndReturnFileGroup(MultipartFile mpFile, Job job, String fileDescription, Random randomNumberGenerator) throws FileUploadException;

	/**
	 * 
	 */
	public void uploadJobDraftFile(MultipartFile mpFile, JobDraft jobDraft, String fileDescription, Random randomNumberGenerator) throws FileUploadException;


	/**
	 * @throws FileUploadException 
	 * 
	 */
	public void copyFileHandleToOutputStream(FileHandle fileHandle, OutputStream os) throws FileDownloadException, FileNotFoundException, GridException, FileUploadException;
	
	/**
	 * @throws FileUploadException 
	 * 
	 */
	public void copyFileHandlesInFileGroupToOutputStream(FileGroup fileGroup, OutputStream os) throws FileDownloadException, FileNotFoundException, GridException, FileUploadException;


	/**
	 * @param String fileName
	 * @return String mimeType (if not known, return empty string)
	 */
	public String getMimeType(String fileName);
	

	public String generateUniqueBaseFileName(SampleSource cellLibrary);

	public String generateUniqueBaseFileName(Sample sample);

	public File createTempFile() throws FileUploadException;
	
	public FileGroup saveLocalJobFile(Job job, File localFile, String fileName, String fileDescription, Random randomNumberGenerator) throws FileUploadException;

	public FileGroup saveLocalQuoteOrInvoiceFile(Job job, File localFile, String fileName, String fileDescription, Random randomNumberGenerator) throws FileUploadException;

	public List<FileGroupMeta> saveFileGroupMeta(List<FileGroupMeta> metaList, FileGroup filegroup) throws MetadataException;

	public FileGroup getFileGroup(UUID uuid) throws FileNotFoundException;

	/**
	 * Returns a list of files (actually a Set<FileGroup) of specified fileType for the given dna macromolecule sample, rna sample, or library sample, or an empty list if none.
	 * @param fileType
	 * @param sample of type library, dna, rna
	 * @return
	 * @throws SampleTypeException
	 */	
	public Set<FileGroup> getFilesForMacromoleculeOrLibraryByType(Sample sample, FileType fileType) throws SampleTypeException;
	
	/**
	 * Returns a list of files (actually a Set<FileGroup) of specified fileType and attributes for the given dna macromolecule sample, rna sample, 
	 * or library sample, or an empty list if none.
	 * @param fileType
	 * @param sample of type library, dna, rna
	 * @param attributes
	 * @param hasOnlyAttributesSpecified
	 * @return
	 * @throws SampleTypeException
	 */	
	public Set<FileGroup> getFilesForMacromoleculeOrLibraryByType(Sample sample, FileType fileType, Set<? extends FileTypeAttribute> attributes, boolean hasOnlyAttributesSpecified) throws SampleTypeException;
	
	/**
	 * Returns a Map of files (actually a Map of a Set<FileGroup) for a given macromolecule or library associated by FileType
	 * @param sample of type library, dna, rna 
	 * @return
	 * @throws SampleTypeException
	 */
	public Map<FileType, Set<FileGroup>> getFilesForMacromoleculeOrLibraryMappedToFileType(Sample sample) throws SampleTypeException;

	public List<FileHandleMeta> saveFileHandleMeta(List<FileHandleMeta> metaList, FileHandle filehandle) throws MetadataException;

	public Set<FileGroup> getActiveFilesForCellLibrary(SampleSource cellLibrary);
	
	public Set<FileGroup> getActiveFilesForSample(Sample sample);
	
	public Set<FileGroup> getFilesForSample(Sample sample);

	public String generateUniqueBaseFileName(Job job);

	/**
	 * Deletes the files in the provided filegroup on the remote server and marks all corresponding fileHandles and the filegroup
	 * as inactive and deleted
	 * @param fileGroup
	 * @throws Exception
	 */
	public void removeFileGroupFromRemoteServerAndMarkDeleted(FileGroup fileGroup) throws Exception;

	/**
	 * Deletes the provided file on the remote server and marks the fileHandle deleted.
	 * If the parent filegroup no longer contains any non-deleted filehandles, it too is set as inactive and deleted in the database
	 * @param fileHandle
	 * @throws Exception
	 */
	public void removeFileHandleFromRemoteServerAndMarkDeleted(FileHandle fileHandle) throws Exception;

	public String generateJobSoftwareBaseFolderName(Job job, Software software);

	public FileHandle saveInDiscreteTransaction(FileHandle file);

	/**
	 * CamelCase the name and remove any illegal characters
	 * @param name
	 * @return
	 */
	public String getSanitizedName(String name);

	public FileGroup saveInDiscreteTransaction(FileGroup group, Set<? extends FileTypeAttribute> attributes);

	public InputStream getInputStreamFromFileHandle(FileHandle fileHandle);

	public String getURLStringFromFileHandle(FileHandle fileHandle);

	/**
	 * Generated and persists a new FileGroup, adds each provided child FileGroup and ensures these are also persisted.
	 * @param childFileGroups
	 * @return a new entity-managed FileGroup containing a set of the provided FileGroups (also entity-managed)
	 */
	public FileGroup createFileGroupCollection(Set<FileGroup> childFileGroups);

	/**
	 * If the FileGroup is a collection of other FileGroups, return true, otherwise return false
	 * @param fg
	 * @return true / false
	 */
	public boolean isFileGroupCollection(FileGroup fg);

	/**
	 * Returns a set of all the FileHandles contained within all the child FileGroups maintained in this FileGroup collection. If a set of FileHandles is also associated
	 * with this FileGroup directly, they are also returned (although it is recommended that FileGroup collections only contain FileGroups).
	 * @param fgCollection
	 * @return
	 */
	public Set<FileHandle> getAllFileHandlesFromFileGroupCollection(FileGroup fgCollection);
	
	public File createLocalTempFile();

	/**
	 * Removes the specified filegroup entry and all its metadata from the database
	 * @param fileGroup
	 */
	public void remove(FileGroup fileGroup);

	/**
	 * Removes the specified filegroup and all associated filehandles (plus all metadata) from the database. WARNING: fileHandles may be associated with other FileGroups.
	 * @param fileGroup
	 */
	public void removeWithAllAssociatedFilehandles(FileGroup fileGroup);

	/**
	 * Remove the specified filehandle and all its metadata from the database
	 * @param fileHandle
	 */
	public void remove(FileHandle fileHandle);
	
	/**
	 * Adds (inner) child file groups to existing (outer) parental FileGroup and ensures persistence.
	 * @paran parentalFileGroup (existing, outer or enclosing file group)
	 * @param childFileGroupSet (child (inner) file groups to be added)
	 * @return parentalFileGroup (entity-managed)
	 */
	public FileGroup addToFileGroupCollection(FileGroup parentFileGroup, Set<FileGroup> childFileGroupSet);
	
	/**
	 * @throws FileUploadException 
	 * 
	 */
	public File copyFileHandleToLocalTempFile(FileHandle fileHandle) throws FileUploadException, FileDownloadException, FileNotFoundException, GridException;
		
}

