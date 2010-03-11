/*
  File: InterpreterTest.java

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
package cytoscape.data.eqn_attribs.interpreter;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import junit.framework.*;
import cytoscape.data.eqn_attribs.AttribEqnCompiler;
import cytoscape.data.eqn_attribs.builtins.*;


public class InterpreterTest extends TestCase {
	private final AttribEqnCompiler compiler = new AttribEqnCompiler();

	protected void setUp() throws Exception {
		compiler.registerFunction(new And());
		compiler.registerFunction(new Or());
		compiler.registerFunction(new Log());
		compiler.registerFunction(new Abs());
		compiler.registerFunction(new Not());
		compiler.registerFunction(new LCase());
		compiler.registerFunction(new UCase());
		compiler.registerFunction(new Substitute());
		compiler.registerFunction(new If());
	}

	public void testSimpleStringConcatExpr() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("s1", String.class);
		assertTrue(compiler.compile("=\"Fred\"&${s1}", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("s1", new IdentDescriptor(String.class, "Bob"));
		final Interpreter interpreter = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals("FredBob", interpreter.run());
	}

	public void testSimpleExpr() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("BOB", Double.class);
		assertTrue(compiler.compile("=42 - 12 + 3 * (4 - 2) + ${BOB:12}", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("BOB", new IdentDescriptor(Double.class, -10.0));
		final Interpreter interpreter = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Double(26.0), interpreter.run());
	}

	public void testUnaryPlusAndMinus() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("attr1", Double.class);
		attribNameToTypeMap.put("attr2", Double.class);
		assertTrue(compiler.compile("=-17.8E-14", attribNameToTypeMap));
		assertTrue(compiler.compile("=+(${attr1} + ${attr2})", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("attr1", new IdentDescriptor(Double.class, 5.5));
		nameToDescriptorMap.put("attr2", new IdentDescriptor(Double.class, 6.5));
		final Interpreter interpreter = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Double(12.0), interpreter.run());
	}

	public void testFunctionCall() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=42 + log(4 - 2)", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Double(42.0 + Math.log10(4.0 - 2.0)), interpreter.run());
	}

	public void testExponentiation() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=2^3^4 - 0.0002", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Double(Math.pow(2.0, Math.pow(3.0, 4.0)) - 0.0002), interpreter.run());
	}

	public void testComparisons() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("x", Double.class);
		attribNameToTypeMap.put("y", Double.class);
		attribNameToTypeMap.put("limit", Double.class);
		assertTrue(compiler.compile("=${x} <= ${y}", attribNameToTypeMap));
		assertTrue(compiler.compile("=-15.4^3 > ${limit}", attribNameToTypeMap));
	}

	public void testVarargs() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertFalse(compiler.compile("=LOG()", attribNameToTypeMap));
		assertTrue(compiler.compile("=LOG(1)", attribNameToTypeMap));
		assertTrue(compiler.compile("=LOG(1,2)", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Double(Math.log(1.0)/Math.log(2.0)), interpreter.run());
		assertFalse(compiler.compile("=LOG(1,2,3)", attribNameToTypeMap));
	}

	public void testFixedargs() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertFalse(compiler.compile("=ABS()", attribNameToTypeMap));
		assertTrue(compiler.compile("=ABS(-1.5e10)", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Double(1.5e10), interpreter.run());
		assertFalse(compiler.compile("=ABS(1,2)", attribNameToTypeMap));
	}

	public void testNOT() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("logical", Boolean.class);
		assertFalse(compiler.compile("=NOT()", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("logical", new IdentDescriptor(Boolean.class, true));
		assertTrue(compiler.compile("=NOT(true)", attribNameToTypeMap));
		final Interpreter interpreter1 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Boolean(false), interpreter1.run());
		assertTrue(compiler.compile("=NOT(false)", attribNameToTypeMap));
		assertTrue(compiler.compile("=NOT(3.2 < 12)", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Boolean(false), interpreter2.run());
		assertTrue(compiler.compile("=NOT(${logical})", attribNameToTypeMap));
		assertFalse(compiler.compile("=NOT(true, true)", attribNameToTypeMap));
	}

	public void testUCASEandLCASE() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=UCASE(\"Fred\")", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter1 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("FRED"), interpreter1.run());
		assertTrue(compiler.compile("=\"bozo\"&LCASE(\"UPPER\")", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("bozoupper"), interpreter2.run());
	}

	public void testSUBSTITUTE() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=SUBSTITUTE(\"ABABBAABAB\", \"A\", \"X\")", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter1 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("XBXBBXXBXB"), interpreter1.run());

		assertTrue(compiler.compile("=Substitute(\"FredBobBillJoeBobHansKarl\", \"Bob\", \"Julie\", 2.4)", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("FredBobBillJoeJulieHansKarl"), interpreter2.run());

		assertTrue(compiler.compile("=Substitute(\"FredBobBillJoeBobHansKarl\", \"Bob\", \"Julie\", 3)", attribNameToTypeMap));
		final Interpreter interpreter3 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("FredBobBillJoeBobHansKarl"), interpreter3.run());

		assertTrue(compiler.compile("=Substitute(\"FredBobBillJoeBobHansKarl\", \"Bob2\", \"Julie\")", attribNameToTypeMap));
		final Interpreter interpreter4 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("FredBobBillJoeBobHansKarl"), interpreter4.run());
	}

	public void testIF() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=IF(2.3 >= 1, \"Xx\", \"Yz\")", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter1 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("Xx"), interpreter1.run());

		assertTrue(compiler.compile("=IF(FALSE, 12.3, -4)", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Double(-4), interpreter2.run());

		assertTrue(compiler.compile("=IF(true, false, true)", attribNameToTypeMap));
		final Interpreter interpreter3 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new Boolean(false), interpreter3.run());

		assertTrue(compiler.compile("=IF(TrUe, 12.3, \"-4\")", attribNameToTypeMap));
		final Interpreter interpreter4 = new Interpreter(compiler.getCode(), nameToDescriptorMap);
		assertEquals(new String("12.3"), interpreter4.run());
	}
}
