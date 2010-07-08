package cytoscape.visual.customgraphic;

import cytoscape.visual.customgraphic.impl.AbstractDCustomGraphics;

/**
 * Null object for Custom Graphics. This is used to reset custom graphics on
 * node views.
 * 
 * @author kono
 * 
 */
public class NullCustomGraphics extends AbstractDCustomGraphics {

	static final CyCustomGraphics NULL = new NullCustomGraphics();

	public static CyCustomGraphics getNullObject() {
		return NULL;
	}

	private static final String NAME = "[ Remove Graphics ]";

	public NullCustomGraphics() {
		super(NAME);
	}

	public String toString() {
		return this.getClass().getCanonicalName();
	}
}
