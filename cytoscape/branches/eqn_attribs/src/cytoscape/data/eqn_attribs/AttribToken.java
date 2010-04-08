/*
  File: AttribToken.java

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


public enum AttribToken {
	STRING_CONSTANT("?", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	FLOAT_CONSTANT("?", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	BOOLEAN_CONSTANT("?", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	IDENTIFIER("?", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	OPEN_BRACE("{", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	CLOSE_BRACE("}", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	OPEN_PAREN("(", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	CLOSE_PAREN(")", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	COLON(":", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	CARET("^", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ true, /* isStringOperator = */ false),
	PLUS("+", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ true, /* isStringOperator = */ false),
	MINUS("-", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ true, /* isStringOperator = */ false),
	DIV("/", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ true, /* isStringOperator = */ false),
	MUL("*", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ true, /* isStringOperator = */ false),
	EQUAL("=", /* isComparisonOperator = */ true, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	NOT_EQUAL("<>", /* isComparisonOperator = */ true, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	GREATER_THAN(">", /* isComparisonOperator = */ true, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	LESS_THAN("<", /* isComparisonOperator = */ true, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	GREATER_OR_EQUAL(">=", /* isComparisonOperator = */ true, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	LESS_OR_EQUAL("<=", /* isComparisonOperator = */ true, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	DOLLAR("$", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	COMMA(",", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	AMPERSAND("&", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ true),
	EOS("?", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false),
	ERROR("?", /* isComparisonOperator = */ false, /* isArithmeticOperator = */ false, /* isStringOperator = */ false);
	

	private final String asString;
	private final boolean isComparisonOperator;
	private final boolean isArithmeticOperator;
	private final boolean isStringOperator;

	AttribToken(final String asString, final boolean isComparisonOperator,
		    final boolean isArithmeticOperator, final boolean isStringOperator)
	{
		this.asString = asString;
		this.isComparisonOperator = isComparisonOperator;
		this.isArithmeticOperator = isArithmeticOperator;
		this.isStringOperator     = isStringOperator;
	}

	public String asString() { return asString; }
	public boolean isComparisonOperator() { return isComparisonOperator; }
	public boolean isArithmeticOperator() { return isArithmeticOperator; }
	public boolean isStringOperator() { return isStringOperator; }
}
