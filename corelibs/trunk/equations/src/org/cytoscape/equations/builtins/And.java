/*
  File: And.java

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


import java.util.ArrayList;
import java.util.List;
import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionUtil;


public class And implements Function {
	/**
	 *  Used to parse the function string.
	 *  @return the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "AND"; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns the logical conjunction of any number of boolean values."; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"AND(logical_expr1,logical_expr2,...,logical_exprN)\"."; }

	public Class getReturnType() { return Boolean.class; }

	/**
	 *  @return Boolean.class or null if there are 0 args or the args are not all of type Boolean
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length == 0)
			return null;

		return Boolean.class;
	}

	/**
	 *  @param args the function arguments which must all be of type Boolean
	 *  @return the result of the function evaluation which is either true or false
	 *  @throws ArithmeticException this can never happen
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Boolean
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		// Now evaluate the function.
		final boolean[] booleans;
		try {
			booleans = FunctionUtil.getBooleans(args);
		} catch (final Exception e) {
			throw new IllegalArgumentException("can't convert an argument or a list element to a boolean in a call to OR()!");
		}

		for (final boolean b : booleans) {
			if (!b)
				return false;
		}

		return true;
	}

	/**
	 *  Used with the equation builder.
	 *
	 *  @param leadingArgs the types of the arguments that have already been selected by the user.
	 *  @return the set of arguments (must be a collection of String.class, Long.class, Double.class,
	 *           Boolean.class and List.class) that are candidates for the next argument.  An empty
	 *           set indicates that no further arguments are valid.
	 */
	public List<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		final List<Class> possibleNextArgs = new ArrayList<Class>();
		possibleNextArgs.add(Boolean.class);
		if (leadingArgs.length > 0)
			possibleNextArgs.add(null);

		return possibleNextArgs;
	}
}
