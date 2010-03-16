package cytoscape.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class IconTableCellRenderer implements TableCellRenderer {

	private static final Font ICON_FONT = new Font("SansSerif", Font.BOLD, 20);
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		final JLabel label = new JLabel();
		label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		
		if (value != null && value instanceof Icon) {
			label.setIcon((Icon) value);
		} else {
			
			label.setFont(ICON_FONT);
			label.setText("?");
		}

		return label;
	}

}