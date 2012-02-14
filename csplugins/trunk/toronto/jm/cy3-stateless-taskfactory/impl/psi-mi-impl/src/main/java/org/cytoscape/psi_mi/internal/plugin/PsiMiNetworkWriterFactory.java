package org.cytoscape.psi_mi.internal.plugin;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterContext;
import org.cytoscape.io.write.CyNetworkViewWriterContextImpl;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.work.TaskIterator;

public class PsiMiNetworkWriterFactory implements CyNetworkViewWriterFactory<CyNetworkViewWriterContext> {

	private final SchemaVersion version;
	private final CyFileFilter filter;

	public PsiMiNetworkWriterFactory(SchemaVersion version, CyFileFilter filter) {
		this.version = version;
		this.filter = filter;
	}

	@Override
	public CyNetworkViewWriterContext createTaskContext() {
		return new CyNetworkViewWriterContextImpl();
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetworkViewWriterContext context) {
		return new TaskIterator(createWriterTask(context));
	}
	
	@Override
	public CyWriter createWriterTask(CyNetworkViewWriterContext context) {
		return new PsiMiWriter(context.getOutputStream(), context.getNetwork(), version);
	}

	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}
}
