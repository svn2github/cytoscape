package cytoscape.data.servers.ui.enums;

import java.awt.Color;

/**
 * Color theme for Import Dialogs.<br>
 * 
 * @since Cytoscape 2.4
 * @version 1.0
 * @author kono
 *
 */
public enum ImportDialogColorTheme {
	LABEL_COLOR(Color.black),
	KEY_ATTR_COLOR(Color.red), PRIMARY_KEY_COLOR(new Color(51, 51, 255)),
	ONTOLOGY_COLOR(new Color(0, 255, 255)), ALIAS_COLOR(new Color(51, 204, 0)),
	SPECIES_COLOR(new Color(182, 36, 212)), ATTRIBUTE_NAME_COLOR(new Color(102, 102,255)),
	NOT_SELECTED_COL_COLOR(new Color(240, 240, 240)),
	SELECTED_COLOR(Color.BLACK), UNSELECTED_COLOR(Color.GRAY),
//	HEADER_BACKGROUND_COLOR(new Color(165, 200, 254)),
	HEADER_BACKGROUND_COLOR(Color.WHITE),
	HEADER_UNSELECTED_BACKGROUND_COLOR(new Color(240, 240, 240)),
	NOT_LOADED_COLOR(Color.RED), LOADED_COLOR(Color.GREEN),
	SOURCE_COLOR(new Color(204,0,204)), INTERACTION_COLOR(new Color(255,0,51)), TARGET_COLOR(new Color(255,102,0)), EDGE_ATTR_COLOR(Color.BLUE);

	private Color color;

	private ImportDialogColorTheme(Color color) {
		this.color = color;

	}

	public Color getColor() {
		return color;
	}
}
