package org.cytoscape.filter;

import org.cytoscape.model.CyNetwork;

/**
 * A factory for creating <code>CyFilterResult</code>s for specific
 * <code>CyNetwork</code>s.
 */
public interface CyFilterResultFactory {
	/**
	 * Creates a <code>CyFilterResult</code> for tracking filtered nodes and
	 * edges from the given network.  
	 * @param network
	 * @return
	 */
	CyFilterResult getResult(CyNetwork network);
}
