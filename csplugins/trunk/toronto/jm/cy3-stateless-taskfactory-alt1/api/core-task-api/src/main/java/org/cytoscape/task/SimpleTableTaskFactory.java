package org.cytoscape.task;

import org.cytoscape.model.CyTable;

public abstract class SimpleTableTaskFactory implements TableTaskFactory {
	@Override
	public boolean isReady(CyTable table) {
		return true;
	}
}
