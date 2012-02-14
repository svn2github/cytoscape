package org.cytoscape.io.internal.write.sif;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.write.AbstractCyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyNetworkViewWriterContext;
import org.cytoscape.io.write.CyNetworkViewWriterContextImpl;
import org.cytoscape.io.write.CyWriter;

public class SifNetworkWriterFactory extends AbstractCyNetworkViewWriterFactory {
	
	public SifNetworkWriterFactory(CyFileFilter filter) {
		super(filter);
	}
	
	@Override
	public CyNetworkViewWriterContext createTaskContext() {
		return new CyNetworkViewWriterContextImpl();
	}
	
	@Override
	public CyWriter createWriterTask(CyNetworkViewWriterContext context) {
		return new SifWriter(context.getOutputStream(), context.getNetworkView());
	}
}
