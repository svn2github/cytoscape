package org.cytoscape.view.model;

/**
 * Factory for nodes in the visual lexicon.
 *
 */
public interface VisualLexiconNodeFactory {
	
	/**
	 * Simple factory to create tree node for VisualLexicon.
	 * 
	 * @param vp visual property to be wrapped by the tree node.
	 * @param parent parent of new tree node.
	 * 
	 * @return new tree node in the lexicon.
	 */
	VisualLexiconNode createNode(final VisualProperty<?> vp, final VisualLexiconNode parent);

}
