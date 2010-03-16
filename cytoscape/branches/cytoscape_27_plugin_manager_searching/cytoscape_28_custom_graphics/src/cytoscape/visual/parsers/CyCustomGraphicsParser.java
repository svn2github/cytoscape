package cytoscape.visual.parsers;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.NullCustomGraphics;

public class CyCustomGraphicsParser implements ValueParser {

	@Override
	public Object parseStringValue(String value) {
		return parse(value);
	}
	
	private CyCustomGraphics parse(String value) {
		return new NullCustomGraphics();
	}

}
