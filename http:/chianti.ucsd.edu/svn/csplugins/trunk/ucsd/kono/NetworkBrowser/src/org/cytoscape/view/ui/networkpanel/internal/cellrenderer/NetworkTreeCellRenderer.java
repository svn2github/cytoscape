package org.cytoscape.view.ui.networkpanel.internal.cellrenderer;

import static org.cytoscape.view.ui.networkpanel.internal.TreeObjectType.GROUP;
import static org.cytoscape.view.ui.networkpanel.internal.TreeObjectType.NETWORK;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.cytoscape.view.ui.networkpanel.NetworkBrowserPlugin;
import org.cytoscape.view.ui.networkpanel.internal.NetworkBrowserTreeNode;
import org.cytoscape.view.ui.networkpanel.internal.NetworkTreeNode;
import org.cytoscape.view.ui.networkpanel.internal.TreeObjectType;

import cytoscape.Cytoscape;

public class NetworkTreeCellRenderer extends DefaultTreeCellRenderer {

	final Icon GROUP_ICON = new ImageIcon(NetworkBrowserPlugin.class
			.getResource("images/chart_organisation.png"));
	final Icon NETWORK_ICON = new ImageIcon(NetworkBrowserPlugin.class
			.getResource("images/bullet_green.png"));

	private static final Color UC = new Color(0, 190, 0, 100);

	private final Map<TreeObjectType, Icon> iconMap;

	public NetworkTreeCellRenderer() {
		iconMap = new HashMap<TreeObjectType, Icon>();
		buildIconMap();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		if (value instanceof NetworkBrowserTreeNode && row != 0) {
			this.setIcon(iconMap.get(((NetworkBrowserTreeNode) value)
					.getObjectType()));
		}

		//
		// if (hasView(value)) {
		// setBackgroundNonSelectionColor(java.awt.Color.green.brighter());
		// setBackgroundSelectionColor(java.awt.Color.green.darker());
		// } else {
		// setBackgroundNonSelectionColor(java.awt.Color.red.brighter());
		// setBackgroundSelectionColor(java.awt.Color.red.darker());
		// }

		return this;
	}

	private boolean hasView(Object value) {
		if (value instanceof NetworkTreeNode) {
			NetworkTreeNode node = (NetworkTreeNode) value;
			setToolTipText(Cytoscape.getNetwork(node.getObjectID()).getTitle());

			return Cytoscape.viewExists(node.getObjectID());
		}

		return false;
	}

	private void buildIconMap() {
		iconMap.put(NETWORK, NETWORK_ICON);
		iconMap.put(GROUP, GROUP_ICON);
	}
}