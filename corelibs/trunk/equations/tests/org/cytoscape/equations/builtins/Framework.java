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
package org.cytoscape.equations.interpreter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import junit.framework.*;
import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.equations.Function;
import org.cytoscape.equations.Parser;
import org.cytoscape.equations.builtins.*;


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

	private final EqnCompiler compiler = new EqnCompiler();

	boolean executeTest(final String equation, final Map<String, Object> variablesAndValues, final Object expectedResult)
	{
		final Map<String, Class> varNameToTypeMap = new HashMap<String, Class>();
		for (final String variableName : variablesAndValues.keySet())
			varNameToTypeMap.put(variableName, variablesAndValues.get(variableName).getClass());

		if (!compiler.compile(equation, varNameToTypeMap))
			return false;

		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		for (final String variableName : variablesAndValues.keySet())
			nameToDescriptorMap.put(variableName, new IdentDescriptor(variablesAndValues.get(variableName)));

		return true;
	}
}
