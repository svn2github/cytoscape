package org.cytoscape.task;

public abstract class AbstractTableColumnTaskFactory implements TableColumnTaskFactory<TableColumnTaskContext> {
	@Override
	public TableColumnTaskContext createTaskContext() {
		return new TableColumnTaskContextImpl();
	}
}
