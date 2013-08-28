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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.command.CyCommandException;
import cytoscape.layout.Tunable;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import cytoscape.view.CyNetworkView;

import nodeCharts.command.ValueUtils;
import nodeCharts.view.ViewUtils.TextAlignment;

/**
 * The PieChart creates a list of custom graphics where each custom graphic represents
 * a slice of the pie.  The data for this is of the format: label1:value1:color1, etc.,
 * where value is numeric and the color is optional, but if specified, it must be one of
 * the named Java colors, hex RGB values, or hex RGBA values.
 */
public class PieChart implements NodeChartViewer {
	private static final String COLORS = "colorlist";
	// TODO
	private static final String LABELFONT = "labelfont";
	private static final String LABELSTYLE = "labelstyle";
	private static final String LABELSIZE = "labelsize";
	private static final String LABELOFFSET = "labeloffset";
	private static final String SORTSLICES = "sortslices";
	private static final String MINIMUMSLICE = "minimumslice";
	private static final String ARCSTART = "arcstart";

	public String getName() {
		return "pie";
	}

	public String getDescription() {
		return "Display the values passed as arguments as a pie chart on the node";
	}

	public Map<String,String> getOptions() {
		Map<String,String> options = new HashMap<String,String>();
		options.put(COLORS,"");
		options.put(ARCSTART,"0.0");
		options.put(LABELFONT, "");
		options.put(LABELSTYLE, "plain");
		options.put(LABELSIZE, "8");
		options.put(MINIMUMSLICE, "5.0");
		options.put(SORTSLICES, "true");
		return options;
	}

	public List<CustomGraphic> getCustomGraphics(Map<String, Object>args, List<Double> values, List<String> labels,
	                                             Rectangle2D bbox, CyNetworkView view) 
	                                                                               throws CyCommandException {
		// System.out.println("Getting pie custom graphics");
		// Get our colors
		List<Color> colors = ValueUtils.convertInputToColor(args.get(COLORS), values);

		// System.out.println("Got colors");

		// Handle our options
		double minimumSlice = 2.0;
		if (args.containsKey(MINIMUMSLICE))
			minimumSlice = ValueUtils.getDoubleValue(args.get(MINIMUMSLICE));

		// System.out.println("Got minimum slice");

		int labelSize = 4;
		if (args.containsKey(LABELSIZE))
			labelSize = ValueUtils.getIntegerValue(args.get(LABELSIZE));

		// System.out.println("Got label size");
		boolean sortSlices = true;
		if (args.containsKey(SORTSLICES))
			sortSlices = ValueUtils.getBooleanValue(args.get(SORTSLICES));

		// Get our angular offset
		double arcStart = 0.0;
		Object startObj = args.get(ARCSTART);
		if (startObj != null) {
			// System.out.println("Getting arc start");
			try {
				arcStart = ValueUtils.getDoubleValue(startObj);
			} catch (NumberFormatException e) {
				// System.out.println("Number format exception");
				throw new CyCommandException("arcstart must be a number: "+e.getMessage());
			}
		}
		// System.out.println("Got options");

		// Convert our data from values to increments
		values= convertData(values);
		// System.out.println("Got values");

		// Sanity check
		if (labels != null && labels.size() > 0 && 
		    (labels.size() != values.size() ||
		     labels.size() != colors.size()))
			throw new CyCommandException("number of labels ("+labels.size()+"), values ("+
			                             values.size()+"), and colors ("+colors.size()+") don't match");
		// System.out.println("Sanity check OK");

		if (sortSlices)
			sortSlicesBySize(values, colors, labels, minimumSlice);

		int nSlices = values.size();
		List<CustomGraphic> cgList = new ArrayList<CustomGraphic>();
		List<CustomGraphic> labelList = new ArrayList<CustomGraphic>();

		for (int slice = 0; slice < nSlices; slice++) {
			String label = null;
			if (labels != null && labels.size() > 0)
				label = labels.get(slice);
			if (values.get(slice) == 0.0) continue;
			// System.out.println("Slice "+slice+" label: "+label+" value = "+values.get(slice)+" color = "+colors.get(slice));
			CustomGraphic[] cg = createSlice(bbox, arcStart, values.get(slice), label, colors.get(slice), view, labelSize);
			cgList.add(cg[0]);
			if (cg[1] != null)
				labelList.add(cg[1]);
			arcStart += values.get(slice).doubleValue();
		}

		if (labelList != null && labelList.size() > 0)
			cgList.addAll(labelList);
		return cgList;
	}

	private List<Double> convertData(List<Double> values) {

		double totalSize = 0.0;
		int nValues = values.size();
		for (Double d: values) {
			totalSize += d.doubleValue();
		}

		// Now we have an array of doubles, but we need to convert them to degree offsets
		for (int index = 0; index < nValues; index++) {
			double v = values.get(index).doubleValue();
			values.set(index, v*360.0/totalSize);
		}
		return values;
	}

	private CustomGraphic[] createSlice(Rectangle2D bbox, double arcStart, Double arc, String label, Color color,
	                                    CyNetworkView view, int fontSize) {
		CustomGraphic[] vals = new CustomGraphic[2];

		// System.out.println("Creating arc from "+arcStart+" to "+(arc.doubleValue()+arcStart)+" with color: "+color);
		double x = bbox.getX();
		double y = bbox.getY();
		double width = bbox.getWidth();
		double height = bbox.getHeight();
		// Create the slice
		Arc2D slice = new Arc2D.Double(x, y, width, height, arcStart, arc, Arc2D.PIE);

		// Create the paint factory
		PaintFactory pf = new DefaultPaintFactory(color);
		vals[0] = new CustomGraphic(slice, pf);

		double midpointAngle = arcStart + arc.doubleValue()/2;

		TextAlignment tAlign = getLabelAlignment(midpointAngle);
		
		vals[1] = null;
		if (label != null) {
			// create the label
			Shape textShape = ViewUtils.getLabelShape(label, null, 0, fontSize, view);

			// Now, position the label.  Put the label on the outer edge of the circle.
			Point2D labelPosition = getLabelPosition(bbox, midpointAngle, 1.7);

			// System.out.println("label position = "+labelPosition);

			// vals[1] = ViewUtils.getLabelCustomGraphic(label, null, 0, 0, labelPosition, tAlign, view);
			textShape = ViewUtils.positionLabel(textShape, labelPosition, tAlign, 0.0, 0.0, 0.0);
			if (textShape != null) {
				// Draw a line between our label and the slice
				labelPosition = getLabelPosition(bbox, midpointAngle, 1.0);
				Shape labelLine = ViewUtils.getLabelLine(textShape.getBounds2D(), labelPosition, tAlign);

				// Combine the shapes
				Area textArea = new Area(textShape);
				textArea.add(new Area(labelLine));

				vals[1] = new CustomGraphic(textArea, new DefaultPaintFactory(Color.BLACK));
			}
		}

		return vals;
	}

	// Return a point on the midpoint of the arc
	private Point2D getLabelPosition(Rectangle2D bbox, double angle, double scale) {
		double midpoint = Math.toRadians(360.0-angle);
		double w = bbox.getWidth()/2*scale;
		double h = bbox.getHeight()/2*scale;
		double x, y;
		// Special case 90 and 270
		if (angle == 270.0) {
			x = 0.0;
			y = h;
		} else if (angle == 90.0) {
			x = 0.0;
			y = -h;
		} else {
			x = Math.cos(midpoint)*w;
			y = Math.sin(midpoint)*h;
		}

		return new Point2D.Double(x, y);
	}

	private TextAlignment getLabelAlignment(double midPointAngle) {
		if (midPointAngle >= 280.0 && midPointAngle < 80.0)
			return TextAlignment.ALIGN_LEFT;

		if (midPointAngle >= 80.0 && midPointAngle < 100.0)
			return TextAlignment.ALIGN_CENTER_TOP;

		if (midPointAngle >= 100.0 && midPointAngle < 260.0)
			return TextAlignment.ALIGN_RIGHT;

		if (midPointAngle >= 260.0 && midPointAngle < 280.0)
			return TextAlignment.ALIGN_CENTER_BOTTOM;

		return TextAlignment.ALIGN_LEFT;
	}

	private void sortSlicesBySize(List<Double>values, List<Color>colors, List<String>labels, double minimumSlice) {
		boolean haveLabels = false;
		Double[] valueArray = values.toArray(new Double[1]);
		values.clear();
		Color[] colorArray = colors.toArray(new Color[1]);
		colors.clear();
		String[] labelArray = null;
		if (labels != null && labels.size() > 0) {
			labelArray = labels.toArray(new String[1]);
			labels.clear();
			haveLabels = true;
		}
		
		Integer[] sortedIndex = new Integer[valueArray.length];
		for (int i = 0; i < valueArray.length; i++) sortedIndex[i] = new Integer(i);
		IndexComparator iCompare = new IndexComparator(valueArray);
		Arrays.sort(sortedIndex, iCompare);

		double otherValues = 0.0;
		
		// index now has the values in sorted order
		for (int index = valueArray.length-1; index >= 0; index--) {
			if (valueArray[sortedIndex[index]] >= minimumSlice) {
				values.add(valueArray[sortedIndex[index]]);
				colors.add(colorArray[sortedIndex[index]]);
				if (haveLabels)
					labels.add(labelArray[sortedIndex[index]]);
			} else {
				otherValues = otherValues + valueArray[sortedIndex[index]];
			}
		}

		if (otherValues > 0.0) {
			values.add(otherValues);
			colors.add(Color.LIGHT_GRAY);
			if (haveLabels)
				labels.add("Other");
		}
	}

  private class IndexComparator implements Comparator<Integer> {
    Double[] data = null;
    Integer[] intData = null;

    public IndexComparator(Double[] data) { this.data = data; }

    public IndexComparator(Integer[] data) { this.intData = data; }

    public int compare(Integer o1, Integer o2) {
      if (data != null) {
        if (data[o1.intValue()] < data[o2.intValue()]) return -1;
        if (data[o1.intValue()] > data[o2.intValue()]) return 1;
        return 0;
      } else if (intData != null) {
        if (intData[o1.intValue()] < intData[o2.intValue()]) return -1;
        if (intData[o1.intValue()] > intData[o2.intValue()]) return 1;
        return 0;
      }
      return 0;
    }
	}

}
