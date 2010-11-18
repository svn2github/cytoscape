package org.cytoscape.view.vizmap.gui.internal.cellrenderer;

import java.awt.Component;
import java.awt.Paint;

import javax.swing.JTable;

import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

/**
 * Cell renderer for Continuous Color mapping
 * 
 */
public class ColorContinuousMappingCellRenderer extends DefaultCellRenderer {

	private static final long serialVersionUID = -6734053848878359286L;
	
	
	public ColorContinuousMappingCellRenderer() {
		
	}

	@Override public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(value == null || value instanceof ContinuousMapping == false) {
			this.setText("!");
			return this;
		}
		
		//ContinuousMapping<? extends Number, Paint> cm = (ContinuousMapping<? extends Number, Paint>) value;
		
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}

		
		this.setText("Map type = " + value.toString());

		return this;
	}

}
