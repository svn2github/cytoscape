/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import infovis.*;
import infovis.graph.*;
import infovis.graph.event.GraphChangedEvent;
import infovis.graph.event.GraphChangedListener;
import infovis.utils.*;

import java.util.EventListener;

import javax.swing.event.*;

/**
 * Class TreeAsGraph
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class TreeAsGraph extends TreeProxy implements Graph,
        TreeModelListener {
    protected EventListenerList listeners;

    public TreeAsGraph(Tree tree) {
        super(tree);
        tree.addTreeModelListener(this);
    }

    public void dispose() {
        tree.removeTreeModelListener(this);
    }

    public boolean isDirected() {
        return true;
    }

    public void setDirected(boolean directed) {
        if (!directed) {
            throw new GraphException(
                    "cannot change a tree into an undirected graph",
                    Graph.NIL, Graph.NIL);
        }
    }

    public int getVerticesCount() {
        return tree.getNodeCount();
    }

    public int addVertex() {
        return tree.addNode(Tree.ROOT);
    }

    public void removeVertex(int vertex) {
        tree.removeNode(vertex);
    }

    public int getEdgesCount() {
        return tree.getNodeCount() - 1;
    }

    public int addEdge(int v1, int v2) {
        tree.reparent(v2, v1);
        return v2;
    }

    public void removeEdge(int edge) {
        tree.reparent(edge, Tree.ROOT);
    }

    public int getFirstVertex(int edge) {
        return tree.getParent(edge);
    }

    public int getSecondVertex(int edge) {
        return edge;
    }
    
    public int getOtherVertex(int edge, int vertex) {
        int in = getFirstVertex(edge);
        int out = getSecondVertex(edge);
        if (in==vertex) {
            return out;
        }
        if (out == vertex) {
            return in;
        }
        return Graph.NIL;
    }
    
    public int getOutEdgeAt(int vertex, int index) {
        return tree.getChild(vertex, index);
    }

    public int getInEdgeAt(int vertex, int index) {
        if (index == 0)
            return tree.getParent(vertex);
        return Graph.NIL;
    }

    public int getEdge(int v1, int v2) {
        return v2;
    }

    public int findEdge(int v1, int v2) {
        return v2;
    }

    public int getOutDegree(int vertex) {
        return tree.getChildCount(vertex);
    }
    
    public int getDegree(int vertex) {
        return getInDegree(vertex) + getOutDegree(vertex);
    }

    public RowIterator outEdgeIterator(int vertex) {
        return tree.childrenIterator(vertex);
    }
    
    public RowIterator edgeIterator(int vertex) {
        return new AppendRowIterator(
                outEdgeIterator(vertex),
                inEdgeIterator(vertex));
    }

    public int getInDegree(int vertex) {
        if (vertex == Tree.ROOT)
            return 0;
        return 1;
    }

    class ParentIterator implements RowIterator {
        int parent;

        public ParentIterator(int parent) {
            this.parent = parent;
        }

        public int nextRow() {
            int ret = parent;
            parent = Graph.NIL;
            return ret;
        }

        public int peekRow() {
            return parent;
        }

        public RowIterator copy() {
            return new ParentIterator(parent);
        }

        public boolean hasNext() {
            return parent != Graph.NIL;
        }

        public Object next() {
            return new Integer(nextRow());
        }

        public void remove() {
            if (parent != Graph.NIL)
                tree.removeNode(parent);
        }
    };

    public RowIterator inEdgeIterator(int vertex) {
        return new ParentIterator(tree.getParent(vertex));
    }

    public RowIterator vertexIterator() {
        return tree.iterator();
    }

    public RowIterator edgeIterator() {
        RowIterator iter = tree.iterator();
        iter.nextRow(); // skip the root
        return iter;
    }

    public DynamicTable getEdgeTable() {
        return tree;
    }

    public DynamicTable getVertexTable() {
        return tree;
    }

    public void addGraphChangedListener(GraphChangedListener l) {
        getListeners().add(GraphChangedListener.class, l);
    }

    public void removeGraphChangedListener(GraphChangedListener l) {
        if (listeners == null)
            return;
        listeners.remove(GraphChangedListener.class, l);
    }

    public boolean shouldFire() {
        return listeners != null
                && listeners
                        .getListenerCount(GraphChangedListener.class) != 0;
    }

    public void treeNodesChanged(TreeModelEvent e) {
        if (!shouldFire())
            return;
    }

    public void treeNodesInserted(TreeModelEvent ev) {
        if (!shouldFire())
            return;
        int parent = RowObject.getRow(tree, ev.getTreePath()
                .getLastPathComponent());
        int[] indices = ev.getChildIndices();
        int i;
        for (i = 0; i < indices.length; i++) {
            int node = tree.getChild(parent, indices[i]);
            GraphChangedEvent e = new GraphChangedEvent(this, node,
                    GraphChangedEvent.GRAPH_VERTEX_ADDED);
            EventListener[] ll = listeners
                    .getListeners(GraphChangedListener.class);
            for (i = 0; i < ll.length; i++) {
                GraphChangedListener l = (GraphChangedListener) ll[i];
                l.graphChanged(e);
            }
            if (node == Tree.ROOT)
                continue;
            e = new GraphChangedEvent(this, node,
                    GraphChangedEvent.GRAPH_EDGE_ADDED);
            for (i = 0; i < ll.length; i++) {
                GraphChangedListener l = (GraphChangedListener) ll[i];
                l.graphChanged(e);
            }
        }
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        if (!shouldFire())
            return;
        //TODO
    }
    
    public void treeStructureChanged(TreeModelEvent e) {
        // TODO Auto-generated method stub

    }

    /*
    public void treeChanged(TreeChangedEvent ev) {
        if (listeners == null)
            return;
        Object[] ll = listeners
                .getListeners(GraphChangedListener.class);
        if (ll.length == 0)
            return;
        GraphChangedEvent e;
        switch (ev.getType()) {
        case TreeChangedEvent.TREE_NODE_ADDED:
            e = new GraphChangedEvent(this, ev.getNode(),
                    GraphChangedEvent.GRAPH_VERTEX_ADDED);
            for (int i = 0; i < ll.length; i++) {
                GraphChangedListener l = (GraphChangedListener) ll[i];
                l.graphChanged(e);
            }
            if (ev.getNode() == Tree.ROOT)
                break;
            e = new GraphChangedEvent(this, ev.getNode(),
                    GraphChangedEvent.GRAPH_EDGE_ADDED);
            for (int i = 0; i < ll.length; i++) {
                GraphChangedListener l = (GraphChangedListener) ll[i];
                l.graphChanged(e);
            }
            break;
        case TreeChangedEvent.TREE_NODE_REMOVED:
            if (ev.getNode() != Tree.ROOT) {
                e = new GraphChangedEvent(this, ev.getNode(),
                        GraphChangedEvent.GRAPH_EDGE_REMOVED);
                for (int i = 0; i < ll.length; i++) {
                    GraphChangedListener l = (GraphChangedListener) ll[i];
                    l.graphChanged(e);
                }
            }
            e = new GraphChangedEvent(this, ev.getNode(),
                    GraphChangedEvent.GRAPH_VERTEX_REMOVED);
            for (int i = 0; i < ll.length; i++) {
                GraphChangedListener l = (GraphChangedListener) ll[i];
                l.graphChanged(e);
            }
            break;
        case TreeChangedEvent.TREE_NODE_MOVED:
            e = new GraphChangedEvent(this, ev.getNode(),
                    GraphChangedEvent.GRAPH_EDGE_REMOVED);
            for (int i = 0; i < ll.length; i++) {
                GraphChangedListener l = (GraphChangedListener) ll[i];
                l.graphChanged(e);
            }
            e = new GraphChangedEvent(this, ev.getNode(),
                    GraphChangedEvent.GRAPH_EDGE_ADDED);
            for (int i = 0; i < ll.length; i++) {
                GraphChangedListener l = (GraphChangedListener) ll[i];
                l.graphChanged(e);
            }
            break;
        }
    }
    */

    protected EventListenerList getListeners() {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        return listeners;
    }
}