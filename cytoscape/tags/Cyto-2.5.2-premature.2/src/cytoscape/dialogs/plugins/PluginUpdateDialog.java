package cytoscape.dialogs.plugins;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import cytoscape.Cytoscape;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

// TODO clean out the tree for each updated plugin

/**
 * @author skillcoy
 */
public class PluginUpdateDialog extends JDialog implements
		TreeSelectionListener {
	private static String title = "Update Plugins";

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
		if (Paths == null) {
			updateSelectedButton.setEnabled(false);
			return;
		}

		if (Paths.length == 0) {
			updateSelectedButton.setEnabled(false);
		}

		for (int i = 0; i < Paths.length; i++) {
			TreeNode LastSelectedNode = (TreeNode) Paths[i]
					.getLastPathComponent();

			if (LastSelectedNode.isLeaf()) {
				PluginInfo New = (PluginInfo) LastSelectedNode.getObject();

				infoTextPane.setText(New.htmlOutput());
				updateSelectedButton.setEnabled(true);
			} else if (LastSelectedNode.getObject() != null
					&& LastSelectedNode.getObject().getClass().equals(
							PluginInfo.class)) {
				PluginInfo NodeInfo = (PluginInfo) LastSelectedNode.getObject();
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
		TreeNode Category = new TreeNode(CategoryName, true);
		treeModel.addNodeToParent(rootTreeNode, Category);

		TreeNode CurrentPluginNode = new TreeNode(CurrentPlugin, true);
		treeModel.addNodeToParent(Category, CurrentPluginNode);

		for (PluginInfo New : NewPlugins) {
			treeModel.addNodeToParent(CurrentPluginNode, new TreeNode(New));
		}
	}

	/**
	 * 
	 * @param Msg
	 */
	public void setMessage(String Msg) {
		msgLabel.setText(Msg);
	}

	// update for single selections
	private void updateSelectedButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		Set<TreeNode> AllParents = new java.util.HashSet<TreeNode>();

		javax.swing.tree.TreePath[] Paths = pluginTree.getSelectionPaths();
		Map<PluginInfo, PluginInfo> UpdatableObjs = new java.util.HashMap<PluginInfo, PluginInfo>();

		// first make sure each node only has one option picked
		String Msg = "Please choose just one update option for the following plugins:\n";
		boolean TooManyChildren = false;
		for (javax.swing.tree.TreePath Path : Paths) {
			TreeNode Node = (TreeNode) Path.getLastPathComponent();
			TreeNode Parent = Node.getParent();

			if (AllParents.contains(Parent)) {
				Msg += Parent.toString() + "\n";
				TooManyChildren = true;
			} else {
				AllParents.add(Parent);
				UpdatableObjs.put(Parent.getObject(), Node.getObject());
				treeModel.removeNodeFromParent(Node);
			}
		}
		if (TooManyChildren) {
			JOptionPane.showMessageDialog(this, Msg,
					"Warning: Too many updates selected",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// run the update
		List<PluginInfo[]> Updatable = getUpdateList(UpdatableObjs);
		createUpdateTask(Updatable);

		setMessage("Update will complete when Cytoscape is restarted.");
	}

	// close dialog
	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		dispose();
	}

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

		Map<PluginInfo, PluginInfo> UpdateableObjs = new java.util.HashMap<PluginInfo, PluginInfo>();
		Set<TreeNode> RemovableNodes = new java.util.HashSet<TreeNode>();

		List<TreeNode> Leaves = recursiveReadTree(rootTreeNode);
		for (TreeNode Node : Leaves) {
			TreeNode Parent = Node.getParent();

			if (Parent.getChildCount() > 1) { // multiple possible updates
				PluginInfo LastInfoObj = null;
				for (TreeNode Sib : Parent.getChildren()) {
					PluginInfo CurrentInfoObj = Sib.getObject();
					if (LastInfoObj == null
							|| LastInfoObj.isNewerPluginVersion(CurrentInfoObj))
						UpdateableObjs.put(Parent.getObject(), CurrentInfoObj);
					LastInfoObj = CurrentInfoObj;
				}
			} else {
				UpdateableObjs.put(Parent.getObject(), Node.getObject());
			}
			RemovableNodes.add(Parent);
		}
		List<PluginInfo[]> ObjToUpdate = getUpdateList(UpdateableObjs);
		createUpdateTask(ObjToUpdate);

		for (TreeNode Node : RemovableNodes) {
			treeModel.removeNodeFromParent(Node);
		}

		setMessage("Update will complete when Cytoscape is restarted.");
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

			// display only if always required at update
			if (New.isLicenseRequired() && New.getLicenseText() != null) {
				final LicenseDialog ld = new LicenseDialog();
				ld.setPluginName(New.getName() + " v" + New.getPluginVersion());
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

	private List<TreeNode> recursiveReadTree(TreeNode Node) {
		List<TreeNode> LeafNodes = new java.util.ArrayList<TreeNode>();
		for (TreeNode Child : Node.getChildren()) {
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
		rootTreeNode = new TreeNode("Updatable Plugins", true);

		pluginTree.addTreeSelectionListener(this);
		pluginTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		treeModel = new ManagerModel(rootTreeNode);
		pluginTree.setModel(treeModel);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
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
        updateLabel.setText("Listed are updates available for currently installed plugins");
        updateLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        updateAllButton.setText("Update All");
        updateAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateAllButtonActionPerformed(evt);
            }
        });

        updateSelectedButton.setText("Update Selected");
        updateSelectedButton.addActionListener(new java.awt.event.ActionListener() {
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
        msgLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(43, 43, 43)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, updateLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, msgLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE))
                .add(41, 41, 41))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(272, Short.MAX_VALUE)
                .add(updateAllButton)
                .add(18, 18, 18)
                .add(updateSelectedButton)
                .add(22, 22, 22)
                .add(closeButton)
                .add(50, 50, 50))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(updateLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(msgLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(updateAllButton)
                    .add(updateSelectedButton)
                    .add(closeButton))
                .addContainerGap())
        );
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
				} catch (cytoscape.plugin.ManagerException me) {
					JOptionPane.showMessageDialog(PluginUpdateDialog.this, me
							.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (cytoscape.plugin.WebstartException we) {
					we.printStackTrace();
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
	private TreeNode rootTreeNode;

	private ManagerModel treeModel;
}
