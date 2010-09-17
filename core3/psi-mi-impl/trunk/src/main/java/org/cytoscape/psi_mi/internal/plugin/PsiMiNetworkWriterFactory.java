package org.cytoscape.psi_mi.internal.plugin;

import java.io.File;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;

public class PsiMiNetworkWriterFactory implements CyNetworkWriterFactory {

	private final SchemaVersion version;
	private final CyFileFilter filter;

	private File file;
	private CyNetwork network;
	
	public PsiMiNetworkWriterFactory(SchemaVersion version, CyFileFilter filter) {
		this.version = version;
		this.filter = filter;
	}
	
	@Override
	public void setOutputFile(File file) {
		this.file = file;
	}

	@Override
	public CyWriter getWriter() {
		return new PsiMiWriter(file, network, version);
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
