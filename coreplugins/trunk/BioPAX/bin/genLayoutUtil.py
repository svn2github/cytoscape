#! /usr/bin/python

# imports
import sys
import string

# setup some globals
if len(sys.argv) < 3:
    sys.exit("usage: ./genLayoutUtil <gen yfiles> <layout file to generate>")
else:
    HAS_YFILES_LIBRARY = string.atoi(sys.argv[1])
    LAYOUT_UTIL_FILE = sys.argv[2]

# open layout file
layoutUtilFile = open(LAYOUT_UTIL_FILE, 'w')

# write out the header
print >> layoutUtilFile, """//
// WARNING - THIS FILE IS AUTO-GENERATED - ANY CHANGES YOU MAKE HERE WILL BE LOST
//
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.util.cytoscape;
"""

# write out imports
print >> layoutUtilFile, """// imports
import giny.view.NodeView;
import java.util.Iterator;
import cytoscape.Cytoscape;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutProperties;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JPanel;
"""

# include needed jars for yFiles use - if necessary
if HAS_YFILES_LIBRARY == 1:
    print >> layoutUtilFile, """import giny.view.EdgeView;
import giny.model.GraphPerspective;
import cern.colt.map.PrimeFinder;
import y.view.Graph2D;
import y.base.NodeCursor;
import y.base.EdgeCursor;
import y.geom.YPoint;
import y.layout.Layouter;
import y.layout.NodeLayout;
import y.layout.EdgeLayout;
import y.layout.organic.OrganicLayouter;
import java.util.Map;
import java.util.HashMap;
import java.awt.geom.Point2D;
"""

# write out class definition
print >> layoutUtilFile, """/**
 * This class implements CyLayoutAlgorithim.  Used to layout BioPAX graphs.
 *
 * @author Benjamin Gross
 */
public class LayoutUtil implements CyLayoutAlgorithm {
"""

if HAS_YFILES_LIBRARY == 1:
    print >> layoutUtilFile, """    /**
     * Ref to yFiles layout class.
     */
    private static Layouter layouter = new OrganicLayouter();
    static { ((OrganicLayouter)layouter).setActivateDeterministicMode(true); }
"""

print >> layoutUtilFile, """    /**
     * Our implementation of LayoutAlgorithm.doLayout().
     */
    public void doLayout() {
        doLayout(Cytoscape.getCurrentNetworkView(), null);
    }

	/**
	 * Our implementation of LayoutAlgorithm.doLayout(..).
	 */
	public void doLayout(CyNetworkView networkView) {
	    doLayout(networkView, null);
    }

	/**
	 * Our implementation of LayoutAlgorithm.doLayout(..,..).
	 */
	public void doLayout(CyNetworkView networkView, TaskMonitor monitor) {
"""

# if yfiles, method body is organic layout, else grid layout
if HAS_YFILES_LIBRARY == 1:
    print >> layoutUtilFile, """		GraphPerspective perspective = networkView.getGraphPerspective();
		Map y_giny_node_map = new HashMap(PrimeFinder.nextPrime(perspective.getNodeCount()));
		Map giny_y_node_map = new HashMap(PrimeFinder.nextPrime(perspective.getNodeCount()));
		Map y_giny_edge_map = new HashMap(PrimeFinder.nextPrime(perspective.getEdgeCount()));

		Iterator node_iterator = perspective.nodesIterator();
		Iterator edge_iterator = perspective.edgesIterator();

		Graph2D graph2d = new Graph2D();

		while (node_iterator.hasNext()) {
			giny.model.Node giny = (giny.model.Node) node_iterator.next();
			NodeView node_view = networkView.getNodeView(giny);
			y.base.Node yfiles = graph2d.createNode(node_view.getXPosition(),
			                                        node_view.getYPosition());

			y_giny_node_map.put(yfiles, giny);
			giny_y_node_map.put(giny, yfiles);
		}

		while (edge_iterator.hasNext()) {
			giny.model.Edge giny = (giny.model.Edge) edge_iterator.next();
			y.base.Edge yfiles = graph2d.createEdge((y.base.Node) giny_y_node_map.get(giny.getSource()),
			                                        (y.base.Node) giny_y_node_map.get(giny.getTarget()));

			y_giny_edge_map.put(yfiles, giny);
		}

		layouter.doLayout(graph2d);

		NodeCursor nc = graph2d.nodes();
		EdgeCursor ec = graph2d.edges();

		for (int i = 0; i < nc.size(); ++i) {
			y.base.Node yfiles = nc.node();
			giny.model.Node giny = (giny.model.Node) y_giny_node_map.get(yfiles);

			NodeView node_view = networkView.getNodeView(giny);
			NodeLayout node_layout = graph2d.getLayout(yfiles);
			node_view.setXPosition(node_layout.getX(), false);
			node_view.setYPosition(node_layout.getY(), false);
			// animation
			node_view.setNodePosition(true);
			nc.next();
		}

		for (int i = 0; i < ec.size(); ++i) {
			y.base.Edge yfiles = ec.edge();
			giny.model.Edge giny = (giny.model.Edge) y_giny_edge_map.get(yfiles);

			EdgeView edge_view = networkView.getEdgeView(giny);
			EdgeLayout edge_layout = graph2d.getLayout(yfiles);

			edge_view.clearBends();

			giny.view.Bend bend = edge_view.getBend();

			// Otherwise it's a self-edge.
			if (giny.getSource().getRootGraphIndex() != giny.getTarget().getRootGraphIndex()) {

				for (int p = edge_layout.pointCount() - 1; p >= 0; --p) {
					YPoint point = edge_layout.getPoint(p);
					bend.addHandle(new Point2D.Double(point.getX() - 15, point.getY() - 15));
				}
			}

			ec.next();
		}

		System.gc();
    }"""

# yfiles does not exist, we default to grid
else:
    print >> layoutUtilFile, """		double distanceBetweenNodes = 50.0d;
		int columns = (int) Math.sqrt(networkView.nodeCount());
		Iterator nodeViews = networkView.getNodeViewsIterator();
		double currX = 0.0d;
		double currY = 0.0d;
		int count = 0;

		while (nodeViews.hasNext()) {
			NodeView nView = (NodeView) nodeViews.next();
			nView.setOffset(currX, currY);
			count++;

			if (count == columns) {
				count = 0;
				currX = 0.0d;
				currY += distanceBetweenNodes;
			} else {
				currX += distanceBetweenNodes;
			}
		}
    }
"""

print >> layoutUtilFile, """	/**
	 * Our implementation of LayoutAlgorithm.supportsSelectedOnly().
	 */
	public boolean supportsSelectedOnly() {
        return false;
    }

	/**
	 * Our implementation of LayoutAlgorithm.setSelectedOnly(..).
	 */
	public void setSelectedOnly(boolean selectedOnly) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.supportsNodeAttributes().
	 */
	public byte[] supportsNodeAttributes() {
	    return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.supportsEdgeAttributes().
	 */
	public byte[] supportsEdgeAttributes() {
	    return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.setLayoutAttribute(..).
	 */
	public void setLayoutAttribute(String attributeName) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.getInitialAttributeList().
	 */
	public List<String> getInitialAttributeList() {
	    return new ArrayList<String>();
    }

	/**
	 * Our implementation of LayoutAlgorithm.getSettingsPanel().
	 */
	public JPanel getSettingsPanel() {
	    return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.revertSettings().
	 */
	public void revertSettings() {
    }

	/**
	 * Our implementation of LayoutAlgorithm.updateSettings().
	 */
	public void updateSettings() {
    }

	/**
	 * Our implementation of LayoutAlgorithm.getSettings().
	 */
	public LayoutProperties getSettings() {
		return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.getName().
	 */
	public String getName() {
	    return "BioPax Plugin Layout Algorithm";
    }

	/**
	 * Our implementation of LayoutAlgorithm.lockNodes(..).
	 */
	public void lockNodes(NodeView[] nodes) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.lockNode(..).
	 */
	public void lockNode(NodeView v) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.unlockNode(..).
	 */
	public void unlockNode(NodeView v) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.unlockAllNodes().
	 */
	public void unlockAllNodes() {
    }

	/**
	 * Our implementation of LayoutAlgorithm.halt().
	 */
	public void halt() {
    }
}
"""

# outta here
layoutUtilFile.close()
