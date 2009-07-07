package org.cytoscape.view.ui.networkpanel.internal.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.cytoscape.view.ui.networkpanel.CyTableCellRenderer;

public class ImageCellRenderer implements CyTableCellRenderer<Image> {

	public Class<Image> getCompatibleType() {
		return Image.class;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof Image == false)
			return null;
		
		final Image image = (Image) value;
		final JLabel iconCell = new JLabel();
		final Icon icon = new ImageIcon(image);
		
		iconCell.setSize(icon.getIconWidth(), icon.getIconHeight());
		iconCell.setIcon(icon);

		if(isSelected) {
			iconCell.setBackground(Color.red);
		}
		
		return iconCell;
	}

}
