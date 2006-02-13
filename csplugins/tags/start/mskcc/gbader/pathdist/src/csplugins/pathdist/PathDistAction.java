/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
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
 **
 ** User: Gary Bader
 ** Date: Nov 14, 2003
 ** Time: 3:48:51 PM
 **/

package csplugins.pathdist;

import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import giny.model.GraphPerspective;
import giny.util.NodeDistances;
import giny.view.GraphView;
import giny.view.NodeView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class PathDistAction implements ActionListener {
	/**
	 * Cytoscape Window.
	 */
	private CyWindow cyWindow;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public PathDistAction(CyWindow cyWindow) {
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
		String callerID = "PathDistAction.actionPerformed";
		network.beginActivity(callerID);
		//this is the graph structure; it should never be null,
		GraphPerspective graphPerspective = network.getGraphPerspective();
		if (graphPerspective == null) {
			System.err.println("In " + callerID + ":");
			System.err.println("Unexpected null graphPerspective in network.");
			network.endActivity(callerID);
			return;
		}
		//and the view should be a view on this structure
		if (graphView.getGraphPerspective() != graphPerspective) {
			System.err.println("In " + callerID + ":");
			System.err.println("Graph view is not a view on network's graph perspective.");
			network.endActivity(callerID);
			return;
		}

		//Select all nodes as the default action if none are selected
		List nodeList;

		if(graphPerspective.getNodeCount() > 0) {
			nodeList = graphPerspective.nodesList();
		} else {
			JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
			        "You must have a network loaded to run this plugin.");
			network.endActivity(callerID);
			return;
		}
		if (graphView.getSelectedNodes().size() == 1) {
			JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
			        "A selection must contain at least 2 nodes.");
			network.endActivity(callerID);
			return;
		}
		//take List of nodes and pass to NodeDistance util in giny.util
		NodeDistances apspResult = new NodeDistances(nodeList, null, graphPerspective);
		int[][] distanceMatrix = (int[][]) apspResult.construct();
		if (distanceMatrix == null) {
			System.err.println("In " + callerID + ":");
			System.err.println("Path length calculation failed.");
			network.endActivity(callerID);
			return;
		}
		//Calculate the distribution
		//initialize
		List nodeViewList=null;
		int nodeCountMax = 0;
		int nodeCount = nodeList.size();
		int distribution[] = new int[nodeCount];
		Arrays.fill(distribution, 0);
		//calculate
		if (graphView.getSelectedNodes().size() != 0) {
			//node subset was selected, only calculate distribution for this subset
			nodeViewList = graphView.getSelectedNodes();
			//create a hashset of all node indices that were selected.
			HashSet hash = new HashSet();
			for(Iterator i=nodeViewList.iterator(); i.hasNext(); ) {
				NodeView nv = (NodeView)i.next();
				hash.add(new Integer(graphPerspective.getIndex(nv.getNode())-1));
			}
			//only look at a matrix triangle without the diagonal
			for (int i = 0; i < nodeCount; i++) {
				for (int j = (i + 1); j < nodeCount; j++) {
					if ((distanceMatrix[i][j] < Integer.MAX_VALUE)&&
					        (hash.contains(new Integer(i))&&(hash.contains(new Integer(j))))) {
						distribution[distanceMatrix[i][j]]++;
						if (nodeCountMax < distanceMatrix[i][j]) {
							nodeCountMax = distanceMatrix[i][j];
						}
					}
				}
			}
		}
		else {
			//entire graph was used as input
			//only look at a matrix triangle without the diagonal
			for (int i = 0; i < nodeCount; i++) {
				for (int j = (i + 1); j < nodeCount; j++) {
					if (distanceMatrix[i][j] < Integer.MAX_VALUE) {
						distribution[distanceMatrix[i][j]]++;
						if(nodeCountMax<distanceMatrix[i][j]) {
							nodeCountMax = distanceMatrix[i][j];
						}
					}
				}
			}
		}
		//output
		//copy temp distribution to final for output
        for(int i=1;i<=nodeCountMax;i++){
			System.err.println(i+" "+distribution[i]);
		}

		//and tell listeners that we're done
		network.endActivity(callerID);
	}
}
