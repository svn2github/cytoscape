package cytoscape.visual.customgraphic;

import java.io.IOException;
import java.util.Collection;

import cytoscape.render.stateful.CustomGraphic;

public class CustomGraphicsUtil {

	public static void generateCustomGraphics(final Collection<String> urlList) {
		for (final String urlStr : urlList) {
			final CyCustomGraphics<CustomGraphic> graphics;
			try {
				graphics = new URLImageCustomGraphics(urlStr);
			} catch (IOException e) {
				continue;
			}
			if(graphics != null)
				CustomGraphicsPool.addGraphics(graphics.getDisplayName(), graphics);
		}
	}

	public static void randomAssign() {

	}

}
