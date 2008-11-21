/*$Id: LinkOutContextMenuListener.java,v 1.1 2006/06/14 18:12:46 mes Exp $*/
package cytoscape.linkout;

import org.cytoscape.view.NodeContextMenuListener;
import org.cytoscape.view.NodeView;

import javax.swing.*;


/**
 * LinkOutContextMenuListener implements NodeContextMenuListener
 * When a node is selected it calls LinkOut that adds the linkout menu to the node's popup menu
 */
public class LinkOutNodeContextMenuListener implements NodeContextMenuListener {
	/**
	 * Creates a new LinkOutNodeContextMenuListener object.
	 */
	public LinkOutNodeContextMenuListener() {
		//System.out.println("[LinkOutContextMenuListener]: Constructor called");
	}

	/**
	 * @param nodeView The clicked NodeView
	 * @param menu popup menu to add the LinkOut menu
	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
		//System.out.println("[LinkOutContextMenuListener]: addNodeContextMenuItem called");
		LinkOut lo = new LinkOut();

		if (menu == null) {
			menu = new JPopupMenu();
		}

		menu.add(new JSeparator());
		menu.add(lo.addLinks(nodeView));
		menu.add(new JSeparator());
	}
}
