/*
  File: MiscTest.java

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

// MiscTest.java

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.util;

import cytoscape.AllTests;

import cytoscape.util.Misc;

//--------------------------------------------------------------------------------------
import junit.framework.*;

import java.awt.Color;

import java.io.*;

import java.util.*;


//------------------------------------------------------------------------------
/**
 *
 */
public class MiscTest extends TestCase {
	private boolean runAll = false;

	//------------------------------------------------------------------------------
	/**
	 * Creates a new MiscTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public MiscTest(String name) {
		super(name);
	}

	//------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	//------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	//------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testParseRGBTest() throws Exception {
		AllTests.standardOut("testRGBParseText");

		Color result = Misc.parseRGBText("0,0,0");
		assertTrue(result.equals(Color.black));

		result = Misc.parseRGBText("27,39,121");
		assertTrue(result.getRed() == 27);
		assertTrue(result.getGreen() == 39);
		assertTrue(result.getBlue() == 121);

		result = Misc.parseRGBText(" 27 , 39 , 121 ");
		assertTrue(result.getRed() == 27);
		assertTrue(result.getGreen() == 39);
		assertTrue(result.getBlue() == 121);

		result = Misc.parseRGBText("255,255,255");
		assertTrue(result.equals(Color.white));
	} // testParseRGBTest
	  //-------------------------------------------------------------------------

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testParseList() throws Exception {
		AllTests.standardOut("testParseList");

		String delimiter = "::";
		String startToken = "(";
		String endToken = ")";

		// --------------------------------------------------------------------
		// 1) four tokens, separated by ::, surrounded with parens and whitespace
		// --------------------------------------------------------------------
		String a = "abcd";
		String b = "efgh";
		String c = "dog";
		String d = "cat";

		StringBuffer sb = new StringBuffer();
		sb.append("  ");
		sb.append(startToken);
		sb.append(a + delimiter);
		sb.append(b + delimiter);
		sb.append(c + delimiter);
		sb.append(d);
		sb.append(endToken);
		sb.append("   ");

		String[] tokens = Misc.parseList(sb.toString(), startToken, endToken, delimiter);

		assertTrue(tokens.length == 4);
		assertTrue(tokens[0].equals(a));
		assertTrue(tokens[1].equals(b));
		assertTrue(tokens[2].equals(c));
		assertTrue(tokens[3].equals(d));

		// --------------------------------------------------------------------------
		// 2) add some embedded parens to the tokens.  they should survive the parsing
		// --------------------------------------------------------------------------
		a = "((abcd))";
		b = "ef()gh";
		c = "do))((g";
		d = "c*())*at";

		sb = new StringBuffer();
		sb.append("  ");
		sb.append(startToken);
		sb.append(a + delimiter);
		sb.append(b + delimiter);
		sb.append(c + delimiter);
		sb.append(d);
		sb.append(endToken);
		sb.append("  ");

		tokens = Misc.parseList(sb.toString(), startToken, endToken, delimiter);
		assertTrue(tokens.length == 4);
		assertTrue(tokens[0].equals(a));
		assertTrue(tokens[1].equals(b));
		assertTrue(tokens[2].equals(c));
		assertTrue(tokens[3].equals(d));

		// --------------------------------------------------------------------------
		// 3) leave off the startToken.  
		// --------------------------------------------------------------------------
		a = "abcd))";
		b = "ef()gh";
		c = "do))((g";
		d = "c*())*at";

		sb = new StringBuffer();
		sb.append(a + delimiter);
		sb.append(b + delimiter);
		sb.append(c + delimiter);
		sb.append(d);
		sb.append(endToken);
		sb.append("   ");

		tokens = Misc.parseList(sb.toString(), startToken, endToken, delimiter);
		assertTrue(tokens.length == 1);

		// --------------------------------------------------------------------------
		// 4) leave off the endToken.  
		// --------------------------------------------------------------------------
		a = "abcd))";
		b = "ef()gh";
		c = "do))((g";
		d = "c*())*at";

		sb = new StringBuffer();
		sb.append(startToken);
		sb.append(a + delimiter);
		sb.append(b + delimiter);
		sb.append(c + delimiter);
		sb.append(d);
		sb.append("   ");

		tokens = Misc.parseList(sb.toString(), startToken, endToken, delimiter);
		assertTrue(tokens.length == 1);

		// --------------------------------------------------------------------------
		// 5) test some url's, which have embedded ":"
		// --------------------------------------------------------------------------
		a = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00600";
		b = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00562";
		c = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00500";
		d = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00860";

		String e = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00760";

		sb = new StringBuffer();
		sb.append(startToken);
		sb.append(a + delimiter);
		sb.append(b + delimiter);
		sb.append(c + delimiter);
		sb.append(d + delimiter);
		sb.append(e);
		sb.append(endToken);

		tokens = Misc.parseList(sb.toString(), startToken, endToken, delimiter);
		assertTrue(tokens.length == 5);

		assertTrue(tokens[0].equals(a));
		assertTrue(tokens[1].equals(b));
		assertTrue(tokens[2].equals(c));
		assertTrue(tokens[3].equals(d));
		assertTrue(tokens[4].equals(e));
	} // testParseList
	  //------------------------------------------------------------------------------

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetPropertyValues() throws Exception {
		AllTests.standardOut("testGetPropertyValues");

		Properties props = new Properties();
		props.put("dog", "dozer");
		props.put("cat", "(ernie::louie)");

		Vector dogs = Misc.getPropertyValues(props, "dog");
		assertTrue(dogs.size() == 1);

		Vector cats = Misc.getPropertyValues(props, "cat");
		assertTrue(cats.size() == 2);

		String[] catNames = (String[]) cats.toArray(new String[0]);
		assertTrue(catNames.length == 2);
		assertTrue(catNames[0].equals("ernie"));
		assertTrue(catNames[1].equals("louie"));
	} // testGetPropertyValues
	  //------------------------------------------------------------------------------

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(MiscTest.class));
	}

	//------------------------------------------------------------------------------
} // MiscTest
