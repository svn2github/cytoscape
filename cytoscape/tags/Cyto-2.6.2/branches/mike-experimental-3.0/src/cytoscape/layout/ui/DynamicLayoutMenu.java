/*
  File: DynamicLayoutMenu.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.layout.ui;

import org.cytoscape.GraphPerspective;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutTask;

import cytoscape.view.CyNetworkView;

import cytoscape.task.util.TaskManager;

import org.cytoscape.Node;

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 *
 * A DynamicLayoutMenu is a more complicated layout menu that constructs layout menu
 * items on-the-fly based on the capabilities of the layout algorithm and environment
 * factors such as whether or not nodes are selected, the presence of node or edge
 * attributes, etc.
 */
public class DynamicLayoutMenu extends JMenu implements MenuListener {
	private final static long serialVersionUID = 1202339874245069L;
	private CyLayoutAlgorithm layout;
	private static final String NOATTRIBUTE = "(none)";
	private Set<Node> selectedNodes;

	/**
	 * Creates a new DynamicLayoutMenu object.
	 *
	 * @param layout  DOCUMENT ME!
	 */
	public DynamicLayoutMenu(CyLayoutAlgorithm layout, boolean enabled) {
		super(layout.toString());
		addMenuListener(this);
		this.layout = layout;
		selectedNodes = new HashSet();
		setEnabled(enabled);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void menuCanceled(MenuEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void menuDeselected(MenuEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void menuSelected(MenuEvent e) {
		// Clear any previous entries
		this.removeAll();

		// Base the menu structure only on the current network. 
		GraphPerspective network = Cytoscape.getCurrentNetwork();

		// First, do we support selectedOnly?
		selectedNodes = network.getSelectedNodes();

		if (layout.supportsSelectedOnly() && (selectedNodes.size() > 0)) {
			// Add selected node/all nodes menu
			addSelectedOnlyMenus();
		} else if (layout.supportsNodeAttributes() != null) {
			// Add node attributes menus
			addNodeAttributeMenus(this, false);
		} else if (layout.supportsEdgeAttributes() != null) {
			// Add edge attributes menus
			addEdgeAttributeMenus(this, false);
		} else {

			// No special menus, so make sure we layout all selected
			List<CyNetworkView> views = Cytoscape.getSelectedNetworkViews();
			for ( CyNetworkView view: views ) {
				layout.setSelectedOnly(false);
				layout.setLayoutAttribute(null);
				TaskManager.executeTask( new LayoutTask(layout, view),
				                         LayoutTask.getDefaultTaskConfig() );
			}
		}
	}

	private void addNodeAttributeMenus(JMenu parent, boolean selectedOnly) {
		// Get the node attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		addAttributeMenus(parent, nodeAttributes, layout.supportsNodeAttributes(), selectedOnly);
	}

	private void addEdgeAttributeMenus(JMenu parent, boolean selectedOnly) {
		// Get the edge attributes
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		addAttributeMenus(parent, edgeAttributes, layout.supportsEdgeAttributes(), selectedOnly);
	}

	private void addAttributeMenus(JMenu parent, CyAttributes attributes, byte[] typeList,
	                               boolean selectedOnly) {
		// Add any special attributes
		List specialList = layout.getInitialAttributeList();

		if (specialList != null) {
			for (Iterator iter = specialList.iterator(); iter.hasNext();) {
				parent.add(new LayoutAttributeMenuItem((String) iter.next(), selectedOnly));
			}
		}

		String[] attList = attributes.getAttributeNames();

		for (int i = 0; i < attList.length; i++) {
			if (!attributes.getUserVisible(attList[i]))
				continue;
			byte type = attributes.getType(attList[i]);

			for (int t = 0; t < typeList.length; t++) {
				if ((typeList[t] == -1) || (typeList[t] == type))
					parent.add(new LayoutAttributeMenuItem(attList[i], selectedOnly));
			}
		}
	}

	private void addSelectedOnlyMenus() {
		JMenuItem allNodes;
		JMenuItem selNodes;

		if ((layout.supportsNodeAttributes() != null) || (layout.supportsEdgeAttributes() != null)) {
			allNodes = new JMenu("All Nodes");
			selNodes = new JMenu("Selected Nodes Only");

			if (layout.supportsNodeAttributes() != null) {
				addNodeAttributeMenus((JMenu) allNodes, false);
				addNodeAttributeMenus((JMenu) selNodes, true);
			} else {
				addEdgeAttributeMenus((JMenu) allNodes, false);
				addEdgeAttributeMenus((JMenu) selNodes, true);
			}
		} else {
			allNodes = new LayoutAttributeMenuItem("All Nodes", false);
			selNodes = new LayoutAttributeMenuItem("Selected Nodes Only", true);
		}

		this.add(allNodes);
		this.add(selNodes);
	}

	protected class LayoutAttributeMenuItem extends JMenuItem implements ActionListener {
	private final static long serialVersionUID = 1202339874231860L;
		boolean selectedOnly = false;

		public LayoutAttributeMenuItem(String label, boolean selectedOnly) {
			super(label);
			addActionListener(this);
			this.selectedOnly = selectedOnly;
		}

		public void actionPerformed(ActionEvent e) {

			List<CyNetworkView> views = Cytoscape.getSelectedNetworkViews();

			for ( CyNetworkView netView : views ) {

				if (layout.supportsSelectedOnly()) {
					layout.setSelectedOnly(selectedOnly);

					if (selectedOnly && (selectedNodes.size() > 0)) {
						// Lock all unselected nodes
						Iterator nodeViews = netView.getNodeViewsIterator();

						while (nodeViews.hasNext()) {
							NodeView nv = (NodeView) nodeViews.next();
							Node node = nv.getNode();
	
							if (!selectedNodes.contains(node))
								layout.lockNode(nv);
						}
					}
				}

				if ((layout.supportsNodeAttributes() != null)
				    || (layout.supportsEdgeAttributes() != null)) {
					layout.setLayoutAttribute(e.getActionCommand());
				}

				TaskManager.executeTask( new LayoutTask(layout, netView), 
				                         LayoutTask.getDefaultTaskConfig() );
			}
		}
	}
}
