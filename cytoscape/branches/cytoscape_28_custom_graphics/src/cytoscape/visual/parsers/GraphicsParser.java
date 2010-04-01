package cytoscape.visual.parsers;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.CyCustomGraphicsParser;
import cytoscape.visual.customgraphic.CyCustomGraphicsParserFactory;
import cytoscape.visual.customgraphic.CyCustomGraphicsParserFactoryImpl;
import cytoscape.visual.customgraphic.URLImageCustomGraphics;
import cytoscape.visual.customgraphic.URLImageCustomGraphicsParser;

public class GraphicsParser implements ValueParser {
	
	// Maybe injected from outside if we use DI framework.
	private final CyCustomGraphicsParserFactory parserFactory;
	
	public GraphicsParser() {
		super();
		
		parserFactory = new CyCustomGraphicsParserFactoryImpl();
		// Register default parser
		parserFactory.registerParser(URLImageCustomGraphics.class, new URLImageCustomGraphicsParser());
		
		// TODO: dynamically add parsers using listener.
	}

	@Override
	public Object parseStringValue(String value) {
		return parse(value);
	}
	
	private CyCustomGraphics<?> parse(String value) {
		if(value == null) return null;
		
		final String[] parts = value.split(",");
		// Extract class
		String className = parts[0];
		final CyCustomGraphicsParser parser = parserFactory.getParser(className);
		
		if(parser == null)		
			return null;
		
		return parser.getInstance(value);
	}

}
