/*
  File: CytoscapeMenuBar.java

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
package org.cytoscape.internal.view;


import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


import org.cytoscape.application.swing.CyAction;
import org.cytoscape.util.swing.JMenuTracker;

public class CytoscapeMenuBar extends JMenuBar {
	private final static long serialVersionUID = 1202339868642259L;
	public static final String DEFAULT_MENU_SPECIFIER = "Tools";

	protected Set actionMembersSet = null;
	protected Map<Action,JMenuItem> actionMenuItemMap = null;
	protected JMenuTracker menuTracker;


	/**
	 * The Menu-&gt;Integer "effective last index"
	 * Map for Menus with menu items that want to be at the end.
	 */
	protected Map<JMenu,Integer> menuEffectiveLastIndexMap = null;

	/**
	 * Default constructor. 
	 */
	public CytoscapeMenuBar() {
		menuEffectiveLastIndexMap = new HashMap<JMenu,Integer>();
		actionMenuItemMap = new HashMap<Action,JMenuItem>();
		menuTracker = new JMenuTracker(this);

		// Load the first menu, just to please the layouter. Also make sure the
		// menu bar doesn't get too small.
		// "File" is always first
		setMinimumSize(getMenu("File").getPreferredSize());
	}


	/**
	 * If the given Action has a present and false inMenuBar property, return;
	 * otherwise delegate to addAction( String, Action ) with the value of its
	 * preferredMenu property, or null if it does not have that property.
	 */
	public boolean addAction(CyAction action) {
		String menu_name = null;

		if (action.isInMenuBar()) {
			menu_name = action.getPreferredMenu();
		} else {
			return false;
		}

		// At present we allow an Action to be in this menu bar only once.
		JMenuItem menu_item = null;

		if (actionMenuItemMap != null) {
			menu_item = (JMenuItem) actionMenuItemMap.get(action);
		}

		if (menu_item != null) {
			return false;
		}

		JMenu menu = getMenu(menu_name);
		menu_item = createJMenuItem(action);

		// Add an Accelerator Key, if wanted
		KeyStroke accelerator = action.getAcceleratorKeyStroke();
		if ( accelerator != null )
			menu_item.setAccelerator(accelerator);
			
		menu.addMenuListener(action);
		menu.add(menu_item);
		actionMenuItemMap.put(action, menu_item);

		return true;
	} 

	/**
	 * If the given Action has a present and false inMenuBar property, return;
	 * otherwise if there's a menu item for the action, remove it. Its menu is
	 * determined my its preferredMenu property if it is present; otherwise by
	 *  DEFAULT_MENU_SPECIFIER.
	 */
	public boolean removeAction(CyAction action) {
		if (actionMenuItemMap == null) {
			return false;
		}

		JMenuItem menu_item = (JMenuItem) actionMenuItemMap.remove(action);

		if (menu_item == null) {
			return false;
		}

		String menu_name = null;

		if (action.isInMenuBar()) {
			menu_name = action.getPreferredMenu();
		} else {
			return false;
		}

		if (menu_name == null) {
			menu_name =  DEFAULT_MENU_SPECIFIER;
		}

		getMenu(menu_name).remove(menu_item);

		return true;
	}

	/**
	 * @return the menu named in the given String. The String may contain
	 *         multiple menu names, separated by dots ('.'). If any contained
	 *         menu name does not correspond to an existing menu, then that menu
	 *         will be created as a child of the menu preceeding the most recent
	 *         dot or, if there is none, then as a child of this MenuBar.
	 */
	public JMenu getMenu(String menu_string) {
		if ( menu_string == null )
			menu_string = DEFAULT_MENU_SPECIFIER;

		final JMenu menu = menuTracker.getMenu(menu_string, -1);
		revalidate();
		repaint();
		return menu; 
	}

	private JMenuItem createJMenuItem(CyAction action) {
		if ( action.useCheckBoxMenuItem() )
			return new JCheckBoxMenuItem(action);

		return new JMenuItem(action);
	}

	public JMenuBar getJMenuBar() {
		return this;
	}
}
