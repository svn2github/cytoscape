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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.model.events.SelectedEdgesEvent;
import org.cytoscape.model.events.SelectedEdgesListener;
import org.cytoscape.model.events.SelectedNodesEvent;
import org.cytoscape.model.events.SelectedNodesListener;
import org.cytoscape.model.events.UnselectedEdgesEvent;
import org.cytoscape.model.events.UnselectedEdgesListener;
import org.cytoscape.model.events.UnselectedNodesEvent;
import org.cytoscape.model.events.UnselectedNodesListener;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.session.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.session.events.NetworkAddedEvent;
import org.cytoscape.session.events.NetworkAddedListener;
import org.cytoscape.session.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.session.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.session.events.SetCurrentNetworkEvent;
import org.cytoscape.session.events.SetCurrentNetworkListener;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.util.swing.AbstractTreeTableModel;
import org.cytoscape.util.swing.JTreeTable;
import org.cytoscape.util.swing.TreeTableModel;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cytoscape.internal.task.NetworkCollectionTaskFactoryTunableAction;
import cytoscape.internal.task.NetworkTaskFactoryTunableAction;
import cytoscape.internal.task.NetworkViewCollectionTaskFactoryTunableAction;
import cytoscape.internal.task.NetworkViewTaskFactoryTunableAction;
import cytoscape.internal.task.TaskFactoryTunableAction;
import cytoscape.view.CyAction;

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

	private static final Logger logger = LoggerFactory.getLogger(NetworkPanel.class);
	
	private final JTreeTable treeTable;
	private final NetworkTreeNode root;
	private JPanel navigatorPanel;
	private JPopupMenu popup;
	private JSplitPane split;
	private final NetworkTreeTableModel treeTableModel;
	private final CyNetworkManager netmgr;
	private Long currentNetId;
	private final TaskManager taskManager;
	private Map<TaskFactory,JMenuItem> popupMap;
	private Map<TaskFactory,CyAction> popupActions;
	private CyEventHelper eventHelper;
	private Map<CyNetwork, RowSetMicroListener> nameListeners;


	/**
	 * Constructor for the Network Panel.
	 * 
	 * @param desktop
	 */
	public NetworkPanel(final CyNetworkManager netmgr, final BirdsEyeViewHandler bird,
	                    final TaskManager taskManager, final CyEventHelper eventHelper)
	{
		super();

		this.netmgr = netmgr;
		this.taskManager = taskManager;
		this.eventHelper = eventHelper;

		root = new NetworkTreeNode("Network Root", 0L);
		treeTableModel = new NetworkTreeTableModel(root);
		treeTable = new JTreeTable(treeTableModel);
		initialize();
		setNavigator(bird.getBirdsEyeView());
		currentNetId = null;
		nameListeners = new HashMap<CyNetwork,RowSetMicroListener>();

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
		setPreferredSize(new Dimension(300, 700));

		treeTable.getTree().addTreeSelectionListener(this);
		treeTable.getTree().setRootVisible(false);

		ToolTipManager.sharedInstance().registerComponent(treeTable);

		treeTable.getTree().setCellRenderer(new TreeCellRenderer());

		treeTable.getColumn("Network").setPreferredWidth(100);
		treeTable.getColumn("Nodes").setPreferredWidth(45);
		treeTable.getColumn("Edges").setPreferredWidth(45);

		navigatorPanel = new JPanel();
		navigatorPanel.setLayout(new BorderLayout());
		navigatorPanel.setPreferredSize(new Dimension(280, 280));
		navigatorPanel.setSize(new Dimension(280, 280));
		navigatorPanel.setBackground(Color.white);

		JScrollPane scroll = new JScrollPane(treeTable);

		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll,
				navigatorPanel);
		split.setResizeWeight(1);
		split.setDividerLocation(300);

		add(split);

		// this mouse listener listens for the right-click event and will show
		// the pop-up window when that occurrs
		treeTable.addMouseListener(new PopupListener());

		// create and populate the popup window
		popup = new JPopupMenu();
		popupMap = new HashMap<TaskFactory,JMenuItem>();
		popupActions = new HashMap<TaskFactory,CyAction>();
	}

	private void addFactory(TaskFactory factory, CyAction action) {
		JMenuItem item = new JMenuItem( action );
		popupMap.put(factory,item);
		popupActions.put(factory,action);
		popup.add(item);
		popup.addPopupMenuListener(action);
	}

	private void removeFactory(TaskFactory factory) {
		JMenuItem item = popupMap.remove(factory);
		if ( item != null )
			popup.remove(item);
		CyAction action = popupActions.remove(factory);
		if ( action != null ) 
			popup.removePopupMenuListener(action);
	}

	public void addTaskFactory(TaskFactory factory, Map props) {
		addFactory(factory, new TaskFactoryTunableAction(taskManager, factory, props, netmgr));
	}

	public void removeTaskFactory(TaskFactory factory, Map props) {
		removeFactory(factory);
	}

	public void addNetworkCollectionTaskFactory(NetworkCollectionTaskFactory factory, Map props) {
		addFactory(factory,new NetworkCollectionTaskFactoryTunableAction(taskManager, factory, props, netmgr));
	}

	public void removeNetworkCollectionTaskFactory(NetworkCollectionTaskFactory factory, Map props) {
		removeFactory(factory);
	}


	public void addNetworkViewCollectionTaskFactory(NetworkViewCollectionTaskFactory factory, Map props) {
		addFactory(factory, new NetworkViewCollectionTaskFactoryTunableAction(taskManager, factory, props, netmgr));
	}

	public void removeNetworkViewCollectionTaskFactory(NetworkViewCollectionTaskFactory factory, Map props) {
		removeFactory(factory);
	}

	public void addNetworkTaskFactory(NetworkTaskFactory factory, Map props) {
		addFactory(factory, new NetworkTaskFactoryTunableAction(taskManager, factory, props, netmgr));
	}

	public void removeNetworkTaskFactory(NetworkTaskFactory factory, Map props) {
		removeFactory(factory);
	}

	public void addNetworkViewTaskFactory(NetworkViewTaskFactory factory, Map props) {
		addFactory(factory, new NetworkViewTaskFactoryTunableAction(taskManager, factory, props, netmgr));
	}

	public void removeNetworkViewTaskFactory(NetworkViewTaskFactory factory, Map props) {
		removeFactory(factory);
	}
	
	/**
	 * DOCUMENT ME!
	 * 
	 * @param comp
	 *            DOCUMENT ME!
	 */
	public void setNavigator(final Component comp) {
		this.navigatorPanel.removeAll();
		this.navigatorPanel.add(comp, BorderLayout.CENTER);
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
	private void updateTitle(final CyNetwork network, final String name) {
		// updates the title in the network panel
		if (treeTable.getTree().getSelectionPath() != null) { // user has
																// selected
																// something
			treeTableModel.setValueAt( name, treeTable
							.getTree().getSelectionPath()
							.getLastPathComponent(), 0);
		} else { // no selection, means the title has been changed
					// programmatically
			NetworkTreeNode node = getNetworkNode(network.getSUID());
			treeTableModel.setValueAt( name, node, 0);
		}
		treeTable.getTree().updateUI();
		treeTable.doLayout();
	}
	

	//// Event handlers /////
	
	public void handleEvent(NetworkAboutToBeDestroyedEvent nde) {
		CyNetwork net = nde.getNetwork();
		logger.debug("Network about to be destroyed " + net.getSUID());
		removeNetwork(net.getSUID());
		RowSetMicroListener rsml = nameListeners.remove(net);
		if ( rsml != null )
			eventHelper.removeMicroListener( rsml, RowSetMicroListener.class, net.getCyRow().getDataTable() ); 
	}

	public void handleEvent(NetworkAddedEvent e) {
		CyNetwork net = e.getNetwork();
		logger.debug("Got NetworkAddedEvent.  Model ID = " + net.getSUID());
		
		addNetwork(net.getSUID(), -1l);
		RowSetMicroListener rsml = new AbstractNetworkNameListener(net) {
			public void updateNetworkName(CyNetwork n, String name) {
				updateTitle(n,name);
			}
		};
		eventHelper.addMicroListener(rsml, RowSetMicroListener.class, net.getCyRow());
		nameListeners.put(e.getNetwork(),rsml);
	}

	public void handleEvent(SetCurrentNetworkViewEvent e) {
		logger.debug("Got SetCurrentNetworkViewEvent.  View ID = "
				+ e.getNetworkView().getSUID());
		
		final long curr = e.getNetworkView().getModel().getSUID();
		
		if (currentNetId == null || curr != currentNetId.longValue())
			focusNetworkNode(curr);
	}

	public void handleEvent(SetCurrentNetworkEvent e) {
		logger.debug("Set current network "
				+ e.getNetwork().getSUID());
		long curr = e.getNetwork().getSUID();
		
		if (currentNetId == null || curr != currentNetId.longValue())
			focusNetworkNode(curr);
	}

	public void handleEvent(NetworkViewAboutToBeDestroyedEvent nde) {
		logger.debug("Network view about to be destroyed "
				+ nde.getNetworkView().getModel().getSUID());
		treeTable.getTree().updateUI();
	}

	public void handleEvent(NetworkViewAddedEvent nde) {
		logger.debug("Network view added "
				+ nde.getNetworkView().getModel().getSUID());
		
		// Set current network view to the new one.
		this.netmgr.setCurrentNetworkView(nde.getNetworkView().getModel().getSUID());
		
		treeTable.getTree().updateUI();
	}

	public void handleEvent(SelectedNodesEvent event) {
		logger.debug("Selected nodes ");
		treeTable.getTree().updateUI();
	}

	public void handleEvent(SelectedEdgesEvent event) {
		logger.debug("Selected edges ");
		treeTable.getTree().updateUI();
	}

	public void handleEvent(UnselectedNodesEvent event) {
		logger.debug("Unselected nodes ");
		treeTable.getTree().updateUI();
	}

	public void handleEvent(UnselectedEdgesEvent event) {
		logger.debug("Unselected edges ");
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
	private void addNetwork(Long network_id, Long parent_id) {
		// first see if it exists
		if (getNetworkNode(network_id) == null) {
			// logger.debug("NetworkPanel: addNetwork " + network_id);
			NetworkTreeNode dmtn = new NetworkTreeNode(netmgr.getNetwork(
					network_id).getCyRow().get("name", String.class), network_id);

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
			// logger.debug("addNetwork getNetworkTreeNode returned: " +
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
		// logger.debug("NetworkPanel: focus network node");
		DefaultMutableTreeNode node = getNetworkNode(network_id);

		if (node != null) {
			// logger.debug("NetworkPanel - setting currentNetId");
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
		// logger.debug("NetworkPanel: valueChanged - " +
		// e.getSource().getClass().getName());
		JTree mtree = treeTable.getTree();

		// sets the "current" network based on last node in the tree selected
		NetworkTreeNode node = (NetworkTreeNode) mtree
				.getLastSelectedPathComponent();
		if (node == null || node.getUserObject() == null) {
			// logger.debug("NetworkPanel: null node - returning");
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
						+ CyTableUtil.getNodesInState(cyNetwork,
								"selected", true).size() + ")";
			} else if (column == 2) {
				CyNetwork cyNetwork = netmgr
						.getNetwork(((NetworkTreeNode) node).getNetworkID());

				return ""
						+ cyNetwork.getEdgeCount()
						+ "("
						+ CyTableUtil.getEdgesInState(cyNetwork,
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
				setToolTipText(n.getCyRow().get("name", String.class));
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
	private class PopupListener extends MouseAdapter {
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
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}
	}
}
