/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import infovis.Tree;
import infovis.utils.IntStack;
import infovis.utils.RowIterator;
import cern.colt.function.IntProcedure;


/**
 * Depth First traversal algorithm for trees.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class DepthFirst {
    /**
     * Traverse the tree starting at a node with a depth first traversal,
     * calling the visitor at each node.
     *
     * @param tree the <code>Tree</code>
     * @param visitor the <code>DepthFirst,Visitor</code>
     * @param node the root node.
     */
    public static void visit(Tree tree, Visitor visitor, int node) {
        if (visitor.preorder(node)) {
            for (RowIterator iter = tree.childrenIterator(node); 
                iter.hasNext(); ) {
                visit(tree, visitor, iter.nextRow());
            }
            visitor.postorder(node);
        }
    }
    
    public static void visitPreorder(
            Tree tree, 
            IntProcedure visitor, 
            int node) {
        IntStack stack = new IntStack();
        stack.push(node);
        while(! stack.isEmpty()) {
            node = stack.pop();
            if (visitor.apply(node)) {
                int start = stack.size();
                for (RowIterator iter = tree.childrenIterator(node); 
                    iter.hasNext(); ) {
                    stack.push(iter.nextRow());
                }
                int end = stack.size()-1;
                while (start < end) {
                    int tmp = stack.getQuick(start);
                    stack.setQuick(start, stack.getQuick(end));
                    stack.setQuick(end, tmp);
                    start++;
                    end--;
                }
            }
        }
    }
    
    public static void visitPreorder(
            Tree tree, 
            IntProcedure visitor) {
        visitPreorder(tree, visitor, Tree.ROOT);
    }
    
    /**
     * Traverse the tree starting at a node with a depth first traversal,
     * calling the visitor at each node.
     *
     * @param tree the <code>Tree</code>
     * @param visitor the <code>DepthFirst,Visitor</code>
     */
    public static void visit(Tree tree, Visitor visitor) {
        visit(tree, visitor, Tree.ROOT);
    }

    /**
     * Class for traversing a tree depth first.
     */
    public interface Visitor {
        /**
         * Method called when the node is initially reached.
         *
         * @param node the reached node.
         *
         * @return true if the subtree should be traversed, false if it
         *         should be pruned from the traversal. In the latter case,
         *         the two other methods are not called for the node.
         */
        public boolean preorder(int node);
        
        /**
         * Method called when all the children of the node have been
         * traversed.
         *
         * @param node the reached node.
         */
        public void postorder(int node);
    }
}
