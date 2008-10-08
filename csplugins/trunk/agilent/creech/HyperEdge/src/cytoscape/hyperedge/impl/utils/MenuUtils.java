
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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


package cytoscape.hyperedge.impl.utils;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 * Various menu utilities for finding, adding, and removing menus and menu
 * items.
 * 
 * @author Michael L. Creech
 * @version 1.0
 */
public final class MenuUtils {
    private static final String MENU_ITEM_PARAM_NAME = "menuItemName";
    // Don't want people to manipulate the utility class constructor.
    private MenuUtils() {}
    // ~ Methods
    // ////////////////////////////////////////////////////////////////

    /**
     * Add a separator to a menu with a given name, if it exists.
     * 
     * @param menuName
     *            non-null name of the menu to which to add a separator.
     * @param menuBar
     *            the JMenuBar to search within. May be null.
     * @return true if a separator was added. false otherwise.
     */
    public static boolean addSeparator(final String menuName, final JMenuBar menuBar) {
	final JMenu result = findMenu(menuName, menuBar);
	if (result != null) {
	    result.add(new JSeparator());
	    return true;
	}
	return false;
    }

    /**
     * Return a JMenu with a given name. If it doesn't exist on the JMenuBar,
     * then create it and add it to the menu bar.
     * 
     * @param menuName
     *            non-null name of the menu to find or create.
     * @param menuBar
     *            the JMenuBar to search within. May be null.
     * @return the JMenu if it already exists, or a new one if it doesn't exist.
     */
    public static JMenu addMenuWhenNecessary(final String menuName, final JMenuBar menuBar) {
	JMenu result = findMenu(menuName, menuBar);
	if (result == null) {
	    result = new JMenu(menuName);
	    if (menuBar != null) {
		menuBar.add(result);
	    }
	}
	return result;
    }

    /**
     * Return a JMenu with a given name. If it doesn't exist on the JMenuBar,
     * then create it.
     * 
     * @param menuName
     *            non-null name of the menu to find or create.
     * @param menuBar
     *            the JMenuBar to search within. May be null.
     * @return a JMenu with a given name. Will create a new one, if it doesn't exist.
     */
    public static JMenu getMenu(final String menuName, final JMenuBar menuBar) {
	JMenu result = findMenu(menuName, menuBar);
	if (result == null) {
	    result = new JMenu(menuName);
	}
	return result;
    }

    /**
     * 
     * 
     * @param menuName
     *            non-null name of the menu to find.
     * @param menuBar
     *            the JMenuBar to search within. May be null.
     * @return the JMenu with a given name that exists on a given JMenuBar.
     */
    public static JMenu findMenu(final String menuName, final JMenuBar menuBar) {
	HEUtils.notNull(menuName, "menuName");
	if (menuBar == null) {
	    return null;
	}
	JMenu posMatch;
	for (int i = 0; i < menuBar.getMenuCount(); i++) {
	    posMatch = menuBar.getMenu(i);
	    // may not have a name (as in a JMenuItem):
	    if (posMatch == null) {
		continue;
	    }
	    if (menuName.equals(posMatch.getText())) {
		return posMatch;
	    }
	}
	return null;
    }

    /**
     * @param menuItemName
     *            non-null name of the JMenuItem to find.
     * @param menu
     *            the JMenu to search within. May be null.
     * @return the JMenuItem with a given name that exists on a given JMenu.
     */
    public static JMenuItem findMenuItem(final String menuItemName, final JMenu menu) {
	HEUtils.notNull(menuItemName, MENU_ITEM_PARAM_NAME);
	if (menu == null) {
	    return null;
	}
	JMenuItem posMatch;
	for (int i = 0; i < menu.getItemCount(); i++) {
	    posMatch = menu.getItem(i);
	    // may not have a name (as in a JSeparator):
	    if (posMatch == null) {
		continue;
	    }
	    if (menuItemName.equals(posMatch.getText())) {
		return posMatch;
	    }
	}
	return null;
    }

    /**
     * Remove a JMenu with a given name that exists on a given JMenuBar.
     * 
     * @param menuName
     *            non-null name of the JMenu to find.
     * @param menuBar
     *            the JMenuBar to search within. May be null.
     * @return the JMenu that was removed, null if not found or the menuBar is
     *         null.
     */
    public static JMenu removeMenu(final String menuName, final JMenuBar menuBar) {
	HEUtils.notNull(menuName, MENU_ITEM_PARAM_NAME);
	if (menuBar == null) {
	    return null;
	}
	JMenu posMatch;
	for (int i = 0; i < menuBar.getMenuCount(); i++) {
	    posMatch = menuBar.getMenu(i);
	    // may not have a name (as in a JMenuItem):
	    if (posMatch == null) {
		continue;
	    }
	    if (menuName.equals(posMatch.getText())) {
		menuBar.remove(posMatch);
		return posMatch;
	    }
	}
	return null;
    }

    /**
     * Remove a JMenuItem with a given name that exists on a given JMenu.
     * 
     * @param menuItemName
     *            non-null name of the JMenuItem to find.
     * @param menu
     *            the JMenu to search within. May be null.
     * @return the JMenuItem that was removed, null if not found or the menu is
     *         null.
     */
    public static JMenuItem removeMenuItem(final String menuItemName, final JMenu menu) {
	HEUtils.notNull(menuItemName, MENU_ITEM_PARAM_NAME);
	if (menu == null) {
	    return null;
	}
	JMenuItem posMatch;
	// String posMatch_name;
	for (int i = 0; i < menu.getItemCount(); i++) {
	    posMatch = menu.getItem(i);
	    // may not have a name (as in a JSeparator):
	    if (posMatch == null) {
		continue;
	    }
	    if (menuItemName.equals(posMatch.getText())) {
		menu.remove(posMatch);
		return posMatch;
	    }
	}
	return null;
    }

    /**
     * Add a Component after a given other Component within a menu. This is
     * usually a JMenuItem or JSeparator
     * 
     * @param toAdd
     *            the Component to add to this menu. It is usually a JMenuItem
     *            or JSeparator.
     * @param match
     *            the Component in which to add toAdd before or after. It is
     *            usually a JMenuItem or JSeparator.
     * @param menu
     *            the JMenu in which the addition is to take place.
     * @param addBefore
     *            if true, to_add is added before match. If false, to_add is
     *            added after match.
     * @return true if match is found and toAdd is added.
     */

    // this allows adding of JSeperators as well as JMenus.
    public static boolean addItem(final Component toAdd, final Component match,
	    final JMenu menu, final boolean addBefore) {
	if ((menu == null) || (match == null) || (toAdd == null)) {
	    return false;
	}
	for (int i = 0; i < menu.getItemCount(); i++) {
	    if (menu.getMenuComponent(i) == match) {
		if (addBefore) {
		    menu.add(toAdd, i);
		} else {
		    menu.add(toAdd, i + 1);
		}
		return true;
	    }
	}
	return false;
    }
}
