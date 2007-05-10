/**
 *
 */
package cytoscape.dialogs.plugins;

import cytoscape.Cytoscape;

import cytoscape.plugin.ManagerUtil;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.PluginTracker.PluginStatus;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 * @author skillcoy
 */
public class PluginManageDialog extends javax.swing.JDialog implements
		TreeSelectionListener, ActionListener {

	public enum PluginInstallStatus {
		INSTALLED("Currently Installed"), AVAILABLE("Available for Install");
		private String typeText;

		private PluginInstallStatus(String type) {
			typeText = type;
		}

		public String toString() {
			return typeText;
		}
	}
	
	public enum CommonError {
		
		NOXML("ERROR: Failed to read XML file "), BADXML("ERROR: XML file may be incorrectly formatted, unable to read ");
		
		private String errorText;
		
		private CommonError(String error) {
			errorText = error;
		}
		
		public String toString() {
			return errorText;
		}
	}
	

	private String baseSiteLabel = "Plugins available for download from: ";

	public PluginManageDialog(javax.swing.JDialog owner) {
		super(owner, "Manage Plugins");
		setLocationRelativeTo(owner);
		initComponents();
		initTree();
		TrackTotals.zero();
	}

	public PluginManageDialog(javax.swing.JFrame owner) {
		super(owner, "Manage Plugins");
		setLocationRelativeTo(owner);
		initComponents();
		initTree();
		TrackTotals.zero();
	}

	// trying to listen to events in the Url dialog
	public void actionPerformed(ActionEvent evt) {
		System.out.println("URL DIALOG: " + evt.getSource().toString());
	}

	/**
	 * Enables the delete/install buttons when the correct leaf node is selected
	 */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode Node = (DefaultMutableTreeNode) pluginTree
				.getLastSelectedPathComponent();
		if (Node == null) {
			return;
		}
		
		if (Node.isLeaf()) {
			// display any object selected
			infoTextPane.setContentType("text/html");
			infoTextPane.setText(((PluginInfo) Node.getUserObject())
					.htmlOutput());
			if (Node.isNodeAncestor(installedNode)) {
				deleteButton.setEnabled(true);
				installButton.setEnabled(false);
			} else if (Node.isNodeAncestor(availableNode)) {
				deleteButton.setEnabled(false);
				installButton.setEnabled(true);
			}
		} else {
			deleteButton.setEnabled(false);
			installButton.setEnabled(false);
		}
		if (PluginManager.usingWebstartManager()) {
			deleteButton.setEnabled(false);
		}		

	}

	/**
	 * Sets a message to be shown to the user regarding the plugin management actions.
	 * @param Msg
	 */
	public void setMessage(String Msg) {
		msgLabel.setText(Msg);

	}

	/**
	 * Set the name of the site the available plugins are from.
	 * 
	 * @param SiteName
	 */
	public void setSiteName(String SiteName) {
		availablePluginsLabel.setText(baseSiteLabel + " " + SiteName);
	}

	/**
	 * Call this when changing download sites to clear out the old available
	 * list in order to create a new one.
	 */
	public void switchDownloadSites() {
		MutableTreeNode[] DeletableNodes = new MutableTreeNode[availableNode.getChildCount()];

		// not sure why I had to do it like this, always missed the second node
		// if I tried to remove them while I looped over the children
		TrackTotals.zero(TrackTotals.Totals.AVAIL);
		//availableNode.setUserObject( PluginInstallStatus.AVAILABLE.toString() + " : " + TrackTotals.getAvailable() );
		availableNode.setUserObject( new CategoryObject(PluginInstallStatus.AVAILABLE.toString(), TrackTotals.getAvailable()));
		int i = 0;
		for (java.util.Enumeration<javax.swing.tree.TreeNode> E = availableNode.children(); E.hasMoreElements();) {
			MutableTreeNode Node = (MutableTreeNode) E.nextElement();
			DeletableNodes[i] = Node;
			i++;
		}
		
		for (MutableTreeNode Node : DeletableNodes) {
			treeModel.removeNodeFromParent(Node);
		}
	}

	/**
	 * Adds a category and it's list of plugins to the appropriate tree
	 * (based on Status) in the dialog.
	 * 
	 * @param CategoryName
	 * 		String category for this list of plugins
	 * @param Plugins
	 * 		List of PluginInfo objects to be shown in the given category
	 * @param Status
	 * 		PluginInstallStatus (currently installed or available for install)
	 */
	public void addCategory(String CategoryName, List<PluginInfo> Plugins,
			PluginInstallStatus Status) {
		switch (Status) {
		case INSTALLED:
			addCategory(CategoryName, Plugins, installedNode, TrackTotals.Totals.CUR);
			//installedNode.setUserObject(PluginInstallStatus.INSTALLED.toString()					+ ": " + TrackTotals.getCurrent());
			installedNode.setUserObject( new CategoryObject(PluginInstallStatus.INSTALLED.toString(), TrackTotals.getCurrent()));
			
			break;
		case AVAILABLE:
			System.out.println("Adding new available category: " + CategoryName);
			addCategory(CategoryName, Plugins, availableNode, TrackTotals.Totals.AVAIL);
			//availableNode.setUserObject(PluginInstallStatus.AVAILABLE.toString() + ": " + TrackTotals.getAvailable());
			availableNode.setUserObject( new CategoryObject(PluginInstallStatus.AVAILABLE.toString(), TrackTotals.getAvailable()));
			break;
		}
		treeModel = new DefaultTreeModel(rootTreeNode);
		pluginTree.setModel(treeModel);
	}

	// add category to the set of plugins under given node
	private void addCategory(String CategoryName, List<PluginInfo> Plugins, MutableTreeNode node, TrackTotals.Totals type) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		int i = 0;
		node.insert(Category, i);
		for (PluginInfo CurrentPlugin: Plugins) {
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
			TrackTotals.add(type);
		}
		//Category.setUserObject(CategoryName + ": " + i);
		Category.setUserObject( new CategoryObject(CategoryName, i) );
	}
	
	// change site url
	private void changeSiteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		PluginUrlDialog dialog = new PluginUrlDialog(this);
		dialog.setVisible(true);
	}

	// delete event
	private void deleteButtonActionPerformed(ActionEvent evt) {
		DefaultMutableTreeNode Node = (DefaultMutableTreeNode) pluginTree
				.getLastSelectedPathComponent();
		if (Node == null) { 
			return;
		}
		PluginInfo NodeInfo = (PluginInfo) Node.getUserObject();
		String ChangeMsg = "Changes will not take effect until you have restarted Cytoscape.";
		String VerifyMsg = "";
		if (NodeInfo.getCategory().equalsIgnoreCase("core")) {
			VerifyMsg = "This is a 'core' plugin and other plugins may depend on it, "
					+ "are you sure you want to delete it?\n" + ChangeMsg;
		} else {
			VerifyMsg = "Are you sure you want to delete the plugin '"
					+ NodeInfo.getName() + "'?\n" + ChangeMsg;
		}
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, VerifyMsg, "Verify Delete Plugin",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
			try {
				PluginManager.getPluginManager().delete(NodeInfo);
				removeNode(Node);
			} catch (cytoscape.plugin.WebstartException we) {
				we.printStackTrace();
			}
		}
	}

	private void removeNode(DefaultMutableTreeNode Node) {
		MutableTreeNode parent = (MutableTreeNode) (Node.getParent());
		if (parent != null) {
			treeModel.removeNodeFromParent(Node);
		}
		if (!parent.children().hasMoreElements()
				&& !parent.equals(availableNode)
				&& !parent.equals(installedNode)) {
			treeModel.removeNodeFromParent(parent);
		}
	}

	// install new plugin
	private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) pluginTree
				.getLastSelectedPathComponent();
		if (node == null) {
			// error
			return;
		}
		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			final PluginInfo info = (PluginInfo) nodeInfo;

			if (info.getLicenseText() != null) {
				final LicenseDialog License = new LicenseDialog(this);
				License.setPluginName(info.getName() + " v" + info.getPluginVersion());
				License.addLicenseText(info.getLicenseText());
				License.addListenerToFinish(new ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						License.dispose();
						createDownloadTask(info, node);
						loadPlugin(info);

					}
				});
				License.setVisible(true);
			} else {
				createDownloadTask(info, node);
				loadPlugin(info);
			}
		}
	}

	private void updateCurrent(PluginInfo info) {
		boolean categoryMatched = false;
		java.util.Enumeration Current = installedNode.children();
		
		while (Current.hasMoreElements()) {
			DefaultMutableTreeNode Child = (DefaultMutableTreeNode)Current.nextElement();
			CategoryObject CatObj = (CategoryObject) Child.getUserObject();

			if (CatObj.getTitle().equals(info.getCategory())) {
				Child.insert(new DefaultMutableTreeNode(info), Child.getChildCount()+1);
				Child.setUserObject( new CategoryObject(CatObj.getTitle(), CatObj.getTotal()+1));
				categoryMatched = true;
			}
		}
		
		if (!categoryMatched) {
			List<PluginInfo> NewPlugin = new java.util.ArrayList<PluginInfo>();
			NewPlugin.add(info);
			addCategory(info.getCategory(), NewPlugin, PluginInstallStatus.INSTALLED);
		}
		
		treeModel = new DefaultTreeModel(rootTreeNode);
		pluginTree.setModel(treeModel);
	}
	
	
	private void loadPlugin(PluginInfo info) {
		PluginManager Mgr = PluginManager.getPluginManager();
		try {
			Mgr.install(info); 
			Mgr.loadPlugin(info);
			updateCurrent(info);
		} catch (ClassNotFoundException cne) {
			cne.printStackTrace();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();				
		}

	}
	
	// close button
	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		dispose();
	}

	private void initTree() {
		pluginTree.setRootVisible(false);
		pluginTree.addTreeSelectionListener(this);

		pluginTree.getSelectionModel().setSelectionMode(
				javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
		rootTreeNode = new DefaultMutableTreeNode("Plugins");
		installedNode = new DefaultMutableTreeNode(
				PluginInstallStatus.INSTALLED.toString());
		availableNode = new DefaultMutableTreeNode(
				PluginInstallStatus.AVAILABLE.toString());
		rootTreeNode.insert(installedNode, 0);
		rootTreeNode.insert(availableNode, 1);
	}

	// initialize the dialog box & components
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jSplitPane1 = new javax.swing.JSplitPane();
		infoScrollPane = new javax.swing.JScrollPane();
		infoTextPane = new javax.swing.JEditorPane();
		treeScrollPane = new javax.swing.JScrollPane();
		pluginTree = new javax.swing.JTree();
		availablePluginsLabel = new javax.swing.JLabel();
		changeSiteButton = new javax.swing.JButton();
		installButton = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();
		msgLabel = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jSplitPane1.setDividerLocation(250);
		infoScrollPane.setViewportView(infoTextPane);

		jSplitPane1.setRightComponent(infoScrollPane);

		treeScrollPane.setViewportView(pluginTree);

		jSplitPane1.setLeftComponent(treeScrollPane);

		availablePluginsLabel.setLabelFor(jSplitPane1);
		availablePluginsLabel
				.setVerticalAlignment(javax.swing.SwingConstants.TOP);

		changeSiteButton.setText("Change Download Site");
		changeSiteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				changeSiteButtonActionPerformed(evt);
			}
		});

		installButton.setText("Install");
		installButton.setEnabled(false);
		installButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				downloadButtonActionPerformed(evt);
			}
		});

		deleteButton.setText("Delete");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});

		msgLabel.setForeground(new java.awt.Color(204, 0, 51));
		msgLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.add(43, 43, 43)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING,
																false)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																msgLabel)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																availablePluginsLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				changeSiteButton)
																		.add(
																				18,
																				18,
																				18)
																		.add(
																				installButton)
																		.add(
																				18,
																				18,
																				18)
																		.add(
																				deleteButton)
																		.add(
																				22,
																				22,
																				22)
																		.add(
																				closeButton))
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																jSplitPane1,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																574,
																Short.MAX_VALUE))
										.addContainerGap(41, Short.MAX_VALUE)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												availablePluginsLabel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												32,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												jSplitPane1,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												324,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												msgLabel,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												33, Short.MAX_VALUE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(changeSiteButton)
														.add(installButton)
														.add(deleteButton).add(
																closeButton))
										.addContainerGap()));
		pack();
	}// </editor-fold>
	
	// to count/show the totals for installed and available plugins on the JTree
	private static class TrackTotals {
		private static int totalCurrent = 0;
		private static int totalAvailable = 0;

		private enum Totals {
			AVAIL("available"), CUR("current");
			
			private String type;
			
			private Totals(String totalType) {
				type = totalType;
			}
		}
		
		public static void add(Totals t) {
			switch(t)
			{
			case AVAIL:
				totalAvailable++;
				break;
			case CUR:
				totalCurrent++;
				break;
			};
		}
		
		public static void zero() {
			zero(Totals.AVAIL);
			zero(Totals.CUR);
		}
		
		public static void zero(Totals t) {
			switch(t)
			{
			case AVAIL: 
				totalAvailable = 0;
				break;
			case CUR: 
				totalCurrent = 0;
				break;
			};
		}
		
		public static int getCurrent()  {
			return totalCurrent;
		}
		
		public static int getAvailable() {
			return totalAvailable;
		}
	
	}
	
	/* --- create the tasks and task monitors to show the user what's going on during download/install --- */

	private void createDownloadTask(PluginInfo obj, DefaultMutableTreeNode node) {
		// Create Task
		cytoscape.task.Task task = new PluginDownloadTask(obj, node);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner( Cytoscape.getDesktop() );
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.displayCancelButton(true);
		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
	
	private class PluginDownloadTask implements cytoscape.task.Task {
		private cytoscape.task.TaskMonitor taskMonitor;

		private PluginInfo pluginInfo;
		private DefaultMutableTreeNode node;
		
		public PluginDownloadTask(PluginInfo Info, DefaultMutableTreeNode Node) {
			pluginInfo = Info;
			node = Node;
		}

		public void run() {
			if (taskMonitor == null) {
				throw new IllegalStateException("Task Monitor is not set.");
			}
			taskMonitor.setStatus("Installing " + pluginInfo.getName() + " v" + pluginInfo.getPluginVersion());
			taskMonitor.setPercentCompleted(-1);

			PluginManager Mgr = PluginManager.getPluginManager(); 
			try {
				 Mgr.download(pluginInfo, taskMonitor);
				 taskMonitor.setStatus(pluginInfo.getName() + " v" + pluginInfo.getPluginVersion() + " complete.");
				 PluginManageDialog.this.setMessage(pluginInfo.getName() + " download complete. Please restart Cytoscape in order to use new plugins.");
				 cleanTree(node);
				 taskMonitor.setStatus(pluginInfo.getName() + " v" + pluginInfo.getPluginVersion() + " loading...");
			} catch (java.io.IOException ioe) { 
				taskMonitor.setException(ioe, "Failed to download " + pluginInfo.getName() + " from " +
						pluginInfo.getUrl()); 
			} catch (cytoscape.plugin.ManagerException me) {
				taskMonitor.setException(me, me.getMessage());
			} finally {
				taskMonitor.setPercentCompleted(100);
			}
		}

		public void halt() {
			// not haltable
		}

		public void setTaskMonitor(TaskMonitor monitor)
				throws IllegalThreadStateException {
			this.taskMonitor = monitor;
		}

		public String getTitle() {
			return "Installing Cytoscape Plugin '" + pluginInfo.getName() + "'";
		}
	

		 // get rid of any sibling nodes that are duplicate (different versions) of the just downloaded one
		private void cleanTree(DefaultMutableTreeNode node) {
			PluginInfo info = (PluginInfo) node.getUserObject();  
			List<DefaultMutableTreeNode> RemovableNodes = new java.util.ArrayList<DefaultMutableTreeNode>();

			for(int i=0; i<node.getParent().getChildCount(); i++) {
				DefaultMutableTreeNode Child = (DefaultMutableTreeNode) node.getParent().getChildAt(i);
				PluginInfo childInfo = (PluginInfo) Child.getUserObject();
				
				if (childInfo.getID().equals(info.getID()) && childInfo.getName().equals(info.getName())) {
					RemovableNodes.add( Child );
				}
			}
			
			for (DefaultMutableTreeNode treeNode: RemovableNodes) {
				removeNode(treeNode);
			}
			
		}
	}

	private class CategoryObject {
		private String title;
		private int total;
		public CategoryObject(String Title, int Total) {
			title = Title;
			total = Total;
		}
		
		public String getTitle() {
			return title;
		}
		
		public int getTotal() {
			return total;
		}
		
		public String toString() {
			return title + ": " + total;  
		}
	}
	
	// Variables declaration - do not modify
	private javax.swing.JLabel availablePluginsLabel;
	private javax.swing.JButton changeSiteButton;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JButton installButton;
	private javax.swing.JScrollPane infoScrollPane;
	private javax.swing.JEditorPane infoTextPane;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JLabel msgLabel;
	private javax.swing.JTree pluginTree;
	private javax.swing.JScrollPane treeScrollPane;

	// End of variables declaration
	private MutableTreeNode rootTreeNode;
	private MutableTreeNode installedNode;
	private MutableTreeNode availableNode;
	private DefaultTreeModel treeModel;
}
