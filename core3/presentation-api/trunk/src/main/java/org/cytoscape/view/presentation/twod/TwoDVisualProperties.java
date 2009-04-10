
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
package org.cytoscape.view.presentation.twod;

import org.cytoscape.view.model.Renderer;
import org.cytoscape.view.model.VisualProperty;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import java.awt.Paint;
import java.awt.Color;

/**
 * Should be implemented as a service.
 * 'Renderer' is simply anything that provides VisualProperties.
 * With a 'VisualProperties as annotations' this won't be needed.
 */
public class TwoDVisualProperties implements Renderer {

	protected Set<VisualProperty<?>> visualPropertySet;

	public static final VisualProperty<? extends Paint> NODE_COLOR
		= new ColorTwoDVisualProperty(VisualProperty.NODE,Color.RED,"NODE_COLOR","Node Color");
	public static final VisualProperty<String> NODE_LABEL
		= new StringTwoDVisualProperty(VisualProperty.NODE,"","NODE_LABEL","Node Label");
	public static final VisualProperty<? extends Paint> NODE_LABEL_COLOR
		= new ColorTwoDVisualProperty(VisualProperty.NODE,Color.BLACK,"NODE_LABEL_COLOR","Node Label Color");
	public static final VisualProperty<Double> NODE_X_LOCATION
		= new DoubleTwoDVisualProperty(VisualProperty.NODE,0.0,"NODE_X_LOCATION","Node X Location");
	public static final VisualProperty<Double> NODE_Y_LOCATION
		= new DoubleTwoDVisualProperty(VisualProperty.NODE,0.0,"NODE_Y_LOCATION","Node Y Location");
	public static final VisualProperty<Boolean> NODE_VISIBLE
		= new BooleanTwoDVisualProperty(VisualProperty.NODE,true,"NODE_VISIBLE","Node Visible");
	public static final VisualProperty<Boolean> NODE_SELECTED
		= new BooleanTwoDVisualProperty(VisualProperty.NODE,false,"NODE_SELECTED","Node Selected");
	public static final VisualProperty<? extends Paint> EDGE_COLOR
		= new ColorTwoDVisualProperty(VisualProperty.EDGE,Color.BLUE,"EDGE_COLOR","Edge Color");
	public static final VisualProperty<String> EDGE_LABEL
		= new StringTwoDVisualProperty(VisualProperty.EDGE,"","EDGE_LABEL","Edge Label");
	public static final VisualProperty<? extends Paint> EDGE_LABEL_COLOR
		= new ColorTwoDVisualProperty(VisualProperty.EDGE,Color.BLACK,"EDGE_LABEL_COLOR","Edge Label Color");
	public static final VisualProperty<Boolean> EDGE_VISIBLE
		= new BooleanTwoDVisualProperty(VisualProperty.EDGE,true,"EDGE_VISIBLE","Edge Visible");
	public static final VisualProperty<Boolean> EDGE_SELECTED
		= new BooleanTwoDVisualProperty(VisualProperty.EDGE,false,"EDGE_SELECTED","Edge Selected");
	public static final VisualProperty<Double> NETWORK_SCALE_FACTOR 
		= new DoubleTwoDVisualProperty(VisualProperty.NETWORK,1.0,"NETWORK_SCALE_FACTOR","Network Scale Factor");
	public static final VisualProperty<Double> NETWORK_CENTER_X_LOCATION
		= new DoubleTwoDVisualProperty(VisualProperty.NETWORK,0.0,"NETWORK_CENTER_X_LOCATION","Network Center X Location");
	public static final VisualProperty<Double> NETWORK_CENTER_Y_LOCATION 
		= new DoubleTwoDVisualProperty(VisualProperty.NETWORK,0.0,"NETWORK_CENTER_Y_LOCATION","Network Center Y Location");
	public static final VisualProperty<Double> NETWORK_WIDTH 
		= new DoubleTwoDVisualProperty(VisualProperty.NETWORK,100.0,"NETWORK_WIDTH","Network Width");
	public static final VisualProperty<Double> NETWORK_HEIGHT 
		= new DoubleTwoDVisualProperty(VisualProperty.NETWORK,100.0,"NETWORK_HEIGHT","Network Height");

	public static final VisualProperty<? extends Paint> NETWORK_BACKGROUND_COLOR
		= new ColorTwoDVisualProperty(VisualProperty.NETWORK,Color.WHITE,"NETWORK_BACKGROUND_COLOR","Network Background Color");
	public static final VisualProperty<String> NETWORK_TITLE 
		= new StringTwoDVisualProperty(VisualProperty.NETWORK,"","NETWORK_TITLE","Network Title");

	public TwoDVisualProperties() {

		visualPropertySet = new HashSet<VisualProperty<?>>();

		visualPropertySet.add(NODE_COLOR);
		visualPropertySet.add(NODE_LABEL);
		visualPropertySet.add(NODE_LABEL_COLOR);
		visualPropertySet.add(NODE_X_LOCATION);
		visualPropertySet.add(NODE_Y_LOCATION);
		visualPropertySet.add(NODE_VISIBLE);
		visualPropertySet.add(NODE_SELECTED);
		visualPropertySet.add(EDGE_COLOR);
		visualPropertySet.add(EDGE_LABEL);
		visualPropertySet.add(EDGE_LABEL_COLOR);
		visualPropertySet.add(EDGE_VISIBLE);
		visualPropertySet.add(EDGE_SELECTED);
		visualPropertySet.add(NETWORK_SCALE_FACTOR);
		visualPropertySet.add(NETWORK_CENTER_X_LOCATION);
		visualPropertySet.add(NETWORK_CENTER_Y_LOCATION);
		visualPropertySet.add(NETWORK_WIDTH);
		visualPropertySet.add(NETWORK_HEIGHT);
		visualPropertySet.add(NETWORK_BACKGROUND_COLOR);
		visualPropertySet.add(NETWORK_TITLE);
	}

	public Set<VisualProperty<?>> getVisualProperties() {
		return Collections.unmodifiableSet( visualPropertySet );
	}
}
