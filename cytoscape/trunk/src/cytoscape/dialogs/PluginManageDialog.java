/**
 *
 */
package cytoscape.dialogs;

import cytoscape.Cytoscape;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;

import cytoscape.util.IndeterminateProgressBar;
import cytoscape.util.SwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;


/**
 * @author skillcoy
 */
public class PluginManageDialog extends javax.swing.JDialog implements TreeSelectionListener,
                                                                       ActionListener {
	public enum PluginInstallStatus {
		INSTALLED("Currently Installed"),
		AVAILABLE("Available for Install");

		private String typeText;

		private PluginInstallStatus(String type) {
			typeText = type;
		}

		public String toString() {
			return typeText;
		}
	}

	private int totalAvailable = 0;
	private int totalCurrent = 0;
	private String baseSiteLabel = "Available plugins from: ";

	public PluginManageDialog(javax.swing.JDialog owner) {
		super(owner, "Manage Plugins");
		setLocationRelativeTo(owner);
		initComponents();
	}

	public PluginManageDialog(javax.swing.JFrame owner) {
		super(owner, "Manage Plugins");
		setLocationRelativeTo(owner);
		initComponents();
	}

	// trying to listen to events in the Url dialog
	public void actionPerformed(ActionEvent evt) {
		System.out.println(evt.getSource().toString());
	}

	/**
	 * Enables the delete/install buttons when the correct leaf node is selected
	 */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode Node = (DefaultMutableTreeNode) 
			pluginsTree.getLastSelectedPathComponent();

		if (Node == null)
			return;
		
		if (Node.isLeaf()) {
			// display any object selected
			pluginInfoPane.setContentType("text/html");
			pluginInfoPane.setText(((PluginInfo) Node.getUserObject()).htmlOutput());
			
			if (Node.isNodeAncestor(this.installedNode)) {
				deleteButton.setEnabled(true);
				downloadButton.setEnabled(false);
			} else if (Node.isNodeAncestor(this.availableNode)) {
				deleteButton.setEnabled(false);
				downloadButton.setEnabled(true);
			}
		} else {
			deleteButton.setEnabled(false);
			downloadButton.setEnabled(false);
		}
	}

	/**
	 * Set the name of the site the available plugins are from.
	 *
	 * @param SiteName
	 */
	public void setSiteName(String SiteName) {
		siteLabel.setText(baseSiteLabel + " " + SiteName);
	}

	/**
	 * Call this when changing download sites to clear out the old available list
	 * in order to create a new one.
	 */
	public void switchDownloadSites() {
		MutableTreeNode[] DeletableNodes = new MutableTreeNode[availableNode.getChildCount()];

		// not sure why I had to do it like this, always missed the second node if I
		// tried to remove them while I looped over the children
		totalAvailable = 0;
		int i = 0;

		for (java.util.Enumeration<javax.swing.tree.TreeNode> E = availableNode.children();
		     E.hasMoreElements();) {
			MutableTreeNode Node = (MutableTreeNode) E.nextElement();
			DeletableNodes[i] = Node;
			i++;
		}

		for (MutableTreeNode Node : DeletableNodes) {
			System.out.println("Removing " + Node.toString());
			treeModel.removeNodeFromParent(Node);
		}
	}

	/**
	 * DOCUMENT ME
	 *
	 * @param CategoryName
	 * @param Plugins
	 * @param Status
	 */
	public void addCategory(String CategoryName, List<PluginInfo> Plugins,
	                        PluginInstallStatus Status) {
		switch (Status) {
			case INSTALLED:
				addInstalled(CategoryName, Plugins);
				installedNode.setUserObject(PluginInstallStatus.INSTALLED.toString() + 
						": " + totalCurrent);
				break;

			case AVAILABLE:
				addAvailable(CategoryName, Plugins);
				availableNode.setUserObject(PluginInstallStatus.AVAILABLE.toString() + 
						": " + totalAvailable);
				break;
		}
		
		
		treeModel = new DefaultTreeModel(rootTreeNode);
		pluginsTree.setModel(treeModel);
	}

	// add category to set of plugins availalbe for download
	private void addAvailable(String CategoryName, List<PluginInfo> Plugins) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		availableNode.insert(Category, 0);

		Iterator<PluginInfo> pI = Plugins.iterator();
		int i = 0;

		while (pI.hasNext()) {
			PluginInfo CurrentPlugin = pI.next();
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
			totalAvailable++;
		}
		Category.setUserObject(CategoryName + ": " + i);
	}

	// add category to the set of installed plugins
	private void addInstalled(String CategoryName, List<PluginInfo> Plugins) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		installedNode.insert(Category, 0);

		Iterator<PluginInfo> pI = Plugins.iterator();
		int i = 0;

		while (pI.hasNext()) {
			PluginInfo CurrentPlugin = pI.next();
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
			totalCurrent++;
		}
		Category.setUserObject(CategoryName + ": " + i);
	}

	// change site url
	private void changeSiteHandler(java.awt.event.ActionEvent evt) {
		PluginUrlDialog dialog = new PluginUrlDialog(this);
		dialog.setVisible(true);
	}

	// delete event
	private void deleteHandler(ActionEvent evt) {
		boolean delete = false;
		DefaultMutableTreeNode Node = (DefaultMutableTreeNode) pluginsTree
		                                                                                                                                                                                           .getLastSelectedPathComponent();

		if (Node == null) { // TODO error, throw an exception??
			System.err.println("Node was null, this is bad...");

			return;
		}

		PluginInfo NodeInfo = (PluginInfo) Node.getUserObject();
		System.out.println("Deleting " + NodeInfo.getName());

		String VerifyMsg = "";

		if (NodeInfo.getCategory().equalsIgnoreCase("core")) {
			VerifyMsg = "This is a 'core' plugin and other plugins may depend on it, "
			            + "are you sure you want to delete it?";
		} else {
			VerifyMsg = "Are you sure you want to delete the plugin '" + NodeInfo.getName() + "'?";
		}

		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
		                                                            VerifyMsg,
		                                                            "Verify Delete Plugin",
		                                                            JOptionPane.YES_NO_OPTION,
		                                                            JOptionPane.QUESTION_MESSAGE)) {
			PluginManager.getPluginManager().delete(NodeInfo);
			removeNode(Node);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
			                              "Plugin " + NodeInfo.getName()
			                              + " will be removed when you restart Cytoscape.",
			                              "Plugin Deletion", JOptionPane.PLAIN_MESSAGE);
		}
	}

	// removes the given node from the tree model, updates the tree for the user
	public void removeNode(DefaultMutableTreeNode Node) {
		MutableTreeNode parent = (MutableTreeNode) (Node.getParent());

		if (parent != null)
			treeModel.removeNodeFromParent(Node);

		if (!parent.children().hasMoreElements() && !parent.equals(availableNode)
		    && !parent.equals(installedNode))
			treeModel.removeNodeFromParent(parent);
	}

	// install new plugin
	private void installHandler(java.awt.event.ActionEvent evt) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) pluginsTree
		                                                                                                                                                                                                                                            .getLastSelectedPathComponent();
		System.out.println(node.toString());

		if (node == null) {
			// error
			System.err.println("Node was null, this is bad...");

			return;
		}

		Object nodeInfo = node.getUserObject();

		if (node.isLeaf()) {
			final PluginInfo info = (PluginInfo) nodeInfo;
			SwingWorker worker = getWorker(info, PluginManageDialog.this);
			worker.start();
		}
	}

	// close button
	private void closeHandler(java.awt.event.ActionEvent evt) {
		PluginManageDialog.this.dispose();
	}

	// initialize the dialog box & components
        // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
		splitPane = new javax.swing.JSplitPane();
		treeScrollPane = new javax.swing.JScrollPane();
		pluginsTree = new javax.swing.JTree();
		infoScrollPane = new javax.swing.JScrollPane();
		pluginInfoPane = new javax.swing.JEditorPane();
		labelPane = new javax.swing.JPanel();
		dialogLabel = new javax.swing.JLabel();
		siteLabel = new javax.swing.JLabel();
		buttonPanel = new javax.swing.JPanel();
		deleteButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();
		changeSiteButton = new javax.swing.JButton();
		downloadButton = new javax.swing.JButton();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		splitPane.setDividerLocation(265);
		splitPane.setAutoscrolls(true);
		splitPane.setMinimumSize(new java.awt.Dimension(50, 100));
		pluginsTree.setRootVisible(false);
		pluginsTree.addTreeSelectionListener(this);
		// might be nice to allow multiple selections with the two different nodes
		// but I'm just not sure how to manage that right now
		pluginsTree.getSelectionModel().setSelectionMode(
				javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
		rootTreeNode = new DefaultMutableTreeNode("Plugins");
		installedNode = new DefaultMutableTreeNode(PluginInstallStatus.INSTALLED.toString());
		availableNode = new DefaultMutableTreeNode(PluginInstallStatus.AVAILABLE.toString());
		rootTreeNode.insert(installedNode, 0);
		rootTreeNode.insert(availableNode, 1);
		treeScrollPane.setViewportView(pluginsTree);
		splitPane.setLeftComponent(treeScrollPane);
		infoScrollPane.setViewportView(pluginInfoPane);
		splitPane.setRightComponent(infoScrollPane);
		dialogLabel.setText("Manage Cytoscape Plugins");

		org.jdesktop.layout.GroupLayout labelPaneLayout = new org.jdesktop.layout.GroupLayout(labelPane);
		labelPane.setLayout(labelPaneLayout);
		
        labelPaneLayout.setHorizontalGroup(
            labelPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, labelPaneLayout.createSequentialGroup()
                .add(siteLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 447, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(234, Short.MAX_VALUE))
        );
        labelPaneLayout.setVerticalGroup(
            labelPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(labelPaneLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(siteLabel))
        );

		deleteButton.setText("Delete");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					deleteHandler(evt);
				}
			});
		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					closeHandler(evt);
				}
			});
		changeSiteButton.setText("Change Download Site");
		changeSiteButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					changeSiteHandler(evt);
				}
			});
		downloadButton.setText("Download");
		downloadButton.setEnabled(false);
		downloadButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					installHandler(evt);
				}
			});

		org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           buttonPanelLayout.createSequentialGroup()
		                                                                            .addContainerGap(36,
		                                                                                             Short.MAX_VALUE)
		                                                                            .add(changeSiteButton)
		                                                                            .add(28, 28, 28)
		                                                                            .add(downloadButton)
		                                                                            .add(18, 18, 18)
		                                                                            .add(deleteButton)
		                                                                            .add(30, 30, 30)
		                                                                            .add(closeButton)
		                                                                            .addContainerGap()));
		buttonPanelLayout.setVerticalGroup(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                    .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                          .add(closeButton)
		                                                                          .add(deleteButton)
		                                                                          .add(downloadButton)
		                                                                          .add(changeSiteButton)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(splitPane,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      659, Short.MAX_VALUE)
		                                                                 .add(38, 38, 38))
		                                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(buttonPanel,
		                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                 .addContainerGap())
		                                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(labelPane,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      Short.MAX_VALUE)
		                                                                 .add(151, 151, 151)))));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().add(28, 28, 28)
		                                         .add(labelPane,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .add(9, 9, 9)
		                                         .add(splitPane,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              398, Short.MAX_VALUE).add(10, 10, 10)
		                                         .add(buttonPanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addContainerGap()));
		pack();
	} // </editor-fold>                        

	/*
	 * Creates the swing worker that displays progress bar during install
	 */
	private SwingWorker getWorker(PluginInfo Info, JDialog Owner) {
		final PluginInfo info = Info;
		final JDialog Dialog = Owner;
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
				final PluginManager Mgr = PluginManager.getPluginManager();
				final IndeterminateProgressBar InstallBar = new IndeterminateProgressBar(Dialog,
				                                                                         "Downloading Plugin",
				                                                                         info
				                                                                                                                                                                                                                                                                                                                                                                                                                            .getName()
				                                                                         + " download in progress...");
				InstallBar.setLayout(new java.awt.GridBagLayout());

				JButton CancelInstall = new JButton("Cancel Download");
				CancelInstall.setSize(new java.awt.Dimension(81, 23));
				CancelInstall.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent E) {
							InstallBar.dispose();

							// TODO abort download
							// Mgr.abortInstall();
						}
					});
				gridBagConstraints.gridy = 2;
				InstallBar.add(CancelInstall, gridBagConstraints);
				InstallBar.pack();
				InstallBar.setLocationRelativeTo(Dialog);
				InstallBar.setVisible(true);

				try {
					Mgr.download(info);
					InstallBar.dispose();
					JOptionPane.showMessageDialog(Dialog,
					                              "Plugin '" + info.getName() + "' downloaded.\n"
					                              + "You will need to restart Cytoscape to use this plugin.",
					                              "Download Complete", JOptionPane.PLAIN_MESSAGE);
				} catch (cytoscape.plugin.ManagerError E) {
					InstallBar.dispose();
					JOptionPane.showMessageDialog(Dialog, E.getMessage(), "Error",
					                              JOptionPane.ERROR_MESSAGE);
					E.printStackTrace();
				}

				return null; // return null object for construct
			}
		};

		return worker;
	}

	// Variables declaration - do not modify
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JButton downloadButton;
	private javax.swing.JButton changeSiteButton;
	private javax.swing.JLabel dialogLabel;
	private javax.swing.JScrollPane infoScrollPane;
	private javax.swing.JPanel labelPane;
	private javax.swing.JEditorPane pluginInfoPane;
	private javax.swing.JTree pluginsTree;
	private javax.swing.JLabel siteLabel;
	private javax.swing.JSplitPane splitPane;
	private javax.swing.JScrollPane treeScrollPane;
	private MutableTreeNode rootTreeNode;
	private MutableTreeNode installedNode;
	private MutableTreeNode availableNode;
	private DefaultTreeModel treeModel;
}
