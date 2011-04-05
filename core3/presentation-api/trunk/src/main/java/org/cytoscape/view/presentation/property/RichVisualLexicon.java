package org.cytoscape.view.presentation.property;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.values.LineType;
import org.cytoscape.view.presentation.property.values.NodeShape;

/**
 * Minimal set of Visual Properties for 3D rendering engines.
 *
 */
public class RichVisualLexicon extends MinimalVisualLexicon {

	// 3D-related props
	public static final VisualProperty<Double> NODE_Z_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NODE_Z_LOCATION", "Node Z Location", true, CyNode.class);

	public static final VisualProperty<Double> NODE_DEPTH = new DoubleVisualProperty(
			0.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NODE_DEPTH", "Node Depth", CyNode.class);

	public static final VisualProperty<Double> NETWORK_CENTER_Z_LOCATION = new DoubleVisualProperty(
			0.0, ARBITRARY_DOUBLE_RANGE, "NETWORK_CENTER_Z_LOCATION", "Network Center Z Location", CyNetwork.class);

	public static final VisualProperty<Double> NETWORK_DEPTH = new DoubleVisualProperty(
			0.0, NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NETWORK_DEPTH", "Network Depth", CyNetwork.class);
	
	
	public static final VisualProperty<NodeShape> NODE_SHAPE = new NodeShapeVisualProperty(
			NodeShapeVisualProperty.RECTANGLE, "NODE_SHAPE", "Node Shape", CyNode.class);
	
	// Line Types
	public static final VisualProperty<LineType> NODE_BORDER_LINE_TYPE = new LineTypeVisualProperty(
			LineTypeVisualProperty.SOLID, "NODE_BORDER_STROKE", "Node Border Line Type", CyNode.class);
	public static final VisualProperty<LineType> EDGE_LINE_TYPE = new LineTypeVisualProperty(
			LineTypeVisualProperty.SOLID, "EDGE_LINE_TYPE", "Edge Line Type", CyEdge.class);

	/**
	 * Construct a {@linkplain VisalLexicon} for 3D rendering engine.
	 * 
	 * @param root Root node in the lexicon tree.
	 * 
	 */
	public RichVisualLexicon(final VisualProperty<NullDataType> root) {
		super(root);

		addVisualProperty(NODE_Z_LOCATION, NODE);
		addVisualProperty(NODE_DEPTH, NODE_SIZE);

		addVisualProperty(NETWORK_CENTER_Z_LOCATION, NETWORK);
		addVisualProperty(NETWORK_DEPTH, NETWORK_SIZE);
		
		addVisualProperty(NODE_SHAPE, NODE);

	}
}
