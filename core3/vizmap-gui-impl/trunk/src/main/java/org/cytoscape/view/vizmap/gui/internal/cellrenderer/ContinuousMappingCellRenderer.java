package org.cytoscape.view.vizmap.gui.internal.cellrenderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;

import org.cytoscape.view.vizmap.gui.internal.editor.mappingeditor.AbstractContinuousMappingEditor;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

/**
 * Cell renderer for Continuous Color mapping
 * 
 */
public class ContinuousMappingCellRenderer extends DefaultCellRenderer {

	private static final long serialVersionUID = -6734053848878359286L;

	private final AbstractContinuousMappingEditor<?, ?> editor;
	
	public ContinuousMappingCellRenderer(final AbstractContinuousMappingEditor<?, ?> editor) {
		if(editor == null)
			throw new NullPointerException("Editor object is null.");
		
		this.editor = editor;
	}
	
	
	@Override public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(value == null || value instanceof ContinuousMapping == false) {
			this.setText("Unkonown Mapping");
			return this;
		}
				
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		
		final int height = table.getRowHeight(row);
		final int width = table.getColumnModel().getColumn(column).getWidth();
		final ImageIcon icon = editor.drawIcon(width, height-2, false);
		this.setIcon(icon);

		return this;
	}

}
