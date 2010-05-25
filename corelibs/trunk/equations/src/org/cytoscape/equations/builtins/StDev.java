/*
  File: StDev.java

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
import java.util.Random;
import org.cytoscape.equations.AttribFunction;
import org.cytoscape.equations.AttribFunctionUtil;
import org.cytoscape.equations.EquationUtil;


public class StDev implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @return the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "STDEV"; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns the sample standard deviation of a list of numbers."; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"STDEV(numbers)\""; }

	public Class getReturnType() { return Double.class; }

	/**
	 *  @return Double.class if the argument types make it at least conceivable that no less than 2 numbers are being passed in
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length == 0 || (argTypes.length == 1 && argTypes[0] != List.class))
			return null;

		return Double.class;
	}

	/**
	 *  @param args the function arguments which must be a list followed by a numeric argument
	 *  @return the result of the function evaluation which is the maximum of the elements in the single list argument or the maximum of the one or more double arguments
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the members of the single List argument cannot be converted to a number
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		final ArrayList<Double> a = new ArrayList();
		for (int i = 0; i < args.length; ++i) {
			if (args[i] instanceof List) {
				final List list = (List)(args[i]);
				for (final Object listElement : list) {
					try {
						a.add(AttribFunctionUtil.getArgAsDouble(listElement));
					} catch (final IllegalArgumentException e) {
						throw new IllegalArgumentException(AttribFunctionUtil.getOrdinal(i) +
										   " element in call to STDEV() is not a number: "
										   + e.getMessage());
					}
				}
			} else {
				try {
					a.add(AttribFunctionUtil.getArgAsDouble(args[i]));
				} catch (final IllegalArgumentException e) {
					throw new IllegalArgumentException(AttribFunctionUtil.getOrdinal(i) +
									   " element in call to STDEV() is not a number: "
									   + e.getMessage());
				}
			}
		}

		if (a.size() < 2)
			throw new IllegalArgumentException("illegal list argument in call to STDEV(): must have at least 2 elements!");

		return Math.sqrt(AttribFunctionUtil.calcSampleVariance(AttribFunctionUtil.arrayListToArray(a)));
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
		if (leadingArgs.length == 1) {
			if (leadingArgs[0] == List.class)
				possibleNextArgs.add(null);
		} else if (leadingArgs.length > 1)
			possibleNextArgs.add(null);

		possibleNextArgs.add(List.class);
		possibleNextArgs.add(Double.class);
		possibleNextArgs.add(Long.class);
		possibleNextArgs.add(Boolean.class);
		possibleNextArgs.add(String.class);

		return possibleNextArgs;
	}
}
