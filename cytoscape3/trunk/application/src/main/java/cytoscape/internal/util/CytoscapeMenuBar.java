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
package cytoscape.internal.util;


import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import cytoscape.util.CyMenuBar;
import cytoscape.util.CyAction;

/**
 *
 */
public class CytoscapeMenuBar extends JMenuBar implements CyMenuBar {
	private final static long serialVersionUID = 1202339868642259L;
	/**
	 *
	 */
	public static final String DEFAULT_MENU_SPECIFIER = "Tools";
	protected static final int NO_INDEX = -2;
	protected String defaultMenuSpecifier = DEFAULT_MENU_SPECIFIER;
	protected Set actionMembersSet = null;
	protected Map<Action,JMenuItem> actionMenuItemMap = null;
	protected Map<String,JMenu> menuMap = null;


	/**
	 * The Menu-&gt;Integer "effective last index"
	 * Map for Menus with menu items that want to be at the end.
	 */
	protected Map<JMenu,Integer> menuEffectiveLastIndexMap = null;

	/**
	 * @beaninfo (rwb)
	 */
	private String identifier;

	/**
	 * Default constructor. 
	 */
	public CytoscapeMenuBar() {
		menuEffectiveLastIndexMap = new HashMap<JMenu,Integer>();
		menuMap = new HashMap<String,JMenu>();
		actionMenuItemMap = new HashMap<Action,JMenuItem>();

		// Load the first menu, just to please the layouter. Also make sure the
		// menu bar doesn't get too small.
		// "File" is always first
		setMinimumSize(getMenu("File").getPreferredSize());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param menu_name DOCUMENT ME!
	 */
	public void setDefaultMenuSpecifier(String menu_name) {
		// TODO: If the existing menu exists, should we rename it?
		defaultMenuSpecifier = menu_name;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDefaultMenuSpecifier() {
		return defaultMenuSpecifier;
	}

	/**
	 * If the given Action has a present and false inMenuBar property, return;
	 * otherwise delegate to addAction( String, Action ) with the value of its
	 * preferredMenu property, or null if it does not have that property.
	 */
	public boolean addAction(CyAction action) {
		return addAction(action, NO_INDEX);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param action DOCUMENT ME!
	 * @param index DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean addAction(CyAction action, int index) {
		String menu_name = null;

			if (action.isInMenuBar()) {
				menu_name = action.getPreferredMenu();
			} else {
				return false;
			}

		if (index != NO_INDEX) {
			action.setPreferredIndex(index);
		}

		return addAction(menu_name, action);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param menu_name DOCUMENT ME!
	 * @param action DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	private boolean addAction(String menu_name, CyAction action) {
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

		// If it wants to be anywhere in particular, try to put it there..
		Object index_object = Integer.valueOf(-1);

		index_object = action.getPrefferedIndex();

		if (action.isAccelerated() && (action.getValue(Action.ACCELERATOR_KEY) == null)) {
			KeyStroke keyStroke = KeyStroke.getKeyStroke(action.getKeyCode(), action.getKeyModifiers());
			menu_item.setAccelerator(keyStroke);
		}
			
		menu.addMenuListener(action);

		if (index_object != null) {
			int index = -1;

			if (index_object instanceof Integer) {
				index = ((Integer) index_object).intValue();
			} else if (index_object instanceof String) {
				try {
					index = Integer.parseInt((String) index_object);
				} catch (NumberFormatException e) {
					System.err.println("WARNING: The action " + action
					                   + " has an \"index\" property but its String value" 
									   + " cannot be converted to an int.  Ignoring.");
					index_object = null;
				}
			} else {
				System.err.println("WARNING: The action " + action
				                   + " has an \"index\" property but its value is neither" 
								   + " an Integer nor a String.  Ignoring.");
				index_object = null;
			}

			if (index_object != null) {
				if (index < 0) {
					index = (menu.getItemCount() + (index + 1));

					if (index < 0) {
						index = 0;
					} else {
						Integer effective_last_index = (Integer) menuEffectiveLastIndexMap.get(menu);

						if (effective_last_index == null) {
							menuEffectiveLastIndexMap.put(menu, Integer.valueOf(index));
							index += 1;
						} else if (effective_last_index.intValue() >= index) {
							menuEffectiveLastIndexMap.put(menu, Integer.valueOf(index));
						}
					}
				}

				menu.insert(menu_item, index);
			}
		}

		if (index_object == null) {
			boolean added_it = false;

			Integer effective_last_index = (Integer) menuEffectiveLastIndexMap.get(menu);

			if (effective_last_index != null) {
				menu.insert(menu_item, effective_last_index.intValue());
				menuEffectiveLastIndexMap.put(menu, Integer.valueOf(effective_last_index.intValue() + 1));
				added_it = true;
			}

			if (!added_it) {
				menu.add(menu_item);
			}
		}

		actionMenuItemMap.put(action, menu_item);

		return true;
	} 

	/**
	 * If the given Action has a present and false inMenuBar property, return;
	 * otherwise if there's a menu item for the action, remove it. Its menu is
	 * determined my its preferredMenu property if it is present; otherwise by
	 * defaultMenuSpecifier.
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
			menu_name = defaultMenuSpecifier;
		}

		getMenu(menu_name).remove(menu_item);

		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param menu_string DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JMenu getMenu(String menu_string) {
		return getMenu(menu_string, -1);
	}

	/**
	 * @return the menu named in the given String. The String may contain
	 *         multiple menu names, separated by dots ('.'). If any contained
	 *         menu name does not correspond to an existing menu, then that menu
	 *         will be created as a child of the menu preceeding the most recent
	 *         dot or, if there is none, then as a child of this MenuBar.
	 */
	public JMenu getMenu(String menu_string, int parentPosition) {
		if (menu_string == null) {
			menu_string = getDefaultMenuSpecifier();
		}

		StringTokenizer st = new StringTokenizer(menu_string, ".");
		String menu_token;
		JMenu parent_menu = null;
		JMenu menu = null;

		while (st.hasMoreTokens()) {
			menu_token = (String) st.nextToken();

			if (menuMap.containsKey(menu_token)) {
				parent_menu = (JMenu) menuMap.get(menu_token);
			} else {
				menu = createJMenu(menu_token);

				if (parent_menu == null) {
					this.add(menu);
					invalidate();
				} else {
					parent_menu.add(menu, parentPosition);
				}

				menuMap.put(menu_token, menu);
				parent_menu = menu;
			}
		}

		if (menu == null) {
			return parent_menu;
		}

		return menu;
	}

	/**
	 * CytoscapeMenuBars are unique -- this equals() method returns true iff the
	 * other object == this.
	 */
	public boolean equals(Object other_object) {
		return (this == other_object);
	}

	/**
	 * implements CommunityMember
	 *
	 * @return  identifier 
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * imlements Reidentifiable
	 * @beaninfo (rwb)
	 */
	public void setIdentifier(String new_identifier) {
		if (identifier == null) {
			if (new_identifier == null) {
				return;
			}
		} else if (new_identifier != null) {
			if (identifier.equals(new_identifier)) {
				return;
			}
		}

		String old_identifier = identifier;
		identifier = new_identifier;
		firePropertyChange("identifier", old_identifier, new_identifier);
	}

	/**
	 * imlements Reidentifiable
	 * @return true (always)
	 */
	public boolean isReidentificationEnabled() {
		return true;
	}

	/**
	 * Delegates to {@link #getIdentifier()}.
	 */
	public String toString() {
		return getIdentifier();
	}

	/**
	 * Factory method for instantiating objects of type JMenu
	 */
	public JMenu createJMenu(String title) {
		JMenu menu = new JMenu(title);
		revalidate();
		repaint();

		return menu;
	}

	/**
	 * Factory method for instantiating the menuItems in the menu. 
	 */
	protected JMenuItem createJMenuItem(CyAction action) {
		if ( action.useCheckBoxMenuItem() )
			return new JCheckBoxMenuItem(action);

		return new JMenuItem(action);
	}

	public JMenuBar getJMenuBar() {
		return this;
	}
}
