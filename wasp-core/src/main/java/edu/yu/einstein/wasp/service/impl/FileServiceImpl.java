
/**
 *
 * FileServiceImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the FileService Implmentation 
 *
 *
 **/

package edu.yu.einstein.wasp.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.yu.einstein.wasp.Assert;
import edu.yu.einstein.wasp.dao.FileDao;
import edu.yu.einstein.wasp.dao.JobDraftFileDao;
import edu.yu.einstein.wasp.dao.SampleFileDao;
import edu.yu.einstein.wasp.exception.FileUploadException;
import edu.yu.einstein.wasp.exception.SampleTypeException;
import edu.yu.einstein.wasp.model.File;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.JobDraft;
import edu.yu.einstein.wasp.model.JobDraftFile;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.model.SampleFile;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.SampleService;

@Service
@Transactional("entityManager")
public class FileServiceImpl extends WaspServiceImpl implements FileService {


	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private JobDraftFileDao jobDraftFileDao;
	
	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private SampleFileDao sampleFileDao;
	
	/**
	 * fileDao;
	 *
	 */
	private FileDao fileDao;

	/**
	 * setFileDao(FileDao fileDao)
	 *
	 * @param fileDao
	 *
	 */
	@Override
	@Autowired
	public void setFileDao(FileDao fileDao) {
		this.fileDao = fileDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileDao getFileDao() {
		return this.fileDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getFileByFileId (final int fileId) {
		return this.getFileDao().getFileByFileId(fileId);
	}
  
 
	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getFileByFilelocation (final String filelocation) {
		return this.getFileDao().getFileByFilelocation(filelocation);
	}
	
	/**
	 * TODO: CLEAN UP THIS HORRIBLE TURD
	 * {@inheritDoc}
	 */
	@Override 
	public File processUploadedFile(MultipartFile mpFile, String destPath, String description) throws FileUploadException{
		String noSpacesFileName = mpFile.getOriginalFilename().replaceAll("\\s+", "_");
		//String absolutePath = destPath+"/"+mpFile.getOriginalFilename();
		String absolutePath = destPath+"/"+noSpacesFileName;
		java.io.File pathFile = new java.io.File(destPath);
		if (!pathFile.exists()){
			try{
				pathFile.mkdir();
			} catch(Exception e){
				throw new FileUploadException("File upload failure trying to create '"+destPath+"': "+e.getMessage());
			}
		}
						
		String md5Hash = "";
		try {
			md5Hash = DigestUtils.md5Hex(mpFile.getInputStream());
		} catch (IOException e) {
			//logger.warn("Cannot generate MD5 Hash for '"+mpFile.getOriginalFilename()+"': "+ e.getMessage());
			logger.warn("Cannot generate MD5 Hash for '"+noSpacesFileName+"': "+ e.getMessage());
		}
		//String fileName = mpFile.getOriginalFilename();
		String fileName = mpFile.getOriginalFilename().replaceAll("\\s+", "_");
		Integer fileSizeK = (int)((mpFile.getSize()/1024) + 0.5);
		logger.debug("Uploading file '"+fileName+"' to '"+absolutePath+"' (size="+fileSizeK+"Kb, md5Hash="+md5Hash+")");
		java.io.File newFile = new java.io.File(absolutePath);
		try{
			mpFile.transferTo(newFile);
		} catch(Exception e){
			throw new FileUploadException("File upload failure trying to save '"+absolutePath+"': "+e.getMessage());
		}
		File file = new File();
		file.setDescription(description);
		//file.setFileURI(absolutePath);
		file.setIsActive(1);
		file.setMd5hash(md5Hash);
		file.setSizek(fileSizeK);		
		return fileDao.save(file);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JobDraftFile linkFileWithJobDraft(File file, JobDraft jobDraft){
		JobDraftFile jobDraftFile = new JobDraftFile();
		jobDraftFile.setFile(file);
		jobDraftFile.setJobDraft(jobDraft);
		return jobDraftFileDao.save(jobDraftFile);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<File> getFilesByType(FileType fileType){
		Assert.assertParameterNotNull(fileType, "must provide a fileType");
		Assert.assertParameterNotNull(fileType.getFileTypeId(), "fileType has no valid fileTypeId");
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("fileTypeId", fileType.getFileTypeId());
		return fileDao.findByMap(m);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SampleTypeException 
	 */
	@Override
	public List<File> getFilesForLibrary(Sample library) throws SampleTypeException{
		Assert.assertParameterNotNull(library, "must provide a library");
		if (!sampleService.isLibrary(library))
			throw new SampleTypeException("sample is not of type library");
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("sampleId", library.getSampleId());
		List<File> files = new ArrayList<File>();
		for (SampleFile sf: sampleFileDao.findByMap(m))
			files.add(sf.getFile());
		return files;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SampleTypeException 
	 */
	@Override
	public List<File> getFilesByForLibraryByType(Sample library, FileType fileType) throws SampleTypeException{
		Assert.assertParameterNotNull(fileType, "must provide a fileType");
		Assert.assertParameterNotNull(fileType.getFileTypeId(), "fileType has no valid fileTypeId");
		List<File> files = new ArrayList<File>();
		for (File f: getFilesForLibrary(library)){
			if (f.getFileTypeId().equals(fileType.getFileTypeId()))
				files.add(f);
		}
		return files;
	}
	
}

