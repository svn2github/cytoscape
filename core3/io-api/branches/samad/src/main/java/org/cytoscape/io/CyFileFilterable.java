package org.cytoscape.io;

/**
 * An interface extended by {@link org.cytoscape.io.read.CyReader} and
 * {@link org.cytoscape.io.read.CyWriter} the provides basic information about
 * the types of files supported.
 */
public interface CyFileFilterable {

	public CyFileFilter getCyFileFilter();

}
