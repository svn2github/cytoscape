
package org.cytoscape.model.builder;

public interface CyEdgeBuilder {

	long getSUID();

	CyRowBuilder getCyRowBuilder();

	CyNodeBuilder getSource();

	CyNodeBuilder getTarget();

	boolean isDirected(); 
}

