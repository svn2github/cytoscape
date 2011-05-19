
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
public final class EdgeFilters {
 
    /**
     * An EdgeFilter that produces all edges--none are filtered out.
     */
    public static final EdgeFilter ALL_EDGES_FILTER = new EdgeFilter() {
        public boolean includeEdge(final HyperEdge he, final CyEdge edge) {
            return true;
        }
    };

    /**
     * An EdgeFilter that filters out all shared edges.
     */
    public static final EdgeFilter UNSHARED_EDGES_FILTER = new EdgeFilter() {
        public boolean includeEdge(final HyperEdge he, final CyEdge edge) {
            return !he.isSharedEdge(edge);
        }
    };

    // Don't want people to manipulate the utility class constructor.
    private EdgeFilters() {}
    
    /**
     * Only include CyEdges found in a given List. For example:
     * <PRE>
     * List&lt;CyEdge&gt; edgesToInclude = new ArrayList&lt;CyEdge&gt;();
     * edgesToInclude.add (e1);
     * edgesToInclude.add (e2);
     * he1.copy (net, new EdgeFilters.EdgeListFilter (edgesToInclude));
     * </PRE>
     * would only copy edges e1 and e2.
     */
    public static class EdgeListFilter implements EdgeFilter {
        private List<CyEdge> edges;

	/**
	 * Create an EdgeListFilter.
	 * @param edgesToInclude a List of CyEdges. An Edge will only be included when
	 *        it belongs to this List.
	 */
        public EdgeListFilter(final List<CyEdge> edgesToInclude) {
            edges = edgesToInclude;
        }

        /**
         * {@inheritDoc}
         */
        public boolean includeEdge(final HyperEdge he, final CyEdge edge) {
            return (edges.contains(edge));
        }
    }

    /**
     * Only include CyEdges that connect to a given List of CyNodes. For example:
     * <PRE>
     * List&lt;CyNode&gt; nodesToInclude = new ArrayList&lt;CyNode&gt;();
     * nodesToInclude.add (node1);
     * nodesToInclude.add (node2);
     * he1.copy (net, new EdgeFilters.NodeListFilter (nodesToInclude));
     * </PRE>
     * would only copy edges that connect to node1 and node2.
     */
    public static class NodeListFilter implements EdgeFilter {
        private List<CyNode> nodes;

	/**
	 * Create a NodeListFilter.
	 * @param nodesToInclude a List of CyNodes. An Edge will only be included when
	 *        it connects to one of the CyNodes in the List.
	 */

        public NodeListFilter(final List<CyNode> nodesToInclude) {
            nodes = nodesToInclude;
        }
        /**
         * {@inheritDoc}
         */
        public boolean includeEdge(final HyperEdge he, final CyEdge edge) {
            return (nodes.contains(he.getNode(edge)));
        }
    }
}
