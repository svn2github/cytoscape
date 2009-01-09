/*
  File: LayoutMenuManager.java

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

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.init.CyInitParams;

import cytoscape.layout.LayoutAlgorithm;
import cytoscape.layout.CyLayouts;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


public class LayoutMenuManager implements MenuListener {

	private static Map<String, List<LayoutAlgorithm>> menuAlgorithmMap;
	private static Map<String, LayoutMenu> menuMap;
	private static Set<LayoutAlgorithm> existingLayouts;

	static {
		menuAlgorithmMap = new HashMap<String,List<LayoutAlgorithm>>();
		menuMap = new HashMap<String,LayoutMenu>();
		existingLayouts = new HashSet<LayoutAlgorithm>();
	}

	public void menuCanceled(MenuEvent e) { };

	public void menuDeselected(MenuEvent e) { };

	public void menuSelected(MenuEvent e) { 
		Object o = e.getSource();
		if ( o instanceof JMenu ) 
			updateMenus((JMenu)o);
	}

	private void updateMenus(JMenu parentMenu) {

		// first add all layouts from cylayouts if they're not already there
		for ( LayoutAlgorithm la : CyLayouts.getAllLayouts() ) 
			if ( !existingLayouts.contains(la) )
				addLayout(la);

		// now remove any existing layouts that are no longer in cylayouts
		Set<LayoutAlgorithm> newLayouts = new HashSet<LayoutAlgorithm>(CyLayouts.getAllLayouts());
		for ( LayoutAlgorithm la : existingLayouts ) 
			if ( !newLayouts.contains(la) )
				removeLayout(la);

		// now update the menus if necessary 
		for ( String name : menuMap.keySet() ) {
			LayoutMenu lm = menuMap.get(name);
			int size = menuAlgorithmMap.get(name).size();

			// if the menu is not already there and
			// actually contains layouts, add it
			if ( !parentMenu.isMenuComponent(lm) && size > 0 )
				parentMenu.add(lm);

			// remove any menus that don't contain any layouts 
			else if ( parentMenu.isMenuComponent(lm) && size <= 0 )
				parentMenu.remove(lm);
		}
	}

	private void addLayout(LayoutAlgorithm layout) {
		
		String menuName = CyLayouts.getMenuName(layout);
		if (menuName == null )
			return;	

		existingLayouts.add(layout);

		// make sure the list is set up for this name
		if ( !menuAlgorithmMap.containsKey(menuName) ) {
			List<LayoutAlgorithm> menuList = new ArrayList<LayoutAlgorithm>();
			menuAlgorithmMap.put(menuName, menuList);
		}

		// add layout to the list of layouts for this name
		menuAlgorithmMap.get(menuName).add(layout);

		// make sure the menu is set up
		if ( !menuMap.containsKey(menuName) ) {
			LayoutMenu menu = new LayoutMenu(menuName);
			menuMap.put(menuName, menu);
		}

		// add layout to the menu for this name
		menuMap.get(menuName).add(layout);
	}

	private void removeLayout(LayoutAlgorithm layout) {

		for (String menu : menuAlgorithmMap.keySet()) {

			List<LayoutAlgorithm> menuList = menuAlgorithmMap.get(menu);

			if (menuList.indexOf(layout) >= 0) {
				menuList.remove(layout);
				menuMap.get(menu).remove(layout);
				existingLayouts.remove(layout);
				return;
			}
		}
	}

	/**
	 * Get all of the layouts associated with a specific
	 * menu.
	 *
	 * @param menu The name of the menu
	 * @return a List of all layouts associated with this menu (could be null)
	 */
	static List<LayoutAlgorithm> getLayoutsInMenu(String menu) {
		return menuAlgorithmMap.get(menu);
	}

	/**
	 * Get all of the menus (categories of layouts) currently defined.
	 *
	 * @return a Collection of Strings representing each of the menus
	 */
	static Set<String> getLayoutMenuNames() {
		return menuAlgorithmMap.keySet();
	}
}
