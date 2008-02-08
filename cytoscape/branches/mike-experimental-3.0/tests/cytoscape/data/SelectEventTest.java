/*
  File: FlagEventTest.java

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
package cytoscape.data;

import cytoscape.AllTests;
import cytoscape.Cytoscape;

import cytoscape.data.SelectEvent;
import cytoscape.data.SelectFilter;

import cytoscape.*;

import junit.framework.*;

import java.io.*;

import java.util.*;


/**
 *
 */
public class SelectEventTest extends TestCase {
	/**
	 * Creates a new SelectEventTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public SelectEventTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 * This method makes sure that all of the static contants defined in the class
	 * have different values.
	 */
	public void testConstants() throws Exception {
		assertTrue(SelectEvent.SINGLE_NODE != SelectEvent.SINGLE_EDGE);
		assertTrue(SelectEvent.SINGLE_NODE != SelectEvent.NODE_SET);
		assertTrue(SelectEvent.SINGLE_NODE != SelectEvent.EDGE_SET);
		assertTrue(SelectEvent.SINGLE_EDGE != SelectEvent.NODE_SET);
		assertTrue(SelectEvent.SINGLE_EDGE != SelectEvent.EDGE_SET);
		assertTrue(SelectEvent.NODE_SET != SelectEvent.EDGE_SET);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testCtor() throws Exception {
		RootGraph rootGraph = Cytoscape.getRootGraph();
		Node node1 = rootGraph.getNode(rootGraph.createNode());
		Node node2 = rootGraph.getNode(rootGraph.createNode());
		Edge edge1 = rootGraph.getEdge(rootGraph.createEdge(node1, node2));
		Edge edge2 = rootGraph.getEdge(rootGraph.createEdge(node2, node1));
		Node[] nodeArray = { node1, node2 };
		Edge[] edgeArray = { edge1, edge2 };
		GraphPerspective gp = rootGraph.createGraphPerspective(nodeArray, edgeArray);
		SelectFilter source = new SelectFilter(gp);
		Set nodeSet = new HashSet();
		nodeSet.add(node1);
		nodeSet.add(node2);

		Set edgeSet = new HashSet();
		edgeSet.add(edge1);
		edgeSet.add(edge2);

		SelectEvent singleNodeOn = new SelectEvent(source, node1, true);
		checkEvent(singleNodeOn, source, node1, SelectEvent.SINGLE_NODE, true);

		SelectEvent singleNodeOff = new SelectEvent(source, node2, false);
		checkEvent(singleNodeOff, source, node2, SelectEvent.SINGLE_NODE, false);

		SelectEvent singleEdgeOn = new SelectEvent(source, edge1, true);
		checkEvent(singleEdgeOn, source, edge1, SelectEvent.SINGLE_EDGE, true);

		SelectEvent singleEdgeOff = new SelectEvent(source, edge2, false);
		checkEvent(singleEdgeOff, source, edge2, SelectEvent.SINGLE_EDGE, false);

		SelectEvent nodeSetOn = new SelectEvent(source, nodeSet, true);
		checkEvent(nodeSetOn, source, nodeSet, SelectEvent.NODE_SET, true);

		SelectEvent nodeSetOff = new SelectEvent(source, nodeSet, false);
		checkEvent(nodeSetOff, source, nodeSet, SelectEvent.NODE_SET, false);

		SelectEvent edgeSetOn = new SelectEvent(source, edgeSet, true);
		checkEvent(edgeSetOn, source, edgeSet, SelectEvent.EDGE_SET, true);

		SelectEvent edgeSetOff = new SelectEvent(source, edgeSet, false);
		checkEvent(edgeSetOff, source, edgeSet, SelectEvent.EDGE_SET, false);
	} // testCtor

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 * @param source DOCUMENT ME!
	 * @param target DOCUMENT ME!
	 * @param targetType DOCUMENT ME!
	 * @param selectOn DOCUMENT ME!
	 */
	public void checkEvent(SelectEvent event, SelectFilter source, Object target, int targetType,
	                       boolean selectOn) {
		assertTrue(event.getSource() == source);
		assertTrue(event.getTarget() == target);
		assertTrue(event.getTargetType() == targetType);
		assertTrue(event.getEventType() == selectOn);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(SelectEventTest.class));
	}
}
