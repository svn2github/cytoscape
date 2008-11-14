package cytoscape.visual.ui.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomColorMappingGenerator implements
		DiscreteValueMapGenerator<Color> {

	private final int MAX_COLOR = 256 * 256 * 256;
	final long seed = System.currentTimeMillis();
	final Random rand = new Random(seed);

	public Map<Object, Color> generateMap(Set<Object> attributeSet) {

		final Map<Object, Color> valueMap = new HashMap<Object, Color>();

		for (Object key : attributeSet)
			valueMap.put(key, new Color(
					((Number) (rand.nextFloat() * MAX_COLOR)).intValue()));

		return valueMap;
	}

}
