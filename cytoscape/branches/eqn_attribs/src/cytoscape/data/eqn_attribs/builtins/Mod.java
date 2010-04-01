/*
  File: Mod.java

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


public class Mod implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "MOD"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function for a casual user.
	 */
	public String getHelpDescription() { return "Call this with \"MOD(number, divisor)\""; }

	/**
	 *  @returns Double.class or null if there is not exactly 1 arg or the arg is not of type Double nor Integer
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length != 2)
			return null;

		for (final Class argType : argTypes) {
			if (argType != Double.class && argType != Integer.class)
				return null;
		}

		return Double.class;
	}

	/**
	 *  @param args the function arguments which must be two objects of type Double or Integer
	 *  @returns the result of the modulo function evaluation
	 *  @throws ArithmeticException thrown if the 2nd argument is zero
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		final double number;
		if (args[0] instanceof Double)
			number = (Double)args[0];
		else // Assume we got an integer.
			number = (Integer)args[0];

		final double divisor;
		if (args[1] instanceof Double)
			divisor = (Double)args[1];
		else // Assume we got an integer.
			divisor = (Integer)args[1];
		if (divisor == 0.0)
			throw new ArithmeticException("division by zero in call to MOD()!");

		final double result = number % divisor;
		if (Math.signum(result) != Math.signum(divisor))
			return -result;
		else
			return result;
	}
}
