package org.cytoscape.task;

import org.cytoscape.model.CyColumn;

public abstract class SimpleTableCellTaskFactory implements TableCellTaskFactory {
	@Override
	public boolean isReady(CyColumn column, Object primaryKeyValue) {
		return true;
	}
}
