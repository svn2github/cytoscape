package cytoscape.visual.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.view.VisualProperty;
import org.cytoscape.vizmap.mappings.DiscreteMapping;

public class GenerateValueListener extends DiscreteMappingEditorListener {
	private final static long serialVersionUID = 1213748836986412L;
	private final int MAX_COLOR = 256 * 256 * 256;
	protected static final int RAINBOW1 = 1;
	protected static final int RAINBOW2 = 2;
	protected static final int RANDOM = 3;
	
	protected final int functionType;
	
	public GenerateValueListener (final VisualPropertySheetPanel panel, final int type) {
		super(panel);
		this.functionType = type;
	}

	public Map<Object, Object> generateValues(VisualProperty type, DiscreteMapping dm, Set<Object> attrSet) {
		// Show error if there is no attribute value.
		if (attrSet.size() == 0) {
			JOptionPane.showMessageDialog(visualPropertySheetPanel.getPSP(), "No attribute value is available.",
		                              	"Cannot generate values", JOptionPane.ERROR_MESSAGE);
		}
		/*
		 * Create random colors
		 */
		final long seed = System.currentTimeMillis();
		final Random rand = new Random(seed);

		final float increment = 1f / ((Number) attrSet.size()).floatValue();
		float hue = 0;
		float sat = 0;
		float br = 0;
		final Map<Object, Object> valueMap = new HashMap<Object, Object>();
		
		if (type.getDataType() == Color.class) {
			int i = 0;
			if (functionType == RAINBOW1) {
				for (Object key : attrSet) {
					hue = hue + increment;
					valueMap.put(key, new Color(Color.HSBtoRGB(hue, 1f, 1f)));
				}
			} else if (functionType == RAINBOW2) {
				for (Object key : attrSet) {
					hue = hue + increment;
					sat = (Math.abs(((Number) Math.cos((8 * i) / (2 * Math.PI))).floatValue()) * 0.7f) + 0.3f;
					br = (Math.abs(((Number) Math.sin(((i) / (2 * Math.PI)) + (Math.PI / 2))).floatValue()) * 0.7f) + 0.3f;
					valueMap.put(key, new Color(Color.HSBtoRGB(hue, sat, br)));
					i++;
				}
			} else {
				for (Object key : attrSet)
						valueMap.put(key, new Color(((Number) (rand.nextFloat() * MAX_COLOR)) .intValue()));
				}
		} else if ((type.getDataType() == Number.class) && (functionType == RANDOM)) {
			final String range = JOptionPane.showInputDialog(visualPropertySheetPanel.getPSP(),
					"Please enter the value range (example: 30-100)",
					"Assign Random Numbers",
					JOptionPane.PLAIN_MESSAGE);
			String[] rangeVals = range.split("-");
			if (rangeVals.length != 2)
				return null;
			Float min = Float.valueOf(rangeVals[0]);
			Float max = Float.valueOf(rangeVals[1]);
			Float valueRange = max - min;
			for (Object key : attrSet)
				valueMap.put(key, (rand.nextFloat() * valueRange) + min);
		}
		return valueMap;
	}
}