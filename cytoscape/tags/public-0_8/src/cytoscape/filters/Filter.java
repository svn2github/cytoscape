package cytoscape.filters;

import y.base.*;
import y.view.*;

import java.util.Hashtable;

import y.algo.GraphHider;

import cytoscape.data.*;
/**
 * Abstract base class for all filters
 * <p>
 * Filters provide mechanisms:
 * <ul>
 * <li> to flag nodes of a graph,</li>
 * <li> to hide nodes according to the flag.</li>
 * </ul>
 * <p>
 * Implementation issues:
 * <ul>
 * <li> Mixed  uses of <code>Filter</code> 
 * and <code>NodeList</code> as parameters
 * to filters. Not a problem but a convenience
 * right now.</li>
 * </ul>
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public abstract class Filter {
    /**
     * Limit flaggable nodes to those flagged in this filter.
     */
    private Filter flaggableF;

    protected Graph2D graph;

    public Filter(Graph2D graph) {
	flaggableF = null;
	this.graph = graph;
    }

    public Filter(Graph2D graph, Filter flaggableF) {
	this.flaggableF = flaggableF;
	this.graph = graph;
    }

    /**
     * Informs whether a filter for flaggable nodes 
     * is provided. 
     * If not, it should be assumed that all nodes are flaggable.
     *
     * @return <code>true</code> if the filter is NOT provided, 
     *         <code>false</code> otherwise.
     */
    protected boolean allFlaggable() {
	return flaggableF == null;
    }

    /**
     * Gets the filter for flaggable nodes.
     * <p>
     * Should only be called if {@link #allFlaggable()} returns 
     * <code>false</code>. 
     *
     * @return The filter for flaggable nodes
     */
    protected Filter getFlaggableF() {
	if (!allFlaggable()) {
	    return flaggableF;
	} else {
	    // Shouldn't be used.
	    return new TrueFilter(graph);
	}
    }

    /**
     * Creates a list of flagged nodes, assuming a list of hidden nodes.
     * <p>
     * This is the only procedure that should be specific to each filter.
     *
     * @param hidden A list of nodes assumed already hidden
     * @return the list of flagged nodes
     */
    protected abstract NodeList get(NodeList hidden);

    /**
     * Shortcut to {@link #get(NodeList hidden)} which assumes 
     * no nodes are hidden.
     */
    public NodeList get() {
	return get(new NodeList());
    }

    /**
     * Hides nodes flagged by {@link #get(NodeList hidden)} which it calls,
     * assuming a list of hidden nodes.
     *
     * @param graphHider The graphHider to use to hide the nodes
     * @param hidden A list of nodes assumed already hidden
     * @return The number of nodes hidden
     */
    protected int hide(GraphHider graphHider, NodeList hidden) {
	NodeList flagged = get(hidden);
	
	int counter = 0;
	for (NodeCursor nc = flagged.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    graphHider.hide(node);
	    counter++;
	}

	System.out.println(counter + " nodes hidden.");
	return counter;
    }

    /**
     * Select nodes NOT flagged by {@link #get(NodeList hidden)} which it calls,
     * assuming a list of hidden nodes.
     *
     * @param hidden A list of nodes assumed already hidden
     * @return The number of nodes selected
     */
    protected int select(NodeList hidden) {
	Filter negF = new NegFilter(graph, this);
	NodeList unflagged = negF.get(hidden);
	
	int counter = 0;
	for (NodeCursor nc = unflagged.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    if (!graph.isSelected(node)) {
		graph.setSelected(node, true);
		counter++;
	    }
	}

	System.out.println(counter + " nodes selected.");
	return counter;
    }

    /**
     * Shortcut to {@link #hide(GraphHider graphHider, NodeList hidden)}
     * which assumes no nodes are hidden.
     */
    public int hide(GraphHider graphHider) {
	return hide(graphHider, new NodeList());
    }

    /**
     * Shortcut to {@link #select(NodeList hidden)}
     * which assumes no nodes are hidden.
     */
    public int select() {
	return select(new NodeList());
    }

    /**
     * Performs a Breadth First Search from given node, 
     * terminating at given depth.
     *
     * @param startNode The node for which to find related nodes
     * @param depthMax The maximum number of links between the starting node 
     *                 and the node to be considered
     *
     * @return A list of all nodes within the given depth of the 
     *         starting node (including it!)
     */
    public static NodeList bfsDepth(Node startNode,
				    int maxDepth) {

	Hashtable depthTable = new Hashtable();
	NodeList lst = new NodeList();

	lst.add(startNode);
	depthTable.put(startNode, new Integer(0));

	for (int i = 0; i < lst.size(); i++) {
	    Node v = (Node)lst.elementAt(i);
	    for (EdgeCursor ec = v.edges(); ec.ok(); ec.next()) {
		Edge e = ec.edge();
		Node w = e.opposite(v);

		Integer depthV = (Integer)depthTable.get(v);
		Integer depthW = (Integer)depthTable.get(w);		

		if (depthW == null && depthV.intValue() < maxDepth) {
		    lst.add(w);
		    depthTable.put(w, new Integer(depthV.intValue() + 1));
		}
	    }
	}

	return lst;
    }

}
