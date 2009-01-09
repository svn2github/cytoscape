//
// UndoableGraphHider.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;
import y.algo.*;
import java.util.HashMap;


/**
 * A GraphHider that properly informs the UndoManager of its hide and
 * show events.
 *
 * This is necessary because the hiding process will otherwise simply
 * remove the nodes from the graph as if they were deleted.  This
 * causes problems.  Hide, Undo, Show results in an "already present
 * in graph" error.
 *
 * Otherwise, this graph hider behaves exactly the same as
 * y.algo.GraphHider.
 */
public class UndoableGraphHider {

    UndoManager undoManager;

    /**
     * The graph from which things will be hidden.
     */
    Graph graph;
    
    /**
     * A list of nodes hidden from the graph.
     */
    NodeList nodes;

    /**
     * A list of EdgeLists off of the corresponding nodes in the
     * NodeList
     */
    HashMap edgesPerNode;

    /**
     * A list of edges hidden from the graph.
     */
    EdgeList edges;


    /**
     * Default constructor.  Saves Graph and UndoManager pointers.
     */
    public UndoableGraphHider (Graph graph, UndoManager undoManager) {
	this.graph = graph;
	this.undoManager = undoManager;

	nodes = new NodeList();
	edges = new EdgeList();
	edgesPerNode = new HashMap();
    }


    /**
     * Hides the given node from the graph.  Fires a PRE_EVENT with
     * this as the data object to notify any UndoManagers of the
     * change.
     */
    public void hide(Node v) {
	undoManager.saveState(new NodeHiddenUndoItem(this, v));
	undoManager.pause();


	// save node and edges
	nodes.add(v);
	EdgeList list = new EdgeList();
	// to avoid counting loops twice, only count loop as out edge
	for (EdgeCursor ec = v.inEdges(); ec.ok(); ec.next()) {
	    Edge e = ec.edge();
	    if (!e.source().equals(e.target())) list.add(ec.edge());
	}
	for (EdgeCursor ec = v.outEdges(); ec.ok(); ec.next()) 
	    list.add(ec.edge());

	edgesPerNode.put(v, list);

	graph.removeNode(v);

	undoManager.resume();
    }

    
    /**
     * Undo a hide event.
     */
    protected void undoHide(Node v) {
	graph.reInsertNode(v);

	// reinsert edges
	EdgeList list = (EdgeList) edgesPerNode.get(v);
	for (int i = 0; i < list.size(); i++)
	    graph.reInsertEdge((Edge)list.elementAt(i));

	nodes.remove(v);
	edgesPerNode.remove(v);
    }

    /**
     * Redo a hide event.
     */
    protected void redoHide(Node v) {
	// save node and edges
	nodes.add(v);
	EdgeList list = new EdgeList();
	for (EdgeCursor ec = v.inEdges(); ec.ok(); ec.next()) {
	    Edge e = ec.edge();
	    if (!e.source().equals(e.target())) list.add(ec.edge());
	}
	for (EdgeCursor ec = v.outEdges(); ec.ok(); ec.next()) 
	    list.add(ec.edge());

	edgesPerNode.put(v, list);

	graph.removeNode(v);
    }

    

    /**
     * Show a given node
     */
    public void unhide(Node v) {
	undoManager.saveState(new NodeShownUndoItem(this, v));
	undoManager.pause();

	graph.reInsertNode(v);

	// reinsert edges
	EdgeList list = (EdgeList) edgesPerNode.get(v);
	for (int i = 0; i < list.size(); i++)
	    graph.reInsertEdge((Edge)list.elementAt(i));

	nodes.remove(v);
	edgesPerNode.remove(v);

	undoManager.resume();
    }

    /**
     * Undo an unhide event.
     */
    protected void undoUnhide(Node v) {
	// save node and edges
	nodes.add(v);
	EdgeList list = new EdgeList();
	for (EdgeCursor ec = v.inEdges(); ec.ok(); ec.next()) {
	    Edge e = ec.edge();
	    if (!e.source().equals(e.target())) list.add(ec.edge());
	}
	for (EdgeCursor ec = v.outEdges(); ec.ok(); ec.next()) 
	    list.add(ec.edge());

	edgesPerNode.put(v, list);

	graph.removeNode(v);
    }

    /**
     * Redo an unhide event.
     */
    protected void redoUnhide (Node v) {
	graph.reInsertNode(v);

	// reinsert edges
	EdgeList list = (EdgeList) edgesPerNode.get(v);
	for (int i = 0; i < list.size(); i++)
	    graph.reInsertEdge((Edge)list.elementAt(i));

	nodes.remove(v);
	edgesPerNode.remove(v);
    }


    
    /**
     * Hides the given edge from the graph.
     */
    public void hide(Edge e) {
	undoManager.saveState(new EdgeHiddenUndoItem(this, e));
	undoManager.pause();
	graph.removeEdge(e);
	edges.add(e);
	undoManager.resume();
    }

    /** 
     * Hides all edges that are self loops.
     */
    public void hideSelfLoops() {
	for (EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    Edge e = ec.edge();
	    if ( e.source().equals(e.target()) ) hide(e);
	}
    }
    
    /**
     * Undo an edge hide
     */
    protected void undoHide(Edge e) {
	graph.reInsertEdge(e);
	edges.remove(e);
    }

    /**
     * Redo an edge hide
     */
    protected void redoHide(Edge e) {
	graph.removeEdge(e);
	edges.add(e);
    }

    

    /**
     * Unhide a given edge.
     */
    public void unhide(Edge e) {
	undoManager.saveState(new EdgeShownUndoItem(this, e));
	undoManager.pause();
	graph.reInsertEdge(e);
	edges.remove(e);
	undoManager.resume();
    }

    /**
     * Undo an edge unhide.
     */
    protected void undoUnhide (Edge e) {
	graph.removeEdge(e);
	edges.add(e);
    }

    /**
     * Redo an edge uhide
     */
    protected void redoUnhide (Edge e) {
	graph.reInsertEdge(e);
	edges.remove(e);
    }



    /**
     * Unhides all nodes and edges hidden by this UndoableGraphHider.
     */
    public void unhideAll() {
	graph.firePreEvent();
	
	for (int i = nodes.size()-1; i >= 0; i--)
	    unhide((Node)nodes.elementAt(i));
	nodes.clear();
	    
	for (int i = edges.size()-1; i >= 0; i--)
	    unhide((Edge)edges.elementAt(i));
	edges.clear();

	graph.firePostEvent();
    }



    /**
     * Hides the edges contained on the given list
     */
    public void hide(EdgeCursor ec) {
	graph.firePreEvent();

	for (ec.toFirst(); ec.ok(); ec.next()) {
	    hide(ec.edge());
	}
	
	graph.firePostEvent();
    }


    /**
     * Hide all edges in the graph
     */
    public void hideEdges() {
	hide(graph.edges());
    }

    /**
     * Unhide all edges in the graph
     */
    public void unhideEdges() {
	graph.firePreEvent();
	
	for (EdgeCursor ec = edges.edges(); ec.ok(); ec.next())
	    unhide(ec.edge());

	graph.firePostEvent();
    }
}
