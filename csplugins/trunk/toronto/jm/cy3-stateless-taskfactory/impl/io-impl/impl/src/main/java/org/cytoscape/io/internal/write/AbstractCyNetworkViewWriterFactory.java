package org.cytoscape.io.internal.write;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterContext;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.work.TaskIterator;

public abstract class AbstractCyNetworkViewWriterFactory implements CyNetworkViewWriterFactory<CyNetworkViewWriterContext> {

	private final CyFileFilter filter;
	
	public AbstractCyNetworkViewWriterFactory(CyFileFilter filter) {
		this.filter = filter;
	}
	
	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}

	public TaskIterator createTaskIterator(CyNetworkViewWriterContext context) {
		return new TaskIterator(createWriterTask(context));
	}
	
	/**
	 * Returns a {@link CyWriter} Task suitable for writing to the specified
	 * output stream.
	 * @return A {@link CyWriter} Task suitable for writing to the specified
	 * output stream.
	 */
	public abstract CyWriter createWriterTask(CyNetworkViewWriterContext context);
}
