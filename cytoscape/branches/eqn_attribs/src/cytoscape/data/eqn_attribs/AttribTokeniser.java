package cytoscape.data.eqn_attribs;

import java.io.IOException;
import java.io.StringReader;


enum AttribToken {
	STRING_CONSTANT,
	FLOAT_CONSTANT,
	INTEGER_CONSTANT,
	IDENTIFIER,
	OPEN_PAREN,
	CLOSE_PAREN,
	COLON,
	CARET,
	PLUS,
	MINUS,
	DIV,
	MUL,
	EQUAL,
	NOT_EQUAL,
	GREATER_THAN,
	LESS_THAN,
	GREATER_OR_EQUAL,
	LESS_OR_EQUAL,
	DOLLAR,
	COMMA,
	EOS,
	ERROR
}


public class AttribTokeniser {
	private AttribToken previousToken;
	private StringReader reader;
	private int stringPos, errorPos;
	private long previousIntConstant, currentIntConstant;
	private String previousIdent, currentIdent;
	private double previousFloatConstant, currentFloatConstant;
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
		case '(': return AttribToken.OPEN_PAREN;
		case ')': return AttribToken.CLOSE_PAREN;
		case '+': return AttribToken.PLUS;
		case '-': return AttribToken.MINUS;
		case '/': return AttribToken.DIV;
		case '*': return AttribToken.MUL;
		case '=': return AttribToken.EQUAL;
		case '$': return AttribToken.DOLLAR;
		case ',': return AttribToken.COMMA;
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
				return AttribToken.LESS_THAN;
			if ((char)nextCh == '=')
				return AttribToken.LESS_OR_EQUAL;
			ungetChar(nextCh);
			return AttribToken.LESS_THAN;
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
		previousStringConstant = currentStringConstant;
		previousIdent = currentIdent;
	}

	public String getStringConstant() {
		return currentStringConstant;
	}

	public double getFloatConstant() {
		return currentFloatConstant;
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
				final long l = Long.parseLong(builder.toString());
				currentIntConstant = l;
				ungetChar(ch);
				return AttribToken.INTEGER_CONSTANT;
			} catch (final NumberFormatException e1) {
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
			ungetChar(ch);
		}

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
		return AttribToken.IDENTIFIER;
	}
	
	static public void main(final String[] args) {
		for (final String arg : args) {
			final AttribTokeniser tokeniser = new AttribTokeniser(arg);
			AttribToken token;
			while ((token = tokeniser.getToken()) != AttribToken.EOS) {
				if (token == AttribToken.STRING_CONSTANT)
					System.out.println("STRING_CONSTANT: \"" + tokeniser.getStringConstant() + "\"");
				else if (token == AttribToken.INTEGER_CONSTANT)
					System.out.println("INTEGER_CONSTANT: \"" + tokeniser.getIntConstant() + "\"");
				else if (token == AttribToken.FLOAT_CONSTANT)
					System.out.println("FLOAT_CONSTANT: \"" + tokeniser.getFloatConstant() + "\"");
				else if (token == AttribToken.IDENTIFIER)
					System.out.println("IDENTIFIER: \"" + tokeniser.getIdent() + "\"");
				else if (token == AttribToken.ERROR)
					System.out.println("ERROR: \"" + tokeniser.getErrorMsg());
				else
					System.out.println(token.toString());
			}
		}
	}
}
