/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph;

import infovis.Graph;
import infovis.Tree;
import infovis.graph.event.GraphChangedEvent;
import infovis.table.DefaultDynamicTable;
import infovis.tree.Algorithms;
import infovis.tree.DefaultTree;
import infovis.tree.DepthFirst;
import infovis.utils.RowIterator;

/**
 * Graph with vertices being a tree
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class HierarchicalGraph extends DefaultGraph {
    protected DefaultTree tree;
    public HierarchicalGraph() {
        super(new DefaultDynamicTable(), new DefaultTree());
        this.tree = (DefaultTree)getVertexTable();
        vertexFirstEdge.setExtend(Tree.ROOT, Graph.NIL);
        vertexLastEdge.setExtend(Tree.ROOT, Graph.NIL);
        vertexFirstInEdge.setExtend(Tree.ROOT, Graph.NIL);
        vertexLastInEdge.setExtend(Tree.ROOT, Graph.NIL);
    }
    
    public void clear() {
        super.clear();
        vertexFirstEdge.setExtend(Tree.ROOT, Graph.NIL);
        vertexLastEdge.setExtend(Tree.ROOT, Graph.NIL);
        vertexFirstInEdge.setExtend(Tree.ROOT, Graph.NIL);
        vertexLastInEdge.setExtend(Tree.ROOT, Graph.NIL);
    }
    
    public int addVertex() {
        return addVertex(Tree.ROOT);
    }
    
    public int addVertex(int parent) {
        int v = tree.addNode(parent);
        try {
            disableNotify();
            vertexFirstEdge.setExtend(v, Graph.NIL);
            vertexLastEdge.setExtend(v, Graph.NIL);
            vertexFirstInEdge.setExtend(v, Graph.NIL);
            vertexLastInEdge.setExtend(v, Graph.NIL);
        }
        finally {
            enableNotify();
        }
        fireGraphChangedListeners(v, GraphChangedEvent.GRAPH_VERTEX_ADDED);
        return v;        
    }
    
    public void removeVertex(int vertex) {
        if (vertex == Tree.ROOT) {
            clear();
            return;
        }
        tree.visit(new DepthFirst.Visitor() {
            public boolean preorder(int node) {
                return true;
            }
            public void postorder(int node) {
                HierarchicalGraph.super.removeVertex(node);
            }
        }, vertex);
    }
    
    public Tree getTree() {
        return tree;
    }
    
    public int getVerticesCount() {
    	return Algorithms.leafCount(tree, Tree.ROOT);
    }

    public RowIterator vertexIterator() {
    	return tree.leafIterator();
    }
}
