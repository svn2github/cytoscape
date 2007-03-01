package cytoscape.visual;

import giny.view.EdgeView;

/**
 * Defines arrow shapes.<br>
 * This replaces constants defined in Arrow.java.
 * 
 * @since Cytoscape 2.5
 * @author kono
 * 
 */
public enum ArrowShape {
	NONE("No Arrow", "NONE", EdgeView.NO_END), DIAMOND("Diamond",
			"COLOR_DIAMOND", EdgeView.EDGE_COLOR_DIAMOND), DELTA("Delta",
			"COLOR_DELTA", EdgeView.EDGE_COLOR_DELTA), ARROW("Arrow",
			"COLOR_ARROW", EdgeView.EDGE_COLOR_ARROW), T("T", "COLOR_T",
			EdgeView.EDGE_COLOR_T), CIRCLE("Circle", "COLOR_CIRCLE",
			EdgeView.EDGE_COLOR_CIRCLE),

	// Not yet implemented
	REVERSE_ARROW("Reverse Arrow", "REVERSE_ARROW", -1);
	private String shapeName;
	private String ginyShapeName;
	private int ginyType;

	private ArrowShape(String shapeName, String ginyShapeName, int ginyType) {
		this.shapeName = shapeName;
		this.ginyShapeName = ginyShapeName;
		this.ginyType = ginyType;
	}

	/**
	 * Returns arrow type in GINY.
	 * 
	 * @return
	 */
	public int getGinyArrow() {
		return ginyType;
	}

	/**
	 * Returns name of arrow shape.
	 * 
	 * @return
	 */
	public String getGinyName() {
		return ginyShapeName;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getName() {
		return shapeName;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static ArrowShape parseArrowText(String text) {
		return valueOf(text);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param ginyType
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static ArrowShape getArrowShape(byte ginyType) {
		for (ArrowShape shape : values()) {
			if (shape.getGinyArrow() == ginyType)
				return shape;
		}

		// if not found, just return rectangle.
		return NONE;
	}
}
