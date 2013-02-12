package edu.yu.einstein.wasp.interfaces;

import edu.yu.einstein.wasp.Hyperlink;

public interface WebInterfacing {
	
	/**
	 * returns the link text and relative URL for the location of a webpage which displays a plugin's description 
	 * @return
	 */
	public Hyperlink getDescriptionPageHyperlink();

}