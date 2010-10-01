package org.cytoscape.io.internal.write.sif;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.write.AbstractCyNetworkWriterFactory;
import org.cytoscape.io.write.CyWriter;

public class SifNetworkWriterFactory extends AbstractCyNetworkWriterFactory {
	
	public SifNetworkWriterFactory(CyFileFilter filter) {
		super(filter);
	}
	
	@Override
	public CyWriter getWriter() {
		return new SifWriter(outputStream, network);
	}
}
