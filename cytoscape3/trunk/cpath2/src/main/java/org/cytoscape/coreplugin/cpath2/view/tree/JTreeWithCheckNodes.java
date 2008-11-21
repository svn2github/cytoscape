package org.cytoscape.coreplugin.cpath2.view.tree;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * JTree with selectable leaves.
 *
 * Code was originally obtained from:
 * http://www.javaresearch.org/source/javaresearch/jrlib0.6/org/jr/swing/tree/
 *
 * and, has since been modified.
 */
public class JTreeWithCheckNodes extends JTree {

    /**
     * Constructor.
     *
     * @param rootNode Root Node.
     */
    public JTreeWithCheckNodes(TreeNode rootNode) {
        super(rootNode);
        setCellRenderer(new CheckNodeRenderer());
        getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION
        );
        putClientProperty("JTree.lineStyle", "Angled");
        addMouseListener(new NodeSelectionListener(this));
    }
}

