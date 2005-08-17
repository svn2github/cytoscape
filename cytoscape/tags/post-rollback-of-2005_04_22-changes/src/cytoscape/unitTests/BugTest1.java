/* -*-Java-*-
********************************************************************************
*
* File:         BugTest1.java
* RCS:          $Header$
* Description:
* Author:       Michael L. Creech
* Created:      Wed Jun 15 06:02:24 2005
* Modified:     Wed Jun 15 09:19:21 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.unitTests;


import cytoscape.Cytoscape;

import giny.model.Edge;
import giny.model.Node;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class BugTest1 extends TestCase
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static Test suite ()
    {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(BugTest1.class);
    }

    public static void main (String[] args)
    {
        junit.textui.TestRunner.run (suite ());
    }

    public void testBug ()
    {
        Node n1 = Cytoscape.getCyNode ("S", true);
        Node n2 = Cytoscape.getCyNode ("M", true);
        Node n3 = Cytoscape.getCyNode ("A", true);
        Node n4 = Cytoscape.getCyNode ("Z", true);

        Cytoscape.getRootGraph ().createEdge (n1, n2, false); // S-M
        int  edge_idx = Cytoscape.getRootGraph ().createEdge (n1, n3, false); // S-A
        Edge del1 = Cytoscape.getRootGraph ().getEdge (edge_idx);
        edge_idx = Cytoscape.getRootGraph ().createEdge (n2, n3, false); // M-A
        Edge del2 = Cytoscape.getRootGraph ().getEdge (edge_idx);
        // Now delete S-A & M-A:
        Cytoscape.getRootGraph ().removeEdge (del1);
        Cytoscape.getRootGraph ().removeEdge (del2);
        edge_idx = Cytoscape.getRootGraph ().createEdge (n1, n4, false);
	// ****** THIS SUBEDGE RETURNED HAS A NULL SOURCE AND TARGET!!: *****
        Edge subedge = Cytoscape.getRootGraph ().getEdge (edge_idx);
        Node src    = subedge.getSource ();
        Node target = subedge.getTarget ();
        Assert.assertNotNull (src); // null!
        Assert.assertNotNull (target); // null!
    }
}
