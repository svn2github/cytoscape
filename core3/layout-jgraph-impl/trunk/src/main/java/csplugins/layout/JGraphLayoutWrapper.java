
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.layout;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayout;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.work.UndoSupport;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;
import org.jgraph.plugins.layouts.AnnealingLayoutAlgorithm;
import org.jgraph.plugins.layouts.CircleGraphLayout;
import org.jgraph.plugins.layouts.GEMLayoutAlgorithm;
import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;
import org.jgraph.plugins.layouts.JGraphLayoutSettings;
import org.jgraph.plugins.layouts.MoenLayoutAlgorithm;
import org.jgraph.plugins.layouts.RadialTreeLayoutAlgorithm;
import org.jgraph.plugins.layouts.SpringEmbeddedLayoutAlgorithm;
import org.jgraph.plugins.layouts.SugiyamaLayoutAlgorithm;
import org.jgraph.plugins.layouts.TreeLayoutAlgorithm;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 *
 */
public class JGraphLayoutWrapper extends AbstractLayout {
	/**
	 * 
	 */
	public static final int ANNEALING = 0;

	/**
	 * 
	 */
	public static final int MOEN = 1;

	/**
	 * 
	 */
	public static final int CIRCLE_GRAPH = 2;

	/**
	 * 
	 */
	public static final int RADIAL_TREE = 3;

	/**
	 * 
	 */
	public static final int GEM = 4;

	/**
	 * 
	 */
	public static final int SPRING_EMBEDDED = 5;

	/**
	 * 
	 */
	public static final int SUGIYAMA = 6;

	/**
	 * 
	 */
	public static final int TREE = 7;
	int layout_type = 0;
	private JGraphLayoutAlgorithm layout = null;
	private JGraphLayoutSettings layoutSettings = null;
	private boolean canceled = false;

	/**
	 * Creates a new JGraphLayoutWrapper object.
	 *
	 * @param layout_type  DOCUMENT ME!
	 */
	public JGraphLayoutWrapper(UndoSupport undoSupport, int layout_type) {
		super(undoSupport);
		this.layout_type = layout_type;

		switch (layout_type) {
			case ANNEALING:
				layout = new AnnealingLayoutAlgorithm();

				break;

			case MOEN:
				layout = new MoenLayoutAlgorithm();

				break;

			case CIRCLE_GRAPH:
				layout = new CircleGraphLayout();

				break;

			case RADIAL_TREE:
				layout = new RadialTreeLayoutAlgorithm();

				break;

			case GEM:
				layout = new GEMLayoutAlgorithm(new AnnealingLayoutAlgorithm());

				break;

			case SPRING_EMBEDDED:
				layout = new SpringEmbeddedLayoutAlgorithm();

				break;

			case SUGIYAMA:
				layout = new SugiyamaLayoutAlgorithm();

				break;

			case TREE:
				layout = new TreeLayoutAlgorithm();

				break;
		}

		layoutSettings = layout.createSettings();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		switch (layout_type) {
			case ANNEALING:
				return "jgraph-annealing";

			case MOEN:
				return "jgraph-moen";

			case CIRCLE_GRAPH:
				return "jgraph-circle";

			case RADIAL_TREE:
				return "jgraph-radial-tree";

			case GEM:
				return "jgraph-gem";

			case SPRING_EMBEDDED:
				return "jgraph-spring";

			case SUGIYAMA:
				return "jgraph-sugiyama";

			case TREE:
				return "jgraph-tree";
		}

		return "";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		switch (layout_type) {
			case ANNEALING:
				return "Simulated Annealing Layout";

			case MOEN:
				return "MOEN Layout";

			case CIRCLE_GRAPH:
				return "Circle Layout";

			case RADIAL_TREE:
				return "Radial Tree Layout";

			case GEM:
				return "GEM Layout";

			case SPRING_EMBEDDED:
				return "Spring Embedded Layout";

			case SUGIYAMA:
				return "Sugiyama Layout";

			case TREE:
				return "Tree Layout";
		}

		return "";
	}

	/**
	 * Get the settings panel for this layout
	 */
	public JPanel getSettingsPanel() {
		return (JPanel) layoutSettings;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void updateSettings() {
		if (layoutSettings != null)
			layoutSettings.apply();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void revertSettings() {
		if (layoutSettings != null)
			layoutSettings.revert();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void halt() {
		canceled = true;

//		if (layout != null)
//			layout.setCanceled();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void construct() {
		canceled = false;
		initialize();

		double currentProgress = 0;
		double percentProgressPerIter = 0;
		CyNetwork network = networkView.getSource();
		Map j_giny_node_map = new HashMap(); //PrimeFinder.nextPrime(network.getNodeCount()));
		Map giny_j_node_map = new HashMap(); //PrimeFinder.nextPrime(network.getNodeCount()));
		Map j_giny_edge_map = new HashMap(); //PrimeFinder.nextPrime(network.getEdgeCount()));

		taskMonitor.setStatusMessage("Executing Layout");
		taskMonitor.setProgress(currentProgress/100.0);

		// Construct Model and Graph
		//
		GraphModel model = new DefaultGraphModel();
		JGraph graph = new JGraph(model);

		// Create Nested Map (from Cells to Attributes)
		//
		Map attributes = new Hashtable();

		Set cells = new HashSet();

		// update progress bar
		currentProgress = 20;
		taskMonitor.setProgress(currentProgress/100.0);
		percentProgressPerIter = 20 / (double) (networkView.getNodeViews().size());

		// create Vertices
		for (CyNode n: network.getNodeList()){
		    if (canceled) return;
			View<CyNode> node_view = networkView.getNodeView(n);

			DefaultGraphCell jcell = new DefaultGraphCell(n.getIndex());

			// Set bounds
			Rectangle2D bounds = new Rectangle2D.Double(
                                       node_view.getVisualProperty(TwoDVisualLexicon.NODE_X_LOCATION),
				       node_view.getVisualProperty(TwoDVisualLexicon.NODE_Y_LOCATION),
				       node_view.getVisualProperty(TwoDVisualLexicon.NODE_X_SIZE),
				       node_view.getVisualProperty(TwoDVisualLexicon.NODE_Y_SIZE)
								    );

			GraphConstants.setBounds(jcell.getAttributes(), bounds);

			j_giny_node_map.put(jcell, n);
			giny_j_node_map.put(n, jcell);

			cells.add(jcell);

			// update progress bar
			currentProgress += percentProgressPerIter;
			taskMonitor.setProgress(currentProgress/100.0);
		}

		// update progress bar
		percentProgressPerIter = 20 / (double) (networkView.getEdgeViews().size());

		for (CyEdge edge: network.getEdgeList()){
			if (canceled) return;
			DefaultGraphCell j_source = (DefaultGraphCell) giny_j_node_map.get(edge.getSource());
			DefaultGraphCell j_target = (DefaultGraphCell) giny_j_node_map.get(edge.getTarget());

			DefaultPort source_port = new DefaultPort();
			DefaultPort target_port = new DefaultPort();

			j_source.add(source_port);
			j_target.add(target_port);

			source_port.setParent(j_source);
			target_port.setParent(j_target);

			// create the edge
			DefaultEdge jedge = new DefaultEdge();
			j_giny_edge_map.put(jedge, edge);

			// Connect Edge
			//
			ConnectionSet cs = new ConnectionSet(jedge, source_port, target_port);
			Object[] ecells = new Object[] { jedge, j_source, j_target };

			// Insert into Model
			//
			model.insert(ecells, attributes, cs, null, null);

			cells.add(jedge);

			// update progress bar
			currentProgress += percentProgressPerIter;
			taskMonitor.setProgress(currentProgress/100.0);
		}

		layout.run(graph, cells.toArray());

		GraphLayoutCache cache = graph.getGraphLayoutCache();

		CellView[] cellViews = graph.getGraphLayoutCache()
		                            .getAllDescendants(graph.getGraphLayoutCache().getRoots());

		currentProgress = 80;
		taskMonitor.setProgress(currentProgress/100.0);
		percentProgressPerIter = 20 / (double) (cellViews.length);

		if (canceled)
			return;

		for (int i = 0; i < cellViews.length; i++) {
			CellView cell_view = cellViews[i];

			if (cell_view instanceof VertexView) {
				// ok, we found a node
				Rectangle2D rect = graph.getCellBounds(cell_view.getCell());
				CyNode giny = (CyNode) j_giny_node_map.get(cell_view.getCell());
				View<CyNode> node_view = networkView.getNodeView(giny);
				node_view.setVisualProperty(TwoDVisualLexicon.NODE_X_LOCATION, rect.getX());
				node_view.setVisualProperty(TwoDVisualLexicon.NODE_Y_LOCATION, rect.getY());

				// update progress bar
				currentProgress += percentProgressPerIter;
				taskMonitor.setProgress(currentProgress/100.0);
			}
		}

		// I don't think that any of the current layouts have edge components, 
		// so I won't bother for now.
		model = null;
		graph = null;
		attributes = null;
		cells = null;
		System.gc();
	}
}
