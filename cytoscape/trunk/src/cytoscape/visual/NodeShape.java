package cytoscape.visual;

import giny.view.NodeView;

/**
 * This is a replacement for ShapeNodeRealizer.java
 * 
 * @author kono
 * 
 */
public enum NodeShape {
	RECT(NodeView.RECTANGLE, "Rectangle"), ROUND_RECT(
			NodeView.ROUNDED_RECTANGLE, "Round Rectangle"), RECT_3D(
			NodeView.RECTANGLE, "3D Rectabgle"), TRAPEZOID(NodeView.RECTANGLE,
			"Trapezoid"), TRAPEZOID_2(NodeView.RECTANGLE, "Trapezoid 2"), TRIANGLE(
			NodeView.TRIANGLE, "Traiangle"), PARALLELOGRAM(
			NodeView.PARALELLOGRAM, "Parallelogram"), DIAMOND(NodeView.DIAMOND,
			"Diamond"), ELLIPSE(NodeView.ELLIPSE, "Ellipse"), HEXAGON(
			NodeView.HEXAGON, "Hexagon"), OCTAGON(NodeView.OCTAGON, "Octagon");
	
	private int ginyShape;
	private String name;

	private NodeShape(int ginyShape, String name) {
		this.ginyShape = ginyShape;
		this.name = name;
	}

	/**
	 * Get name of the shape.
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getShapeName() {
		return name;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param type
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	@Deprecated
	public static boolean isValidShape(byte type) {
		return isValidShape(values()[type]);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param type
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static boolean isValidShape(NodeShape type) {
		for (NodeShape curType : values()) {
			if (type == curType)
				return true;
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param shape
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static String getNodeShapeText(NodeShape shape) {
		String nstext = shape.name();
		nstext = nstext.replaceAll("_", "");

		return nstext.toLowerCase();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param ginyType
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static NodeShape getNodeShape(byte ginyType) {
		for (NodeShape shape : values()) {
			if (shape.ginyShape == ginyType)
				return shape;
		}

		// if not found, just return rectangle.
		return RECT;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param shape
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getGinyShape(NodeShape shape) {
		return ginyShape;
	}
}
