package cytoscape.hyperedge;

import cytoscape.CyEdge;


/**
 * Interface for defining CyEdge-based filters used for determining
 * which Edges should be used for a particular operation, such as
 * HyperEdge.copy().
 * @author Michael L. Creech
 */
public interface EdgeFilter {
    /**
     * Return true iff the Edge edge in he should be included in some
     * operation.
     * For example, if we were performing a HyperEdge.copy(),
     * returning true would mean to copy this edge.
     * @see cytoscape.hyperedge.HyperEdge#copy
     */
    boolean includeEdge(HyperEdge he, CyEdge edge);
}
