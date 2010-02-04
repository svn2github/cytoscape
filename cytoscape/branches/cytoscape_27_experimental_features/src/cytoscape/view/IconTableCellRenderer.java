package cytoscape.view;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class IconTableCellRenderer implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		final JLabel label;
		if (value != null && value instanceof Icon) {
			label = new JLabel();
			label.setIcon((Icon) value);
		} else
			label = new JLabel("?");

		return label;
	}

}