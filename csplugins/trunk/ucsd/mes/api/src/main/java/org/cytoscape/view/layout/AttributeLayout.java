package org.cytoscape.view.layout;

import java.util.List;

/**
 * A supplemental interface that indicates that the layout algorithm 
 * may be capable of using node and/or edge attributes to calculate
 * the layout.
 */
public interface AttributeLayout extends Layout {

	/**
	 * This list should be of the type of however we end up identifying
	 * CyAttribute types.
	 * Shouldn't we be able to figure out which attributes are available
	 * from the NetworkView?  Couldn't this just return a boolean?
	 */ 
	public List supportsNodeAttributes();

	/** 
	 *  This list should be of the type of however we end up identifying
	 *  CyAttribute types
	 */
	public List supportsEdgeAttributes();

	/**
	 * Can layout algorithms use more than one attribute to calculate a layout?
	 * Is there any reason to limit this number?  Should there be two methods,
	 * one for nodes and one for edges?
	 */
	public void setLayoutAttribute(String attributeName);
}

