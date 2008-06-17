package cytoscape.plugin.cheminfo;

import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

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

	public JMenu buildMenu() {
		JMenu menu = new JMenu("Cheminformatics Tools");
		JMenuItem depict = buildMenuItem("Depict 2D Structure", DEPICT);
		menu.add(depict);
		return menu;
	}

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

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd.equals(DEPICT)) {
			CyAttributes attributes = Cytoscape.getNodeAttributes();
			CyNode node = (CyNode) nodeView.getNode();
			
			String smiles = attributes.getStringAttribute(node.getIdentifier(),
					"smiles");
			if (null == smiles || "".equals(smiles)) {
				// Now search for smiles
				String[] names = attributes.getAttributeNames();
				for (String string : names) { 
					if ("smiles".equalsIgnoreCase(string)) {
						smiles = attributes.getStringAttribute(node.getIdentifier(), string);
						break;
					}
				}
			}				
			if (null == smiles || "".equals(smiles)) {			
				displayErrorDialog(systemProps.getProperty("cheminfo.depictor.noSmilesError"));
				return;
			}
			Image image = new StructureDepictor().depictWithUCSFSmi2Gif(smiles);
			if (image == null) {
				displayErrorDialog(systemProps.getProperty("cheminfo.system.error"));
				return;
			}
			JDialog dialog = new JDialog(Cytoscape.getDesktop(), smiles, false);
			JLabel label = new JLabel(new ImageIcon(image));
			dialog.getContentPane().add(label, BorderLayout.CENTER);
			dialog.setLocationRelativeTo(Cytoscape.getDesktop());
			dialog.pack();
			dialog.setVisible(true);
		}
	}

	public void displayErrorDialog(String message) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "ChemInfo Plugin Error!", JOptionPane.ERROR_MESSAGE);
	}
}
