package org.cytoscape.task;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleTableTaskFactory implements TableTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, CyTable table) {
		return createTaskIterator(table);
	}
	
	@Override
	public final boolean isReady(Object tunableContext, CyTable table) {
		return isReady(table);
	}
	
	protected boolean isReady(CyTable table) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}
	
	protected abstract TaskIterator createTaskIterator(CyTable table);
}
