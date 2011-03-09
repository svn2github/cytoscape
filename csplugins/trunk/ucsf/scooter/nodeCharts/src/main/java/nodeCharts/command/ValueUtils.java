/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package nodeCharts.command;

// System imports
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.awt.Color;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.data.CyAttributes;

public class ValueUtils {

	public static List<Double> convertInputToDouble(Object input) throws CyCommandException {
		if (input == null)
			throw new CyCommandException("no input data?");

		List<Double>values = null;
		if (input instanceof String) {
			values = ValueUtils.parseStringList((String)input);
		} else if (input instanceof List) {
			Object o = ((List)input).get(0);
			if (o instanceof Double) {
				values = (List<Double>)input;
			} else if (o instanceof String) {
				values = ValueUtils.convertStringList((List<String>)input);
			} else if (o instanceof Integer) {
				values = ValueUtils.convertIntegerList((List<Integer>)input);
			}
		}
		return values;
	}

	/**
 	 * Get values from a list of attributes.  The attributesList can either be a single list attribute with
 	 * numeric values or a list of integer or floating point attributes.  At some point, it might be interesting
 	 * to think about other combinations, but this is a good starting point.
 	 *
 	 * @param node the node we're getting the custom graphics from
 	 * @param attributelist the list of attribute names
 	 * @param labels the list of labels if the user wants to override the attribute names
 	 * @return the list of values
 	 * @throws CyCommandException if the attributes aren't numeric
 	 */
	public static List<Double> getDataFromAttributes (CyNode node, Object attributesList, List<String> labels) 
	                                                                                     throws CyCommandException {
		if (attributesList == null)
			throw new CyCommandException("no attributes with data?");
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String nodeName = node.getIdentifier();

		String[] attributeArray = null;
		if (attributesList instanceof String) {
			attributeArray = ((String)attributesList).split(",");
		} else if (attributesList instanceof List) {
			String[] s = new String[1];
			attributeArray = ((List<String>)attributesList).toArray(s);
		} else {
			return new ArrayList<Double>();
		}
		if (attributeArray.length == 1) {
			// Handle the case where we were given a single, list attribute
			if (labels == null || labels.size() == 0) {
				throw new CyCommandException("Labels must be specified if attribute is a list");
			}
			String attr = attributeArray[0].trim();
			if (nodeAttributes.hasAttribute(nodeName, attr) &&
			    nodeAttributes.getType(attr) == CyAttributes.TYPE_SIMPLE_LIST) {
				List vList = nodeAttributes.getListAttribute(nodeName, attr);
				return convertInputToDouble(vList);
			} else {
				throw new CyCommandException("Node "+nodeName+" doesn't have a list attribute named "+attr);
			}
		} else {
			// Handle the case of a list of attributes that contain the data
			List<Double> result = new ArrayList<Double>();
			for (String attr: attributeArray) {
				if (!nodeAttributes.hasAttribute(nodeName, attr) ||
				    ((nodeAttributes.getType(attr) != CyAttributes.TYPE_FLOATING) &&
				     (nodeAttributes.getType(attr) != CyAttributes.TYPE_INTEGER) &&
				     (nodeAttributes.getType(attr) != CyAttributes.TYPE_STRING))) {
					result.add(0.0);
					continue;
				}

				switch (nodeAttributes.getType(attr)) {
				case CyAttributes.TYPE_FLOATING:
					result.add(nodeAttributes.getDoubleAttribute(nodeName, attr));
					break;
				case CyAttributes.TYPE_INTEGER:
					result.add(nodeAttributes.getIntegerAttribute(nodeName, attr).doubleValue());
					break;
				case CyAttributes.TYPE_STRING:
					String s = null;
					try {
						s = nodeAttributes.getStringAttribute(nodeName, attr);
						result.add(Double.valueOf(s));
					} catch (NumberFormatException e) {
						throw new CyCommandException("Non-numeric value: '"+s+"' in attribute: "+attr);
					}
				}
			}
			if (labels != null && labels.size() == 0)
				labels.addAll(Arrays.asList(attributeArray));
			return result;
		}
	}

	public static List<Double> convertStringList(List<String> input) throws CyCommandException {
		List<Double> values = new ArrayList<Double>(input.size());
		for (String s: input) {
			try {
				Double d = Double.valueOf(s);
				values.add(d);
			} catch (NumberFormatException e) {
				throw new CyCommandException("Non-numeric value: '"+s+"' in values array");
			}
		}
		return values;
	}

	public static List<Double> convertIntegerList(List<Integer> input) {
		List<Double> values = new ArrayList<Double>(input.size());
		for (Integer s: input) {
			double d = s.doubleValue();
			values.add(d);
		}
		return values;
	}

	public static List<Double> parseStringList(String input) throws CyCommandException {
		if (input == null)
			throw new CyCommandException("no input data?");
		String[] inputArray = ((String)input).split(",");
		return convertStringList(Arrays.asList(inputArray));
	}

	public static List<String> getStringList(Object input) {
		if (input instanceof String) {
			String[] inputArray = ((String)input).split(",");
			return Arrays.asList(inputArray);
		} else if (input instanceof List) {
			List<String> result = new ArrayList<String>();
			for (Object o: (List)input) {
				result.add(o.toString());
			}
			return result;
		}

		return new ArrayList<String>();
	}

	/**
 	 * Return the boolean equivalent of the input
 	 *
 	 * @param input an input value that is supposed to be Boolean
 	 * @return the boolean value it represents
 	 */
	public static boolean getBooleanValue(Object input) {
		if (input instanceof Boolean)
			return ((Boolean)input).booleanValue();
		return Boolean.parseBoolean(input.toString());
	}

	/**
 	 * Return the double equivalent of the input
 	 *
 	 * @param input an input value that is supposed to be a double
 	 * @return the a double value it represents
 	 * @throws NumberFormatException is the value is illegal
 	 */
	public static double getDoubleValue(Object input) throws NumberFormatException {
		if (input instanceof Double)
			return ((Double)input).doubleValue();
		else if (input instanceof Integer)
			return ((Integer)input).doubleValue();
		else if (input instanceof String)
			return Double.parseDouble((String)input);
		throw new NumberFormatException("input can not be converted to double");
	}

	/**
 	 * Return the size specified by the user in the width and height fields of the Rectangle
 	 * The size can be either "sss" where "sss" will be both the height and the width or
 	 * "hhhxwww" where hhh is the height and www is the width.
 	 *
 	 * @param input the input size
 	 * @return a rectangle to get the width and height from
 	 */
	public static Rectangle2D getSize(Object input) throws CyCommandException {
		if (input instanceof Rectangle2D) 
			return (Rectangle2D) input;
		else if (input instanceof Double) {
			double v = ((Double)input).doubleValue();
			return new Rectangle2D.Double(0.0,0.0,v,v);
		} else if (input instanceof Integer) {
			double v = ((Integer)input).doubleValue();
			return new Rectangle2D.Double(0.0,0.0,v,v);
		} else if (input instanceof String) {
			String inputString = (String)input;
			String[] sizes = inputString.split("[xX]");
			if (sizes.length == 1) {
				double v = Double.parseDouble(sizes[0]);
				return new Rectangle2D.Double(0.0,0.0,v,v);
			} else if (sizes.length == 2) {
				double h = Double.parseDouble(sizes[0]);
				double w = Double.parseDouble(sizes[1]);
				return new Rectangle2D.Double(0.0,0.0,w,h);
			} 
		}
		throw new CyCommandException("unable to convert "+input.toString()+" to a size");
	}

	public static List<Double> arrayMax(List<Double> maxValues, List<Double> values) {
		// Initialize, if necessary
		if (maxValues == null) {
			maxValues = new ArrayList<Double>(values.size());
			for (Double d: values)
				maxValues.add(Math.abs(d));
			return maxValues;
		}

		// OK, now we need to actually do the work...
		for (int index = 0; index < values.size(); index++) {
			maxValues.set(index, Math.max(maxValues.get(index), Math.abs(values.get(index))));
		}
		return maxValues;
	}

	public static void normalize(List<Double> values, List<Double> maxValues) {
		for (int index = 0; index < values.size(); index++) {
			values.set(index, values.get(index)/maxValues.get(index));
		}
	}

	/**
 	 * Takes a map of objects indexed by a string keyword and returns
 	 * a map of strings indexed by that keyword.  This involves figuring
 	 * out if the object is a list, and if so converting it to a comma
 	 * separated string
 	 *
 	 * @param argMap the map of objects indexed by strings
 	 * @return the serialized map
 	 */
	public static Map<String,String> serializeArgMap(Map<String, Object> argMap) {
		Map<String,String> sMap = new HashMap<String,String>();
		for (String key: argMap.keySet()) {
			sMap.put(key, serializeObject(argMap.get(key)));
		}
		return sMap;
	}

	/**
 	 * Serialize an object that might be a list to a string
 	 */
	private static String serializeObject(Object obj) {
		String result;
		if (obj instanceof List) {
			result = "";
			for (Object o: (List)obj) {
				result += o.toString()+",";
			}
			result = result.substring(0, result.length()-1);
		} else
			result = obj.toString();

		return result;
	}

	private static final String	CONTRASTING = "contrasting";
	private static final String	DOWN = "down:";
	private static final String	MODULATED = "modulated";
	private static final String	RAINBOW = "rainbow";
	private static final String RANDOM = "random";
	private static final String	UP = "up:";
	private static final String	ZERO = "zero:";
	private static final double EPSILON = 1E-8f;

	public static List<Color> convertInputToColor(Object input, List<Double>values) throws CyCommandException {
		int nColors = values.size();

		if (input == null) {
			// give the default: contrasting colors
			return generateContrastingColors(nColors);
		}

		// OK, we have three posibilities.  The input could be a keyword, a comma-separated list of colors, or
		// a list of Color objects.  We need to figure this out first...
		if (input instanceof List) {
			if ( ((List<Object>)input).get(0) instanceof Color) 
				return (List<Color>)input;
			else 
				throw new CyCommandException("color list not of type Color?");
		} else if (input instanceof String) {
			String inputString = (String) input;
			// See if we have a csv
			String [] colorArray = inputString.split(",");
			// Look for up/down special case
			if (colorArray.length == 2 &&
			    (colorArray[0].toLowerCase().startsWith(UP) ||
			     colorArray[0].toLowerCase().startsWith(DOWN))) {
				return parseUpDownColor(colorArray, values);
			} else if (colorArray.length == 3 &&
			    (colorArray[0].toLowerCase().startsWith(UP) ||
			     colorArray[0].toLowerCase().startsWith(DOWN) ||
			     colorArray[0].toLowerCase().startsWith(ZERO))) {
				return parseUpDownColor(colorArray, values);
			} else if (colorArray.length > 1)
				return parseColorList(colorArray);
			else
				return parseColorKeyword(inputString.trim(), nColors);
		} else 
			throw new CyCommandException("unknown type for color list");
	}

	public static List<Color> parseUpDownColor(String[] colorArray) throws CyCommandException {
		if (colorArray.length < 2)
			throw new CyCommandException("Up/Down color spec must have at least 2 colors");

		String [] colors = new String[3];
		colors[2] = "black";
		for (int index = 0; index < colorArray.length; index++) {
			if (colorArray[index].toLowerCase().startsWith(UP)) {
				colors[0] = colorArray[index].substring(UP.length());
			} else if (colorArray[index].toLowerCase().startsWith(DOWN)) {
				colors[1] = colorArray[index].substring(DOWN.length());
			} else if (colorArray[index].toLowerCase().startsWith(ZERO)) {
				colors[2] = colorArray[index].substring(ZERO.length());
			}
		} 
		return parseColorList(colors);
	}

	private static List<Color> parseUpDownColor(String[] colorArray, List<Double>values) throws CyCommandException {
		List<Color> upDownColors = parseUpDownColor(colorArray);
		Color up = upDownColors.get(0);
		Color down = upDownColors.get(1);
		Color zero = upDownColors.get(2);

		List<Color> results = new ArrayList<Color>(values.size());
		for (Double v: values) {
			if (v < EPSILON) 
				results.add(down);
			else if (v > EPSILON)
				results.add(up);
			else
				results.add(zero);
		}
		return results;
	}

	private static List<Color> parseColorKeyword(String input, int nColors) throws CyCommandException {
		if (input.equals(RANDOM))
			return generateRandomColors(nColors);
		else if (input.equals(RAINBOW))
			return generateRainbowColors(nColors);
		else if (input.equals(MODULATED))
			return generateModulatedRainbowColors(nColors);
		else if (input.equals(CONTRASTING))
			return generateContrastingColors(nColors);
		else {
			String [] colorArray = null;
			colorArray[0] = input;
			List<Color> colors = parseColorList(colorArray);
			return colors;
			//throw new CyCommandException("unknown color keyword: '"+input+"'");
		}
	}

	private static List<Color> parseColorList(String[] inputArray) throws CyCommandException {
		List<Color> colors = new ArrayList<Color>();
		// A color in the array can either be a hex value or a text color
		for (String colorString: inputArray) {
			colorString = colorString.trim();
			if (colorString.matches("^#([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6})$")) {
				// We have a hex value with either 6 (rgb) or 8 (rgba) digits
				int r = Integer.parseInt(colorString.substring(1,3), 16);
				int g = Integer.parseInt(colorString.substring(3,5), 16);
				int b = Integer.parseInt(colorString.substring(5,7), 16);
				if (colorString.length() > 7) {
					int a = Integer.parseInt(colorString.substring(7,9), 16);
					colors.add(new Color(r,g,b,a));
				} else {
					colors.add(new Color(r,g,b));
				}
			} else {
				// Check for color string
				Color c = ColorKeyword.getColor(colorString);
				if (c == null)
					throw new CyCommandException("unknown color: '"+colorString+"'");
				colors.add(c);
			}
		}
		return colors;
	}

	private static List<Color> generateRandomColors(int nColors) {
		// System.out.println("Generating random colors");
		Calendar cal = Calendar.getInstance();
		int seed = cal.get(Calendar.SECOND);
		Random rand = new Random(seed);

		List<Color> result = new ArrayList<Color>(nColors);
		for (int index = 0; index < nColors; index++) {
			int r = rand.nextInt(255);
			int g = rand.nextInt(255);
			int b = rand.nextInt(255);
			result.add(index, new Color(r,g,b,200));
		}
		return result;
	}

	// Rainbow colors just divide the Hue wheel into n pieces and return them
	private static List<Color> generateRainbowColors(int nColors) {
		// System.out.println("Generating rainbow colors");
		List<Color> values = new ArrayList<Color>();
		for (float i = 0.0f; i < (float)nColors; i += 1.0f) {
			values.add(new Color(Color.HSBtoRGB(i/(float)nColors, 1.0f, 1.0f)));
		}
		return values;
	}

	// Rainbow colors just divide the Hue wheel into n pieces and return them, but
	// in this case, we're going to change the saturation and intensity
	private static List<Color> generateModulatedRainbowColors(int nColors) {
		// System.out.println("Generating modulated colors");
		List<Color> values = new ArrayList<Color>();
		for (float i = 0.0f; i < (float)nColors; i += 1.0f) {
			float sat = (Math.abs(((Number) Math.cos((8 * i) / (2 * Math.PI))).floatValue()) * 0.7f) 
			             + 0.3f;
			float br = (Math.abs(((Number) Math.sin(((i) / (2 * Math.PI)) + (Math.PI / 2)))
			                      .floatValue()) * 0.7f) + 0.3f;

			// System.out.println("Color("+(i/(float)nColors)+","+sat+","+br+")");
			values.add(new Color(Color.HSBtoRGB(i/(float)nColors, sat, br)));
		}
		return values;
	}

	// This is like rainbow, but we alternate sides of the color wheel
	private static List<Color> generateContrastingColors(int nColors) {
		// System.out.println("Generating contrasting colors");
		List<Color> values = new ArrayList<Color>();
		float divs = ((float)nColors)/2.0f;
		for (float i = 0.0f; i < (float)nColors; i += 1.0f) {
			// System.out.println("Color("+(i/divs)+","+1.0f+","+1.0f+")");
			values.add(new Color(Color.HSBtoRGB(i/divs, 1.0f, 1.0f)));
			i += 1.0f;
			if (i >= (float)nColors) break;
			// System.out.println("Color("+((i/divs)+0.5f)+","+1.0f+","+1.0f+")");
			values.add(new Color(Color.HSBtoRGB((i/divs)+0.5f, 1.0f, 1.0f)));
		}
		return values;
	}

}
