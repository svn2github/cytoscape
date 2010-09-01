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

import java.awt.geom.Rectangle2D;

// System imports
import java.util.List;
import java.util.Map;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import cytoscape.render.stateful.CustomGraphic;

import ding.view.DGraphView;
import ding.view.DNodeView;
import giny.view.NodeView;

/**
 * The NodeChartViewer creates the actual custom graphics
 */
public class ViewUtils {
	public static void addCustomGraphics(List<CustomGraphic> cgList, CyNode node, CyNetworkView view) {
		// Get the DNodeView
		NodeView nView = view.getNodeView(node);
		for (CustomGraphic cg: cgList) {
			((DNodeView)nView).addCustomGraphic(cg);
		}
	}

	public static Rectangle2D getNodeBoundingBox(CyNode node, CyNetworkView view) {
		DNodeView nView = (DNodeView)view.getNodeView(node);

		// Get the affine transform 
		double height = (nView.getHeight()-nView.getBorderWidth())*0.90;
		double width = (nView.getWidth()-nView.getBorderWidth())*0.90;

		// Create the bounding box.
		return new Rectangle2D.Double(-width/2, -height/2, width, height);
	}
}
