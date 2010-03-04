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


public interface AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	String getName();

	/**
	 *  Used to parse the function parameters.  The only entries that are allowed in the returned
	 *  array are Integer.class, Double.getCLass(), String.class, or Boolean.class.
	 *  @returns the list of argument types for this function
	 */
	Class[] getParameterTypes();
 
	/**
	 *  Used to define the function's return type. 
	 *  @returns must be Integer.class, Double.getCLass(), String.class, or Boolean.class.
	 */
	Class getReturnType();

	/**
	 *  This confunsingly named method should return -1 if the function cannot take a variable
	 *  number of arguments.  If the function can take a variable number of arguments this method
	 *  should return the minimum number of arguments.  (N.B., there is no maximum.)  In the case
	 *  of a variable number of arguments getParameterTypes() must return an array with a single
	 *  entry which will then be used to validate all parameters.  This implies of course that it
	 *  is impossible to define a function with a variable number of arguments where the
	 *  arguments may have differing types.
	 *  @returns the minimum number of args for a vararg function or -1 if the function may not have a variable number of args
	 */
	int getMinNumberOfArgsForVariableArity();

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function for a casual user.
	 */
	String getHelpDescription();

	/**
	 *  Used to invoke this function.
	 *  @param args the function arguments which must correspond in type and number to what getParameterTypes() returns.
	 *  @returns the result of the function evaluation.  The actual type of the returned object will be what getReturnType() returns.
	 *  @throws ArithmeticException thrown if a numeric error, e.g. a division by zero occurred.
	 *  @throws IllegalArgumentException thrown for any error that is not a numeric error, for example if a function only accepts positive
	 *  numbers and a negative number was passed in.
	 */
	Object evaluateFunction(Object... args) throws IllegalArgumentException, ArithmeticException;
}
