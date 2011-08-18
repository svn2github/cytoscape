/*
 File: PluginMenuListener.java

 Copyright (c) 2011, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.util.swing;

import java.util.Arrays;
import java.util.Comparator;

import java.awt.Component;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * This class is called from CyMenus to sort the plugins menu to make it
 * easier to find a particular plugin from a long list.
 */
public class SwingMenuSorter implements MenuListener {
	JMenu pluginMenu;
	int sepCount = 0;

	public SwingMenuSorter(JMenu p, int sepCount) {
		pluginMenu = p;
		this.sepCount = sepCount;
	}

	public void menuCanceled(MenuEvent e) {}
	public void menuDeselected(MenuEvent e) {}
	public void menuSelected(MenuEvent e) {
		// Get the list of Components
		Component[] components = pluginMenu.getMenuComponents();
		int sepFound = 0;
		int userMenuStart = 0;
		for (int i = 0; (i < components.length) && (sepFound < sepCount); i++) {
			if (components[i] instanceof JSeparator) {
				sepFound++;
				userMenuStart = i+1;
			}
		}

		JMenuItem[] userMenus = new JMenuItem[components.length-userMenuStart];
		int j = 0;
		for (int i = userMenuStart; i < components.length; i++) {
			userMenus[j++] = (JMenuItem)components[i];
		}

		// OK, now sort and put back in
		Arrays.sort(userMenus, new MenuComparator());
		j = 0;
		for (int i = userMenuStart; i < components.length; i++) {
			pluginMenu.add(userMenus[j++],i);
		}

	}

	class MenuComparator implements Comparator {
		public int compare(Object o1, Object o2) {
		JMenuItem j1 = (JMenuItem)o1;
		JMenuItem j2 = (JMenuItem)o2;
		return j1.getText().compareTo(j2.getText());
		}

		public boolean equals(Object o1, Object o2) {
			JMenuItem j1 = (JMenuItem)o1;
			JMenuItem j2 = (JMenuItem)o2;
			return j1.getText().equals(j2.getText());
		}
	}
}



