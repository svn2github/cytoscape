/** Copyright (c) 2004 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Robert Sheridan
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 ** Date: January 19.2004
 ** Description: Hierarcical layout plugin, based on techniques by Sugiyama
 ** et al. described in chapter 9 of "graph drawing", Di Battista et al,1999
 **
 ** Based on the csplugins.tutorial written by Ethan Cerami and GINY plugin
 ** written by Andrew Markiel
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

package csplugins.hierarchicallayout;

import cytoscape.AbstractPlugin;
import cytoscape.view.CyWindow;
import javax.swing.*;

/**
 * Registers the plugin menu item.
 * This class simply adds a menu choice to the plug-in menu, and
 * assigns a listener.<br>
 * This layout approximates an optimal layout for nodes which have
 * a tree like relationship.
 * @see <a href="HierarchicalLayoutListener.html">HierarchicalLayoutListener</a>
 * @author Robert Sheridan
 */
public class HierarchicalLayoutPlugin extends AbstractPlugin {

    /**
     * Adds a menuitem to the plugin menu of the provided Cytoscape Window.
     * Creates and registers a HierarchicalLayoutListener.
     * @param cyWindow Main Cytoscape Window object.
     */
    public HierarchicalLayoutPlugin(CyWindow cyWindow) {
		HierarchicalLayoutListener listener = new HierarchicalLayoutListener(cyWindow);
	    JMenu menu = cyWindow.getCyMenus().getOperationsMenu();
	    JMenuItem item = new JMenuItem("Hierarchical Layout");
	    item.addActionListener(listener);
	    menu.add(item);
    }

    /**
     * Describes the plug in.
     * @return short plug in description.
     */
    public String describe() {
        return new String("Lay out selected nodes using an algorithm designed for graphs "
		+ "with a strong hierarchical organization. If no nodes are selected the "
		+ "entire graph will be layed out");
    }
}
