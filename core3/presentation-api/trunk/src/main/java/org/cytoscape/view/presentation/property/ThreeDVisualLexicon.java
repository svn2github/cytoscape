package org.cytoscape.view.presentation.property;

import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

import org.cytoscape.view.model.VisualProperty;

public class ThreeDVisualLexicon extends TwoDVisualLexicon {

	public static final VisualProperty<Double> NODE_Z_LOCATION = new DoubleVisualProperty(
			NODE, 0.0, "NODE_Z_LOCATION", "Node Z Location");
	
	public static final VisualProperty<Double> NODE_Z_SIZE = new DoubleVisualProperty(
			NODE, 0.0, "NODE_Z_SIZE", "Node z size (depth)");
	
	public static final VisualProperty<Double> NETWORK_CENTER_Z_LOCATION = new DoubleVisualProperty(
			NETWORK, 0.0, "NETWORK_CENTER_Z_LOCATION",
			"Network Center Z Location");
	
	public static final VisualProperty<Double> NETWORK_DEPTH = new DoubleVisualProperty(
			NETWORK, 0.0, "NETWORK_DEPTH", "Network Depth");
}
