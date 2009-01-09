package org.cytoscape.io.import;

import java.io.File;

/**
 * Defines a CyNetwork file type.
 * The file extensions and other hard coded information about the
 * type of file is captured in the meta data of the implementating
 * class. This meta data includes:
 * <p>
 * file.extensions=comma separated list of valid extensions 
 * file.mimetype=comma separated list of valid mimetypes 
 * description=description of the file type omitting the extensions
 * </p>
 */
public interface CyNetworkFileFilter {

	/**
	 * Accept is only needed when we have multiple file filters
	 * of the that have the same extension.  In this case, we need
	 * to open the file and read it to determine it's type. If the
	 * source is a URL, then we'd need to download the file and 
	 * read the local copy rather than reading the stream from the
	 * URL twice.
	 */
	public boolean acceptContents(File f);

	/**
	 * Returns the appropriate CyNetworkReader for the given file type.
	 */
	public CyNetworkReader getReader();

}
