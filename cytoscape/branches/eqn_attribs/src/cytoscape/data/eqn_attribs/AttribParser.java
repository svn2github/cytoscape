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


import java.util.HashMap;


public class AttribParser {
	private String eqn;
	private AttribTokeniser tokeniser;
	private HashMap<String, AttribFunction> nameToFunctionMap;

	public AttribParser() {
		this.nameToFunctionMap = new HashMap<String, AttribFunction>();
	}

	public void registerFunction(final AttribFunction func) throws IllegalArgumentException {
		// Sanity check for functions with varargs.
		if (func.getMinNumberOfArgsForVariableArity() > -1 && func.getParameterTypes().length != 1)
			throw new IllegalArgumentException("functions can't have varargs and specify anything but a single argument type!");

		// Sanity check for the parameter types of a function.
		for (final Class c : func.getParameterTypes()) {
			if (c != Integer.class && c != Double.class && c != Boolean.class && c != String.class)
				throw new IllegalArgumentException("function arguments must be of type Integer, Double, Boolean, or String!");
		}

		// Sanity check for the name of the function.
		final String funcName = func.getName().toUpperCase();
		if (funcName == null || funcName.equals(""))
			throw new IllegalArgumentException("empty or missing function name!");

		// Sanity check to catch duplicate function registrations.
		if (nameToFunctionMap.get(funcName) != null)
			throw new IllegalArgumentException("attempt at registering " + funcName + "() twice!");

		nameToFunctionMap.put(funcName, func);
	}

	public boolean parse(final String eqn) {
		if (eqn == null)
			throw new NullPointerException("equation string must not be null!");

		this.eqn = eqn;
		this.tokeniser = new AttribTokeniser(eqn);

		try {
			parseExpr(0);
			final AttribToken token = tokeniser.getToken();
			if (token != AttribToken.EOS)
				throw new IllegalStateException("premature end of expression!");
		} catch (final IllegalStateException e) {
			return false;
		}
		System.out.println("Whoohoo!");
		return true;
	}

	/**
	 *   Implements expr --> term | term {+ term } | term {- term} .
	 */
	private void parseExpr(int level) {
		level += 1;
		parseTerm(level);

		for (;;) {
			final AttribToken token = tokeniser.getToken();
			if (token == AttribToken.EOS) {
				tokeniser.ungetToken(token);
				return;
			}

			if (token == AttribToken.PLUS || token == AttribToken.MINUS) {
				parseTerm(level);
			}
			else if (token == AttribToken.EQUAL || token == AttribToken.NOT_EQUAL
				 || token == AttribToken.GREATER_THAN
				 || token == AttribToken.LESS_THAN
				 || token == AttribToken.GREATER_OR_EQUAL
				 || token == AttribToken.LESS_OR_EQUAL)
			{
				parseTerm(level);
				return; // Only one trip through the loop for comparison operators!
			} else {
				tokeniser.ungetToken(token);
				return;
			}
		}
	}

	/**
	 *  Implements term --> power {* power} | power {/ power}
	 */
	private void parseTerm(int level) {
		level += 1;
		parsePower(level);

		for (;;) {
			final AttribToken token = tokeniser.getToken();
			if (token == AttribToken.MUL || token == AttribToken.DIV) {
				parsePower(level);
			}
			else {
				tokeniser.ungetToken(token);
				return;
			}
		}
	}

	/**
	 *  Implements power --> factor | factor ^ power
	 */
	private void parsePower(int level) {
		level += 1;
		parseFactor(level);

		final AttribToken token = tokeniser.getToken();
		if (token == AttribToken.CARET) {
			parsePower(level);
		}
		else {
			tokeniser.ungetToken(token);
			return;
		}
	}

	/**
	 *  Implements factor --> const | attrib_ref | "(" E ")" | "-" P | function_call
	 */
	private void parseFactor(int level) {
		level += 1;
		AttribToken token = tokeniser.getToken();

		// 1. a constant
		if (token == AttribToken.INTEGER_CONSTANT || token == AttribToken.FLOAT_CONSTANT || token == AttribToken.STRING_CONSTANT) {

			return;
		}

		// 2. an attribute reference
		if (token == AttribToken.DOLLAR) {
			token = tokeniser.getToken();
			if (token != AttribToken.IDENTIFIER)
				throw new IllegalStateException("identifier expected!");

			return;
		}

		// 3. a parenthesised expression
		if (token == AttribToken.OPEN_PAREN) {
			parseExpr(level);
			token = tokeniser.getToken();
			if (token != AttribToken.CLOSE_PAREN)
				throw new IllegalStateException("'(' expected!");

			return;
		}

		// 4. a unary operator
		if (token == AttribToken.PLUS || token == AttribToken.MINUS) {
			parseFactor(level);

			return;
		}

		// 5. function call
		if (token == AttribToken.IDENTIFIER) {
			final String functionNameCandidate = tokeniser.getIdent().toUpperCase();
			final AttribFunction func = nameToFunctionMap.get(functionNameCandidate);
			if (func == null)
				throw new IllegalStateException("call to unknown function " + functionNameCandidate+ "()!");

			final int arity = 1;

			token = tokeniser.getToken();
			if (token != AttribToken.OPEN_PAREN)
				throw new IllegalStateException("expected '(' after function name \"" + functionNameCandidate + "\"!");

			// Parse the comma-separated argument list.
			for (int arg_no = 0; arg_no < arity; ++arg_no) {
				parseExpr(level);
				if (arg_no < arity - 1) {
					token = tokeniser.getToken();
					if (token != AttribToken.COMMA)
						throw new IllegalStateException("expected a comman in a function argument list, found: " + token + "!");
				}
			}

			token = tokeniser.getToken();
			if (token != AttribToken.CLOSE_PAREN)
				throw new IllegalStateException("expected the closing parenthesis of a function call!");

			return;
		}
	}
}
