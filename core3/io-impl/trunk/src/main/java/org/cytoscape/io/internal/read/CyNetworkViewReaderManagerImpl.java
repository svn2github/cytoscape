package org.cytoscape.io.internal.read;

import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.io.read.CyNetworkViewReaderFactory;
import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.DataCategory;

public class CyNetworkViewReaderManagerImpl 
	extends GenericReaderManager<CyNetworkViewReaderFactory,CyNetworkViewReader> 
	implements CyNetworkViewReaderManager {

	public CyNetworkViewReaderManagerImpl() {
		super(DataCategory.NETWORK);
	}
}
