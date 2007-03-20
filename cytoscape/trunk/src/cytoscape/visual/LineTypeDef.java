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
package cytoscape.visual;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *
 * Define line stroke.
 *
 * TODO: need to modify rendering engine to fully support dash lines.
 *
 * @author kono
 *
 */
public enum LineTypeDef {
	SOLID(null),
	DOT("2.0,2.0"),
	DASH("5.0,3.0"),
	DASH_DOT("10.0,2.0,2.0,2.0");

	private float[] strokeDef;

	private LineTypeDef(String def) {
		if (def == null) {
			strokeDef = null;
		} else {
			final String[] parts = def.split(",");
			strokeDef = new float[parts.length];

			for (int i = 0; i < strokeDef.length; i++) {
				strokeDef[i] = Float.parseFloat(parts[i]);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Stroke getStroke(float width) {
		if (strokeDef != null) {
			return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
			                       strokeDef, 0.0f);
		} else {
			return new BasicStroke(width);
		}
	}
}
