package cytoscape.visual.parsers;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.NullCustomGraphics;

public class CyCustomGraphicsParser implements ValueParser {

	@Override
	public Object parseStringValue(String value) {
		return parse(value);
	}
	
	private CyCustomGraphics<?> parse(String value) {
		if(value == null) return null;
		
		final String[] parts = value.split(",");
		
		// Extract class
		String className = parts[0];
		
		return new NullCustomGraphics();
	}

}
