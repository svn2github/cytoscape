package org.cytoscape.io.write;

import java.io.IOException;

import org.cytoscape.io.FileIOFactory;
import org.cytoscape.model.CyTable;

/**
 * Returns a Task that will write
 */
public interface CyTableWriterFactory extends CyWriterFactory {

	void setTable(CyTable table);
}
