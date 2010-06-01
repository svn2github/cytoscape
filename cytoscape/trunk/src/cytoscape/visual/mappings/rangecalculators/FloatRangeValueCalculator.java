package cytoscape.visual.mappings.rangecalculators;

import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.parsers.FloatParser;
import cytoscape.visual.parsers.ValueParser;

public class FloatRangeValueCalculator implements RangeValueCalculator<Float> {

	private ValueParser<Float> parser;

	public FloatRangeValueCalculator() {
		parser = new FloatParser();
	}

	@Override
	public Float getRange(Object attrValue) {
		if (attrValue instanceof Number) {
			final Number num = (Number) attrValue;
			return num.floatValue();
		} else if (attrValue instanceof String)
			return parser.parseStringValue((String) attrValue);

		return null;
	}

	@Override
	public boolean isCompatible(Class<?> type) {
		if (type.isAssignableFrom(Float.class))
			return true;
		else
			return false;
	}

}
