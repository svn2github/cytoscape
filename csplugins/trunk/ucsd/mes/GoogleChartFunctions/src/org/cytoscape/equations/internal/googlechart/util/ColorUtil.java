package org.cytoscape.equations.internal.googlechart.util;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.charts4j.Color;

public class ColorUtil {

	
	public static Color getNextColor() {
		return Color.BLACK;
	}
	
	public static List<Color> getColors(final int numberOfColors) {
		
		final List<Color> colors = new ArrayList<Color>();
		
		final float increment = 1f / numberOfColors;

		float hue = 0;
		
		for (int i=0; i<numberOfColors; i++) {
			hue = hue + increment;
			final java.awt.Color awtColor = new java.awt.Color(java.awt.Color.HSBtoRGB(hue, 1f, 1f));
			final String colorString = Integer.toHexString(awtColor.getRGB());
			final String withoutAlpha = colorString.substring(2, colorString.length());
			colors.add(Color.newColor(withoutAlpha, 100 ) );
		}
		
		return colors;
	}
}
