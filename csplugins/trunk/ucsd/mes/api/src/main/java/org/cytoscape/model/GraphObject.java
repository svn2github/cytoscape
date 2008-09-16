
package org.cytoscape.model;

public interface GraphObject extends Identifiable { 
	public CyRow getCyRow(String namespace);
	public CyRow attrs();
}
