package org.cytoscape.browser.internal;


import java.awt.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;


public class TableChooser extends JComboBox {
	TableChooser(final CyTableManager tableManager) {
		super(new MyComboBoxModel(tableManager));
		setRenderer(new MyCellRenderer());
	}
}


class MyComboBoxModel extends DefaultComboBoxModel {
	private final CyTableManager tableManager;
	private List<CyTable> tables;
	private Set<CyTable> oldSet;

	MyComboBoxModel(final CyTableManager tableManager) {
		this.tableManager = tableManager;
		oldSet = tableManager.getAllTables(/* includePrivate = */ false);
		tables = new ArrayList<CyTable>(oldSet.size());
		for (final CyTable table : oldSet)
			tables.add(table);
	}

	public int getSize() {
		final Set<CyTable> tableSet = tableManager.getAllTables(/* includePrivate = */ false);
		if (!tableSet.equals(oldSet)) {
			oldSet = tableSet;
			fireContentsChanged(this, 0, tableSet.size() - 1);
			tables = new ArrayList<CyTable>(tableSet.size());
			for (final CyTable table : tableSet)
				tables.add(table);
		}

		return tables.size();
	}

	public Object getElementAt(int index) {
		return tables.get(index);
	}
}


class MyCellRenderer extends JLabel implements ListCellRenderer {
	// This is the only method defined by ListCellRenderer.
	// We just reconfigure the JLabel each time we're called.

	public Component getListCellRendererComponent(final JList list,              // the list
						      final Object value,            // value to display
						      final int index,               // cell index
						      final boolean isSelected,      // is the cell selected
						      final boolean cellHasFocus)    // does the cell have focus
	{
		final CyTable table = (CyTable)value;
		setText(table == null ? "" : table.getTitle());

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);

		return this;
	}
}