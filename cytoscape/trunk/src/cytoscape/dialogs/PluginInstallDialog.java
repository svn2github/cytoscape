package cytoscape.dialogs;

import cytoscape.Cytoscape;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.util.SwingWorker;
import cytoscape.util.IndeterminateProgressBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


public class PluginInstallDialog extends JDialog implements
		TreeSelectionListener
	{
	private JEditorPane InfoPanel;
	private JTree Tree;
	private JScrollPane TreeScroll;
	private DefaultMutableTreeNode RootNode;
	private JPanel ButtonPanel;
	private JButton Install;
	private JButton Cancel;
	protected static int InstallStopped = 0;

	public PluginInstallDialog() throws HeadlessException
		{
		super(Cytoscape.getDesktop(), "Install Plugins");
		this.setLocationRelativeTo(Cytoscape.getDesktop());
		this.initDialog();
		}


	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e)
		{ // displays the info for each plugin
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) Tree
				.getLastSelectedPathComponent();
		if (node == null) return;
		Object nodeInfo = node.getUserObject();
		if (node.isLeaf())
			{
			PluginInfo info = (PluginInfo) nodeInfo;
			displayInfo(info);
			}
		}

	private void displayInfo(PluginInfo obj)
		{ // TODO obviously this needs work
		InfoPanel.setText(obj.getName() + " \n " + obj.getDescription());
		}

	public void addCategory(String CategoryName, List<PluginInfo> Plugins,
			int index)
		{
		DefaultMutableTreeNode Category = new DefaultMutableTreeNode(CategoryName);
		RootNode.insert(Category, index);
		Iterator<PluginInfo> pI = Plugins.iterator();
		int i = 0;
		while (pI.hasNext())
			{
			PluginInfo CurrentPlugin = pI.next();
			Category.insert(new DefaultMutableTreeNode(CurrentPlugin), i);
			i++;
			}
		}

	private void initDialog()
		{
		setPreferredSize(new Dimension(700, 500));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());

		initTree();
		initSplit();
		initButtons();
		}

	/*
	 * Set up the tree/info pane widgets for plugin categories
	 */
	private void initTree()
		{
		GridBagConstraints gridBagConstraints;
		TreeScroll = new JScrollPane(Tree);
		Tree = new JTree();
		Tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Set up tree
		Tree.addTreeSelectionListener(this); // give tree a listener
		RootNode = new DefaultMutableTreeNode("Cytoscape Plugin Categories");
		DefaultTreeModel model = new DefaultTreeModel(RootNode);
		Tree.setModel(model);
		// Tree.setRootVisible(false);

		JLabel Label = new JLabel("Cytoscape Plugins By Category");
		Label.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 24));
		TreeScroll.add(Label);
		TreeScroll.setViewportView(Tree);
		InfoPanel = new JEditorPane();
		InfoPanel.setEditable(false);
		// label panel
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		getContentPane().add(new JLabel("Cytoscape Plugins"), gridBagConstraints);
		}

	/*
	 * Set up split pane for the tree and info panels
	 */
	private void initSplit()
		{
		GridBagConstraints gridBagConstraints;
		// set up split panel
		JSplitPane Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		Split.setLeftComponent(TreeScroll);
		Split.setRightComponent(InfoPanel);
		// add split panel
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(0, 10, 20, 10);
		getContentPane().add(Split, gridBagConstraints);
		}
	
	/*
	 * Sets up install/cancel buttons
	 */
	private void initButtons()
		{
		GridBagConstraints gridBagConstraints;
		// set up button panel
		ButtonPanel = new JPanel(new GridBagLayout());
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 0, 5);
		Install = new JButton("Install");
		Install.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
					{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) Tree
							.getLastSelectedPathComponent();
					System.out.println(node.toString());
					if (node == null)
						{
						// error
						System.err.println("Node was null, this is bad...");
						return;
						}
					Object nodeInfo = node.getUserObject();
					// TODO should have a dialog of some sort during the install
					if (node.isLeaf())
						{
						final PluginInfo info = (PluginInfo) nodeInfo;
						SwingWorker worker = getWorker(info, PluginInstallDialog.this);
						worker.start();
						}
					}
			});
		PluginInstallDialog.InstallStopped = 0;
		
		Install.setPreferredSize(new Dimension(81, 23));
		ButtonPanel.add(Install, gridBagConstraints);
		Cancel = new JButton("Close");
		Cancel.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
					{
					dispose();
					}
			});
		Cancel.setPreferredSize(new Dimension(81, 23));
		ButtonPanel.add(Cancel, gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		getContentPane().add(ButtonPanel, gridBagConstraints);
		}
	
	/*
	 * Creates the swing worker that displays progress bar during install
	 */
	private SwingWorker getWorker(PluginInfo Info, JDialog Owner)
		{
		final PluginInfo info = Info;
		final JDialog Dialog = Owner;
		SwingWorker worker = new SwingWorker()
			{
				public Object construct()
					{
					GridBagConstraints gridBagConstraints = new GridBagConstraints();
					final PluginManager Mgr = new PluginManager();
					final IndeterminateProgressBar InstallBar = new IndeterminateProgressBar(
							Dialog, "Installing Plugin", info.getName()
									+ " installation in progress...");
					InstallBar.setLayout(new GridBagLayout());
					JButton Cancel = new JButton("Cancel Install");
					Cancel.setSize(new Dimension(81, 23));
					Cancel.addActionListener(new ActionListener()
						{
						public void actionPerformed(ActionEvent E)
							{
							InstallBar.dispose();
							Mgr.abortInstall();
							}
						});
					gridBagConstraints.gridy = 2;
					InstallBar.add(Cancel, gridBagConstraints);
					InstallBar.pack();
					InstallBar.setLocationRelativeTo(Dialog); 
					InstallBar.setVisible(true);
					boolean installOk = Mgr.install(info);
					InstallBar.dispose();
					if (installOk)
						{
						JOptionPane.showMessageDialog(Dialog, 
								"Plugin '" + info.getName() + "' installed.  You will need to restart Cytoscape to use this plugin.", 
								"Installation Complete", JOptionPane.PLAIN_MESSAGE);
						}
					return null;
					}
			};
		return worker;
		}
	}
