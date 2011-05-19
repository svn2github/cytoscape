
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

/* 
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
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
import cytoscape.logger.CyLogger;


/**
 * Test the various HyperEdge  operations.
 * @author Michael L. Creech
 * @version 1.0
 */
public class HyperEdgeTest extends TestBase {
    private static final String        NET1_LOC              = "hyperedge-test-net1.xgmml";
    private static final String        SHARED_EDGE_NET1_LOC  = "hyperedge-test-shared-edge-net1.xgmml";
    private static final String        SHARED_EDGE_NET2_LOC  = "hyperedge-test-shared-edge-net2.xgmml";
    private static final String        FUNNY_NAME            = "&\"'<funny>'\"&";
    private static final String        FUNNY_NODE1           = "&funny1<name>&";
    private static final String        FUNNY_NODE2           = "\"'funny2'\"";
    private static final String        SHARED1_NAME          = "shared1";
    private static final String        SHARED2_NAME          = "shared2";
    private static final String        SHARED3_NAME          = "shared3";
    private static final String        UNSHARED1_NAME        = "unshared1";
    private static final String        SHARED_EDGE_NET1_NAME = "SharedEdgeTest1";
    private static final String        SHARED_EDGE_NET2_NAME = "SharedEdgeTest2";
    private static final String        JOJO                  = "Jojo";
    private static final String        HARRY                 = "Harry";

    private static final int THREE = 3;
    private static final int FOUR  = 4;
    private static final int SIX   = 6;
    private static final int TEN   = 10;
    private HyperEdge         funnyHe;
    private HyperEdge         shared1;
    private HyperEdge         shared2;
    private HyperEdge         shared3;
    private HyperEdge         unshared1;
    private String            funnySubUuid;
    private String            funnyProdUuid;
    private CyEdge            funnySub;
    private CyEdge            funnyProd;
    private CyNetwork         sharedEdgeNet1;
    private CyNetwork         sharedEdgeNet2;
    private PersistenceHelper pHelper              = new PersistenceHelper();

    /** 
     * Bonehead Checkstyle requires constructor and javadoc.
     */
    public HyperEdgeTest () { super();}

    /**
     * JUnit method for running tests for this class.
     * @return the Test to peform.
     */
    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(HyperEdgeTest.class);
    }

    /**
     * Main for test.
     * @param args standard args to main program
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp1(final boolean resetUuids) {
        super.setUp1(resetUuids);

        // test persistent names with special characters in them:
        final CyNode funny1   = Cytoscape.getCyNode(FUNNY_NODE1, true);
        final CyNode funny2 = Cytoscape.getCyNode(FUNNY_NODE2, true);
        funnyHe        = factory.createHyperEdge(funny1,
                                                  EdgeTypeMap.SUBSTRATE,
                                                  funny2,
                                                  EdgeTypeMap.PRODUCT,
                                                  net1);
        funnySub       = funnyHe.getAnEdge(funny1);
        funnyProd      = funnyHe.getAnEdge(funny2);
        funnySubUuid  = funnySub.getIdentifier();
        funnyProdUuid = funnyProd.getIdentifier();
        funnyHe.setName(FUNNY_NAME);

        sharedEdgeNet1 = Cytoscape.createNetwork(SHARED_EDGE_NET1_NAME);
        sharedEdgeNet2 = Cytoscape.createNetwork(SHARED_EDGE_NET2_NAME);

        // sharedEdgeNet1-->shared1,shared2,shared3,unshared1
        // sharedEdgeNet2-->unshared1
        // Shared edge connections: shared1-->shared2-->shared3-->shared1
        shared1 = factory.createHyperEdge(sNode,
                                          EdgeTypeMap.SUBSTRATE,
                                          mNode,
                                          EdgeTypeMap.ACTIVATING_MEDIATOR,
                                          sharedEdgeNet1);
        shared1.setName(SHARED1_NAME);
        shared2 = factory.createHyperEdge(mNode,
                                          EdgeTypeMap.ACTIVATING_MEDIATOR,
                                          pNode,
                                          EdgeTypeMap.PRODUCT,
                                          sharedEdgeNet1);
        shared2.setName(SHARED2_NAME);
        shared3 = factory.createHyperEdge(aNode,
                                          EdgeTypeMap.SUBSTRATE,
                                          pNode,
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
        unshared1 = factory.createHyperEdge(mNode,
                                            EdgeTypeMap.ACTIVATING_MEDIATOR,
                                            pNode,
                                            EdgeTypeMap.PRODUCT,
                                            sharedEdgeNet1);
        unshared1.setName(UNSHARED1_NAME);
        unshared1.addToNetwork(sharedEdgeNet2);
    }

    // When we reload and reset the sample objects, reconnect the
    // instance variables to the newly read objects.
    /**
     * {@inheritDoc}
     */
    protected void reconnectInstanceVariables() {
        super.reconnectInstanceVariables();
        funnyHe   = null;
        funnySub  = null;
        funnyProd = null;
        shared1    = null;
        shared2    = null;
        shared3    = null;
        unshared1  = null;

        CyEdge           edge;
        String           eUuid;
        final Iterator<CyEdge> edgeIt = manager.getEdgesByNetwork(null);

        while (edgeIt.hasNext()) {
            edge   = edgeIt.next();
            eUuid = edge.getIdentifier();

            if (funnySubUuid.equals(eUuid)) {
                funnySub = edge;
            }

            if (funnyProdUuid.equals(eUuid)) {
                funnyProd = edge;
            }
        }

        String              hName;
        HyperEdge           he;
        final Iterator<HyperEdge> heIt = manager.getHyperEdgesByNetwork(null);

        while (heIt.hasNext()) {
            he     = heIt.next();
            hName = he.getName();

            if (FUNNY_NAME.equals(hName)) {
                HEUtils.log("reconnect HE with name = " + FUNNY_NAME);
                funnyHe = he;
            } else if (SHARED1_NAME.equals(hName)) {
                HEUtils.log("reconnect HE with name = " + SHARED1_NAME);
                shared1 = he;
            } else if (SHARED2_NAME.equals(hName)) {
                HEUtils.log("reconnect HE with name = " + SHARED2_NAME);
                shared2 = he;
            } else if (SHARED3_NAME.equals(hName)) {
                HEUtils.log("reconnect HE with name = " + SHARED3_NAME);
                shared3 = he;
            } else if (UNSHARED1_NAME.equals(hName)) {
                HEUtils.log("reconnect HE with name = " + UNSHARED1_NAME);
                unshared1 = he;
            }
        }

        // Now connect up networks:
        final Set<CyNetwork> nets = (Set<CyNetwork>) Cytoscape.getNetworkSet();
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
    /**
     * OVerall HyperEdge tester.
     */
    public void testHyperEdges() {
        setUp1(true);
        pHelper.saveTestHelper(NET1_LOC, net1);
        pHelper.saveTestHelper(SHARED_EDGE_NET1_LOC, sharedEdgeNet1);
        pHelper.saveTestHelper(SHARED_EDGE_NET2_LOC, sharedEdgeNet2);
        performHyperEdgeTests(true);
        removeCyNetworks();
        tearDown1(false);
        tearDown1(true);
        pHelper.restoreTestHelper(NET1_LOC);
        pHelper.restoreTestHelper(SHARED_EDGE_NET1_LOC);
        pHelper.restoreTestHelper(SHARED_EDGE_NET2_LOC);
        reconnectInstanceVariables();
        performHyperEdgeTests(true);
    }

    private void removeCyNetworks() {
        final Set<CyNetwork> nets = (Set<CyNetwork>) Cytoscape.getNetworkSet();

        for (CyNetwork net : nets) {
            Cytoscape.destroyNetwork(net);
        }
    }

    private void performHyperEdgeTests(final boolean cleared) {
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
            he1.hasNode(sNode) && he1.hasNode(mNode) && he1.hasNode(pNode) &&
            (!he1.hasNode(aNode)));
    }

    private void performHasEdgeTests() {
        // TEST hasEdge()
        Assert.assertFalse(he1.hasEdge(null));
        Assert.assertTrue(
            he1.hasEdge(he1Sub) && he1.hasEdge(he1Med) &&
            he1.hasEdge(he1Prod) && (!he1.hasEdge(extra)));
    }

    private void performHasEdgeOfTypeTests() {
        // TEST hasEdgeOfType():
        Assert.assertFalse(he1.hasEdgeOfType(null));
        Assert.assertFalse(he1.hasEdgeOfType("substrate1"));
        Assert.assertFalse(he1.hasEdgeOfType(EXTRA_LABEL));
        Assert.assertTrue(he1.hasEdgeOfType(EdgeTypeMap.SUBSTRATE));
        Assert.assertTrue(he1.hasEdgeOfType(EdgeTypeMap.PRODUCT));
        Assert.assertTrue(he1.hasEdgeOfType(EdgeTypeMap.ACTIVATING_MEDIATOR));
    }

    private void performGetNameTests() {
        // TEST getName():
        Assert.assertTrue("he1".equals(he1.getName()));
        Assert.assertTrue("hd1".equals(hd1.getName()));
        // check names with problematic characters for persistence:
        Assert.assertTrue(FUNNY_NAME.equals(funnyHe.getName()));
        Assert.assertTrue(
            FUNNY_NODE1.equals(((CyNode) funnySub.getSource()).getIdentifier()));
        Assert.assertTrue(
            FUNNY_NODE2.equals(((CyNode) funnyProd.getTarget()).getIdentifier()));
    }

    private void performGetNumEdgesTests() {
        // TEST getNumEdges():
        Assert.assertTrue(he1.getNumEdges() == THREE);
	Assert.assertTrue(hd1.getNumEdges() == THREE);
    }

    private void performGetNumNodesTests() {
        // TEST getNumNodes():
        Assert.assertTrue(he1.getNumNodes() == THREE);
        Assert.assertTrue(hd1.getNumNodes() == 2);
    }

    private void performGetAnEdgeTests() {
        // TEST getAnEdge():
        //    null node:
        Assert.assertTrue(he1.getAnEdge(null) == null);
        //    node not in HE:
        Assert.assertTrue(hd1.getAnEdge(aNode) == null);
        Assert.assertTrue(he1.getAnEdge(sNode) == he1Sub);
        Assert.assertTrue(he1.getAnEdge(pNode) == he1Prod);
        Assert.assertTrue(
            (hd1.getAnEdge(sNode) == hd1Sub) || (hd1.getAnEdge(sNode) == hd1Imed));
        Assert.assertTrue(hd1.getAnEdge(mNode) == hd1Med);
    }

    private void performHasMultipleEdgesTests() {
        // TEST hasMultipleEdges():
        //    null node:
        Assert.assertFalse(he1.hasMultipleEdges(null));
        //    node not in HE:
        Assert.assertFalse(hd1.hasMultipleEdges(aNode));
        Assert.assertFalse(
            he1.hasMultipleEdges(sNode) || he1.hasMultipleEdges(mNode) ||
            he1.hasMultipleEdges(pNode));
        Assert.assertTrue(hd1.hasMultipleEdges(sNode));
        Assert.assertFalse(hd1.hasMultipleEdges(mNode));
    }

    private void performGetNodesTests() {
        // TEST getNodes(null);
        List<CyNode> testList = testIterator(he1.getNodes(null), THREE);
        Assert.assertTrue(
            testList.contains(sNode) && testList.contains(mNode) &&
            testList.contains(pNode));
        testList = testIterator(hd1.getNodes(null), 2);
        Assert.assertTrue(testList.contains(sNode) && testList.contains(mNode));

        // TEST getNodes():
        testList = testIterator(he1.getNodes("substrate1"), 0);
        testList = testIterator(he1.getNodes(null), THREE);
        testList = testIterator(he1.getNodes(EdgeTypeMap.SUBSTRATE), 1);
        Assert.assertTrue(testList.contains(sNode));
        testList = testIterator(he1.getNodes(EdgeTypeMap.ACTIVATING_MEDIATOR),
                                 1);
        Assert.assertTrue(testList.contains(mNode));
        testList = testIterator(he1.getNodes(EdgeTypeMap.PRODUCT), 1);
        Assert.assertTrue(testList.contains(pNode));
        testList = testIterator(hd1.getNodes(EdgeTypeMap.SUBSTRATE), 1);
        Assert.assertTrue(testList.contains(sNode));
    }

    private void performGetNodesByEdgeTypesTests() {
        // TEST getNodesByEdgeTypes(null);
        List<CyNode> testList = testIterator(he1.getNodesByEdgeTypes(null), THREE);
        Assert.assertTrue(
            testList.contains(sNode) && testList.contains(mNode) &&
            testList.contains(pNode));
        testList = testIterator(hd1.getNodesByEdgeTypes(null), 2);
        Assert.assertTrue(testList.contains(sNode) && testList.contains(mNode));

        // TEST getNodesByEdgeTypes():
        final Collection<String> acceptableTypes = new ArrayList<String>();
        acceptableTypes.add("substrate1");
        testList = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 0);
        testList = testIterator(he1.getNodesByEdgeTypes(null), THREE);
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.SUBSTRATE);
        testList = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(testList.contains(sNode));
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
        testList = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(testList.contains(mNode));
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.PRODUCT);
        testList = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(testList.contains(pNode));
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.SUBSTRATE);
        testList = testIterator(hd1.getNodesByEdgeTypes(acceptableTypes), 1);
        Assert.assertTrue(testList.contains(sNode));
        acceptableTypes.add(EdgeTypeMap.PRODUCT);
        testList = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 2);
        Assert.assertTrue(testList.contains(sNode) && testList.contains(pNode));

        // Add an INHIBITING_MEDIATOR to he1 and test both mediators:
        // NOTE: This will not test persistence with useage of getNodesByEdgeTypes:
        final CyNode m2 = Cytoscape.getCyNode("M2", true);
        he1.addEdge(m2, EdgeTypeMap.INHIBITING_MEDIATOR);
        acceptableTypes.clear();
        acceptableTypes.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
        acceptableTypes.add(EdgeTypeMap.INHIBITING_MEDIATOR);
        testList = testIterator(he1.getNodesByEdgeTypes(acceptableTypes), 2);
        Assert.assertTrue(testList.contains(mNode) && testList.contains(m2));
        he1.removeNode(m2);
    }

    private void performGetEdgesTests() {
        // TEST getEdges(null):
        List<CyEdge> testList = testIterator(he1.getEdges(null), THREE);
        Assert.assertTrue(
            testList.contains(he1Sub) && testList.contains(he1Med) &&
            testList.contains(he1Prod));
        testList = testIterator(hd1.getEdges(null), THREE);
        Assert.assertTrue(testList.contains(hd1Sub) &&
			  testList.contains(hd1Med) && testList.contains(hd1Imed));

        // TEST getEdges():
        testList = testIterator(he1.getEdges(null), THREE);
        // A is not in he1:
        testList = testIterator(he1.getEdges(aNode), 0);
        testList = testIterator(he1.getEdges(sNode), 1);
        Assert.assertTrue(testList.contains(he1Sub));
        // testList = net.getEdge(adjacentEdges[j]);
        // MLC 04/02/08:
        testList = testIterator(he1.getEdges(mNode), 1);
        Assert.assertTrue(testList.contains(he1Med));
        testList = testIterator(he1.getEdges(pNode), 1);
        Assert.assertTrue(testList.contains(he1Prod));

	testList = testIterator(hd1.getEdges(sNode), 2);
	Assert.assertTrue(testList.contains(hd1Sub));
	Assert.assertTrue(testList.contains(hd1Imed));
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
            he1.addEdge(aNode, null);
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
            he1.addEdge(sNode, EdgeTypeMap.SUBSTRATE);
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        //    actually add (EXTRA is unregistered in EdgeTypeMap):
        extra = he1.addEdge(aNode, EXTRA_LABEL);
        Assert.assertNotNull(extra);
        Assert.assertTrue(extra.getSource() == aNode);
        Assert.assertTrue(he1.getNumEdges() == FOUR);
        Assert.assertTrue(he1.getNumNodes() == FOUR);
        Assert.assertTrue(he1.removeEdge(extra));
        //    now add with EXTRA registered as EdgeRole.TARGET:
        factory.getEdgeTypeMap().put(EXTRA_LABEL, EdgeRole.TARGET);

        final CyEdge extra1 = he1.addEdge(aNode, EXTRA_LABEL);
        Assert.assertNotNull(extra1);
        Assert.assertTrue(extra1.getTarget() == aNode);
        Assert.assertTrue(he1.getNumEdges() == FOUR);
        Assert.assertTrue(he1.getNumNodes() == FOUR);

        //    now add 2 Edges using standard EdgeTypeMap entries:
        final CyEdge addedMediator = he1.addEdge(aNode, EdgeTypeMap.INHIBITING_MEDIATOR);
        final CyEdge addedProduct = he1.addEdge(aNode, EdgeTypeMap.PRODUCT);
        Assert.assertNotNull(addedMediator);
        Assert.assertNotNull(addedProduct);
        Assert.assertTrue(addedMediator.getSource() == aNode);
        Assert.assertTrue(addedProduct.getTarget() == aNode);
        Assert.assertTrue(he1.getNumEdges() == SIX);
        Assert.assertTrue(he1.getNumNodes() == FOUR);
        factory.getEdgeTypeMap().remove(EXTRA_LABEL);

        // test removeEdge():
        Assert.assertTrue(!he1.removeEdge(null));
        //    actually remove:
        // Assert.assertTrue(he1.removeEdge(extra));
        Assert.assertTrue(he1.removeEdge(extra1));
        Assert.assertTrue(he1.removeEdge(addedMediator));
        Assert.assertTrue(he1.removeEdge(addedProduct));
        // extra should be gone:
        Assert.assertFalse(net1.containsEdge(extra));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(extra));
        Assert.assertTrue(he1.getNumEdges() == THREE);
        Assert.assertTrue(he1.getNumNodes() == THREE);
    }

    private void performRemoveNodeTests() {
        // test removeNode():
        Assert.assertFalse(he1.removeNode(null));
        // A isn't there:
        Assert.assertFalse(he1.removeNode(aNode));

        // Now add 3 edges to A:
        final CyEdge extra          = he1.addEdge(aNode, EXTRA_LABEL);
        final CyEdge addedMediator = he1.addEdge(aNode, EdgeTypeMap.INHIBITING_MEDIATOR);
        final CyEdge addedProduct  = he1.addEdge(aNode, EdgeTypeMap.PRODUCT);
        Assert.assertTrue(he1.getNumEdges() == SIX);
        Assert.assertTrue(he1.getNumNodes() == FOUR);
        //  now remove A:
        Assert.assertTrue(he1.removeNode(aNode));
        //   show that all the CyEdges and node are gone:
        Assert.assertTrue(he1.getNumEdges() == THREE);
        Assert.assertTrue(he1.getNumNodes() == THREE);
        Assert.assertFalse(net1.containsEdge(extra));
        Assert.assertFalse(net1.containsEdge(addedMediator));
        Assert.assertFalse(net1.containsEdge(addedProduct));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(extra));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(added_mediator));
        // Assert.assertFalse(Cytoscape.getRootGraph().containsEdge(added_product));
    }

    private void performIsAndSetDirectedTests(final boolean cleared) {
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

        final CyEdge sharedE1 = shared1.getAnEdge(shared2.getConnectorNode());
        final CyEdge sharedE2 = shared2.getAnEdge(shared3.getConnectorNode());
        final CyEdge sharedE3 = shared3.getAnEdge(shared1.getConnectorNode());
        Assert.assertTrue(
            sharedE1 == shared2.getAnEdge(shared1.getConnectorNode()));
        Assert.assertTrue(
            sharedE2 == shared3.getAnEdge(shared2.getConnectorNode()));
        Assert.assertTrue(
            sharedE3 == shared1.getAnEdge(shared3.getConnectorNode()));

        // test that recreating the connection just returns shared:
        final CyEdge anotherSharedE1 = shared1.connectHyperEdges(shared2,
                                                           EdgeTypeMap.SUBSTRATE
                                                           );
        Assert.assertTrue(sharedE1 == anotherSharedE1);

        // Now make a real shared edge:
        //        CyEdge shared = shared1.connectHyperEdges(shared2,
        //                                                  EdgeTypeMap.SUBSTRATE,
        //                                                  EdgeTypeMap.PRODUCT);
        //        Assert.assertTrue(shared1.hasEdge(shared));
        Assert.assertTrue(
            (shared1.getNumEdges() == FOUR) && (shared1.getNumNodes() == FOUR));
        Assert.assertTrue(shared1.hasEdge(sharedE1));

        Assert.assertTrue(
            (shared2.getNumEdges() == FOUR) && (shared2.getNumNodes() == FOUR));
        // addToCyNetwork should fail:
        Assert.assertFalse(shared1.addToNetwork(sharedEdgeNet2));
        // Now remove the shared edge and test that shared1 and shared2
        // are disconnected:
        shared1.removeEdge(sharedE1);
        Assert.assertFalse(shared1.hasEdge(sharedE1));
        Assert.assertTrue(
            (shared1.getNumEdges() == THREE) && (shared1.getNumNodes() == THREE));
        Assert.assertFalse(shared2.hasEdge(sharedE1));
        Assert.assertTrue(
            (shared2.getNumEdges() == THREE) && (shared2.getNumNodes() == THREE));
    }

    /**
     * Test copying HyperEdge structures.
     */
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
                    public boolean includeEdge(final HyperEdge he, final CyEdge edge) {
                        return false;
                    }
                });
            Assert.fail("should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            // ok
            HEUtils.log("OK. Expected Exception: " + e.getMessage());
        }

        // simple test:
        int                       numHes = manager.getNumHyperEdges(net1);
        Map<HyperEdge, HyperEdge> result  = he1.copy(net1,
                                                     EdgeFilters.ALL_EDGES_FILTER);
        HyperEdge                 he1Copy = result.get(he1);
        Assert.assertTrue(manager.getNumHyperEdges(net1) == (numHes + 1));
        Assert.assertTrue(he1Copy.getNumEdges() == he1.getNumEdges());
        Assert.assertTrue(he1Copy.getNumNodes() == he1.getNumNodes());

        // Now test copying attributes and copying a copy:
        addExtraUserAttributes(he1Copy.getConnectorNode());
        result = he1Copy.copy(net1, EdgeFilters.ALL_EDGES_FILTER);

        final HyperEdge he1CopyCopy = result.get(he1Copy);
        Assert.assertTrue(manager.getNumHyperEdges(net1) == (numHes + 2));
        Assert.assertTrue(he1CopyCopy.getNumEdges() == he1.getNumEdges());
        Assert.assertTrue(he1CopyCopy.getNumNodes() == he1.getNumNodes());
        // Test that added attributes are the same:
        testExtraUserAttributes(he1CopyCopy.getConnectorNode());
        he1CopyCopy.destroy();

        // Now copy a shared HyperEdge without following shared edges:
        numHes = manager.getNumHyperEdges(sharedEdgeNet1);
        result  = shared1.copy(sharedEdgeNet1, EdgeFilters.UNSHARED_EDGES_FILTER);

        HyperEdge shared1Copy = result.get(shared1);
        Assert.assertTrue(
            manager.getNumHyperEdges(sharedEdgeNet1) == (numHes + 1));
        // add checks here:

        // Now copy a shared HyperEdge following shared edges:
        numHes     = manager.getNumHyperEdges(sharedEdgeNet1);
        result      = shared1.copy(sharedEdgeNet1, EdgeFilters.ALL_EDGES_FILTER);
        shared1Copy = result.get(shared1);

        final HyperEdge shared2Copy = result.get(shared2);
        final HyperEdge shared3Copy = result.get(shared3);

        Assert.assertTrue(
            manager.getNumHyperEdges(sharedEdgeNet1) == (numHes + THREE));

        // &&&& add checks here:

        // Now test EdgeFilters.EdgeListFilter:
        final List<CyEdge> edgeList = new ArrayList<CyEdge>();
        edgeList.add(he1.getAnEdge(sNode));
        edgeList.add(he1.getAnEdge(mNode));
        result  = he1.copy(net1, new EdgeFilters.EdgeListFilter(edgeList));
        he1Copy = result.get(he1);
        Assert.assertTrue(
            (he1Copy.getNumNodes() == 2) && he1Copy.hasNode(sNode) &&
            he1Copy.hasNode(mNode));

        // Now test EdgeFilters.NodeListFilter:
        final List<CyNode> nodeList = new ArrayList<CyNode>();
        nodeList.add(mNode);
        nodeList.add(pNode);
        result  = he1.copy(net1, new EdgeFilters.NodeListFilter(nodeList));
        he1Copy = result.get(he1);
        Assert.assertTrue(
            (he1Copy.getNumNodes() == 2) && he1Copy.hasNode(mNode) &&
            he1Copy.hasNode(pNode));
        shared1Copy.destroy();
        shared2Copy.destroy();
        shared3Copy.destroy();
        // test copy of regular edges added:
        // test copy of user additional attributes:
    }

    private void testExtraUserAttributes(final CyNode node) {
        final String       nodeID = node.getIdentifier();
        final CyAttributes attrs = Cytoscape.getNodeAttributes();
        Assert.assertTrue(Boolean.TRUE.equals(attrs.getBooleanAttribute(
                                                                        nodeID,
                                                                        "BooleanTest")));
        Assert.assertTrue(
            "string test value".equals(attrs.getStringAttribute(
                                                                nodeID,
                                                                "StringTest")));
        final Integer sixInt = SIX;
        Assert.assertTrue(sixInt.equals(attrs.getIntegerAttribute(
                                                                          nodeID,
                                                                          "IntegerTest")));
        Assert.assertTrue(new Double(5.0).equals(attrs.getDoubleAttribute(
                                                                          nodeID,
                                                                          "DoubleTest")));

        final List<String> listVal = attrs.getListAttribute(nodeID, "ListTest");
        Assert.assertTrue(
            (listVal.size() == 2) && listVal.contains("list test value1") &&
            listVal.contains("list test value2"));

        final Map<String, String> mapVal = attrs.getMapAttribute(nodeID, "MapTest");
        Assert.assertTrue(
            (mapVal.size() == 2) &&
            "map key1 value".equals(mapVal.get("map key1")) &&
            "map key2 value".equals(mapVal.get("map key2")));
        testExtraComplexAttributes(node);
    }

    private void addExtraUserAttributes(final CyNode node) {
        final String       nodeID = node.getIdentifier();
        final CyAttributes attrs = Cytoscape.getNodeAttributes();
        attrs.setAttribute(nodeID, "BooleanTest", true);
        attrs.setAttribute(nodeID, "StringTest", "string test value");
        attrs.setAttribute(nodeID, "IntegerTest", SIX);
        attrs.setAttribute(nodeID, "DoubleTest", new Double(5.0));

        final List<String> listTestValue = new ArrayList<String>();
        listTestValue.add("list test value1");
        listTestValue.add("list test value2");
        attrs.setListAttribute(nodeID, "ListTest", listTestValue);

        final Map<String, String> mapTestValue = new HashMap<String, String>();
        mapTestValue.put("map key1", "map key1 value");
        mapTestValue.put("map key2", "map key2 value");
        attrs.setMapAttribute(nodeID, "MapTest", mapTestValue);

        // Now add a complex value to test:
        addExtraComplexUserAttributes(node);
    }

    private void addExtraComplexUserAttributes(final CyNode node) {
        final String                 nodeID  = node.getIdentifier();
        final CyAttributes           attrs   = Cytoscape.getNodeAttributes();
        final MultiHashMap           mmap    = attrs.getMultiHashMap();
        final MultiHashMapDefinition mmapDef = attrs.getMultiHashMapDefinition();

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
                               new Object[] { JOJO, 0 });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.6),
                               new Object[] { JOJO, 1 });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.6),
                               new Object[] { JOJO, 2 });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.7),
                               new Object[] { HARRY, 0 });
        mmap.setAttributeValue(nodeID,
                               "p-valuesTest",
                               new Double(0.6),
                               new Object[] { HARRY, 1 });

        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: sentence1",
                               new Object[] {
                                   "url1", 0, 0
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: sentence2",
                               new Object[] {
                                   "url1", 0, 1
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: sentence3",
                               new Object[] {
                                   "url1", 0, TEN
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url1: publication 1",
                               new Object[] {
                                   "url1", 1, 0
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url2: sentence1",
                               new Object[] {
                                   "url2", 0, SIX
                               });
        mmap.setAttributeValue(nodeID,
                               "TextSourceInfo",
                               "url2: publication 1",
                               new Object[] {
                                   "url2", 1, 0});

    }

    private void testExtraComplexAttributes(final CyNode node) {
        final String       nodeID = node.getIdentifier();
        final CyAttributes attrs = Cytoscape.getNodeAttributes();
        final MultiHashMap mmap  = attrs.getMultiHashMap();
        Assert.assertTrue(new Double(0.5).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            JOJO,
                                                                            0
                                                                        })));
        Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            JOJO,
                                                                            1
                                                                        })));
        Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            JOJO,
                                                                            2
                                                                        })));
        Assert.assertTrue(new Double(0.7).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            HARRY,
                                                                            0
                                                                        })));
        Assert.assertTrue(new Double(0.6).equals(mmap.getAttributeValue(
                                                                        nodeID,
                                                                        "p-valuesTest",
                                                                        new Object[] {
                                                                            HARRY,
                                                                            1
                                                                        })));

        Assert.assertTrue("url1: sentence1".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url1",
                                                                              0,0
                                                                          })));
        Assert.assertTrue("url1: sentence2".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url1",
                                                                              0,1
                                                                          })));
        Assert.assertTrue("url1: sentence3".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url1",
                                                                              0,TEN
                                                                          })));

        Assert.assertTrue(
            "url1: publication 1".equals(mmap.getAttributeValue(
                                                                nodeID,
                                                                "TextSourceInfo",
                                                                new Object[] {
                                                                    "url1",
                                                                    1,0
                                                                })));

        Assert.assertTrue("url2: sentence1".equals(mmap.getAttributeValue(
                                                                          nodeID,
                                                                          "TextSourceInfo",
                                                                          new Object[] {
                                                                              "url2",
                                                                              0,SIX
                                                                          })));
        Assert.assertTrue(
            "url2: publication 1".equals(mmap.getAttributeValue(
                                                                nodeID,
                                                                "TextSourceInfo",
                                                                new Object[] {
                                                                    "url2",
                                                                    1,0
                                                                })));
    }

    /**
     * Test destroying hyperedges.
     */
    public void testDestroy() {
        manager.reset(false);
        setUp1(true);

        // test destroy():
        final int    numHes      = manager.getNumHyperEdges(null);
        final int    numEdges    = manager.getNumEdges(null);
        final int    numNodes    = manager.getNumNodes(null);
        final CyNode he1Cn       = he1.getConnectorNode();
        final CyNode hd1Cn       = hd1.getConnectorNode();
        final CyNode shared1Cn   = shared1.getConnectorNode();
        final CyNode shared2Cn   = shared2.getConnectorNode();
        final CyNode unshared1Cn = unshared1.getConnectorNode();

        // Add a regular CyEdge to unshared1_cn:
        final CyEdge regularEdge = HEUtils.createHEEdge(unshared1Cn,
                                                aNode,
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
        CyLogger.getLogger().debug("LIFESTATE = " + he1.getState());

        try {
            // all regular operations should fail:
            he1.getName();
            Assert.fail("should have thrown IllegalStateException!");
        } catch (IllegalStateException e) {
            // ok
        }

        // Now check that stuff is deleted:
        Assert.assertTrue(manager.getNumHyperEdges(null) == (numHes - SIX));
        Assert.assertTrue(manager.getNumEdges(null) == (numEdges - 17));
        // S,M,P, shared1 and shared2 connector nodes gone:
        Assert.assertTrue(manager.getNumNodes(null) == (numNodes - 7));

        // all edges should be gone:
        Assert.assertFalse(net1.containsEdge(he1Sub));
        Assert.assertFalse(net1.containsEdge(he1Med));
        Assert.assertFalse(net1.containsEdge(he1Prod));
        Assert.assertFalse(net1.containsEdge(hd1Med));
        Assert.assertFalse(net1.containsEdge(hd1Sub));
        // TODO FIX: Uncomment when Cytoscape session reader fixed:
         Assert.assertFalse(net1.containsEdge(hd1Imed));

        // leave normal nodes and Connector nodes with regular edges
        // alone:
        Assert.assertTrue(net1.containsNode(sNode));
        Assert.assertTrue(net1.containsNode(mNode));
        Assert.assertTrue(net1.containsNode(pNode));
        Assert.assertFalse(net1.containsNode(he1Cn));
        Assert.assertFalse(net1.containsNode(hd1Cn));
        Assert.assertFalse(sharedEdgeNet1.containsNode(shared1Cn));
        Assert.assertFalse(sharedEdgeNet1.containsNode(shared2Cn));
        Assert.assertTrue(sharedEdgeNet1.containsNode(unshared1Cn));
        Assert.assertFalse(sharedEdgeNet2.containsNode(unshared1Cn));
    }

    /**
     * Test the HyperEdge constructors.
     */
    public void testConstructors() {
        // TODO ADD MORE TESTS:
        // TEST constructors:
        manager.reset(false);
        setUp1(true);

        // try null CyNetwork:
        try {
            factory.createHyperEdge(sNode, EXTRA_LABEL, mNode, EdgeTypeMap.PRODUCT, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        // create with an unregistered edgeIType in EdgeTypeMap and default
        // type:
        final CyNetwork cnet1  = Cytoscape.createNetwork("cnet1");
        final HyperEdge new1   = factory.createHyperEdge(sNode,
                                                   EXTRA_LABEL,
                                                   mNode,
                                                   EdgeTypeMap.PRODUCT,
                                                   cnet1);
        final CyEdge    sEdge = new1.getAnEdge(sNode);
        CyEdge    mEdge = new1.getAnEdge(mNode);
        Assert.assertTrue(sEdge.getSource() == sNode);
        Assert.assertTrue(mEdge.getTarget() == mNode);
        Assert.assertTrue(new1.getAnEdge(sNode).getSource() == sNode);
        Assert.assertTrue(new1.getAnEdge(mNode).getTarget() == mNode);

        // create with a registered edgeIType in EdgeTypeMap and default
        // type:
        final CyNode[] nodes = new CyNode[THREE];
        final String[] types = new String[THREE];
        nodes[0] = mNode;
        nodes[1] = pNode;
        nodes[2] = aNode;
        types[0] = EdgeTypeMap.SUBSTRATE;
        types[1] = EdgeTypeMap.ACTIVATING_MEDIATOR;
        types[2] = EXTRA_LABEL;
        factory.getEdgeTypeMap().put(EXTRA_LABEL, EdgeRole.TARGET);

        final HyperEdge new2 = factory.createHyperEdge(nodes, types, cnet1);
        mEdge = new2.getAnEdge(mNode);

        final CyEdge pEdge = new2.getAnEdge(pNode);
        final CyEdge aEdge = new2.getAnEdge(aNode);
        Assert.assertTrue(mEdge.getSource() == mNode);
        Assert.assertTrue(pEdge.getSource() == pNode);
        Assert.assertTrue(new2.getAnEdge(pNode).getSource() == pNode);
        Assert.assertTrue(aEdge.getTarget() == aNode);
        Assert.assertTrue(new2.getAnEdge(aNode).getTarget() == aNode);

        // try using connector node in constructor:
        final CyNode cn = he1.getConnectorNode();

        try {
            factory.createHyperEdge(cn,
                                    EdgeTypeMap.SUBSTRATE,
                                    mNode,
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
            factory.createHyperEdge(sNode, null, null, null, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            factory.createHyperEdge(sNode, EdgeTypeMap.SUBSTRATE, null, null, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            factory.createHyperEdge(sNode, EdgeTypeMap.SUBSTRATE, mNode, null, null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }

        // cleanup network:
        Cytoscape.destroyNetwork(cnet1, false);
    }
}
