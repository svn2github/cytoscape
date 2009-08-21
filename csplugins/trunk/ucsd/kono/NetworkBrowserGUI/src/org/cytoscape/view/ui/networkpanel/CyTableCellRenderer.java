package org.cytoscape.view.ui.networkpanel;

import javax.swing.table.TableCellRenderer;

public interface CyTableCellRenderer<T> extends TableCellRenderer {
	
	public Class<T> getCompatibleType();

}
