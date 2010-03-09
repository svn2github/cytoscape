/*
  File: BinOpNode.java

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
package cytoscape.data.eqn_attribs.parse_tree;


import java.util.Stack;
import cytoscape.data.eqn_attribs.AttribToken;
import cytoscape.data.eqn_attribs.AttribTokeniser;
import cytoscape.data.eqn_attribs.interpreter.Instruction;


/**
 *  A node in the parse tree representing a binary operator.
 */
public class BinOpNode implements Node {
	private final AttribToken operator;
	private final Node lhs, rhs;

	public BinOpNode(final AttribToken operator, final Node lhs, final Node rhs) {
		if (lhs == null)
			throw new IllegalArgumentException("left operand must nor be null!");
		if (rhs == null)
			throw new IllegalArgumentException("right operand must nor be null!");

		this.operator = operator;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public String toString() { return "BinOpNode: " + operator; }

	public Class getType() { return AttribTokeniser.isComparisonOperator(operator) ? Boolean.class : lhs.getType(); }

	/**
	 *  @returns the left operand
	 */
	public Node getLeftChild() { return lhs; }

	/**
	 *  @returns the right operand
	 */
	public Node getRightChild() { return rhs; }

	public AttribToken getOperator() { return operator; }

	public void genCode(final Stack<Instruction> opCodes, final Stack<Object> arguments) {
		switch (operator) {
		case CARET:
			opCodes.push(Instruction.FPOW);
			break;
		case PLUS:
			opCodes.push(Instruction.FADD);
			break;
		case MINUS:
			opCodes.push(Instruction.FSUB);
			break;
		case DIV:
			opCodes.push(Instruction.FDIV);
			break;
		case MUL:
			opCodes.push(Instruction.FMUL);
			break;
		case EQUAL:
			opCodes.push(determineOpCode(Instruction.BEQLF, Instruction.BEQLS, Instruction.BEQLB));
			break;
		case NOT_EQUAL:
			opCodes.push(determineOpCode(Instruction.BNEQLF, Instruction.BNEQLS, Instruction.BNEQLB));
			break;
		case GREATER_THAN:
			opCodes.push(determineOpCode(Instruction.BGTF, Instruction.BGTS, null));
			break;
		case LESS_THAN:
			opCodes.push(determineOpCode(Instruction.BLTF, Instruction.BLTS, null));
			break;
		case GREATER_OR_EQUAL:
			opCodes.push(determineOpCode(Instruction.BGTEF, Instruction.BGTES, null));
			break;
		case LESS_OR_EQUAL:
			opCodes.push(determineOpCode(Instruction.BLTEF, Instruction.BLTES, null));
			break;
		case AMPERSAND:
			opCodes.push(Instruction.SCONCAT);
			break;
		default:
			throw new IllegalStateException("unknown operator: " + operator + "!");
		}

		lhs.genCode(opCodes, arguments);
		rhs.genCode(opCodes, arguments);
	}

	/**
	 *  Picks one of three opcodes based on operand types.
	 *  (N.B.: We assume that the LHS and RHS operands are of the same type!)
	 */
	private Instruction determineOpCode(final Instruction floatOpCode, final Instruction stringOpCode, final Instruction booleanOpCode) {
		final Class operandType = lhs.getType();
		if (operandType == Double.class)
			return floatOpCode;
		else if (operandType == String.class)
			return stringOpCode;
		else if (booleanOpCode != null && operandType == Boolean.class)
			return booleanOpCode;

		throw new IllegalStateException("invalid LHS operand type for comparison: " + operandType + "!");
	}
}
