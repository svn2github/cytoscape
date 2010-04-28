package cytoscape.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

	private static final Map<String, Color> COLOR_MAP = new HashMap<String, Color>();

	static {
		COLOR_MAP.put("black", Color.black);
		COLOR_MAP.put("blue", Color.blue);
		COLOR_MAP.put("cyan", Color.cyan);
		COLOR_MAP.put("darkGray", Color.darkGray);
		COLOR_MAP.put("gray", Color.gray);
		COLOR_MAP.put("green", Color.green);
		COLOR_MAP.put("lightGray", Color.lightGray);
		COLOR_MAP.put("magenta", Color.magenta);
		COLOR_MAP.put("orange", Color.orange);
		COLOR_MAP.put("pink", Color.pink);
		COLOR_MAP.put("red", Color.red);
		COLOR_MAP.put("white", Color.white);
		COLOR_MAP.put("yellow", Color.yellow);
	}

	/**
	 * Convert text representation of color into Color object.
	 * 
	 * <p>
	 * This parser cupports the following test representation of color:
	 * 
	 * <ul>
	 * 	<li>Hex representation of color (e.g. #6677FF)</li>
	 * 	<li>RGB numbers (float or integer, e.g. (255, 10, 100))</li>
	 * 	<li>Java standard colors text representations (all lower case.  e.g. "black")</li>
	 * </ul>
	 * 
	 * @param colorAsText
	 * @return Color object
	 */
	public static Color parseColorText(final String colorAsText) {
		if (colorAsText == null)
			return null;

		final String[] parts = colorAsText.split(",");

		// Start by seeing if this is a hex representation
		if (parts.length == 1) {
			try {
				final String colorStr = colorAsText.trim();
				if (COLOR_MAP.containsKey(colorStr))
					return COLOR_MAP.get(colorStr);

				return Color.decode(colorStr);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		if (parts.length != 3)
			return null;

		final String red = parts[0].trim();
		final String green = parts[1].trim();
		final String blue = parts[2].trim();

		try {
			if (red.contains(".") || green.contains(".") || blue.contains(".")) {
				float r = Float.parseFloat(red);
				float g = Float.parseFloat(green);
				float b = Float.parseFloat(blue);
				return new Color(r, g, b);
			} else {
				int r = Integer.parseInt(red);
				int g = Integer.parseInt(green);
				int b = Integer.parseInt(blue);
				return new Color(r, g, b);
			}

		} catch (Exception e) {
			return null;
		}
	}

	
	/**
	 * Convert Color object into RGB String (e.g., (200,100,120))
	 * 
	 * @param color
	 * @return
	 */
	public static String getColorAsText(Color color) {
		return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
	}

}
