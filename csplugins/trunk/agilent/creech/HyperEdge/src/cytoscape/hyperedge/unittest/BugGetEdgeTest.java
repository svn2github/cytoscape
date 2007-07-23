/* -*-Java-*-
********************************************************************************
*
* File:         BugGetEdgeTest.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/BugGetEdgeTest.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Sep 23 12:17:09 2005
* Modified:     Tue Nov 07 09:18:15 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.unittest;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.giny.CytoscapeRootGraph;

import giny.model.Edge;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Demonstrate bug in CyotscapeRootGraph.getEdge().
 * @author Michael L. Creech
 * @version 1.0
 */
public class BugGetEdgeTest extends TestBase {
    private CytoscapeRootGraph _rg = Cytoscape.getRootGraph();

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(BugGetEdgeTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public void testBug() {
        String target_uuid  = "2:1127687773353:141.184.136.96";
        String s1_edge_uuid = "3:1127687773353:141.184.136.96";
        String s2_edge_uuid = "4:1127687773353:141.184.136.96";
        CyNode n1           = Cytoscape.getCyNode("S", true);
        CyNode target       = Cytoscape.getCyNode(target_uuid, true);

        createEdge(n1, target, s1_edge_uuid);
        createEdge(n1, target, s2_edge_uuid);
        System.out.println("After Creation:");
        Assert.assertNotNull(findEdge(s2_edge_uuid));

        // Will cause edge1 and edge2 to be removed:
        Cytoscape.getRootGraph().removeNode(target);
        System.out.println("After Deleting 'Target' Node:");

        Assert.assertNull(findEdge(s2_edge_uuid));

        // remake target:
        System.out.println("Adding back Target Node:");
        target = Cytoscape.getCyNode(target_uuid, true);

        Assert.assertNull(findEdge(s2_edge_uuid));
        // recreate edge1:
        createEdge(n1, target, s1_edge_uuid);
        System.out.println("After Recreating edge1:");
        Assert.assertNull(slowGetEdge(s2_edge_uuid));

        // *****NOW getEdge() WILL FIND edge2 WHEN IT SHOULDN'T*********:
        Assert.assertNull(findEdge(s2_edge_uuid));
    }

    public void testMaps() {
        Map<String, List<String>> test_map = new HashMap<String, List<String>>();
        List<String>              list1 = new ArrayList<String>();
        list1.add("sentence1");
        list1.add("sentence2");
        test_map.put("source1", list1);

        List<String> list2 = new ArrayList<String>();
        list2.add("sentence3");
        list2.add("sentence4");
        test_map.put("source2", list2);
        Assert.assertTrue(test_map.keySet().size() == 2);
        Assert.assertTrue(test_map.size() == 2);
    }

    private Edge createEdge(CyNode source, CyNode target, String uuid) {
        int edge_idx = _rg.createEdge(source, target);
        System.out.println(
            "createEdge: " + uuid + " RGidx = " + edge_idx + " source = " +
            source.getIdentifier() + " target = " + target.getIdentifier());

        Edge edge = _rg.getEdge(edge_idx);
        edge.setIdentifier(uuid);

        return edge;
    }

    public Edge findEdge(String uuid) {
        Edge edge = _rg.getEdge(uuid);

        if (edge != null) {
            System.out.println(
                "findEdge: " + uuid + " exists = true" + " RGidx = " +
                _rg.getIndex(edge));
        } else {
            System.out.println("findEdge: " + uuid + " exists = false");
        }

        return edge;
    }

    private Edge slowGetEdge(String uuid) {
        Iterator it   = _rg.edgesIterator();
        Edge     edge;

        while (it.hasNext()) {
            edge = (Edge) it.next();

            if (uuid.equals(edge.getIdentifier())) {
                return edge;
            }
        }

        return null;
    }
}
