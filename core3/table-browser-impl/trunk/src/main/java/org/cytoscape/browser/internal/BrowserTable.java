package org.cytoscape.browser.internal;


import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.service.util.CyServiceRegistrar;


public class BrowserTable extends JTable {
	private static final TableCellRenderer cellRenderer = new BrowserTableCellRenderer();

	public BrowserTable() {
		setCellSelectionEnabled(true);
		setDefaultEditor(Object.class, new MyTableCellEditor());
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return cellRenderer;
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return column != 0;
	}
}

