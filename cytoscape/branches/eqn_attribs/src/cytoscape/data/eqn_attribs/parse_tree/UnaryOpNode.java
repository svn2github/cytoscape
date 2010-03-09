/*
  File: UnaryOpNode.java

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
import cytoscape.data.eqn_attribs.interpreter.Instruction;


/**
 *  A node in the parse tree representing a unary operator application.
 */
public class UnaryOpNode implements Node {
	private final AttribToken operator;
	private final Node operand;

	public UnaryOpNode(final AttribToken operator, final Node operand) {
		if (operand == null)
			throw new IllegalArgumentException("operand must not be null!");

		this.operator = operator;
		this.operand = operand;
	}

	public String toString() { return "UnaryOpNode: " + operator; }

	public Class getType() { return operand.getType(); }

	/**
	 *  @returns the operand
	 */
	public Node getLeftChild() { return operand; }

	/**
	 *  @returns null, This type of node never has any left children!
	 */
	public Node getRightChild() { return null; }

	public AttribToken getOperator() { return operator; }

	public void genCode(final Stack<Instruction> opCodes, final Stack<Object> arguments) {
		switch (operator) {
		case PLUS:
			opCodes.push(Instruction.FUPLUS);
			break;
		case MINUS:
			opCodes.push(Instruction.FUMINUS);
			break;
		default:
			throw new IllegalStateException("invalid unary operation: " + operator + "!");
		}

		operand.genCode(opCodes, arguments);
	}
}
