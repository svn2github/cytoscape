package org.cytoscape.browser.internal;


import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.service.util.CyServiceRegistrar;


public class BrowserTable extends JTable {
	private static final TableCellRenderer cellRenderer = new MyTableCellRenderer();

	public BrowserTable() {
		super();
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		return cellRenderer;
	}
}

