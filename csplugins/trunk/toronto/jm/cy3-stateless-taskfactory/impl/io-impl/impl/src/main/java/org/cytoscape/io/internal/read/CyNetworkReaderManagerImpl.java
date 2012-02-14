package org.cytoscape.io.internal.read;

import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;

public class CyNetworkReaderManagerImpl extends GenericReaderManager<InputStreamTaskFactory<InputStreamTaskContext>, CyNetworkReader, InputStreamTaskContext> implements
		CyNetworkReaderManager {

	public CyNetworkReaderManagerImpl(final StreamUtil streamUtil) {
		super(DataCategory.NETWORK, streamUtil);
	}
}
