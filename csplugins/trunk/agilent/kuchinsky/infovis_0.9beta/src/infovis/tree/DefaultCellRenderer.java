/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree;

import infovis.Tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Cell rendered for trees.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class DefaultCellRenderer extends DefaultTreeCellRenderer {
    String columnName;
	
    public DefaultCellRenderer() {
	this(null);
    }
	
    public DefaultCellRenderer(String name) {
	columnName = name;
    }
	
    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(JTree, Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(
						  JTree jtree,
						  Object value,
						  boolean selected,
						  boolean expanded,
						  boolean leaf,
						  int row,
						  boolean hasFocus) {
	if (columnName == null)
	    return super.getTreeCellRendererComponent(
						      jtree, value, selected, expanded, leaf, row, hasFocus);
		
	Tree tree = (Tree)jtree.getModel();
	return super.getTreeCellRendererComponent(
						  jtree,
						  tree.getColumn(columnName).getValueAt(((Integer)value).intValue()),
						  selected, expanded, leaf, row, hasFocus);
    }

    /**
     * Returns the columnName.
     * @return int
     */
    public String getColumnName() {
	return columnName;
    }

    /**
     * Sets the column.
     * @param name The column name to set.
     */
    public void setColumnName(String name) {
	this.columnName = name;
    }
	

}
