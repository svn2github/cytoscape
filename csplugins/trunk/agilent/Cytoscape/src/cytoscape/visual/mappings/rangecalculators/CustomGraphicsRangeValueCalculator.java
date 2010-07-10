package cytoscape.visual.mappings.rangecalculators;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.parsers.GraphicsParser;
import cytoscape.visual.parsers.ValueParser;

public class CustomGraphicsRangeValueCalculator implements
		RangeValueCalculator<CyCustomGraphics<?>> {
	
	private ValueParser<CyCustomGraphics<?>> parser;
	
	public CustomGraphicsRangeValueCalculator() {
		parser = new GraphicsParser();
	}


	public CyCustomGraphics<?> getRange(Object attrValue) {
		if (attrValue instanceof String) {
			CyCustomGraphics<?> obj = parser.parseStringValue((String) attrValue);
			return obj;
		}
		return null;
	}

	
	public boolean isCompatible(Class<?> type) {
		if(CyCustomGraphics.class.isAssignableFrom(type))
			return true;
		else
			return false;
	}

}
