package org.cytoscape.io;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public interface CyFileFilter {

	public boolean accept(URI uri, DataCategory category) throws IOException;

	/**
	 * Returns a list of file extensions (xml, xgmml, sif) suitable for for use
	 * in FileChoosers. This information could be provided as metadata to the
	 * CyReader service!
	 */
	public Set<String> getExtensions();

	public Set<String> getContentTypes();

	/**
	 * A short, human readable description of the file extensions suitable for
	 * display in FileChoosers. This information could be provided as metadata
	 * to the CyReader service!
	 */
	public String getDescription();

	public DataCategory getDataCategory();

}
