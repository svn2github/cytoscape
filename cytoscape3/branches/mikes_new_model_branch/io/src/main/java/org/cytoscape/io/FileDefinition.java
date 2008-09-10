
package org.cytoscape.io;

/**
 * An interface extended by {@link org.cytoscape.io.read.CyReader}
 * and {@link org.cytoscape.io.read.CyWriter} the provides basic
 * information about the types of files supported.
 */
public interface FileDefinition {

	/**
	 * Returns a list of file extensions (xml, xgmml, sif) suitable for
	 * for use in FileChoosers.
	 * This information could be provided as metadata to the CyReader service!
	 */
	public String[] getExtensions();
	
	/**
	 * A short, human readable description of the file extensions suitable
	 * for display in FileChoosers.
	 * This information could be provided as metadata to the CyReader service!
	 */
	public String getExtensionDescription();  

}
