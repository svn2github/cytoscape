package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;

public class ThreeDVisualLexicon extends TwoDVisualLexicon {

	public static final VisualProperty<Double> NODE_Z_LOCATION = new DoubleVisualProperty(
			0.0, "NODE_Z_LOCATION", "Node Z Location", NODE_LOCATION, true);

	public static final VisualProperty<Double> NODE_Z_SIZE = new DoubleVisualProperty(
			0.0, "NODE_Z_SIZE", "Node z size (depth)", NODE_SIZE);

	public static final VisualProperty<Double> NETWORK_CENTER_Z_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_Z_LOCATION", "Network Center Z Location", NETWORK_CENTER_LOCATION);

	public static final VisualProperty<Double> NETWORK_DEPTH = new DoubleVisualProperty(
			0.0, "NETWORK_DEPTH", "Network Depth", NETWORK_SIZE);

	public ThreeDVisualLexicon(final VisualProperty<NullDataType> root) {
		super(root);

		NODE_LOCATION.getChildren().add(NODE_Z_LOCATION);
		NODE_SIZE.getChildren().add(NODE_Z_SIZE);
		addVisualProperty(NODE_Z_LOCATION);
		addVisualProperty(NODE_Z_SIZE);
		
		NETWORK_CENTER_LOCATION.getChildren().add(NETWORK_CENTER_Z_LOCATION);
		NETWORK_SIZE.getChildren().add(NETWORK_DEPTH);
		addVisualProperty(NETWORK_CENTER_Z_LOCATION);
		addVisualProperty(NETWORK_DEPTH);

	}
}
