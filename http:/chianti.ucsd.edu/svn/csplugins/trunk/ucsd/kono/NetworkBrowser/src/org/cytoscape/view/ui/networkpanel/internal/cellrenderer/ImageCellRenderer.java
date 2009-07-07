package org.cytoscape.view.ui.networkpanel.internal.cellrenderer;

import java.awt.Component;
import java.awt.Image;

import javax.swing.JTable;

import org.cytoscape.view.ui.networkpanel.CyTableCellRenderer;

public class ImageCellRenderer implements CyTableCellRenderer<Image> {

	public Class<Image> getCompatibleType() {
		return Image.class;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return null;
	}

}
