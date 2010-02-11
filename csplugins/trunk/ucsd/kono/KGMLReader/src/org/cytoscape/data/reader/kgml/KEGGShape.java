package org.cytoscape.data.reader.kgml;

import cytoscape.visual.NodeShape;

public enum KEGGShape {
	CIRCLE("circle", NodeShape.ELLIPSE), RECTANGLE("rectangle",
			NodeShape.RECT), ROUND_RECTANGLE("roundrectangle",
			NodeShape.ROUND_RECT), LINE("line", null);

	private String tag;
	private NodeShape shape;

	private KEGGShape(final String tag, final NodeShape shape) {
		this.shape = shape;
		this.tag = tag;
	}

	public static int getShape(final String shape) {
		for (KEGGShape keggShape : KEGGShape.values()) {
			if (keggShape.tag.equals(shape)) {
				if (keggShape.shape == null)
					return -1;
				else
					return keggShape.shape.getGinyShape();
			}
		}

		return NodeShape.RECT.getGinyShape();
	}
	
	public String getTag() {
		return this.tag;
	}
}