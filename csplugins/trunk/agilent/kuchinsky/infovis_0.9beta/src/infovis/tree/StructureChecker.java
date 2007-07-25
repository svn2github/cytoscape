/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import cern.colt.map.OpenIntIntHashMap;
import infovis.Tree;
import infovis.utils.IntStack;


/**
 * Checks the integrity of a tree.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class StructureChecker implements DepthFirst.Visitor {
    Tree tree;
    OpenIntIntHashMap nodeMap;
    IntStack parentStack = new IntStack();
    
    /**
     * Constructor for StructureChecker.
     */
    public StructureChecker(Tree tree) {
        this.tree = tree;
        nodeMap = new OpenIntIntHashMap();
        for (int i = 0; i < tree.getRowCount(); i++) {
            nodeMap.put(i, 1);
        }
        parentStack.push(Tree.NIL);
        DepthFirst.visit(tree, this, Tree.ROOT);
        if (! nodeMap.isEmpty())
        	fail(Tree.ROOT);
    }

    void fail(int node) {
    	throw new RuntimeException("failed at node "+node);
    }

    public boolean preorder(int node) {
        if (parentStack.top() != tree.getParent(node))
            fail(node);
        if (! nodeMap.containsKey(node))
            fail(node);
        nodeMap.removeKey(node);
        parentStack.push(node);
        return true;
    }
    public void postorder(int node) {
    	parentStack.pop();
    }
    
    public static boolean check(Tree tree) {
    	try {
    	    new StructureChecker(tree);
    	}
    	catch(RuntimeException e) {
    	    return false;
    	}
    	return true;
    }
}
