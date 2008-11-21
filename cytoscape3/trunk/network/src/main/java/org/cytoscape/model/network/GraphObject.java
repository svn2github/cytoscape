
package org.cytoscape.model.network;

import org.cytoscape.model.CyRow;

public interface GraphObject extends Identifiable { 
	public CyAttributes getCyAttributes(String namespace);
}
