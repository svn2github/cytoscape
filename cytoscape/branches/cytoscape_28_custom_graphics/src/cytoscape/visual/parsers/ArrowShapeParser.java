package cytoscape.visual.parsers;

import cytoscape.visual.ArrowShape;

public class ArrowShapeParser implements ValueParser<ArrowShape> {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public ArrowShape parseStringValue(String value) {
		return parseArrowShape(value);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public ArrowShape parseArrowShape(String value) {
		return ArrowShape.parseArrowText(value);
	}
}
