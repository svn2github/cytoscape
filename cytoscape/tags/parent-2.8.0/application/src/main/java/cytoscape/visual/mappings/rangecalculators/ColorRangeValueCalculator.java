package cytoscape.visual.mappings.rangecalculators;

import java.awt.Color;
import java.awt.Paint;

import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;


public class ColorRangeValueCalculator implements RangeValueCalculator<Paint> {
	
	private final ValueParser parser;
	
	public ColorRangeValueCalculator() {
		parser = new ColorParser();
	}
	

	public Color getRange(Object attrValue) {

		// OK, try returning the attrValue itself
		if (attrValue instanceof String) {
			return (Color) parser.parseStringValue((String) attrValue);
		} else if(attrValue instanceof Number) {
			return (Color) parser.parseStringValue(attrValue.toString());
		} else
			return null;
	}


	
	public boolean isCompatible(Class<?> type) {
		if(Paint.class.isAssignableFrom(type))
			return true;
		else
			return false;
	}

}
