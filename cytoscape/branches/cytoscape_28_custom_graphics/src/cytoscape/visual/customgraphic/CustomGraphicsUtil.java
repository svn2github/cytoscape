package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.io.IOException;
import java.util.Collection;

import cytoscape.Cytoscape;
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
				Cytoscape.getVisualMappingManager().getCustomGraphicsPool().addGraphics(graphics.getDisplayName(), graphics);
		}
	}

	public static Image getResizedImage(Image original, final Integer w, final Integer h, boolean keepAspectRatio) {
		if(original == null)
			throw new IllegalArgumentException("Original image cannot be null.");
		
		if(w == null && h == null)
			return original;
		
		final int currentW = original.getWidth(null);
		final int currentH = original.getHeight(null);
		float ratio;
		int converted;
		
		if (keepAspectRatio == false)
			return original.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
		else if (h == null) {
			ratio = ((float) currentH) / ((float) currentW);
			converted = (int) (w * ratio);
			return original.getScaledInstance(w, converted, Image.SCALE_AREA_AVERAGING);
		} else {
			ratio = ((float) currentW) / ((float) currentH);
			converted = (int) (h * ratio);
			return original.getScaledInstance(converted, h, Image.SCALE_AREA_AVERAGING);
		}

	}

}
