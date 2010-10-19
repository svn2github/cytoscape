package org.cytoscape.view.model;

import java.util.Collection;

/**
 * A node in the visual property tree (lexicon).
 * <p>
 * Wrapping a {@linkplain VisualProperty} and holding parent-child relationships.
 * <p>
 * All data fields are immutable. 
 * 
 * @author kono
 *
 */
public interface VisualLexiconNode {
	
	/**
	 * Returns wrapped {@linkplain VisualProerty} object.
	 * 
	 * Since VisualProperty itself does not have any hierarchical structure, 
	 * such relationship is implemented by this wrapper.
	 * 
	 * @return wrapped {@linkplain VisualProeprty} object.
	 */
	VisualProperty<?> getVisualProperty();

	
	/**
	 * Get the parent of this VP node. The relationship is immutable, i.e.,
	 * cannot change parent/child relationship.
	 * 
	 * @return parent VisualProperty object.
	 */
	VisualLexiconNode getParent();

	/**
	 * Returns collection of all children of this node.
	 * 
	 * @return collection of all children
	 */
	Collection<VisualLexiconNode> getChildren();

}
