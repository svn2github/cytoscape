package org.cytoscape.io.read;

import java.io.InputStream;
import org.cytoscape.io.CyFileFilterable;
import org.cytoscape.work.Task;
import org.cytoscape.view.model.CyNetworkView;

public interface CyNetworkViewReaderFactory extends CyFileFilterable {

	public Task getReader(InputStream input, CyNetworkView networkView);

}
