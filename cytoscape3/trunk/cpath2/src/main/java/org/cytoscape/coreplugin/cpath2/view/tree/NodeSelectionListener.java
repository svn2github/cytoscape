package org.cytoscape.coreplugin.cpath2.view.tree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Listens for node selection events.
 */
class NodeSelectionListener extends MouseAdapter {
    JTree tree;

    /**
     * Constructor.
     *
     * @param tree JTree Object.
     */
    NodeSelectionListener(JTree tree) {
        this.tree = tree;
    }

    /**
     * Mouse Click Event.
     *
     * @param e MouseEvent Object.
     */
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int row = tree.getRowForLocation(x, y);
        TreePath path = tree.getPathForRow(row);
        if (path != null) {
            CheckNode node = (CheckNode) path.getLastPathComponent();
            boolean isSelected = !(node.isSelected());
            node.setSelected(isSelected);
            if (node.getSelectionMode() == CheckNode.DIG_IN_SELECTION) {
                if (isSelected) {
                    tree.expandPath(path);
                } else {
                    tree.collapsePath(path);
                }
            }
            ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
            if (row == 0) {
                tree.revalidate();
                tree.repaint();
            }
        }
    }
}
