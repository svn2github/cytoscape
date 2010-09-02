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
package nodeCharts.view;

// System imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.awt.Color;

// Cytoscape imports
import cytoscape.command.CyCommandException;

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

		String[] inputArray = input.split(",");
		return convertStringList(Arrays.asList(inputArray));
	}

	public enum ColorKeyword {
		RANDOM ("random"),
		CONTRASTING ("contrasting"),
		RAINBOW ("rainbow"),
		MODULATED ("modulated");
	
		private String label;
		private static Map<String, ColorKeyword>cMap;
	
		ColorKeyword(String label) { 
			this.label = label; 
			addKeyword(this);
		}
	
		public String getLabel() {
			return label;
		}

		public String toString() {
			return label;
		}
	
		private void addKeyword(ColorKeyword col) {
			if (cMap == null) cMap = new HashMap<String,ColorKeyword>();
			cMap.put(col.getLabel(), col);
		}
	
		static ColorKeyword getColorKeyword(String label) {
			if (cMap.containsKey(label))
				return cMap.get(label);
			return null;
		}
	}

	public static List<Color> convertInputToColor(Object input, int nColors) throws CyCommandException {
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
			if (colorArray.length > 1)
				return parseColorList(colorArray);
			else
				return parseColorKeyword(inputString.trim(), nColors);
		} else 
			throw new CyCommandException("unknown type for color list");
	}

	private static List<Color> parseColorKeyword(String input, int nColors) throws CyCommandException {
		ColorKeyword ckey = ColorKeyword.getColorKeyword(input);
		if (ckey == null) 
			throw new CyCommandException("unknown color keyword");
		switch (ckey) {
		case RANDOM:
			return generateRandomColors(nColors);
		case RAINBOW:
			return generateRainbowColors(nColors);
		case MODULATED:
			return generateModulatedRainbowColors(nColors);
		case CONTRASTING:
		default:
			return generateContrastingColors(nColors);
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
		for (float i = 0.0f; i < divs; i += 1.0f) {
			// System.out.println("Color("+(i/divs)+","+1.0f+","+1.0f+")");
			values.add(new Color(Color.HSBtoRGB(i/divs, 1.0f, 1.0f)));
			// System.out.println("Color("+((i/divs)+0.5f)+","+1.0f+","+1.0f+")");
			values.add(new Color(Color.HSBtoRGB((i/divs)+0.5f, 1.0f, 1.0f)));
		}
		return values;
	}

}
