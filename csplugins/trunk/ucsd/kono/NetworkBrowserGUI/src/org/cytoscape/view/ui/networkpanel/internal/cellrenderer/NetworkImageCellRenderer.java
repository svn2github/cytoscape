package org.cytoscape.view.ui.networkpanel.internal.cellrenderer;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class NetworkImageCellRenderer extends JLabel implements TableCellRenderer {

	
	public NetworkImageCellRenderer() {
		
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		try{
		if(value == null || (row == 0 && column == 1)) {
			this.setIcon(null);
			return this;
		}
		
		if(value instanceof Icon) {
			this.setIcon((Icon) value);
		}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}

}
