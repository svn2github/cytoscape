/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import infovis.Tree;

/**
 * Generate value 1 for leaf nodes and 0 for interior nodes.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class RowIsLeafGenerator implements RowDoubleValueGenerator {
    protected Tree tree;
    
    public RowIsLeafGenerator(Tree tree) {
        this.tree = tree;
    }

    public double generate(int row) {
        return tree.isLeaf(row) ? 1 : 0;
    }

}
