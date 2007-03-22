/*
  File: CyLayouts.java

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
package cytoscape.layout;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.init.CyInitParams;

import cytoscape.layout.LayoutAlgorithm;

import cytoscape.layout.algorithms.GridNodeLayout;

import cytoscape.layout.ui.LayoutMenu;
import cytoscape.layout.ui.LayoutSettingsDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 * CyLayouts is a singleton class that is used to register all available
 * layout algorithms.  The contents of this list are used to build the Layout
 * menu, display the advanced settings dialog, and interface with the current
 * cytoscape properties.
 *
 */
public class CyLayouts {
	private static HashMap<String, LayoutAlgorithm> layoutMap;
	private static HashMap<String, List> menuNameMap;
	private static HashMap<String, LayoutMenu> menuMap;
	private static JMenu layoutMenu = null;;
	private static JMenuItem settingsMenu;
	private static LayoutSettingsDialog settingsDialog;
	private static final String layoutProperty = "layout.";
	private static int mode = CyInitParams.GUI;

	static {
		new CyLayouts();
	}

	private CyLayouts() {
		layoutMap = new HashMap();
		menuNameMap = new HashMap();
		menuMap = new HashMap();
		mode = CytoscapeInit.getCyInitParams().getMode();
		if ((mode == CyInitParams.EMBEDDED_WINDOW) || (mode == CyInitParams.GUI)) {
			layoutMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout");
		}

		// Add the Settings menu
		addSettingsMenu(layoutMenu);
		// Add Cytoscape layouts by default
		addLayout(new GridNodeLayout(), "Cytoscape Layouts");

		//addLayout(new xxyy, "Cytoscape layouts");
	}

	/**
	 * Add a layout to the layout manager's list.  If menu is "null"
	 * it will be assigned to the "none" menu, which is not displayed.
	 * This can be used to register layouts that are to be used for
	 * specific algorithmic purposes, but not, in general, supposed
	 * to be for direct user use.
	 *
	 * @param layout The layout to be added
	 * @param menu The menu that this should appear under
	 */
	public static void addLayout(LayoutAlgorithm layout, String menu) {
		ArrayList<LayoutAlgorithm> menuList;
		layoutMap.put(layout.getName(), layout);

		// Don't mess with menus in headless mode
		if (layoutMenu == null) 
			return;

		if (menu == null) {
			menu = "none";
		}

		if (menuNameMap.containsKey(menu)) {
			menuList = (ArrayList) menuNameMap.get(menu);
		} else {
			menuList = new ArrayList();
			menuNameMap.put(menu, menuList);
			// New menu!  Create it
			createMenu(menu);
		}

		menuList.add(layout);
		addLayoutToMenu(menu, layout);
	}

	/**
	 * Remove a layout from the layout maanger's list.
	 *
	 * @param layout The layout to remove
	 */
	public static void removeLayout(LayoutAlgorithm layout) {
		// Remove it from the layout map
		layoutMap.remove(layout.getName());

		// Don't mess with menus in headless mode
		if (layoutMenu == null) 
			return;

		// Remove it from the menuNameMap
		Iterator iter = menuNameMap.keySet().iterator();

		while (iter.hasNext()) {
			// Get the key
			String menu = (String) iter.next();

			// OK, now get the list
			List menuList = (List) menuNameMap.get(menu);

			if (menuList.indexOf(layout) >= 0) {
				removeLayoutFromMenu(menu, layout);
				menuList.remove(layout);

				return;
			}
		}
	}

	/**
	 * Get the layout named "name".  If "name" does
	 * not exist, this will return null
	 *
	 * @param name String representing the name of the layout
	 * @return the layout of that name or null if it is not reigstered
	 */
	public static LayoutAlgorithm getLayout(String name) {
		if (layoutMap.containsKey(name))
			return (LayoutAlgorithm) layoutMap.get(name);

		return null;
	}

	/**
	 * Get all of the available layouts.
	 *
	 * @return a Collection of all the available layouts
	 */
	public static Collection<LayoutAlgorithm> getAllLayouts() {
		return layoutMap.values();
	}

	/**
	 * Get all of the layouts associated with a specific
	 * menu.
	 *
	 * @param menu The name of the menu
	 * @return a List of all layouts associated with this menu (could be null)
	 */
	public static List<LayoutAlgorithm> getLayoutMenuList(String menu) {
		if (menuNameMap.containsKey(menu))
			return (List<LayoutAlgorithm>) menuNameMap.get(menu);

		return null;
	}

	/**
	 * Get all of the menus (categories of layouts) currently defined.
	 *
	 * @return a Collection of Strings representing each of the menus
	 */
	public static Set<String> getLayoutMenus() {
		return menuNameMap.keySet();
	}

	/**
	 * Get the default layout.  This is either the grid layout or a layout
	 * chosen by the user via the setting of the "layout.default" property.
	 *
	 * @return LayoutAlgorithm to use as the default layout algorithm
	 */
	public static LayoutAlgorithm getDefaultLayout() {
		// See if the user has set the layout.default property
		String defaultLayout = CytoscapeInit.getProperties().getProperty(layoutProperty + "default");

		if ((defaultLayout == null) || !layoutMap.containsKey(defaultLayout)) {
			defaultLayout = "grid";
		}

		LayoutAlgorithm l = (LayoutAlgorithm) layoutMap.get(defaultLayout);
		System.out.println("getDefaultLayout returning " + l);

		// Nope, so return the grid layout 
		return l;
	}

	/**
	 * Menu interfaces
	 */
	private static void addLayoutToMenu(String menuName, LayoutAlgorithm layout) {
		if (menuName.equals("none") || layoutMenu == null)
			return;

		if (!menuMap.containsKey(menuName)) {
			createMenu(menuName);
		}

		// Get the LayoutMenu associated with this menuName
		LayoutMenu topMenu = (LayoutMenu) menuMap.get(menuName);
		topMenu.add(layout);
	}

	private static void createMenu(String menuName) {
		if (menuName.equals("none") || layoutMenu == null)
			return;

		// Create an empty JMenu
		LayoutMenu menu = new LayoutMenu(menuName);
		// Add it to our list
		menuMap.put(menuName, menu);
		layoutMenu.add(menu);
	}

	private static void removeLayoutFromMenu(String menuName, LayoutAlgorithm layout) {
		if (menuName.equals("none") || layoutMenu == null)
			return;

		if (!menuMap.containsKey(menuName))
			return;

		// Get the menu
		LayoutMenu topMenu = (LayoutMenu) menuMap.get(menuName);
		topMenu.remove(layout);

		// Check and see if this was the last item on this menu
		if (topMenu.getItemCount() == 0) {
			// Remove it from our map
			menuMap.remove(menuName);
			layoutMenu.remove(topMenu);
		}
	}

	private static void addSettingsMenu(JMenu topMenu) {
		if (topMenu == null) return;
		settingsMenu = new JMenuItem("Settings...");
		settingsMenu.setEnabled(false);
		settingsMenu.setArmed(false);
		settingsDialog = new LayoutSettingsDialog();
		settingsMenu.addActionListener(settingsDialog);
		topMenu.add(settingsMenu);
		topMenu.addSeparator();
	}
}
