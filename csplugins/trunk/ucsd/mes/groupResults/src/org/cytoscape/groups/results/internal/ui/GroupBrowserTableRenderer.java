package org.cytoscape.groups.results.internal.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import cytoscape.groups.CyGroup;

public class GroupBrowserTableRenderer extends JTextArea implements
		TableCellRenderer {

	private static final long serialVersionUID = 8849136401512660949L;
	int minHeight;

	/**
	 * Constructor
	 * 
	 * @param minHeight
	 *            The minimum height of the row, either the size of the graph
	 *            picture or zero
	 */
	public GroupBrowserTableRenderer(int minHeight) {
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		this.setEditable(false);
		this.setFont(new Font(this.getFont().getFontName(), Font.PLAIN, 11));
		this.minHeight = minHeight;
	}

	/**
	 * Used to render a table cell. Handles selection color and cell heigh and
	 * width. Note: Be careful changing this code as there could easily be
	 * infinite loops created when calculating preferred cell size as the user
	 * changes the dialog box size.
	 * 
	 * @param table
	 *            Parent table of cell
	 * @param value
	 *            Value of cell
	 * @param isSelected
	 *            True if cell is selected
	 * @param hasFocus
	 *            True if cell has focus
	 * @param row
	 *            The row of this cell
	 * @param column
	 *            The column of this cell
	 * @return The cell to render by the calling code
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		final String detail = MCODEUtil.getClusterDetails((CyGroup) value);
		this.setText(detail);

		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
			this.setForeground(table.getSelectionForeground());
		} else {
			this.setBackground(table.getBackground());
			this.setForeground(table.getForeground());
		}
		// row height calculations
		int currentRowHeight = table.getRowHeight(row);
		int rowMargin = table.getRowMargin();
		this.setSize(table.getColumnModel().getColumn(column).getWidth(),
				currentRowHeight - (2 * rowMargin));
		int textAreaPreferredHeight = (int) this.getPreferredSize().getHeight();
		// JTextArea can grow and shrink here
		if (currentRowHeight != Math.max(textAreaPreferredHeight
				+ (2 * rowMargin), minHeight + (2 * rowMargin))) {
			table.setRowHeight(row, Math.max(textAreaPreferredHeight
					+ (2 * rowMargin), minHeight + (2 * rowMargin)));
		}
		return this;
	}
}
