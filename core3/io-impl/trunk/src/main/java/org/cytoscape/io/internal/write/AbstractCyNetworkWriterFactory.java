package org.cytoscape.io.internal.write;

import java.io.File;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkWriterFactory;
import org.cytoscape.model.CyNetwork;

public abstract class AbstractCyNetworkWriterFactory implements CyNetworkWriterFactory {

	private final CyFileFilter filter;
	
	protected File outputFile;
	protected CyNetwork network;

	public AbstractCyNetworkWriterFactory(CyFileFilter filter) {
		this.filter = filter;
	}
	
	@Override
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public CyFileFilter getCyFileFilter() {
		return filter;
	}

	@Override
	public void setNetwork(CyNetwork network) {
		this.network = network;
	}

}
