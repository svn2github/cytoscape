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
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nodeCharts.command.ValueUtils;
import nodeCharts.view.ViewUtils.TextAlignment;
import cytoscape.CyNode;
import cytoscape.command.CyCommandException;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import cytoscape.view.CyNetworkView;

/**
 * The StripeChart creates a list of custom graphics where each custom graphic
 * represents a stripe of an evenly divided, horizontally-oriented rectangle.
 * The data for this is a simple list of colors (named Java colors, hex RGB
 * values, or hex RGBA values).
 */
public class StripeChart implements NodeChartViewer {
	private static final String COLORS = "colorlist";
	// TODO
//	private static final String LABELFONT = "labelfont";
//	private static final String LABELSTYLE = "labelstyle";
//	private static final String LABELSIZE = "labelsize";
//	private static final String LABELOFFSET = "labeloffset";

	public String getName() {
		return "stripe";
	}

	public String getDescription() {
		return "Display the values passed as equally distributed stripes on the node";
	}

	public Map<String, String> getOptions() {
		Map<String, String> options = new HashMap<String, String>();
		options.put(COLORS, "");
		return options;
	}

	public List<CustomGraphic> getCustomGraphics(Map<String, Object> args, List<Double> values, List<String> labels, CyNode node,
			CyNetworkView view, Object position, double scale) throws CyCommandException {
		// Get our colors
		List<Color> colors = ValueUtils.convertInputToColor(args.get(COLORS), values);

		int nStripes = colors.size();
		
		List<CustomGraphic> cgList = new ArrayList<CustomGraphic>();

		// We need to get our bounding box in order to scale our graphic
		// properly
		Rectangle2D bbox = ViewUtils.getNodeBoundingBox(node, view, position, scale);

		// System.out.println("Node: "+node);

		for (int stripe = 0; stripe < nStripes; stripe++) {
			CustomGraphic cg = createStripe(bbox, stripe, nStripes, colors.get(stripe), view);
			cgList.add(cg);
		}

		return cgList;
	}


	private CustomGraphic createStripe(Rectangle2D bbox, int i, int n, Color color, CyNetworkView view) {
		CustomGraphic val;
		// System.out.println("Creating arc from "+arcStart+" to "+(arc.doubleValue()+arcStart)+" with color: "+color);
		double x = bbox.getX();
		double y = bbox.getY();
		double width = bbox.getWidth();
		double height = bbox.getHeight();
		// Create the stripe
		Rectangle2D stripe = new Rectangle2D.Double((x + (i * width/n)), y, width/n, height);

		// Create the paint factory
		PaintFactory pf = new DefaultPaintFactory(color);
		val = new CustomGraphic(stripe, pf);

		return val;
	}
}
