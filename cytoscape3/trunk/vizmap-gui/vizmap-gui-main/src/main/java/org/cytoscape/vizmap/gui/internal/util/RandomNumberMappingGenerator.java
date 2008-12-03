package org.cytoscape.vizmap.gui.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.vizmap.gui.util.DiscreteValueMapGenerator;

public class RandomNumberMappingGenerator implements
		DiscreteValueMapGenerator<Number> {

	public Map<Object, Number> generateMap(Set<Object> attributeSet) {
		final String range = JOptionPane.showInputDialog(
				null,
				"Please enter the value range (example: 30-100)",
				"Assign Random Numbers", JOptionPane.PLAIN_MESSAGE);

		String[] rangeVals = range.split("-");

		if (rangeVals.length != 2)
			return null;

		final long seed = System.currentTimeMillis();
		final Random rand = new Random(seed);
		final Map<Object, Number> valueMap = new HashMap<Object, Number>();

		Float min = Float.valueOf(rangeVals[0]);
		Float max = Float.valueOf(rangeVals[1]);
		Float valueRange = max - min;

		for (Object key : attributeSet)
			valueMap.put(key, (rand.nextFloat() * valueRange) + min);
		
		return valueMap;
	}

}
