/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import infovis.Tree;
import infovis.utils.RowIterator;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class Algorithms {
    static class TreeDepth implements DepthFirst.Visitor {
        int depth;
        int maxDepth;
    
        public TreeDepth() {
           depth = 0;
           maxDepth = 0;
        }
        public boolean preorder(int node) {
            depth++;
            if (depth > maxDepth)
                maxDepth = depth;
            return true;
        }
        public void postorder(int node) {
            depth--;
        }
    }

    public static int treeDepth(Tree tree, int root) {
        TreeDepth visitor = new TreeDepth();
        DepthFirst.visit(tree, visitor, root);
        return visitor.maxDepth;
    }
    
    public static int treeDepth(Tree tree) {
        return treeDepth(tree, Tree.ROOT);
    }

    public static int leafCount(Tree tree, int node) {
        if (tree.isLeaf(node)) {
            return 1;
        }
        int sum = 0;
        for (RowIterator iter = tree.childrenIterator(node); iter.hasNext(); ) {
            sum += leafCount(tree, iter.nextRow());
        }
        return sum;
    }
    
    public static int leafCount(Tree tree, int node, int[] sumDegrees) {
        if (tree.isLeaf(node)) {
            return sumDegrees[node] = 1;
        }
        int sum = 0;
        for (RowIterator iter = tree.childrenIterator(node); iter.hasNext(); ) {
            sum += leafCount(tree, iter.nextRow(), sumDegrees);
        }
        return sumDegrees[node]= sum;
    }
    
    public static Tree insert(Tree fromTree, final int fromRoot, final Tree toTree, final int toRoot) {
        DepthFirst.visit(fromTree, new DepthFirst.Visitor() {
            int n = toRoot;
            public boolean preorder(int node) {
                if (node != fromRoot) {
                    n = toTree.addNode(n);
                }
                return true;
            }

            public void postorder(int node) {
                n = toTree.getParent(n);
            }
        },
        fromRoot);
        return toTree;
    }
    
    public static Tree insert(Tree fromTree, Tree toTree) {
        return insert(fromTree, Tree.ROOT, toTree, Tree.ROOT);
    }
}
