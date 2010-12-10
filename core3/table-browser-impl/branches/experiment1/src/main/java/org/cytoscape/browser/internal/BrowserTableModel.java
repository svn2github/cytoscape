package org.cytoscape.browser.internal;


import javax.swing.table.AbstractTableModel;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;


public class BrowserTableModel extends AbstractTableModel
	implements ColumnCreatedListener, ColumnDeletedListener
{
	private final CyTable table;

	public BrowserTableModel(final CyTable table) {
		this.table = table;
	}

	public int getRowCount() {
		return 10;
	}

	public int getColumnCount() {
		return 10;
	}

	public Object getValueAt(int row, int column) {
		return row + "," + column;
	}

	public void handleEvent(final ColumnCreatedEvent e) {
	}

	public void handleEvent(final ColumnDeletedEvent e) {
	}
}
