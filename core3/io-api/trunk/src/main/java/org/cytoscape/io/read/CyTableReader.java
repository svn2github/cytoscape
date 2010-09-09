package org.cytoscape.io.read;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.Task;

public interface CyTableReader extends Task{

	public CyTable[] getCyDataTables();
}
