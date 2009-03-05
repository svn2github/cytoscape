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

import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.work.TaskManager;
import cytoscape.view.CyMenus;
import cytoscape.CyNetworkManager;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.*;


public class LayoutMenuManager implements MenuListener {

	private Map<String, List<CyLayoutAlgorithm>> menuAlgorithmMap;
	private Map<String, LayoutMenu> menuMap;
	private Set<CyLayoutAlgorithm> existingLayouts;
	private CyNetworkManager netmgr;
	private TaskManager tm;

	private CyLayouts cyLayouts;

	public LayoutMenuManager(CyMenus cyMenus, CyLayouts cyLayouts, CyNetworkManager netmgr, TaskManager tm) {
		menuAlgorithmMap = new HashMap<String,List<CyLayoutAlgorithm>>();
		menuMap = new HashMap<String,LayoutMenu>();
		existingLayouts = new HashSet<CyLayoutAlgorithm>();
		this.cyLayouts = cyLayouts;
		this.netmgr = netmgr;
		this.tm = tm;

		cyMenus.getLayoutMenu().addMenuListener(this);
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
		for ( CyLayoutAlgorithm la : cyLayouts.getAllLayouts() ) 
			if ( !existingLayouts.contains(la) )
				addLayout(la);

		// now remove any existing layouts that are no longer in cylayouts
		Set<CyLayoutAlgorithm> newLayouts = new HashSet<CyLayoutAlgorithm>(cyLayouts.getAllLayouts());
		for ( CyLayoutAlgorithm la : existingLayouts ) 
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

	private void addLayout(CyLayoutAlgorithm layout) {
		
		String menuName = cyLayouts.getMenuName(layout);
		if (menuName == null )
			return;	

		existingLayouts.add(layout);

		// make sure the list is set up for this name
		if ( !menuAlgorithmMap.containsKey(menuName) ) {
			List<CyLayoutAlgorithm> menuList = new ArrayList<CyLayoutAlgorithm>();
			menuAlgorithmMap.put(menuName, menuList);
		}

		// add layout to the list of layouts for this name
		menuAlgorithmMap.get(menuName).add(layout);

		// make sure the menu is set up
		if ( !menuMap.containsKey(menuName) ) {
			LayoutMenu menu = new LayoutMenu(menuName, this, netmgr, tm);
			menuMap.put(menuName, menu);
		}

		// add layout to the menu for this name
		menuMap.get(menuName).add(layout);
	}

	private void removeLayout(CyLayoutAlgorithm layout) {

		for (String menu : menuAlgorithmMap.keySet()) {

			List<CyLayoutAlgorithm> menuList = menuAlgorithmMap.get(menu);

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
	List<CyLayoutAlgorithm> getLayoutsInMenu(String menu) {
		return menuAlgorithmMap.get(menu);
	}

	/**
	 * Get all of the menus (categories of layouts) currently defined.
	 *
	 * @return a Collection of Strings representing each of the menus
	 */
	Set<String> getLayoutMenuNames() {
		return menuAlgorithmMap.keySet();
	}
}
