/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.AttributeHolder;

/**
 * @author skillcoy
 *
 */
public class EdgeAttributeHolderImpl implements AttributeHolder {

	public enum EdgeAttributes { // yes, not complete but there ya go
		WEIGHT(), DIRECTION();
	}
	
	public boolean isEdgeDirected() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.AttributeHolder#addAttribute(java.lang.Object)
	 */
	public void addAttribute(Object obj) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.AttributeHolder#findAttribute(java.lang.Object)
	 */
	public Object findAttribute(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.AttributeHolder#getAttributes()
	 */
	public Object[] getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
