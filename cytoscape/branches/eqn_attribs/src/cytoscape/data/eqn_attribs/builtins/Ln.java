/*
  File: Ln.java

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


public class Ln implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "LN"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function for a casual user.
	 */
	public String getHelpDescription() { return "Attempts to emulate the Excel™ LN function.\nCall this with \"LN(number)\""; }

	/**
	 *  @returns Double.class or null if there are not 1 or 2 args or the args are not of type Double
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length != 1 || argTypes[0] != Double.class)
			return null;

		return Double.class;
	}

	/**
	 *  @param args the function arguments which must be one object of type Double
	 *  @returns the result of the function evaluation which is the natural logarithm of the argument
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if the argument is not of type Double
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		final double number = (Double)args[0];
		if (number <= 0.0)
			throw new IllegalArgumentException("LN() called with a number <= 0.0!");

		return  Math.log(number);
	}
}
