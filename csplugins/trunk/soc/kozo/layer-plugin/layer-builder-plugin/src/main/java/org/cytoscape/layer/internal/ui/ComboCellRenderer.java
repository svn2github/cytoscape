package org.cytoscape.layer.internal.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.TableCellRenderer;

public class ComboCellRenderer extends JComboBox implements TableCellRenderer {
	private DefaultComboBoxModel model;

	public ComboCellRenderer(JComboBox base) {
		super();
		this.initialize(base.getModel());
	}

	public void initialize(ListModel listModel) {
		Object[] items = new Object[listModel.getSize()];
		for (int i = 0; i < items.length; i++) {
			items[i] = listModel.getElementAt(i);
		}
		this.model = new DefaultComboBoxModel(items);
		this.setModel(this.model);
		this.setBorder(BorderFactory.createEmptyBorder());
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		removeAllItems();
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}

		int index = this.model.getIndexOf(value);
		if (index != -1) {
			this.setSelectedIndex(index);
		} else {
			this.setSelectedIndex(0);
		}
		return this;
	}
}
