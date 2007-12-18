/**
 * 
 */
package org.cytoscape.model;

import java.util.*;

/**
 * @author skillcoy
 *
 */
public interface Edge<Src, Tgt> {
	/* If we move to a hyperedge idea then does source and target actually mean anything or
	 * would that be a separate edge interface?
	 */
	public Src getSource();
	public Tgt getTarget();
	
	// these could very easily be an attribute, and probably should be 
	public boolean isDirected();
	public double getEdgeWeight();
	
	// attributes
	public AttributeHolder getAttributeHolder();
}
