/*
 File: MenuGravityTracker.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.application.swing;


import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** This class inserts menu items and separators in a menu based on their "gravity".
 *  Items with higher gravity appear further down the menu.
 */
public final class MenuGravityTracker {
	private final static Logger logger = LoggerFactory.getLogger(MenuGravityTracker.class);
	private final JMenu menu;
	private final Map<Component, Double> componentGravity;

	public MenuGravityTracker(final JMenu menu) {
		this.menu = menu;
		this.componentGravity = new HashMap<Component, Double>();
	}

	public void addMenuItem(final JMenuItem newMenuItem, final double gravity) {
                final int index = getInsertLocation(menu, gravity);
                logger.info("Inserted menu item: " + newMenuItem +
                            " with gravity: " + gravity);
                menu.insert(newMenuItem, index);
	}

	public void addMenu(final JMenu newSubmenu, final double gravity) {
                final int index = getInsertLocation(menu, gravity);
                logger.info("Inserted menu: " + newSubmenu +
                            " with gravity: " + gravity);
                menu.insert(newSubmenu, index);
	}

	public void addMenuSeparator(final double gravity) {
                final int index = getInsertLocation(menu, gravity);
                menu.insertSeparator(index);
                final Component separator = menu.getMenuComponent(index);
                componentGravity.put(separator, gravity);
                logger.info("Inserted menu separator with gravity: " + gravity);
	}

        private int getInsertLocation(final JMenu menu, final double newGravity) {
                for (int i = 0; i < menu.getMenuComponentCount(); ++i) {
                        final Component item = menu.getMenuComponent(i);
                        if (componentGravity.containsKey(item)) {
                                if (newGravity < componentGravity.get(item))
                                        return i;
                        }
                }
                return menu.getMenuComponentCount();
        }
}
