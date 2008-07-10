/* -*-Java-*-
********************************************************************************
*
* File:         BugGetNodeTest.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Sun Aug 20 05:50:29 2006
* Modified:     Tue Aug 22 12:40:46 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape;

import org.cytoscape.GraphPerspective;
import org.cytoscape.Node;

import cytoscape.Cytoscape;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Demonstrate bug when using Cyotscape.getCyNode() when Node has been removed.
 * The real problem seems to be in CytoscapeFingRootGraph.getNode()
 * @author Michael L. Creech
 * @version 1.0
 */
public class BugGetCyNodeTest extends TestCase {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		// Will dynamically add all methods as tests that begin with 'test'
		// and have no arguments:
		return new TestSuite(BugGetCyNodeTest.class);
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
		Node S = Cytoscape.getCyNode("S", true);
		String sid = S.getIdentifier();
		GraphPerspective net1 = Cytoscape.createNetwork("net1");
		net1.restoreNode(S);
		Cytoscape.getRootGraph().removeNode(S);
		// The following gets a NullPointerException:
		System.out.println("BugGetNodeTest sid " + sid);
		Assert.assertNull(Cytoscape.getCyNode(sid, false));
	}
}
