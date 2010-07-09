package cytoscape.visual.parsers;

import java.io.IOException;
import java.net.URL;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.CyCustomGraphicsParser;
import cytoscape.visual.customgraphic.CyCustomGraphicsParserFactory;
import cytoscape.visual.customgraphic.DefaultCyCustomGraphicsParser;
import cytoscape.visual.customgraphic.URLImageCustomGraphicsParser;
import cytoscape.visual.customgraphic.impl.CyCustomGraphicsParserFactoryImpl;
import cytoscape.visual.customgraphic.impl.bitmap.URLImageCustomGraphics;

public class GraphicsParser implements ValueParser<CyCustomGraphics> {
	
	private static final String NULL_CG = "cytoscape.visual.customgraphic.NullCustomGraphics";
	
	private static final CyLogger logger = CyLogger.getLogger();
	
	// Maybe injected from outside if we use DI framework.
	private final CyCustomGraphicsParserFactory parserFactory;
	
	private final CyCustomGraphicsParser defaultParser;
	
	public GraphicsParser() {
		super();
		
		parserFactory = new CyCustomGraphicsParserFactoryImpl();
		// Register default parser
		parserFactory.registerParser(URLImageCustomGraphics.class, new URLImageCustomGraphicsParser());
		defaultParser = new DefaultCyCustomGraphicsParser();
		
		// TODO: dynamically add parsers using listener.
	}


	public CyCustomGraphics parseStringValue(String value) {
		return parse(value);
	}
	
	
	/**
	 * Parse given string.
	 * 
	 * Syntax 1: (URL)
	 * Syntax 2: (Class Name, ID, Name, Tags)
	 * 
	 * @param value
	 * @return
	 */
	private CyCustomGraphics parse(String value) {
		if(value == null || value.equals(NULL_CG))
			return null;
		
		// Syntax 1:  URL String.
		try {
			final URL url = new URL(value);
			CyCustomGraphics graphics = Cytoscape.getVisualMappingManager().getCustomGraphicsManager().getBySourceURL(url);
			if(graphics == null) {
				// Currently not in the Manager.  Need to create new instance.
				graphics = new URLImageCustomGraphics(url.toString());
				// Use URL as display name
				graphics.setDisplayName(value);
				
				// Register to manager.
				Cytoscape.getVisualMappingManager().getCustomGraphicsManager().addGraphics(graphics, url);
			}
			return graphics;
		} catch (IOException e) {
			
			// Syntax 2:
			final String[] parts = value.split(",");
			if(parts.length<4)
				return null;
			
			// Extract class name
			final String className = parts[0];
			
			// Get class-specific parser
			final CyCustomGraphicsParser parser = parserFactory.getParser(className);
			
			if(parser == null) 
				return defaultParser.getInstance(value);
			else
				return parser.getInstance(value);
			
		}
	}
	
	

}
