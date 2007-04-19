package cytoscape.dialogs.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import cytoscape.Cytoscape;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.ManagerError;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.IndeterminateProgressBar;

/**
 * @author skillcoy
 */
public class PluginUpdateDialog extends JDialog implements
		TreeSelectionListener {
	private static String title = "Update Plugins";

	private String updateMsg = "The following plugins will be updated the next time you restart Cytoscape:\n";

	private String errorMsg = "The following errors occurred while updating:\n";

	public PluginUpdateDialog(javax.swing.JDialog owner) {
		super(owner, title);
		setLocationRelativeTo(owner);
		initComponents();
		initTree();
	}

	public PluginUpdateDialog(javax.swing.JFrame owner) {
		super(owner, title);
		setLocationRelativeTo(owner);
		initComponents();
		initTree();
	}

	/**
	 * Enables the delete/install buttons when the correct leaf node is selected
	 */
	public void valueChanged(TreeSelectionEvent e) {
		infoTextPane.setContentType("text/html");
		javax.swing.tree.TreePath[] Paths = pluginTree.getSelectionPaths();

		for (int i = 0; i < Paths.length; i++) {
			DefaultMutableTreeNode LastSelectedNode = (DefaultMutableTreeNode) Paths[i]
					.getLastPathComponent();

			if (LastSelectedNode.isLeaf()) {
				PluginInfo New = (PluginInfo) LastSelectedNode.getUserObject();
				infoTextPane.setText(New.htmlOutput());
				updateSelectedButton.setEnabled(true);
			} else if (LastSelectedNode.getUserObject() != null
					&& LastSelectedNode.getUserObject().getClass().equals(
							PluginInfo.class)) {
				PluginInfo NodeInfo = (PluginInfo) LastSelectedNode
						.getUserObject();
				infoTextPane.setText(NodeInfo.htmlOutput());
			}
		}
	}

	/**
	 * DOCUMENT ME
	 * 
	 * @param CategoryName
	 * @param Plugins
	 * @param Status
	 */
	public void addCategory(String CategoryName, PluginInfo CurrentPlugin,
			List<PluginInfo> NewPlugins) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(
				CategoryName);
		rootTreeNode.insert(Category, 0);
		DefaultMutableTreeNode CurrentPluginNode = new DefaultMutableTreeNode(
				CurrentPlugin);

		int i = 0;
		Category.insert(CurrentPluginNode, i);
		for (PluginInfo New : NewPlugins) {
			CurrentPluginNode.insert(new DefaultMutableTreeNode(New), i);
			i++;
		}
		Category.setUserObject(CategoryName + ": " + i);

		treeModel = new DefaultTreeModel(rootTreeNode);
		pluginTree.setModel(treeModel);
	}

	// update for single selections
	private void updateSelectedButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		java.util.Set<DefaultMutableTreeNode> AllParents = new java.util.HashSet<DefaultMutableTreeNode>();
		javax.swing.tree.TreePath[] Paths = pluginTree.getSelectionPaths();

		// first make sure each node only has one option picked
		String Msg = "Please choose just one update option for the following plugins:\n";
		boolean TooManyChildren = false;
		for (javax.swing.tree.TreePath Path : Paths) {
			DefaultMutableTreeNode Node = (DefaultMutableTreeNode) Path
					.getLastPathComponent();
			DefaultMutableTreeNode Parent = (DefaultMutableTreeNode) Node
					.getParent();

			if (AllParents.contains(Parent)) {
				Msg += Parent.toString() + "\n";
				TooManyChildren = true;
			} else {
				AllParents.add(Parent);
			}
		}
		if (TooManyChildren) {
			JOptionPane.showMessageDialog(this, Msg,
					"Warning: Too many updates selected",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// update!
		java.util.Map<PluginInfo, PluginInfo> UpdatableObjs = new java.util.HashMap<PluginInfo, PluginInfo>();
		for (javax.swing.tree.TreePath Path : Paths) {
			DefaultMutableTreeNode Node = (DefaultMutableTreeNode) Path
					.getLastPathComponent();
			DefaultMutableTreeNode Parent = (DefaultMutableTreeNode) Node
					.getParent();

			UpdatableObjs.put((PluginInfo) Parent.getUserObject(),
					(PluginInfo) Node.getUserObject());
		}

		// run the update
		List<PluginInfo[]> Updatable = getUpdateList(UpdatableObjs);
		createUpdateTask(Updatable);
	}

	// close dialog
	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		dispose();
	}

	// updates all plugins to the newest version available
	private void updateAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (JOptionPane
				.showConfirmDialog(
						this,
						"All plugins will be updated to the newest available version.\n"
								+ "If you wish to choose a different version please press \"No\" then\n"
								+ "choose each version and \"Update Selected Plugins\"",
						"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
			return;
		}

		java.util.Map<PluginInfo, PluginInfo> UpdateableObjs = new java.util.HashMap<PluginInfo, PluginInfo>();
		Enumeration Children = rootTreeNode.children();
		while (Children.hasMoreElements()) {
			TreeNode Child = (TreeNode) Children.nextElement();
			List<TreeNode> LeafNodes = recursiveReadTree(Child);

			PluginInfo LastObj = null;
			for (TreeNode Node : LeafNodes) {
				PluginInfo Obj = (PluginInfo) ((DefaultMutableTreeNode) Node)
						.getUserObject();
				if (LastObj != null) {
					PluginInfo NewObj = getNewerVersion(Obj, LastObj);
					PluginInfo ParentObj = (PluginInfo) ((DefaultMutableTreeNode) Node
							.getParent()).getUserObject();

					// this should deal with an object having multiple available
					// updates
					if (UpdateableObjs.containsKey(ParentObj)) {
						NewObj = getNewerVersion(NewObj, UpdateableObjs
								.get(ParentObj));
						UpdateableObjs.remove(ParentObj);
						UpdateableObjs.put(ParentObj, NewObj);
					} else {
						UpdateableObjs.put(ParentObj, NewObj);
					}
				}
				LastObj = Obj;
			}
		}

		List<PluginInfo[]> ObjToUpdate = getUpdateList(UpdateableObjs);
		createUpdateTask(ObjToUpdate);
	}

	/**
	 * Show licenses if required, add all plugins to be updated to list.
	 * 
	 * @param PotentialUpdates
	 *            Key: Current plugin, Value: New plugin
	 * @return List <PluginInfo[]{Old, New}>
	 */
	private List<PluginInfo[]> getUpdateList(
			java.util.Map<PluginInfo, PluginInfo> PotentialUpdates) {
		final List<PluginInfo[]> Updates = new java.util.ArrayList<PluginInfo[]>();

		// display licenses, set up the list of objects to be updated
		for (PluginInfo Original : PotentialUpdates.keySet()) {
			final PluginInfo Old = Original;
			final PluginInfo New = PotentialUpdates.get(Old);
			if (New.getLicenseText() != null) {
				final LicenseDialog ld = new LicenseDialog();
				ld.addLicenseText(New.getLicenseText());
				ld.addListenerToFinish(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						Updates.add(new PluginInfo[] { Old, New });
						ld.dispose();
					}
				});
				ld.setVisible(true);
			} else {
				Updates.add(new PluginInfo[] { Old, New });
			}
		}
		return Updates;
	}

	private PluginInfo getNewerVersion(PluginInfo New, PluginInfo Old) {
		PluginInfo NewerObj = Old;
		String[] CurrentVersion = Old.getPluginVersion().split("\\.");
		String[] NewVersion = New.getPluginVersion().split("\\.");
		System.out.println(New.getPluginVersion().toString() + ":"
				+ Old.getPluginVersion().toString());
		for (int i = 0; i < NewVersion.length; i++) {
			// if we're beyond the end of the current version array then it's a
			// new version
			if (CurrentVersion.length <= i) {
				NewerObj = New;
				break;
			}
			// if at any point the new version number is greater
			// then it's "new" ie. 1.2.1 > 1.1
			// whoops...what if they add a character in here?? TODO !!!!
			System.out.println(NewVersion[i] + ":" + CurrentVersion[i]);
			if (Integer.valueOf(NewVersion[i]) > Integer
					.valueOf(CurrentVersion[i]))
				NewerObj = New;
		}
		return NewerObj;
	}

	private List<TreeNode> recursiveReadTree(TreeNode Node) {
		List<TreeNode> LeafNodes = new java.util.ArrayList<TreeNode>();
		Enumeration Children = Node.children();
		while (Children.hasMoreElements()) {
			TreeNode Child = (TreeNode) Children.nextElement();
			System.out.println(Child.toString());
			if (!Child.isLeaf()) {
				List<TreeNode> DeeperNodes = recursiveReadTree(Child);
				LeafNodes.addAll(DeeperNodes);
			} else {
				LeafNodes.add(Child);
			}
		}
		return LeafNodes;
	}

	// sets up the swing stuff

	private void initTree() {
		pluginTree.setRootVisible(false);
		rootTreeNode = new DefaultMutableTreeNode("Plugins");
		pluginTree.addTreeSelectionListener(this);
		pluginTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

	}

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jSplitPane1 = new javax.swing.JSplitPane();
		infoScrollPane = new javax.swing.JScrollPane();
		infoTextPane = new javax.swing.JEditorPane();
		treeScrollPane = new javax.swing.JScrollPane();
		pluginTree = new javax.swing.JTree();
		updateLabel = new javax.swing.JLabel();
		updateAllButton = new javax.swing.JButton();
		updateSelectedButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();
		msgLabel = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jSplitPane1.setDividerLocation(250);
		infoScrollPane.setViewportView(infoTextPane);

		jSplitPane1.setRightComponent(infoScrollPane);

		treeScrollPane.setViewportView(pluginTree);

		jSplitPane1.setLeftComponent(treeScrollPane);

		updateLabel.setLabelFor(jSplitPane1);
		updateLabel
				.setText("Listed are updates available for currently installed plugins");
		updateLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

		updateAllButton.setText("Update All");
		updateAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateAllButtonActionPerformed(evt);
			}
		});

		updateSelectedButton.setText("Update Selected");
		updateSelectedButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						updateSelectedButtonActionPerformed(evt);
					}
				});

		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});

		msgLabel.setForeground(new java.awt.Color(204, 0, 51));
		msgLabel.setText("Messages/errors here....");
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
																msgLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																updateLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																jSplitPane1,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																574,
																Short.MAX_VALUE))
										.addContainerGap(41, Short.MAX_VALUE))
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								layout.createSequentialGroup().addContainerGap(
										272, Short.MAX_VALUE).add(
										updateAllButton).add(18, 18, 18).add(
										updateSelectedButton).add(22, 22, 22)
										.add(closeButton).add(50, 50, 50)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												updateLabel,
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
														.add(updateAllButton)
														.add(
																updateSelectedButton)
														.add(closeButton))
										.addContainerGap()));
		pack();
	}// </editor-fold>

	private void createUpdateTask(List<PluginInfo[]> UpdateObjs) {
		// Create Task
		Task task = new PluginUpdateTask(UpdateObjs);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.displayCancelButton(true);
		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	private class PluginUpdateTask implements cytoscape.task.Task {
		private cytoscape.task.TaskMonitor taskMonitor;

		private List<PluginInfo[]> toUpdate;

		public PluginUpdateTask(List<PluginInfo[]> Updates) {
			toUpdate = Updates;
		}

		public void run() {
			if (taskMonitor == null) {
				throw new IllegalStateException("Task Monitor is not set.");
			}
			taskMonitor.setStatus("Updating...");
			taskMonitor.setPercentCompleted(-1);

			PluginManager Mgr = PluginManager.getPluginManager();

			for (PluginInfo[] UpdatePair : toUpdate) {
				taskMonitor.setStatus("Updating " + UpdatePair[0].getName()
						+ " to version " + UpdatePair[1].getPluginVersion());
				try {
					Mgr.update(UpdatePair[0], UpdatePair[1], taskMonitor);
				} catch (java.io.IOException ioe) {
					taskMonitor.setException(ioe, "Failed to download "
							+ UpdatePair[1].getName());
				} catch (cytoscape.plugin.ManagerError me) {
					JOptionPane.showMessageDialog(PluginUpdateDialog.this, me
							.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			taskMonitor.setPercentCompleted(100);
		}

		public void halt() {
			// not haltable
		}

		public void setTaskMonitor(TaskMonitor monitor)
				throws IllegalThreadStateException {
			this.taskMonitor = monitor;
		}

		public String getTitle() {
			return "Updating Plugins";
		}

	}

	private IndeterminateProgressBar getProgressBar() {
		final IndeterminateProgressBar InstallBar = new IndeterminateProgressBar(
				this, "Updating Plugins", "Download in progress...");

		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		InstallBar.setLayout(new java.awt.GridBagLayout());
		JButton CancelInstall = new JButton("Cancel Download");
		CancelInstall.setSize(new java.awt.Dimension(81, 23));
		CancelInstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent E) {
				// TODO actually cancel the installation
				InstallBar.dispose();
			}
		});
		gridBagConstraints.gridy = 2;
		InstallBar.add(CancelInstall, gridBagConstraints);
		InstallBar.pack();
		InstallBar.setLocationRelativeTo(this);

		return InstallBar;
	}

	// runnable object for running the update
	private class UpdateRunnable implements Runnable {
		private boolean done = false;

		private boolean error = false;

		private boolean update = false;

		private List<PluginInfo[]> updateObjs;

		public UpdateRunnable(List<PluginInfo[]> UpdateObjs) {
			updateObjs = UpdateObjs;
		}

		public void run() {
			try {
				PluginManager Mgr = PluginManager.getPluginManager();
				for (PluginInfo[] ToUpdate : updateObjs) {
					try {
						Mgr.update(ToUpdate[0], ToUpdate[1]);
						PluginUpdateDialog.this.updateMsg += ToUpdate[1] + "\n";
						update = true;

					} catch (java.io.IOException ioe) {
						PluginUpdateDialog.this.errorMsg += "Failed to download update to "
								+ ToUpdate[0].getName() + "\n";
						error = true;
					} catch (ManagerError E) {
						PluginUpdateDialog.this.errorMsg += E.getMessage()
								+ "\n";
						error = true;
					}
				}
			} finally {
				done = true;
			}
		}

		public boolean hasError() {
			return error;
		}

		public boolean updateOk() {
			return update;
		}

		public boolean isDone() {
			return done;
		}

	}

	// Variables declaration - do not modify
	private javax.swing.JButton closeButton;

	private javax.swing.JScrollPane infoScrollPane;

	private javax.swing.JEditorPane infoTextPane;

	private javax.swing.JSplitPane jSplitPane1;

	private javax.swing.JLabel msgLabel;

	private javax.swing.JTree pluginTree;

	private javax.swing.JScrollPane treeScrollPane;

	private javax.swing.JButton updateAllButton;

	private javax.swing.JLabel updateLabel;

	private javax.swing.JButton updateSelectedButton;

	// End of variables declaration
	private javax.swing.tree.MutableTreeNode rootTreeNode;

	private DefaultTreeModel treeModel;
}
