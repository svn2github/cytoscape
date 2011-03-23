/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.presentation.property;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.ContinuousRangeImpl;
import org.cytoscape.view.model.DiscreteRangeImpl;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.Range;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * Minimal set of {@linkplain VisualProperty} objects which will be required for
 * all {@linkplain RenderingEngine}s. All of Visual Properties are singletons
 * and accessible as static object.
 * 
 */
public class TwoDVisualLexicon extends AbstractVisualLexicon {
	
	protected static final Color MIN_COLOR = new Color(0, 0, 0);
	protected static final Color MAX_COLOR = new Color(0xFF, 0xFF, 0xFF);
	protected static final Range<Paint> PAINT_RANGE = new ContinuousRangeImpl<Paint>(Paint.class, MIN_COLOR, MAX_COLOR);
	
	protected static final Set<String> STRING_SET = new HashSet<String>();
	// This will be used to for String VP which accepts any string values.
	protected static final Range<String> ARBITRARY_STRING_RANGE = new DiscreteRangeImpl<String>(String.class, STRING_SET);
	
	protected static final Range<Double> ARBITRARY_DOUBLE_RANGE = new ContinuousRangeImpl<Double>(Double.class, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	protected static final Range<Double> NONE_ZERO_POSITIVE_DOUBLE_RANGE = new ContinuousRangeImpl<Double>(Double.class, 0d, Double.POSITIVE_INFINITY, false, true);

	// Top level nodes has null as parent, and will be pointed by parent node.
	// This is because all VPs are static objects.

	////// Top level objects ///////
	public static final VisualProperty<Visualizable> NETWORK = new DefaultVisualizableVisualProperty(
			"NETWORK", "Network Visual Property", CyNetwork.class);
	public static final VisualProperty<Visualizable> NODE = new DefaultVisualizableVisualProperty(
			"NODE", "Node Visual Property", CyNode.class);
	public static final VisualProperty<Visualizable> EDGE = new DefaultVisualizableVisualProperty(
			"EDGE", "Edge Visual Property", CyEdge.class);
	

	////// Node VP /////////
	public static final VisualProperty<Paint> NODE_PAINT = new PaintVisualProperty(
			Color.gray, PAINT_RANGE, "NODE_PAINT", "Node Paint", CyNode.class);
	public static final VisualProperty<Paint> NODE_COLOR = new PaintVisualProperty(
			Color.RED, PAINT_RANGE, "NODE_COLOR", "Node Color", CyNode.class);
	
	public static final VisualProperty<Paint> NODE_LABEL_COLOR = new PaintVisualProperty(
			Color.BLACK, PAINT_RANGE, "NODE_LABEL_COLOR", "Node Label Color", CyNode.class);

	public static final VisualProperty<String> NODE_TEXT = new StringVisualProperty(
			"", ARBITRARY_STRING_RANGE, "NODE_TEXT", "Node Text", CyNode.class);
	public static final VisualProperty<String> NODE_LABEL = new StringVisualProperty(
			"", ARBITRARY_STRING_RANGE, "NODE_LABEL", "Node Label", CyNode.class);

	public static final VisualProperty<Double> NODE_LOCATION = new DoubleVisualProperty(
			Double.NaN, ARBITRARY_DOUBLE_RANGE, "NODE_LOCATION", "Node Location", true, CyNode.class);
	public static final VisualProperty<Double> NODE_X_LOCATION = new DoubleVisualProperty(
			Double.NaN, ARBITRARY_DOUBLE_RANGE, "NODE_X_LOCATION", "Node X Location", true, CyNode.class);
	public static final VisualProperty<Double> NODE_Y_LOCATION = new DoubleVisualProperty(
			Double.NaN, ARBITRARY_DOUBLE_RANGE, "NODE_Y_LOCATION", "Node Y Location", true, CyNode.class);

	public static final VisualProperty<Double> NODE_SIZE = new DoubleVisualProperty(
			50.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NODE_SIZE", "Node size", CyNode.class);
	public static final VisualProperty<Double> NODE_X_SIZE = new DoubleVisualProperty(
			60.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NODE_X_SIZE", "Node X size (width)", CyNode.class);
	public static final VisualProperty<Double> NODE_Y_SIZE = new DoubleVisualProperty(
			40.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NODE_Y_SIZE", "Node y size (height)", CyNode.class);

	public static final VisualProperty<Boolean> NODE_VISIBLE = new BooleanVisualProperty(
			true, "NODE_VISIBLE", "Node Visible", CyNode.class);

	public static final VisualProperty<Boolean> NODE_SELECTED = new BooleanVisualProperty(
			false, "NODE_SELECTED", "Node Selected", CyNode.class);

	
	/////// Edge VP ///////
	public static final VisualProperty<Paint> EDGE_PAINT = new PaintVisualProperty(
			Color.gray, PAINT_RANGE, "EDGE_PAINT", "Edge Paint", CyEdge.class);
//	public static final VisualProperty<Paint> EDGE_COLOR = new PaintVisualProperty(
//			Color.gray, PAINT_RANGE, "EDGE_COLOR", "Edge Color", CyEdge.class);
	public static final VisualProperty<Paint> EDGE_LABEL_COLOR = new PaintVisualProperty(
			Color.BLACK, PAINT_RANGE, "EDGE_LABEL_COLOR", "Edge Label Color", CyEdge.class);

	public static final VisualProperty<String> EDGE_TEXT = new StringVisualProperty(
			"", ARBITRARY_STRING_RANGE, "EDGE_TEXT", "Edge Text", CyEdge.class);
	public static final VisualProperty<String> EDGE_LABEL = new StringVisualProperty(
			"", ARBITRARY_STRING_RANGE, "EDGE_LABEL", "Edge Label", CyEdge.class);

	public static final VisualProperty<Double> EDGE_WIDTH = new DoubleVisualProperty(
			1d, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "EDGE_WIDTH", "Edge Width", CyEdge.class);

	public static final VisualProperty<Boolean> EDGE_VISIBLE = new BooleanVisualProperty(
			true, "EDGE_VISIBLE", "Edge Visible", CyEdge.class);

	public static final VisualProperty<Boolean> EDGE_SELECTED = new BooleanVisualProperty(
			false, "EDGE_SELECTED", "Edge Selected", CyEdge.class);

	
	//////// Network VP ////////
	public static final VisualProperty<Double> NETWORK_SCALE_FACTOR = new DoubleVisualProperty(
			1.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NETWORK_SCALE_FACTOR", "Network Scale Factor", true,  CyNetwork.class);

	public static final VisualProperty<Double> NETWORK_CENTER_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NETWORK_CENTER_LOCATION", "Network Center Location", true, CyNetwork.class);
	public static final VisualProperty<Double> NETWORK_CENTER_X_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NETWORK_CENTER_X_LOCATION", "Network Center X Location", true, CyNetwork.class);
	public static final VisualProperty<Double> NETWORK_CENTER_Y_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NETWORK_CENTER_Y_LOCATION", "Network Center Y Location", true, CyNetwork.class);

	public static final VisualProperty<Double> NETWORK_SIZE = new DoubleVisualProperty(
			400.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NETWORK_SIZE", "Network Size", true, CyNetwork.class);
	public static final VisualProperty<Double> NETWORK_WIDTH = new DoubleVisualProperty(
			400.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NETWORK_WIDTH", "Network Width", true, CyNetwork.class);
	public static final VisualProperty<Double> NETWORK_HEIGHT = new DoubleVisualProperty(
			400.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NETWORK_HEIGHT", "Network Height", true, CyNetwork.class);

	public static final VisualProperty<Paint> NETWORK_BACKGROUND_PAINT = new PaintVisualProperty(
			Color.WHITE, PAINT_RANGE, "NETWORK_BACKGROUND_PAINT", "Network Background Paint", CyNetwork.class);
	
	public static final VisualProperty<Paint> NETWORK_NODE_SELECTED_COLOR = new PaintVisualProperty(
			Color.YELLOW, PAINT_RANGE, "NETWORK_NODE_SELECTED_COLOR", "Node Selected Color", CyNetwork.class);
	
	public static final VisualProperty<Paint> NETWORK_EDGE_SELECTED_COLOR = new PaintVisualProperty(
			Color.YELLOW, PAINT_RANGE, "NETWORK_EDGE_SELECTED_COLOR", "Edge Selected Color", CyNetwork.class);

	/**
	 * Build tree-structure for the set of Visual Properties defined in this
	 * class.
	 * 
	 * @param root root node in the lexicon tree.  It is just an entry point for this tree.
	 * 
	 */
	public TwoDVisualLexicon(final VisualProperty<NullDataType> root) {
		super(root);

		addVisualProperty(NETWORK, root);

		addVisualProperty(NODE, NETWORK);
		addVisualProperty(EDGE, NETWORK);

		addVisualProperty(NETWORK_SIZE, NETWORK);
		addVisualProperty(NETWORK_CENTER_LOCATION, NETWORK);
		addVisualProperty(NETWORK_SCALE_FACTOR, NETWORK);
		addVisualProperty(NETWORK_NODE_SELECTED_COLOR, NETWORK);
		addVisualProperty(NETWORK_EDGE_SELECTED_COLOR, NETWORK);


		addVisualProperty(NODE_LOCATION, NODE);
		addVisualProperty(NODE_PAINT, NODE);
		addVisualProperty(NODE_SIZE, NODE);
		addVisualProperty(NODE_TEXT, NODE);
		addVisualProperty(NODE_VISIBLE, NODE);
		addVisualProperty(NODE_SELECTED, NODE);

		addVisualProperty(EDGE_PAINT, EDGE);
		addVisualProperty(EDGE_TEXT, EDGE);
		addVisualProperty(EDGE_VISIBLE, EDGE);
		addVisualProperty(EDGE_SELECTED, EDGE);

		addVisualProperty(NETWORK_BACKGROUND_PAINT, NETWORK);
		addVisualProperty(NETWORK_WIDTH, NETWORK_SIZE);
		addVisualProperty(NETWORK_HEIGHT, NETWORK_SIZE);
		addVisualProperty(NETWORK_CENTER_X_LOCATION, NETWORK_CENTER_LOCATION);
		addVisualProperty(NETWORK_CENTER_Y_LOCATION, NETWORK_CENTER_LOCATION);

		addVisualProperty(NODE_X_LOCATION, NODE_LOCATION);
		addVisualProperty(NODE_Y_LOCATION, NODE_LOCATION);
		addVisualProperty(NODE_COLOR, NODE_PAINT);
		addVisualProperty(NODE_LABEL_COLOR, NODE_PAINT);
		addVisualProperty(NODE_X_SIZE, NODE_SIZE);
		addVisualProperty(NODE_Y_SIZE, NODE_SIZE);
		addVisualProperty(NODE_LABEL, NODE_TEXT);

		addVisualProperty(EDGE_LABEL_COLOR, EDGE_PAINT);
		addVisualProperty(EDGE_WIDTH, EDGE);
		addVisualProperty(EDGE_LABEL, EDGE_TEXT);

		createLookupMap();
	}

	private void createLookupMap() {
		// XGMML:
		addIdentifierMapping(CyNetwork.class,"color",NETWORK_BACKGROUND_PAINT);
		addIdentifierMapping(CyNetwork.class,"GRAPH_VIEW_CENTER_X",NETWORK_CENTER_X_LOCATION);
		addIdentifierMapping(CyNetwork.class,"GRAPH_VIEW_CENTER_Y",NETWORK_CENTER_Y_LOCATION);
		addIdentifierMapping(CyNetwork.class,"GRAPH_VIEW_ZOOM",NETWORK_SCALE_FACTOR);
		
		addIdentifierMapping(CyNode.class,"fill",NODE_COLOR);
		addIdentifierMapping(CyNode.class,"x",NODE_X_LOCATION);
		addIdentifierMapping(CyNode.class,"y",NODE_Y_LOCATION);
		addIdentifierMapping(CyNode.class,"w",NODE_X_SIZE);
		addIdentifierMapping(CyNode.class,"h",NODE_Y_SIZE);
		addIdentifierMapping(CyNode.class,"size",NODE_SIZE);

		addIdentifierMapping(CyEdge.class,"fill",EDGE_PAINT);
		addIdentifierMapping(CyEdge.class,"width",EDGE_WIDTH);
		
		// 2.x VizMap Properties:
		addIdentifierMapping(CyNetwork.class,"backgroundColor",NETWORK_BACKGROUND_PAINT);
		addIdentifierMapping(CyNetwork.class,"nodeSelectionColor",NETWORK_NODE_SELECTED_COLOR);
		addIdentifierMapping(CyNetwork.class,"edgeSelectionColor",NETWORK_EDGE_SELECTED_COLOR);
		
		addIdentifierMapping(CyNode.class,"nodeFillColor",NODE_COLOR);
		addIdentifierMapping(CyNode.class,"nodeSize",NODE_SIZE);
		addIdentifierMapping(CyNode.class,"nodeWidth",NODE_X_SIZE);
		addIdentifierMapping(CyNode.class,"nodeHight",NODE_Y_SIZE);
		addIdentifierMapping(CyNode.class,"nodeHeight",NODE_Y_SIZE);
		addIdentifierMapping(CyNode.class,"nodeLabel",NODE_LABEL);
		addIdentifierMapping(CyNode.class,"nodeLabelColor",NODE_LABEL_COLOR);
		
		addIdentifierMapping(CyEdge.class,"edgeColor",EDGE_PAINT);
		addIdentifierMapping(CyEdge.class,"edgeLineWidth",EDGE_WIDTH);
		addIdentifierMapping(CyEdge.class,"edgeLabel",EDGE_LABEL);
		addIdentifierMapping(CyEdge.class,"edgeLabelColor",EDGE_LABEL_COLOR);
		
		// TODO add more mappings!
	}
}
