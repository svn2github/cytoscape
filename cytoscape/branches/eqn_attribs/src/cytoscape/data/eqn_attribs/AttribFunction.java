/*
  File: AttribFunction.java

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


import java.util.Set;


public interface AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	String getName();

	/**
	 *  Used to provide help for users.
	 *  @returns a description of what this function does
	 */
	String getFunctionSummary();

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function
	 */
	String getUsageDescription();

	/**
	 *  @returns the return type for this function (Double.getCLass(), String.class, or Boolean.class)
	 *           or null if the args passed in had the wrong arity or a type mismatch
	 */
	Class validateArgTypes(final Class[] argTypes);

	/**
	 *  Used to invoke this function.
	 *  @param args the function arguments which must correspond in type and number to what getParameterTypes() returns.
	 *  @returns the result of the function evaluation.  The actual type of the returned object will be what getReturnType() returns.
	 *  @throws ArithmeticException thrown if a numeric error, e.g. a division by zero occurred.
	 *  @throws IllegalArgumentException thrown for any error that is not a numeric error, for example if a function only accepts positive numbers and a negative number was passed in.
	 */
	Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException;

	/**
	 *  Used with the equation builder.
	 *
	 *  @params leadingArgs the types of the arguments that have already been selected by the user.
	 *  @returns the set of arguments (must be a collection of String.class, Long.class, Double.class, Boolean.class and List.class) that are candidates for the next argument.  A null return indicates that no further arguments are valid.
	 *  Please note that if the returned set contains a null, this indicates an optional additional argument.
	 */
	Set<Class> getPossibleArgTypes(final Class[] leadingArgs);
}
