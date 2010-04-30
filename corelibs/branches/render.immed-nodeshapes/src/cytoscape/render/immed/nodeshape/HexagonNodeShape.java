
/*
 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.render.immed.nodeshape;

import cytoscape.render.immed.GraphGraphics;

import java.awt.geom.GeneralPath;
import java.awt.Shape;

public class HexagonNodeShape extends AbstractNodeShape {

	private final GeneralPath path; 

	public HexagonNodeShape() {
		super(GraphGraphics.SHAPE_HEXAGON);
		path = new GeneralPath(); 
	}
		
	public Shape getShape(double xMin, double yMin, double xMax, double yMax) {

		path.reset();

		path.moveTo(((2.0d * xMin) + xMax) / 3.0d, yMin);
		path.lineTo(((2.0d * xMax) + xMin) / 3.0d, yMin);
		path.lineTo(xMax, (yMin + yMax) / 2.0d);
		path.lineTo(((2.0d * xMax) + xMin) / 3.0d, yMax);
		path.lineTo(((2.0d * xMin) + xMax) / 3.0d, yMax);
		path.lineTo(xMin, (yMin + yMax) / 2.0d);

		path.closePath();

		return path;
	}
}

