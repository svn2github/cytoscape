package cytoscape.visual.ui.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RainbowColorMappingGenerator implements
		DiscreteValueMapGenerator<Color> {

	public Map<Object, Color> generateMap(Set<Object> attributeSet) {
		final float increment = 1f / ((Number) attributeSet.size())
				.floatValue();

		float hue = 0;

		final Map<Object, Color> valueMap = new HashMap<Object, Color>();

		for (Object key : attributeSet) {
			hue = hue + increment;
			valueMap.put(key, new Color(Color.HSBtoRGB(hue, 1f, 1f)));
		}

		return valueMap;
	}

}
