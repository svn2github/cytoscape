// $Id: CyNetworkViewUtil.java,v 1.12 2006/07/21 17:05:28 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.util.cytoscape;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.view.CyNetworkView;

import ding.view.DGraphView;

import giny.view.NodeView;

import legacy.layout.algorithm.MutablePolyEdgeGraphLayout;

import legacy.layout.impl.SpringEmbeddedLayouter2;

import legacy.util.GraphConverter;

import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;

import java.util.Iterator;

import javax.swing.*;


/**
 * A Utility Class for Creating CyNetwork Views.
 *
 * @author Ethan Cerami
 */
public class CyNetworkViewUtil {
	/**
	 * Creates a CyNetworkView from the specified CyNetwork.
	 *
	 * @param cyNetwork           CyNetwork Object.
	 * @param taskMonitor         TaskMonitor Object.
	 * @param executeSpringLayout Flag to Execute Spring Layout.
	 * @param applyVisualStyle    Flag to Apply Current Visual Style.
	 */
	public static void createNetworkView(CyNetwork cyNetwork, TaskMonitor taskMonitor,
	                                     boolean executeSpringLayout, boolean applyVisualStyle) {
		// hack to make sure progress bar get set to 100%
		// after network creation is complete
		taskMonitor.setPercentCompleted(100);

		//  Conditionally Create Network View
		if (cyNetwork.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties()
		                                                             .getProperty("viewThreshold"))) {
			taskMonitor.setStatus("Creating Network View");
			taskMonitor.setPercentCompleted(-1);

			CyNetworkView networkView = createCyNetworkView(cyNetwork, applyVisualStyle);

			//  Execute the Spring Embedder Layout Algorithm
			if (executeSpringLayout) {
				taskMonitor.setStatus("Executing Spring Layout...");
				executeLayout(networkView, taskMonitor);
			}

			if (applyVisualStyle) {
				taskMonitor.setStatus("Applying Visual Styles");
				Cytoscape.getVisualMappingManager().applyAppearances();
				MapNodeAttributes.customNodes(networkView);
			}

			//  Lastly, make the GraphView Canvas Visible.
			//  After everthing is done, show the network view instantly.
			SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DingNetworkView view = (DingNetworkView) Cytoscape.getCurrentNetworkView();
						view.setGraphLOD(new CyGraphLOD());
						((DGraphView) view).fitContent();
					}
				});
		}
	}

	/**
	 * Creates the CyNetworkView.
	 * Most of this code is copied directly from Cytoscape.createCyNetworkView.
	 * However, it requires a bit of a hack to actually hide the network
	 * view from the user, and I didn't want to use this hack in the core
	 * Cytoscape.java class.
	 *
	 * @param cyNetwork        CyNetwork
	 * @param applyVisualStyle Flag to Apply Current Visual Style.
	 * @return CyNetworkView
	 */
	private static CyNetworkView createCyNetworkView(CyNetwork cyNetwork, boolean applyVisualStyle) {
		final DingNetworkView view = new DingNetworkView(cyNetwork, cyNetwork.getTitle());

		view.setIdentifier(cyNetwork.getIdentifier());
		Cytoscape.getNetworkViewMap().put(cyNetwork.getIdentifier(), view);
		view.setTitle(cyNetwork.getTitle());

		if (applyVisualStyle) {
			view.setVisualStyle(BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE);
		}

		// set the selection mode on the view
		Cytoscape.setSelectionMode(Cytoscape.getSelectionMode(), view);

		Cytoscape.firePropertyChange(cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, null,
		                             view);

		// outta here
		return view;
	}

	/**
	 * Executes the Spring Embedded Layout2.
	 *
	 * @param networkView CyNetworkView
	 * @param taskMonitor TaskMonitor
	 */
	private static void executeLayout(CyNetworkView networkView, TaskMonitor taskMonitor) {
		// move a network node to jumpstart the SpringEmbeddedLayouter2
		Iterator i = networkView.getNetwork().nodesIterator();

		if (i.hasNext()) {
			CyNode node = (CyNode) i.next();
			NodeView nodeView = networkView.getNodeView(node);
			double xPos = nodeView.getXPosition();
			double yPos = nodeView.getYPosition();
			nodeView.setXPosition(xPos + 2500);
			nodeView.setYPosition(yPos + 2500);
		}

		final MutablePolyEdgeGraphLayout nativeGraph = GraphConverter.getGraphCopy(0.0d, false,
		                                                                           false);

		Task task = new SpringEmbeddedLayouter2(nativeGraph);
		task.setTaskMonitor(taskMonitor);
		task.run();
		GraphConverter.updateCytoscapeLayout(nativeGraph);
	}
}
