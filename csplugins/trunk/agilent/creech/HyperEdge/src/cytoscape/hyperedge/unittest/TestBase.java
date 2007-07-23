/* -*-Java-*-
********************************************************************************
*
* File:         TestBase.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/TestBase.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Tue Sep 20 09:24:27 2005
* Modified:     Mon Nov 13 15:59:20 2006 (Michael L. Creech) creech@w235krbza760
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
    protected static final String EXTRA         = "extra";
    protected HyperEdgeFactory    factory       = HyperEdgeFactory.INSTANCE;
    protected HyperEdgeManager    manager       = factory.getHyperEdgeManager();
    protected CyEdge              he1_sub;
    protected CyEdge              he1_med;
    protected CyEdge              he1_prod;
    protected CyEdge              hd1_sub;
    protected CyEdge              hd1_imed;
    protected CyEdge              hd1_med;
    protected CyEdge              extra;
    protected String              he1_sub_uuid;
    protected String              he1_med_uuid;
    protected String              he1_prod_uuid;
    protected String              hd1_sub_uuid;
    protected String              hd1_imed_uuid;
    protected String              hd1_med_uuid;
    protected String              extra_uuid;
    protected CyNode              S;
    protected CyNode              M;
    protected CyNode              P;
    protected CyNode              A;
    protected CyNetwork           net1;
    protected HyperEdge           he1;

    // homodimer--S-M-S
    protected HyperEdge hd1;

    /**
     * @param reset_uuids true when we should reset the saved
     *                    uuids (e.g., he1_sub_uuid) used for matching
     *                    when the reconnectInstanceVariables () is
     *                    called.
     */
    protected void setUp1(boolean reset_uuids) {
        S    = Cytoscape.getCyNode("S", true);
        M    = Cytoscape.getCyNode("M", true);
        P    = Cytoscape.getCyNode("P", true);
        A    = Cytoscape.getCyNode("A", true);
        net1 = Cytoscape.createNetwork("net1");
        // he1 = S(su)M(ame)P(pr) (for net1)
        he1  = factory.createHyperEdge(S,
                                       EdgeTypeMap.SUBSTRATE,
                                       M,
                                       EdgeTypeMap.ACTIVATING_MEDIATOR,
                                       P,
                                       EdgeTypeMap.PRODUCT,
                                       net1);
        // MLC 08/19/06:
        // he1.addToCyNetwork(net2);

        // he1.addToCyNetwork(net3);
        he1.setName("he1");
        he1_sub  = he1.getAnEdge(S);
        he1_med  = he1.getAnEdge(M);
        he1_prod = he1.getAnEdge(P);
        Assert.assertTrue(he1_sub.getSource() == S);
        Assert.assertTrue(he1_med.getSource() == M);
        Assert.assertTrue(he1_prod.getTarget() == P);

        if (reset_uuids) {
            he1_sub_uuid  = he1_sub.getIdentifier();
            he1_med_uuid  = he1_med.getIdentifier();
            he1_prod_uuid = he1_prod.getIdentifier();
        }

        // homodimer--S-M-S
        hd1      = factory.createHyperEdge(S,
                                           EdgeTypeMap.SUBSTRATE,
                                           M,
                                           EdgeTypeMap.INHIBITING_MEDIATOR,
                                           net1);
        hd1_sub = hd1.getAnEdge(S);
        hd1_med  = hd1.getAnEdge(M);
        // now add the same node again:
        hd1_imed = hd1.addEdge(S, EdgeTypeMap.INHIBITING_MEDIATOR);
        Assert.assertTrue(hd1_sub.getSource() == S);
        Assert.assertTrue(hd1_imed.getSource() == S);
        Assert.assertTrue(hd1_med.getSource() == M);

        if (reset_uuids) {
            hd1_sub_uuid = hd1_sub.getIdentifier();
            hd1_med_uuid  = hd1_med.getIdentifier();
            hd1_imed_uuid = hd1_imed.getIdentifier();
        }

        hd1.setName("hd1");
        Assert.assertNotNull(hd1_imed);
    }

    // When we reload and reset the sample objects, reconnect the
    // instance variables to the newly read objects.
    protected void reconnectInstanceVariables() {
        // Find all the appropriate valued to reset the instance variables
        // used:
        he1_sub  = null;
        hd1_sub = null;
        hd1_imed = null;
        he1_med  = null;
        hd1_med  = null;
        he1_prod = null;
        extra    = null;
        S        = null;
        M        = null;
        P        = null;
        A        = null;
        he1      = null;
        hd1      = null;

        CyEdge           edge;
        String           e_uuid;
        Iterator<CyEdge> edge_it = manager.getEdgesByNetwork(null);

        while (edge_it.hasNext()) {
            edge   = edge_it.next();
            e_uuid = edge.getIdentifier();
            HEUtils.log("reconnect, edge = " + e_uuid);

            if (he1_sub_uuid.equals(e_uuid)) {
                he1_sub = edge;
            } else if (hd1_sub_uuid.equals(e_uuid)) {
                hd1_sub = edge;
            } else if (hd1_imed_uuid.equals(e_uuid)) {
                hd1_imed = edge;
            } else if (he1_med_uuid.equals(e_uuid)) {
                he1_med = edge;
            } else if (hd1_med_uuid.equals(e_uuid)) {
                hd1_med = edge;
            } else if (he1_prod_uuid.equals(e_uuid)) {
                he1_prod = edge;
            } else if ((extra_uuid != null) && (extra_uuid.equals(e_uuid))) {
                extra = edge;
            }
        }

        String    h_name;
        HyperEdge he;
        Iterator<HyperEdge>  he_it = manager.getHyperEdgesByNetwork(null);

        while (he_it.hasNext()) {
            he     = he_it.next();
            h_name = he.getName();

            if ("he1".equals(h_name)) {
		HEUtils.log("reconnect HE with name = he1");
                he1 = he;
            } else if ("hd1".equals(h_name)) {
		HEUtils.log("reconnect HE with name = hd1");
                hd1 = he;
            }
        }

        S = Cytoscape.getCyNode("S", false);
        M = Cytoscape.getCyNode("M", false);
        P = Cytoscape.getCyNode("P", false);
        A = Cytoscape.getCyNode("A", false);
        Assert.assertNotNull(he1_sub);
        // DEBUG: Uncomment when session reader fixed:
        // Assert.assertNotNull(hd1_sub);
        // DEBUG: Uncomment when session reader fixed:
        // Assert.assertNotNull(hd1_imed);
        Assert.assertNotNull(he1_med);
        Assert.assertNotNull(hd1_med);
        Assert.assertNotNull(he1_prod);
        // Assert.assertNotNull (extra);
        Assert.assertNotNull(S);
        Assert.assertNotNull(M);
        Assert.assertNotNull(P);
        Assert.assertNotNull(A);
        Assert.assertNotNull(he1);
        Assert.assertNotNull(hd1);
    }

    protected CyNetwork getNetworkWithTitle(String title) {
        Set       n_set     = Cytoscape.getNetworkSet();
        CyNetwork net;
        String    net_title;
        Iterator  net_it = n_set.iterator();

        while (net_it.hasNext()) {
            net       = (CyNetwork) net_it.next();
            net_title = net.getTitle();

            if (((title == null) && (net_title == null)) ||
                (title.equals(net_title))) {
                return net;
            }
        }

        return null;
    }

    protected void tearDown1(boolean fire_events) {
        manager.reset(fire_events);
        // remove Cytoscape networks:
        Cytoscape.destroyNetwork(net1);

        // Cytoscape.destroyNetwork (net1, true);
    }

    protected List testIterator(Iterator it, int required_num) {
        List<Object> test_list = new ArrayList<Object>();

        while (it.hasNext()) {
            test_list.add(it.next());
        }

        if (test_list.size() != required_num) {
            fail(
                "Expected " + required_num +
                " element(s) in iterator, but found " + test_list.size() +
                " instead. Items are:" + printNames(test_list));
        }

        return test_list;
    }

    private String printNames(List stuff) {
        StringBuffer sb  = new StringBuffer();
        Iterator     it  = stuff.iterator();
        Object       obj;

        while (it.hasNext()) {
            obj = it.next();

            if (obj instanceof CyNetwork) {
                sb.append("CyNetwork: ");
                sb.append(((CyNetwork) obj).getIdentifier());
            } else if (obj instanceof CyEdge) {
                CyEdge edge = (CyEdge) obj;
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
