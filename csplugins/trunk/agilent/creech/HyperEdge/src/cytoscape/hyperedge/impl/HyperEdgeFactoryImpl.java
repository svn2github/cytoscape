
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
* Wed Oct 08 05:07:14 2008 (Michael L. Creech) creech@w235krbza760
*  Changed createInstance()->getInstance() and corrected javadocs.
* Tue Nov 07 09:09:22 2006 (Michael L. Creech) creech@w235krbza760
*  Changed Edge-->CyEdge.
* Mon Nov 06 09:09:15 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode.
* Mon Sep 11 18:11:10 2006 (Michael L. Creech) creech@w235krbza760
*  Changed createHyperEdge(Collection...) to createHyperEdge(List...).
* Thu Aug 17 15:23:04 2006 (Michael L. Creech) creech@w235krbza760
*  Added GraphPerspective to all createHyperEdge() operations.
*  Moved addNewObjectListener() and removeNewObjectListener() to HyperEdgeManager.
* Sun Aug 13 11:49:59 2006 (Michael L. Creech) creech@w235krbza760
*  Changed params to createRestoredHyperEdge().
* Fri Aug 11 18:51:12 2006 (Michael L. Creech) creech@w235krbza760
*  Removed createRestoredEdgeTypeMap().
* Fri Sep 30 10:51:29 2005 (Michael L. Creech) creech@Dill
*  Added createRestoredEdgeTypeMap().
* Thu Sep 29 13:10:36 2005 (Michael L. Creech) creech@Dill
*  Added getEdgeTypeMap ().
*********************************************************************************/
package cytoscape.hyperedge.impl;


import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.HyperEdgeManager;

import java.util.List;
import java.util.Map;


/**
 * Singleton object implementation of the HyperEdgeFactory interface.
 * @author Michael L. Creech
 * @version 2.0
 */
public final class HyperEdgeFactoryImpl implements HyperEdgeFactory {
    private static HyperEdgeFactoryImpl singletonFactory = new HyperEdgeFactoryImpl();

    private HyperEdgeFactoryImpl() {
    }
    /**
     * Obtain the singleton instance of the factory.
     */
    public static HyperEdgeFactoryImpl getInstance() {
        return singletonFactory;
    }
    /**
     * {@inheritDoc}
     */
    public HyperEdgeManager getHyperEdgeManager() {
        return HyperEdgeManagerImpl.getHyperEdgeManager();
    }
    /**
     * {@inheritDoc}
     */
    public EdgeTypeMap getEdgeTypeMap() {
        return EdgeTypeMapImpl.getEdgeTypeMap();
    }
    /**
     * {@inheritDoc}
     */
    public HyperEdge createHyperEdge(final CyNode node1, final String edgeIType1, final CyNode node2,
                                     final String edgeIType2, final CyNetwork net) {
        final HyperEdge he = new HyperEdgeImpl(node1,
                                         edgeIType1,
                                         node2,
                                         edgeIType2,
                                         net,
                                         true);

        return he;
    }
    /**
     * {@inheritDoc}
     */
    public HyperEdge createHyperEdge(final CyNode node1, final String edgeIType1, final CyNode node2,
                                     final String edgeIType2, final CyNode node3,
                                     final String edgeIType3, final CyNetwork net) {
        final HyperEdge he = new HyperEdgeImpl(node1,
                                         edgeIType1,
                                         node2,
                                         edgeIType2,
                                         node3,
                                         edgeIType3,
                                         net,
                                         true);

        return he;
    }
    /**
     * {@inheritDoc}
     */
    public HyperEdge createHyperEdge(final CyNode[] nodes, final String[] edgeITypes,
                                     final CyNetwork net) {
        final HyperEdge he = new HyperEdgeImpl(nodes, edgeITypes, net, true);

        return he;
    }
    /**
     * {@inheritDoc}
     */
    public HyperEdge createHyperEdge(final List<CyNode> nodes,
                                     final List<String> edgeITypes,
                                     final CyNetwork net) {
        final HyperEdge he = new HyperEdgeImpl(nodes, edgeITypes, net, true);

        return he;
    }
    /**
     * {@inheritDoc}
     */
    public HyperEdge createHyperEdge(final Map<CyNode, String> nodeEdgeITypeMap,
                                     final CyNetwork net) {
        final HyperEdge he = new HyperEdgeImpl(nodeEdgeITypeMap, net, true);

        return he;
    }

    /**
     * Used by the restore process for creating objects.
     * Not a user-level operation.
     * @param connectorNode the ConnectorNode of the HyperEdge to restore.
     * @param net the CyNetwork containing the HyperEdge.
     * @return a restored HyperEdge
     */
    public HyperEdge createRestoredHyperEdge(final CyNode connectorNode,
                                             final CyNetwork net) {
        return new HyperEdgeImpl(connectorNode, net);
    }

    //    /**
    //     * Used by the restore process for creating objects.
    //     * Not a user-level operation.
    //     */
    //    public EdgeTypeMap createRestoredEdgeTypeMap (String uuid)
    //    {
    //        return new EdgeTypeMapImpl(uuid);
    //    }
}
