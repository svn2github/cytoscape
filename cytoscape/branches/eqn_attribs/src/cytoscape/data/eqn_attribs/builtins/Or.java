/*
  File: Or.java

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

import cytoscape.data.eqn_attribs.AttribFunction;


public class Or implements AttribFunction {
	/**
	 *  Used to parse the function string.
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "OR"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function for a casual user.
	 */
	public String getHelpDescription() { return "Attempts to emulate the Excel™ OR function.\nCall this with \"OR(logical_expr1,logical_expr2,...,logical_exprN)\"."; }

	/**
	 *  @returns Boolean.class or null if there are 0 args or the args are not all of type Boolean
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length == 0)
			return null;

		for (final Class argType : argTypes) {
			if (argType != Boolean.class)
				return null;
		}

		return Boolean.class;
	}

	/**
	 *  @param args the function arguments which must all be of type Boolean
	 *  @returns the result of the function evaluation which is either true or false
	 *  @throws ArithmeticException this can never happen
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Boolean
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		for (final Object arg : args) {
			if ((Boolean)arg)
				return true;
		}

		return false;
	}
}
