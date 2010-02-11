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

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import giny.view.EdgeView;
import giny.view.NodeView;

import chemViz.model.Compound;
import chemViz.model.Compound.DescriptorType;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.tasks.CreateAttributesTask;


/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class AttributesMenu extends ChemVizAbstractMenu {
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public AttributesMenu(JMenu menu, Properties systemProps, ChemInfoSettingsDialog settingsDialog,
	                       Object context) {
		super(systemProps, settingsDialog);

		JMenu create = new JMenu(systemProps.getProperty("chemViz.menu.createattributes"));
		if (context == null) {
			addEdgeAttributesMenus(create, null);
			addNodeAttributesMenus(create, null);
		} else if (context instanceof NodeView) {
			addNodeAttributesMenus(create, (NodeView)context);
		} else {
			addEdgeAttributesMenus(create, (EdgeView)context);
		}
		menu.add(create);
	}

	private void addNodeAttributesMenus(JMenu menu, NodeView nodeContext) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			// Populating main menu
			JMenu create = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.allNodes"));
			addDescriptors(create, Cytoscape.getCurrentNetwork().nodesList(), null);
			if (!settingsDialog.hasNodeCompounds(null))
				create.setEnabled(false);
			menu.add(create);
			if (selectedNodes != null && selectedNodes.size() > 0) {
				create = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.selectedNodes"));
				addDescriptors(create, selectedNodes, null);
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					create.setEnabled(false);
				menu.add(create);
			}
			return;
		}

		JMenu create = new JMenu(systemProps.getProperty("chemViz.menu.createattributes"));

		// Populating popup menu
		JMenu thisNodeMenu = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.thisNode"));
		List<CyNode> thisNode = new ArrayList();
		thisNode.add((CyNode)nodeContext.getNode());
		addDescriptors(thisNodeMenu, thisNode, null);
		if (!settingsDialog.hasNodeCompounds(thisNode)) {
			thisNodeMenu.setEnabled(false);
		}
		create.add(thisNodeMenu);

		if (selectedNodes.size() > 1) {
			JMenu selectedMenu = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.selectedNodes"));
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
			JMenu create = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.allEdges"));
			addDescriptors(create, null, Cytoscape.getCurrentNetwork().edgesList());
			if (!settingsDialog.hasEdgeCompounds(null))
				create.setEnabled(false);
			menu.add(create);
			if (selectedEdges != null && selectedEdges.size() > 0) {
				create = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.selectedEdges"));
				addDescriptors(create, null, selectedEdges);
				if (!settingsDialog.hasEdgeCompounds(selectedEdges))
					create.setEnabled(false);
				menu.add(create);
			}
			return;
		}

		// Populating popup menu
		JMenu create = new JMenu(systemProps.getProperty("chemViz.menu.createattributes"));
		JMenu thisEdgeMenu = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.thisEdge"));
		List<CyEdge> thisEdge = new ArrayList();
		thisEdge.add((CyEdge)edgeContext.getEdge());
		addDescriptors(thisEdgeMenu, null, thisEdge);
		if (!settingsDialog.hasEdgeCompounds(thisEdge)) {
			thisEdgeMenu.setEnabled(false);
		}
		create.add(thisEdgeMenu);

		if (selectedEdges.size() > 1) {
			JMenu selectedMenu = new JMenu(systemProps.getProperty("chemViz.menu.createattributes.selectedEdges"));
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

	// NotUsed
	public void actionPerformed(ActionEvent evt) {}

}
