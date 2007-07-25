/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import infovis.Column;
import infovis.Tree;
import infovis.utils.RowIterator;

/**
 * Compare two trees for equalities.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class Compare {
    Tree tree1;
    Tree tree2;

	
    /**
     * Constructor for Compare.
     */
    public Compare() {
	super();
    }
	
    public boolean equals(Tree tree1, Tree tree2) {
	this.tree1 = tree1;
	this.tree2 = tree2;
	int i;
		
	for (i = 0; i < tree1.getColumnCount(); i++) {
	    Column c1 = tree1.getColumnAt(i);
	    if (c1.isInternal())
		continue;
	    Column c2 = tree2.getColumn(c1.getName());
	    if (c2 == null || c1.getValueClass() != c2.getValueClass())
		return false;
	}
	for (i = 0; i < tree2.getColumnCount(); i++) {
	    Column c2 = tree2.getColumnAt(i);
	    if (c2.isInternal())
		continue;
	    Column c1 = tree1.getColumn(c2.getName());
	    if (c1 == null)
		return false;
	}
	return visit(Tree.ROOT, Tree.ROOT);
		
    }

    protected boolean visit(int node1, int node2) {
	if (node1 == Tree.NIL && node2 == Tree.NIL)
	    return true;
	if (node1 == Tree.NIL || node2 == Tree.NIL)
	    return false;
		
	if (! attributesEqual(node1, node2)) {
	    return false;
	}
        RowIterator it1;
        RowIterator it2;
	for (it1 = tree1.childrenIterator(node1), it2 = tree2.childrenIterator(node2);
	     it1.hasNext() && it2.hasNext();) {
            int child1 = it1.nextRow();
            int child2 = it2.nextRow();
	    if (! visit(child1, child2))
		return false;
	}
	return true;		
    }
	
    protected boolean attributesEqual(int node1, int node2) {
	for (int i = 0; i < tree1.getColumnCount(); i++) {
	    Column c1 = tree1.getColumnAt(i);
	    if (c1.isInternal())
		continue;
	    Column c2 = tree2.getColumn(c1.getName());
	    if (! c1.isValueUndefined(node1) || ! c2.isValueUndefined(node2))
		return false;
	    if (! c1.getValueAt(node1).equals(c2.getValueAt(node2)))
		return false;
	}
		
	return true;
    }
}
