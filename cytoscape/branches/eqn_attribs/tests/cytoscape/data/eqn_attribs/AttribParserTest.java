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
		assertTrue(parser.parse("=42 - 12 + 3 * (4 - 2) + ${BOB:12}"));
	}

	public void testUnaryPlusAndMinus() throws Exception {
		assertTrue(parser.parse("=-17.8E-14"));
		assertTrue(parser.parse("=+(${attr1} + ${attr2})"));
	}

	public void testFunctionCall() throws Exception {
		assertTrue(parser.parse("=42 + log(4 - 2)"));
	}

	public void testExponentiation() throws Exception {
		assertTrue(parser.parse("=2^3^4 - 0.0002"));
	}

	public void testComparisons() throws Exception {
		assertTrue(parser.parse("=${x} <= ${y}"));
		assertTrue(parser.parse("=-15.4^3 > ${limit}"));
	}

	public void testVarargs() throws Exception {
		assertFalse(parser.parse("=LOG()"));
		assertTrue(parser.parse("=LOG(1)"));
		assertTrue(parser.parse("=LOG(1,2)"));
		assertFalse(parser.parse("=LOG(1,2,3)"));
	}

	public void testFixedargs() throws Exception {
		assertFalse(parser.parse("=ABS()"));
		assertTrue(parser.parse("=ABS(1)"));
		assertFalse(parser.parse("=ABS(1,2)"));
	}

	public void testNOT() throws Exception {
		assertFalse(parser.parse("=NOT()"));
		assertTrue(parser.parse("=NOT(true)"));
		assertTrue(parser.parse("=NOT(false)"));
		assertTrue(parser.parse("=NOT(3.2 < 12)"));
		assertTrue(parser.parse("=NOT(${logical})"));
		assertFalse(parser.parse("=NOT(true, true)"));
	}
}
