/* -*-Java-*-
********************************************************************************
*
* File:         EdgeFilters.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/utils/EdgeFilters.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Tue Nov 14 14:05:18 2006
* Modified:     Wed Nov 15 15:13:14 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.impl.utils;


import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.hyperedge.EdgeFilter;
import cytoscape.hyperedge.HyperEdge;

import java.util.List;


/**
 * Common EdgeFilters to use with operations like HyperEdge.copy().
 * @author Michael L. Creech
 */
public class EdgeFilters {
    /**
     * An EdgeFilter that produces all edges--none are filtered out.
     */
    public static final EdgeFilter ALL_EDGES_FILTER = new EdgeFilter() {
        public boolean includeEdge(HyperEdge he, CyEdge edge) {
            return true;
        }
    };

    /**
     * An EdgeFilter that filters out all shared edges.
     */
    public static final EdgeFilter UNSHARED_EDGES_FILTER = new EdgeFilter() {
        public boolean includeEdge(HyperEdge he, CyEdge edge) {
            return !he.isSharedEdge(edge);
        }
    };

    /**
     * Only include CyEdges found in a given List. For example:
     * <PRE>
     * List<CyEdge> edgesToInclude = new ArrayList<CyEdge>();
     * edgesToInclude.add (e1);
     * edgesToInclude.add (e2);
     * he1.copy (net, new EdgeFilters.EdgeListFilter (edgesToInclude));
     * </PRE>
     * would only copy edges e1 and e2.
     */
    public static class EdgeListFilter implements EdgeFilter {
        private List<CyEdge> _edges;

        public EdgeListFilter(List<CyEdge> edges) {
            _edges = edges;
        }

        public boolean includeEdge(HyperEdge he, CyEdge edge) {
            return (_edges.contains(edge));
        }
    }

    /**
     * Only include CyEdges that connect to a given List of CyNodes. For example:
     * <PRE>
     * List<CyNode> nodesToInclude = new ArrayList<CyNode>();
     * nodesToInclude.add (node1);
     * nodesToInclude.add (node2);
     * he1.copy (net, new EdgeFilters.NodeListFilter (nodesToInclude));
     * </PRE>
     * would only copy edges that connect to node1 and node2.
     */
    public static class NodeListFilter implements EdgeFilter {
        private List<CyNode> _nodes;

        public NodeListFilter(List<CyNode> nodes) {
            _nodes = nodes;
        }

        public boolean includeEdge(HyperEdge he, CyEdge edge) {
            return (_nodes.contains(he.getNode(edge)));
        }
    }
}
