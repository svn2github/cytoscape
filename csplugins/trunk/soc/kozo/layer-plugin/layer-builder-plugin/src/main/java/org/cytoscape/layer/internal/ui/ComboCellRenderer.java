package org.cytoscape.layer.internal.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class ComboCellRenderer extends JComboBox implements TableCellRenderer {
	private final JTextField editor;

	public ComboCellRenderer() {
		super();
		setEditable(true);
		setBorder(BorderFactory.createEmptyBorder());

		editor = (JTextField) getEditor().getEditorComponent();
		editor.setBorder(BorderFactory.createEmptyBorder());
		editor.setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		removeAllItems();
		if (isSelected) {
			editor.setForeground(table.getSelectionForeground());
			editor.setBackground(table.getSelectionBackground());
		} else {
			editor.setForeground(table.getForeground());
			editor.setBackground(table.getBackground());
		}
		addItem((value == null) ? "" : value.toString());
		return this;
	}

}
