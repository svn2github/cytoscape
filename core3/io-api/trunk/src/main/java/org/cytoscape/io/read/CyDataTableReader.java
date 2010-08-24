package org.cytoscape.io.read;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.work.Task;

public interface CyDataTableReader extends Task{

	public CyDataTable[] getCyDataTables();
}
