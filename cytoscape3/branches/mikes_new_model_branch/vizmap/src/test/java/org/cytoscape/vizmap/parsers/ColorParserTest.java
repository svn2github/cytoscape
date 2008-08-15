/*
  File: ColorParserTest.java

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


import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.*;


/**
 *
 */
public class ColorParserTest extends TestCase {
	private ColorParser parser;

	/**
	 * Creates a new MiscTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public ColorParserTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
		parser = new ColorParser();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testParseRGB() throws Exception {
		Color result = parser.parseColor("0,0,0");
		assertTrue(result.equals(Color.black));

		result = parser.parseColor("27,39,121");
		assertTrue(result.getRed() == 27);
		assertTrue(result.getGreen() == 39);
		assertTrue(result.getBlue() == 121);

		result = parser.parseColor(" 27 , 39 , 121 ");
		assertTrue(result.getRed() == 27);
		assertTrue(result.getGreen() == 39);
		assertTrue(result.getBlue() == 121);

		result = parser.parseColor("255,255,255");
		assertTrue(result.equals(Color.white));

		result = parser.parseColor("#334455");
		assertTrue(result.getRed() == 51);
		assertTrue(result.getGreen() == 68);
		assertTrue(result.getBlue() == 85);

		result = parser.parseColor("#FFFFFF");
		assertTrue(result.equals(Color.white));

		result = parser.parseColor("#errroor");
		assertTrue(result.equals(Color.black));

	} 


	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(ColorParserTest.class));
	}
} 
