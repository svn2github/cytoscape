
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
* Tue Nov 07 06:45:29 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge.
* Mon Nov 06 09:28:23 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode.
* Thu Nov 02 05:20:50 2006 (Michael L. Creech) creech@w235krbza760
* Changed to usage of new shared HyperEdges API.
* Sat Aug 12 05:52:16 2006 (Michael L. Creech) creech@w235krbza760
*  Removed savedTestHelper() and restoreTestHelper().
* Sat Jul 29 14:18:20 2006 (Michael L. Creech) creech@w235krbza760
*  Changed MEDIATOR-->ACTIVATING_MEDIATOR & INHIBITING_MEDIATOR.
* Wed Sep 28 06:51:32 2005 (Michael L. Creech) creech@Dill
*  Updated to check Edge direction after creation.
********************************************************************************
*/
package cytoscape.hyperedge.unittest;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.HyperEdgeManager;
import cytoscape.hyperedge.impl.utils.HEUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * HyperEdge test support methods and creation of
 * some commonly used HyperEdge structures.
 * @author Michael L. Creech
 * @version 1.0
 */
public class TestBase extends TestCase {
    /**
     * name of the extra edge used in various HyperEdge tests.
     */
    protected static final String EXTRA_LABEL         = "extra";
    private static final String S_LABEL = "S";
    private static final String M_LABEL = "M";
    private static final String P_LABEL = "P";
    private static final String A_LABEL = "A";
    private static final String HE1_LABEL = "he1";
    private static final String HD1_LABEL = "hd1";
    
    /**
     * The singleton factory instance.
     */
    protected HyperEdgeFactory    factory       = HyperEdgeFactory.INSTANCE;
    /**
     * The HyperEdgeManager to use.
     */
    protected HyperEdgeManager    manager       = factory.getHyperEdgeManager();
    /**
     * HyperEdge he1's edge to substrate.
     */
    protected CyEdge              he1Sub;
    /**
     * HyperEdge he1's edge to mediator.
     */
    protected CyEdge              he1Med;
    /**
     * HyperEdge he1's edge to product.
     */
    protected CyEdge              he1Prod;
    /**
     * HyperEdge hd1's edge to substrate.
     */
    protected CyEdge              hd1Sub;
    /**
     * HyperEdge hd1's edge to inhibiting mediator.
     */
    protected CyEdge              hd1Imed;
    /**
     * HyperEdge hd1's edge to mediator.
     */
    protected CyEdge              hd1Med;
    /**
     * An extra edge used for testing.
     */
    protected CyEdge              extra;
    /**
     * HyperEdge he1's UUID of the edge to the substrate.
     */
    protected String              he1SubUuid;
    /**
     * HyperEdge he1's UUID of the edge to the mediator.
     */
    protected String              he1MedUuid;
    /**
     * HyperEdge he1's UUID of the edge to the product.
     */
    protected String              he1ProdUuid;
    /**
     * HyperEdge hd1's UUID of the edge to the substrate.
     */
    protected String              hd1SubUuid;
    /**
     * HyperEdge hd1's UUID of the edge to the inhibiting mediator.
     */
    protected String              hd1ImedUuid;
    /**
     * HyperEdge hd1's UUID of the edge to the mediator.
     */
    protected String              hd1MedUuid;
    /**
     * UUID of the "extra" edge.
     */
    protected String              extraUuid;
    /**
     * Substrate CyNode.
     */
    protected CyNode              sNode;
    /**
     * Mediator CyNode.
     */
    protected CyNode              mNode;
    /**
     * Product CyNode.
     */
    protected CyNode              pNode;
    /**
     *  CyNode A.
     */
    protected CyNode              aNode;
    /**
     * CyNetwork net1.
     */
    protected CyNetwork           net1;
    /**
     * HyperEdge he1.
     */
    protected HyperEdge           he1;

    /**
     * Homodimer hd1.
     * (homodimer--S-M-S)
     */
    protected HyperEdge           hd1;

    /**
     * @param resetUuids true when we should reset the saved
     *                    uuids (e.g., he1SubUuid) used for matching
     *                    when the reconnectInstanceVariables () is
     *                    called.
     */
    protected void setUp1(final boolean resetUuids) {
        sNode    = Cytoscape.getCyNode(S_LABEL, true);
        mNode    = Cytoscape.getCyNode(M_LABEL, true);
        pNode    = Cytoscape.getCyNode(P_LABEL, true);
        aNode    = Cytoscape.getCyNode(A_LABEL, true);
        net1 = Cytoscape.createNetwork("net1");
        // he1 = S(su)M(ame)P(pr) (for net1)
        he1  = factory.createHyperEdge(sNode,
                                       EdgeTypeMap.SUBSTRATE,
                                       mNode,
                                       EdgeTypeMap.ACTIVATING_MEDIATOR,
                                       pNode,
                                       EdgeTypeMap.PRODUCT,
                                       net1);
        // MLC 08/19/06:
        // he1.addToCyNetwork(net2);

        // he1.addToCyNetwork(net3);
        he1.setName(HE1_LABEL);
        he1Sub  = he1.getAnEdge(sNode);
        he1Med  = he1.getAnEdge(mNode);
        he1Prod = he1.getAnEdge(pNode);
        Assert.assertTrue(he1Sub.getSource() == sNode);
        Assert.assertTrue(he1Med.getSource() == mNode);
        Assert.assertTrue(he1Prod.getTarget() == pNode);

        if (resetUuids) {
            he1SubUuid  = he1Sub.getIdentifier();
            he1MedUuid  = he1Med.getIdentifier();
            he1ProdUuid = he1Prod.getIdentifier();
        }

        // homodimer--S-M-S
        hd1      = factory.createHyperEdge(sNode,
                                           EdgeTypeMap.SUBSTRATE,
                                           mNode,
                                           EdgeTypeMap.INHIBITING_MEDIATOR,
                                           net1);
        hd1Sub = hd1.getAnEdge(sNode);
        hd1Med  = hd1.getAnEdge(mNode);
        // now add the same node again:
        hd1Imed = hd1.addEdge(sNode, EdgeTypeMap.INHIBITING_MEDIATOR);
        Assert.assertTrue(hd1Sub.getSource() == sNode);
        Assert.assertTrue(hd1Imed.getSource() == sNode);
        Assert.assertTrue(hd1Med.getSource() == mNode);

        if (resetUuids) {
            hd1SubUuid = hd1Sub.getIdentifier();
            hd1MedUuid  = hd1Med.getIdentifier();
            hd1ImedUuid = hd1Imed.getIdentifier();
        }

        hd1.setName(HD1_LABEL);
        Assert.assertNotNull(hd1Imed);
    }

    /**
     * When we reload and reset the sample objects, reconnect the
     * instance variables to the newly read objects.
     */
    protected void reconnectInstanceVariables() {
        // Find all the appropriate valued to reset the instance variables
        // used:
        he1Sub  = null;
        hd1Sub = null;
        hd1Imed = null;
        he1Med  = null;
        hd1Med  = null;
        he1Prod = null;
        extra    = null;
        sNode        = null;
        mNode        = null;
        pNode        = null;
        aNode        = null;
        he1      = null;
        hd1      = null;

        CyEdge           edge;
        String           eUuid;
        final Iterator<CyEdge> edgeIt = manager.getEdgesByNetwork(null);

        while (edgeIt.hasNext()) {
            edge   = edgeIt.next();
            eUuid = edge.getIdentifier();
            HEUtils.log("reconnect, edge = " + eUuid);

            if (he1SubUuid.equals(eUuid)) {
                he1Sub = edge;
            } else if (hd1SubUuid.equals(eUuid)) {
                hd1Sub = edge;
            } else if (hd1ImedUuid.equals(eUuid)) {
                hd1Imed = edge;
            } else if (he1MedUuid.equals(eUuid)) {
                he1Med = edge;
            } else if (hd1MedUuid.equals(eUuid)) {
                hd1Med = edge;
            } else if (he1ProdUuid.equals(eUuid)) {
                he1Prod = edge;
            } else if ((extraUuid != null) && (extraUuid.equals(eUuid))) {
                extra = edge;
            }
        }

        String    hName;
        HyperEdge he;
        final Iterator<HyperEdge>  heIt = manager.getHyperEdgesByNetwork(null);

        while (heIt.hasNext()) {
            he     = heIt.next();
            hName = he.getName();

            if (HE1_LABEL.equals(hName)) {
		HEUtils.log("reconnect HE with name = he1");
                he1 = he;
            } else if (HD1_LABEL.equals(hName)) {
		HEUtils.log("reconnect HE with name = hd1");
                hd1 = he;
            }
        }

        sNode = Cytoscape.getCyNode(S_LABEL, false);
        mNode = Cytoscape.getCyNode(M_LABEL, false);
        pNode = Cytoscape.getCyNode(P_LABEL, false);
        aNode = Cytoscape.getCyNode(A_LABEL, false);
        Assert.assertNotNull(he1Sub);
        // DEBUG: Uncomment when session reader fixed:
        // Assert.assertNotNull(hd1_sub);
        // DEBUG: Uncomment when session reader fixed:
        // Assert.assertNotNull(hd1_imed);
        Assert.assertNotNull(he1Med);
        Assert.assertNotNull(hd1Med);
        Assert.assertNotNull(he1Prod);
        // Assert.assertNotNull (extra);
        Assert.assertNotNull(sNode);
        Assert.assertNotNull(mNode);
        Assert.assertNotNull(pNode);
        Assert.assertNotNull(aNode);
        Assert.assertNotNull(he1);
        Assert.assertNotNull(hd1);
    }

    /**
     * Return an existing CyNetwork with a given name.
     * @param title the title of the CyNetwork to find.
     * @return the CyNetwork with the given name.
     */
    protected CyNetwork getNetworkWithTitle(final String title) {
        final Set<CyNetwork>       nSet     = Cytoscape.getNetworkSet();
        CyNetwork net;
        String    netTitle;
        final Iterator<CyNetwork>  netIt = nSet.iterator();

        while (netIt.hasNext()) {
            net       = netIt.next();
            netTitle = net.getTitle();

            if (((title == null) && (netTitle == null)) ||
                (title.equals(netTitle))) {
                return net;
            }
        }

        return null;
    }

    /**
     * What to do after each batch of tests.
     * Attempts to clean up the HyperEdgeManager and Cytoscape networks.
     * @param fireEvents true iff we should call HyperEdgeManager.reset(true).
     */
    protected void tearDown1(final boolean fireEvents) {
        manager.reset(fireEvents);
        // remove Cytoscape networks:
        Cytoscape.destroyNetwork(net1);

        // Cytoscape.destroyNetwork (net1, true);
    }

    /**
     * Helper method for testing the values returned by an Iterator.
     * @param it the Iterator to test.
     * @param requiredNum the required number of elements returned by the Iterator.
     * @param <Type> the type of the elements in the Iterator.
     * @return A List of the elements returned by the Iterator.
     */
    protected <Type> List<Type> testIterator(final Iterator<Type> it, final int requiredNum) {
        final List<Type> testList = new ArrayList<Type>();

        while (it.hasNext()) {
            testList.add(it.next());
        }

        if (testList.size() != requiredNum) {
            fail(
                "Expected " + requiredNum +
                " element(s) in iterator, but found " + testList.size() +
                " instead. Items are:" + printNames(testList));
        }

        return testList;
    }

    private String printNames(final List<?> stuff) {
        final StringBuffer sb  = new StringBuffer();
        final Iterator<?>     it  = stuff.iterator();
        Object       obj;

        while (it.hasNext()) {
            obj = it.next();

            if (obj instanceof CyNetwork) {
                sb.append("CyNetwork: ");
                sb.append(((CyNetwork) obj).getIdentifier());
            } else if (obj instanceof CyEdge) {
                final CyEdge edge = (CyEdge) obj;
                sb.append("CyEdge: ");
                sb.append(edge.getSource().getIdentifier());
                sb.append(":");
                sb.append(edge.getTarget().getIdentifier());
            } else if (obj instanceof CyNode) {
                sb.append("CyNode: ");
                sb.append(((CyNode) obj).getIdentifier());
            } else {
                sb.append(obj);
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}
