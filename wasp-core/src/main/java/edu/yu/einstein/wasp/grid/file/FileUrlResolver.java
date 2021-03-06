/**
 * 
 */
package edu.yu.einstein.wasp.grid.file;

import java.net.URL;

import edu.yu.einstein.wasp.exception.LoginNameException;
import edu.yu.einstein.wasp.grid.GridUnresolvableHostException;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileHandle;

/**
 * 
 * A FileUrlResolver returns a URL for a WASP file object.
 * 
 * @author calder
 *
 */
public interface FileUrlResolver {
	
	/**
	 * Get a URL to a file.  The implementation of this can grant access to files on the same
	 * or a remote host.  
	 * 
	 * @param file
	 * @return
	 * @throws SecurityException
	 * @throws LoginNameException
	 */
	public URL getURL(FileHandle file) throws GridUnresolvableHostException;
	
	/**
	 * Get a URL to a file group.  If the group consists of a single file, the URL should represent
	 * that single file or an on-the-fly compressed representation of that file.  Groups of 2 or more files
	 * need to be concatenated and or compressed to be represented by a single file.
	 * 
	 * @param group
	 * @return
	 * @throws GridUnresolvableHostException
	 */
	public URL getURL(FileGroup group) throws GridUnresolvableHostException;

}
