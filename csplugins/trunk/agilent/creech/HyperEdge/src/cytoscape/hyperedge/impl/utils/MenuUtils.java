/* -*-Java-*-
********************************************************************************
*
* File:         MenuUtils.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/utils/MenuUtils.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:  
* Author:       Michael L. Creech
* Created:      Sat Jun 18 16:23:41 2005
* Modified:     Sat Jun 18 16:23:42 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:      
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
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
 * @author Michael L. Creech
 * @version 1.0
 */
public class MenuUtils
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Add a separator to a menu with a given name, if it exists.
     * @param menu_name non-null name of the menu to which to add a separator.
     * @param menu_bar the JMenuBar to search within. May be null.
     * @return true if a separator was added. false otherwise.
     */
    static public boolean addSeparator (String   menu_name,
                                        JMenuBar menu_bar)
    {
        JMenu result = findMenu (menu_name, menu_bar);
        if (result != null)
        {
            result.add (new JSeparator());
            return true;
        }
        return false;
    }

    /**
     * Return a JMenu with a given name. If it doesn't exist on the
     * JMenuBar, then create it and add it to the menu bar.
     * @param menu_name non-null name of the menu to find or create.
     * @param menu_bar the JMenuBar to search within. May be null.
     */
    static public JMenu addMenuWhenNecessary (String   menu_name,
                                              JMenuBar menu_bar)
    {
        JMenu result = findMenu (menu_name, menu_bar);
        if (result == null)
        {
            result = new JMenu(menu_name);
            if (menu_bar != null)
            {
                menu_bar.add (result);
            }
        }
        return result;
    }

    /**
     * Return a JMenu with a given name. If it doesn't exist on the
     * JMenuBar, then create it.
     * @param menu_name non-null name of the menu to find or create.
     * @param menu_bar the JMenuBar to search within. May be null.
     */
    static public JMenu getMenu (String   menu_name,
                                 JMenuBar menu_bar)
    {
        JMenu result = findMenu (menu_name, menu_bar);
        if (result == null)
        {
            result = new JMenu(menu_name);
        }
        return result;
    }

    /**
     * Return The JMenu with a given name that exists on
     * a given JMenuBar.
     * @param menu_name non-null name of the menu to find.
     * @param menu_bar the JMenuBar to search within. May be null.
     */
    static public JMenu findMenu (String   menu_name,
                                  JMenuBar menu_bar)
    {
        HEUtils.notNull (menu_name, "menu_name");
        if (menu_bar == null)
        {
            return null;
        }
        JMenu pos_match;
        for (int i = 0; i < menu_bar.getMenuCount (); i++)
        {
            pos_match = menu_bar.getMenu (i);
            // may not have a name (as in a JMenuItem):
            if (pos_match == null)
            {
                continue;
            }
            if (menu_name.equals (pos_match.getText ()))
            {
                return pos_match;
            }
        }
        return null;
    }

    /**
     * Return The JMenuItem with a given name that exists on
     * a given JMenu.
     * @param menu_item_name non-null name of the JMenuItem to find.
     * @param menu the JMenu to search within. May be null.
     */
    static public JMenuItem findMenuItem (String menu_item_name,
                                          JMenu  menu)
    {
        HEUtils.notNull (menu_item_name, "menu_item_name");
        if (menu == null)
        {
            return null;
        }
        JMenuItem pos_match;
        for (int i = 0; i < menu.getItemCount (); i++)
        {
            pos_match = menu.getItem (i);
            // may not have a name (as in a JSeparator):
            if (pos_match == null)
            {
                continue;
            }
            if (menu_item_name.equals (pos_match.getText ()))
            {
                return pos_match;
            }
        }
        return null;
    }

    /**
     * Remove a JMenu with a given name that exists on
     * a given JMenuBar.
     * @param menu_name non-null name of the JMenu to find.
     * @param menu_bar the JMenuBar to search within. May be null.
     * @return the JMenu that was removed, null if not found or
     * the menu_bar is null.
     */
    static public JMenu removeMenu (String   menu_name,
                                    JMenuBar menu_bar)
    {
        HEUtils.notNull (menu_name, "menu_item_name");
        if (menu_bar == null)
        {
            return null;
        }
        JMenu pos_match;
        for (int i = 0; i < menu_bar.getMenuCount (); i++)
        {
            pos_match = menu_bar.getMenu (i);
            // may not have a name (as in a JMenuItem):
            if (pos_match == null)
            {
                continue;
            }
            if (menu_name.equals (pos_match.getText ()))
            {
                menu_bar.remove (pos_match);
                return pos_match;
            }
        }
        return null;
    }

    /**
     * Remove a JMenuItem with a given name that exists on
     * a given JMenu.
     * @param menu_item_name non-null name of the JMenuItem to find.
     * @param menu the JMenu to search within. May be null.
     * @return the JMenuItem that was removed, null if not found or
     * the menu is null.
     */
    static public JMenuItem removeMenuItem (String menu_item_name,
                                            JMenu  menu)
    {
        HEUtils.notNull (menu_item_name, "menu_item_name");
        if (menu == null)
        {
            return null;
        }
        JMenuItem pos_match;
        // String    pos_match_name;
        for (int i = 0; i < menu.getItemCount (); i++)
        {
            pos_match = menu.getItem (i);
            // may not have a name (as in a JSeparator):
            if (pos_match == null)
            {
                continue;
            }
            if (menu_item_name.equals (pos_match.getText ()))
            {
                menu.remove (pos_match);
                return pos_match;
            }
        }
        return null;
    }

    /**
     * Add a Component after a given other Component within a menu.
     * This is usually a JMenuItem or JSeparator
     * @param to_add the Component to add to this menu. It is usually a
     *               JMenuItem or JSeparator.
     * @param match the Component in which to add to_add before or after.
     *              It is usually a JMenuItem or JSeparator.
     * @param menu the JMenu in which the addition is to take place.
     * @param add_before if true, to_add is added before match. If false,
     *                   to_add is added after match.
     */

    // this allows adding of JSeperators as well as JMenus.
    static public boolean addItem (Component to_add,
                                   Component match,
                                   JMenu     menu,
                                   boolean   add_before)
    {
        if ((menu == null) || (match == null) || (to_add == null))
        {
            return false;
        }
        for (int i = 0; i < menu.getItemCount (); i++)
        {
            if (menu.getMenuComponent (i) == match)
            {
                if (add_before)
                {
                    menu.add (to_add, i);
                }
                else
                {
                    menu.add (to_add, i + 1);
                }
                return true;
            }
        }
        return false;
    }
}
