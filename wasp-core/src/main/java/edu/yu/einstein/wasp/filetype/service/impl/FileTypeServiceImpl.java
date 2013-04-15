/**
 * 
 */
package edu.yu.einstein.wasp.filetype.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.Assert;
import edu.yu.einstein.wasp.dao.FileGroupDao;
import edu.yu.einstein.wasp.dao.FileGroupMetaDao;
import edu.yu.einstein.wasp.dao.FileHandleDao;
import edu.yu.einstein.wasp.dao.FileHandleMetaDao;
import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.filetype.service.FileTypeService;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileGroupMeta;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.FileHandleMeta;
import edu.yu.einstein.wasp.service.impl.WaspServiceImpl;
import edu.yu.einstein.wasp.util.MetaHelper;

/**
 * @author calder
 *
 */
@Service
@Transactional("entityManager")
public abstract class FileTypeServiceImpl extends WaspServiceImpl implements FileTypeService {
	
	@Autowired
	private FileGroupMetaDao fileGroupMetaDao;
	
	@Autowired
	private FileHandleDao fileHandleDao;
	
	@Autowired
	private FileGroupDao fileGroupDao;
	
	private FileHandleMetaDao fileMetaDao;
	
	@Autowired
	public void setFileMetaDao(FileHandleMetaDao fileMetaDao) {
		this.fileMetaDao = fileMetaDao;
	}

	@Override
	public boolean isSingleFile(FileHandle file) {
		String single = getMeta(file, FILETYPE_AREA, FILETYPE_IS_SINGLE_META_KEY);
		Boolean b = new Boolean(single);
		return b.booleanValue();
	}
	
	public void setSingleFile(FileHandle file, boolean single) throws MetadataException {
		Boolean b = new Boolean(single);
		setMeta(file, FILETYPE_AREA, FILETYPE_IS_SINGLE_META_KEY, b.toString());
	}

	@Override
	public Integer getFileNumber(FileHandle file) {
		String num = getMeta(file, FILETYPE_AREA, FILETYPE_FILE_NUMBER_META_KEY);
		return new Integer(num);
	}
	
	public void setFileNumber(FileHandle file, Integer number) throws MetadataException {
		setMeta(file, FILETYPE_AREA, FILETYPE_FILE_NUMBER_META_KEY, number.toString());
	}
	
	protected String getMeta(FileHandle f, String area, String k) {
		Assert.assertParameterNotNull(f, "file cannot be null");
		f = fileHandleDao.merge(f);
		String v = null;
		List<FileHandleMeta> fileMetaList = f.getFileHandleMeta();
		if (fileMetaList == null)
			fileMetaList = new ArrayList<FileHandleMeta>();
		fileMetaList.size();
		try{
			v = (String) MetaHelper.getMetaValue(area, k, fileMetaList);
		} catch(MetadataException e) {
			// value not found
		}
		return v;
	}
	
	protected String getMeta(FileGroup f, String area, String k) {
		Assert.assertParameterNotNull(f, "file group cannot be null");
		f = fileGroupDao.merge(f);
		String v = null;
		List<FileGroupMeta> fileGroupMetaList = f.getFileGroupMeta();
		if (fileGroupMetaList == null)
			fileGroupMetaList = new ArrayList<FileGroupMeta>();
		fileGroupMetaList.size();
		try{
			v = (String) MetaHelper.getMetaValue(area, k, fileGroupMetaList);
		} catch(MetadataException e) {
			// value not found
		}
		return v;
	}

	protected void setMeta(FileHandle file, String area, String metaKey, String metaValue) throws MetadataException{
		Assert.assertParameterNotNull(file, "file cannot be null");
		Assert.assertParameterNotNull(metaKey, "metaKey cannot be null");
		Assert.assertParameterNotNull(metaValue, "metaValue cannot be null");
		file = fileHandleDao.merge(file);
		FileHandleMeta fileMeta = new FileHandleMeta();
		fileMeta.setFileHandleId(file.getId());
		fileMeta.setK(area + "." + metaKey);
		fileMeta.setV(metaValue);
		fileMetaDao.setMeta(fileMeta);
	}
	
	protected void setMeta(FileGroup fileGroup, String area, String metaKey, String metaValue) throws MetadataException{
		Assert.assertParameterNotNull(fileGroup, "file cannot be null");
		Assert.assertParameterNotNull(metaKey, "metaKey cannot be null");
		Assert.assertParameterNotNull(metaValue, "metaValue cannot be null");
		fileGroup = fileGroupDao.merge(fileGroup);
		FileGroupMeta fileMeta = new FileGroupMeta();
		fileMeta.setFileGroupId(fileGroup.getId());
		fileMeta.setK(area + "." + metaKey);
		fileMeta.setV(metaValue);
		fileGroupMetaDao.setMeta(fileMeta);
	}
	
}