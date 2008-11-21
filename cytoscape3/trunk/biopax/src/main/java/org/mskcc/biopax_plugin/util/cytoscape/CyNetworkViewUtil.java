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

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.ding.CyGraphLOD;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import legacy.layout.algorithm.MutablePolyEdgeGraphLayout;
import legacy.layout.impl.SpringEmbeddedLayouter2;
import legacy.util.GraphConverter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.view.NodeView;
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;

import javax.swing.*;
import java.util.Iterator;


/**
 * A Utility Class for Creating GraphPerspective Views.
 *
 * @author Ethan Cerami
 */
public class CyNetworkViewUtil {
	/**
	 * Creates a GraphView from the specified GraphPerspective.
	 *
	 * @param cyNetwork           GraphPerspective Object.
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

			GraphView networkView = createGraphView(cyNetwork, applyVisualStyle);

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
						GraphView view = Cytoscape.getCurrentNetworkView();
						view.setGraphLOD(new CyGraphLOD());
						view.fitContent();
					}
				});
		}
	}

	/**
	 * Creates the GraphView.
	 * Most of this code is copied directly from Cytoscape.createGraphView.
	 * However, it requires a bit of a hack to actually hide the network
	 * view from the user, and I didn't want to use this hack in the core
	 * Cytoscape.java class.
	 *
	 * @param cyNetwork        GraphPerspective
	 * @param applyVisualStyle Flag to Apply Current Visual Style.
	 * @return GraphView
	 */
	private static GraphView createGraphView(CyNetwork cyNetwork, boolean applyVisualStyle) {
		final GraphView view = GraphViewFactory.createGraphView(cyNetwork);

		view.setIdentifier(cyNetwork.getIdentifier());
		Cytoscape.getNetworkViewMap().put(cyNetwork.getIdentifier(), view);
		view.setTitle(cyNetwork.getTitle());

		if (applyVisualStyle) {
			Cytoscape.getVisualMappingManager().setVisualStyleForView(view,
				Cytoscape.getVisualMappingManager().getVisualStyle(
						BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE));
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
	 * @param networkView GraphView
	 * @param taskMonitor TaskMonitor
	 */
	private static void executeLayout(GraphView networkView, TaskMonitor taskMonitor) {
		// move a network node to jumpstart the SpringEmbeddedLayouter2
		Iterator i = networkView.getGraphPerspective().nodesIterator();

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
