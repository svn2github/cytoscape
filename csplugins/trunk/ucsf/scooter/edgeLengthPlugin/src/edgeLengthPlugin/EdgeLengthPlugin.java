/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
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
package edgeLengthPlugin;

// System imports
import java.util.List;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;
import giny.view.NodeView;

/**
 * The EdgeLengthPlugin class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class EdgeLengthPlugin extends CytoscapePlugin implements ActionListener {
	public static final double VERSION = 1.0;
	public static final String LENGTH_ATTRIBUTE="length";

  /**
   * Create our action and add it to the plugins menu
   */
	public EdgeLengthPlugin() {
		// Create our main plugin menu
		JMenuItem menu = new JMenuItem("Calculate Edge Lengths");
		menu.addActionListener(this);

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

		System.out.println("Edge Length Plugin "+VERSION+" initialized");
	}

	public void actionPerformed(ActionEvent e) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// Get the list of edges
		List<CyEdge> edgeList = network.edgesList();
		for (CyEdge edge: edgeList) {
			CyNode source = (CyNode)edge.getSource();
			CyNode target = (CyNode)edge.getTarget();
			NodeView sourceView = networkView.getNodeView(source);
			Point2D.Double sourceLocation = 
				new Point2D.Double(sourceView.getXPosition(), sourceView.getYPosition());
			NodeView targetView = networkView.getNodeView(target);
			Point2D.Double targetLocation = 
				new Point2D.Double(targetView.getXPosition(), targetView.getYPosition());
			double dist = sourceLocation.distance(targetLocation);
			edgeAttributes.setAttribute(edge.getIdentifier(), LENGTH_ATTRIBUTE, new Double(dist));
		}
	}
}
