/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.*;

/**
 * This is just one example of an implementation of edge
 *
 */
public class EdgeImpl<NodeSrc, NodeTgt> implements Edge {

	private Node source;
	private Node target;
	private AttributeHolder attHold;
	private int graphIndex;
	
	protected EdgeImpl(Node nSource, Node nTarget, int index) {
		source = nSource;
		target = nTarget;
		index = graphIndex;
		attHold = new EdgeAttributeHolderImpl();
	}
	
	protected int getIndex() {
		return graphIndex;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.Edge#getAttributeHolder()
	 */
	public AttributeHolder getAttributeHolder() {
		return attHold;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Edge#getSource()
	 */
	public Object getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Edge#getTarget()
	 */
	public Object getTarget() {
		return target;
	}

	// these don't really need to be on the edge itself 
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.Edge#getEdgeWeight()
	 */
	public double getEdgeWeight() {
		// obviously this isn't the way it should really be done...just messing with it
		return (Double) attHold.findAttribute("weight");
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Edge#isDirected()
	 */
	public boolean isDirected() {
		// again, not really the way it should be done
		return (Boolean) attHold.findAttribute("directed");
	}

}
