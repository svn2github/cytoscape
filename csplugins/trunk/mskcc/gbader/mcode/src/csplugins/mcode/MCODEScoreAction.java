/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 ** Date: Nov 14, 2003
 ** Time: 3:48:51 PM
 ** Description: Action code for PathDistPlugin
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

package csplugins.mcode;

import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import giny.model.GraphPerspective;
import giny.view.GraphView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MCODEScoreAction implements ActionListener {
	/**
	 * Cytoscape Window.
	 */
	private CyWindow cyWindow;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public MCODEScoreAction(CyWindow cyWindow) {
		this.cyWindow = cyWindow;
	}

	/**
	 * This method is called when the user selects the menu item.
	 * @param event Menu Item Selected.
	 */
	public void actionPerformed(ActionEvent event) {
		//get the graph view object from the window.
		GraphView graphView = cyWindow.getView();
		//get the network object; this contains the graph
		CyNetwork network = cyWindow.getNetwork();
		//can't continue if either of these is null
		if (graphView == null || network == null) {
			return;
		}

		//inform listeners that we're doing an operation on the network
		String callerID = "MCODEScoreAction.actionPerformed";
		network.beginActivity(callerID);
		//this is the graph structure; it should never be null,
		GraphPerspective gpInputGraph = network.getGraphPerspective();
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ":");
			System.err.println("Unexpected null gpInputGraph in network.");
			network.endActivity(callerID);
			return;
		}
		//and the view should be a view on this structure
		if (graphView.getGraphPerspective() != gpInputGraph) {
			System.err.println("In " + callerID + ":");
			System.err.println("Graph view is not a view on network's graph perspective.");
			network.endActivity(callerID);
			return;
		}

		if (gpInputGraph.getNodeCount() < 1) {
			JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
			        "You must have a network loaded to run this plugin.");
			network.endActivity(callerID);
			return;
		}

		//run MCODE scoring algorithm - node scores are saved as node attributes
		long msTimeBefore = System.currentTimeMillis();
		MCODE.getInstance().alg.scoreGraph(gpInputGraph, network.getNodeAttributes());
		long msTimeAfter = System.currentTimeMillis();
		JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
		        "Network was scored in " + (msTimeAfter - msTimeBefore) + " ms.");

		//and tell listeners that we're done
		network.endActivity(callerID);
	}
}
