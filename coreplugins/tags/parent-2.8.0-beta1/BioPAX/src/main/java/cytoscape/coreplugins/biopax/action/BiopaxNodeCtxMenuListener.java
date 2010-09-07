package cytoscape.coreplugins.biopax.action;

import giny.view.NodeView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ding.view.NodeContextMenuListener;

public class BiopaxNodeCtxMenuListener implements NodeContextMenuListener {

	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
		JMenuItem myMenuItem = new JMenuItem("Show OWL");
		myMenuItem.addActionListener(new DisplayBiopaxXmlAction(nodeView));
		if (menu == null) {
			menu = new JPopupMenu();
		}
		menu.add(myMenuItem);

	}

}
