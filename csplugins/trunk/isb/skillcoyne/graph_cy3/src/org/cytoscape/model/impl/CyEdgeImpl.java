/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.AttributeHolder;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * @author skillcoy
 *
 */
public class CyEdgeImpl implements CyEdge {
	
	private int index;
	private CyNode source;
	private CyNode target;
	private boolean directed;
	
	
	CyEdgeImpl(CyNode sourceNode, CyNode targetNode, boolean dir, int edgeIndex) {
		source = sourceNode;
		target = targetNode;
		directed = dir;
		index = edgeIndex;
	}

	CyEdgeImpl(CyNode sourceNode, CyNode targetNode, boolean dir) {
		source = sourceNode;
		target = targetNode;
		directed = dir;
		index = -1;
	}

	
	public void setIndex(int edgeIndex) {
		index = edgeIndex;
	}
	
	public int getIndex() {
		return index;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyEdge#getAttributes()
	 */
	public AttributeHolder getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyEdge#getSource()
	 */
	public CyNode getSource() {
		return source;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyEdge#getTarget()
	 */
	public CyNode getTarget() {
		return target;
	}
	
	// ???
	public Object getEdgeType() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyEdge#isDirected()
	 */
	public boolean isDirected() {
		return directed;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyEdge#setAttributes(org.cytoscape.model.Attribute)
	 */
	public void setAttributes(AttributeHolder edgeAtt) {
		// TODO Auto-generated method stub
	}

}
