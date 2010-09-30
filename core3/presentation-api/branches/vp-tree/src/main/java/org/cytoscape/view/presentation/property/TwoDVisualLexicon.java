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

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexiconNodeFactory;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;

/**
 * Should be implemented as a service. 'Renderer' is simply anything that
 * provides VisualProperties. With a 'VisualProperties as annotations' this
 * won't be needed.
 */
public class TwoDVisualLexicon extends AbstractVisualLexicon {

	// Top level nodes has null as parent, and will be pointed by parent node.
	// This is because all VPs are static objects.
	public static final VisualProperty<Visualizable> NETWORK = new DefaultVisualizableVisualProperty(
			"NETWORK", "Network Visual Property");

	public static final VisualProperty<Visualizable> NODE = new DefaultVisualizableVisualProperty(
			"NODE", "Node Visual Property");
	public static final VisualProperty<Visualizable> EDGE = new DefaultVisualizableVisualProperty(
			"EDGE", "Edge Visual Property");

	public static final VisualProperty<Color> NODE_PAINT = new PaintVisualProperty<Color>(
			Color.gray, "NODE_PAINT", "Node Paint");
	public static final VisualProperty<Color> NODE_COLOR = new PaintVisualProperty<Color>(
			Color.RED, "NODE_COLOR", "Node Color");
	public static final VisualProperty<Color> NODE_SELECTED_COLOR = new PaintVisualProperty<Color>(
			Color.YELLOW, "NODE_SELECTED_COLOR", "Node Selected Color");
	public static final VisualProperty<Color> NODE_LABEL_COLOR = new PaintVisualProperty<Color>(
			Color.BLACK, "NODE_LABEL_COLOR", "Node Label Color");

	public static final VisualProperty<String> NODE_TEXT = new StringVisualProperty(
			"", "NODE_TEXT", "Node Text");
	public static final VisualProperty<String> NODE_LABEL = new StringVisualProperty(
			"", "NODE_LABEL", "Node Label");

	public static final VisualProperty<Double> NODE_LOCATION = new DoubleVisualProperty(
			Double.NaN, "NODE_LOCATION", "Node Location", true);
	public static final VisualProperty<Double> NODE_X_LOCATION = new DoubleVisualProperty(
			Double.NaN, "NODE_X_LOCATION", "Node X Location", true);
	public static final VisualProperty<Double> NODE_Y_LOCATION = new DoubleVisualProperty(
			Double.NaN, "NODE_Y_LOCATION", "Node Y Location", true);

	public static final VisualProperty<Double> NODE_SIZE = new DoubleVisualProperty(
			50.0, "NODE_SIZE", "Node size");
	public static final VisualProperty<Double> NODE_X_SIZE = new DoubleVisualProperty(
			50.0, "NODE_X_SIZE", "Node X size (width)");
	public static final VisualProperty<Double> NODE_Y_SIZE = new DoubleVisualProperty(
			30.0, "NODE_Y_SIZE", "Node y size (height)");

	public static final VisualProperty<Boolean> NODE_VISIBLE = new BooleanVisualProperty(
			true, "NODE_VISIBLE", "Node Visible");

	public static final VisualProperty<Boolean> NODE_SELECTED = new BooleanVisualProperty(
			false, "NODE_SELECTED", "Node Selected");

	public static final VisualProperty<? extends Paint> EDGE_PAINT = new PaintVisualProperty<Color>(
			Color.gray, "EDGE_PAINT", "Edge Paint");
	public static final VisualProperty<Color> EDGE_COLOR = new PaintVisualProperty<Color>(
			Color.gray, "EDGE_COLOR", "Edge Color");
	public static final VisualProperty<Color> EDGE_LABEL_COLOR = new PaintVisualProperty<Color>(
			Color.BLACK, "EDGE_LABEL_COLOR", "Edge Label Color");

	public static final VisualProperty<String> EDGE_TEXT = new StringVisualProperty(
			"", "EDGE_TEXT", "Edge Text");
	public static final VisualProperty<String> EDGE_LABEL = new StringVisualProperty(
			"", "EDGE_LABEL", "Edge Label");

	public static final VisualProperty<Double> EDGE_WIDTH = new DoubleVisualProperty(
			1d, "EDGE_WIDTH", "Edge Width");

	public static final VisualProperty<Boolean> EDGE_VISIBLE = new BooleanVisualProperty(
			true, "EDGE_VISIBLE", "Edge Visible");

	public static final VisualProperty<Boolean> EDGE_SELECTED = new BooleanVisualProperty(
			false, "EDGE_SELECTED", "Edge Selected");

	public static final VisualProperty<Double> NETWORK_SCALE_FACTOR = new DoubleVisualProperty(
			1.0, "NETWORK_SCALE_FACTOR", "Network Scale Factor");

	public static final VisualProperty<Double> NETWORK_CENTER_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_LOCATION", "Network Center Location");
	public static final VisualProperty<Double> NETWORK_CENTER_X_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_X_LOCATION", "Network Center X Location");
	public static final VisualProperty<Double> NETWORK_CENTER_Y_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_Y_LOCATION", "Network Center Y Location");

	public static final VisualProperty<Double> NETWORK_SIZE = new DoubleVisualProperty(
			100.0, "NETWORK_SIZE", "Network Size");
	public static final VisualProperty<Double> NETWORK_WIDTH = new DoubleVisualProperty(
			100.0, "NETWORK_WIDTH", "Network Width");
	public static final VisualProperty<Double> NETWORK_HEIGHT = new DoubleVisualProperty(
			100.0, "NETWORK_HEIGHT", "Network Height");

	public static final VisualProperty<String> NETWORK_TITLE = new StringVisualProperty(
			"", "NETWORK_TITLE", "Network Title");

	public static final VisualProperty<Color> NETWORK_PAINT = new PaintVisualProperty<Color>(
			Color.WHITE, "NETWORK_PAINT", "Network Paint");
	public static final VisualProperty<Paint> NETWORK_BACKGROUND_COLOR = new PaintVisualProperty<Paint>(
			Color.WHITE, "NETWORK_BACKGROUND_COLOR", "Network Background Color");

	/**
	 * Build basic VP tree.
	 * 
	 * @param rootVisualProperty
	 */
	public TwoDVisualLexicon(final VisualProperty<NullDataType> root,
			final VisualLexiconNodeFactory nodeFactory) {
		super(root, nodeFactory);

		addVisualProperty(NETWORK, root);

		addVisualProperty(NODE, NETWORK);
		addVisualProperty(EDGE, NETWORK);

		addVisualProperty(NETWORK_PAINT, NETWORK);
		addVisualProperty(NETWORK_SIZE, NETWORK);
		addVisualProperty(NETWORK_CENTER_LOCATION, NETWORK);
		addVisualProperty(NETWORK_SCALE_FACTOR, NETWORK);

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

		addVisualProperty(NETWORK_TITLE, NETWORK);
		addVisualProperty(NETWORK_BACKGROUND_COLOR, NETWORK_PAINT);
		addVisualProperty(NETWORK_WIDTH, NETWORK_SIZE);
		addVisualProperty(NETWORK_HEIGHT, NETWORK_SIZE);
		addVisualProperty(NETWORK_CENTER_X_LOCATION, NETWORK_CENTER_LOCATION);
		addVisualProperty(NETWORK_CENTER_Y_LOCATION, NETWORK_CENTER_LOCATION);

		addVisualProperty(NODE_X_LOCATION, NODE_LOCATION);
		addVisualProperty(NODE_Y_LOCATION, NODE_LOCATION);
		addVisualProperty(NODE_COLOR, NODE_PAINT);
		addVisualProperty(NODE_LABEL_COLOR, NODE_PAINT);
		addVisualProperty(NODE_SELECTED_COLOR, NODE_PAINT);
		addVisualProperty(NODE_X_SIZE, NODE_SIZE);
		addVisualProperty(NODE_Y_SIZE, NODE_SIZE);
		addVisualProperty(NODE_LABEL, NODE_TEXT);

		addVisualProperty(EDGE_COLOR, EDGE_PAINT);
		addVisualProperty(EDGE_LABEL_COLOR, EDGE_PAINT);
		addVisualProperty(EDGE_WIDTH, EDGE);
		addVisualProperty(EDGE_LABEL, EDGE_TEXT);
	}

}
