
package org.cytoscape.model.network;

import org.cytoscape.attributes.CyAttributes;

public interface GraphObject extends Identifiable { 
	public CyAttributes getCyAttributes(String namespace);
}
