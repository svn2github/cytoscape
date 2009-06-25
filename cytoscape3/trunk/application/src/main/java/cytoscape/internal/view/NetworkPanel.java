/*
 File: NetworkPanel.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.SelectedEdgesEvent;
import org.cytoscape.model.events.SelectedEdgesListener;
import org.cytoscape.model.events.SelectedNodesEvent;
import org.cytoscape.model.events.SelectedNodesListener;
import org.cytoscape.model.events.UnselectedEdgesEvent;
import org.cytoscape.model.events.UnselectedEdgesListener;
import org.cytoscape.model.events.UnselectedNodesEvent;
import org.cytoscape.model.events.UnselectedNodesListener;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;

import cytoscape.CyNetworkManager;
import cytoscape.events.NetworkAboutToBeDestroyedEvent;
import cytoscape.events.NetworkAboutToBeDestroyedListener;
import cytoscape.events.NetworkAddedEvent;
import cytoscape.events.NetworkAddedListener;
import cytoscape.events.NetworkViewAboutToBeDestroyedEvent;
import cytoscape.events.NetworkViewAboutToBeDestroyedListener;
import cytoscape.events.NetworkViewAddedEvent;
import cytoscape.events.NetworkViewAddedListener;
import cytoscape.events.SetCurrentNetworkEvent;
import cytoscape.events.SetCurrentNetworkListener;
import cytoscape.events.SetCurrentNetworkViewEvent;
import cytoscape.events.SetCurrentNetworkViewListener;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.swing.AbstractTreeTableModel;
import cytoscape.util.swing.JTreeTable;
import cytoscape.util.swing.TreeTableModel;
import cytoscape.xtask.CreateNetworkPresentationTaskFactory;

/**
 *
 */
public class NetworkPanel extends JPanel implements TreeSelectionListener,
		SelectedNodesListener, SelectedEdgesListener, UnselectedNodesListener,
		UnselectedEdgesListener, SetCurrentNetworkViewListener,
		SetCurrentNetworkListener, NetworkAddedListener,
		NetworkViewAddedListener, NetworkAboutToBeDestroyedListener,
		NetworkViewAboutToBeDestroyedListener {
	private final static long serialVersionUID = 1213748836763243L;
	private final JTreeTable treeTable;
	private final NetworkTreeNode root;
	private JPanel navigatorPanel;
	private JPopupMenu popup;
	private PopupActionListener popupActionListener;
	private JMenuItem createViewItem;
	private JMenuItem destroyViewItem;
	private JMenuItem destroyNetworkItem;
	private JMenuItem editNetworkTitle;
	private JSplitPane split;
	private final NetworkTreeTableModel treeTableModel;
	private final CyNetworkManager netmgr;
	private final NetworkViewManager viewmgr;
	private Long currentNetId;
	private final TaskManager taskManager;
	private final CreateNetworkPresentationTaskFactory viewFactory;
	private final CyNetworkNaming naming;

	/**
	 * Constructor for the Network Panel.
	 * 
	 * @param desktop
	 */
	public NetworkPanel(final NetworkViewManager viewmgr,
			final CyNetworkManager netmgr, final BirdsEyeViewHandler bird,
			final TaskManager taskManager,
			final CreateNetworkPresentationTaskFactory viewFactory,
			CyNetworkNaming naming) {
		super();
		this.netmgr = netmgr;
		this.viewmgr = viewmgr;
		this.taskManager = taskManager;
		this.viewFactory = viewFactory;
		this.naming = naming;

		root = new NetworkTreeNode("Network Root", 0L);
		treeTableModel = new NetworkTreeTableModel(root);
		treeTable = new JTreeTable(treeTableModel);
		initialize();
		setNavigator(bird.getBirdsEyeView());
		currentNetId = null;

		/*
		 * Remove CTR-A for enabling select all function in the main window.
		 */
		for (KeyStroke listener : treeTable.getRegisteredKeyStrokes()) {
			if (listener.toString().equals("ctrl pressed A")) {
				final InputMap map = treeTable.getInputMap();
				map.remove(listener);
				treeTable.setInputMap(WHEN_FOCUSED, map);
				treeTable.setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, map);
			}
		}

	}

	protected void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(180, 700));

		treeTable.getTree().addTreeSelectionListener(this);
		treeTable.getTree().setRootVisible(false);

		ToolTipManager.sharedInstance().registerComponent(treeTable);

		treeTable.getTree().setCellRenderer(new TreeCellRenderer());

		treeTable.getColumn("Network").setPreferredWidth(100);
		treeTable.getColumn("Nodes").setPreferredWidth(45);
		treeTable.getColumn("Edges").setPreferredWidth(45);

		navigatorPanel = new JPanel();
		navigatorPanel.setMinimumSize(new Dimension(180, 180));
		navigatorPanel.setMaximumSize(new Dimension(180, 180));
		navigatorPanel.setPreferredSize(new Dimension(180, 180));

		JScrollPane scroll = new JScrollPane(treeTable);

		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll,
				navigatorPanel);
		split.setResizeWeight(1);

		add(split);

		/*
		 * this mouse listener listens for the right-click event and will show
		 * the pop-up window when that occurrs
		 */
		treeTable.addMouseListener(new PopupListener());

		// create and populate the popup window
		popup = new JPopupMenu();
		editNetworkTitle = new JMenuItem(PopupActionListener.EDIT_TITLE);
		createViewItem = new JMenuItem(PopupActionListener.CREATE_VIEW);
		destroyViewItem = new JMenuItem(PopupActionListener.DESTROY_VIEW);
		destroyNetworkItem = new JMenuItem(PopupActionListener.DESTROY_NETWORK);

		// action listener which performs the tasks associated with the popup
		// listener
		popupActionListener = new PopupActionListener(this, netmgr,
				taskManager, viewFactory, naming);
		editNetworkTitle.addActionListener(popupActionListener);
		createViewItem.addActionListener(popupActionListener);
		destroyViewItem.addActionListener(popupActionListener);
		destroyNetworkItem.addActionListener(popupActionListener);
		popup.add(editNetworkTitle);
		popup.add(createViewItem);
		popup.add(destroyViewItem);
		popup.add(destroyNetworkItem);
	}
	
	public void addContextMenu(TaskFactory factory, Map props) {
		System.out.println("\n\n\n-----> Adding context menu for network panel\n\n\n");
	}
	
	public void removeContextMenu(TaskFactory factory, Map props) {
		
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param comp
	 *            DOCUMENT ME!
	 */
	public void setNavigator(final Component comp) {
		split.setRightComponent(comp);
		split.validate();
	}

	/**
	 * This is used by Session writer.
	 * 
	 * @return
	 */
	public JTreeTable getTreeTable() {
		return treeTable;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public JPanel getNavigatorPanel() {
		return navigatorPanel;
	}

	/**
	 * Remove a network from the panel.
	 * 
	 * @param network_id
	 */
	public void removeNetwork(final Long network_id) {
		final NetworkTreeNode node = getNetworkNode(network_id);
		final Enumeration children = node.children();
		final List<NetworkTreeNode> removed_children = new ArrayList<NetworkTreeNode>();

		while (children.hasMoreElements()) {
			removed_children.add((NetworkTreeNode) children.nextElement());
		}

		for (NetworkTreeNode child : removed_children) {
			child.removeFromParent();
			root.add(child);
		}

		node.removeFromParent();
		treeTable.getTree().updateUI();
		treeTable.doLayout();
	}

	/**
	 * update a network title
	 * 
	 * @param network
	 */
	public void updateTitle(final CyNetwork network) {
		// updates the title in the network panel
		if (treeTable.getTree().getSelectionPath() != null) { // user has
																// selected
																// something
			treeTableModel.setValueAt(
					network.attrs().get("name", String.class), treeTable
							.getTree().getSelectionPath()
							.getLastPathComponent(), 0);
		} else { // no selection, means the title has been changed
					// programmatically
			NetworkTreeNode node = getNetworkNode(network.getSUID());
			treeTableModel.setValueAt(
					network.attrs().get("name", String.class), node, 0);
		}
		treeTable.getTree().updateUI();
		treeTable.doLayout();
		// updates the title in the networkViewMap
		viewmgr.updateNetworkTitle(network);
	}

	public void handleEvent(NetworkAboutToBeDestroyedEvent nde) {
		System.out.println("NetworkPanel: network about to be destroyed "
				+ nde.getNetwork().getSUID());
		removeNetwork(nde.getNetwork().getSUID());
	}

	public void handleEvent(NetworkAddedEvent e) {
		System.out.println("NetworkPanel: network added "
				+ e.getNetwork().getSUID());
		addNetwork(e.getNetwork().getSUID(), -1l);
	}

	public void handleEvent(SetCurrentNetworkViewEvent e) {
		System.out.println("NetworkPanel: set current network view "
				+ e.getNetworkView().getSource().getSUID());
		long curr = e.getNetworkView().getSource().getSUID();
		// System.out.println("NetworkPanel setting current network view: " +
		// curr);
		if (currentNetId == null || curr != currentNetId.longValue())
			focusNetworkNode(curr);
	}

	public void handleEvent(SetCurrentNetworkEvent e) {
		System.out.println("NetworkPanel: set current network "
				+ e.getNetwork().getSUID());
		long curr = e.getNetwork().getSUID();
		// System.out.println("NetworkPanel setting current network view: " +
		// curr);
		if (currentNetId == null || curr != currentNetId.longValue())
			focusNetworkNode(curr);
	}

	public void handleEvent(NetworkViewAboutToBeDestroyedEvent nde) {
		System.out.println("NetworkPanel: network view about to be destroyed "
				+ nde.getNetworkView().getSource().getSUID());
		treeTable.getTree().updateUI();
	}

	public void handleEvent(NetworkViewAddedEvent nde) {
		System.out.println("NetworkPanel: network view added "
				+ nde.getNetworkView().getSource().getSUID());
		treeTable.getTree().updateUI();
	}

	public void handleEvent(SelectedNodesEvent event) {
		System.out.println("NetworkPanel: selected nodes ");
		treeTable.getTree().updateUI();
	}

	public void handleEvent(SelectedEdgesEvent event) {
		System.out.println("NetworkPanel: selected edges ");
		treeTable.getTree().updateUI();
	}

	public void handleEvent(UnselectedNodesEvent event) {
		System.out.println("NetworkPanel: unselected nodes ");
		treeTable.getTree().updateUI();
	}

	public void handleEvent(UnselectedEdgesEvent event) {
		System.out.println("NetworkPanel: unselected edges ");
		treeTable.getTree().updateUI();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param network_id
	 *            DOCUMENT ME!
	 * @param parent_id
	 *            DOCUMENT ME!
	 */
	public void addNetwork(Long network_id, Long parent_id) {
		// first see if it exists
		if (getNetworkNode(network_id) == null) {
			// System.out.println("NetworkPanel: addNetwork " + network_id);
			NetworkTreeNode dmtn = new NetworkTreeNode(netmgr.getNetwork(
					network_id).attrs().get("name", String.class), network_id);

			if (parent_id != null && getNetworkNode(parent_id) != null) {
				getNetworkNode(parent_id).add(dmtn);
			} else {
				root.add(dmtn);
			}

			// apparently this doesn't fire valueChanged
			treeTable.getTree().collapsePath(
					new TreePath(new TreeNode[] { root }));

			treeTable.getTree().updateUI();
			TreePath path = new TreePath(dmtn.getPath());
			treeTable.getTree().expandPath(path);
			treeTable.getTree().scrollPathToVisible(path);
			treeTable.doLayout();

			// this is necessary because valueChanged is not fired above
			focusNetworkNode(network_id);
		} else {
			// System.out.println("addNetwork getNetworkTreeNode returned: " +
			// getNetworkNode(network_id).getNetworkID());
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param network_id
	 *            DOCUMENT ME!
	 */
	public void focusNetworkNode(Long network_id) {
		// System.out.println("NetworkPanel: focus network node");
		DefaultMutableTreeNode node = getNetworkNode(network_id);

		if (node != null) {
			// System.out.println("NetworkPanel - setting currentNetId");
			// do this first so that events triggered by subequent lines don't
			// recurse unecessarily
			currentNetId = network_id;

			// fires valueChanged if the network isn't already selected
			treeTable.getTree().getSelectionModel().setSelectionPath(
					new TreePath(node.getPath()));
			treeTable.getTree().scrollPathToVisible(
					new TreePath(node.getPath()));
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param network_id
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public NetworkTreeNode getNetworkNode(Long network_id) {
		Enumeration tree_node_enum = root.breadthFirstEnumeration();

		while (tree_node_enum.hasMoreElements()) {
			NetworkTreeNode node = (NetworkTreeNode) tree_node_enum
					.nextElement();

			if (node.getNetworkID().equals(network_id)) {
				return node;
			}
		}

		return null;
	}

	/**
	 * This method highlights a network in the NetworkPanel.
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void valueChanged(TreeSelectionEvent e) {
		// System.out.println("NetworkPanel: valueChanged - " +
		// e.getSource().getClass().getName());
		JTree mtree = treeTable.getTree();

		// sets the "current" network based on last node in the tree selected
		NetworkTreeNode node = (NetworkTreeNode) mtree
				.getLastSelectedPathComponent();
		if (node == null || node.getUserObject() == null) {
			// System.out.println("NetworkPanel: null node - returning");
			return;
		}

		netmgr.setCurrentNetwork(node.getNetworkID());

		// creates a list of all selected networks
		List<Long> networkList = new LinkedList<Long>();
		try {
			for (int i = mtree.getMinSelectionRow(); i <= mtree
					.getMaxSelectionRow(); i++) {
				NetworkTreeNode n = (NetworkTreeNode) mtree.getPathForRow(i)
						.getLastPathComponent();
				if (n != null && n.getUserObject() != null
						&& mtree.isRowSelected(i))
					networkList.add(n.getNetworkID());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (networkList.size() > 0) {
			netmgr.setSelectedNetworkViews(networkList);
		}
	}

	/**
	 * Inner class that extends the AbstractTreeTableModel
	 */
	class NetworkTreeTableModel extends AbstractTreeTableModel {
		String[] columns = { "Network", "Nodes", "Edges" };
		Class[] columns_class = { TreeTableModel.class, String.class,
				String.class };

		public NetworkTreeTableModel(Object root) {
			super(root);
		}

		public Object getChild(Object parent, int index) {
			Enumeration tree_node_enum = ((DefaultMutableTreeNode) getRoot())
					.breadthFirstEnumeration();

			while (tree_node_enum.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree_node_enum
						.nextElement();

				if (node == parent) {
					return node.getChildAt(index);
				}
			}

			return null;
		}

		public int getChildCount(Object parent) {
			Enumeration tree_node_enum = ((DefaultMutableTreeNode) getRoot())
					.breadthFirstEnumeration();

			while (tree_node_enum.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree_node_enum
						.nextElement();

				if (node == parent) {
					return node.getChildCount();
				}
			}

			return 0;
		}

		public int getColumnCount() {
			return columns.length;
		}

		public String getColumnName(int column) {
			return columns[column];
		}

		public Class getColumnClass(int column) {
			return columns_class[column];
		}

		public Object getValueAt(Object node, int column) {
			if (column == 0)
				return ((DefaultMutableTreeNode) node).getUserObject();
			else if (column == 1) {
				CyNetwork cyNetwork = netmgr
						.getNetwork(((NetworkTreeNode) node).getNetworkID());

				return ""
						+ cyNetwork.getNodeCount()
						+ "("
						+ CyDataTableUtil.getNodesInState(cyNetwork,
								"selected", true).size() + ")";
			} else if (column == 2) {
				CyNetwork cyNetwork = netmgr
						.getNetwork(((NetworkTreeNode) node).getNetworkID());

				return ""
						+ cyNetwork.getEdgeCount()
						+ "("
						+ CyDataTableUtil.getEdgesInState(cyNetwork,
								"selected", true).size() + ")";
			}

			return "";
		}

		public void setValueAt(Object aValue, Object node, int column) {
			if (column == 0) {
				((DefaultMutableTreeNode) node).setUserObject(aValue);
			} else
				JOptionPane.showMessageDialog(NetworkPanel.this,
						"Error: assigning value at in NetworkPanel");
			// This function is not used to set node and edge values.
		}
	}

	public class NetworkTreeNode extends DefaultMutableTreeNode {
		private final static long serialVersionUID = 1213748836736485L;
		protected Long network_uid;

		public NetworkTreeNode(Object userobj, Long id) {
			super(userobj.toString());
			network_uid = id;
		}

		protected void setNetworkID(Long id) {
			network_uid = id;
		}

		protected Long getNetworkID() {
			return network_uid;
		}
	}

	private class TreeCellRenderer extends DefaultTreeCellRenderer {
		private final static long serialVersionUID = 1213748836751014L;

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

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
			CyNetwork n = netmgr.getNetwork(node.getNetworkID());
			if (n != null)
				setToolTipText(n.attrs().get("name", String.class));
			else
				setToolTipText("Root");

			return netmgr.viewExists(node.getNetworkID());
		}
	}

	/**
	 * This class listens to mouse events from the TreeTable, if the mouse event
	 * is one that is canonically associated with a popup menu (ie, a right
	 * click) it will pop up the menu with option for destroying view, creating
	 * view, and destroying network (this is platform specific apparently)
	 */
	protected class PopupListener extends MouseAdapter {
		/**
		 * Don't know why you need both of these, but this is how they did it in
		 * the example
		 */
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * Don't know why you need both of these, but this is how they did it in
		 * the example
		 */
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * if the mouse press is of the correct type, this function will maybe
		 * display the popup
		 */
		private void maybeShowPopup(MouseEvent e) {
			// check for the popup type
			if (e.isPopupTrigger()) {
				// get the row where the mouse-click originated
				int row = treeTable.rowAtPoint(e.getPoint());

				if (row != -1) {
					JTree tree = treeTable.getTree();
					TreePath treePath = tree.getPathForRow(row);
					Long networkID = ((NetworkTreeNode) treePath
							.getLastPathComponent()).getNetworkID();

					CyNetwork cyNetwork = netmgr.getNetwork(networkID);

					if (cyNetwork != null) {
						/*
						 * disable or enable specific options with respect to
						 * the actual network that is selected
						 */
						if (netmgr.viewExists(networkID)) {
							// disable the view creation item
							createViewItem.setEnabled(false);
							destroyViewItem.setEnabled(true);
						} // end of if ()
						else {
							createViewItem.setEnabled(true);
							destroyViewItem.setEnabled(false);
						} /*
						 * end of else let the actionlistener know which network
						 * it should be operating on when (if) it is called
						 */

						popupActionListener.setActiveNetwork(cyNetwork);
						// display the popup
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}
	}
}
