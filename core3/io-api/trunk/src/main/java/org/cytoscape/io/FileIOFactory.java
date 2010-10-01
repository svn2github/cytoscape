package org.cytoscape.io;


/**
 * An interface extended by various reader and writer factories.
 * It the provides basic information about the types of supported files.
 */
public interface FileIOFactory {

	public CyFileFilter getCyFileFilter();

}
