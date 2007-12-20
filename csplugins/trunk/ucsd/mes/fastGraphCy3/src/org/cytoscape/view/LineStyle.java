
package org.cytoscape.view;

public enum LineStyle {
	SOLID("line",null),
	DASH("dash",new float[] {4.0f,4.0f}),
	LONG_DASH("long dash", new float[] {10.0f,4.0f}),
	DASH_DOT("dash dot", new float[] {12.0f,3.0f,3.0f,3.0f}),
	;

	private String name;
	private float[]  dashDescription;

	private LineStyle(String n, float[] dd) {
		name = n;
		dashDescription = dd;
	}

	public String getName() {
		return name;
	}

	public float[] getDashDescription() {
		return dashDescription;
	}
}
