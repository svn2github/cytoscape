/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree.io;

import infovis.Tree;
import infovis.column.StringColumn;
import infovis.table.io.AbstractTableReader;
import infovis.utils.RowIterator;

import java.io.InputStream;

/**
 * Abstract Reader for Trees.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public abstract class AbstractTreeReader extends AbstractTableReader {
    protected Tree tree;

    protected AbstractTreeReader(InputStream in, String name, Tree tree) {
        super(in, name, tree);
        this.tree = tree;
    }

    public static int findNode(
            String name,
            int parent,
            Tree tree,
            StringColumn nameColumn) {
        if (parent == Tree.NIL) {
            parent = Tree.ROOT;
            if (nameColumn.isValueUndefined(parent)) {
                nameColumn.setExtend(parent, name);
            }
            else if (!nameColumn.get(parent).equals(name)) {
                return Tree.NIL; // shouldn't happen
            }
            return parent;
        }
        for (RowIterator iter = tree.childrenIterator(parent); iter.hasNext();) {
            int child = iter.nextRow();
            if (!nameColumn.isValueUndefined(child)
                    && nameColumn.get(child).equals(name)) {
                return child;
            }
        }
        int node = tree.addNode(parent);
        nameColumn.setExtend(node, name);

        return node;
    }

}
