/*
  File: AttribParserImpl.java

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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import cytoscape.data.eqn_attribs.parse_tree.*;


class AttribParserImpl implements AttribParser {
	private String eqn;
	private AttribTokeniser tokeniser;
	private Map<String, AttribFunction> nameToFunctionMap;
	private String lastErrorMessage;
	private Node parseTree;
	private Map<String, Class> attribNameToTypeMap;
	private Set<String> attribReferences;

	public AttribParserImpl() {
		this.nameToFunctionMap = new HashMap<String, AttribFunction>();
		this.parseTree = null;
	}

	public void registerFunction(final AttribFunction func) throws IllegalArgumentException {
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
	 *  @param eqn                  a valid attribute equation which must start with an equal sign
	 *  @param attribNameToTypeMap  a list of existing attribute names and their types
	 *  @returns true if the parse succeeded otherwise false
	 */
	public boolean parse(final String eqn, final Map<String, Class> attribNameToTypeMap) {
		if (eqn == null)
			throw new NullPointerException("equation string must not be null!");
		if (eqn.length() < 1 || eqn.charAt(0) != '=')
			throw new NullPointerException("equation string must start with an equal sign!");

		this.eqn = eqn;
		this.attribNameToTypeMap = attribNameToTypeMap;
		this.attribReferences = new TreeSet<String>();
		this.tokeniser = new AttribTokeniser(eqn.substring(1));
		this.lastErrorMessage = null;

		try {
			parseTree = parseExpr(0);
			final AttribToken token = tokeniser.getToken();
			if (token != AttribToken.EOS)
				throw new IllegalStateException("premature end of expression!");
		} catch (final IllegalStateException e) {
			lastErrorMessage = e.getMessage();
			return false;
		} catch (final ArithmeticException e) {
			lastErrorMessage = e.getMessage();
			return false;
		} catch (final IllegalArgumentException e) {
			lastErrorMessage = e.getMessage();
			return false;
		}

		return true;
	}

	/**
	 *  @returns the result type of the parsed equstion if the parse succeded, otherwise null
	 */
	public Class getType() { return parseTree == null ? null : parseTree.getType(); }

	/**
	 *  If parse() failed, this will return the last error messages.
	 *  @returns the last error message of null
	 */
	public String getErrorMsg() { return lastErrorMessage; }

	public Set<String> getAttribReferences() { return attribReferences; }

	/**
	 *  @returns the parse tree.  Must only be called if parse() returns true!
	 */
	public Node getParseTree() { return parseTree; }

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
					exprNode = new BinOpNode(AttribToken.AMPERSAND, exprNode, term);
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
	private Node handleBinaryArithmeticOp(final AttribToken operator, final Node lhs, final Node rhs) {
		if (lhs.getType() == Double.class && rhs.getType() == Double.class)
			return new BinOpNode(operator, lhs, rhs);
		else
			throw new ArithmeticException("incompatible operands for \""
			                              + operator.asString() + "\"! (lhs="
			                              + lhs.toString() + ":" + lhs.getType() + ", rhs="
			                              + rhs.toString() + ":" + rhs.getType() + ")");
	}

	/**
	 *  Deals w/ any necessary type conversions for any binary comparison operation.
	 */
	private Node handleComparisonOp(final AttribToken operator, final Node lhs, final Node rhs) {
		if (lhs.getType() == Double.class && rhs.getType() == Double.class)
			return new BinOpNode(operator, lhs, rhs);
		if (lhs.getType() == String.class && rhs.getType() == String.class)
			return new BinOpNode(operator, lhs, rhs);
		if (lhs.getType() == Boolean.class && rhs.getType() == Boolean.class) {
			if (operator == AttribToken.EQUAL || operator == AttribToken.NOT_EQUAL)
				return new BinOpNode(operator, lhs, rhs);
			else
				throw new IllegalArgumentException("unimplemented comparison "
				                                   + operator.asString()
				                                   + " for boolean operands!");
		}
		else
			throw new IllegalArgumentException("incompatible operands for \""
			                                   + operator.asString() + "\"! (lhs="
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
		if (token == AttribToken.FLOAT_CONSTANT || token == AttribToken.STRING_CONSTANT || token == AttribToken.BOOLEAN_CONSTANT) {
			switch (token) {
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

			final Class attribRefType = attribNameToTypeMap.get(tokeniser.getIdent());
			if (attribRefType == null)
				throw new IllegalStateException("unknown attribute reference name: \"" + tokeniser.getIdent() + "\"!");
			attribReferences.add(tokeniser.getIdent());
			token = tokeniser.getToken();

			// Do we have a default value?
			Object defaultValue = null;
			if (token == AttribToken.COLON) {
				token = tokeniser.getToken();
				if (token != AttribToken.FLOAT_CONSTANT && token != AttribToken.STRING_CONSTANT && token != AttribToken.BOOLEAN_CONSTANT)
					throw new IllegalStateException("expected default value for attribute reference!");
				switch (token) {
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

			return new IdentNode(tokeniser.getIdent(), defaultValue, attribRefType);
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
			return handleUnaryOp(token, factor);
		}

		// 5. function call
		if (token == AttribToken.IDENTIFIER) {
			tokeniser.ungetToken(token);
			return parseFunctionCall(level);
		}

		if (token == AttribToken.ERROR)
			throw new IllegalStateException(tokeniser.getErrorMsg());

		throw new IllegalStateException("we should never get here!");
	}

	private Node handleUnaryOp(final AttribToken operator, final Node operand) {
		if (operand.getType() == Boolean.class || operand.getType() == String.class)
			throw new ArithmeticException("can't apply a unary " + operator.asString()
			                              + " a boolean or string operand!");
		return new UnaryOpNode(operator, operand);
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
		if (functionNameCandidate.equals("DEFINED"))
			return parseDefined();

		final AttribFunction func = nameToFunctionMap.get(functionNameCandidate);
		if (func == null)
			throw new IllegalStateException("call to unknown function " + functionNameCandidate + "()!");

		token = tokeniser.getToken();
		if (token != AttribToken.OPEN_PAREN)
			throw new IllegalStateException("expected '(' after function name \"" + functionNameCandidate + "\"!");

		// Parse the comma-separated argument list.
		final ArrayList<Class> argTypes = new ArrayList<Class>();
		ArrayList<Node> args = new ArrayList<Node>();
		for (;;) {
			token = tokeniser.getToken();
			if (token ==  AttribToken.CLOSE_PAREN)
				break;

			tokeniser.ungetToken(token);
			final Node exprNode = parseExpr(level);
			argTypes.add(exprNode.getType());
			args.add(exprNode);

			token = tokeniser.getToken();
			if (token != AttribToken.COMMA)
				break;
		}

		final Class returnType = func.validateArgTypes(argTypes.toArray(new Class[argTypes.size()]));
		if (returnType == null)
			throw new IllegalStateException("invalid number of type of arguments in call to "
			                                + functionNameCandidate + "()!");

		if (token != AttribToken.CLOSE_PAREN)
			throw new IllegalStateException("expected the closing parenthesis of a call to "
			                                + functionNameCandidate + "!");

		Node[] nodeArray = new Node[args.size()];
		return new FuncCallNode(func, returnType, args.toArray(nodeArray));
	}


	/**
	 *  Implements --> "(" ["{"] ident ["}"] ")".  If the opening brace is found a closing brace is also required.
	 */
	private Node parseDefined() {
		AttribToken token = tokeniser.getToken();
		if (token != AttribToken.OPEN_PAREN)
			throw new IllegalStateException("\"(\" expected after \"DEFINED\"!");
		token = tokeniser.getToken();
		Class attribRefType;
		if (token != AttribToken.DOLLAR) {
			if (token != AttribToken.IDENTIFIER)
				throw new IllegalStateException("attribute reference expected after \"DEFINED(\"!");
			attribRefType = attribNameToTypeMap.get(tokeniser.getIdent());
		}
		else {
			token = tokeniser.getToken();
			if (token != AttribToken.OPEN_BRACE)
				throw new IllegalStateException("\"{\" expected after \"DEFINED($\"!");
			token = tokeniser.getToken();
			if (token != AttribToken.IDENTIFIER)
				throw new IllegalStateException("attribute reference expected after \"DEFINED(${\"!");
			attribRefType = attribNameToTypeMap.get(tokeniser.getIdent());
			token = tokeniser.getToken();
			if (token != AttribToken.CLOSE_BRACE)
				throw new IllegalStateException("\"}\" expected after after \"DEFINED(${" + tokeniser.getIdent() + "\"!");
		}
		token = tokeniser.getToken();
		if (token != AttribToken.CLOSE_PAREN)
			throw new IllegalStateException("missing \")\" in call to DEFINED()!");

		return new BooleanConstantNode(attribRefType != null);
	}
}
