/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.dialogs;

import cytoscape.Cytoscape;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;

import cytoscape.util.IndeterminateProgressBar;
import cytoscape.util.SwingWorker;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;


/**
 *
 */
public class PluginInstallDialog extends JDialog implements TreeSelectionListener {
	protected static int InstallStopped = 0;

	/**
	* Creates a new PluginInstallDialog object.
	*
	* DOCUMENT ME!
	*/
	public PluginInstallDialog(JFrame owner) {
		super(owner, "Install Plugins");
		initComponents();
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) { // displays the info for each plugin

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) pluginsTree
		                                                                                    .getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();

		if (node.isLeaf()) {
			PluginInfo info = (PluginInfo) nodeInfo;
			displayInfo(info);
		}
	}

	// display PluginInfo object text in editor box
	private void displayInfo(PluginInfo obj) {
		pluginInfoPane.setText(obj.prettyOutput());
	}

	/**
	* DOCUMENT ME!
	*
	* @param CategoryName
	*          DOCUMENT ME!
	* @param Plugins
	*          DOCUMENT ME!
	* @param index
	*          DOCUMENT ME!
	*/
	public void addCategory(String CategoryName, List<PluginInfo> Plugins, int index) {
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		rootTreeNode.insert(Category, index);

		Iterator<PluginInfo> pI = Plugins.iterator();
		int i = 0;

		while (pI.hasNext()) {
			PluginInfo CurrentPlugin = pI.next();
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
		}

		final DefaultTreeModel model = new DefaultTreeModel(rootTreeNode);
		pluginsTree.setModel(model);
	}

	/**
	* Adds the 'Project' string to the label
	* "Avaialble Cytoscape Plugins From ..."
	*
	* @param Project
	*/
	public void setProjectName(String Project) {
		dialogLabel.setText(dialogLabel.getText() + " From " + Project);
	}

	// change site url
	private void changeSiteHandler(java.awt.event.ActionEvent evt) {
		PluginUrlDialog dialog = new PluginUrlDialog(Cytoscape.getDesktop());
		dialog.setVisible(true);
	}

	// close the dialog box, pop the currently installed list open
	private void closeHandler(java.awt.event.ActionEvent evt) {
		PluginInstallDialog.this.dispose();

		PluginManager Mgr = PluginManager.getPluginManager();
		java.util.Map<String, List<PluginInfo>> CurrentPlugins = Mgr.getPluginsByCategory(Mgr
		                                                                                                                                                       .getInstalledPlugins());

		PluginListDialog dialog = new PluginListDialog(Cytoscape.getDesktop());
		int index = 0;

		for (String Category : CurrentPlugins.keySet()) {
			dialog.addCategory(Category, CurrentPlugins.get(Category), index);

			if (index == 0)
				index++;
		}

		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.setVisible(true);
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

		// TODO should have a dialog of some sort during the install
		if (node.isLeaf()) {
			final PluginInfo info = (PluginInfo) nodeInfo;
			SwingWorker worker = getWorker(info, PluginInstallDialog.this);
			worker.start();
		}
	}

	private void initComponents() {
		splitPane = new JSplitPane();
		treeScrollPane = new JScrollPane();
		pluginsTree = new JTree();
		infoScrollPane = new JScrollPane();
		pluginInfoPane = new JEditorPane();
		labelPane = new JPanel();
		dialogLabel = new JLabel();
		buttonPanel = new JPanel();
		installButton = new JButton();
		changeSiteButton = new JButton();
		closeButton = new JButton();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		splitPane.setDividerLocation(200);
		splitPane.setAutoscrolls(true);
		splitPane.setMinimumSize(new java.awt.Dimension(50, 100));
		pluginsTree.setRootVisible(false);
		rootTreeNode = new DefaultMutableTreeNode("Categories");
		pluginsTree.addTreeSelectionListener(this);

		treeScrollPane.setViewportView(pluginsTree);

		splitPane.setLeftComponent(treeScrollPane);

		infoScrollPane.setViewportView(pluginInfoPane);

		splitPane.setRightComponent(infoScrollPane);

		dialogLabel.setText("Available Cytoscape Plugins");

		GroupLayout labelPaneLayout = new GroupLayout(labelPane);
		labelPane.setLayout(labelPaneLayout);
		labelPaneLayout.setHorizontalGroup(labelPaneLayout.createParallelGroup(GroupLayout.LEADING)
		                                                  .add(labelPaneLayout.createSequentialGroup()
		                                                                      .add(56, 56, 56)
		                                                                      .add(dialogLabel)
		                                                                      .addContainerGap(274,
		                                                                                       Short.MAX_VALUE)));
		labelPaneLayout.setVerticalGroup(labelPaneLayout.createParallelGroup(GroupLayout.LEADING)
		                                                .add(dialogLabel, GroupLayout.DEFAULT_SIZE,
		                                                     27, Short.MAX_VALUE));

		installButton.setText("Install");
		installButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					installHandler(evt);
				}
			});

		changeSiteButton.setText("Choose Download Site");
		changeSiteButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					changeSiteHandler(evt);
				}
			});

		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					closeHandler(evt);
				}
			});

		GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(buttonPanelLayout.createParallelGroup(GroupLayout.LEADING)
		                                                      .add(GroupLayout.TRAILING,
		                                                           buttonPanelLayout.createSequentialGroup()
		                                                                            .add(54, 54, 54)
		                                                                            .add(installButton)
		                                                                            .addPreferredGap(LayoutStyle.RELATED,
		                                                                                             33,
		                                                                                             Short.MAX_VALUE)
		                                                                            .add(changeSiteButton)
		                                                                            .add(30, 30, 30)
		                                                                            .add(closeButton)
		                                                                            .addContainerGap()));
		buttonPanelLayout.setVerticalGroup(buttonPanelLayout.createParallelGroup(GroupLayout.LEADING)
		                                                    .add(buttonPanelLayout.createParallelGroup(GroupLayout.BASELINE)
		                                                                          .add(installButton)
		                                                                          .add(closeButton)
		                                                                          .add(changeSiteButton)));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(GroupLayout.LEADING)
		                                                      .add(GroupLayout.TRAILING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(layout.createParallelGroup(GroupLayout.TRAILING)
		                                                                            .add(buttonPanel,
		                                                                                 GroupLayout.PREFERRED_SIZE,
		                                                                                 GroupLayout.DEFAULT_SIZE,
		                                                                                 GroupLayout.PREFERRED_SIZE)
		                                                                            .add(splitPane,
		                                                                                 GroupLayout.DEFAULT_SIZE,
		                                                                                 572,
		                                                                                 Short.MAX_VALUE))
		                                                                 .add(38, 38, 38))
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(labelPane,
		                                                                      GroupLayout.DEFAULT_SIZE,
		                                                                      GroupLayout.DEFAULT_SIZE,
		                                                                      Short.MAX_VALUE)
		                                                                 .add(64, 64, 64)))));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(labelPane, GroupLayout.PREFERRED_SIZE,
		                                              GroupLayout.DEFAULT_SIZE,
		                                              GroupLayout.PREFERRED_SIZE).add(8, 8, 8)
		                                         .add(splitPane, GroupLayout.DEFAULT_SIZE, 412,
		                                              Short.MAX_VALUE)
		                                         .addPreferredGap(LayoutStyle.RELATED, 10,
		                                                          Short.MAX_VALUE)
		                                         .add(buttonPanel, GroupLayout.PREFERRED_SIZE,
		                                              GroupLayout.DEFAULT_SIZE,
		                                              GroupLayout.PREFERRED_SIZE).addContainerGap()));
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

	private JPanel buttonPanel;
	private JButton changeSiteButton;
	private JButton closeButton;
	private JLabel dialogLabel;
	private JScrollPane infoScrollPane;
	private JButton installButton;
	private JPanel labelPane;
	private JEditorPane pluginInfoPane;
	private JTree pluginsTree;
	private JSplitPane splitPane;
	private JScrollPane treeScrollPane;
	private MutableTreeNode rootTreeNode;
}
