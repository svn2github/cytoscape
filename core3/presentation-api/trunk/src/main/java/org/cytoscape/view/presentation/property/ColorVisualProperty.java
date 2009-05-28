
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
import java.util.StringTokenizer;
import org.cytoscape.view.model.AbstractVisualProperty;

public class ColorVisualProperty extends AbstractVisualProperty<Color> { 

	public ColorVisualProperty(final String ot, final Color def, final String id, final String name) {
		super(ot,def,id,name);
	}
	
	public String getSerializableString(final Color color) {
		Integer red = new Integer(color.getRed());
		Integer green = new Integer(color.getGreen());
		Integer blue = new Integer(color.getBlue());

		return new String(red.toString() + "," + green.toString() + "," + blue.toString());
	}

	public Color parseSerializableString(final String text) {
		// Start by seeing if this is a hex representation
		if (text.startsWith("#")) {
			try {
				Color c = Color.decode(text);
				return c;
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("invalid hex RGB format");	
			}
		}

		// ok, this must be 3 comma separated integers instead
		StringTokenizer strtok = new StringTokenizer(text, ",");

		if (strtok.countTokens() != 3) 
			throw new IllegalArgumentException("not all RGB integers specified");	

		String red = strtok.nextToken().trim();
		String green = strtok.nextToken().trim();
		String blue = strtok.nextToken().trim();

		try {
			int r = Integer.parseInt(red);
			int g = Integer.parseInt(green);
			int b = Integer.parseInt(blue);

			return new Color(r, g, b);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("invalid RGB format");	
		}
	}
}
