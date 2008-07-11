package org.cytoscape.view.model; 

/**
 * The available node shapes in Cytoscape.
 */
public enum NodeShape {
	RECT("Rectangle"),
	ROUND_RECT("Round Rectangle"),
	TRAPEZOID("Trapezoid"),
	TRIANGLE("Triangle"),
	PARALLELOGRAM("Parallelogram"),
	DIAMOND("Diamond"),
	ELLIPSE("Ellipse"),
	HEXAGON("Hexagon"),
	OCTAGON("Octagon"),
	;


	private String name;
	private NodeShape(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}
}
