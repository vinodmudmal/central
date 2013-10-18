/**
 * 
 */
package edu.yu.einstein.wasp.filetype.service;

import java.util.Set;

import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;

/**
 * @author calder
 *
 */
public interface FileTypeService {
	
	public static final String FILETYPE_IS_SINGLE_META_KEY = "isSingleton";
	public static final String FILETYPE_FILE_NUMBER_META_KEY = "fileNumber";
	public static final String FILETYPE_AREA = "filetype";
	public static final String FILEGROUP_ATTRIBUTE_META_KEY = "fileGroupAttribute";
	
	/**
	 * Is this a single file (or one of a series)?
	 * 
	 * @param file
	 * @return
	 */
	public boolean isSingleFile(FileHandle file);
	
	/**
	 * get the number of the file in the series.  Always 0-based and 0 or null if there is only one file.
	 * In the case where multiple files comprise a component of a single file (e.g. separate FASTQ files
	 * for forward and reverse reads in an Illumina run), each of these files will have the same "file number"
	 * and must be discriminated by an additional piece of metadata supplied by the file specific service. (e.g.
	 * FastqService.FASTQ_READ_SEGMENT_NUMBER). 
	 * 
	 * @param file
	 * @return integer or null
	 */
	public Integer getFileNumber(FileHandle file);
	
	/**
	 * add a file group attribute, typically managed as a public static variable in the specific file type service
	 * implementation (eg FastqService).
	 * @param fg
	 * @param attribute
	 */
	public void addAttribute(FileGroup fg, String attribute);
	
	/**
	 * remove a FileGroup attribute
	 * @param fg
	 * @param attribute
	 */
	public void removeAttribute(FileGroup fg, String attribute);
	
	/**
	 * set FileGroup attributes at once
	 * @param fg
	 * @param attributes
	 */
	public void setAttributes(FileGroup fg, Set<String> attributes);
	
	/**
	 * get all FileGroup attributes
	 * @param fg
	 * @return
	 */
	public Set<String> getAttributes(FileGroup fg);
	
	/**
	 * Test whether or not the FileGroup has at least the set of attributes
	 * @param fg
	 * @param attributes
	 * @return
	 */
	public boolean hasAttributes(FileGroup fg, Set<String> attributes);
	
	/**
	 * Test whether or not the FileGroup has ONLY the set of attributes.
	 * @param fg
	 * @param attributes
	 * @return
	 */
	public boolean hasOnlyAttributes(FileGroup fg, Set<String> attributes);

}
