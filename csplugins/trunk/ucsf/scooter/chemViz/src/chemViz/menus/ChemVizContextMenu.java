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

package chemViz.menus;

import java.util.List;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ding.view.NodeContextMenuListener;
import ding.view.EdgeContextMenuListener;

import giny.model.GraphObject;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;

import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.ui.ChemInfoSettingsDialog;

/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class ChemVizContextMenu implements NodeContextMenuListener, EdgeContextMenuListener {
	
	static private String pubChemURL = "http://pubchem.ncbi.nlm.nih.gov/";
	static private String pubChemSearch = pubChemURL+"search/search.cgi?cmd=search&q_type=dt&simp_schtp=fs&q_data=";
	static private String chemSpiderURL = "http://www.chemspider.com/";
	static private String chemSpiderSearch = chemSpiderURL+"Search.aspx?q=";
	static private String cebiURL = "http://www.ebi.ac.uk/";
	static private String cebiSearch = cebiURL+"chebi/searchFreeText.do?searchString=";

	private Properties systemProperties;
	private ChemInfoSettingsDialog settingsDialog;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public ChemVizContextMenu(Properties systemProperties, ChemInfoSettingsDialog settingsDialog) {
		this.systemProperties = systemProperties;
		this.settingsDialog = settingsDialog;
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
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		JMenu menu = new JMenu(systemProperties.getProperty("chemViz.menu"));
		pmenu.add(buildPopupMenu(menu, nodeView));
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
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		JMenu menu = new JMenu(systemProperties.getProperty("chemViz.menu"));
		pmenu.add(buildPopupMenu(menu, edgeView));
	}

	private JMenu buildPopupMenu(JMenu m, Object context) {
		if (context instanceof EdgeView) {
			new DepictionMenus(m, systemProperties, settingsDialog, (EdgeView) context);
			new AttributesMenu(m, systemProperties, settingsDialog, (EdgeView) context);
			updateLinkOut(((EdgeView)context).getEdge());
		} else {
			new DepictionMenus(m, systemProperties, settingsDialog, (NodeView) context);
			new NodeGraphicsMenus(m, systemProperties, settingsDialog, (NodeView) context);
			new AttributesMenu(m, systemProperties, settingsDialog, (NodeView) context);
			updateLinkOut(((NodeView)context).getNode());
		}
		new SimilarityMenu(m, systemProperties, settingsDialog);
		new SettingsMenu(m, systemProperties, settingsDialog);
		return m;
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

		Properties cytoProps = CytoscapeInit.getProperties();
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
