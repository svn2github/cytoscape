/*
  File: SelectFilterTest.java

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
import cytoscape.data.SelectEventListener;
import cytoscape.data.SelectFilter;

import giny.model.*;

import junit.framework.*;

import java.io.*;

import java.util.*;


/**
 *
 */
public class SelectFilterTest extends TestCase {
	SelectFilter filter;
	Node node1;
	Node node2;
	Node otherNode;
	Edge edge1;
	Edge edge2;
	Edge otherEdge;
	GraphPerspective gp;
	TestListener listener;
	SelectEvent savedEvent;

	/**
	 * Creates a new SelectFilterTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public SelectFilterTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
		RootGraph rootGraph = Cytoscape.getRootGraph();
		node1 = rootGraph.getNode(rootGraph.createNode());
		node2 = rootGraph.getNode(rootGraph.createNode());
		edge1 = rootGraph.getEdge(rootGraph.createEdge(node1, node2));
		edge2 = rootGraph.getEdge(rootGraph.createEdge(node2, node1));

		Node[] nodeArray = { node1, node2 };
		Edge[] edgeArray = { edge1, edge2 };
		gp = rootGraph.createGraphPerspective(nodeArray, edgeArray);
		//some objects not in this GraphPerspective
		otherNode = rootGraph.getNode(rootGraph.createNode());
		otherEdge = rootGraph.getEdge(rootGraph.createEdge(node1, otherNode));
		filter = new SelectFilter(gp);
		listener = new TestListener();
		filter.addSelectEventListener(listener);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 * Checks that the current state of the filter matches the state described
	 * by the arguments.
	 */
	public void checkState(boolean n1, boolean n2, boolean e1, boolean e2) {
		assertTrue(filter.isSelected(node1) == n1);
		assertTrue(filter.isSelected(node2) == n2);
		assertTrue(filter.isSelected(edge1) == e1);
		assertTrue(filter.isSelected(edge2) == e2);
		assertTrue(filter.passesFilter(node1) == n1);
		assertTrue(filter.passesFilter(node2) == n2);
		assertTrue(filter.passesFilter(edge1) == e1);
		assertTrue(filter.passesFilter(edge2) == e2);

		if (n1) {
			assertTrue(filter.getSelectedNodes().contains(node1));
		}

		if (n2) {
			assertTrue(filter.getSelectedNodes().contains(node2));
		}

		if (e1) {
			assertTrue(filter.getSelectedEdges().contains(edge1));
		}

		if (e2) {
			assertTrue(filter.getSelectedEdges().contains(edge2));
		}

		int nodeCount = 0;

		if (n1) {
			nodeCount++;
		}

		if (n2) {
			nodeCount++;
		}

		assertTrue(filter.getSelectedNodes().size() == nodeCount);

		int edgeCount = 0;

		if (e1) {
			edgeCount++;
		}

		if (e2) {
			edgeCount++;
		}

		assertTrue(filter.getSelectedEdges().size() == edgeCount);
	}

	/**
	 * Checks that the most recently fired event matches the supplied arguments.
	 */
	public void checkEvent(Object target, int targetType, boolean flagOn) {
		SelectEvent event = listener.getEvent();
		assertTrue(event.getSource() == filter);
		assertTrue(event.getTarget() == target);
		assertTrue(event.getTargetType() == targetType);
		assertTrue(event.getEventType() == flagOn);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testSingleFlags() throws Exception {
		checkState(false, false, false, false);

		filter.setSelected(node1, true);
		checkState(true, false, false, false);
		checkEvent(node1, SelectEvent.SINGLE_NODE, true);

		filter.setSelected(edge1, true);
		checkState(true, false, true, false);
		checkEvent(edge1, SelectEvent.SINGLE_EDGE, true);

		filter.setSelected(node2, true);
		checkState(true, true, true, false);
		checkEvent(node2, SelectEvent.SINGLE_NODE, true);

		filter.setSelected(edge2, true);
		checkState(true, true, true, true);
		checkEvent(edge2, SelectEvent.SINGLE_EDGE, true);

		filter.setSelected(edge1, false);
		checkState(true, true, false, true);
		checkEvent(edge1, SelectEvent.SINGLE_EDGE, false);

		filter.setSelected(node1, false);
		checkState(false, true, false, true);
		checkEvent(node1, SelectEvent.SINGLE_NODE, false);

		savedEvent = listener.getEvent();
		filter.setSelected(edge2, true); //should do nothing
		checkState(false, true, false, true);
		assertTrue(listener.getEvent() == savedEvent); //no event should have been fired
		filter.setSelected(node2, true); //should do nothing
		checkState(false, true, false, true);
		assertTrue(listener.getEvent() == savedEvent); //no event should have been fired

		filter.setSelected(edge2, false);
		checkState(false, true, false, false);
		checkEvent(edge2, SelectEvent.SINGLE_EDGE, false);

		filter.setSelected(node2, false);
		checkState(false, false, false, false);
		checkEvent(node2, SelectEvent.SINGLE_NODE, false);

		savedEvent = listener.getEvent();
		filter.setSelected(node1, false); //should do nothing
		checkState(false, false, false, false);
		assertTrue(listener.getEvent() == savedEvent); //no event should have been fired
		filter.setSelected(edge1, false); //should do nothing
		checkState(false, false, false, false);
		assertTrue(listener.getEvent() == savedEvent); //no event should have been fired

		//test objects not in this perspective
		/* these tests embargoed due to a bug in GraphPerspective.containsNode
		filter.setSelected(otherNode, true); //should do nothing
		checkState(false, false, false, false);
		assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
		filter.setSelected(otherNode, false); //should do nothing
		checkState(false, false, false, false);
		assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
		filter.setSelected(otherEdge, true); //should do nothing
		checkState(false, false, false, false);
		assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
		filter.setSelected(otherEdge, false); //should do nothing
		checkState(false, false, false, false);
		assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
		*/
	} // testCtor

	/**
	 *  DOCUMENT ME!
	 */
	public void testGroupFlags() {
		Set testSet = null; //return value from filter methods
		checkState(false, false, false, false);

		Set nodeSet1 = new HashSet();
		nodeSet1.add(node1);
		testSet = filter.setSelectedNodes(nodeSet1, true);
		checkState(true, false, false, false);
		checkEvent(testSet, SelectEvent.NODE_SET, true);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(node1));

		Set edgeSet1 = new HashSet();
		edgeSet1.add(edge2);
		testSet = filter.setSelectedEdges(edgeSet1, true);
		checkState(true, false, false, true);
		checkEvent(testSet, SelectEvent.EDGE_SET, true);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(edge2));

		Set nodeSet2 = new HashSet();
		nodeSet2.add(node1);
		nodeSet2.add(node2);
		testSet = filter.setSelectedNodes(nodeSet2, true);
		checkState(true, true, false, true);
		checkEvent(testSet, SelectEvent.NODE_SET, true);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(node2));

		Set edgeSet2 = new HashSet();
		edgeSet2.add(edge1);
		edgeSet2.add(edge2);
		testSet = filter.setSelectedEdges(edgeSet2, true);
		checkState(true, true, true, true);
		checkEvent(testSet, SelectEvent.EDGE_SET, true);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(edge1));

		testSet = filter.setSelectedNodes(nodeSet1, false);
		checkState(false, true, true, true);
		checkEvent(testSet, SelectEvent.NODE_SET, false);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(node1));

		testSet = filter.setSelectedEdges(edgeSet1, false);
		checkState(false, true, true, false);
		checkEvent(testSet, SelectEvent.EDGE_SET, false);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(edge2));

		testSet = filter.setSelectedNodes(nodeSet2, false);
		checkState(false, false, true, false);
		checkEvent(testSet, SelectEvent.NODE_SET, false);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(node2));

		testSet = filter.setSelectedEdges(edgeSet2, false);
		checkState(false, false, false, false);
		checkEvent(testSet, SelectEvent.EDGE_SET, false);
		assertTrue(testSet.size() == 1);
		assertTrue(testSet.contains(edge1));

		testSet = filter.setSelectedNodes(nodeSet2, true);
		checkState(true, true, false, false);
		checkEvent(testSet, SelectEvent.NODE_SET, true);
		assertTrue(testSet.size() == 2);
		assertTrue(testSet.contains(node1));
		assertTrue(testSet.contains(node2));

		testSet = filter.setSelectedNodes(nodeSet2, false);
		checkState(false, false, false, false);
		checkEvent(testSet, SelectEvent.NODE_SET, false);
		assertTrue(testSet.size() == 2);
		assertTrue(testSet.contains(node1));
		assertTrue(testSet.contains(node2));

		testSet = filter.setSelectedEdges(edgeSet2, true);
		checkState(false, false, true, true);
		checkEvent(testSet, SelectEvent.EDGE_SET, true);
		assertTrue(testSet.size() == 2);
		assertTrue(testSet.contains(edge1));
		assertTrue(testSet.contains(edge2));

		testSet = filter.setSelectedEdges(edgeSet2, false);
		checkState(false, false, false, false);
		checkEvent(testSet, SelectEvent.EDGE_SET, false);
		assertTrue(testSet.size() == 2);
		assertTrue(testSet.contains(edge1));
		assertTrue(testSet.contains(edge2));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testFlagAll() {
		checkState(false, false, false, false);

		filter.selectAllNodes();
		checkState(true, true, false, false);
		checkSelectAllEvent(2, SelectEvent.NODE_SET, true);
		filter.unselectAllNodes();
		checkState(false, false, false, false);
		checkSelectAllEvent(2, SelectEvent.NODE_SET, false);
		filter.selectAllEdges();
		checkState(false, false, true, true);
		checkSelectAllEvent(2, SelectEvent.EDGE_SET, true);
		filter.unselectAllEdges();
		checkState(false, false, false, false);
		checkSelectAllEvent(2, SelectEvent.EDGE_SET, false);

		filter.setSelected(node1, true);
		checkState(true, false, false, false);
		filter.selectAllNodes();
		checkState(true, true, false, false);
		checkSelectAllEvent(1, SelectEvent.NODE_SET, true);
		filter.setSelected(node1, false);
		checkState(false, true, false, false);
		filter.unselectAllNodes();
		checkState(false, false, false, false);
		checkSelectAllEvent(1, SelectEvent.NODE_SET, false);

		filter.setSelected(edge1, true);
		checkState(false, false, true, false);
		filter.selectAllEdges();
		checkState(false, false, true, true);
		checkSelectAllEvent(1, SelectEvent.EDGE_SET, true);
		filter.setSelected(edge1, false);
		checkState(false, false, false, true);
		filter.unselectAllEdges();
		checkState(false, false, false, false);
		checkSelectAllEvent(1, SelectEvent.EDGE_SET, false);
	}

	/**
	 * The event fired in response to a flagAll or unflagAll method call contains
	 * a Set that we don't have a reference to. This method tests the contents of
	 * that Set in addition to the other event parameters.
	 */
	public void checkSelectAllEvent(int setSize, int targetType, boolean flagOn) {

		//  SelectEvent event = listener.getEvent();
		//     assertTrue( event.getSource() == filter );
		//     assertTrue( event.getTarget() instanceof Set );
		//     Set targetSet = (Set)event.getTarget();
		//     assertTrue( targetSet.size() == setSize );
		//     assertTrue( event.getTargetType() == targetType );
		//     assertTrue( event.getEventType() == flagOn );
	}

	/**
	 * Test the add and remove listener methods, as well as responding to object
	 * removal in the underlying GraphPerspective.
	 */
	public void testListeners() {
		checkState(false, false, false, false);
		filter.setSelected(node1, true);
		checkEvent(node1, SelectEvent.SINGLE_NODE, true);
		savedEvent = listener.getEvent();
		filter.removeSelectEventListener(listener);
		filter.setSelected(node1, false);
		//this should be the same event since the listener is detached
		assertTrue(listener.getEvent() == savedEvent);
		checkEvent(node1, SelectEvent.SINGLE_NODE, true);
		filter.setSelected(edge1, true);
		assertTrue(listener.getEvent() == savedEvent);
		checkEvent(node1, SelectEvent.SINGLE_NODE, true);
		filter.addSelectEventListener(listener);
		filter.setSelected(edge1, false);
		assertTrue(listener.getEvent() != savedEvent);
		checkEvent(edge1, SelectEvent.SINGLE_EDGE, false);

		checkState(false, false, false, false);
		filter.selectAllNodes();
		filter.selectAllEdges();
		checkState(true, true, true, true);
		gp.hideEdge(edge1);
		checkState(true, true, false, true);
		checkSelectAllEvent(1, SelectEvent.EDGE_SET, false);
		savedEvent = listener.getEvent();
		gp.restoreEdge(edge1); //shouldn't change flagged state or fire an event
		checkState(true, true, false, true);
		assertTrue(listener.getEvent() == savedEvent);
		filter.unselectAllNodes();
		filter.unselectAllEdges();

		checkState(false, false, false, false);
		filter.selectAllNodes();
		filter.selectAllEdges();
		checkState(true, true, true, true);
		gp.hideNode(node1); //implicitly hides both edges
		checkState(false, true, false, false);
		//two events get fired, we only catch the second one for the edges
		checkSelectAllEvent(2, SelectEvent.EDGE_SET, false);
		savedEvent = listener.getEvent();
		gp.restoreNode(node1); //shouldn't change flagged state or fire an event
		checkState(false, true, false, false);
		assertTrue(listener.getEvent() == savedEvent);
		filter.unselectAllNodes();
		filter.unselectAllEdges();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(SelectFilterTest.class));
	}

	private class TestListener implements SelectEventListener {
		private SelectEvent event;

		public void onSelectEvent(SelectEvent newEvent) {
			event = newEvent;
		}

		public SelectEvent getEvent() {
			return event;
		}
	}
}
