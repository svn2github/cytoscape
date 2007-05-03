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

import cytoscape.CytoscapeInit;
import cytoscape.layout.algorithms.GridNodeLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * CyLayouts is a singleton class that is used to register all available
 * layout algorithms.  
 */
public class CyLayouts {
	private static HashMap<String, LayoutAlgorithm> layoutMap;
	private static HashMap<LayoutAlgorithm, String> menuNameMap;

	static {
		new CyLayouts();
	}

	private CyLayouts() {
		layoutMap = new HashMap<String,LayoutAlgorithm>();
		menuNameMap = new HashMap<LayoutAlgorithm,String>();

		addLayout(new GridNodeLayout(), "Cytoscape Layouts");
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
		layoutMap.put(layout.getName(),layout);
		menuNameMap.put(layout,menu);
	}

	/**
	 * Remove a layout from the layout maanger's list.
	 *
	 * @param layout The layout to remove
	 */
	public static void removeLayout(LayoutAlgorithm layout) {
		layoutMap.remove(layout.getName());
		menuNameMap.remove(layout);
	}

	/**
	 * Get the layout named "name".  If "name" does
	 * not exist, this will return null
	 *
	 * @param name String representing the name of the layout
	 * @return the layout of that name or null if it is not reigstered
	 */
	public static LayoutAlgorithm getLayout(String name) {
		return layoutMap.get(name);
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
	 * Get the default layout.  This is either the grid layout or a layout
	 * chosen by the user via the setting of the "layout.default" property.
	 *
	 * @return LayoutAlgorithm to use as the default layout algorithm
	 */
	public static LayoutAlgorithm getDefaultLayout() {
		// See if the user has set the layout.default property
		String defaultLayout = CytoscapeInit.getProperties().getProperty("layout.default");

		if ((defaultLayout == null) || !layoutMap.containsKey(defaultLayout)) {
			defaultLayout = "grid";
		}

		LayoutAlgorithm l = layoutMap.get(defaultLayout);
		System.out.println("getDefaultLayout returning " + l);

		// Nope, so return the grid layout 
		return l;
	}

	// Ack.
	public static String getMenuName(LayoutAlgorithm layout) {
		return menuNameMap.get(layout); 
	}
}
