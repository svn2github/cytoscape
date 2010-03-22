package cytoscape.view;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import cytoscape.Cytoscape;

public class TreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -678559990857492912L;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		if (hasView(value)) {
			setBackgroundNonSelectionColor(java.awt.Color.green.brighter());
			setBackgroundSelectionColor(java.awt.Color.green.darker());
		} else {
			setBackgroundNonSelectionColor(java.awt.Color.red.brighter());
			setBackgroundSelectionColor(java.awt.Color.red.darker());
		}

		return this;
	}

	private boolean hasView(Object value) {
		NetworkTreeNode node = (NetworkTreeNode) value;
		setToolTipText(Cytoscape.getNetwork(node.getNetworkID()).getTitle());

		return Cytoscape.viewExists(node.getNetworkID());
	}
}