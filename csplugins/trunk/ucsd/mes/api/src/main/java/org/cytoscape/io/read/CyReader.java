
package org.cytoscape.io.read;

import java.net.URI;

/**
 * The basic input interface that specifies what is to be read and when it is
 * to be read.  This interface should be extended by other interfaces to provide 
 * access to the data that gets read.  One class can then implement multiple
 * CyReader interfaces to support reading files that contain multiple types
 * of data (like networks that contain both attribute and view model information).
 */
public interface CyReader {

	/**
	 * Calling this method will initiate reading of the input specified in the
	 * {@link CyReader#setInput(URI u)}. This method will return once the data
	 * has been read and will(?) throw an exception otherwise.
	 * <p>
	 * This should probably throw an {@link java.io.IOException}
	 */
	public void read();


	/**
	 * This method sets the input that is to be read and must be called prior 
	 * to the  {@link CyReader#read()} method. 
	 * <p>
	 * This should probably throw a exceptions if the URI is invalid.
	 * <p>
	 * Is URI sufficient or are there other input types we should handle?
	 * @param u A URI specifying a file or data source to be read.
	 */
	public void setInput(URI u);
}
