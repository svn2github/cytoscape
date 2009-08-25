package org.cytoscape.view.ui.networkpanel.internal;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.cytoscape.view.ui.networkpanel.NetworkBrowserPlugin;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.groups.CyGroup;

public class NetworkTreeTableModel extends DefaultTreeTableModel implements
		TreeSelectionListener, SelectEventListener {

	private Map<String, MutableTreeTableNode> treeNodeMap;

	private static final Icon DEF = new ImageIcon(NetworkBrowserPlugin.class
			.getResource("images/def_networkicon.png"));

	public NetworkTreeTableModel(NetworkTreeNode root) {
		super(root);
		treeNodeMap = new HashMap<String, MutableTreeTableNode>();
	}

	protected void groupCreated(CyGroup group) {
		System.out.println("*** Adding Group: " + group.getGroupName());
		MutableTreeTableNode node = treeNodeMap.get(Cytoscape
				.getCurrentNetwork().getIdentifier());

		GroupTreeNode dmtn = new GroupTreeNode(Cytoscape.getCurrentNetwork(),
				group, group.getGroupName(), this.getColumnCount());
		dmtn.setValueAt(group.getNodes().size(), 1);
		((AbstractMutableTreeTableNode) node).add(dmtn);
		treeNodeMap.put(group.getGroupName(), dmtn);
		
	}

	private void groupChanged(CyGroup group) {
		// TODO Auto-generated method stub

	}

	private void groupRemoved(CyGroup group) {
		// TODO Auto-generated method stub

	}

	protected void updateTitle(CyNetwork _network) {
		// TODO Auto-generated method stub

	}

	protected void focusNetworkNode(String newValue) {
		// TODO Auto-generated method stub

	}

	protected void removeNetwork(String networkID) {
		treeNodeMap.remove(networkID);
	}

	/**
	 * Add network node to the tree.
	 * 
	 * @param network_id
	 * @param parent_id
	 */
	protected void addNetwork(String network_id, String parent_id) {

		try {
			// first see if it exists
			if (treeNodeMap.get(network_id) == null) {
				System.out.println("====== Adding Network: " + network_id);
				CyNetwork target = Cytoscape.getNetwork(network_id);

				NetworkTreeNode dmtn = new NetworkTreeNode(Cytoscape
						.getNetwork(network_id).getTitle(), network_id, this
						.getColumnCount());
				Cytoscape.getNetwork(network_id).addSelectEventListener(this);

				if (parent_id != null && treeNodeMap.get(parent_id) != null) {
					((AbstractMutableTreeTableNode) treeNodeMap.get(parent_id))
							.add(dmtn);
				} else {
					System.out.println("====== To ROOT: "
							+ getRoot().getChildCount());
					((AbstractMutableTreeTableNode) getRoot()).add(dmtn);
					System.out.println("====== After ROOT ADD: "
							+ getRoot().getChildCount());
				}

				dmtn.setValueAt(target.getNodeCount(), 1);
				dmtn.setValueAt(target.getEdgeCount(), 2);
				//

				// this is necessary because valueChanged is not fired above
				focusNetworkNode(network_id);

				treeNodeMap.put(network_id, dmtn);

				// System.out.println("=============> Value Set: " +
				// getValueAt(dmtn, 1) + ", and count = " + dmtn.getValueAt(1));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void onSelectEvent(SelectEvent arg0) {
		// TODO Auto-generated method stub

	}

}
