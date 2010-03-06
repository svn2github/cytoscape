/*
  File: BinIdentOpNode.java

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

import cytoscape.data.eqn_attribs.AttribToken;
import cytoscape.data.eqn_attribs.AttribTokeniser;

/**
 *  A node in the parse tree representing a binary operator w/ both operands being identifiers.
 */
public class BinIdentOpNode implements Node {
	private final AttribToken operator;
	private final IdentNode lhs, rhs;

	public BinIdentOpNode(final AttribToken operator, final IdentNode lhs, final IdentNode rhs) {
		if (lhs == null)
			throw new IllegalArgumentException("left operand must nor be null!");
		if (rhs == null)
			throw new IllegalArgumentException("right operand must nor be null!");

		this.operator = operator;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public String toString() { return "BinIdentOpNode: " + operator + " (" + lhs + ", " + rhs + ")"; }

	public Class getType() { return AttribTokeniser.isComparisonOperator(operator) ? Boolean.class : Object.class; }

	/**
	 *  @returns the left operand
	 */
	public Node getLeftChild() { return lhs; }

	/**
	 *  @returns the right operand
	 */
	public Node getRightChild() { return rhs; }

	public AttribToken getOperator() { return operator; }
}
