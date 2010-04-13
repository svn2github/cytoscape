/*
  File: Substitute.java

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


public class Substitute implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "SUBSTITUTE"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of what this function does
	 */
	public String getFunctionSummary() { return "Replaces some text with other text."; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"SUBSTITUTE(text, original, replacement [, nth_appearance])\""; }

	public Class getReturnType() { return String.class; }

	/**
	 *  @returns String.class or null if the args passed in had the wrong arity or a type mismatch
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length < 3 || argTypes.length > 4 || (argTypes.length == 4 && argTypes[3] != Double.class))
			return null;

		return String.class;
	}

	/**
	 *  @param args the function arguments which must be either one or two objects of type String
	 *  @returns the result of the function evaluation which is the natural logarithm of the first argument
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Boolean
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		final String text        = args[0].toString();
		final String original    = args[1].toString();
		final String replacement = args[2].toString();

		if (args.length == 3)
			return replaceAll(text, original, replacement);
		else { // Assume args.length == 4
			final int nthAppearance = (int)Math.round((Double)args[3] - 0.5);
			if (nthAppearance <= 0)
				return text;
			final int startIndex;
			if ((startIndex = findNth(nthAppearance, text, original)) == -1)
				return text;

			return text.substring(0, startIndex) + replacement + text.substring(startIndex + original.length());
		}
	}

	/**
	 *  Used with the equation builder.
	 *
	 *  @params leadingArgs the types of the arguments that have already been selected by the user.
	 *  @returns the set of arguments (must be a collection of String.class, Long.class, Double.class, Boolean.class and List.class) that are candidates for the next argument.  An empty set inicates that no further arguments are valid.
	 */
	public List<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		if (leadingArgs.length > 3)
			return null;

		final List<Class> possibleNextArgs = new ArrayList<Class>();
		if (leadingArgs.length < 3)
			possibleNextArgs.add(String.class);
		else { // Assume leadingArgs.length == 3.
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(null);
		}

		return possibleNextArgs;
	}

	/**
	 *  @returns the 0-based starting position of the nth appearance of "needle" in "hayStack" or -1 if it can't be found
	 */
	private static int findNth(final int n, final String hayStack, final String needle) {
		int startOffset = 0;
		int offset = 0;
		for (int i = 0; i < n; ++i) {
			if ((offset = hayStack.indexOf(needle, startOffset)) == -1)
				return offset;
			startOffset = offset + needle.length();
		}

		return offset;
	}

	/**
	 *  @returns a string where all occurrences of "original" found in "s" have been replaced with "replacement"
	 */
	private static String replaceAll(final String s, final String original, final String replacement) {
		final StringBuilder builder = new StringBuilder();

		int startOffset = 0;
		int startMatch;
		while ((startMatch = s.indexOf(original, startOffset)) != -1) {
			builder.append(s.substring(startOffset, startMatch));
			builder.append(replacement);
			startOffset = startMatch + original.length();
		}
		builder.append(s.substring(startOffset));

		return builder.toString();
	}
}
