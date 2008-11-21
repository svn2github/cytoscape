/*
  File: ColorParser.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.vizmap.parsers;

import org.cytoscape.vizmap.ValueParser;

import java.awt.*;
import java.util.StringTokenizer;



/**
 * Parses a String into a Color object.
 */
public class ColorParser
    implements ValueParser {
    /**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object parseStringValue(String value) {
        return parseColor(value);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Color parseColor(String value) {
		// Start by seeing if this is a hex representation
		if (value.startsWith("#")) {
			try {
				Color c = Color.decode(value);
				return c;
			} catch (NumberFormatException e) {
				return Color.black;
			}
		}

		StringTokenizer strtok = new StringTokenizer(value, ",");

		if (strtok.countTokens() != 3) {
			return Color.black;
		}

		String red = strtok.nextToken().trim();
		String green = strtok.nextToken().trim();
		String blue = strtok.nextToken().trim();

		try {
			int r = Integer.parseInt(red);
			int g = Integer.parseInt(green);
			int b = Integer.parseInt(blue);

			return new Color(r, g, b);
		} catch (NumberFormatException e) {
			return Color.black;
		}
	} 

	public static String getRGBText(Color color) {
		Integer red = Integer.valueOf(color.getRed());
		Integer green = Integer.valueOf(color.getGreen());
		Integer blue = Integer.valueOf(color.getBlue());

		return new String(red.toString() + "," + green.toString() + "," + blue.toString());
	} 
}
