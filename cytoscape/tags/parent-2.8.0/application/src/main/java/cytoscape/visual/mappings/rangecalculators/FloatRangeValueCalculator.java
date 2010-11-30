package cytoscape.visual.mappings.rangecalculators;

import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.parsers.FloatParser;
import cytoscape.visual.parsers.ValueParser;

public class FloatRangeValueCalculator implements RangeValueCalculator<Float> {

	private ValueParser parser;

	public FloatRangeValueCalculator() {
		parser = new FloatParser();
	}

	
	public Float getRange(Object attrValue) {
		if (attrValue instanceof Number) {
			final Number num = (Number) attrValue;
			return num.floatValue();
		} else if (attrValue instanceof String)
			return (Float) parser.parseStringValue((String) attrValue);

		return null;
	}

	
	public boolean isCompatible(Class<?> type) {
		if (type.isAssignableFrom(Float.class))
			return true;
		else
			return false;
	}

}
