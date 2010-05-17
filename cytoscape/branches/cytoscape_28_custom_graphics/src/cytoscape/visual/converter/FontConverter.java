package cytoscape.visual.converter;

import java.awt.Font;

public class FontConverter implements ValueToStringConverter {

	@Override
	public String toString(Object value) {
		if (value instanceof Font)
			return getFontStringValue((Font) value);
		else
			return "";
	}

	@Override
	public Class<?> getType() {
		return Font.class;
	}

	private String getFontStringValue(final Font f) {
		String name = f.getName();
		int style = f.getStyle();
		String styleString = "plain";

		if (style == Font.BOLD)
			styleString = "bold";
		else if (style == Font.ITALIC)
			styleString = "italic";
		else if (style == (Font.BOLD | Font.ITALIC))
			styleString = "bold|italic";

		int size = f.getSize();
		String sizeString = Integer.toString(size);

		return name + "," + styleString + "," + sizeString;
	}
}