package org.cytoscape.vizmap.gui.internal.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.vizmap.gui.util.DiscreteValueMapGenerator;

public class RainbowOscColorMappingGenerator implements
		DiscreteValueMapGenerator<Color> {

	public Map<Object, Color> generateMap(Set<Object> attributeSet) {
		final float increment = 1f / ((Number) attributeSet.size())
				.floatValue();

		float hue = 0;
		float sat = 0;
		float br = 0;

		final Map<Object, Color> valueMap = new HashMap<Object, Color>();

		int i = 0;
		for (Object key : attributeSet) {
			hue = hue + increment;
			sat = (Math.abs(((Number) Math.cos((8 * i) / (2 * Math.PI)))
					.floatValue()) * 0.7f) + 0.3f;
			br = (Math.abs(((Number) Math.sin(((i) / (2 * Math.PI))
					+ (Math.PI / 2))).floatValue()) * 0.7f) + 0.3f;
			valueMap.put(key, new Color(Color.HSBtoRGB(hue, sat, br)));
			i++;
		}

		return valueMap;
	}

}
