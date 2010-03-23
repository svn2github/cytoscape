package cytoscape.visual.parsers;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.CyCustomGraphicsParser;
import cytoscape.visual.customgraphic.CyCustomGraphicsParserFactoryImpl;
import cytoscape.visual.customgraphic.URLImageCustomGraphicsParser;

public class GraphicsParser implements ValueParser {
	
	public GraphicsParser() {
		super();
		// Add default value parser
		URLImageCustomGraphicsParser.getInstance();
	}

	@Override
	public Object parseStringValue(String value) {
		return parse(value);
	}
	
	private CyCustomGraphics<?> parse(String value) {
		if(value == null) return null;
		
		
		System.out.println("@@@@@@@@ PARSE: " + value);
		final String[] parts = value.split(",");
		// Extract class
		String className = parts[0];
		
		System.out.println("@@@@@@@@ CLASSNAME: " + className);
		final CyCustomGraphicsParser parser = CyCustomGraphicsParserFactoryImpl.getFactory().getParser(className);
		System.out.println("@@@@@@@@ PARSER: " + parser);
		
		if(parser == null)		
			return null;
		
		return parser.getInstance(value);
	}

}
