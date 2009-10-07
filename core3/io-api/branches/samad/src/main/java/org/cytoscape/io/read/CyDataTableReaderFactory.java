package org.cytoscape.io.read;

import java.io.InputStream;
import org.cytoscape.io.CyFileFilterable;
import org.cytoscape.work.Task;
import org.cytoscape.model.CyDataTable;

public interface CyDataTableReaderFactory extends CyFileFilterable {

	public Task getReader(InputStream input, CyDataTable dataTable);

}
