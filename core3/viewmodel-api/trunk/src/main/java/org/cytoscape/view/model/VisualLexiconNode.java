package org.cytoscape.view.model;

import java.util.Collection;

public interface VisualLexiconNode {
	
	
	/**
	 * Wrapped object.
	 * 
	 * Since VisualProperty itself does not have any hierarchical structure, 
	 * such relationship is implemented by this wrapper.
	 * 
	 * @return
	 */
	VisualProperty<?> getVisualProperty();

	
	/**
	 * Get the parent of this VP node. The relationship is immutable, i.e.,
	 * cannot change parent/child relationship.
	 */
	VisualLexiconNode getParent();

	/**
	 * Returns all children of this node.
	 * 
	 * @return all child nodes.
	 */
	Collection<VisualLexiconNode> getChildren();

}
