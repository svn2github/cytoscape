/* -*-Java-*-
********************************************************************************
*
* File:         BugDestroyNetworkTest.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/BugDestroyNetworkTest.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Sep 23 12:17:09 2005
* Modified:     Tue Nov 07 09:15:12 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.unittest;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import giny.model.Edge;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Demonstrates Cytoscape.destoryNetwork() NullPointerException.
 * @author Michael L. Creech
 * @version 1.0
 */
public class BugDestroyNetworkTest extends TestBase
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static Test suite ()
    {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(BugDestroyNetworkTest.class);
    }

    public static void main (String[] args)
    {
        junit.textui.TestRunner.run (suite ());
    }

    public void testBug ()
    {
        CyNetwork net1     = Cytoscape.createNetwork ("net1");
        CyNode      n1       = Cytoscape.getCyNode ("S", true);
        CyNode      target   = Cytoscape.getCyNode ("7789023", true);
        int       edge_idx = Cytoscape.getRootGraph ().createEdge (n1, target,
                                                                   true);
        Edge      edge1 = Cytoscape.getRootGraph ().getEdge (edge_idx);
        String    uuid  = "12345678";
        edge1.setIdentifier (uuid);
        // net1.restoreNode (n1);
        // net1.restoreNode (target);
        net1.restoreEdge (edge1);
        Cytoscape.destroyNetwork (net1, true);
    }
}
