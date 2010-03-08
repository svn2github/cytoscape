/*
  File: AttribParserTest.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data.eqn_attribs;

import java.util.HashMap;
import java.util.Map;
import junit.framework.*;
import cytoscape.data.eqn_attribs.builtins.*;


public class AttribParserTest extends TestCase {
	private final AttribParser parser = new AttribParser();

	protected void setUp() throws Exception {
		parser.registerFunction(new And());
		parser.registerFunction(new Or());
		parser.registerFunction(new Log());
		parser.registerFunction(new Abs());
		parser.registerFunction(new Not());
	}

	public void testSimpleExpr() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("BOB", Double.class);
		assertTrue(parser.parse("=42 - 12 + 3 * (4 - 2) + ${BOB:12}", attribNameToTypeMap));
	}

	public void testUnaryPlusAndMinus() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("attr1", Double.class);
		attribNameToTypeMap.put("attr2", Double.class);
		assertTrue(parser.parse("=-17.8E-14", attribNameToTypeMap));
		assertTrue(parser.parse("=+(${attr1} + ${attr2})", attribNameToTypeMap));
	}

	public void testFunctionCall() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(parser.parse("=42 + log(4 - 2)", attribNameToTypeMap));
	}

	public void testExponentiation() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(parser.parse("=2^3^4 - 0.0002", attribNameToTypeMap));
	}

	public void testComparisons() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("x", Double.class);
		attribNameToTypeMap.put("y", Double.class);
		attribNameToTypeMap.put("limit", Double.class);
		assertTrue(parser.parse("=${x} <= ${y}", attribNameToTypeMap));
		assertTrue(parser.parse("=-15.4^3 > ${limit}", attribNameToTypeMap));
	}

	public void testVarargs() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertFalse(parser.parse("=LOG()", attribNameToTypeMap));
		assertTrue(parser.parse("=LOG(1)", attribNameToTypeMap));
		assertTrue(parser.parse("=LOG(1,2)", attribNameToTypeMap));
		assertFalse(parser.parse("=LOG(1,2,3)", attribNameToTypeMap));
	}

	public void testFixedargs() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertFalse(parser.parse("=ABS()", attribNameToTypeMap));
		assertTrue(parser.parse("=ABS(1)", attribNameToTypeMap));
		assertFalse(parser.parse("=ABS(1,2)", attribNameToTypeMap));
	}

	public void testNOT() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("logical", Boolean.class);
		assertFalse(parser.parse("=NOT()", attribNameToTypeMap));
		assertTrue(parser.parse("=NOT(true)", attribNameToTypeMap));
		assertTrue(parser.parse("=NOT(false)", attribNameToTypeMap));
		assertTrue(parser.parse("=NOT(3.2 < 12)", attribNameToTypeMap));
		assertTrue(parser.parse("=NOT(${logical})", attribNameToTypeMap));
		assertFalse(parser.parse("=NOT(true, true)", attribNameToTypeMap));
	}
}
