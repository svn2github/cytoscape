/*
  File: Framework.java

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
package org.cytoscape.equations.builtins;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.equations.Function;
import org.cytoscape.equations.Parser;
import org.cytoscape.equations.interpreter.IdentDescriptor;
import org.cytoscape.equations.interpreter.Interpreter;


class Framework {
	static private class BadReturnFunction implements Function {
		public String getName() { return "BAD"; }
		public String getFunctionSummary() { return "Returns an invalid type at runtime."; }
		public String getUsageDescription() { return "Call this with \"BAD()\"."; }
		public Class getReturnType() { return Double.class; }
		public Class validateArgTypes(final Class[] argTypes) { return argTypes.length == 0 ? Double.class : null; }
		public Object evaluateFunction(final Object[] args) { return new Integer(1); }
		public List<Class> getPossibleArgTypes(final Class[] leadingArgs) { return null; }
	}

	private static final EqnCompiler compiler = new EqnCompiler();

	static {
		Parser.getParser().registerFunction(new BadReturnFunction());
	}

	/**
	 *  Execute a test that should succeed at compile time and runtime.
	 *  @return true if the test compiled and ran and produced the expected result
	 */
	static boolean executeTest(final String equation, final Map<String, Object> variablesAndValues, final Object expectedResult) {
		final Map<String, Class> varNameToTypeMap = new HashMap<String, Class>();
		for (final String variableName : variablesAndValues.keySet())
			varNameToTypeMap.put(variableName, variablesAndValues.get(variableName).getClass());
		
		try {
			if (!compiler.compile(equation, varNameToTypeMap)) {
				System.err.println("Error while compiling \"" + equation + "\": " + compiler.getLastErrorMsg());
				return false;
			}
		} catch (final Exception e) {
			System.err.println("Error while compiling \"" + equation + "\": " + e.getMessage());
			return false;
		}

		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		try {
			for (final String variableName : variablesAndValues.keySet())
				nameToDescriptorMap.put(variableName, new IdentDescriptor(variablesAndValues.get(variableName)));
		} catch (final Exception e) {
			System.err.println("Error while processing variables for \"" + equation + "\": " + e.getMessage());
			return false;
		}

		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		try {
			final Object actualResult = interpreter.run();
			if (!actualResult.equals(expectedResult)) {
				System.err.println("[" + equation + "] expected: " + expectedResult + ", found: " + actualResult);
				return false;
			} else
				return true;
		} catch (final Exception e) {
			return false;
		}
	}

	static boolean executeTest(final String equation, final Object expectedResult) {
		final Map<String, Object> variablesAndValues = new HashMap<String, Object>();
		return executeTest(equation, variablesAndValues, expectedResult);
	}

	/**
	 *  Excecute a test that should fail at either compile time or runtime.
	 *  @return true if the test fails at compile time or runtime, otherwise false
	 *
	 */
	static boolean executeTestExpectFailure(final String equation, final Map<String, Object> variablesAndValues) {
		final Map<String, Class> varNameToTypeMap = new HashMap<String, Class>();
		for (final String variableName : variablesAndValues.keySet())
			varNameToTypeMap.put(variableName, variablesAndValues.get(variableName).getClass());
		
		try {
			if (!compiler.compile(equation, varNameToTypeMap)) {
				System.err.println("Error while compiling \"" + equation + "\": " + compiler.getLastErrorMsg());
				return true;
			}
		} catch (final Exception e) {
			System.err.println("Error while compiling \"" + equation + "\": " + e.getMessage());
			return true;
		}

		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		try {
			for (final String variableName : variablesAndValues.keySet())
				nameToDescriptorMap.put(variableName, new IdentDescriptor(variablesAndValues.get(variableName)));
		} catch (final Exception e) {
			System.err.println("Error while processing variables for \"" + equation + "\": " + e.getMessage());
			return true;
		}

		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		try {
			final Object result = interpreter.run();
			// We should never get here!
			return false;
		} catch (final Exception e) {
			return true;
		}
	}

	static boolean executeTestExpectFailure(final String equation) {
		final Map<String, Object> variablesAndValues = new HashMap<String, Object>();
		return executeTestExpectFailure(equation, variablesAndValues);
	}
}
