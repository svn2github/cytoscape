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


import java.util.ArrayList;
import java.util.HashMap;
import cytoscape.data.eqn_attribs.parse_tree.*;


public class AttribParser {
	private String eqn;
	private AttribTokeniser tokeniser;
	private HashMap<String, AttribFunction> nameToFunctionMap;
	private String lastErrorMessage;
	private Node parseTree;

	public AttribParser() {
		this.nameToFunctionMap = new HashMap<String, AttribFunction>();
		this.parseTree = null;
	}

	public void registerFunction(final AttribFunction func) throws IllegalArgumentException {
		// Sanity check for functions with varargs.
		if (func.getMinNumberOfArgsForVariableArity() > -1 && func.getParameterTypes().length != 1)
			throw new IllegalArgumentException("functions can't have varargs and specify anything but a single argument type!");
		if (func.getMinNumberOfArgsForVariableArity() > -1
		    && func.getMinNumberOfArgsForVariableArity() >= func.getMaxNumberOfArgsForVariableArity())
			throw new IllegalArgumentException("functions can't have varargs and specify a min. number of args >= max. number of args!");

		// Sanity check for the parameter types of a function.
		for (final Class c : func.getParameterTypes()) {
			if (c != Long.class && c != Double.class && c != Boolean.class && c != String.class)
				throw new IllegalArgumentException("function arguments must be of type Long, Double, Boolean, or String!");
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

	/**
	 *  @param A valid attribute equation which must start with an equal sign
	 *  @returns true if the parse succeeded otherwise false
	 */
	public boolean parse(final String eqn) {
		if (eqn == null)
			throw new NullPointerException("equation string must not be null!");
		if (eqn.length() < 1 || eqn.charAt(0) != '=')
			throw new NullPointerException("equation string must start with an equal sign!");

		this.eqn = eqn;
		this.tokeniser = new AttribTokeniser(eqn.substring(1));
		this.lastErrorMessage = null;

		try {
			parseTree = parseExpr(0);
			final AttribToken token = tokeniser.getToken();
			if (token != AttribToken.EOS)
				throw new IllegalStateException("premature end of expression!");
		} catch (final IllegalStateException e) {
			lastErrorMessage = e.getMessage();
System.err.println("lastErrorMessage="+lastErrorMessage);
			return false;
		} catch (final ArithmeticException e) {
			lastErrorMessage = e.getMessage();
System.err.println("lastErrorMessage="+lastErrorMessage);
			return false;
		} catch (final IllegalArgumentException e) {
			lastErrorMessage = e.getMessage();
System.err.println("lastErrorMessage="+lastErrorMessage);
			return false;
		}

		return true;
	}

	/**
	 *  If parse() failed, this will return the last error messages.
	 *  @returns the last error message of null
	 */
	public String getErrorMsg() {
		return lastErrorMessage;
	}

	/**
	 *  Only used for unit testing.
	 */
	Node getParseTree() { return parseTree; }

	/**
	 *   Implements expr --> term | term {+ term } | term {- term} .
	 */
	private Node parseExpr(int level) {
		level += 1;
		Node exprNode = parseTerm(level);

		for (;;) {
			final AttribToken token = tokeniser.getToken();
			if (token == AttribToken.EOS) {
				tokeniser.ungetToken(token);
				return exprNode;
			}

			if (token == AttribToken.PLUS || token == AttribToken.MINUS || token == AttribToken.AMPERSAND) {
				final Node term = parseTerm(level);
				if (token == AttribToken.PLUS || token == AttribToken.MINUS)
					exprNode = handleBinaryArithmeticOp(token, exprNode, term);
				else
					exprNode = handleStringConcat(exprNode, term);
			}
			else if (token == AttribToken.EQUAL || token == AttribToken.NOT_EQUAL
				 || token == AttribToken.GREATER_THAN
				 || token == AttribToken.LESS_THAN
				 || token == AttribToken.GREATER_OR_EQUAL
				 || token == AttribToken.LESS_OR_EQUAL)
			{
				final Node term = parseTerm(level);
				return handleComparisonOp(token, exprNode, term); // Only one trip through the loop for comparison operators!
			} else {
				tokeniser.ungetToken(token);
				return exprNode;
			}
		}
	}

	/**
	 *  Deals w/ any necessary type conversions for any binary arithmetic operation on numbers.
	 */
	private Node handleBinaryArithmeticOp(final AttribToken token, final Node lhs, final Node rhs) {
		if (lhs.getType() == Double.class && rhs.getType() == Double.class)
			return new BinOpNode(token, lhs, rhs);
		else if (lhs.getType() == Long.class && rhs.getType() == Long.class)
			return new BinOpNode(token, lhs, rhs);
		else if (lhs.getType() == Double.class && rhs.getType() == Long.class)
			return new BinOpNode(token, lhs, new ConvertIntegerToFloatNode(rhs));
		else if (lhs.getType() == Long.class && rhs.getType() == Double.class)
			return new BinOpNode(token, new ConvertIntegerToFloatNode(lhs), rhs);
		else if (lhs.getType() == Double.class && rhs.getType() == Object.class)
			return new BinOpNode(token, lhs, new DynamicallyConvertToFloatNode(rhs));
		else if (lhs.getType() == Object.class && rhs.getType() == Double.class)
			return new BinOpNode(token, new DynamicallyConvertToFloatNode(lhs), rhs);
		else if (lhs.getType() == Long.class && rhs.getType() == Object.class)
			return new BinOpNode(token, lhs, new DynamicallyConvertToIntegerNode(rhs));
		else if (lhs.getType() == Object.class && rhs.getType() == Long.class)
			return new BinOpNode(token, new DynamicallyConvertToIntegerNode(lhs), rhs);
		else if (lhs.getType() == Double.class && rhs instanceof IdentNode)
			return new BinOpNode(token, lhs, new ConvertIdentToFloatNode((IdentNode)rhs));
		else if (lhs.getType() == Object.class && rhs.getType() == Object.class)
			return new DynamicBinArithmeticOpNode(token, lhs, rhs);
		else if (lhs instanceof IdentNode && rhs.getType() == Double.class)
			return new BinOpNode(token, new ConvertIdentToFloatNode((IdentNode)lhs), rhs);
		else if (lhs.getType() == Long.class && rhs instanceof IdentNode)
			return new BinOpNode(token, lhs, new ConvertIdentToIntegerNode((IdentNode)rhs));
		else if (lhs instanceof IdentNode && rhs.getType() == Long.class)
			return new BinOpNode(token, new ConvertIdentToIntegerNode((IdentNode)lhs), rhs);
		else if (lhs instanceof IdentNode && rhs instanceof IdentNode)
			return new BinIdentOpNode(token, (IdentNode)lhs, (IdentNode)rhs);
		else
			throw new ArithmeticException("incompatible operands for \""
			                              + AttribTokeniser.opTokenToString(token) + "\"! (lhs="
			                              + lhs.toString() + ":" + lhs.getType() + ", rhs="
			                              + rhs.toString() + ":" + rhs.getType() + ")");
	}

	/**
	 *  Deals w/ any necessary type conversions for string concatenation.
	 */
	private Node handleStringConcat(final Node lhs, final Node rhs) {
		if (lhs.getType() == String.class && rhs.getType() == String.class)
			return new BinOpNode(AttribToken.AMPERSAND, lhs, rhs);
		else if (lhs.getType() == String.class)
			return new BinOpNode(AttribToken.AMPERSAND, lhs, new ConvertToStringNode(rhs));
		else if (rhs.getType() == String.class)
			return new BinOpNode(AttribToken.AMPERSAND, new ConvertToStringNode(lhs), rhs);
		else
			return new BinOpNode(AttribToken.AMPERSAND, new ConvertToStringNode(lhs), new ConvertToStringNode(rhs));
	}

	/**
	 *  Deals w/ any necessary type conversions for any binary comparison operation.
	 */
	private Node handleComparisonOp(final AttribToken token, final Node lhs, final Node rhs) {
		if (lhs.getType() == Double.class && rhs.getType() == Double.class)
			return new BinOpNode(token, lhs, rhs);
		else if (lhs.getType() == Long.class && rhs.getType() == Long.class)
			return new BinOpNode(token, lhs, rhs);
		else if (lhs.getType() == Double.class && rhs.getType() == Long.class)
			return new BinOpNode(token, lhs, new ConvertIntegerToFloatNode(rhs));
		else if (lhs.getType() == Double.class && rhs.getType() == Object.class)
			return new BinOpNode(token, lhs, new DynamicallyConvertToFloatNode(rhs));
		else if (lhs.getType() == Long.class && rhs.getType() == Double.class)
			return new BinOpNode(token, new ConvertIntegerToFloatNode(lhs), rhs);
		else if (lhs.getType() == Long.class && rhs.getType() == Object.class)
			return new BinOpNode(token, lhs, new DynamicallyConvertToIntegerNode(rhs));
		else if (lhs.getType() == Object.class && rhs.getType() == Long.class)
			return new BinOpNode(token, new DynamicallyConvertToIntegerNode(lhs), rhs);
		else if (lhs.getType() == Object.class && rhs.getType() == Double.class)
			return new BinOpNode(token, new DynamicallyConvertToFloatNode(lhs), rhs);
		else if (lhs.getType() == Object.class && rhs.getType() == Object.class)
			return new DynamicBinCompNode(token, lhs, rhs);
		else if (lhs.getType() == Double.class && rhs instanceof IdentNode)
			return new BinOpNode(token, lhs, new ConvertIdentToFloatNode((IdentNode)rhs));
		else if (lhs instanceof IdentNode && rhs.getType() == Double.class)
			return new BinOpNode(token, new ConvertIdentToFloatNode((IdentNode)lhs), rhs);
		else if (lhs.getType() == Long.class && rhs instanceof IdentNode)
			return new BinOpNode(token, lhs, new ConvertIdentToIntegerNode((IdentNode)rhs));
		else if (lhs instanceof IdentNode && rhs.getType() == Long.class)
			return new BinOpNode(token, new ConvertIdentToIntegerNode((IdentNode)lhs), rhs);
		else if (lhs instanceof IdentNode && rhs instanceof IdentNode)
			return new BinIdentOpNode(token, (IdentNode)lhs, (IdentNode)rhs);
		else
			throw new ArithmeticException("incompatible operands for \""
			                              + AttribTokeniser.opTokenToString(token) + "\"! (lhs="
			                              + lhs.toString() + ":" + lhs.getType() + ", rhs="
			                              + rhs.toString() + ":" + rhs.getType() + ")");
	}

	/**
	 *  Implements term --> power {* power} | power {/ power}
	 */
	private Node parseTerm(int level) {
		level += 1;
		Node termNode = parsePower(level);

		for (;;) {
			final AttribToken token = tokeniser.getToken();
			if (token == AttribToken.MUL || token == AttribToken.DIV) {
				final Node powerNode = parsePower(level);
				termNode = handleBinaryArithmeticOp(token, termNode, powerNode);
			}
			else {
				tokeniser.ungetToken(token);
				return termNode;
			}
		}
	}

	/**
	 *  Implements power --> factor | factor ^ power
	 */
	private Node parsePower(int level) {
		level += 1;
		Node powerNode = parseFactor(level);

		final AttribToken token = tokeniser.getToken();
		if (token == AttribToken.CARET) {
			final Node rhs = parsePower(level);
			powerNode = handleBinaryArithmeticOp(token, powerNode, rhs);
		}
		else
			tokeniser.ungetToken(token);

		return powerNode;
	}

	/**
	 *  Implements factor --> const | attrib_ref | "(" E ")" | "-" P | func_call
	 */
	private Node parseFactor(int level) {
		level += 1;
		AttribToken token = tokeniser.getToken();

		// 1. a constant
		if (token == AttribToken.INTEGER_CONSTANT || token == AttribToken.FLOAT_CONSTANT
		    || token == AttribToken.STRING_CONSTANT || token == AttribToken.BOOLEAN_CONSTANT)
		{
			switch (token) {
			case INTEGER_CONSTANT:
				return new IntConstantNode(tokeniser.getIntConstant());
			case FLOAT_CONSTANT:
				return new FloatConstantNode(tokeniser.getFloatConstant());
			case BOOLEAN_CONSTANT:
				return new BooleanConstantNode(tokeniser.getBooleanConstant());
			case STRING_CONSTANT:
				return new StringConstantNode(tokeniser.getStringConstant());
			}
		}

		// 2. an attribute reference
		if (token == AttribToken.DOLLAR) {
			token = tokeniser.getToken();
			if (token != AttribToken.OPEN_BRACE)
				throw new IllegalStateException("opening brace expected!");
			token = tokeniser.getToken();
			if (token != AttribToken.IDENTIFIER)
				throw new IllegalStateException("identifier expected!");
			token = tokeniser.getToken();

			// Do we have a default value?
			Object defaultValue = null;
			if (token == AttribToken.COLON) {
				token = tokeniser.getToken();
				if (token != AttribToken.INTEGER_CONSTANT && token != AttribToken.FLOAT_CONSTANT
				    && token != AttribToken.STRING_CONSTANT && token != AttribToken.BOOLEAN_CONSTANT)
					throw new IllegalStateException("expected default value for attribute reference!");
				switch (token) {
				case INTEGER_CONSTANT:
					defaultValue = new Long(tokeniser.getIntConstant());
					break;
				case FLOAT_CONSTANT:
					defaultValue = new Double(tokeniser.getFloatConstant());
					break;
				case BOOLEAN_CONSTANT:
					defaultValue = new Boolean(tokeniser.getBooleanConstant());
					break;
				case STRING_CONSTANT:
					defaultValue = new String(tokeniser.getStringConstant());
					break;
				}
				token = tokeniser.getToken();
			}

			if (token != AttribToken.CLOSE_BRACE)
				throw new IllegalStateException("closeing brace expected!");

			return new IdentNode(tokeniser.getIdent(), defaultValue);
		}

		// 3. a parenthesised expression
		if (token == AttribToken.OPEN_PAREN) {
			final Node exprNode = parseExpr(level);
			token = tokeniser.getToken();
			if (token != AttribToken.CLOSE_PAREN)
				throw new IllegalStateException("'(' expected!");

			return exprNode;
		}

		// 4. a unary operator
		if (token == AttribToken.PLUS || token == AttribToken.MINUS) {
			final Node factor = parseFactor(level);
			return new UnaryOpNode(token, factor);
		}

		// 5. function call
		if (token == AttribToken.IDENTIFIER) {
			tokeniser.ungetToken(token);
			return parseFunctionCall(level);
		}

		throw new IllegalStateException("we should never get here!");
	}

	/**
	 *   Implements func_call --> ident "(" ")" | ident "(" expr {"," expr} ")".
	 */
	private Node parseFunctionCall(int level) {
		level += 1;
		AttribToken token = tokeniser.getToken();
		if (token != AttribToken.IDENTIFIER)
			throw new IllegalStateException();

		final String functionNameCandidate = tokeniser.getIdent().toUpperCase();
		final AttribFunction func = nameToFunctionMap.get(functionNameCandidate);
		if (func == null)
			throw new IllegalStateException("call to unknown function " + functionNameCandidate + "()!");

		final Object[] argTypes = func.getParameterTypes();
		final boolean varargs = func.getMinNumberOfArgsForVariableArity() > -1;
		final int minArity, maxArity;
		if (varargs) {
			minArity = func.getMinNumberOfArgsForVariableArity();
			maxArity = func.getMaxNumberOfArgsForVariableArity();
		} else
			minArity = maxArity = argTypes.length;

		token = tokeniser.getToken();
		if (token != AttribToken.OPEN_PAREN)
			throw new IllegalStateException("expected '(' after function name \"" + functionNameCandidate + "\"!");

		ArrayList<Node> args = new ArrayList<Node>();
		// Parse the comma-separated argument list.
		int argCount = 0;
		for (;;) {
			token = tokeniser.getToken();
			if (token ==  AttribToken.CLOSE_PAREN)
				break;

			++argCount;
			if (argCount > maxArity)
				throw new IllegalStateException("expected the closing parenthesis of a call to "
				                                + functionNameCandidate + "() (1)!");

			tokeniser.ungetToken(token);
			args.add(parseExpr(level));

			token = tokeniser.getToken();
			if (token != AttribToken.COMMA)
				break;
		}

		if (token != AttribToken.CLOSE_PAREN)
			throw new IllegalStateException("expected the closing parenthesis of a call to "
			                                + functionNameCandidate + "() (2)!");

		if (argCount < minArity)
			throw new IllegalStateException("too few arguments in a call to " + functionNameCandidate + "()!");

		Node[] nodeArray = new Node[args.size()];
		return new FuncCallNode(func, args.toArray(nodeArray));
	}
}
