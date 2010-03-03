/*
  File: AttribParser.java

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


public class AttribParser {
	final String eqn;
	final AttribTokeniser tokeniser;

	public AttribParser(final String eqn) {
		if (eqn == null)
			throw new NullPointerException("equation string must not be null!");

		this.eqn = eqn;
		tokeniser = new AttribTokeniser(eqn);
	}

	public boolean parse() {
		try {
			parseE();
			final AttribToken token = tokeniser.getToken();
			if (token != AttribToken.EOS)
				throw new IllegalStateException("premature end of expression!");
		} catch (final IllegalStateException e) {
			System.err.println("D'oh: " + e);
			return false;
		}
		System.out.println("Whoohoo!");
		return true;
	}

	/**
	 *   Implements E --> P {op P}.
	 */
	private void parseE() {
		parseP();

		for (;;) {
			final AttribToken token = tokeniser.getToken();
			if (token == AttribToken.EOS) {
				tokeniser.ungetToken(token);
				return;
			}

			if (token == AttribToken.PLUS || token == AttribToken.MINUS
			    || token == AttribToken.MUL || token == AttribToken.DIV
			    || token == AttribToken.CARET)
			{
				parseP();
			}
			else if (token == AttribToken.EQUAL || token == AttribToken.NOT_EQUAL
				 || token == AttribToken.GREATER_THAN
				 || token == AttribToken.LESS_THAN
				 || token == AttribToken.GREATER_OR_EQUAL
				 || token == AttribToken.LESS_OR_EQUAL)
			{
				parseP();
				return; // Only one trip through the loop for comparison operators!
			} else {
				tokeniser.ungetToken(token);
				return;
			}
		}
	}

	/**
	 *  Implements P --> const | attrib_ref | "(" E ")" | "-" P | function_call
	 */
	private void parseP() {
		AttribToken token = tokeniser.getToken();

		// 1. a constant
		if (token == AttribToken.INTEGER_CONSTANT || token == AttribToken.FLOAT_CONSTANT || token == AttribToken.STRING_CONSTANT)
			return;

		// 2. an attribute reference
		if (token == AttribToken.DOLLAR) {
			token = tokeniser.getToken();
			if (token != AttribToken.IDENTIFIER)
				throw new IllegalStateException("identifier expected!");
		}

		// 3. a parenthesised expression
		if (token == AttribToken.OPEN_PAREN) {
			parseE();
			token = tokeniser.getToken();
			if (token != AttribToken.CLOSE_PAREN)
				throw new IllegalStateException("'(' expected!");
		}

		// 4. a unary operator
		if (token == AttribToken.PLUS || token == AttribToken.MINUS) {
			parseP();
		}

		// 5. function call
		if (token == AttribToken.IDENTIFIER) {
			final String functionNameCandidate = tokeniser.getIdent();

			// Need to look up the function name and, if it is a known function, determine its arity.
			// For now, we assume that it has an arity of 1:
			final int arity = 1;

			token = tokeniser.getToken();
			if (token != AttribToken.OPEN_PAREN)
				throw new IllegalStateException("expected '(' after function name \"" + functionNameCandidate + "\"!");

			// Parse the comma-separated argument list.
			for (int arg_no = 0; arg_no < arity; ++arg_no) {
				parseE();
				if (arg_no < arity - 1) {
					token = tokeniser.getToken();
					if (token != AttribToken.COMMA)
						throw new IllegalStateException("expected a comman in a function argument list, found: " + token + "!");
				}
			}

			token = tokeniser.getToken();
			if (token != AttribToken.CLOSE_PAREN)
				throw new IllegalStateException("expected the closing parenthesis of a function call!");
		}
	}
}
