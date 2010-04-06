package cytoscape.visual.mappings;

import java.awt.Color;

import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;

public class ColorRangeValueCalculator implements RangeValueCalculator<Color> {
	
	private final ValueParser<Color> parser;
	
	public ColorRangeValueCalculator() {
		parser = new ColorParser();
	}
	
	@Override
	public Color getRange(Object attrValue) {

		// OK, try returning the attrValue itself
		if (attrValue instanceof String) {
			return parser.parseStringValue((String) attrValue);
		} else if(attrValue instanceof Number) {
			Number num = (Number) attrValue;
			int rgbValue = num.intValue();
			if(rgbValue <=255 && rgbValue >=0) {
				return new Color(rgbValue);
			} else
				return null;
		} else
			return null;
	}

	@Override
	public Class<Color> getRangeClass() {
		return Color.class;
	}

}
