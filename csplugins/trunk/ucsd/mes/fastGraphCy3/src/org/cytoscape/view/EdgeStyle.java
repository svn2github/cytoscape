
package org.cytoscape.view;

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
