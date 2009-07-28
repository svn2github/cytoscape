package org.cytoscape.view.ui.networkpanel.internal;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class NetworkTreeNodeListener extends MouseAdapter {

	private JTree targetTree;
	
	public NetworkTreeNodeListener(JTree targetTree) {
		this.targetTree = targetTree;
	}
	
	public void mousePressed(MouseEvent e) {
		int selRow = targetTree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = targetTree.getPathForLocation(e.getX(), e.getY());
		if (selRow != -1) {
			if (e.getClickCount() == 1) {
				processSingleClick(selRow, selPath);
			}
		}
	}
	
	private void processSingleClick(int selRow, TreePath selPath) {
		System.out.println("Tree node processed--------> " + selRow);
	}

}
