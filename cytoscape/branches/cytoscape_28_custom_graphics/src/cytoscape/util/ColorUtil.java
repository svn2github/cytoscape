package cytoscape.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cytoscape.Cytoscape;
/**
 * Color utility class to convert Java color object to string and vice versa.
 * 
 * @author kono
 * @since Cytoscape 2.8.0
 * 
 */
public class ColorUtil {

	private static final Map<String, String> COLOR_MAP = new HashMap<String, String>();
	private static final String COLOR_CODE_RESOURCE = "resources/cross_browser_color_code.txt";

	static {
		buildColorCodeTable(Cytoscape.class.getResource(COLOR_CODE_RESOURCE));
	}

	private static void buildColorCodeTable(final URL resourceURL) {
		BufferedReader bufRd = null;
		String line;

		try {
			bufRd = new BufferedReader(new InputStreamReader(URLUtil
					.getBasicInputStream(resourceURL)));
			while ((line = bufRd.readLine()) != null) {
				String[] parts = line.split("\\t");
				COLOR_MAP.put(parts[0].trim().toUpperCase(), parts[1].trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufRd != null) {
				try {
					bufRd.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					bufRd = null;
				}
			}
		}
	}

	/**
	 * Convert text representation of color into Color object.
	 * 
	 * <p>
	 * This parser cupports the following test representation of color:
	 * 
	 * <ul>
	 * <li>Hex representation of color (e.g. #6677FF)</li>
	 * <li>RGB numbers (float or integer, e.g. (255, 10, 100))</li>
	 * <li>Java standard colors text representations (all lower case. e.g.
	 * "black")</li>
	 * </ul>
	 * 
	 * @param colorAsText
	 * @return Color object
	 */
	public static Color parseColorText(final String colorAsText) {
		if (colorAsText == null)
			return null;

		final String trimed = colorAsText.trim();
		final String[] parts = trimed.split(",");

		// Start by seeing if this is a hex representation
		if (parts.length == 1) {
			try {
				// Chech this is a cross-browser standard color name
				final String upper = trimed.toUpperCase();				
				if (COLOR_MAP.containsKey(upper))
					return Color.decode(COLOR_MAP.get(upper));
				
				// Otherwise, treat as a hex notation.
				return Color.decode(trimed);
			} catch (Exception e) {
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
