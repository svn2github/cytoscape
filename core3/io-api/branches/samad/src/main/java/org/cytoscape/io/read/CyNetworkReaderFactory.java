package org.cytoscape.io.read;

import java.io.IOException;
import java.io.InputStream;

import org.cytoscape.io.CyFileFilterable;
import org.cytoscape.work.Task;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTable;

public interface CyNetworkReaderFactory extends CyFileFilterable {

	public Task getReader(InputStream input, CyNetwork network, CyDataTable dataTable) throws IOException;

}
