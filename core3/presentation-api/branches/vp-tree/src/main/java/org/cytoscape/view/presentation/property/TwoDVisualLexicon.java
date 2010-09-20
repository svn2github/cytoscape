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
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;

/**
 * Should be implemented as a service. 'Renderer' is simply anything that
 * provides VisualProperties. With a 'VisualProperties as annotations' this
 * won't be needed.
 */
public class TwoDVisualLexicon extends AbstractVisualLexicon {

	public static final VisualProperty<Visualizable> NETWORK = new DefaultVisualizableVisualProperty(
			"NETWORK", "Network Visual Property", null);

	public static final VisualProperty<Visualizable> NODE = new DefaultVisualizableVisualProperty(
			"NODE", "Node Visual Property", NETWORK);
	public static final VisualProperty<Visualizable> EDGE = new DefaultVisualizableVisualProperty(
			"EDGE", "Edge Visual Property", NETWORK);

	public static final VisualProperty<Color> NODE_PAINT = new PaintVisualProperty<Color>(
			Color.gray, "NODE_PAINT", "Node Paint", NODE);
	public static final VisualProperty<Color> NODE_COLOR = new PaintVisualProperty<Color>(
			Color.RED, "NODE_COLOR", "Node Color", NODE_PAINT);
	public static final VisualProperty<Color> NODE_SELECTED_COLOR = new PaintVisualProperty<Color>(
			Color.YELLOW, "NODE_SELECTED_COLOR", "Node Selected Color",
			NODE_PAINT);
	public static final VisualProperty<Color> NODE_LABEL_COLOR = new PaintVisualProperty<Color>(
			Color.BLACK, "NODE_LABEL_COLOR", "Node Label Color", NODE_PAINT);

	public static final VisualProperty<String> NODE_TEXT = new StringVisualProperty(
			"", "NODE_TEXT", "Node Text", NODE);
	public static final VisualProperty<String> NODE_LABEL = new StringVisualProperty(
			"", "NODE_LABEL", "Node Label", NODE_TEXT);

	public static final VisualProperty<Double> NODE_LOCATION = new DoubleVisualProperty(
			Double.NaN, "NODE_LOCATION", "Node Location", NODE, true);
	public static final VisualProperty<Double> NODE_X_LOCATION = new DoubleVisualProperty(
			Double.NaN, "NODE_X_LOCATION", "Node X Location", NODE_LOCATION,
			true);
	public static final VisualProperty<Double> NODE_Y_LOCATION = new DoubleVisualProperty(
			Double.NaN, "NODE_Y_LOCATION", "Node Y Location", NODE_LOCATION,
			true);

	public static final VisualProperty<Double> NODE_SIZE = new DoubleVisualProperty(
			50.0, "NODE_SIZE", "Node size", NODE);
	public static final VisualProperty<Double> NODE_X_SIZE = new DoubleVisualProperty(
			50.0, "NODE_X_SIZE", "Node X size (width)", NODE_SIZE);
	public static final VisualProperty<Double> NODE_Y_SIZE = new DoubleVisualProperty(
			30.0, "NODE_Y_SIZE", "Node y size (height)", NODE_SIZE);

	public static final VisualProperty<Boolean> NODE_VISIBLE = new BooleanVisualProperty(
			true, "NODE_VISIBLE", "Node Visible", NODE);

	public static final VisualProperty<Boolean> NODE_SELECTED = new BooleanVisualProperty(
			false, "NODE_SELECTED", "Node Selected", NODE);

	public static final VisualProperty<? extends Paint> EDGE_PAINT = new PaintVisualProperty<Color>(
			Color.gray, "EDGE_PAINT", "Edge Paint", EDGE);
	public static final VisualProperty<? extends Paint> EDGE_COLOR = new PaintVisualProperty<Color>(
			Color.gray, "EDGE_COLOR", "Edge Color", EDGE_PAINT);
	public static final VisualProperty<? extends Paint> EDGE_LABEL_COLOR = new PaintVisualProperty<Color>(
			Color.BLACK, "EDGE_LABEL_COLOR", "Edge Label Color", EDGE_PAINT);

	public static final VisualProperty<String> EDGE_TEXT = new StringVisualProperty(
			"", "EDGE_TEXT", "Edge Text", EDGE);
	public static final VisualProperty<String> EDGE_LABEL = new StringVisualProperty(
			"", "EDGE_LABEL", "Edge Label", EDGE_TEXT);

	public static final VisualProperty<Double> EDGE_SIZE = new DoubleVisualProperty(
			1d, "EDGE_SIZE", "Edge Size", EDGE);
	public static final VisualProperty<Double> EDGE_WIDTH = new DoubleVisualProperty(
			1d, "EDGE_WIDTH", "Edge Width", EDGE_SIZE);

	public static final VisualProperty<Boolean> EDGE_VISIBLE = new BooleanVisualProperty(
			true, "EDGE_VISIBLE", "Edge Visible", EDGE);

	public static final VisualProperty<Boolean> EDGE_SELECTED = new BooleanVisualProperty(
			false, "EDGE_SELECTED", "Edge Selected", EDGE);

	public static final VisualProperty<Double> NETWORK_SCALE_FACTOR = new DoubleVisualProperty(
			1.0, "NETWORK_SCALE_FACTOR", "Network Scale Factor", NETWORK);

	public static final VisualProperty<Double> NETWORK_CENTER_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_LOCATION", "Network Center Location", NETWORK);
	public static final VisualProperty<Double> NETWORK_CENTER_X_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_X_LOCATION", "Network Center X Location",
			NETWORK_CENTER_LOCATION);
	public static final VisualProperty<Double> NETWORK_CENTER_Y_LOCATION = new DoubleVisualProperty(
			0.0, "NETWORK_CENTER_Y_LOCATION", "Network Center Y Location",
			NETWORK_CENTER_LOCATION);

	public static final VisualProperty<Double> NETWORK_SIZE = new DoubleVisualProperty(
			100.0, "NETWORK_SIZE", "Network Size", NETWORK);
	public static final VisualProperty<Double> NETWORK_WIDTH = new DoubleVisualProperty(
			100.0, "NETWORK_WIDTH", "Network Width", NETWORK_SIZE);
	public static final VisualProperty<Double> NETWORK_HEIGHT = new DoubleVisualProperty(
			100.0, "NETWORK_HEIGHT", "Network Height", NETWORK_SIZE);

	public static final VisualProperty<String> NETWORK_TEXT = new StringVisualProperty(
			"", "NETWORK_TEXT", "Network Text", NETWORK);
	public static final VisualProperty<String> NETWORK_TITLE = new StringVisualProperty(
			"", "NETWORK_TITLE", "Network Title", NETWORK_TEXT);

	public static final VisualProperty<Color> NETWORK_PAINT = new PaintVisualProperty<Color>(
			Color.WHITE, "NETWORK_PAINT", "Network Paint", NETWORK);
	public static final VisualProperty<Paint> NETWORK_BACKGROUND_COLOR = new PaintVisualProperty<Paint>(
			Color.WHITE, "NETWORK_BACKGROUND_COLOR",
			"Network Background Color", NETWORK_PAINT);

	/**
	 * Build basic VP tree.
	 * 
	 * @param rootVisualProperty
	 */
	public TwoDVisualLexicon(final VisualProperty<NullDataType> root) {
		super(root);
		root.getChildren().add(NETWORK);
		
		addVisualProperty(NETWORK);
		((DefaultVisualizableVisualProperty) NETWORK).setParent(root);

		addVisualProperty(NODE);
		addVisualProperty(EDGE);

		addVisualProperty(NETWORK_TEXT);
		addVisualProperty(NETWORK_PAINT);
		addVisualProperty(NETWORK_SIZE);
		addVisualProperty(NETWORK_CENTER_LOCATION);
		addVisualProperty(NETWORK_SCALE_FACTOR);

		addVisualProperty(NODE_LOCATION);
		addVisualProperty(NODE_PAINT);
		addVisualProperty(NODE_SIZE);
		addVisualProperty(NODE_TEXT);
		addVisualProperty(NODE_VISIBLE);
		addVisualProperty(NODE_SELECTED);

		addVisualProperty(EDGE_PAINT);
		addVisualProperty(EDGE_SIZE);
		addVisualProperty(EDGE_TEXT);
		addVisualProperty(EDGE_VISIBLE);
		addVisualProperty(EDGE_SELECTED);

		addVisualProperty(NETWORK_TITLE);
		addVisualProperty(NETWORK_BACKGROUND_COLOR);
		addVisualProperty(NETWORK_WIDTH);
		addVisualProperty(NETWORK_HEIGHT);
		addVisualProperty(NETWORK_CENTER_X_LOCATION);
		addVisualProperty(NETWORK_CENTER_Y_LOCATION);

		addVisualProperty(NODE_X_LOCATION);
		addVisualProperty(NODE_Y_LOCATION);
		addVisualProperty(NODE_COLOR);
		addVisualProperty(NODE_LABEL_COLOR);
		addVisualProperty(NODE_SELECTED_COLOR);
		addVisualProperty(NODE_X_SIZE);
		addVisualProperty(NODE_Y_SIZE);
		addVisualProperty(NODE_LABEL);

		addVisualProperty(EDGE_COLOR);
		addVisualProperty(EDGE_LABEL_COLOR);
		addVisualProperty(EDGE_WIDTH);
		addVisualProperty(EDGE_LABEL);
	}

}
