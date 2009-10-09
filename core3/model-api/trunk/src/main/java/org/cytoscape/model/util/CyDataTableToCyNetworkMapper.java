package org.cytoscape.model.util;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;

public interface CyDataTableToCyNetworkMapper
{
	public Task getMapper(CyDataTable source, String sourceColumn, CyNetwork target, String targetColumn, String objectType);
}
