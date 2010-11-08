package org.cytoscape.equations.parse_tree;


import java.util.Stack;

import org.cytoscape.equations.CodeAndSourceLocation;


class SimpleNode extends Node {
	SimpleNode(final int sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public String toString() { return null; }

	@Override
	public Class getType() { return null; }

	@Override
	public Node getLeftChild() { return null; }

	@Override
	public Node getRightChild() { return null; }

	@Override
	public void genCode(final Stack<CodeAndSourceLocation> codeStack) { }
}