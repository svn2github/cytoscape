/**
 *
 */
package cytoscape.dialogs;

import cytoscape.*;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * @author skillcoy
 *
 */
public class PluginListDialog extends JDialog implements TreeSelectionListener {
	/**
	* Creates a new PluginListDialog object.
	*/
	public PluginListDialog(JFrame owner) {
		super(owner, "Cytoscape Plugins");
		this.initComponents();
	}

	/**
	* Enables the delete button when a leaf node is selected
	*/
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) pluginsTree
		                                              .getLastSelectedPathComponent();

		if (node == null)
			return;

		if (node.isLeaf()) {
			PluginListDialog.this.deleteButton.setEnabled(true);
			displayInfo((PluginInfo) node.getUserObject());
		} else
			PluginListDialog.this.deleteButton.setEnabled(false);
	}

	// Shows the PluginInfo object as text in the editor panel
	private void displayInfo(PluginInfo obj) {
		pluginInfoPane.setText(obj.prettyOutput());
	}

	/**
	*
	* @param CategoryName
	*          String name of the category of plugins
	* @param Plugins
	*          List of PluginInfo objects within the given category name
	* @param index
	*          0 or 1
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

		treeModel = new DefaultTreeModel(rootTreeNode);
		pluginsTree.setModel(treeModel);
	}

	// new plugin event
	private void newPluginHandler(ActionEvent evt) {
		PluginManager Mgr = PluginManager.getPluginManager();
		PluginInstallDialog Install = new PluginInstallDialog(Cytoscape.getDesktop());

		Map<String, List<PluginInfo>> Plugins = Mgr.getPluginsByCategory(Mgr.inquire());
		Iterator<String> pI = Plugins.keySet().iterator();
		int index = 0;

		while (pI.hasNext()) {
			String Category = pI.next();
			Install.addCategory(Category, Plugins.get(Category), index);

			if (index <= 0)
				index++; // apparenlty just need 0/1
		}

		Install.pack();
		Install.setProjectName("cytoscape.org"); // !!!!!
		Install.setLocationRelativeTo(PluginListDialog.this.getContentPane());
		Install.setVisible(true);

		PluginListDialog.this.dispose();
	}

	// delete event
	private void deleteHandler(ActionEvent evt) {
		boolean delete = false;

		DefaultMutableTreeNode Node = (DefaultMutableTreeNode) pluginsTree
		                                                                                                                       .getLastSelectedPathComponent();

		System.out.println(Node.toString());

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
	/**
	 *  DOCUMENT ME!
	 *
	 * @param Node DOCUMENT ME!
	 */
	public void removeNode(DefaultMutableTreeNode Node) {
		MutableTreeNode parent = (MutableTreeNode) (Node.getParent());

		if (parent != null)
			treeModel.removeNodeFromParent(Node);

		if (!parent.children().hasMoreElements())
			treeModel.removeNodeFromParent(parent);
	}

	// initializes the dialog components
	private void initComponents() {
		splitPane = new JSplitPane();
		treeScrollPane = new JScrollPane();
		pluginsTree = new JTree();
		infoScrollPane = new JScrollPane();
		pluginInfoPane = new JEditorPane();
		labelPane = new JPanel();
		dialogLabel = new JLabel();
		buttonPanel = new JPanel();
		deleteButton = new JButton();
		newButton = new JButton();
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

		dialogLabel.setText("Installed Cytoscape Plugins By Category");

		org.jdesktop.layout.GroupLayout labelPaneLayout = new org.jdesktop.layout.GroupLayout(labelPane);
		labelPane.setLayout(labelPaneLayout);
		labelPaneLayout.setHorizontalGroup(labelPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                  .add(labelPaneLayout.createSequentialGroup()
		                                                                      .add(56, 56, 56)
		                                                                      .add(dialogLabel)
		                                                                      .addContainerGap(239,
		                                                                                       Short.MAX_VALUE)));
		labelPaneLayout.setVerticalGroup(labelPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                .add(dialogLabel,
		                                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                     27, Short.MAX_VALUE));

		deleteButton.setText("Delete");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					deleteHandler(evt);
				}
			});

		newButton.setText("Get New Plugins");
		newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					newPluginHandler(evt);
				}
			});

		closeButton.setText("Close");
		closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					PluginListDialog.this.dispose();
				}
			});

		org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           buttonPanelLayout.createSequentialGroup()
		                                                                            .add(54, 54, 54)
		                                                                            .add(deleteButton)
		                                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                                                             33,
		                                                                                             Short.MAX_VALUE)
		                                                                            .add(newButton)
		                                                                            .add(30, 30, 30)
		                                                                            .add(closeButton)
		                                                                            .addContainerGap()));
		buttonPanelLayout.setVerticalGroup(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                    .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                          .add(deleteButton)
		                                                                          .add(closeButton)
		                                                                          .add(newButton)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                     layout.createSequentialGroup().add(64, 64, 64)
		                                           .add(labelPane,
		                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                Short.MAX_VALUE).addContainerGap())
		                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                     layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
		                                                      .add(buttonPanel,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                      .add(splitPane,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           572, Short.MAX_VALUE))
		                                           .add(38, 38, 38)));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(labelPane,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .add(8, 8, 8)
		                                         .add(splitPane,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              412, Short.MAX_VALUE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          10, Short.MAX_VALUE)
		                                         .add(buttonPanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addContainerGap()));
		pack();
	}

	private JPanel buttonPanel;
	private JButton closeButton;
	private JButton deleteButton;
	private JLabel dialogLabel;
	private JScrollPane infoScrollPane;
	private JPanel labelPane;
	private JButton newButton;
	private JEditorPane pluginInfoPane;
	private JTree pluginsTree;
	private JSplitPane splitPane;
	private JScrollPane treeScrollPane;
	private MutableTreeNode rootTreeNode;
	private DefaultTreeModel treeModel;
}
