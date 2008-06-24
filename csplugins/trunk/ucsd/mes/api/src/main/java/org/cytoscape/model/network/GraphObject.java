
package org.cytoscape.model.network;

import org.cytoscape.model.attrs.CyAttributes;

public interface GraphObject extends Identifiable { 
	public CyAttributes getCyAttributes(String namespace);
}
