package org.cytoscape.task;

import org.cytoscape.model.CyColumn;

public abstract class AbstractTableCellTaskFactory implements TableCellTaskFactory {
	@Override
	public boolean isReady(CyColumn column, Object primaryKeyValue) {
		return true;
	}
}
