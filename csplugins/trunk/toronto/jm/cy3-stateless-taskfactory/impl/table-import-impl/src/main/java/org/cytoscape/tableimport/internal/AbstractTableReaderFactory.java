package org.cytoscape.tableimport.internal;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskContextImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyTableFactory;

// Copy from io-impl
public abstract class AbstractTableReaderFactory implements InputStreamTaskFactory<InputStreamTaskContext> {
	private final CyFileFilter filter;

	protected final CyTableFactory tableFactory;

	public AbstractTableReaderFactory(CyFileFilter filter, CyTableFactory tableFactory) {
		if (filter == null)
			throw new NullPointerException("filter is null");
		this.filter = filter;

		if (tableFactory == null)
			throw new NullPointerException("tableFactory is null");
		this.tableFactory = tableFactory;
	}
	
	@Override
	public InputStreamTaskContext createTaskContext() {
		return new InputStreamTaskContextImpl();
	}

	public CyFileFilter getFileFilter() {
		return filter;
	}
}

