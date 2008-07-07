//$Revision$ $Author$ $Date$
/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.plugin.cheminfo;

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.NodeContextMenuListener;

/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
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
			final List nodes = Cytoscape.getCurrentNetworkView().getSelectedNodes();
			if (nodes.size() > 1) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						depictMultipleNodes(nodes, Cytoscape.getCurrentNetworkView());
					}
				});
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
	private void depictMultipleNodes(List nodes, CyNetworkView networkView) {
		List rows = new ArrayList();
		for (Object node : nodes) {
			NodeView nodeView = (NodeView)node;
			//depictSingleNode((CyNode)nodeView.getNode());
			StructureDepictor depictor = new StructureDepictor((CyNode)nodeView.getNode());
			//Image image = depictor.
			String text = depictor.getMoleculeString();
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
        ChemTable table = new ChemTable(sorter, networkView.getIdentifier());
        networkView.getNetwork().addSelectEventListener(table);
        sorter.setTableHeader(table.getTableHeader());
        table.showDialog();
	}


	/**
	 * Depict 2D structure for a single node
	 * 
	 * @param node
	 */
	private void depictSingleNode(CyNode node) {
		StructureDepictor depictor = new StructureDepictor(node);
		if (null == depictor.getMoleculeString() || "".equals(depictor.getMoleculeString())) {
			displayErrorDialog(systemProps.getProperty("cheminfo.depictor.noSmilesError"));
			return;
		}
		MoleculeViewDialog dialog = new MoleculeViewDialog(Cytoscape.getDesktop());
		dialog.setSize(320, 320);
		if (dialog.setDepictor(depictor)) {
			dialog.setLocationRelativeTo(Cytoscape.getDesktop());
			dialog.pack();
			dialog.setVisible(true);
		} else {
			displayErrorDialog(systemProps.getProperty("cheminfo.system.error"));
		}
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
