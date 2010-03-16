package cytoscape.visual.customgraphic;

import java.util.Collection;

import cytoscape.render.stateful.CustomGraphic;

public class CustomGraphicsUtil {
	
	public static void generateCustomGraphics(final Collection<String> urlList) {
		for(final String urlStr: urlList) {
			final CyCustomGraphics<CustomGraphic> graphics = new URLImageCustomGraphics(urlStr);
			CustomGraphicsPool.getPool().addGraphics(graphics.getDisplayName(), graphics);
		}
	}
	
	public static void randomAssign() {
		
	}

}
