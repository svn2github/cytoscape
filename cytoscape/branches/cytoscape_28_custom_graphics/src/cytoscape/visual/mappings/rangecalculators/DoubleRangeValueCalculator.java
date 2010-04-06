package cytoscape.visual.mappings.rangecalculators;

import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.parsers.DoubleParser;
import cytoscape.visual.parsers.ValueParser;

public class DoubleRangeValueCalculator implements RangeValueCalculator<Double>{

	private ValueParser<Double> parser;
	
	public DoubleRangeValueCalculator() {
		parser = new DoubleParser();
	}
	
	@Override
	public Double getRange(Object attrValue) {
		if(attrValue instanceof Number) {
			final Number num = (Number) attrValue;
			return num.doubleValue();
		} else if(attrValue instanceof String)
			return parser.parseStringValue((String) attrValue);
		
		return null;
	}

	@Override
	public boolean isCompatible(Class<?> type) {
		if(type.isAssignableFrom(Double.class))
			return true;
		else
			return false;
	}

}
