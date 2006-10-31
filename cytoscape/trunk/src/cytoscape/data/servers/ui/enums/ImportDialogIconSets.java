package cytoscape.data.servers.ui.enums;

import javax.swing.ImageIcon;

import cytoscape.Cytoscape;


/**
 * Iconset for Import Dialog GUI.
 * 
 * @author kono
 *
 */
public enum ImportDialogIconSets {
	STRING_ICON("images/ximian/stock_font-16.png"), INTEGER("images/ximian/stock_sort-row-ascending-16.png"), 
	FLOAT_ICON("images/ximian/stock_format-scientific-16.png"),
	INT_ICON("images/ximian/stock_sort-row-ascending-16.png"),
	LIST_ICON("images/ximian/stock_navigator-list-box-toggle-16.png"), 
	BOOLEAN_ICON("images/ximian/stock_form-radio-16.png"), 
	ID_ICON("images/ximian/stock_3d-light-on-16.png"),
	INTERACTION_ICON("images/ximian/stock_interaction.png"),
	SPREADSHEET_ICON_LARGE("images/ximian/stock_new-spreadsheet-48.png"),
	REMOTE_SOURCE_ICON("images/ximian/stock_internet-16.png"),
	LOCAL_SOURCE_ICON("images/ximian/stock_data-sources-modified-16.png"),
	SPREADSHEET_ICON("images/ximian/stock_new-spreadsheet.png"),
	TEXT_FILE_ICON("images/ximian/stock_new-text-32.png"),
	RIGHT_ARROW_ICON("images/ximian/stock_right-16.png");
	
	private String resourceLoc;
	
	private ImportDialogIconSets(String resourceLocation) {
		this.resourceLoc = resourceLocation;
	}
	
	public ImageIcon getIcon() {
		return new ImageIcon(Cytoscape.class.getResource(resourceLoc));
	}

}
