package cytoscape.plugin.cheminfo;

import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.NodeContextMenuListener;

public class ChemInfoPlugin extends CytoscapePlugin implements
		NodeContextMenuListener, PropertyChangeListener, ActionListener {

	private static String DEPICT = "DEPICT";

	private JMenu menu = null;
	private NodeView nodeView = null;
	private Properties systemProps = null;

	public ChemInfoPlugin() {
		try {
			// Set ourselves up to listen for new networks
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
					.addPropertyChangeListener(
							CytoscapeDesktop.NETWORK_VIEW_CREATED, this);

			((DGraphView) Cytoscape.getCurrentNetworkView())
					.addNodeContextMenuListener(this);
		} catch (ClassCastException ccex) {
			ccex.printStackTrace();
		}
		this.menu = buildMenu();

		// Loading properties
		systemProps = new Properties();
		try {
			systemProps.load(this.getClass().getResourceAsStream("cheminfo.props"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(systemProps.get("cheminfo.error"));
	}

	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu pmenu) {
		this.nodeView = nodeView;
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		pmenu.add(menu);
	}

	/**
	 * Builds the popup menu
	 * 
	 * @return
	 */
	public JMenu buildMenu() {
		JMenu menu = new JMenu("Cheminformatics Tools");
		JMenuItem depict = buildMenuItem("Depict 2D Structure", DEPICT);
		menu.add(depict);
		return menu;
	}

	/**
	 * Builds a menu item in the popup menu
	 * 
	 * @param label
	 * @param command
	 * @return
	 */
	public JMenuItem buildMenuItem(String label, String command) {
		JMenuItem item = new JMenuItem(label);
		item.setActionCommand(command);
		item.addActionListener(this);
		return item;
	}

	/**
	 * Detect that a new network view has been created and add our node context
	 * menu listener to nodes within this network
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			// Add menu to the context dialog
			((CyNetworkView) evt.getNewValue())
					.addNodeContextMenuListener(this);
		}
	}
	
	/**
	 * Get an attribute from a node
	 * 
	 * @param node
	 * @param attr
	 * @return
	 */
	public static String getAttribute(CyNode node, String attr) {
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		String value = attributes.getStringAttribute(node.getIdentifier(),
				attr);
		if (null == value || "".equals(value)) {
			// Now search for smiles
			String[] names = attributes.getAttributeNames();
			for (String string : names) { 
				if (attr.equalsIgnoreCase(string)) {
					value = attributes.getStringAttribute(node.getIdentifier(), string);
					break;
				}
			}
		}			
		return value;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd.equals(DEPICT)) {
			List nodes = Cytoscape.getCurrentNetworkView().getSelectedNodes();
			if (nodes.size() > 1) {
				depictMultipleNodes(nodes);
			} else if (nodes.size() == 1) {
				CyNode node = (CyNode) nodeView.getNode();				
				depictSingleNode(node);
			}
		}
	}
	
	/**
	 * Depict 2D structure for multiple nodes
	 * 
	 * @param nodes
	 */
	private void depictMultipleNodes(List nodes) {
		JDialog dialog = new JDialog(Cytoscape.getDesktop(), "View 2D Structures", false);
		List rows = new ArrayList();
		for (Object node : nodes) {
			NodeView nodeView = (NodeView)node;
			//depictSingleNode((CyNode)nodeView.getNode());
			StructureDepictor depictor = new StructureDepictor((CyNode)nodeView.getNode());
			//Image image = depictor.
			String text = depictor.getNodeText();
			String id = nodeView.getNode().getIdentifier();
			List row = new ArrayList();
			row.add(id);
			row.add(text);
			row.add(depictor);
			rows.add(row);
		}
		
		List colNames = new ArrayList();
		colNames.add("Identifier");
		colNames.add("Smiles/InChI");
		colNames.add("2D Structure");
		
        ChemTableSorter sorter = new ChemTableSorter(rows, colNames);
        ChemTable table = new ChemTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        
        JScrollPane spane = new JScrollPane();
        spane.getViewport().add(table);
        dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(spane, BorderLayout.CENTER);
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.pack();
		dialog.setVisible(true);		
	}


	/**
	 * Depict 2D structure for a single node
	 * 
	 * @param node
	 */
	private void depictSingleNode(CyNode node) {
		StructureDepictor depictor = new StructureDepictor(node);
		MoleculeViewDialog dialog = new MoleculeViewDialog(Cytoscape.getDesktop());
		dialog.setSize(320, 320);
		dialog.setDepictor(depictor);
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Display an error message
	 * 
	 * @param message
	 */
	public void displayErrorDialog(String message) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "ChemInfo Plugin Error!", JOptionPane.ERROR_MESSAGE);
	}
}
