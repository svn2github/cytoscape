/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Gary Bader
 ** Date: Nov.14.2003
 ** Description: Cytoscape Plug In that calculates a path length distribution for the selected nodes.
 ** If no nodes are selected the calculation will be based on the entire graph.
 **
 ** Based on the csplugins.tutorial written by Ethan Cerami
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
package csplugins.pathdist;

import cytoscape.AbstractPlugin;
import cytoscape.view.CyWindow;
import javax.swing.*;

/**
 * Path Length Distribution Plug In.
 * Calculates the path length distribution of a graph.
 *
 * @author Gary Bader
 */
public class PathDistPlugin extends AbstractPlugin {
    private CyWindow cyWindow;

    /**
     * Constructor.
     * @param cyWindow Main Cytoscape Window object.
     */
    public PathDistPlugin(CyWindow cyWindow) {
		PathDistAction action = new PathDistAction(cyWindow);
	    JMenu menu = cyWindow.getCyMenus().getOperationsMenu();
	    JMenuItem item = new JMenuItem("Path Distribution");
	    item.addActionListener(action);
	    menu.add(item);
    }

    /**
     * Describes the plug in.
     * @return short plug in description.
     */
    public String describe() {
        return new String("Calculates a path length distribution for the selected nodes."+
                "If no nodes are selected the calculation will be based on the entire graph");
    }
}