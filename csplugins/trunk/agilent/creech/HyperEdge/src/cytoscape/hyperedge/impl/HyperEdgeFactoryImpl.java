/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeFactoryImpl.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/HyperEdgeFactoryImpl.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Sep 14 09:06:05 2005
* Modified:     Tue Nov 07 09:09:30 2006 (Michael L. Creech) creech@w235krbza760
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
public class HyperEdgeFactoryImpl implements HyperEdgeFactory {
    private static HyperEdgeFactoryImpl _singleton_factory = new HyperEdgeFactoryImpl();

    private HyperEdgeFactoryImpl() {
    }

    public static HyperEdgeFactoryImpl createInstance() {
        return _singleton_factory;
    }

    public HyperEdgeManager getHyperEdgeManager() {
        return HyperEdgeManagerImpl.getHyperEdgeManager();
    }

    public EdgeTypeMap getEdgeTypeMap() {
        return EdgeTypeMapImpl.getEdgeTypeMap();
    }

    public HyperEdge createHyperEdge(CyNode node1, String edgeIType1, CyNode node2,
                                     String edgeIType2, CyNetwork net) {
        HyperEdge he = new HyperEdgeImpl(node1,
                                         edgeIType1,
                                         node2,
                                         edgeIType2,
                                         net,
                                         true);

        return he;
    }

    public HyperEdge createHyperEdge(CyNode node1, String edgeIType1, CyNode node2,
                                     String edgeIType2, CyNode node3,
                                     String edgeIType3, CyNetwork net) {
        HyperEdge he = new HyperEdgeImpl(node1,
                                         edgeIType1,
                                         node2,
                                         edgeIType2,
                                         node3,
                                         edgeIType3,
                                         net,
                                         true);

        return he;
    }

    public HyperEdge createHyperEdge(CyNode[] nodes, String[] edgeITypes,
                                     CyNetwork net) {
        HyperEdge he = new HyperEdgeImpl(nodes, edgeITypes, net, true);

        return he;
    }

    public HyperEdge createHyperEdge(List<CyNode> nodes,
                                     List<String> edgeITypes,
                                     CyNetwork net) {
        HyperEdge he = new HyperEdgeImpl(nodes, edgeITypes, net, true);

        return he;
    }

    public HyperEdge createHyperEdge(Map<CyNode, String> node_edgeIType_map,
                                     CyNetwork net) {
        HyperEdge he = new HyperEdgeImpl(node_edgeIType_map, net, true);

        return he;
    }

    /**
     * Used by the restore process for creating objects.
     * Not a user-level operation.
     */
    public HyperEdge createRestoredHyperEdge(CyNode connectorNode,
                                             CyNetwork net) {
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
