package cytoscape.data.readers;

import cytoscape.CyNetwork;

/**
 * Special interface for Graph readers which reads multiple networks from a file.
 * 
 * TODO: is this a proper naming for this interface?
 * 
 * @author kono
 * @since Cytoscape 2.7.0
 * 
 */
public interface MultiGraphFileReader {

	/**
	 * Returns first network in the result.
	 * 
	 * @return one of the read network.  It is up to the author which network 
	 * will be returned by this method call.
	 * 
	 */
	public CyNetwork getFirstNetwork();
}
