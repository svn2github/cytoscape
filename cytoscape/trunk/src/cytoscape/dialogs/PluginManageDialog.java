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
 *
 */
public class PluginManageDialog extends javax.swing.JDialog implements TreeSelectionListener {
	public enum PluginStatus {
		INSTALLED("Currently Installed"),
		AVAILABLE("Available for Install");

		private String typeText;

		private PluginStatus(String type) {
			typeText = type;
		}

		public String toString() {
			return typeText;
		}
	}
	public PluginManageDialog(javax.swing.JFrame owner) {
		super(owner, "Manage Plugins");
		setLocationRelativeTo(owner);
		initComponents();
	}

	/**
	* Enables the delete/install buttons when the correct leaf node is selected
	*/
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode Node = (DefaultMutableTreeNode) pluginsTree
		                                                     .getLastSelectedPathComponent();

		if (Node == null)
			return;

		if (Node.isLeaf()) {
			// display any object selected
			pluginInfoPane.setText(((PluginInfo) Node.getUserObject()).prettyOutput());

			if (Node.isNodeAncestor(this.installedNode)) {
				deleteButton.setEnabled(true);
				installButton.setEnabled(false);
			} else if (Node.isNodeAncestor(this.availableNode)) {
				deleteButton.setEnabled(false);
				installButton.setEnabled(true);
			}
		} else {
			deleteButton.setEnabled(false);
			installButton.setEnabled(false);
		}
	}

	public void setSiteName(String SiteName) {
		siteLabel.setText(siteLabel.getText() + " " + SiteName);
	}

	/**
	 * DOCUMENT ME
	 * @param CategoryName
	 * @param Plugins
	 * @param Status
	 */
	public void addCategory(String CategoryName, List<PluginInfo> Plugins, PluginStatus Status) {
		switch (Status) {
			case INSTALLED:
				addInstalled(CategoryName, Plugins);

				break;

			case AVAILABLE:
				addAvailable(CategoryName, Plugins);

				break;
		}

		treeModel = new DefaultTreeModel(rootTreeNode);
		pluginsTree.setModel(treeModel);
	}

	// add category to set of plugins availalbe for download
	private void addAvailable(String CategoryName, List<PluginInfo> Plugins) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		this.availableNode.insert(Category, 0);

		Iterator<PluginInfo> pI = Plugins.iterator();
		int i = 0;

		while (pI.hasNext()) {
			PluginInfo CurrentPlugin = pI.next();
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
		}
	}

	// add category to the set of installed plugins
	private void addInstalled(String CategoryName, List<PluginInfo> Plugins) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		this.installedNode.insert(Category, 0);

		Iterator<PluginInfo> pI = Plugins.iterator();
		int i = 0;

		while (pI.hasNext()) {
			PluginInfo CurrentPlugin = pI.next();
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
		}
	}

	// change site url
	private void changeSiteHandler(java.awt.event.ActionEvent evt) {
		PluginUrlDialog dialog = new PluginUrlDialog(Cytoscape.getDesktop());
		dialog.setVisible(true);
	}

	// delete event
	private void deleteHandler(ActionEvent evt) {
		boolean delete = false;

		DefaultMutableTreeNode Node = (DefaultMutableTreeNode) pluginsTree
		                                                                                                                                               .getLastSelectedPathComponent();

		if (Node == null) {
			// TODO error, throw an exception??
			System.err.println("Node was null, this is bad...");

			return;
		}

		PluginInfo NodeInfo = (PluginInfo) Node.getUserObject();

		System.out.println("Deleting " + NodeInfo.getName());

		if (NodeInfo.getCategory().equalsIgnoreCase("core")) {
			String Msg = "This is a 'core' plugin and other plugins may depend on it, are you sure you want to delete it?";

			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
			                                                            Msg, "Core Plugin Removal",
			                                                            JOptionPane.YES_NO_OPTION,
			                                                            JOptionPane.QUESTION_MESSAGE))
				delete = true;
		} else
			delete = true;

		if (delete) {
			if (PluginManager.getPluginManager().delete(NodeInfo)) {
				removeNode(Node);
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
				                              "Plugin " + NodeInfo.getName()
				                              + " successfully deleted.", "Plugin Deletion",
				                              JOptionPane.PLAIN_MESSAGE);
				repaint();
			} else
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
				                              "Failed to delete plugin " + NodeInfo.getName()
				                              + ".  You may need to manually delete it.");
		}
	}

	// removes the given node from the tree model, updates the tree for the user
	public void removeNode(DefaultMutableTreeNode Node) {
		MutableTreeNode parent = (MutableTreeNode) (Node.getParent());

		if (parent != null)
			treeModel.removeNodeFromParent(Node);

		if (!parent.children().hasMoreElements())
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
		installButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		splitPane.setDividerLocation(225);
		splitPane.setAutoscrolls(true);
		splitPane.setMinimumSize(new java.awt.Dimension(50, 100));

		pluginsTree.setRootVisible(false);
		pluginsTree.addTreeSelectionListener(this);

		rootTreeNode = new DefaultMutableTreeNode("Plugins");
		installedNode = new DefaultMutableTreeNode(PluginStatus.INSTALLED.toString());
		availableNode = new DefaultMutableTreeNode(PluginStatus.AVAILABLE.toString());
		rootTreeNode.insert(installedNode, 0);
		rootTreeNode.insert(availableNode, 1);

		treeScrollPane.setViewportView(pluginsTree);

		splitPane.setLeftComponent(treeScrollPane);

		infoScrollPane.setViewportView(pluginInfoPane);

		splitPane.setRightComponent(infoScrollPane);

		dialogLabel.setText("Manage Cytoscape Plugins");

		siteLabel.setText("Available plugins from: ");

		org.jdesktop.layout.GroupLayout labelPaneLayout = new org.jdesktop.layout.GroupLayout(labelPane);
		labelPane.setLayout(labelPaneLayout);
		labelPaneLayout.setHorizontalGroup(labelPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                  .add(labelPaneLayout.createSequentialGroup()
		                                                                      .addContainerGap()
		                                                                      .add(dialogLabel,
		                                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                           191,
		                                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                      .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                                                       63,
		                                                                                       Short.MAX_VALUE)
		                                                                      .add(siteLabel)
		                                                                      .add(126, 126, 126)));
		labelPaneLayout.setVerticalGroup(labelPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                .add(dialogLabel,
		                                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                     50, Short.MAX_VALUE)
		                                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                     siteLabel,
		                                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                     50, Short.MAX_VALUE));

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

		installButton.setText("Install");
		installButton.setEnabled(false);
		installButton.addActionListener(new java.awt.event.ActionListener() {
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
		                                                                            .add(installButton)
		                                                                            .add(18, 18, 18)
		                                                                            .add(deleteButton)
		                                                                            .add(30, 30, 30)
		                                                                            .add(closeButton)
		                                                                            .addContainerGap()));
		buttonPanelLayout.setVerticalGroup(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                    .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                          .add(closeButton)
		                                                                          .add(deleteButton)
		                                                                          .add(installButton)
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
	}

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
				                                                                         "Installing Plugin",
				                                                                         info
				                                                                                                                                                                                                                                                                                                                                                                                                  .getName()
				                                                                         + " installation in progress...");

				InstallBar.setLayout(new java.awt.GridBagLayout());

				JButton CancelInstall = new JButton("Cancel Install");
				CancelInstall.setSize(new java.awt.Dimension(81, 23));
				CancelInstall.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent E) {
							InstallBar.dispose();
							Mgr.abortInstall();
						}
					});
				gridBagConstraints.gridy = 2;
				InstallBar.add(CancelInstall, gridBagConstraints);
				InstallBar.pack();
				InstallBar.setLocationRelativeTo(Dialog);
				InstallBar.setVisible(true);

				boolean installOk = Mgr.install(info);
				InstallBar.dispose();

				if (installOk) {
					JOptionPane.showMessageDialog(Dialog,
					                              "Plugin '" + info.getName()
					                              + "' installed.  You will need to restart Cytoscape to use this plugin.",
					                              "Installation Complete", JOptionPane.PLAIN_MESSAGE);
				}

				return null;
			}
		};

		return worker;
	}

	// Variables declaration - do not modify                     
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JButton installButton;
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
