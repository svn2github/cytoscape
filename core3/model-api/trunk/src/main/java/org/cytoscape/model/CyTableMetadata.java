package org.cytoscape.model;

import java.util.Set;

public interface CyTableMetadata {
	Class<?> getType();
	CyTable getCyTable();
	Set<CyNetwork> getCyNetworks();
	String getNamespace();
}
