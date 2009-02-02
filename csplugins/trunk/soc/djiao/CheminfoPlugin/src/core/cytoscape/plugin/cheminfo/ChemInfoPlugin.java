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

import giny.model.GraphObject;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.NodeContextMenuListener;
import ding.view.EdgeContextMenuListener;

import cytoscape.plugin.cheminfo.model.Compound;
import cytoscape.plugin.cheminfo.model.Compound.AttriType;
import cytoscape.plugin.cheminfo.model.Compound.DescriptorType;
import cytoscape.plugin.cheminfo.ui.ChemInfoSettingsDialog;
import cytoscape.plugin.cheminfo.tasks.CreateAttributesTask;
import cytoscape.plugin.cheminfo.tasks.CreateCompoundTableTask;
import cytoscape.plugin.cheminfo.tasks.CreatePopupTask;
import cytoscape.plugin.cheminfo.tasks.TanimotoScorerTask;;


/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class ChemInfoPlugin extends CytoscapePlugin implements
		NodeContextMenuListener, EdgeContextMenuListener, PropertyChangeListener, 
		MenuListener,ActionListener {
	
	static public CyLogger logger = CyLogger.getLogger(ChemInfoPlugin.class);
	static private String pubChemURL = "http://pubchem.ncbi.nlm.nih.gov/";
	static private String pubChemSearch = pubChemURL+"search/search.cgi?cmd=search&q_type=dt&simp_schtp=fs&q_data=";
	static private String chemSpiderURL = "http://www.chemspider.com/";
	static private String chemSpiderSearch = chemSpiderURL+"Search.aspx?q=";
	static private String cebiURL = "http://www.ebi.ac.uk/";
	static private String cebiSearch = cebiURL+"chebi/searchFreeText.do?searchString=";

	private NodeView nodeView = null;
	private EdgeView edgeView = null;
	private Properties systemProps = null;
	private ChemInfoSettingsDialog settingsDialog = null; 
	private Properties cytoProps;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public ChemInfoPlugin() {
		try {
			// Set ourselves up to listen for new networks
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
					.addPropertyChangeListener(
							CytoscapeDesktop.NETWORK_VIEW_CREATED, this);

			((DGraphView) Cytoscape.getCurrentNetworkView())
					.addNodeContextMenuListener(this);
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.addEdgeContextMenuListener(this);
		} catch (ClassCastException ccex) {
			logger.error("Unable to setup network listeners: "+ccex.getMessage(), ccex);
			return;
		}

		// Loading properties
		systemProps = new Properties();
		try {
			systemProps.load(this.getClass().getResourceAsStream(
					"cheminfo.props"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Unable to load properties: "+e.getMessage(), e);
			return;
		}

		try {
			JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
			JMenu menu = new JMenu(systemProps.getProperty("cheminfo.menu"));
			menu.addMenuListener(this);
			pluginMenu.add(menu);
		
			cytoProps = CytoscapeInit.getProperties();
			settingsDialog = new ChemInfoSettingsDialog();
		} catch (Exception e) {
			logger.error("Unable to initialize menus: "+e.getMessage(), e);
		}
	}

	/**
 	 * NodeContextMenuListener method to add our context menu to a specific
 	 * NodeView.  We create our menu entries dynamically depending on whether
 	 * this node has compound information defined.
 	 *
 	 * @param nodeView the nodeView whose context menu we're creating
 	 * @param pmenu the menu we're adding our menu to
 	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu pmenu) {
		this.nodeView = nodeView;
		this.edgeView = null;
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		JMenu menu = new JMenu(systemProps.getProperty("cheminfo.menu"));
		pmenu.add(buildPopupMenu(menu, false, nodeView));
	}

	/**
 	 * EdgeContextMenuListener method to add our context menu to a specific
 	 * EdgeView.  We create our menu entries dynamically depending on whether
 	 * this edge has compound information defined.
 	 *
 	 * @param edgeView the edgeView whose context menu we're creating
 	 * @param pmenu the menu we're adding our menu to
 	 */
	public void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu pmenu) {
		this.edgeView = edgeView;
		this.nodeView = null;
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		JMenu menu = new JMenu(systemProps.getProperty("cheminfo.menu"));
		pmenu.add(buildPopupMenu(menu, true, edgeView));
	}


	/**
 	 * MenuListener: menuCanceled (not used)
 	 */
	public void menuCanceled (MenuEvent e) {};

	/**
 	 * MenuListener: menuDeselected (not used)
 	 */
	public void menuDeselected (MenuEvent e) {};

	/**
 	 * MenuListener: menuSelected is called when the user
 	 * selects our main menu. This method will populate the submenu
 	 * depending on what is selected, the presence of attributes
 	 * that contain compound descriptors, and the type of object
 	 * that has been selected.
 	 *
 	 * @param e the menu event
 	 */
	public void menuSelected (MenuEvent e) {
		JMenu m = (JMenu)e.getSource();

		// remove the current entries
		Component[] subMenus = m.getMenuComponents();
		for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }
		addDepictionMenus(m);
		addSimilarityMenu(m);
		addCreateAttributesMenu(m);
		addSettingsMenu(m);
	};

	/**
	 * Detect that a new network view has been created and add our node context
	 * menu listener to nodes within this network
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			// Add menu to the context dialog
			((CyNetworkView) evt.getNewValue())
					.addNodeContextMenuListener(this);
			((CyNetworkView) evt.getNewValue())
					.addEdgeContextMenuListener(this);
		}
	}


	/**
	 * Builds the popup menu for context menus
	 * 
	 * @param menu the menu we're going add our items to
	 * @param edge if 'true' then this is an edge context
	 * @param context the NodeView or EdgeView this menu is for
	 * @return the updated menu
	 */
	private JMenu buildPopupMenu(JMenu menu, boolean edge, Object context) {
		if (edge) {
			addEdgeDepictionMenus(menu, (EdgeView)context);
			addEdgeAttributesMenus(menu, (EdgeView)context);
			updateLinkOut(((EdgeView)context).getEdge());
		} else {
			addNodeDepictionMenus(menu, (NodeView)context);
			addNodeAttributesMenus(menu, (NodeView)context);
			updateLinkOut(((NodeView)context).getNode());
		}
		addSimilarityMenu(menu);
		addSettingsMenu(menu);
		return menu;
	}

	/**
 	 * Builds the menus for depiction
 	 *
 	 * @param menu the menu we're going to add our items to
 	 */
	private void addDepictionMenus(JMenu menu) {
		JMenu depict = new JMenu(systemProps.getProperty("cheminfo.menu.2ddepiction"));
		addEdgeDepictionMenus(depict, null);
		addNodeDepictionMenus(depict, null);
		menu.add(depict);
	}

	/**
	 * Builds the popup menu for edge depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param edgeContext the EdgeView this menu is for
	 */
	private void addEdgeDepictionMenus(JMenu menu, EdgeView edgeContext) {
		// Check and see if we have any edge attributes
		Collection<CyEdge> selectedEdges = Cytoscape.getCurrentNetwork().getSelectedEdges();

		if (edgeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem("cheminfo.menu.2ddepiction.allEdges",
				                             "cheminfo.menu.2ddepiction.allEdges");
			if (!settingsDialog.hasEdgeCompounds(null))
				item.setEnabled(false);
			menu.add(item);
			if (selectedEdges != null && selectedEdges.size() > 0) {
				item = buildMenuItem("cheminfo.menu.2ddepiction.selectedEdges",
			  	                   "cheminfo.menu.2ddepiction.selectedEdges");
				if (!settingsDialog.hasEdgeCompounds(selectedEdges))
					item.setEnabled(false);
				menu.add(item);
			}
			return;
		}

		// Populating popup menu
		JMenu depict = new JMenu(systemProps.getProperty("cheminfo.menu.2ddepiction"));

		depict.add(buildMenuItem("cheminfo.menu.2ddepiction.thisEdge",
		                         "cheminfo.menu.2ddepiction.thisEdge"));

		if (selectedEdges == null) selectedEdges = new ArrayList();

		if (!selectedEdges.contains(edgeContext.getEdge()))
			selectedEdges.add((CyEdge)edgeContext.getEdge());

		depict.add(buildMenuItem("cheminfo.menu.2ddepiction.selectedEdges",
		                         "cheminfo.menu.2ddepiction.selectedEdges"));

		if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
			depict.setEnabled(false);
		}
		menu.add(depict);

		return;
	}

	/**
	 * Builds the popup menu for node depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param nodeContext the NodeView this menu is for
	 */
	private void addNodeDepictionMenus(JMenu menu, NodeView nodeContext) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem("cheminfo.menu.2ddepiction.allNodes",
				                            "cheminfo.menu.2ddepiction.allNodes");
			if (!settingsDialog.hasNodeCompounds(null))
				item.setEnabled(false);
			menu.add(item);
			if (selectedNodes != null && selectedNodes.size() > 0) {
				item = buildMenuItem("cheminfo.menu.2ddepiction.selectedNodes",
			  	                   "cheminfo.menu.2ddepiction.selectedNodes");
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					item.setEnabled(false);
				menu.add(item);
			}
			return;
		}

		// Populating popup menu
		JMenu depict = new JMenu(systemProps.getProperty("cheminfo.menu.2ddepiction"));

		depict.add(buildMenuItem("cheminfo.menu.2ddepiction.thisNode",
		                         "cheminfo.menu.2ddepiction.thisNode"));

		if (selectedNodes == null) selectedNodes = new ArrayList();

		if (!selectedNodes.contains(nodeContext.getNode()))
			selectedNodes.add((CyNode)nodeContext.getNode());

		depict.add(buildMenuItem("cheminfo.menu.2ddepiction.selectedNodes",
		                         "cheminfo.menu.2ddepiction.selectedNodes"));

		if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
			depict.setEnabled(false);
		}
		menu.add(depict);

		return;
	}

	/**
	 * Builds the popup menu for similarity calculations
	 * 
	 * @param menu the menu we're going add our items to
	 */
	private void addSimilarityMenu(JMenu menu) {
		JMenu simMenu = new JMenu(systemProps
				.getProperty("cheminfo.menu.similarity"));
		menu.add(simMenu);
		Set<GraphObject> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
		if (selectedNodes != null && selectedNodes.size() > 1) {
			JMenu tanMenu = new JMenu(systemProps.getProperty("cheminfo.menu.similarity.tanimoto"));
			tanMenu.add(buildMenuItem("cheminfo.menu.similarity.tanimoto.allNodes",
			                          "cheminfo.menu.similarity.tanimoto.allNodes"));
			tanMenu.add(buildMenuItem("cheminfo.menu.similarity.tanimoto.selectedNodes",
			                          "cheminfo.menu.similarity.tanimoto.selectedNodes"));
			simMenu.add(tanMenu);
		} else {
			JMenuItem tanimoto = buildMenuItem("cheminfo.menu.similarity.tanimoto",
				"cheminfo.menu.similarity.tanimoto.allNodes");
			simMenu.add(tanimoto);
		}
		return;
	}

	/**
	 * Builds the popup menu for createing attributes from chemical descriptors
	 * 
	 * @param menu the menu we're going add our items to
	 */
	private void addCreateAttributesMenu(JMenu menu) {
		JMenu create = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes"));
		addEdgeAttributesMenus(create, null);
		addNodeAttributesMenus(create, null);
		menu.add(create);
	}

	private void addNodeAttributesMenus(JMenu menu, NodeView nodeContext) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			// Populating main menu
			JMenu create = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.allNodes"));
			addDescriptors(create, Cytoscape.getCurrentNetwork().nodesList(), null);
			if (!settingsDialog.hasNodeCompounds(null))
				create.setEnabled(false);
			menu.add(create);
			if (selectedNodes != null && selectedNodes.size() > 0) {
				create = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.selectedNodes"));
				addDescriptors(create, selectedNodes, null);
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					create.setEnabled(false);
				menu.add(create);
			}
			return;
		}

		JMenu create = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes"));

		// Populating popup menu
		JMenu thisNodeMenu = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.thisNode"));
		List<CyNode> thisNode = new ArrayList();
		thisNode.add((CyNode)nodeContext.getNode());
		addDescriptors(thisNodeMenu, thisNode, null);
		if (!settingsDialog.hasNodeCompounds(thisNode)) {
			thisNodeMenu.setEnabled(false);
		}
		create.add(thisNodeMenu);

		if (selectedNodes.size() > 1) {
			JMenu selectedMenu = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.selectedNodes"));
			addDescriptors(selectedMenu, selectedNodes, null);
			if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
				selectedMenu.setEnabled(false);
			}
			create.add(selectedMenu);
		}

		menu.add(create);

		if (!selectedNodes.contains(nodeContext.getNode()))
			selectedNodes.add((CyNode)nodeContext.getNode());

		return;
	}

	private void addEdgeAttributesMenus(JMenu menu, EdgeView edgeContext) {
		// Check and see if we have any node attributes
		Collection<CyEdge> selectedEdges = Cytoscape.getCurrentNetwork().getSelectedEdges();

		if (edgeContext == null) {
			// Populating main menu
			JMenu create = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.allEdges"));
			addDescriptors(create, null, Cytoscape.getCurrentNetwork().edgesList());
			if (!settingsDialog.hasEdgeCompounds(null))
				create.setEnabled(false);
			menu.add(create);
			if (selectedEdges != null && selectedEdges.size() > 0) {
				create = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.selectedEdges"));
				addDescriptors(create, null, selectedEdges);
				if (!settingsDialog.hasEdgeCompounds(selectedEdges))
					create.setEnabled(false);
				menu.add(create);
			}
			return;
		}

		// Populating popup menu
		JMenu create = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes"));
		JMenu thisEdgeMenu = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.thisEdge"));
		List<CyEdge> thisEdge = new ArrayList();
		thisEdge.add((CyEdge)edgeContext.getEdge());
		addDescriptors(thisEdgeMenu, null, thisEdge);
		if (!settingsDialog.hasEdgeCompounds(thisEdge)) {
			thisEdgeMenu.setEnabled(false);
		}
		create.add(thisEdgeMenu);

		if (selectedEdges.size() > 1) {
			JMenu selectedMenu = new JMenu(systemProps.getProperty("cheminfo.menu.createattributes.selectedEdges"));
			addDescriptors(selectedMenu, null, selectedEdges);
			if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
				selectedMenu.setEnabled(false);
			}
			create.add(selectedMenu);
		}
		menu.add(create);

		if (!selectedEdges.contains(edgeContext.getEdge()))
			selectedEdges.add((CyEdge)edgeContext.getEdge());

		return;
	}

	private void addDescriptors(JMenu menu, Collection<CyNode> selectedNodes, 
	                            Collection<CyEdge> selectedEdges) {
		// Get the list of descriptors
		List<DescriptorType> dList = Compound.getDescriptorList();
		// Add them to the menus
		for (DescriptorType type: dList) {
			if (type == DescriptorType.IMAGE ||
          type == DescriptorType.ATTRIBUTE ||
			    type == DescriptorType.IDENTIFIER) continue;

			JMenuItem item = new JMenuItem(type.toString());
			item.addActionListener(new CreateAttributesTask(selectedNodes, selectedEdges, type, settingsDialog));
			menu.add(item);
		}
	}

	/**
 	 * Adds the Settings menu item
 	 *
 	 * @param menu the menu to add our Settings menu to
 	 */
	private void addSettingsMenu(JMenu menu) {
		menu.add(new JSeparator());
		menu.add(buildMenuItem("cheminfo.menu.settings","cheminfo.menu.settings"));
	}

	/**
	 * Builds a menu item in the popup menu
	 * 
	 * @param label
	 * @param command
	 * @return
	 */
	private JMenuItem buildMenuItem(String label, String command) {
		JMenuItem item = new JMenuItem(systemProps.getProperty(label));
		item.setActionCommand(command);
		item.addActionListener(this);
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		
		if (cmd.equals("cheminfo.menu.settings")) {
			// Bring up the settings dialog
			settingsDialog.pack();
			settingsDialog.setVisible(true);
		} else if (cmd.equals("cheminfo.menu.2ddepiction.thisNode")) {
			// Bring up the popup-style of depiction
			createPopup(nodeView, settingsDialog);
		} else if (cmd.equals("cheminfo.menu.2ddepiction.selectedNodes")) {
			// Bring up the compound table
			createTable(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog);
		} else if (cmd.equals("cheminfo.menu.2ddepiction.allNodes")) {
			createTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog);
		} else if (cmd.equals("cheminfo.menu.2ddepiction.thisEdge")) {
			createPopup(edgeView, settingsDialog);
		} else if (cmd.equals("cheminfo.menu.2ddepiction.selectedEdges")) {
			// Bring up the compound table
			createTable(Cytoscape.getCurrentNetwork().getSelectedEdges(), settingsDialog);
		} else if (cmd.equals("cheminfo.menu.2ddepiction.allEdges")) {
			createTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().edgesList(), settingsDialog);
		} else if (cmd.equals("cheminfo.menu.similarity.tanimoto.selectedNodes")) {
			createScoreTable(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog, false);
		} else if (cmd.equals("cheminfo.menu.similarity.tanimoto.allNodes")) {
			createScoreTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog, true);
		} else if (cmd.equals("createAttributes")) {
		}
	}
	
	/**
	 * Display an error message
	 * 
	 * @param message
	 */
	public void displayErrorDialog(String messageKey) {
		JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), systemProps
				.getProperty(messageKey), "ChemInfo Plugin Error!",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
 	 * Create a 2D popup dialog for this node or edge
 	 *
 	 * @param view the nodeView or edgeView we're going to pull the compounds from
 	 * @param dialog the settings dialog
 	 */
	private void createPopup(Object view, ChemInfoSettingsDialog dialog) {
    CreatePopupTask loader = new CreatePopupTask(view, dialog, dialog.getMaxCompounds());
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
  }

	/**
 	 * Create a compound table for this group of nodes or edges
 	 *
 	 * @param selection the nodes or edges we're going to pull the compounds from
 	 * @param dialog the settings dialog
 	 */
	private void createTable(Collection<GraphObject>selection, ChemInfoSettingsDialog dialog) {
		CreateCompoundTableTask loader = new CreateCompoundTableTask(selection, dialog, dialog.getMaxCompounds());
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
	}

	/**
 	 * Calculate the tanimoto coefficients for each pair of compounds
 	 *
 	 * @param selection the nodes or edges we're going to pull the compounds from
 	 * @param dialog the settings dialog
 	 * @param newNetwork if 'true' a new network is created and 
 	 */
	private void createScoreTable(Collection<GraphObject>selection, ChemInfoSettingsDialog dialog, boolean newNetwork) {
		TanimotoScorerTask scorer = new TanimotoScorerTask(selection, dialog, newNetwork);
		TaskManager.executeTask(scorer, scorer.getDefaultTaskConfig());
	}


	/**
 	 * Add our compound-dependent linkouts to the linkout properties
 	 *
 	 * @param go the object (Node or Edge) we're adding our linkout to
 	 */
	private void updateLinkOut(GraphObject go) {

		CyAttributes attributes = null;
		String type = null;
		if (go instanceof Node) {
			attributes = Cytoscape.getNodeAttributes();
			type = "node";
		} else {
			attributes = Cytoscape.getEdgeAttributes();
			type = "edge";
		}

		// Only get the loaded compounds so our popup menus don't get really slow
		List<Compound> cList = Compound.getCompounds(go, attributes,
                                                 settingsDialog.getCompoundAttributes(type,AttriType.smiles),
                                                 settingsDialog.getCompoundAttributes(type,AttriType.inchi), true);

		if (cList == null || cList.size() == 0) {
			cytoProps.remove(type+"linkouturl.Entrez.PubChem");
			cytoProps.remove(type+"linkouturl.ChemSpider");
			cytoProps.remove(type+"linkouturl.ChEBI");
		} else {
			cytoProps.put(type+"linkouturl.Entrez.PubChem", pubChemSearch+cList.get(0).getMoleculeString());
			cytoProps.put(type+"linkouturl.ChemSpider", chemSpiderSearch+cList.get(0).getMoleculeString());
			cytoProps.put(type+"linkouturl.ChEBI", cebiSearch+cList.get(0).getMoleculeString());
		}
	}
}
