package org.cytoscape.view.ui.networkpanel;

import java.util.Map;

public interface CellRendererFactory {
	
	public <T> CyTableCellRenderer<T> getCellRenderer(Class<T> dataType);
	
	public void addTableCellRenderer(CyTableCellRenderer<?> renderer, Map props);
	public void removeTableCellRenderer(CyTableCellRenderer<?> renderer, Map props);
	
}
