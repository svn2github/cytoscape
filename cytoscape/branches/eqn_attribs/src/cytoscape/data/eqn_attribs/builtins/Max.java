/*
  File: Max.java

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


import java.util.ArrayList;
import java.util.List;
import cytoscape.data.eqn_attribs.AttribFunction;


public class Max implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "MAX"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns the maximum of a group of numbers."; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"MAX(list)\" or \"MAX(arg1,arg2,...,argN)\""; }

	public Class getReturnType() { return Double.class; }

	/**
	 *  @returns Double.class or null if there is not exactly a single list argument, or one or more arguments which might be converted to double
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length == 0) // No empty argument list!
			return null;
		if (argTypes[0] == List.class && argTypes.length != 1) // If we have a list argument it must be the only one!
			return null;

		if (argTypes.length == 1 && argTypes[0] == List.class)
			return Double.class;

		for (final Class argType : argTypes) {
			if (argType != Double.class && argType != Long.class)
				return null;
		}

		return Double.class;
	}

	/**
	 *  @param args the function arguments which must be either one or two objects of type Double
	 *  @returns the result of the function evaluation which is the maximum of the elements in the single list argument or the maximum of the one or more double arguments
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Double
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		double max = Double.NEGATIVE_INFINITY;

		if (args[0] instanceof List) {
			final List list = (List)args[0];

			for (final Object listEntry : list) {
				final Class listEntryType = listEntry.getClass();
				final double value;
				if (listEntryType == Double.class)
					value = (Double)listEntry;
				else if (listEntryType == Long.class)
					value = (Long)listEntry;
				else if (listEntryType == Integer.class)
					value = (Integer)listEntry;
				else if (listEntryType == String.class) {
					try {
						value = Double.parseDouble((String)listEntry);
					} catch (final NumberFormatException e) {
						throw new IllegalArgumentException("can't convert a list element to a number while evaluating a call to MAX()!");
					}
				}
				else
					throw new IllegalArgumentException("can't convert a list element to a number while evaluating a call to MAX()!");

				if (value > max)
					max = value;
			}
		} else { // One or more individual numbers.
			for (final Object arg : args) {
				final double value;
				if (arg.getClass() == Double.class)
					value = (Double)arg;
				else if (arg.getClass() == Long.class)
					value = (Long)arg;
				else if (arg.getClass() == String.class) {
					try {
						value = Double.parseDouble((String)arg);
					} catch (final NumberFormatException e) {
						throw new IllegalArgumentException("can't convert an argument of MAX() to a number!");
					}
				}
				else
					throw new IllegalArgumentException("can't convert an argument of MAX() to a number!");

				if (value > max)
					max = value;
			}
		}

		return max;
	}

	/**
	 *  Used with the equation builder.
	 *
	 *  @params leadingArgs the types of the arguments that have already been selected by the user.
	 *  @returns the set of arguments (must be a collection of String.class, Long.class, Double.class, Boolean.class and List.class) that are candidates for the next argument.  An empty set inicates that no further arguments are valid.
	 */
	public List<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		if (leadingArgs.length == 1 && leadingArgs[0] == List.class)
			return null;

		final List<Class> possibleNextArgs = new ArrayList<Class>();
		possibleNextArgs.add(Double.class);
		possibleNextArgs.add(Long.class);
		if (leadingArgs.length == 0)
			possibleNextArgs.add(List.class);

		return possibleNextArgs;
	}
}
