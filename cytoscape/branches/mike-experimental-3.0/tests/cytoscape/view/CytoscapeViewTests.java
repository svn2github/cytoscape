/*
  File: CytoscapeViewTests.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// $Revision$
// $Date$
// $Author$
package cytoscape.view;

import cytoscape.*;
import cytoscape.AllTests;

import cytoscape.view.CyNetworkView;

import cytoscape.*;

import giny.view.*;

import junit.framework.*;

import java.io.*;

import java.util.*;


/**
 *
 */
public class CytoscapeViewTests extends TestCase {
	GraphPerspective network;
	Node node1;
	Node node2;
	Edge edge1;
	Edge edge2;
	CyNetworkView view;
	NodeView nodeView1;
	NodeView nodeView2;
	EdgeView edgeView1;
	EdgeView edgeView2;

	/**
	 * Creates a new CytoscapeViewTests object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public CytoscapeViewTests(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
		node1 = Cytoscape.getCyNode("node1", true);
		node2 = Cytoscape.getCyNode("node2", true);
		edge1 = Cytoscape.getCyEdge("node1", "node1 (pp) node2", "node2", "pp");
		edge2 = Cytoscape.getCyEdge("node2", "node2 (pp) node1", "node1", "pp");

		int[] nodeArray = { node1.getRootGraphIndex(), node2.getRootGraphIndex() };
		int[] edgeArray = { edge1.getRootGraphIndex(), edge2.getRootGraphIndex() };
		network = Cytoscape.createNetwork(nodeArray, edgeArray, null);
		view = Cytoscape.createNetworkView(network);
		nodeView1 = view.getNodeView(node1);
		nodeView2 = view.getNodeView(node2);
		edgeView1 = view.getEdgeView(edge1);
		edgeView2 = view.getEdgeView(edge2);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 * Tests that the view is properly modified when the selectfilter is changed.
	 */
	public void testFilterToView() throws Exception {
		checkState(false, false, false, false);
		network.setSelectedNodeState(node1, true);
		checkState(true, false, false, false);
		network.setSelectedEdgeState(edge2, true);
		checkState(true, false, false, true);
		network.selectAllNodes();
		checkState(true, true, false, true);
		network.selectAllEdges();
		checkState(true, true, true, true);
		network.setSelectedNodeState(node2, false);
		checkState(true, false, true, true);
		network.setSelectedEdgeState(edge1, false);
		checkState(true, false, false, true);
		network.unselectAllEdges();
		checkState(true, false, false, false);
		network.unselectAllEdges();
		checkState(false, false, false, false);
	}

	/**
	 * Tests that the selectfilter is properly modified when the view is changed.
	 */
	public void testViewToFilter() throws Exception {
		checkState(false, false, false, false);
		nodeView1.setSelected(true);
		checkState(true, false, false, false);
		edgeView2.setSelected(true);
		checkState(true, false, false, true);
		nodeView2.setSelected(true);
		checkState(true, true, false, true);
		edgeView1.setSelected(true);
		checkState(true, true, true, true);
		nodeView2.setSelected(false);
		checkState(true, false, true, true);
		edgeView1.setSelected(false);
		checkState(true, false, false, true);
		edgeView2.setSelected(false);
		checkState(true, false, false, false);
		nodeView1.setSelected(false);
		checkState(false, false, false, false);
	}

	/**
	 * Checks that the current state of the filter and the view match the state
	 * defined by the arguments.
	 */
	public void checkState(boolean n1, boolean n2, boolean e1, boolean e2) {
		assertTrue(network.isSelected(node1) == n1);
		assertTrue(network.isSelected(node2) == n2);

		//assertTrue( network.isSelected(edge1) == e1 );
		//assertTrue( network.isSelected(edge2) == e2 );
		//assertTrue( nodeView1.isSelected() == n1 );
		// assertTrue( nodeView2.isSelected() == n2 );
		//assertTrue( edgeView1.isSelected() == e1 );
		//assertTrue( edgeView2.isSelected() == e2 );
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(CytoscapeViewTests.class));
		Cytoscape.exit(0);
	}

	//------------------------------------------------------------------------------
}
