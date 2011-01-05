package org.cytoscape.browser.internal;


import java.awt.Component;
import java.awt.event.MouseEvent;

import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	private final static int CLICK_COUNT_TO_START = 2;
	
	// This is the component that will handle the editing of the cell value
	private final JComponent component = new JTextField();

	// This method is called when a cell value is edited by the user.
	public Component getTableCellEditorComponent(final JTable table, final Object value,
						     final boolean isSelected, final int rowIndex,
						     final int vColIndex)
	{
		// 'value' is value contained in the cell located at (rowIndex, vColIndex)
		if (isSelected) {
				// cell (and perhaps other cells) are selected
		}

		// Configure the component with the specified value
		final String text = (value != null) ? ((ValidatedObjectAndEditString)value).getEditString() : "";
		((JTextField)component).setText(text);

		// Return the configured component
		return component;
	}

	// This method is called when editing is completed.
	// It must return the new value to be stored in the cell.
	public Object getCellEditorValue() {
		return ((JTextField)component).getText();
	}

	public int getClickCountToStart() {
		return CLICK_COUNT_TO_START;
	}

	public boolean isCellEditable(EventObject e) {
		return !(e instanceof MouseEvent)
		       || (((MouseEvent) e).getClickCount() >= CLICK_COUNT_TO_START);
	}
}
