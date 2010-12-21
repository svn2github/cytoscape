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
		return options;
	}

	public List<CustomGraphic> getCustomGraphics(Map<String, Object>args, List<Double> values, List<String> labels,
	                                             CyNode node, CyNetworkView view, Object position) 
	                                                                               throws CyCommandException {
		// Get our colors
		List<Color> colors = ValueUtils.convertInputToColor(args.get(COLORS), values);

		// Get our angular offset
		double arcStart = 0.0;
		Object startObj = args.get(ARCSTART);
		if (startObj != null) {
			if (startObj instanceof Double)
				arcStart = ((Double)startObj).doubleValue();
			else if (startObj instanceof Integer)
				arcStart = ((Integer)startObj).doubleValue();
			else if (startObj instanceof String) {
				try {
					arcStart = Double.parseDouble((String)startObj);
				} catch (NumberFormatException e) {
					throw new CyCommandException("arcstart must be a number: "+e.getMessage());
				}
			}
		}

		// Convert our data from values to increments
		values= convertData(values);

		// Sanity check
		if (labels.size() != values.size() ||
		    labels.size() != colors.size())
			throw new CyCommandException("number of labels ("+labels.size()+"), values ("+
			                             values.size()+"), and colors ("+colors.size()+") don't match");

		int nSlices = labels.size();
		List<CustomGraphic> cgList = new ArrayList<CustomGraphic>();
		List<CustomGraphic> labelList = new ArrayList<CustomGraphic>();

		// We need to get our bounding box in order to scale our graphic properly
		Rectangle2D bbox = ViewUtils.getNodeBoundingBox(node, view, position);

		// System.out.println("Node: "+node);

		for (int slice = 0; slice < nSlices; slice++) {
			CustomGraphic[] cg = createSlice(bbox, arcStart, values.get(slice), labels.get(slice), colors.get(slice), view);
			cgList.add(cg[0]);
			if (cg[1] != null)
				labelList.add(cg[1]);
			arcStart += values.get(slice).doubleValue();
		}

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
	                                    CyNetworkView view) {
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
		
		// create the label
		Shape textShape = ViewUtils.getLabelShape(label, null, 0, 0, view);

		// Now, position the label.  Put the label on the outer edge of the circle.
		Point2D labelPosition = getLabelPosition(bbox, midpointAngle, 1.4);
		// vals[1] = ViewUtils.getLabelCustomGraphic(label, null, 0, 0, labelPosition, tAlign, view);
		textShape = ViewUtils.positionLabel(textShape, labelPosition, tAlign, 0.0, 0.0, 0.0);
		if (textShape == null) {
			vals[1] = null;
			return vals;
		}

		// Draw a line between our label and the slice
		labelPosition = getLabelPosition(bbox, midpointAngle, 1.0);
		Shape labelLine = ViewUtils.getLabelLine(textShape.getBounds2D(), labelPosition, tAlign);

		// Combine the shapes
		Area textArea = new Area(textShape);
		textArea.add(new Area(labelLine));


		vals[1] = new CustomGraphic(textArea, new DefaultPaintFactory(Color.BLACK));

		return vals;
	}

	// Return a point on the midpoint of the arc
	private Point2D getLabelPosition(Rectangle2D bbox, double angle, double scale) {
		double midpoint = Math.toRadians(360.0-angle);
		double length = bbox.getWidth()/2; // Assumes width = height!
		double x = Math.cos(midpoint)*length*scale + (bbox.getX()+bbox.getWidth()/2);
		double y = Math.sin(midpoint)*length*scale + (bbox.getY()+bbox.getHeight()/2);

		// System.out.println("getLabelPosition: bbox = "+bbox+", midpoint = "+angle+" arcpoint = ("+x+","+y+")");

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
}
