/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeManagerTest.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/HyperEdgeManagerTest.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Tue Sep 20 06:09:08 2005
* Modified:     Tue Nov 07 06:56:43 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Tue Nov 07 06:52:30 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge.
* Mon Nov 06 09:27:21 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode.
* Thu Nov 02 05:21:56 2006 (Michael L. Creech) creech@w235krbza760
* Changed to usage of new shared HyperEdges API.
* Sun Jul 30 12:40:22 2006 (Michael L. Creech) creech@w235krbza760
*  Added 'addEdge' test to testgetEdgesByGraphPerspective() and
*  made InMemoryAndRestoredTestType more flexible for extra setup
*  before tests.
* Sat Jul 29 14:16:31 2006 (Michael L. Creech) creech@w235krbza760
*  Changed MEDIATOR-->ACTIVATING_MEDIATOR & INHIBITING_MEDIATOR.
* Tue May 09 08:32:35 2006 (Michael L. Creech) creech@Dill
*  Changed InMemoryAndRestoredTest to InMemoryAndRestoredTestType to
*  stop interactions with JUnit.
********************************************************************************
*/
package cytoscape.hyperedge.unittest;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.event.EventNote;
import cytoscape.hyperedge.impl.utils.HEUtils;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * Test the various HyperEdgeManager operations.
 * @author Michael L. Creech
 * @version 1.1
 */
public class HyperEdgeManagerTest extends TestBase {
    // private final String TEST1_LOC = "hyperedge-manager-test1.xml";
    // private final String TEST2_LOC = "hyperedge-manager-test2.xml";
    protected CyNode    B;
    protected CyNode    C;
    protected CyNode    D;
    protected CyNode    E;
    protected CyEdge    he2_sub;
    protected CyEdge    he2_prod;
    protected CyEdge    he3_sub;
    protected CyEdge    he3_prod;
    protected CyEdge    he3_med1;
    protected CyEdge    he3_med2;
    protected String    he2_sub_uuid;
    protected String    he2_prod_uuid;
    protected String    he3_sub_uuid;
    protected String    he3_prod_uuid;
    protected String    he3_med1_uuid;
    protected String    he3_med2_uuid;
    protected HyperEdge he2;
    protected HyperEdge he3;
    protected CyNetwork net6;
    protected CyNetwork net7;
    private boolean     _saved = false;

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(HyperEdgeManagerTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    private void setup(InMemoryAndRestoredTestType test) {
        // he1  --> S(su)M(ame)P(pr)
        // hd1  --> S(su)M(ime)S(su)
        // he2  --> A(su)B(pr)
        // he3  --> A(su)B(pr)C(ame)D(ime)
        // // net1 --> he1, he2, hd1
        // net6 --> he1, he2, hd1
        // // net2 --> he1, he3
        // net7 --> he1, he3
        // 
        manager.reset(false);
        setUp1(!_saved);
        B    = Cytoscape.getCyNode("B", true);
        C    = Cytoscape.getCyNode("C", true);
        D    = Cytoscape.getCyNode("D", true);
        E    = Cytoscape.getCyNode("E", true);
        net6 = Cytoscape.createNetwork("net6");
        net7 = Cytoscape.createNetwork("net7");
        he2  = factory.createHyperEdge(A,
                                       EdgeTypeMap.SUBSTRATE,
                                       B,
                                       EdgeTypeMap.PRODUCT,
                                       net6);
        he2.setName("he2");
        he2_sub  = he2.getAnEdge(A);
        he2_prod = he2.getAnEdge(B);

        // only reset the uuids when generating what will be in the loaded files.
        if (!_saved) {
            he2_sub_uuid  = he2_sub.getIdentifier();
            he2_prod_uuid = he2_prod.getIdentifier();
        }

        List<String> edges = new ArrayList<String>();
        edges.add(EdgeTypeMap.SUBSTRATE);
        edges.add(EdgeTypeMap.PRODUCT);
        edges.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
        edges.add(EdgeTypeMap.INHIBITING_MEDIATOR);

        List<CyNode> nodes = new ArrayList<CyNode>();
        nodes.add(A);
        nodes.add(B);
        nodes.add(C);
        nodes.add(D);
        he3 = factory.createHyperEdge(nodes, edges, net7);
        he3.setName("he3");
        he3_sub  = he3.getAnEdge(A);
        he3_prod = he3.getAnEdge(B);
        he3_med1 = he3.getAnEdge(C);
        he3_med2 = he3.getAnEdge(D);

        // only reset the uuids when generating what will be in the loaded files.
        if (!_saved) {
            he3_sub_uuid  = he3_sub.getIdentifier();
            he3_prod_uuid = he3_prod.getIdentifier();
            he3_med1_uuid = he3_med1.getIdentifier();
            he3_med2_uuid = he3_med2.getIdentifier();
        }

        // TODO: Move to eventTest
        new ChangeTester(he1, EventNote.Type.HYPEREDGE,
                         EventNote.SubType.ADDED, true, net6);
        he1.addToNetwork(net6);
        he1.addToNetwork(net7);
        // isolate he1 from original net1:
        he1.removeFromNetwork(net1);
        Assert.assertTrue(containsHEParts(net6, he1));

        // TODO: Move to eventTest
        new ChangeTester(hd1, EventNote.Type.HYPEREDGE,
                         EventNote.SubType.ADDED, true, net6);
        // he2.addToCyNetwork(net6);
        hd1.addToNetwork(net6);
        // isolate hd1 from original net1:
        hd1.removeFromNetwork(net1);
        // he3.addToCyNetwork(net7);

        // Any final, specific things to setup:
        test.extraSetup();

        if (!_saved) {
            // MLC 08/15/06 FIX!!:
            // saveTestHelper(TEST1_LOC, net6);
            // saveTestHelper(TEST2_LOC, net7);
            // MLC 08/15/06 END PATCH
            _saved = true;
        }
    }

    protected void tearDown1(boolean fire_events) {
        super.tearDown1(fire_events);
        // remove Cytoscape networks:
        Cytoscape.destroyNetwork((CyNetwork) net7);
    }

    // When we reload and reset the sample objects, reconnect the
    // instance variables to the newly read objects.
    protected void reconnectInstanceVariables() {
        super.reconnectInstanceVariables();
        he2      = null;
        he3      = null;
        he2_sub  = null;
        he2_prod = null;
        he3_sub  = null;
        he3_prod = null;
        he3_med1 = null;
        he3_med2 = null;

        CyEdge           edge;
        String           e_uuid;
        Iterator<CyEdge> edge_it = manager.getEdgesByNetwork(null);

        while (edge_it.hasNext()) {
            edge   = edge_it.next();
            e_uuid = edge.getIdentifier();

            if (he2_sub_uuid.equals(e_uuid)) {
                he2_sub = edge;
            } else if (he2_prod_uuid.equals(e_uuid)) {
                he2_prod = edge;
            } else if (he3_sub_uuid.equals(e_uuid)) {
                he3_sub = edge;
            } else if (he3_prod_uuid.equals(e_uuid)) {
                he3_prod = edge;
            } else if (he3_med1_uuid.equals(e_uuid)) {
                he3_med1 = edge;
            } else if (he3_med2_uuid.equals(e_uuid)) {
                he3_med2 = edge;
            }
        }

        String              h_name;
        HyperEdge           he;
        Iterator<HyperEdge> he_it = manager.getHyperEdgesByNetwork(null);

        while (he_it.hasNext()) {
            he     = he_it.next();
            h_name = he.getName();

            if ("he2".equals(h_name)) {
                he2 = he;
            } else if ("he3".equals(h_name)) {
                he3 = he;
            }
        }

        B = Cytoscape.getCyNode("B", false);
        C = Cytoscape.getCyNode("C", false);
        D = Cytoscape.getCyNode("D", false);
        E = Cytoscape.getCyNode("E", false);

        Assert.assertNotNull(he2);
        Assert.assertNotNull(he3);
        Assert.assertNotNull(he2_sub);
        Assert.assertNotNull(he2_prod);
        Assert.assertNotNull(he3_sub);
        Assert.assertNotNull(he3_prod);
        Assert.assertNotNull(he3_med1);
        Assert.assertNotNull(B);
        Assert.assertNotNull(C);
        Assert.assertNotNull(D);
        Assert.assertNotNull(E);
    }

    public void testHyperEdgeManager() {
        runTest(new testAddAndRemoveFromCyNetwork());
        runTest(new testgetEdgesByCyNetwork());
        runTest(new testGetEdgesByNode());
        runTest(new testGetCyNetwork());
        runTest(new testGetHyperEdgeForConnectorNode());
        runTest(new testGetHyperEdgesByEdgeTypes());
        runTest(new testGetHyperEdgesByCyNetwork());
        runTest(new testGetHyperEdgesByNode());
        runTest(new testGetHyperEdgesByNodes());
        runTest(new testGetHyperEdgeVersion());
        runTest(new testGetHyperEdgeVersionNumber());
        runTest(new testGetNodesByEdgeTypes());
        runTest(new testGetNumEdges());
        runTest(new testGetNumHyperEdges());
        runTest(new testGetNumNodes());
        runTest(new testInHyperEdge());
        runTest(new testIsConnectorNode());
        // load tested elsewhere
        runTest(new testReset());

        // save tested elsewhere
    }

    protected void runTest(InMemoryAndRestoredTestType test) {
        setup(test);
        test.runIt();
        tearDown1(false);

        // MLC 08/15/06 PATCH FIX!!:
        // net6 and net7 were destroyed, rebuild them:
        //        net6 = Cytoscape.createNetwork("net6");
        //        net7 = Cytoscape.createNetwork("net7");
        // rerun the tests from the restored objects:
        // restoreTestHelper(TEST1_LOC, net6);
        //  restoreTestHelper(TEST2_LOC, net7);
        //        reconnectInstanceVariables();
        //        test.extraSetup();
        //        test.runIt();
        // MLC 08/15/06 END PATCH
    }

    // ensure all of he's contents are in GP:
    private boolean containsHEParts(CyNetwork gp, HyperEdge he) {
        CyNode           node;
        CyEdge           edge;
        Iterator<CyEdge> edge_it = he.getEdges(null);

        while (edge_it.hasNext()) {
            edge = edge_it.next();

            if (!gp.containsEdge(edge)) {
                HEUtils.log(
                    "Didn't find edge " + edge.getIdentifier() +
                    " in HyperEdge " + he.getName() +
                    " that should belong to the CyNetwork " +
                    ((CyNetwork) gp).getTitle());

                return false;
            }
        }

        Iterator node_it = he.getNodes(null);

        while (node_it.hasNext()) {
            node = (CyNode) node_it.next();

            if (!gp.containsNode(node)) {
                HEUtils.log(
                    "Didn't find node " + node.getIdentifier() +
                    " in HyperEdge " + he.getName() +
                    " that should belong to the CyNetwork " +
                    ((CyNetwork) gp).getTitle());

                return false;
            }
        }

        return true;
    }

    // ensure none of he's contents are in GP:
    private boolean containsNoHEParts(CyNetwork gp, Iterator<CyEdge> edgeIt,
                                      String heName) {
        CyEdge edge;

        while (edgeIt.hasNext()) {
            edge = edgeIt.next();

            if (gp.containsEdge(edge)) {
                HEUtils.log(
                    "Found edge " + edge.getIdentifier() + " in HyperEdge " +
                    heName + " that shouldn't belong to the CyNetwork " +
                    ((CyNetwork) gp).getTitle());

                return false;
            }
        }

        // NOTE: We don't test CyNodes because CyNodes are not removed:
        return true;
    }

    private interface InMemoryAndRestoredTestType {
        public void runIt();

        public void extraSetup();
    }

    private class testGetCyNetwork implements InMemoryAndRestoredTestType {
        public void runIt() {
            // TODO: Move to HyperEdgeTest:
            testIterator(he1.getNetworks(), 2);
            testIterator(he3.getNetworks(), 1);
            testIterator(he2.getNetworks(), 1);
            testIterator(hd1.getNetworks(), 1);
        }

        public void extraSetup() {
        }
    }

    private class testGetHyperEdgeForConnectorNode
        implements InMemoryAndRestoredTestType {
        public void runIt() {
            CyNode he1_con = he1.getConnectorNode();
            CyNode he2_con = he2.getConnectorNode();
            Assert.assertNull(manager.getHyperEdgeForConnectorNode(null));
            Assert.assertNull(manager.getHyperEdgeForConnectorNode(S));

            HyperEdge he = manager.getHyperEdgeForConnectorNode(he1_con);
            Assert.assertTrue(he == he1);
            // Assert.assertTrue (manager.getHyperEdgeForConnectorNode (he1_con) == he1);
            he = manager.getHyperEdgeForConnectorNode(he2_con);
            Assert.assertTrue(he == he2);

            // Assert.assertTrue (manager.getHyperEdgeForConnectorNode (he2_con) == he2);
        }

        public void extraSetup() {
        }
    }

    private class testGetHyperEdgesByCyNetwork
        implements InMemoryAndRestoredTestType {
        public void runIt() {
            // returns all hes:
            testIterator(manager.getHyperEdgesByNetwork(null), 4);

            // test no hes:
            CyNetwork net3 = Cytoscape.createNetwork("net3");
            testIterator(manager.getHyperEdgesByNetwork(net3), 0);
            testIterator(manager.getHyperEdgesByNetwork(net6), 3);
            testIterator(manager.getHyperEdgesByNetwork(net7), 2);
        }

        public void extraSetup() {
        }
    }

    private class testGetHyperEdgesByNode implements InMemoryAndRestoredTestType {
        public void runIt() {
            // should return all hes:
            testIterator(manager.getHyperEdgesByNode(null, null), 4);
            // all hes in net6:
            testIterator(manager.getHyperEdgesByNode(null, net6), 3);
            // all hes in net7:
            testIterator(manager.getHyperEdgesByNode(null, net7), 2);
            // node S over all gps:
            testIterator(manager.getHyperEdgesByNode(S, null), 2);
            // node A over all gps:
            testIterator(manager.getHyperEdgesByNode(A, null), 2);
            // node S over net6
            testIterator(manager.getHyperEdgesByNode(S, net6), 2);
            // node A over net6:
            testIterator(manager.getHyperEdgesByNode(A, net6), 1);
            // test node not in gp:
            testIterator(manager.getHyperEdgesByNode(D, net6), 0);
        }

        public void extraSetup() {
        }
    }

    private class testGetHyperEdgesByNodes
        implements InMemoryAndRestoredTestType {
        public void runIt() {
            // return all HyperEdges:
            testIterator(manager.getHyperEdgesByNodes(null, null), 4);
            // return all HyperEdges in net6:
            testIterator(manager.getHyperEdgesByNodes(null, net6), 3);
            // return all HyperEdges in net7:
            testIterator(manager.getHyperEdgesByNodes(null, net7), 2);

            // try empty collection:
            Collection<CyNode> col = new ArrayList<CyNode>();
            testIterator(manager.getHyperEdgesByNodes(col, null), 0);
            col.add(S);
            // node S over all gps:
            testIterator(manager.getHyperEdgesByNodes(col, null), 2);
            // node S over net6
            testIterator(manager.getHyperEdgesByNodes(col, net6), 2);
            col.add(P);
            // node S & P over all gps:
            testIterator(manager.getHyperEdgesByNodes(col, null), 1);
            // node S & P over net6:
            testIterator(manager.getHyperEdgesByNodes(col, net6), 1);
            col.clear();
            col.add(A);
            col.add(B);
            // node A & B over all gps:
            testIterator(manager.getHyperEdgesByNodes(col, null), 2);
            // node A & B over net6:
            testIterator(manager.getHyperEdgesByNodes(col, net6), 1);
            // node A & B over net7:
            testIterator(manager.getHyperEdgesByNodes(col, net7), 1);
            col.add(C);
            // node A & B & C over net6:
            testIterator(manager.getHyperEdgesByNodes(col, net6), 0);
            // node A & B & C over net6:
            testIterator(manager.getHyperEdgesByNodes(col, net7), 1);
            // no match:
            col.add(S);
            testIterator(manager.getHyperEdgesByNodes(col, null), 0);
        }

        public void extraSetup() {
        }
    }

    private class testGetHyperEdgeVersion implements InMemoryAndRestoredTestType {
        public void runIt() {
            String version = manager.getHyperEdgeVersion();
            Assert.assertTrue((version != null) && (version.length() > 0));
        }

        public void extraSetup() {
        }
    }

    private class testGetHyperEdgeVersionNumber
        implements InMemoryAndRestoredTestType {
        public void runIt() {
            Double version_num = manager.getHyperEdgeVersionNumber();
            Assert.assertTrue((version_num != null));
        }

        public void extraSetup() {
        }
    }

    private class testGetHyperEdgesByEdgeTypes
        implements InMemoryAndRestoredTestType {
        public void runIt() {
            // return all HyperEdges:
            testIterator(manager.getHyperEdgesByEdgeTypes(null, null), 4);
            // return all HyperEdges in net6:
            testIterator(manager.getHyperEdgesByEdgeTypes(null, net6), 3);
            // return all HyperEdges in net7:
            testIterator(manager.getHyperEdgesByEdgeTypes(null, net7), 2);

            // try empty collection:
            Collection<String> col = new ArrayList<String>();
            testIterator(manager.getHyperEdgesByEdgeTypes(col, null), 0);
            col.add(EdgeTypeMap.SUBSTRATE);
            // any "substrate" roles:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, null), 4);
            // any "substrate" roles in net6:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, net6), 3);
            // any "substrate" roles in net7:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, net7), 2);
            col.add(EdgeTypeMap.PRODUCT);
            // any "substrate" & "product" roles:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, null), 3);
            // any "substrate" & "product" roles in net6:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, net6), 2);
            // any "substrate" & "product" roles in net7:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, net7), 2);
            col.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
            // any "substrate" & "product" & "mediator" roles:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, null), 2);
            // any "substrate" & "product"  & "mediator" roles in net6:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, net6), 1);
            // any "substrate" & "product"  & "mediator" roles in net7:
            testIterator(manager.getHyperEdgesByEdgeTypes(col, net7), 2);
            // no match:
            col.add("mediator1");
            testIterator(manager.getHyperEdgesByEdgeTypes(col, null), 0);
        }

        public void extraSetup() {
        }
    }

    private class testGetNodesByEdgeTypes implements InMemoryAndRestoredTestType {
        public void runIt() {
            // return all CyNodes:
            testIterator(manager.getNodesByEdgeTypes(null, null), 7);
            // return all CyNodes in net6:
            testIterator(manager.getNodesByEdgeTypes(null, net6), 5);
            // return all CyNodes in net7:
            testIterator(manager.getNodesByEdgeTypes(null, net7), 7);

            // try empty collection:
            Collection<String> col = new ArrayList<String>();
            testIterator(manager.getNodesByEdgeTypes(col, null), 0);
            col.add(EdgeTypeMap.SUBSTRATE);
            // any "substrate" nodes (A & S):
            testIterator(manager.getNodesByEdgeTypes(col, null), 2);
            // any "substrate" nodes (A & S) in net6:
            testIterator(manager.getNodesByEdgeTypes(col, net6), 2);
            // any "substrate" nodes (A & S) in net7:
            testIterator(manager.getNodesByEdgeTypes(col, net7), 2);
            col.add(EdgeTypeMap.INHIBITING_MEDIATOR);
            // all nodes containing "SUBSTRATE" or "INHIBITING_MEDIATOR" (A & S & M & D):
            testIterator(manager.getNodesByEdgeTypes(col, null), 4);
            col.remove(EdgeTypeMap.INHIBITING_MEDIATOR);

            col.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
            // all nodes in net7 containing "SUBSTRATE" or
            // "ACTIVATING_MEDIATOR" (S & M & A & C):
            testIterator(manager.getNodesByEdgeTypes(col, net7), 4);
            testIterator(manager.getNodesByEdgeTypes(col, net6), 3);

            col.clear();
            col.add("jojo");
            testIterator(manager.getNodesByEdgeTypes(col, null), 0);
        }

        public void extraSetup() {
        }
    }

    private class testGetEdgesByNode implements InMemoryAndRestoredTestType {
        public void runIt() {
            // should return all edges:
            testIterator(manager.getEdgesByNode(null, null),
                         manager.getNumEdges(null));
            // all edges in net6:
            testIterator(manager.getEdgesByNode(null, net6),
                         manager.getNumEdges(net6));
            // all edges in net7:
            testIterator(manager.getEdgesByNode(null, net7),
                         manager.getNumEdges(net7));
            // node S over all gps:
            testIterator(manager.getEdgesByNode(S, null), 3);
            // node A over all gps:
            testIterator(manager.getEdgesByNode(A, null), 2);
            // node S over net7
            testIterator(manager.getEdgesByNode(S, net7), 1);
            // node A over net6:
            testIterator(manager.getEdgesByNode(A, net6), 1);
            // test node not in gp:
            testIterator(manager.getEdgesByNode(D, net6), 0);
        }

        public void extraSetup() {
        }
    }

    private class testGetNumHyperEdges implements InMemoryAndRestoredTestType {
        public void runIt() {
            Assert.assertTrue(manager.getNumHyperEdges(null) == 4);
            Assert.assertTrue(manager.getNumHyperEdges(net6) == 3);
            Assert.assertTrue(manager.getNumHyperEdges(net7) == 2);

            CyNetwork net4 = Cytoscape.createNetwork("net4");
            Assert.assertTrue(manager.getNumHyperEdges(net4) == 0);
        }

        public void extraSetup() {
        }
    }

    private class testGetNumNodes implements InMemoryAndRestoredTestType {
        public void runIt() {
            // Would be 7, but we added m2 to he1, making it 8:
            Assert.assertTrue(manager.getNumNodes(null) == 8);
            // Would be 5, but we added m2 to he1, making it 6:
            Assert.assertTrue(manager.getNumNodes(net6) == 6);
            // Would be 7, but we added m2 to he1, making it 8:
            Assert.assertTrue(manager.getNumNodes(net7) == 8);

            CyNetwork net4 = Cytoscape.createNetwork("net4");
            Assert.assertTrue(manager.getNumNodes(net4) == 0);
        }

        public void extraSetup() {
            // test that addEdge() does right behavior so
            // CyNetwork's that he1 belongs to are updated with
            // the new node:
            CyNode m2 = Cytoscape.getCyNode("M2", true);
            he1.addEdge(m2, EdgeTypeMap.INHIBITING_MEDIATOR);
        }
    }

    private class testGetNumEdges implements InMemoryAndRestoredTestType {
        public void runIt() {
            Assert.assertTrue(manager.getNumEdges(null) == 12);
            Assert.assertTrue(manager.getNumEdges(net6) == 8);
            Assert.assertTrue(manager.getNumEdges(net7) == 7);

            CyNetwork net4 = Cytoscape.createNetwork("net4");
            Assert.assertTrue(manager.getNumEdges(net4) == 0);
        }

        public void extraSetup() {
        }
    }

    private class testgetEdgesByCyNetwork implements InMemoryAndRestoredTestType {
        public void runIt() {
            // returns all roles:
            // Would be 12, but we added INHIBITING_MEDIATOR to he1,
            // making it 13:
            testIterator(manager.getEdgesByNetwork(null), 13);

            // test no roles:
            CyNetwork net3 = Cytoscape.createNetwork("net3");
            testIterator(manager.getEdgesByNetwork(net3), 0);
            // Would be 8, but we added INHIBITING_MEDIATOR to he1,
            // making it 9:
            testIterator(manager.getEdgesByNetwork(net6), 9);
            // Would be 7, but we added INHIBITING_MEDIATOR to he1,
            // making it 8:
            testIterator(manager.getEdgesByNetwork(net7), 8);
        }

        public void extraSetup() {
            // test that addEdge() does right behavior so
            // CyNetwork's that he1 belongs to are updated with
            // the new edge:
            CyNode m2 = Cytoscape.getCyNode("M2", true);
            he1.addEdge(m2, EdgeTypeMap.INHIBITING_MEDIATOR);
        }
    }

    private class testInHyperEdge implements InMemoryAndRestoredTestType {
        public void runIt() {
            Assert.assertFalse(manager.inHyperEdge(null, null));
            Assert.assertFalse(manager.inHyperEdge(null, net6));
            // not in any hyperedge:
            Assert.assertFalse(manager.inHyperEdge(E, null));
            Assert.assertTrue(manager.inHyperEdge(S, null));
            Assert.assertTrue(manager.inHyperEdge(S, net6));
            Assert.assertTrue(manager.inHyperEdge(S, net7));
            Assert.assertFalse(manager.inHyperEdge(D, net6));
            Assert.assertTrue(manager.inHyperEdge(D, net7));
        }

        public void extraSetup() {
        }
    }

    private class testIsConnectorNode implements InMemoryAndRestoredTestType {
        public void runIt() {
            // in net6 & net7:
            CyNode he1_con = he1.getConnectorNode();

            // in net6:
            CyNode hd1_con = hd1.getConnectorNode();

            // in net7:
            CyNode he3_con = he3.getConnectorNode();
            Assert.assertFalse(manager.isConnectorNode(null, null));
            // node not a connector:
            Assert.assertFalse(manager.isConnectorNode(S, null));
            Assert.assertFalse(manager.isConnectorNode(B, net6));
            // wrong network:
            Assert.assertFalse(manager.isConnectorNode(he3_con, net6));
            Assert.assertFalse(manager.isConnectorNode(hd1_con, net7));
            // matches:
            Assert.assertTrue(manager.isConnectorNode(he1_con, net6));
            Assert.assertTrue(manager.isConnectorNode(he1_con, net7));
            Assert.assertTrue(manager.isConnectorNode(he1_con, null));
            Assert.assertTrue(manager.isConnectorNode(hd1_con, null));
            Assert.assertTrue(manager.isConnectorNode(he3_con, null));
        }

        public void extraSetup() {
        }
    }

    private class testReset implements InMemoryAndRestoredTestType {
        public void runIt() {
            CyNode hd1_con = hd1.getConnectorNode();
            CyNode he3_con = he3.getConnectorNode();
            manager.reset(false);
            Assert.assertTrue(0 == manager.getNumEdges(null));
            Assert.assertTrue(0 == manager.getNumNodes(null));
            Assert.assertTrue(0 == manager.getNumHyperEdges(null));
            // check that connectors are no longer in RootGraph:
            //            // TODO: Replace with Cytoscape.getCyNode () calls when
            //            //       it is fixed:
            // Assert.assertNull(HEUtils.slowGetNode(hd1_con.getIdentifier()));
            // Assert.assertNull(HEUtils.slowGetNode(he3_con.getIdentifier()));
            //            Assert.assertNotNull(Cytoscape.getRootGraph()
            //                                          .getNode(hd1_con.getIdentifier()));
            //            Assert.assertNotNull(Cytoscape.getRootGraph()
            //                                          .getNode(he3_con.getIdentifier()));
            Assert.assertNotNull(Cytoscape.getCyNode(hd1_con.getIdentifier(),
                                                     false));
            Assert.assertNotNull(Cytoscape.getCyNode(he3_con.getIdentifier(),
                                                     false));

            // check no errors occur when already empty:
            manager.reset(false);
            manager.reset(true);
            setup(this);
            manager.reset(true);
            Assert.assertTrue(0 == manager.getNumEdges(null));
            Assert.assertTrue(0 == manager.getNumNodes(null));
            Assert.assertTrue(0 == manager.getNumHyperEdges(null));
            // check no errors occur when already empty:
            manager.reset(true);
            manager.reset(false);
        }

        public void extraSetup() {
        }
    }

    private class testAddAndRemoveFromCyNetwork
        implements InMemoryAndRestoredTestType {
        public void runIt() {
            Assert.assertTrue(containsHEParts(net6, he2));
            Assert.assertTrue(containsHEParts(net6, hd1));
            Assert.assertTrue(containsHEParts(net7, he1));
            Assert.assertTrue(containsHEParts(net7, he3));

            // TEST removeFromCyNetwork:
            testIterator(manager.getHyperEdgesByNetwork(null), 4);

            // Assert.assertFalse(manager.removeFromCyNetwork(null, null));
            CyNetwork emptyNet = Cytoscape.createNetwork("emptyNet");
            Assert.assertFalse(he1.removeFromNetwork(emptyNet));

            // Assert.assertFalse(manager.removeFromCyNetwork(net6, null));
            // TODO: Move to eventTest:
            new ChangeTester(he1, EventNote.Type.HYPEREDGE,
                             EventNote.SubType.REMOVED, true, net6);

            // Iterator<CyEdge> edgesIt = he1.getEdges(null, net6);
            Iterator<CyEdge> edgesIt = he1.getEdges(null);
            String           heName = he1.getName();
            Assert.assertTrue(he1.removeFromNetwork(net6));
            Assert.assertTrue(containsNoHEParts(net6, edgesIt, heName));
            // attempt to remove again:
            Assert.assertFalse(he1.removeFromNetwork(net6));
            // he1 is still in net7:
            testIterator(manager.getHyperEdgesByNetwork(net6), 2);
            testIterator(manager.getHyperEdgesByNetwork(net7), 2);
            testIterator(manager.getHyperEdgesByNetwork(null), 4);

            // TODO: Move to eventTest:
            new ChangeTester(he1, EventNote.Type.HYPEREDGE,
                             EventNote.SubType.REMOVED, true, net7);

            // edgesIt = he1.getEdges(null, net7);
            // edgesIt = he1.getEdges(null);
            // Avoid ConcurrentModificationException with using edges Iterator
            // directly:
            Iterator<CyEdge> edgeIt = HEUtils.createCollection(he1.getEdges(null))
                                             .iterator();
            heName = he1.getName();
            Assert.assertTrue(he1.removeFromNetwork(net7));

            Assert.assertTrue(containsNoHEParts(net7, edgeIt, heName));
            testIterator(manager.getHyperEdgesByNetwork(net7), 1);
            // // will still be 4--not affected by CyNetwork removal:
            // testIterator(manager.getHyperEdgesByCyNetwork(null), 4);
            testIterator(manager.getHyperEdgesByNetwork(null), 3);

            // edgesIt = he3.getEdges(null, net7);
            // Avoid ConcurrentModificationException with using edges Iterator
            // directly:
            edgeIt = HEUtils.createCollection(he3.getEdges(null)).iterator();
            heName = he3.getName();
            Assert.assertTrue(he3.removeFromNetwork(net7));
            Assert.assertTrue(containsNoHEParts(net7, edgeIt, heName));
            testIterator(manager.getHyperEdgesByNetwork(net7), 0);

            // edgesIt = hd1.getEdges(null, net6);
            // edgesIt = hd1.getEdges(null);
            // Avoid ConcurrentModificationException with using edges Iterator
            // directly:
            edgeIt = HEUtils.createCollection(hd1.getEdges(null)).iterator();
            heName = hd1.getName();
            Assert.assertTrue(hd1.removeFromNetwork(net6));
            Assert.assertTrue(containsNoHEParts(net6, edgesIt, heName));
            testIterator(manager.getHyperEdgesByNetwork(net6), 1);

            // edgesIt = he2.getEdges(null, net6);
            // edgesIt = he2.getEdges(null);
            // Avoid ConcurrentModificationException with using edges Iterator
            // directly:
            edgeIt = HEUtils.createCollection(he2.getEdges(null)).iterator();
            heName = he2.getName();
            Assert.assertTrue(he2.removeFromNetwork(net6));
            Assert.assertTrue(containsNoHEParts(net6, edgesIt, heName));
            testIterator(manager.getHyperEdgesByNetwork(net6), 0);
            testIterator(manager.getHyperEdgesByNetwork(null), 0);
            // testIterator(manager.getHyperEdgesByCyNetwork(null), 4);

            // TEST addToCyNetwork:
            // TODO: FIX: Add tests
            // Assert.assertFalse(manager.addToCyNetwork(null, null));
            // Assert.assertFalse(manager.addToCyNetwork(net6, null));
            // Assert.assertFalse(he1.addToCyNetwork(null));

            //      other add tests are part of setup().
        }

        public void extraSetup() {
        }
    }
}
