package org.cytoscape.browser.internal;


import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class MyTableCellRenderer extends JLabel implements TableCellRenderer {
	// This method is called each time a cell in a column
	// using this renderer needs to be rendered.
	@Override
	public Component getTableCellRendererComponent(
		final JTable table, final Object value, final boolean isSelected,
		final boolean hasFocus, final int rowIndex, final int vColIndex)
	{
		if (isSelected) {
			// cell (and perhaps other cells) are selected
			setOpaque(true);
			setBackground(Color.GRAY);
		}

		if (hasFocus) {
			// this cell is the anchor and the table has the focus
		}

		// Configure the component with the specified value
		final String text, tooltipText;
		if (value == null)
			text = tooltipText = "";
		else if (value instanceof ValidatedObjectAndEditString) {
			final ValidatedObjectAndEditString v = (ValidatedObjectAndEditString)value;
			final String errorText = v.getErrorText();
			if (errorText != null)
				text = errorText;
			else
				text = v.getValidatedObject().toString();
			tooltipText = v.getEditString();
		} else
			text = tooltipText = value.toString();

		setText(text);
		setToolTipText(tooltipText);

		// Since the renderer is a component, return itself
		return this;
	}

	//
	// The following methods override the defaults for performance reasons.
	//

	@Override
	public void validate() { }

	@Override
	public void revalidate() { }

	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) { }
}
