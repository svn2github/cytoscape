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
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.Line;
import cytoscape.visual.LineTypeDef;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.LineTypeParser;

//import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;
import giny.model.Edge;

import java.util.Properties;


/**
 *
 */
public class GenericEdgeLineWidthCalculator {
	//	public GenericEdgeLineWidthCalculator(String name, ObjectMapping m, Class c) {
	//		super(name, m, c, EDGE_LINE_WIDTH);
	//	}
	//
	//	public GenericEdgeLineWidthCalculator(String name, Properties props,
	//			String baseKey) {
	//		super(name, props, baseKey, new LineTypeParser(), new Line(LineTypeDef.SOLID, 1.0f),
	//				EDGE_LINE_WIDTH);
	//	}
	//	
	//	public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
	//		final Line line = (Line) getRangeValue(edge);
	//
	//		// default has already been set - no need to do anything
	//		if (line == null)
	//			return;
	//
	//		appr.setLine(line);
	//	}
	//
	//	public Line calculateEdgeLineWidth(Edge e, CyNetwork n) {
	//		final EdgeAppearance ea = new EdgeAppearance();
	//		apply(ea, e, n);
	//		return ea.getLine();
	//	}
}
