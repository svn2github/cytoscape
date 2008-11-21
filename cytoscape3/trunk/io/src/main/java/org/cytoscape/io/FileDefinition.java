
package org.cytoscape.io;

import java.io.File;
import java.net.URL;

/**
 * An interface extended by {@link org.cytoscape.io.read.CyReader}
 * and {@link org.cytoscape.io.read.CyWriter} the provides basic
 * information about the types of files supported.
 */
public interface FileDefinition {

	enum Category {
		NETWORK,
		TABLE,
		IMAGE,
		PROPERTIES,
		SESSION,
	}

	/**
	 * Returns a list of file extensions (xml, xgmml, sif) suitable for
	 * for use in FileChoosers.
	 * This information could be provided as metadata to the CyReader service!
	 */
	public String[] getExtensions();
	public String[] getContentTypes();
	
	/**
	 * A short, human readable description of the file extensions suitable
	 * for display in FileChoosers.
	 * This information could be provided as metadata to the CyReader service!
	 */
	public String getExtensionDescription();  

	/**
	 * Indicates whether the specified file can be read by this class.
	 */
//	public boolean accept(File f);
//	public boolean accept(String f);
//	public boolean accept(URL u, String contentType);

	/**
	 * The category that this definition supports.
	 */
//	public Category getCategory();
}



