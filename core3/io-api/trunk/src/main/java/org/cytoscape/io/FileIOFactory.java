package org.cytoscape.io;


/**
 * An interface extended by various reader and writer factories.
 * It provides basic information about the types of supported files.
 */
public interface FileIOFactory {
	/**
	 * Return the file filter associated with the IOFactory
	 */
	public CyFileFilter getCyFileFilter();

}
