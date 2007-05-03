/*
  File: LayoutAlgorithm.java

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

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.layout.LayoutAlgorithm;

import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 *
 * A DynamicLayoutMenu is a more complicated layout menu that constructs layout menu
 * items on-the-fly based on the capabilities of the layout algorithm and environment
 * factors such as whether or not nodes are selected, the presence of node or edge
 * attributes, etc.
 */
public class LayoutMenu extends JMenu implements MenuListener {
	List<LayoutAlgorithm> subMenuList;

	/**
	 * Creates a new LayoutMenu object.
	 *
	 * @param menuName  DOCUMENT ME!
	 */
	public LayoutMenu(String menuName) {
		super(menuName);
		addMenuListener(this);
		subMenuList = new ArrayList<LayoutAlgorithm>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param layout DOCUMENT ME!
	 */
	public void add(LayoutAlgorithm layout) {
		subMenuList.add(layout);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param layout DOCUMENT ME!
	 */
	public void remove(LayoutAlgorithm layout) {
		subMenuList.remove(layout);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getItemCount() {
		return subMenuList.size();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void menuCanceled(MenuEvent e) { } ;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void menuDeselected(MenuEvent e) { } ;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void menuSelected(MenuEvent e) {
		// Clear any previous entries
		this.removeAll();

		// Figure out if we have anything selected
		CyNetwork network = Cytoscape.getCurrentNetwork();
		Set selectedNodes = network.getSelectedNodes();
		boolean enableMenuItem = checkEnabled(); 

		// Now, add each layout, as appropriate
		for (LayoutAlgorithm layout: LayoutMenuManager.getLayoutsInMenu(getLabel())) {
			// Make sure we don't have any lingering locked nodes
			layout.unlockAllNodes();

			if ((layout.supportsNodeAttributes() != null)
			    || (layout.supportsEdgeAttributes() != null)) {
				super.add(new DynamicLayoutMenu(layout,enableMenuItem));
			} else if (layout.supportsSelectedOnly() && (selectedNodes.size() > 0)) {
				super.add(new DynamicLayoutMenu(layout,enableMenuItem));
			} else {
				super.add(new StaticLayoutMenu(layout,enableMenuItem));
			}
		}
	}

	private boolean checkEnabled() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		if ( network == null || network == Cytoscape.getNullNetwork() )
			return false;

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if ( view == null || view == Cytoscape.getNullNetworkView() )
			return false;
		else
			return true;
	}
}
