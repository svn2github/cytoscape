package cytoscape.data.servers.ui.enums;

import java.awt.Font;

public enum ImportDialogFontTheme {
	SELECTED_COL_FONT(new Font("Sans-serif", Font.BOLD, 14)),
	SELECTED_FONT(new Font("Sans-serif", Font.BOLD, 14)),
	UNSELECTED_FONT(new Font("Sans-serif",Font.PLAIN, 14)),
	KEY_FONT(new Font("Sans-Serif", Font.BOLD, 14));
	
	private Font font;
	
	private ImportDialogFontTheme(Font font) {
		this.font = font;
	}
	
	public Font getFont() {
		return font;
	}
}
