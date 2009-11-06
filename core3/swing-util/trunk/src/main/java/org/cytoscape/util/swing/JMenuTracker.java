/*
  File: JMenuTracker.java

  Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.util.swing;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A class that creates and manages hierarchies of JMenu objects.
 */
public class JMenuTracker {

	final private Map<String,JMenu> menuMap;
	final private JMenuBar rootMenuBar;
	final private JPopupMenu rootPopupMenu;

	/**
	 * This constructor allows you to specify a root JPopupMenu that all parent-less
	 * menus will be added to.  For example, if you call getMenu("File.New") then two
	 * menus will be created, "File" and it's submenu "New".  In this situation the
	 * "File" menu will be added to the JPopupMenu, while "New" will not.
	 *
	 * @param rootPopupMenu The root JPopupMenu that all parent-less JMenus will
	 * be added to.
	 */
	public JMenuTracker(JPopupMenu rootPopupMenu) {
		if ( rootPopupMenu == null )
			throw new NullPointerException("root popupmenu for menus is null");
		this.rootPopupMenu = rootPopupMenu;
		this.rootMenuBar = null;
		menuMap = new HashMap<String,JMenu>();
	}

	/**
	 * This constructor allows you to specify a root JMenuBar that all parent-less
	 * menus will be added to.  For example, if you call getMenu("File.New") then two
	 * menus will be created, "File" and it's submenu "New".  In this situation the
	 * "File" menu will be added to the JMenuBar, while "New" will not.
	 *
	 * @param rootMenuBar The root JMenuBar that all parent-less JMenus will
	 * be added to.
	 */
	public JMenuTracker(JMenuBar rootMenuBar) {
		if ( rootMenuBar == null )
			throw new NullPointerException("root menubar for menus is null");
		this.rootMenuBar = rootMenuBar;
		this.rootPopupMenu = null;
		menuMap = new HashMap<String,JMenu>();
	}


	/**
	 * Same as below, except the child menu is always put in the last
	 * location of the parent menu (parent_position = -1).
	 * 
	 * @param menu_string A '.' delimited string identifying menu names.
	 * @return The last JMenu object specified by the menu_string parameter. 
	 */
	public JMenu getMenu(String menu_string) {
		return getMenu(menu_string, -1);
	}

	/**
	 * This method will fetch an exising menu or create a new one if a menu
	 * with the specified name does not exist. The menu name is specified
	 * with a '.' delimited string, such that each token creates a new child
	 * menu.
	 *
	 * @param menu_string A '.' delimited string identifying menu names.
	 * @param parent_position The position within the parent menu at which the
	 * child menu should be placed.
	 * @return The last child JMenu object specified by the menu_string parameter. 
	 */
	public JMenu getMenu(String menu_string, int parent_position) {
		if (menu_string == null) 
			throw new NullPointerException("menu string is null");

		if ( menu_string.length() <= 0 )
			throw new IllegalArgumentException("menu string has zero length");

		StringTokenizer st = new StringTokenizer(menu_string, ".");
		JMenu parent_menu = null;
		JMenu menu = null;

		while (st.hasMoreTokens()) {
			String menu_token = st.nextToken();

			if (menuMap.containsKey(menu_token)) {
				menu = menuMap.get(menu_token);
			} else {
				menu = new JMenu(menu_token);
			
				// if there is a JMenu parent, use that
				if (parent_menu != null) 
					parent_menu.add(menu, parent_position);
				// otherwise use add the menu to the root component 
				else if ( rootMenuBar != null && rootPopupMenu == null ) 
					rootMenuBar.add(menu);
				else if ( rootMenuBar == null && rootPopupMenu != null ) 
					rootPopupMenu.add(menu, parent_position);
				else
					throw new IllegalStateException("we have no root popup menu or menu bar!");

				menuMap.put(menu_token, menu);
			}

			parent_menu = menu;
		}

		return menu;
	}
}
