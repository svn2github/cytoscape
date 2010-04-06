/*
  File: Average.java

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


import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import cytoscape.data.eqn_attribs.AttribFunction;


public class Average implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "AVERAGE"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns the average of a group of numbers."; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"AVERAGE(list)\" or \"AVERAGE(arg1,arg2,...,argN)\""; }

	/**
	 *  @returns Double.class or null if there is not either exactly a single list argument nor a list of numeric arguments
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		// An empty argument list is invalid.
		if (argTypes.length == 0)
			return null;

		// A single List argument is valid.
		if (argTypes.length == 1 && argTypes[0] == List.class)
			return Double.class;

		// Any number of numeric arguments are valid.
		for (final Class argType : argTypes) {
			if (argType != Double.class && argType != Long.class)
				return null;
		}

		return Double.class;
	}

	/**
	 *  @param args the function arguments which must be either one or two objects of type Double
	 *  @returns the result of the function evaluation which is the average of the elements in the single list argument or the average of the one or more double arguments
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Double
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		double sum = 0.0;
		double count = 0.0;

		if (args.length == 1 && args[0] instanceof List) {
			final List list = (List)args[0];

			for (final Object listEntry : list) {
				final Class listEntryType = listEntry.getClass();
				final double value;
				if (listEntryType == Double.class)
					value = (Double)listEntry;
				else if (listEntryType == Integer.class)
					value = (Integer)listEntry;
				else if (listEntryType == String.class) {
					try {
						value = Double.parseDouble((String)listEntry);
					} catch (final NumberFormatException e) {
						throw new IllegalArgumentException("can't convert a list element to a number while evaluating a call to AVERAGE()!");
					}
				}
				else
					throw new IllegalArgumentException("can't convert a list element to a number while evaluating a call to AVERAGE()!");

				sum += value;
				++count;
			}
		} else { // We expect any number of numeric args.
			for (final Object arg : args) {
				if (arg instanceof Double)
					sum += (Double)arg;
				else // Must be a Long.
					sum += (Long)arg;
				++count;
			}
		}
			
		if (count == 0.0)
			throw new IllegalArgumentException("can't take the average of an empty list!");

		return sum / count;
	}

	/**
	 *  Used with the equation builder.
	 *
	 *  @params leadingArgs the types of the arguments that have already been selected by the user.
	 *  @returns the set of arguments (must be a collection of String.class, Long.class, Double.class, Boolean.class and List.class) that are candidates for the next argument.  An empty set inicates that no further arguments are valid.
	 */
	public Set<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		if (leadingArgs.length == 1 && leadingArgs[0] == List.class)
			return null;

		final Set<Class> possibleNextArgs = new TreeSet<Class>();
		possibleNextArgs.add(Double.class);
		possibleNextArgs.add(Long.class);
		if (leadingArgs.length == 0)
			possibleNextArgs.add(List.class);
		if (leadingArgs.length > 0)
			possibleNextArgs.add(null);

		return possibleNextArgs;
	}
}
