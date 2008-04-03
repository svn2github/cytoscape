/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeTest.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/HyperEdgeTest.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Sep 21 09:14:34 2005
* Modified:     Thu Apr 03 11:01:50 2008 (Michael L. Creech) creech@w235krbza760
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
* Thu Apr 03 11:01:05 2008 (Michael L. Creech) creech@w235krbza760
*  Removed various hacks around old cytoscape bugs and updated to test save/restore.
* Tue Nov 07 06:46:29 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge.
* Mon Nov 06 09:25:45 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode.
* Thu Nov 02 05:21:42 2006 (Michael L. Creech) creech@w235krbza760
* Changed to usage of new shared HyperEdges API.
* Mon Aug 07 09:22:31 2006 (Michael L. Creech) creech@w235krbza760
*  Added performGetNodesByEdgeTypesTests() to test new
*  HyperEdge.getNodesByEdgeTypes().
* Sat Jul 29 14:24:12 2006 (Michael L. Creech) creech@w235krbza760
*  Changed MEDIATOR-->ACTIVATING_MEDIATOR & INHIBITING_MEDIATOR.
* Fri Sep 30 09:13:15 2005 (Michael L. Creech) creech@Dill
*  Tested save/load and usage of XML control characters in names of Nodes and
*  HyperEdges.
********************************************************************************
*/
package cytoscape.hyperedge.unittest;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.hyperedge.EdgeFilter;
import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.LifeState;
import cytoscape.hyperedge.EdgeTypeMap.EdgeRole;
import cytoscape.hyperedge.impl.utils.EdgeFilters;
import cytoscape.hyperedge.impl.utils.HEUtils;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Test the various HyperEdge  operations.
 * @author Michael L. Creech
 * @version 1.0
 */
public class HyperEdgeTest extends TestBase {
    private final String        NET1_LOC              = "hyperedge-test-net1.xgmml";
    private final String        SHARED_EDGE_NET1_LOC  = "hyperedge-test-shared-edge-net1.xgmml";
    private final String        SHARED_EDGE_NET2_LOC  = "hyperedge-test-shared-edge-net2.xgmml";
    private final String        FUNNY_NAME            = "&\"'<funny>'\"&";
    private final String        FUNNY_NODE1           = "&funny1<name>&";
    private final String        FUNNY_NODE2           = "\"'funny2'\"";
    private final String        SHARED1_NAME          = "shared1";
    private final String        SHARED2_NAME          = "shared2";
    private final String        SHARED3_NAME          = "shared3";
    private final String        UNSHARED1_NAME        = "unshared1";
    private final String        SHARED_EDGE_NET1_NAME = "SharedEdgeTest1";
    private final String        SHARED_EDGE_NET2_NAME = "SharedEdgeTest2";
    protected HyperEdge         funny_he;
    protected HyperEdge         shared1;
    protected HyperEdge         shared2;
    protected HyperEdge         shared3;
    protected HyperEdge         unshared1;
    protected String            funny_sub_uuid;
    protected String            funny_prod_uuid;
    protected CyEdge            funny_sub;
    protected CyEdge            funny_prod;
    protected CyNetwork         sharedEdgeNet1;
    protected CyNetwork         sharedEdgeNet2;
    protected PersistenceHelper _pHelper              = new PersistenceHelper();

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(HyperEdgeTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    protected void setUp1(boolean reset_uuids) {
        super.setUp1(reset_uuids);

        // test persistent names with special characters in them:
        CyNode funny1   = Cytoscape.getCyNode(FUNNY_NODE1, true);
        CyNode funny2 = Cytoscape.getCyNode(FUNNY_NODE2, true);
        funny_he        = factory.createHyperEdge(funny1,
                                                  EdgeTypeMap.SUBSTRATE,
                                                  funny2,
                                                  EdgeTypeMap.PRODUCT,
                                                  net1);
        funny_sub       = funny_he.getAnEdge(funny1);
        funny_prod      = funny_he.getAnEdge(funny2);
        funny_sub_uuid  = funny_sub.getIdentifier();
        funny_prod_uuid = funny_prod.getIdentifier();
        funny_he.setName(FUNNY_NAME);

        sharedEdgeNet1 = Cytoscape.createNetwork(SHARED_EDGE_NET1_NAME);
        sharedEdgeNet2 = Cytoscape.createNetwork(SHARED_EDGE_NET2_NAME);

        // sharedEdgeNet1-->shared1,shared2,shared3,unshared1
        // sharedEdgeNet2-->unshared1
        // Shared edge connections: shared1-->shared2-->shared3-->shared1
        shared1 = factory.createHyperEdge(S,
                                          EdgeTypeMap.SUBSTRATE,
                                          M,
                                          EdgeTypeMap.ACTIVATING_MEDIATOR,
                                          sharedEdgeNet1);
        shared1.setName(SHARED1_NAME);
        shared2 = factory.createHyperEdge(M,
                                          EdgeTypeMap.ACTIVATING_MEDIATOR,
                                          P,
                                          EdgeTypeMap.PRODUCT,
                                          sharedEdgeNet1);
        shared2.setName(SHARED2_NAME);
        shared3 = factory.createHyperEdge(A,
                                          EdgeTypeMap.SUBSTRATE,
                                          P,
                                          EdgeTypeMap.PRODUCT,
                                          sharedEdgeNet1);
        shared3.setName(SHARED3_NAME);
        // connect shared1-->shared2:
        shared1.connectHyperEdges(shared2,
                                  EdgeTypeMap.SUBSTRATE
                                  );
        // connect shared2-->shared3:
        shared2.connectHyperEdges(shared3,
                                  
                                  EdgeTypeMap.PRODUCT);
        // connect shared3-->shared1:
        shared3.connectHyperEdges(shared1,
                                  EdgeTypeMap.SUBSTRATE
                                  );
        unshared1 = factory.createHyperEdge(M,
                                            EdgeTypeMap.ACTIVATING_MEDIATOR,
                                            P,
                                            EdgeTypeMap.PRODUCT,
                                            sharedEdgeNet1);
        unshared1.setName(UNSHARED1_NAME);
        unshared1.addToNetwork(sharedEdgeNet2);
    }

    // When we reload and reset the sample objects, reconnect the
    // instance variables to the newly read objects.
    protected void reconnectInstanceVariables() {
        super.reconnectInstanceVariables();
        funny_he   = null;
        funny_sub  = null;
        funny_prod = null;
        shared1    = null;
        shared2    = null;
        shared3    = null;
        unshared1  = null;

        CyEdge           edge;
        String           e_uuid;
        Iterator<CyEdge> edge_it = manager.getEdgesByNetwork(null);

        while (edge_it.hasNext()) {
            edge   = edge_it.next();
            e_uuid = edge.getIdentifier();

            if (funny_sub_uuid.equals(e_uuid)) {
                funny_sub = edge;
            }

            if (funny_prod_uuid.equals(e_uuid)) {
                funny_prod = edge;
            }
        }

        String              h_name;
        HyperEdge           he;
        Iterator<HyperEdge> he_it = manager.getHyperEdgesByNetwork(null);

        while (he_it.hasNext()) {
            he     = he_it.next();
            h_name = he.getName();

            if (FUNNY_NAME.equals(h_name)) {
                HEUtils.log("reconnect HE with name = " + FUNNY_NAME);
                funny_he = he;
            } else if (SHARED1_NAME.equals(h_name)) {
                HEUtils.log("reconnect HE with name = " + SHARED1_NAME);
                shared1 = he;
            } else if (SHARED2_NAME.equals(h_name)) {
                HEUtils.log("reconnect HE with name = " + SHARED2_NAME);
                shared2 = he;
            } else if (SHARED3_NAME.equals(h_name)) {
                HEUtils.log("reconnect HE with name = " + SHARED3_NAME);
                shared3 = he;
            } else if (UNSHARED1_NAME.equals(h_name)) {
                HEUtils.log("reconnect HE with name = " + UNSHARED1_NAME);
                unshared1 = he;
            }
        }

        // Now connect up networks:
        Set<CyNetwork> nets = (Set<CyNetwork>) Cytoscape.getNetworkSet();
        HEUtils.log("nets size = " + nets.size());

        for (CyNetwork net : nets) {
            HEUtils.log("NET = " + net.getTitle());

            if (SHARED_EDGE_NET1_NAME.equals(net.getTitle())) {
                HEUtils.log(
                    "reconnect Network with name = " + SHARED_EDGE_NET1_NAME);
                sharedEdgeNet1 = net;
            } else if (SHARED_EDGE_NET2_NAME.equals(net.getTitle())) {
                HEUtils.log(
                    "reconnect Network with name = " + SHARED_EDGE_NET2_NAME);
                sharedEdgeNet2 = net;
            }
        }
    }

    public void testHyperEdges() {
        setUp1(true);
        _pHelper.saveTestHelper(NET1_LOC, net1);
        _pHelper.saveTestHelper(SHARED_EDGE_NET1_LOC, sharedEdgeNet1);
        _pHelper.saveTestHelper(SHARED_EDGE_NET2_LOC, sharedEdgeNet2);
        performHyperEdgeTests(true);
        removeCyNetworks();
        tearDown1(false);
        tearDown1(true);
        _pHelper.restoreTestHelper(NET1_LOC);
        _pHelper.restoreTestHelper(SHARED_EDGE_NET1_LOC);
        _pHelper.restoreTestHelper(SHARED_EDGE_NET2_LOC);
        reconnectInstanceVariables();
        performHyperEdgeTests(true);
    }

    private void removeCyNetworks() {
        Set<CyNetwork> nets = (Set<CyNetwork>) Cytoscape.getNetworkSet();

        for (CyNetwork net : nets) {
            Cytoscape.destroyNetwork(net);
        }
    }

    private void performHyperEdgeTests(boolean cleared) {
        performHasNodeTests();
        performHasEdgeTests();
        performHasEdgeOfTypeTests();
        performGetNameTests();
        performGetNumEdgesTests();
        performGetNumNodesTests();
        performGetAnEdgeTests();
        performHasMultipleEdgesTests();
        performGetNodesTests();
        performGetNodesByEdgeTypesTests();
        performGetEdgesTests();
        performAddAndRemoveEdgeTests();
        performRemoveNodeTests();
        performIsAndSetDirectedTests(cleared);
        performConnectedHyperEdgeTests();
    }

    private void performHasNodeTests() {
        // TEST hasNode()
        Assert.assertFalse(he1.hasNode(null));
        Assert.assertTrue(
            he1.hasNode(S) && he1.hasNode(M) && he1.hasNode(P) &&
            (!he1.hasNode(A)));
    }

    private void performHasEdgeTests() {
        // TEST hasEdge()
        Assert.assertFalse(he1.hasEdge(null));
        Assert.assertTrue(
            he1.hasEdge(he1_sub) && he1.hasEdge(he1_med) &&
            he1.hasEdge(he1_prod) && (!he1.hasEdge(extra)));
    }

    private void performHasEdgeOfTypeTests() {
        // TEST hasEdgeOfType():
        Assert.assertFalse(he1.hasEdgeOfType(null));
        Assert.assertFalse(he1.hasEdgeOfType("substrate1"));
        Assert.assertFalse(he1.hasEdgeOfType(EXTRA));
        Assert.assertTrue(he1.hasEdgeOfType(EdgeTypeMap.SUBSTRATE));
        Assert.assertTrue(he1.hasEdgeOfType(EdgeTypeMap.PRODUCT));
        Assert.assertTrue(he1.hasEdgeOfType(EdgeTypeMap.ACTIVATING_MEDIATOR));
    }

    private void performGetNameTests() {
        // TEST getName():
        Assert.assertTrue("he1".equals(he1.getName()));
        Assert.assertTrue("hd1".equals(hd1.getName()));
        // check names with problemmatic characters for persistence:
        Assert.assertTrue(FUNNY_NAME.equals(funny_he.getName()));
        Assert.assertTrue(
            FUNNY_NODE1.equals(((CyNode) funny_sub.getSource()).getIdentifier()));
        Assert.assertTrue(
            FUNNY_NODE2.equals(((CyNode) funny_prod.getTarget()).getIdentifier()));
    }

    private void performGetNumEdgesTests() {
        // TEST getNumEdges():
        Assert.assertTrue(he1.getNumEdges() == 3);
	Assert.assertTrue(hd1.getNumEdges() == 3);
    }

    private void performGetNumNodesTests() {
        // TEST getNumNodes():
        Assert.assertTrue(he1.getNumNodes() == 3);
        Assert.assertTrue(hd1.getNumNodes() == 2);
    }

    private void performGetAnEdgeTests() {
        // TEST getAnEdge():
        //    null node:
        Assert.assertTrue(he1.getAnEdge(null) == null);
        //    node not in HE:
        Assert.assertTrue(hd1.getAnEdge(A) == null);
        Assert.assertTrue(he1.getAnEdge(S) == he1_sub);
        Assert.assertTrue(he1.getAnEdge(P) == he1_prod);
        Assert.assertTrue(
            (hd1.getAnEdge(S) == hd1_sub) || (hd1.getAnEdge(S) == hd1_imed));
        Assert.assertTrue(hd1.getAnEdge(M) == hd1_med);
    }

    private void performHasMultipleEdgesTests() {
        // TEST hasMultipleEdges():
        //    null node:
        Assert.assertFalse(he1.hasMultipleEdges(null));
        //    node not in HE:
        Assert.assertFalse(hd1.hasMultipleEdges(A));
        Assert.assertFalse(
            he1.hasMultipleEdges(S) || he1.hasMultipleEdges(M) ||
            he1.hasMultipleEdges(P));
        Assert.assertTrue(hd1.hasMultipleEdges(S));
        Assert.assertFalse(hd1.hasMultipleEdges(M));
    }

    private void performGetNodesTests() {
        // TEST getNodes(null);
        List test_list = testIterator(he1.getNodes(null), 3);
        Assert.assertTrue(
            test_list.contains(S) && test_list.contains(M) &&
            test_list.contains(P));
        test_list = testIterator(hd1.getNodes(null), 2);
        Assert.assertTrue(test_list.contains(S) && test_list.contains(M));

        // TEST getNodes():
        test_list = testIterator(he1.getNodes("substrate1"), 0);
        test_list = testIterator(he1.getNodes(null), 3);
        test_list = testIterator(he1.getNodes(EdgeTypeMap.SUBSTRATE), 1);
        Assert.assertTrue(test_list.contains(S));
        test_list = testIterator(he1.getNodes(EdgeTypeMap.ACTIVATING_MEDIATOR),
                                 1);
        Assert.assertTrue(test_list.contains(M));
        test_list = testIterator(he1.getNodes(EdgeTypeMap.PRODUCT), 1);
        Assert.assertTrue(test_list.contains(P));
        test_list = testIterator(hd1.getNodes(EdgeTypeMap.SUBSTRATE), 1);
        Assert.assertTrue(test_list.contains(S));
    }

    private void performGetNodesByEdgeTypesTests() {
        // TEST getNodesByEdgeTypes(null);
        List test_list = testIterator(he1.getNodesByEdgeTypes(null), 3);
        Assert.assertTrue(
            test_list.contains(S) && test_list.contains(M) &&
            test_list.contains(P));
        test_list = testIterator(hd1.getNodesByEdgeTypes(null), 2);
        Assert.assertTrue(test_list.contains(S) && test_list.contains(M));

        // TEST getNodesByEdgeTypes():
        Collection<String> acceptableTypes = new ArrayList<String>();
        acceptableTypes.add("substrate1");
        test_list = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 0);
        test_list = testIterator(he1.getNodesByEdgeTypes(null), 3);
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.SUBSTRATE);
        test_list = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(test_list.contains(S));
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
        test_list = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(test_list.contains(M));
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.PRODUCT);
        test_list = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(test_list.contains(P));
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.SUBSTRATE);
        test_list = testIterator(hd1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(test_list.contains(S));
        acceptableTypes.add(EdgeTypeMap.PRODUCT);
        test_list = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 2);
        Assert.assertTrue(test_list.contains(S) && test_list.contains(P));

        // Add an INHIBITING_MEDIATOR to he1 and test both mediators:
        // NOTE: This will not test persistence with useage of getNodesByEdgeTypes:
        CyNode m2 = Cytoscape.getCyNode("M2", true);
        he1.addEdge(m2, EdgeTypeMap.INHIBITING_MEDIATOR);
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
        acceptableTypes.add(EdgeTypeMap.INHIBITING_MEDIATOR);
        test_list = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 2);
        Assert.assertTrue(test_list.contains(M) && test_list.contains(m2));
        he1.removeNode(m2);
    }

    private void performGetEdgesTests() {
        // TEST getEdges(null):
        List test_list = testIterator(he1.getEdges(null), 3);
        Assert.assertTrue(
            test_list.contains(he1_sub) && test_list.contains(he1_med) &&
            test_list.contains(he1_prod));
        test_list = testIterator(hd1.getEdges(null), 3);
        Assert.assertTrue(test_list.contains(hd1_sub) &&
			  test_list.contains(hd1_med) && test_list.contains(hd1_imed));

        // TEST getEdges():
        test_list = testIterator(he1.getEdges(null), 3);
        // A is not in he1:
        test_list = testIterator(he1.getEdges(A), 0);
        test_list = testIterator(he1.getEdges(S), 1);
        Assert.assertTrue(test_list.contains(he1_sub));
        // test_list = net.getEdge(adjacentEdges[j]);
        // MLC 04/02/08:
        test_list = testIterator(he1.getEdges(M), 1);
        Assert.assertTrue(test_list.contains(he1_med));
        test_list = testIterator(he1.getEdges(P), 1);
        Assert.assertTrue(test_list.contains(he1_prod));

	test_list = testIterator(hd1.getEdges(S), 2);
	Assert.assertTrue(test_list.contains(hd1_sub));
	Assert.assertTrue(test_list.contains(hd1_imed));
    }

    private void performAddAndRemoveEdgeTests() {
        // test addEdge():
        // test null node:
        try {
            he1.addEdge(null, EdgeTypeMap.PRODUCT);
            Assert.fail("should have thrown IllegalStateException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // test null edgeIType:
        try {
            he1.addEdge(A, null);
            Assert.fail("should have thrown IllegalStateException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // test ConnectorNode for node:
        try {
            he1.addEdge(hd1.getConnectorNode(), null);
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // test existing edge:
        try {
            he1.addEdge(S, EdgeTypeMap.SUBSTRATE);
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        //    actually add (EXTRA is unregistered in EdgeTypeMap):
        extra = he1.addEdge(A, EXTRA);
        Assert.assertNotNull(extra);
        Assert.assertTrue(extra.getSource() == A);
        Assert.assertTrue(he1.getNumEdges() == 4);
        Assert.assertTrue(he1.getNumNodes() == 4);
        Assert.assertTrue(he1.removeEdge(extra));
        //    now add with EXTRA registered as EdgeRole.TARGET:
        factory.getEdgeTypeMap().put(EXTRA, EdgeRole.TARGET);

        CyEdge extra1 = he1.addEdge(A, EXTRA);
        Assert.assertNotNull(extra1);
        Assert.assertTrue(extra1.getTarget() == A);
        Assert.assertTrue(he1.getNumEdges() == 4);
        Assert.assertTrue(he1.getNumNodes() == 4);

        //    now add 2 Edges using standard EdgeTypeMap entries:
        CyEdge added_mediator = he1.addEdge(A, EdgeTypeMap.INHIBITING_MEDIATOR);
        CyEdge added_product = he1.addEdge(A, EdgeTypeMap.PRODUCT);
        Assert.assertNotNull(added_mediator);
        Assert.assertNotNull(added_product);
        Assert.assertTrue(added_mediator.getSource() == A);
        Assert.assertTrue(added_product.getTarget() == A);
        Assert.assertTrue(he1.getNumEdges() == 6);
        Assert.assertTrue(he1.getNumNodes() == 4);
        factory.getEdgeTypeMap().remove(EXTRA);

        // test removeEdge():
        Assert.assertTrue(!he1.removeEdge(null));
        //    actually remove:
        // Assert.assertTrue(he1.removeEdge(extra));
        Assert.assertTrue(he1.removeEdge(extra1));
        Assert.assertTrue(he1.removeEdge(added_mediator));
        Assert.assertTrue(he1.removeEdge(added_product));
        // extra should be gone:
        Assert.assertFalse(net1.containsEdge(extra));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(extra));
        Assert.assertTrue(he1.getNumEdges() == 3);
        Assert.assertTrue(he1.getNumNodes() == 3);
    }

    private void performRemoveNodeTests() {
        // test removeNode():
        Assert.assertFalse(he1.removeNode(null));
        // A isn't there:
        Assert.assertFalse(he1.removeNode(A));

        // Now add 3 edges to A:
        CyEdge extra          = he1.addEdge(A, EXTRA);
        CyEdge added_mediator = he1.addEdge(A, EdgeTypeMap.INHIBITING_MEDIATOR);
        CyEdge added_product  = he1.addEdge(A, EdgeTypeMap.PRODUCT);
        Assert.assertTrue(he1.getNumEdges() == 6);
        Assert.assertTrue(he1.getNumNodes() == 4);
        //  now remove A:
        Assert.assertTrue(he1.removeNode(A));
        //   show that all the CyEdges and node are gone:
        Assert.assertTrue(he1.getNumEdges() == 3);
        Assert.assertTrue(he1.getNumNodes() == 3);
        Assert.assertFalse(net1.containsEdge(extra));
        Assert.assertFalse(net1.containsEdge(added_mediator));
        Assert.assertFalse(net1.containsEdge(added_product));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(extra));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(added_mediator));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(added_product));
    }

    private void performIsAndSetDirectedTests(boolean cleared) {
        // TEST isDirected()/setDirected():
        if (cleared) {
            Assert.assertFalse(he1.isDirected());
            Assert.assertFalse(he1.setDirected(true));
        } else {
            Assert.assertTrue(he1.isDirected());
            Assert.assertTrue(he1.setDirected(true));
        }

        Assert.assertTrue(he1.setDirected(true));
        Assert.assertTrue(he1.isDirected());
    }

    private void performConnectedHyperEdgeTests() {
        // test null connector node:
        try {
            // all regular operations should fail:
            shared1.connectHyperEdges(null,
                                      EdgeTypeMap.SUBSTRATE
                                      );
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // test hyperedges not in the same one Network:
        try {
            shared1.connectHyperEdges(unshared1,
                                      
                                      EdgeTypeMap.PRODUCT);
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // Now remove unshared1 from sharedEdgeNet1, so it is only in
        // sharedEdgeNet2. shared1 is in sharedEdgeNet1 and unshared1
        // is in sharedEdgeNet2.
        unshared1.removeFromNetwork(sharedEdgeNet1);

        try {
            shared1.connectHyperEdges(unshared1,
                                      EdgeTypeMap.SUBSTRATE
                                      );
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // reset state:
        unshared1.addToNetwork(sharedEdgeNet1);

        CyEdge sharedE1 = shared1.getAnEdge(shared2.getConnectorNode());
        CyEdge sharedE2 = shared2.getAnEdge(shared3.getConnectorNode());
        CyEdge sharedE3 = shared3.getAnEdge(shared1.getConnectorNode());
        Assert.assertTrue(
            sharedE1 == shared2.getAnEdge(shared1.getConnectorNode()));
        Assert.assertTrue(
            sharedE2 == shared3.getAnEdge(shared2.getConnectorNode()));
        Assert.assertTrue(
            sharedE3 == shared1.getAnEdge(shared3.getConnectorNode()));

        // test that recreating the connection just returns shared:
        CyEdge anotherSharedE1 = shared1.connectHyperEdges(shared2,
                                                           EdgeTypeMap.SUBSTRATE
                                                           );
        Assert.assertTrue(sharedE1 == anotherSharedE1);

        // Now make a real shared edge:
        //        CyEdge shared = shared1.connectHyperEdges(shared2,
        //                                                  EdgeTypeMap.SUBSTRATE,
        //                                                  EdgeTypeMap.PRODUCT);
        //        Assert.assertTrue(shared1.hasEdge(shared));
        Assert.assertTrue(
            (shared1.getNumEdges() == 4) && (shared1.getNumNodes() == 4));
        Assert.assertTrue(shared1.hasEdge(sharedE1));

        Assert.assertTrue(
            (shared2.getNumEdges() == 4) && (shared2.getNumNodes() == 4));
        // addToCyNetwork should fail:
        Assert.assertFalse(shared1.addToNetwork(sharedEdgeNet2));
        // Now remove the shared edge and test that shared1 and shared2
        // are disconnected:
        shared1.removeEdge(sharedE1);
        Assert.assertFalse(shared1.hasEdge(sharedE1));
        Assert.assertTrue(
            (shared1.getNumEdges() == 3) && (shared1.getNumNodes() == 3));
        Assert.assertFalse(shared2.hasEdge(sharedE1));
        Assert.assertTrue(
            (shared2.getNumEdges() == 3) && (shared2.getNumNodes() == 3));
    }

    public void testCopy() {
        manager.reset(false);
        setUp1(true);

        // test null net
        try {
            shared1.copy(null, EdgeFilters.ALL_EDGES_FILTER);
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // test null filter
        try {
            shared1.copy(sharedEdgeNet1, null);
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // test filter that causes too few edges:
        try {
            shared1.copy(sharedEdgeNet1,
                         new EdgeFilter() {
                    public boolean includeEdge(HyperEdge he, CyEdge edge) {
                        return false;
                    }
                });
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // simple test:
        int                       num_hes = manager.getNumHyperEdges(net1);
        Map<HyperEdge, HyperEdge> result  = he1.copy(net1,
                                                     EdgeFilters.ALL_EDGES_FILTER);
        HyperEdge                 he1Copy = result.get(he1);
        Assert.assertTrue(manager.getNumHyperEdges(net1) == (num_hes + 1));
        Assert.assertTrue(he1Copy.getNumEdges() == he1.getNumEdges());
        Assert.assertTrue(he1Copy.getNumNodes() == he1.getNumNodes());

        // Now test copying attributes and copying a copy:
        addExtraUserAttributes(he1Copy.getConnectorNode());
        result = he1Copy.copy(net1, EdgeFilters.ALL_EDGES_FILTER);

        HyperEdge he1CopyCopy = result.get(he1Copy);
        Assert.assertTrue(manager.getNumHyperEdges(net1) == (num_hes + 2));
        Assert.assertTrue(he1CopyCopy.getNumEdges() == he1.getNumEdges());
        Assert.assertTrue(he1CopyCopy.getNumNodes() == he1.getNumNodes());
        // Test that added attributes are the same:
        testExtraUserAttributes(he1CopyCopy.getConnectorNode());
        he1CopyCopy.destroy();

        // Now copy a shared HyperEdge without following shared edges:
        num_hes = manager.getNumHyperEdges(sharedEdgeNet1);
        result  = shared1.copy(sharedEdgeNet1, EdgeFilters.UNSHARED_EDGES_FILTER);

        HyperEdge shared1Copy = result.get(shared1);
        Assert.assertTrue(
            manager.getNumHyperEdges(sharedEdgeNet1) == (num_hes + 1));
        // add checks here:

        // Now copy a shared HyperEdge following shared edges:
        num_hes     = manager.getNumHyperEdges(sharedEdgeNet1);
        result      = shared1.copy(sharedEdgeNet1, EdgeFilters.ALL_EDGES_FILTER);
        shared1Copy = result.get(shared1);

        HyperEdge shared2Copy = result.get(shared2);
        HyperEdge shared3Copy = result.get(shared3);

        Assert.assertTrue(
            manager.getNumHyperEdges(sharedEdgeNet1) == (num_hes + 3));

        // &&&& add checks here:

        // Now test EdgeFilters.EdgeListFilter:
        List<CyEdge> edgeList = new ArrayList<CyEdge>();
        edgeList.add(he1.getAnEdge(S));
        edgeList.add(he1.getAnEdge(M));
        result  = he1.copy(net1, new EdgeFilters.EdgeListFilter(edgeList));
        he1Copy = result.get(he1);
        Assert.assertTrue(
            (he1Copy.getNumNodes() == 2) && he1Copy.hasNode(S) &&
            he1Copy.hasNode(M));

        // Now test EdgeFilters.NodeListFilter:
        List<CyNode> nodeList = new ArrayList<CyNode>();
        nodeList.add(M);
        nodeList.add(P);
        result  = he1.copy(net1, new EdgeFilters.NodeListFilter(nodeList));
        he1Copy = result.get(he1);
        Assert.assertTrue(
            (he1Copy.getNumNodes() == 2) && he1Copy.hasNode(M) &&
            he1Copy.hasNode(P));
        shared1Copy.destroy();
        shared2Copy.destroy();
        shared3Copy.destroy();
        // test copy of regular edges added:
        // test copy of user additional attributes:
    }

    private void testExtraUserAttributes(CyNode node) {
        String       nodeID = node.getIdentifier();
        CyAttributes attrs = Cytoscape.getNodeAttributes();
        Assert.assertTrue(Boolean.TRUE.equals(attrs.getBooleanAttribute(
                                                                        nodeID,
                                                                        "BooleanTest")));
        Assert.assertTrue(
            "string test value".equals(attrs.getStringAttribute(
                                                                nodeID,
                                                                "StringTest")));
        Assert.assertTrue(new Integer(6).equals(attrs.getIntegerAttribute(
                                                                          nodeID,
                                                                          "IntegerTest")));
        Assert.assertTrue(new Double(5.0).equals(attrs.getDoubleAttribute(
                                                                          nodeID,
                                                                          "DoubleTest")));

        List<String> listVal = attrs.getListAttribute(nodeID, "ListTest");
        Assert.assertTrue(
            (listVal.size() == 2) && listVal.contains("list test value1") &&
            listVal.contains("list test value2"));

        Map<String, String> mapVal = attrs.getMapAttribute(nodeID, "MapTest");
        Assert.assertTrue(
            (mapVal.size() == 2) &&
            "map key1 value".equals(mapVal.get("map key1")) &&
            "map key2 value".equals(mapVal.get("map key2")));
        testExtraComplexAttributes(node);
    }

    private void addExtraUserAttributes(CyNode node) {
        String       nodeID = node.getIdentifier();
        CyAttributes attrs = Cytoscape.getNodeAttributes();
        attrs.setAttribute(nodeID, "BooleanTest", new Boolean(true));
        attrs.setAttribute(nodeID, "StringTest", "string test value");
        attrs.setAttribute(nodeID, "IntegerTest", new Integer(6));
        attrs.setAttribute(nodeID, "DoubleTest", new Double(5.0));

        List<String> listTestValue = new ArrayList<String>();
        listTestValue.add("list test value1");
        listTestValue.add("list test value2");
        attrs.setListAttribute(nodeID, "ListTest", listTestValue);

        Map<String, String> mapTestValue = new HashMap<String, String>();
        mapTestValue.put("map key1", "map key1 value");
        mapTestValue.put("map key2", "map key2 value");
        attrs.setMapAttribute(nodeID, "MapTest", mapTestValue);

        // Now add a complex value to test:
        addExtraComplexUserAttributes(node);
    }

    private void addExtraComplexUserAttributes(CyNode node) {
        String                 nodeID  = node.getIdentifier();
        CyAttributes           attrs   = Cytoscape.getNodeAttributes();
        MultiHashMap           mmap    = attrs.getMultiHashMap();
        MultiHashMapDefinition mmapDef = attrs.getMultiHashMapDefinition();

        if (mmapDef.getAttributeValueType("p-valuesTest") < 0) {
            mmapDef.defineAttribute(
                "p-valuesTest",
                // most specific values:
                MultiHashMapDefinition.TYPE_FLOATING_POINT,
                new byte[] {
                    MultiHashMapDefinition.TYPE_STRING,
                    MultiHashMapDefinition.TYPE_INTEGER
                });
        }

        if (mmapDef.getAttributeValueType("TextSourceInfo") < 0) {
            mmapDef.defineAttribute(
                "TextSourceInfo",
                // most specific values:
                MultiHashMapDefinition.TYPE_STRING,
                new byte[] {
                    MultiHashMapDefinition.TYPE_STRING,
                    MultiHashMapDefinition.TYPE_INTEGER,
                    MultiHashMapDefinition.TYPE_INTEGER
                });
        }

        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.5),
                               new Object[] { "Jojo", new Integer(0) });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.6),
                               new Object[] { "Jojo", new Integer(1) });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.6),
                               new Object[] { "Jojo", new Integer(2) });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.7),
                               new Object[] { "Harry", new Integer(0) });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.6),
                               new Object[] { "Harry", new Integer(1) });

        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: sentence1",
                               new Object[] {
                                   "url1", new Integer(0), new Integer(0)
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: sentence2",
                               new Object[] {
                                   "url1", new Integer(0), new Integer(1)
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: sentence3",
                               new Object[] {
                                   "url1", new Integer(0), new Integer(10)
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: publication 1",
                               new Object[] {
                                   "url1", new Integer(1), new Integer(0)
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url2: sentence1",
                               new Object[] {
                                   "url2", new Integer(0), new Integer(6)
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url2: publication 1",
                               new Object[] {
                                   "url2", new Integer(1), new Integer(0)
                               });

    }

    private void testExtraComplexAttributes(CyNode node) {
        String       nodeID = node.getIdentifier();
        CyAttributes attrs = Cytoscape.getNodeAttributes();
        MultiHashMap mmap  = attrs.getMultiHashMap();
        Assert.assertTrue(new Double(0.5).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            "Jojo",
                                                                            new Integer(
            0)
                                                                        })));
        Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            "Jojo",
                                                                            new Integer(
            1)
                                                                        })));
        Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            "Jojo",
                                                                            new Integer(
            2)
                                                                        })));
        Assert.assertTrue(new Double(0.7).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            "Harry",
                                                                            new Integer(
            0)
                                                                        })));
        Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            "Harry",
                                                                            new Integer(
            1)
                                                                        })));

        Assert.assertTrue("url1: sentence1".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url1",
                                                                              new Integer(
            0), new Integer(0)
                                                                          })));
        Assert.assertTrue("url1: sentence2".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url1",
                                                                              new Integer(
            0), new Integer(1)
                                                                          })));
        Assert.assertTrue("url1: sentence3".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url1",
                                                                              new Integer(
            0), new Integer(10)
                                                                          })));

        Assert.assertTrue(
            "url1: publication 1".equals(mmap.getAttributeValue(
                                                                nodeID,
                                                                "TextSourceInfo",
                                                                new Object[] {
                                                                    "url1",
                                                                    new Integer(1),
                                                                    new Integer(0)
                                                                })));

        Assert.assertTrue("url2: sentence1".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url2",
                                                                              new Integer(
            0), new Integer(6)
                                                                          })));
        Assert.assertTrue(
            "url2: publication 1".equals(mmap.getAttributeValue(
                                                                nodeID,
                                                                "TextSourceInfo",
                                                                new Object[] {
                                                                    "url2",
                                                                    new Integer(1),
                                                                    new Integer(0)
                                                                })));
    }

    public void testDestroy() {
        manager.reset(false);
        setUp1(true);

        // test destroy():
        int    num_hes      = manager.getNumHyperEdges(null);
        int    num_edges    = manager.getNumEdges(null);
        int    num_nodes    = manager.getNumNodes(null);
        CyNode he1_cn       = he1.getConnectorNode();
        CyNode hd1_cn       = hd1.getConnectorNode();
        CyNode shared1_cn   = shared1.getConnectorNode();
        CyNode shared2_cn   = shared2.getConnectorNode();
        CyNode unshared1_cn = unshared1.getConnectorNode();

        // Add a regular CyEdge to unshared1_cn:
        CyEdge regularEdge = HEUtils.createHEEdge(unshared1_cn,
                                                A,
                                                HEUtils.generateUUID(null));
        sharedEdgeNet1.restoreEdge(regularEdge);
        he1.destroy();
        hd1.destroy();
        shared1.destroy();
        shared2.destroy();
        shared3.destroy();
        unshared1.destroy();
        // test access to deleted HyperEdges throws exception:
        Assert.assertTrue(he1.getState() == LifeState.DELETED);
        Assert.assertTrue(hd1.isState(LifeState.DELETED));
        System.out.println("LIFESTATE = " + he1.getState());

        try {
            // all regular operations should fail:
            he1.getName();
            Assert.fail("should have thrown IllegalStateException!");
        } catch (IllegalStateException e) {
            // ok
        }

        // Now check that stuff is deleted:
        Assert.assertTrue(manager.getNumHyperEdges(null) == (num_hes - 6));
        Assert.assertTrue(manager.getNumEdges(null) == (num_edges - 17));
        // S,M,P, shared1 and shared2 connector nodes gone:
        Assert.assertTrue(manager.getNumNodes(null) == (num_nodes - 7));

        // all edges should be gone:
        Assert.assertFalse(net1.containsEdge(he1_sub));
        Assert.assertFalse(net1.containsEdge(he1_med));
        Assert.assertFalse(net1.containsEdge(he1_prod));
        Assert.assertFalse(net1.containsEdge(hd1_med));
        Assert.assertFalse(net1.containsEdge(hd1_sub));
        // TODO: FIX: Uncomment when Cytoscape session reader fixed:
         Assert.assertFalse(net1.containsEdge(hd1_imed));

        // leave normal nodes and Connector nodes with regular edges
        // alone:
        Assert.assertTrue(net1.containsNode(S));
        Assert.assertTrue(net1.containsNode(M));
        Assert.assertTrue(net1.containsNode(P));
        Assert.assertFalse(net1.containsNode(he1_cn));
        Assert.assertFalse(net1.containsNode(hd1_cn));
        Assert.assertFalse(sharedEdgeNet1.containsNode(shared1_cn));
        Assert.assertFalse(sharedEdgeNet1.containsNode(shared2_cn));
        Assert.assertTrue(sharedEdgeNet1.containsNode(unshared1_cn));
        Assert.assertFalse(sharedEdgeNet2.containsNode(unshared1_cn));
    }

    public void testConstructors() {
        // TODO: ADD MORE TESTS:
        // TEST constructors:
        manager.reset(false);
        setUp1(true);

        // try null CyNetwork:
        try {
            factory.createHyperEdge(S, EXTRA, M, EdgeTypeMap.PRODUCT, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        // create with an unregistered edgeIType in EdgeTypeMap and default
        // type:
        CyNetwork cnet1  = Cytoscape.createNetwork("cnet1");
        HyperEdge new1   = factory.createHyperEdge(S,
                                                   EXTRA,
                                                   M,
                                                   EdgeTypeMap.PRODUCT,
                                                   cnet1);
        CyEdge    S_edge = new1.getAnEdge(S);
        CyEdge    M_edge = new1.getAnEdge(M);
        Assert.assertTrue(S_edge.getSource() == S);
        Assert.assertTrue(M_edge.getTarget() == M);
        Assert.assertTrue(new1.getAnEdge(S).getSource() == S);
        Assert.assertTrue(new1.getAnEdge(M).getTarget() == M);

        // create with a registered edgeIType in EdgeTypeMap and default
        // type:
        CyNode[] nodes = new CyNode[3];
        String[] types = new String[3];
        nodes[0] = M;
        nodes[1] = P;
        nodes[2] = A;
        types[0] = EdgeTypeMap.SUBSTRATE;
        types[1] = EdgeTypeMap.ACTIVATING_MEDIATOR;
        types[2] = EXTRA;
        factory.getEdgeTypeMap().put(EXTRA, EdgeRole.TARGET);

        HyperEdge new2 = factory.createHyperEdge(nodes, types, cnet1);
        M_edge = new2.getAnEdge(M);

        CyEdge P_edge = new2.getAnEdge(P);
        CyEdge A_edge = new2.getAnEdge(A);
        Assert.assertTrue(M_edge.getSource() == M);
        Assert.assertTrue(P_edge.getSource() == P);
        Assert.assertTrue(new2.getAnEdge(P).getSource() == P);
        Assert.assertTrue(A_edge.getTarget() == A);
        Assert.assertTrue(new2.getAnEdge(A).getTarget() == A);

        // try using connector node in constructor:
        CyNode cn = he1.getConnectorNode();

        try {
            factory.createHyperEdge(cn,
                                    EdgeTypeMap.SUBSTRATE,
                                    M,
                                    EdgeTypeMap.PRODUCT,
                                    null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            factory.createHyperEdge(null, null, null, null, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            factory.createHyperEdge(S, null, null, null, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            factory.createHyperEdge(S, EdgeTypeMap.SUBSTRATE, null, null, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            factory.createHyperEdge(S, EdgeTypeMap.SUBSTRATE, M, null, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        // cleanup network:
        Cytoscape.destroyNetwork(cnet1, false);
    }
}
