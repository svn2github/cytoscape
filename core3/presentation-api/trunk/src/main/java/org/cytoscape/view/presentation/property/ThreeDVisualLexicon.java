package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;

public class ThreeDVisualLexicon extends TwoDVisualLexicon {

	public static final VisualProperty<Double> NODE_Z_LOCATION = new DoubleVisualProperty(
			0.0, "NODE_Z_LOCATION", "Node Z Location", true);

	public static final VisualProperty<Double> NODE_Z_SIZE = new DoubleVisualProperty(
			0.0, "NODE_Z_SIZE", "Node z size (depth)");

	public static final VisualProperty<Double> NETWORK_CENTER_Z_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_Z_LOCATION", "Network Center Z Location");

	public static final VisualProperty<Double> NETWORK_DEPTH = new DoubleVisualProperty(
			0.0, "NETWORK_DEPTH", "Network Depth");

	public ThreeDVisualLexicon(final VisualProperty<NullDataType> root) {
		super(root);

		addVisualProperty(NODE_Z_LOCATION, NODE_LOCATION);
		addVisualProperty(NODE_Z_SIZE, NODE_SIZE);

		addVisualProperty(NETWORK_CENTER_Z_LOCATION, NETWORK_CENTER_LOCATION);
		addVisualProperty(NETWORK_DEPTH, NETWORK_SIZE);

	}
}
