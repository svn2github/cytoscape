package cytoscape.visual.mappings.rangecalculators;

import java.awt.Color;
import java.awt.Paint;

import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.mappings.RangeValueCalculatorType;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;

@RangeValueCalculatorType
public class ColorRangeValueCalculator implements RangeValueCalculator<Paint> {
	
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
	public boolean isCompatible(Class<?> type) {
		if(Paint.class.isAssignableFrom(type))
			return true;
		else
			return false;
	}

}
