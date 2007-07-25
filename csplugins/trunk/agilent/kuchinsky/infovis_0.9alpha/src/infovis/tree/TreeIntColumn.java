/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import infovis.Tree;
import infovis.column.IntColumn;
import infovis.metadata.IO;

/**
 * Class TreeIntColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public abstract class TreeIntColumn extends IntColumn 
    implements TreeModelListener {
    protected Tree tree;
    
    public TreeIntColumn(String name, Tree tree) {
        super(name, tree.getRowCount());
        this.tree = tree;
        tree.addTreeModelListener(this);
        getMetadata().put(IO.IO_TRANSIENT, Boolean.TRUE);
        update();
    }
    
    public void dispose() {
        tree.removeTreeModelListener(this);
        clear();
        tree = null;
    }

    public abstract void update();
    
    public void treeNodesChanged(TreeModelEvent e) {
        update();
    }
    
    public void treeNodesInserted(TreeModelEvent e) {
        update();
    }
    
    public void treeNodesRemoved(TreeModelEvent e) {
        update();
    }
    
    public void treeStructureChanged(TreeModelEvent e) {
        update();
    }
    
}
