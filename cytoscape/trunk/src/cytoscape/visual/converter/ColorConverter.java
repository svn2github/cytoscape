package cytoscape.visual.converter;

import java.awt.Color;

import cytoscape.util.ColorUtil;

public class ColorConverter implements ValueToStringConverter {


	public String toString(Object value) {
		if(value instanceof Color)
			return  ColorUtil.getColorAsText((Color) value);
		else
			return "";
	}

	
	public Class<?> getType() {
		return Color.class;
	}

}
