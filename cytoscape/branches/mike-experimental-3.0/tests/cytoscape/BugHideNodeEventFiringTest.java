/* -*-Java-*-
********************************************************************************
*
* File:         BugHideNodeEventFiringTest.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Sat Dec 02 07:38:10 2006
* Modified:     Sat Dec 02 08:22:57 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape;

import cytoscape.Edge;
import cytoscape.GraphPerspective;
import cytoscape.Node;
import cytoscape.Cytoscape;

import cytoscape.data.Semantics;

import cytoscape.GraphPerspectiveChangeEvent;
import cytoscape.GraphPerspectiveChangeListener;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Ensure that GraphPerspective.hideNode() [GraphPerspective.hideNode()] calls
 * GraphPerspectiveChangeEvent for the node being hidden BEFORE the callbacks
 * for the edges hidden (associated with that node).
 * @author Michael L. Creech
 * @version 1.0
 */
public class BugHideNodeEventFiringTest extends TestCase implements GraphPerspectiveChangeListener {
	private boolean _edgeHiddenCallback;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		// Will dynamically add all methods as tests that begin with 'test'
		// and have no arguments:
		return new TestSuite(BugHideNodeEventFiringTest.class);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testBug() {
		// setup CN1-->S in GraphPerspective net:
		Node CN1 = Cytoscape.getCyNode("CN1", true);
		Node S = Cytoscape.getCyNode("S", true);
		Edge e1 = Cytoscape.getCyEdge(CN1, S, Semantics.INTERACTION, "testInteraction", true, true);
		GraphPerspective net = Cytoscape.createNetwork("net", false);
		net.restoreNode(CN1);
		net.restoreNode(S);
		net.restoreEdge(e1);
		net.addGraphPerspectiveChangeListener(this);
		_edgeHiddenCallback = false;
		// The following will remove e1 before specifying that CN1 is being
		// hidden!:
		net.hideNode(CN1);
		net.removeGraphPerspectiveChangeListener(this);
	}

	// implements GraphPerspectiveChangeListener:
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void graphPerspectiveChanged(GraphPerspectiveChangeEvent e) {
		if (e.isNodesHiddenType()) {
			int[] hiddenNodes = e.getHiddenNodeIndices();

			if (hiddenNodes != null) {
				if (_edgeHiddenCallback) {
					Assert.fail("We received hidden edge event callback BEFORE hidden node event callback!");
				}
			}
		}

		if (e.isEdgesHiddenType()) {
			int[] hiddenEdges = e.getHiddenEdgeIndices();

			if (hiddenEdges != null) {
				_edgeHiddenCallback = true;
			}
		}
	}
}
