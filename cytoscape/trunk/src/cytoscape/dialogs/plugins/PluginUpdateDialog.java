/**
 * 
 */
package cytoscape.dialogs.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
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
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.ManagerError;
import cytoscape.util.IndeterminateProgressBar;
import cytoscape.util.SwingWorker;


/**
 * @author skillcoy
 */
public class PluginUpdateDialog extends javax.swing.JDialog implements
		TreeSelectionListener
	{
	private static String title = "Update Plugins";
	private String updateMsg = "The following plugins will be updated the next time you restart Cytoscape:\n";
	private String errorMsg = "The following errors occurred while updating:\n";
	private boolean update = false;
	private boolean error = false;

	public PluginUpdateDialog(javax.swing.JDialog owner)
		{
		super(owner, title);
		setLocationRelativeTo(owner);
		initComponents();
		}

	public PluginUpdateDialog(javax.swing.JFrame owner)
		{
		super(owner, title);
		setLocationRelativeTo(owner);
		initComponents();
		}

	/**
	 * Enables the delete/install buttons when the correct leaf node is selected
	 */
	public void valueChanged(TreeSelectionEvent e)
		{
		pluginInfoPane.setContentType("text/html");
		String PluginText = "";
		javax.swing.tree.TreePath[] Paths = pluginsTree.getSelectionPaths();
		for (int i = 0; i < Paths.length; i++)
			{
			DefaultMutableTreeNode Node = (DefaultMutableTreeNode) Paths[i]
					.getLastPathComponent();
			if (Node.isLeaf())
				{
				DefaultMutableTreeNode Parent = (DefaultMutableTreeNode) Node
						.getParent();
				// PluginInfo Current = (PluginInfo) Parent.getUserObject();
				PluginInfo New = (PluginInfo) Node.getUserObject();
				// //PluginText += New.prettyOutput();
				pluginInfoPane.setText(New.htmlOutput());
				updatedSelectedButton.setEnabled(true);
				// PluginText += "\nUpdates " + Current.getName() + " "
				// + Current.getPluginVersion();
				// if (!(i == Paths.length - 1))
				// PluginText += "\n ------------------- \n\n";
				}
			else if (Node.getUserObject() != null
					&& Node.getUserObject().getClass().equals(PluginInfo.class))
				{
				PluginInfo NodeInfo = (PluginInfo) Node.getUserObject();
				pluginInfoPane.setText(NodeInfo.htmlOutput());
				// + NodeInfo.prettyOutput());
				}
			}
		// if (PluginText.length() > 0) {
		// pluginInfoPane.setText(PluginText);
		// updatedSelectedButton.setEnabled(true);
		// }
		}

	/**
	 * DOCUMENT ME
	 * 
	 * @param CategoryName
	 * @param Plugins
	 * @param Status
	 */
	public void addCategory(String CategoryName, PluginInfo CurrentPlugin,
			List<PluginInfo> NewPlugins)
		{
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		rootTreeNode.insert(Category, 0);
		DefaultMutableTreeNode CurrentPluginNode = new DefaultMutableTreeNode(
				CurrentPlugin);
		Category.insert(CurrentPluginNode, 0);
		// TODO ideally the user shouldn't pick more than one update for each
		// current plugin node...HOW??
		int i = 0;
		for (PluginInfo New : NewPlugins)
			{
			CurrentPluginNode.insert(new DefaultMutableTreeNode(New), i);
			i++;
			}
		Category.setUserObject(CategoryName + ": " + i);
		treeModel = new DefaultTreeModel(rootTreeNode);
		pluginsTree.setModel(treeModel);
		}

	private void updateHandler(java.awt.event.ActionEvent evt)
		{
		final List<PluginInfo[]> Updatable = new java.util.ArrayList<PluginInfo[]>();
		PluginManager Mgr = PluginManager.getPluginManager();
		javax.swing.tree.TreePath[] Paths = pluginsTree.getSelectionPaths();
		for (javax.swing.tree.TreePath Path : Paths)
			{
			DefaultMutableTreeNode Node = (DefaultMutableTreeNode) Path
					.getLastPathComponent();
			DefaultMutableTreeNode Parent = (DefaultMutableTreeNode) Node.getParent();
			final PluginInfo ParentInfo = (PluginInfo) Parent.getUserObject();
			final PluginInfo NewInfo = (PluginInfo) Node.getUserObject();
			Updatable.add( new PluginInfo[] {ParentInfo, NewInfo});

			if (NewInfo.getLicenseText() != null)
				{
				final LicenseDialog License = new LicenseDialog(this);
				License.addLicenseText(NewInfo.getLicenseText());
				License.addListenerToFinish(new java.awt.event.ActionListener()
					{
						public void actionPerformed(java.awt.event.ActionEvent evt)
							{
							License.dispose();
							SwingWorker worker = getWorker(Updatable,
									PluginUpdateDialog.this, new ThreadSync() );
							worker.start();
							}
					});
				License.setVisible(true);
				}
			else
				{
				SwingWorker worker = getWorker(Updatable,
						PluginUpdateDialog.this, new ThreadSync() );
				worker.start();
				}
			if (error)
				{
				JOptionPane.showMessageDialog(this, errorMsg, "Error Updating Plugins",
						JOptionPane.ERROR_MESSAGE);
				}
			if (update)
				{
				JOptionPane.showMessageDialog(this, updateMsg, "Plugins Updated",
						JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}

	private void updateAllHandler(java.awt.event.ActionEvent evt)
		{
		System.out.println("UPDATE ALL HANDLER START");
		if (JOptionPane
				.showConfirmDialog(
						this,
						"All plugins will be updated to the newest available version.\n"
								+ "If you wish to choose a different version please press \"No\" then\n"
								+ "choose each version and \"Update Selected Plugins\"",
						"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) { return; }

		java.util.Map<PluginInfo, PluginInfo> UpdateableObjs = new java.util.HashMap<PluginInfo, PluginInfo>();
		Enumeration Children = rootTreeNode.children();
		while (Children.hasMoreElements())
			{
			TreeNode Child = (TreeNode) Children.nextElement();
			List<TreeNode> LeafNodes = recursiveReadTree(Child);
			// this assumes there is more than 1...TODO deal with the single case
			PluginInfo LastObj = null;
			for (TreeNode Node : LeafNodes)
				{
				PluginInfo Obj = (PluginInfo) ((DefaultMutableTreeNode) Node)
						.getUserObject();
				if (LastObj != null)
					{
					PluginInfo NewObj = getNewerVersion(Obj, LastObj);
					PluginInfo ParentObj = (PluginInfo)((DefaultMutableTreeNode)Node.getParent()).getUserObject();
					
					// this should deal with an object having multiple available updates
					if (UpdateableObjs.containsKey(ParentObj))
						{
						System.out.println("alreayd have an update for " + ParentObj.getName());
						NewObj = getNewerVersion(NewObj, UpdateableObjs.get(ParentObj));
						System.out.println("New object version " + NewObj.getPluginVersion());
						UpdateableObjs.remove(ParentObj);
						UpdateableObjs.put(ParentObj, NewObj);
						}
					else
						{
						UpdateableObjs.put(ParentObj, NewObj);
						}
					}
				LastObj = Obj;
				}
			}

		final List<PluginInfo[]> ObjToUpdate = new java.util.ArrayList<PluginInfo[]>();

		// display licenses, set up the list of objects to be updated
		for (PluginInfo Original: UpdateableObjs.keySet())
			{
			final PluginInfo Old = Original;
			final PluginInfo New = UpdateableObjs.get(Old);
			System.out.println("Checking " + New.getName());
			if (New.getLicenseText() != null)
				{
				final LicenseDialog ld = new LicenseDialog();
				ld.addLicenseText(New.getLicenseText());
				ld.addListenerToFinish( new java.awt.event.ActionListener()
					{
					// ARRRRGGGG how do I get the info out of this thread before moving on??
					public void actionPerformed(java.awt.event.ActionEvent evt)
						{ // don't actually need to do anything I suppose
						System.out.println("Adding " + Old.getName() + " " + Old.getPluginVersion() + " " + New.getPluginVersion());
						ObjToUpdate.add( new PluginInfo[] {Old, New} );
						System.out.println("After add " + ObjToUpdate.size());
						ld.dispose();
						}
					});
				ld.setVisible(true);
				}
			else
				{
				ObjToUpdate.add( new PluginInfo[] {Old, New} );
				}
			}
// this is getting called before we actually know what licenses have been accepted
		System.out.println("Total to update " + ObjToUpdate.size());
		if (ObjToUpdate.size() > 0)
			{
			ThreadSync ts = new ThreadSync();
			SwingWorker worker = this.getWorker(ObjToUpdate, this, ts);
			worker.start();
			System.out.println("Before sync isDone " + ts.isThreadDone());
			System.out.println("After sync isDone " + ts.isThreadDone());
			
			System.out.println("Update: " + update + " Error: " + error);
			}
		
		if (update)
			{
			JOptionPane.showMessageDialog(this, updateMsg, "Plugin Updates",
					JOptionPane.INFORMATION_MESSAGE);
			}
		if (error)
			{
			JOptionPane.showMessageDialog(this, errorMsg, "Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}

	private PluginInfo getNewerVersion(PluginInfo New, PluginInfo Old)
		{
		PluginInfo NewerObj = Old;
		String[] CurrentVersion = Old.getPluginVersion().split("\\.");
		String[] NewVersion = New.getPluginVersion().split("\\.");
		System.out.println(New.getPluginVersion().toString() + ":"
				+ Old.getPluginVersion().toString());
		for (int i = 0; i < NewVersion.length; i++)
			{
			// if we're beyond the end of the current version array then it's a
			// new version
			if (CurrentVersion.length <= i)
				{
				NewerObj = New;
				break;
				}
			// if at any point the new version number is greater
			// then it's "new" ie. 1.2.1 > 1.1
			// whoops...what if they add a character in here?? TODO !!!!
			System.out.println(NewVersion[i] + ":" + CurrentVersion[i]);
			if (Integer.valueOf(NewVersion[i]) > Integer.valueOf(CurrentVersion[i])) NewerObj = New;
			}
		return NewerObj;
		}

	private List<TreeNode> recursiveReadTree(TreeNode Node)
		{
		List<TreeNode> LeafNodes = new java.util.ArrayList<TreeNode>();
		Enumeration Children = Node.children();
		while (Children.hasMoreElements())
			{
			TreeNode Child = (TreeNode) Children.nextElement();
			System.out.println(Child.toString());
			if (!Child.isLeaf())
				{
				List<TreeNode> DeeperNodes = recursiveReadTree(Child);
				LeafNodes.addAll(DeeperNodes);
				}
			else
				{
				LeafNodes.add(Child);
				}
			}
		return LeafNodes;
		}

	// sets up the swing stuff
	private void initComponents()
		{
		splitPane = new javax.swing.JSplitPane();
		treeScrollPane = new javax.swing.JScrollPane();
		pluginsTree = new javax.swing.JTree();
		infoScrollPane = new javax.swing.JScrollPane();
		pluginInfoPane = new javax.swing.JEditorPane();
		labelPane = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		buttonPanel = new javax.swing.JPanel();
		closeButton = new javax.swing.JButton();
		updateAllButton = new javax.swing.JButton();
		updatedSelectedButton = new javax.swing.JButton();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		splitPane.setDividerLocation(280);
		splitPane.setAutoscrolls(true);
		splitPane.setMinimumSize(new java.awt.Dimension(50, 100));
		pluginsTree.setRootVisible(false);
		rootTreeNode = new DefaultMutableTreeNode("Plugins");
		pluginsTree.addTreeSelectionListener(this);
		pluginsTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		treeScrollPane.setViewportView(pluginsTree);
		splitPane.setLeftComponent(treeScrollPane);
		infoScrollPane.setViewportView(pluginInfoPane);
		splitPane.setRightComponent(infoScrollPane);
		jLabel1
				.setText("The listed plugins are the updates available for plugins currently installed");
		org.jdesktop.layout.GroupLayout labelPaneLayout = new org.jdesktop.layout.GroupLayout(
				labelPane);
		labelPane.setLayout(labelPaneLayout);
		labelPaneLayout.setHorizontalGroup(labelPaneLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				labelPaneLayout.createSequentialGroup().add(jLabel1,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 600,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(
						234, Short.MAX_VALUE)));
		labelPaneLayout.setVerticalGroup(labelPaneLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				labelPaneLayout.createSequentialGroup().addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(
						jLabel1)));
		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
					{
					dispose();
					}
			});
		updateAllButton.setText("Update All Plugins");
		updateAllButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
					{
					updateAllHandler(evt);
					}
			});
		updatedSelectedButton.setText("Update Selected Plugins");
		updatedSelectedButton.setEnabled(false);
		updatedSelectedButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
					{
					updateHandler(evt);
					}
			});
		org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(
				buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(buttonPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				buttonPanelLayout.createSequentialGroup().addContainerGap(107,
						Short.MAX_VALUE).add(updateAllButton).add(42, 42, 42).add(
						updatedSelectedButton).add(29, 29, 29).add(closeButton).add(34, 34,
						34)));
		buttonPanelLayout.setVerticalGroup(buttonPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				buttonPanelLayout.createSequentialGroup().add(
						buttonPanelLayout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(closeButton,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 29,
								Short.MAX_VALUE).add(updatedSelectedButton)
								.add(updateAllButton)).addContainerGap()));
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(labelPane,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																layout
																		.createSequentialGroup()
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.TRAILING)
																						.add(
																								splitPane,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								681, Short.MAX_VALUE)
																						.add(
																								layout
																										.createSequentialGroup()
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED,
																												73, Short.MAX_VALUE)
																										.add(
																												buttonPanel,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
																		.addContainerGap()))));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				layout.createSequentialGroup().addContainerGap().add(labelPane,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE).add(
						splitPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 426,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(10, 10, 10)
						.add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(35, 35,
								35)));
		pack();
		}

	
	/*
	 * Creates the swing worker that displays progress bar during install
	 */
	private SwingWorker getWorker(List<PluginInfo[]> UpdateObjs,
			JDialog Owner, ThreadSync ts)
		{
		final List<PluginInfo[]> InfoObjsForUpdate = UpdateObjs;
		final JDialog Dialog = Owner;
		final ThreadSync tSync = ts;
		
		SwingWorker worker = new SwingWorker()
			{
				public Object construct()
					{
					java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
					final PluginManager Mgr = PluginManager.getPluginManager();
					final IndeterminateProgressBar InstallBar = new IndeterminateProgressBar(
							Dialog, "Updating Plugins", "Download in progress...");
					InstallBar.setLayout(new java.awt.GridBagLayout());
					JButton CancelInstall = new JButton("Cancel Download");
					CancelInstall.setSize(new java.awt.Dimension(81, 23));
					CancelInstall.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent E)
								{
								// TODO actually cancel the installation
								InstallBar.dispose();
								}
						});
					gridBagConstraints.gridy = 2;
					InstallBar.add(CancelInstall, gridBagConstraints);
					InstallBar.pack();
					InstallBar.setLocationRelativeTo(Dialog);
					InstallBar.setVisible(true);
					
					try
						{
						System.out.println("Calling Mgr.update");
						
						for (PluginInfo[] Objs: InfoObjsForUpdate)
							{
							Mgr.update(Objs[0], Objs[1]);
							}

						InstallBar.dispose();
						System.out.println("BEFORE Thread sync: " + tSync.isThreadDone());
						tSync.setThreadDone(true);
						System.out.println("AFTER Thread sync: " + tSync.isThreadDone());

//						JOptionPane.showMessageDialog(Dialog, "Plugin '" + New.getName()
//								+ " version " + New.getPluginVersion() + "' downloaded.\n"
//								+ "You will need to restart Cytoscape to use this plugin.",
//								"Download Complete", JOptionPane.PLAIN_MESSAGE);
						}
					catch (cytoscape.plugin.ManagerError E)
						{
						tSync.setFailed(true);
						InstallBar.dispose();
						PluginUpdateDialog.this.errorMsg += E.getMessage() + "\n";
						PluginUpdateDialog.this.error = true;
						System.out.println("ERROR UPDATING!! " + PluginUpdateDialog.this.error);
						E.printStackTrace();
						}
					return null; // return null object for construct
					}
			};
		return worker;
		}
	
	private class ThreadSync
		{
		private boolean done = false;
		private boolean failure = false;
		
		public void setThreadDone(boolean bool)
			{
			done = bool;
			}
		public boolean isThreadDone()
			{
			return done;
			}
		public void setFailed(boolean bool)
			{
			failure = bool;
			}
		public boolean threadFailed()
			{
			return failure;
			}
		}
	
	
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JButton closeButton;
	private javax.swing.JScrollPane infoScrollPane;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel labelPane;
	private javax.swing.JEditorPane pluginInfoPane;
	private javax.swing.JTree pluginsTree;
	private javax.swing.JSplitPane splitPane;
	private javax.swing.JScrollPane treeScrollPane;
	private javax.swing.JButton updateAllButton;
	private javax.swing.JButton updatedSelectedButton;
	private javax.swing.tree.MutableTreeNode rootTreeNode;
	private DefaultTreeModel treeModel;
	}
