package cytoscape.plugin.cheminfo;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class TextAreaRenderer extends JTextArea implements TableCellRenderer {
	private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
	/** map from table to map of rows to map of column heights */
	private final Map cellSizes = new HashMap();

	public TextAreaRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public Component getTableCellRendererComponent(
			//
			JTable table, Object obj, boolean isSelected, boolean hasFocus,
			int row, int column) {
		// set the colours, etc. using the standard for that platform
		adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus,
				row, column);
		setForeground(adaptee.getForeground());
		setBackground(adaptee.getBackground());
		setBorder(adaptee.getBorder());
		setFont(adaptee.getFont());
		setText(adaptee.getText());

		return this;
	}

}
