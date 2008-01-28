/*$Id: LinkOutContextMenuListener.java,v 1.1 2006/06/14 18:12:46 mes Exp $*/
package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import ding.view.NodeContextMenuListener;

import giny.view.NodeView;

import javax.swing.JPopupMenu;
import javax.swing.JSeparator;



public class PopupNodeContextMenuListener implements NodeContextMenuListener {
	/**
	 * Creates a new LinkOutNodeContextMenuListener object.
	 */
	public PopupNodeContextMenuListener() {
		//System.out.println("[LinkOutContextMenuListener]: Constructor called");
	}

	/**
	 * @param nodeView The clicked NodeView
	 * @param menu popup menu to add the LinkOut menu
	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
		//System.out.println("[LinkOutContextMenuListener]: addNodeContextMenuItem called");
		PathwayWalking pw = new PathwayWalking();
		pw.startGUI(nodeView);
	}
}
