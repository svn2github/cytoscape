package cytoscape.data.servers.ui;

import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.KEY_FONT;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.BOOLEAN_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.FLOAT_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.INT_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.LIST_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.STRING_ICON;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import cytoscape.data.CyAttributes;

/**
 * Cell renderer for alias table in Import Dialog.<br>
 * 
 * @author kono
 *
 */
class AliasTableRenderer extends DefaultTableCellRenderer {

	
	
	
	private List<Byte> dataTypes;
	private int primaryKey;

	private final JLabel iconLabel = new JLabel();
	private final JLabel label = new JLabel();

	public AliasTableRenderer(List<Byte> dataTypes, int primaryKey) {
		this.dataTypes = dataTypes;
		this.primaryKey = primaryKey;
		
		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
	}

	public void setDataTypes(List<Byte> dataTypes) {
		this.dataTypes = dataTypes;
	}
	
	public void setPrimaryKey(int newKey) {
		this.primaryKey = newKey;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		setBackground(Color.white);
		
		if (column != 2) {
			label.setText((value == null) ? "" : value.toString());
			label.setFont(table.getFont());
			if((Boolean)table.getValueAt(row, 0) == true && primaryKey != row) {
				label.setForeground(Color.green);
				label.setFont(KEY_FONT.getFont());
			} else if(primaryKey == row) {
				label.setForeground(Color.blue);
				label.setFont(KEY_FONT.getFont());
			} else {
				label.setForeground(Color.black);
			}
			
			
			return label;
		} else {
			if (dataTypes.get(row) == CyAttributes.TYPE_STRING) {
				iconLabel.setIcon(STRING_ICON.getIcon());
				iconLabel.setText("String");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_INTEGER) {
				iconLabel.setIcon(INT_ICON.getIcon());
				iconLabel.setText("Integer");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_FLOATING) {
				iconLabel.setIcon(FLOAT_ICON.getIcon());
				iconLabel.setText("Float");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_BOOLEAN) {
				iconLabel.setIcon(BOOLEAN_ICON.getIcon());
				iconLabel.setText("Boolean");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_SIMPLE_LIST) {
				iconLabel.setIcon(LIST_ICON.getIcon());
				iconLabel.setText("List");
			}

			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
			return iconLabel;
		}
	}
}

