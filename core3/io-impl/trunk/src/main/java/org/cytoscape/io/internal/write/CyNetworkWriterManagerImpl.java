package org.cytoscape.io.internal.write;

import java.io.File;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.write.CyNetworkWriterFactory;
import org.cytoscape.io.write.CyNetworkWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;

public class CyNetworkWriterManagerImpl extends AbstractWriterManager<CyNetworkWriterFactory> implements CyNetworkWriterManager {
	public CyNetworkWriterManagerImpl(DataCategory category) {
		super(category);
	}

	@Override
	public CyWriter getWriter(CyNetwork network, CyFileFilter filter, File file) {
		CyNetworkWriterFactory factory = getMatchingFactory(filter, file);
		if (factory == null) {
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		}
		factory.setNetwork(network);
		return factory.getWriter();
	}
}