package cytoscape.data.servers.ui;

import static cytoscape.data.readers.TextFileDelimiters.PIPE;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.ALIAS_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.HEADER_BACKGROUND_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.HEADER_UNSELECTED_BACKGROUND_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.NOT_SELECTED_COL_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.ONTOLOGY_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.PRIMARY_KEY_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.SELECTED_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.SPECIES_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.UNSELECTED_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.SELECTED_COL_FONT;
import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.SELECTED_FONT;
import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.UNSELECTED_FONT;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.BOOLEAN_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.FLOAT_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.INTEGER_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.LIST_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.STRING_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.*;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import cytoscape.data.CyAttributes;

/**
 * Cell and table header renderer for preview table.
 * 
 * @author kono
 * 
 */
public class AttributePreviewTableCellRenderer extends DefaultTableCellRenderer {

	public static final int PARAMETER_NOT_EXIST = -1;
	private final static String DEF_LIST_DELIMITER = PIPE.toString();

	private int keyInFile;
	private List<Integer> aliases;
	private int ontologyColumn;
	private int species;
	private boolean[] importFlag;
	private String listDelimiter;

	/*
	 * For network import
	 */
	private int source;
	private int target;
	private int interaction;
	
	
	/*
	 * Constructors.<br>
	 * 
	 * Primary Key is required.
	 */

	public AttributePreviewTableCellRenderer(int primaryKey,
			List<Integer> aliases, final String listDelimiter) {
		this(primaryKey, aliases, PARAMETER_NOT_EXIST, PARAMETER_NOT_EXIST,
				null, listDelimiter);
	}

	public AttributePreviewTableCellRenderer(int primaryKey,
			List<Integer> aliases, int ontologyColumn) {
		this(primaryKey, aliases, ontologyColumn, PARAMETER_NOT_EXIST, null,
				DEF_LIST_DELIMITER);
	}

	public AttributePreviewTableCellRenderer(int primaryKey,
			List<Integer> aliases, int ontologyColumn, int species) {
		this(primaryKey, aliases, ontologyColumn, species, null,
				DEF_LIST_DELIMITER);
	}

	public AttributePreviewTableCellRenderer(int primaryKey,
			List<Integer> aliases, int ontologyColumn, int species,
			boolean[] importFlag, final String listDelimiter) {
		super();
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		this.source = PARAMETER_NOT_EXIST;
		this.target = PARAMETER_NOT_EXIST;
		this.interaction = PARAMETER_NOT_EXIST;
		
		this.keyInFile = primaryKey;
		this.ontologyColumn = ontologyColumn;

		if (aliases == null) {
			this.aliases = new ArrayList<Integer>();
		} else {
			this.aliases = aliases;
		}

		this.species = species;
		if (importFlag != null) {
			this.importFlag = importFlag;
		}

		if (listDelimiter == null) {
			this.listDelimiter = DEF_LIST_DELIMITER;
		} else {
			this.listDelimiter = listDelimiter;
		}
	}

	public void setImportFlag(int index, boolean flag) {
		if (importFlag != null && importFlag.length > index) {
			importFlag[index] = flag;
		}
	}

	public boolean getImportFlag(int index) {
		if (importFlag != null && importFlag.length > index) {
			return importFlag[index];
		}
		return false;
	}

	public void setAliasFlag(Integer i, boolean flag) {
		if (aliases.contains(i) && flag == false) {
			aliases.remove(i);
		} else if (!aliases.contains(i) && flag == true) {
			aliases.add(i);
		}
	}
	
	public void setSourceIndex(int idx) {
		source = idx;
	}
	
	public void setInteractionIndex(int idx) {
		interaction = idx;
	}
	
	public void setTargetIndex(int idx) {
		target = idx;
	}
	
	
	
	

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

		if (importFlag == null || table.getModel().getColumnCount() != importFlag.length) {
			importFlag = new boolean[table.getColumnCount()];
			for (int i = 0; i < importFlag.length; i++) {
				importFlag[i] = true;
			}
		}
		

		if (column == keyInFile) {
			setForeground(PRIMARY_KEY_COLOR.getColor());
			// setFont(selectedFont);

			// super.setBackground(Color.RED);
		} else if (column == ontologyColumn) {
			setForeground(ONTOLOGY_COLOR.getColor());
			// setFont(selectedFont);
		} else if (aliases.contains(column)) {
			setForeground(ALIAS_COLOR.getColor());
			// setFont(selectedFont);
		} else if (column == species) {
			setForeground(SPECIES_COLOR.getColor());
			// setFont(selectedFont);
		} else if (column == source) {
			setForeground(SOURCE_COLOR.getColor());
			importFlag[column] = true;
		} else if (column == target) {
			setForeground(TARGET_COLOR.getColor());
			importFlag[column] = true;
		} else if( column == interaction) {
			setForeground(INTERACTION_COLOR.getColor());
			importFlag[column] = true;
		} else if(column != source && column != target && column != interaction && source != PARAMETER_NOT_EXIST ) {
			setForeground(EDGE_ATTR_COLOR.getColor());
		} else {
			setForeground(Color.BLACK);
			// super.setBackground(table.getBackground());
			// setFont(table.getFont());
		}

		setText((value == null) ? "" : value.toString());

		if (importFlag[column] == true) {
			setBackground(Color.WHITE);
			setFont(SELECTED_COL_FONT.getFont());
		} else {
			setBackground(NOT_SELECTED_COL_COLOR.getColor());
			setFont(table.getFont());
		}
		return this;
	}

}

/**
 * For rendering table header.
 * 
 * @author kono
 * 
 */
class HeaderRenderer implements TableCellRenderer {

	private static final int PARAMETER_NOT_EXIST = -1;

	private final TableCellRenderer tcr;
	
	private Byte[] dataTypes;
	
	/*
	 * For network import
	 */
	private int source;
	private int target;
	private int interaction;

	public HeaderRenderer(TableCellRenderer tcr, Byte[] dataTypes) {
		this.tcr = tcr;
		this.dataTypes = dataTypes;
		
		this.source = PARAMETER_NOT_EXIST;
		this.target = PARAMETER_NOT_EXIST;
		this.interaction = PARAMETER_NOT_EXIST;
	}

	public Component getTableCellRendererComponent(JTable tbl, Object val,
			boolean isS, boolean hasF, int row, int col) {
		JLabel columnName = (JLabel) tcr.getTableCellRendererComponent(tbl,
				val, isS, hasF, row, col);

		AttributePreviewTableCellRenderer rend = (AttributePreviewTableCellRenderer) tbl
				.getCellRenderer(0, col);
		final boolean flag = rend.getImportFlag(col);
		if (flag) {
			columnName.setFont(SELECTED_FONT.getFont());
			columnName.setForeground(SELECTED_COLOR.getColor());
			columnName.setBackground(HEADER_BACKGROUND_COLOR.getColor());
		} else {
			columnName.setFont(UNSELECTED_FONT.getFont());
			columnName.setForeground(UNSELECTED_COLOR.getColor());
			columnName.setBackground(HEADER_UNSELECTED_BACKGROUND_COLOR.getColor());
		}
		
		if(col == source){
			columnName.setForeground(SOURCE_COLOR.getColor());
		} else if(col == target) {
			columnName.setForeground(TARGET_COLOR.getColor());
		} else if(col == interaction) {
			columnName.setForeground(INTERACTION_COLOR.getColor());
		} else if(col != target && col != source && col != interaction && source != PARAMETER_NOT_EXIST) {
			columnName.setForeground(EDGE_ATTR_COLOR.getColor());
		}
		
		
		
		if (dataTypes != null && dataTypes.length > col) {
			columnName.setIcon(getDataTypeIcon(dataTypes[col]));
		} else {
			columnName.setIcon(getDataTypeIcon(CyAttributes.TYPE_STRING));
		}
		return columnName;
	}

	private static ImageIcon getDataTypeIcon(byte dataType) {
		ImageIcon dataTypeIcon = null;
		if (dataType == CyAttributes.TYPE_STRING) {
			dataTypeIcon = STRING_ICON.getIcon();
		} else if (dataType == CyAttributes.TYPE_INTEGER) {
			dataTypeIcon = INTEGER_ICON.getIcon();
		} else if (dataType == CyAttributes.TYPE_FLOATING) {
			dataTypeIcon = FLOAT_ICON.getIcon();
		} else if (dataType == CyAttributes.TYPE_BOOLEAN) {
			dataTypeIcon = BOOLEAN_ICON.getIcon();
		} else if (dataType == CyAttributes.TYPE_SIMPLE_LIST) {
			dataTypeIcon = LIST_ICON.getIcon();
		}
		return dataTypeIcon;
	}
	
	public void setSourceIndex(int idx) {
		source = idx;
	}
	
	public void setInteractionIndex(int idx) {
		interaction = idx;
	}
	
	public void setTargetIndex(int idx) {
		target = idx;
	}
	
}
