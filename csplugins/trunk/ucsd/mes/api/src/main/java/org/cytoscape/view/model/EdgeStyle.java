
package org.cytoscape.view.model;

/**
 * Defines the different types of edges we can have.
 */
public enum EdgeStyle {
	CURVED("Curved"),
	STRAIGHT("Straight"),
	;

	private String name;
	private EdgeStyle(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}
}
