package edu.yu.einstein.wasp.plugin;

import java.util.Map;

import edu.yu.einstein.wasp.Hyperlink;


public interface FileTypeViewProviding {
	
	public Map<String, Hyperlink> getFileDetails(Integer fileGroupId);

}
