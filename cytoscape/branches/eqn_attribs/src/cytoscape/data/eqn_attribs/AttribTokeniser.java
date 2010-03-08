/*
  File: AttribTokeniser.java

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

import java.io.IOException;
import java.io.StringReader;


public class AttribTokeniser {
	private AttribToken previousToken;
	private StringReader reader;
	private int stringPos, errorPos;
	private long previousIntConstant, currentIntConstant;
	private String previousIdent, currentIdent;
	private double previousFloatConstant, currentFloatConstant;
	private boolean previousBooleanConstant, currentBooleanConstant;
	private String previousStringConstant, currentStringConstant;
	private String errorMsg;
	private int previousChar;
	private boolean putBackChar;

	public AttribTokeniser(final String equationAsString) {
		previousToken = null;
		reader = new StringReader(equationAsString);
		stringPos = 0;
		putBackChar = false;
	}

	public AttribToken getToken() {
		if (previousToken != null) {
			final AttribToken retval = previousToken;
			previousToken = null;

			currentIntConstant = previousIntConstant;
			currentFloatConstant = previousFloatConstant;
			currentBooleanConstant = previousBooleanConstant;
			currentStringConstant = previousStringConstant;
			currentIdent = previousIdent;

			return retval;
		}

		int nextCh = getChar();
		while (nextCh != -1 && Character.isWhitespace((char)nextCh))
			nextCh = getChar();

		if (nextCh == -1)
			return AttribToken.EOS;

		final char ch = (char)nextCh;
		switch (ch) {
		case ':': return AttribToken.COLON;
		case '^': return AttribToken.CARET;
		case '{': return AttribToken.OPEN_BRACE;
		case '}': return AttribToken.CLOSE_BRACE;
		case '(': return AttribToken.OPEN_PAREN;
		case ')': return AttribToken.CLOSE_PAREN;
		case '+': return AttribToken.PLUS;
		case '-': return AttribToken.MINUS;
		case '/': return AttribToken.DIV;
		case '*': return AttribToken.MUL;
		case '=': return AttribToken.EQUAL;
		case '$': return AttribToken.DOLLAR;
		case ',': return AttribToken.COMMA;
		case '&': return AttribToken.AMPERSAND;
		}

		if (ch == '"')
			return parseStringConstant();
		if (Character.isDigit(ch)) {
			ungetChar(nextCh);
			return parseNumericConstant();
		}

		if (ch == '<') {
			nextCh = getChar();
			if (nextCh == -1)
				return AttribToken.LESS_THAN;
			if ((char)nextCh == '>')
				return AttribToken.NOT_EQUAL;
			if ((char)nextCh == '=')
				return AttribToken.LESS_OR_EQUAL;
			ungetChar(nextCh);
			return AttribToken.LESS_THAN;
		}

		if (ch == '>') {
			nextCh = getChar();
			if (nextCh == -1)
				return AttribToken.GREATER_THAN;
			if ((char)nextCh == '=')
				return AttribToken.GREATER_OR_EQUAL;
			ungetChar(nextCh);
			return AttribToken.GREATER_THAN;
		}

		if (Character.isLetter(ch)) {
			ungetChar(nextCh);
			return parseIdentifier();
		}

		errorMsg = "unexpected input character '" + Character.toString(ch) + "'";

		return AttribToken.ERROR;
	}

	public void ungetToken(final AttribToken token) throws IllegalStateException {
		if (previousToken != null)
			throw new IllegalStateException("can't unget more than one token!");

		previousToken = token;
		previousIntConstant = currentIntConstant;
		previousFloatConstant = currentFloatConstant;
		previousBooleanConstant = currentBooleanConstant;
		previousStringConstant = currentStringConstant;
		previousIdent = currentIdent;
	}

	/**
	 *  Returns a representation of the next token as a string.  Used primarily for testing.
	 *  You should stop calling this after it returned "EOS"!
	 */
	public String getTokenAsString() {
		final AttribToken token = getToken();
		if (token == AttribToken.STRING_CONSTANT)
			return "STRING_CONSTANT: \"" + getStringConstant() + "\"";
		if (token == AttribToken.FLOAT_CONSTANT)
			return "FLOAT_CONSTANT: \"" + getFloatConstant() + "\"";
		if (token == AttribToken.BOOLEAN_CONSTANT)
			return "BOOLEAN_CONSTANT: \"" + getBooleanConstant() + "\"";
		if (token == AttribToken.IDENTIFIER)
			return "IDENTIFIER: \"" + getIdent() + "\"";
		if (token == AttribToken.ERROR)
			return "ERROR: \"" + getErrorMsg();
		return token.toString();
	}

	public String getStringConstant() {
		return currentStringConstant;
	}

	public double getFloatConstant() {
		return currentFloatConstant;
	}

	public boolean getBooleanConstant() {
		return currentBooleanConstant;
	}

	public long getIntConstant() {
		return currentIntConstant;
	}

	public String getIdent() {
		return currentIdent;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public static boolean isComparisonOperator(final AttribToken token) {
		switch (token) {
		case EQUAL:
		case NOT_EQUAL:
		case GREATER_THAN:
		case LESS_THAN:
		case GREATER_OR_EQUAL:
		case LESS_OR_EQUAL:
			return true;
		default:
			return false;
		}
	}

	/**
	 *  Converts tokens representing an operator to a string.  All other tokens return "?".
	 */
	public static String opTokenToString(final AttribToken token) {
		switch (token) {
		case CARET: return "^";
		case PLUS: return "+";
		case MINUS: return "-";
		case DIV: return "/";
		case MUL: return "*";
		case EQUAL: return "=";
		case NOT_EQUAL: return "<>";
		case GREATER_THAN: return ">";
		case LESS_THAN: return "<";
		case GREATER_OR_EQUAL: return ">=";
		case LESS_OR_EQUAL: return "<=";
		case AMPERSAND: return "&";
		default: return "?";
		}
	}

	private int getChar() {
		final int retval;

		if (putBackChar) {
			retval = previousChar;
			putBackChar = false;
			return retval;
		}

		try {
			retval = reader.read();
		} catch (final IOException e) {
			return -1;
		}

		if (retval != -1)
			++stringPos;

		return retval;
	}

	private void ungetChar(final int ch) {
		if (putBackChar)
			throw new IllegalStateException("can't unget two chars in a row!");
		previousChar = ch;
		putBackChar = true;
	}
	
	private AttribToken parseStringConstant() {
		final int startPos = stringPos;
		final int INITIAL_CAPACITY = 20;
		final StringBuilder builder = new StringBuilder(INITIAL_CAPACITY);

		boolean escaped = false;
		int nextCh;
		while ((nextCh = getChar()) != -1) {
			final char ch = (char)nextCh;
			if (ch == '\\')
				escaped = true;
			else {
				if (escaped) {
					switch (ch) {
					case '\\':
						builder.append('\\');
						break;
					case '"':
						builder.append('"');
						break;
					case 'n':
						builder.append('\n');
						break;
					default:
						errorMsg = "unknown escape character '" + Character.toString(ch) + "'!";
						return AttribToken.ERROR;
					}

					escaped = false;
				}
				else if (ch == '"') {
					currentStringConstant = builder.toString();
					return AttribToken.STRING_CONSTANT;
				}
				else
					builder.append(ch);
			}
		}

		errorMsg = "unterminated String constant!";
		return AttribToken.ERROR;
	}

	private AttribToken parseNumericConstant() {
		final int startPos = stringPos;
		final int INITIAL_CAPACITY = 20;
		final StringBuilder builder = new StringBuilder(INITIAL_CAPACITY);

		int ch;
		while ((ch = getChar()) != -1 && Character.isDigit((char)ch))
			builder.append((char)ch);

		if (ch == -1 || ((char)ch != 'e' && (char)ch != 'E' && (char)ch != '.')) {
			try {
				final double d = Double.parseDouble(builder.toString());
				currentFloatConstant = d;
				ungetChar(ch);
				return AttribToken.FLOAT_CONSTANT;
			} catch (final NumberFormatException e2) {
				errorMsg = "invalid numeric constant!";
				return AttribToken.ERROR;
			}
		}

		// Optional decimal point.
		if ((char)ch == '.') {
			builder.append((char)ch);
			while ((ch = getChar()) != -1 && Character.isDigit((char)ch))
				builder.append((char)ch);
		}

		// Optional exponent.
		if ((char)ch == 'e' || (char)ch == 'E') {
			builder.append((char)ch);

			ch = getChar();
			if (ch == -1) {
				errorMsg = "invalid numeric constant!";
				return AttribToken.ERROR;
			}

			// Optional sign.
			if ((char)ch == '+' || (char)ch == '-') {
				builder.append((char)ch);
				ch = getChar();
			}

			// Now we require at least a single digit.
			if (!Character.isDigit((char)ch)) {
				errorMsg = "missing digits in exponent!";
				return AttribToken.ERROR;
			}
			ungetChar(ch);

			while ((ch = getChar()) != -1 && Character.isDigit((char)ch))
				builder.append((char)ch);
		}

		ungetChar(ch);

		try {
			final double d = Double.parseDouble(builder.toString());
			currentFloatConstant = d;
			return AttribToken.FLOAT_CONSTANT;
		} catch (final NumberFormatException e3) {
			errorMsg = "invalid numeric constant!";
			return AttribToken.ERROR;
		}
	}

	private AttribToken parseIdentifier() {
		final int startPos = stringPos;
		final int INITIAL_CAPACITY = 20;
		final StringBuilder builder = new StringBuilder(INITIAL_CAPACITY);

		int ch;
		while ((ch = getChar()) != -1 && (Character.isLetter((char)ch) || Character.isDigit((char)ch) || (char)ch == '_'))
			builder.append((char)ch);
		ungetChar(ch);

		currentIdent = builder.toString();

		if (currentIdent.equalsIgnoreCase("TRUE")) {
			currentBooleanConstant = true;
			return AttribToken.BOOLEAN_CONSTANT;
		}
		if (currentIdent.equalsIgnoreCase("FALSE")) {
			currentBooleanConstant = false;
			return AttribToken.BOOLEAN_CONSTANT;
		}

		return AttribToken.IDENTIFIER;
	}
	
	static public void main(final String[] args) {
		for (final String arg : args) {
			final AttribTokeniser tokeniser = new AttribTokeniser(arg);
			String tokenAsString;
			do {
				tokenAsString = tokeniser.getTokenAsString();
				System.out.println(tokenAsString);
			}
			while (tokenAsString != "EOS");
		}
	}
}
