/*
  File: Abs.java

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
package cytoscape.data.eqn_attribs.builtins;


import java.util.Set;
import java.util.TreeSet;
import cytoscape.data.eqn_attribs.AttribFunction;


public class Abs implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "ABS"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns the absolute value of a number."; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"ABS(number)\""; }

	/**
	 *  @returns Double.class or null if there is not exactly 1 arg or the arg is not of type Double or Integer
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length != 1 || (argTypes[0] != Double.class && argTypes[0] != Integer.class))
			return null;

		return Double.class;
	}

	/**
	 *  @param args the function arguments which must be either one object of type Double or Integer
	 *  @returns the result of the function evaluation which is the natural logarithm of the first argument
	 */
	public Object evaluateFunction(final Object[] args) {
		final double number;
		if (args[0] instanceof Double)
			number = (Double)args[0];
		else // Assume we are dealing with an integer.
			number = (Integer)args[0];

		return Math.abs(number);
	}

	/**
	 *  Used with the equation builder.
	 *
	 *  @params leadingArgs the types of the arguments that have already been selected by the user.
	 *  @returns the set of arguments (must be a collection of String.class, Long.class, Double.class, Boolean.class and List.class) that are candidates for the next argument.  An empty set inicates that no further arguments are valid.
	 */
	public Set<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		if (leadingArgs.length == 0) {
			final Set<Class> possibleNextArgs = new TreeSet<Class>();
			possibleNextArgs.add(Double.class);
			possibleNextArgs.add(Long.class);
			return possibleNextArgs;
		}

		return null;
	}
}
