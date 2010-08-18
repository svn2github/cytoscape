package org.cytoscape.io.read;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.work.Task;

public interface CyDataTableProducer extends Task{

	public CyDataTable[] getCyDataTables();
}
