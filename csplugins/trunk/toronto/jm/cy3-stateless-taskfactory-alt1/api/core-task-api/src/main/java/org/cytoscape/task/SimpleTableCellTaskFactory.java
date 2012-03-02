package org.cytoscape.task;

import org.cytoscape.model.CyColumn;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleTableCellTaskFactory implements TableCellTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, CyColumn column, Object primaryKeyValue) {
		return createTaskIterator(column, primaryKeyValue);
	}

	@Override
	public final boolean isReady(Object tunableContext, CyColumn column, Object primaryKeyValue) {
		return isReady(column, primaryKeyValue);
	}
	
	protected boolean isReady(CyColumn column, Object primaryKeyValue) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}
	
	protected abstract TaskIterator createTaskIterator(CyColumn column, Object primaryKeyValue);
}
