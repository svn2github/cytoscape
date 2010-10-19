package org.cytoscape.view.presentation.property;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualLexiconNodeFactory;
import org.cytoscape.view.model.VisualProperty;

/**
 * Implementations for common features for all VisualLexicons.
 *
 */
public abstract class AbstractVisualLexicon implements VisualLexicon {
	
	//
	private final Map<VisualProperty<?>, VisualLexiconNode> visualPropertyMap;
	
	// Root of this tree.
	protected final VisualProperty<NullDataType> rootVisualProperty;
	
	protected final VisualLexiconNodeFactory nodeFactory;
	

	/**
	 * Constructor for VisualLexicon.  The parameters are required for all lexicons.
	 * 
	 * @param rootVisualProperty Root of the visual property tree.
	 * @param nodeFactory factory to create tree nodes for a lexicon.
	 */
	public AbstractVisualLexicon(final VisualProperty<NullDataType> rootVisualProperty, final VisualLexiconNodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
		
		this.visualPropertyMap = new HashMap<VisualProperty<?>, VisualLexiconNode>();
		this.rootVisualProperty = rootVisualProperty;
		final VisualLexiconNode rootNode = this.nodeFactory.createNode(rootVisualProperty, null);
		
		visualPropertyMap.put(rootVisualProperty, rootNode);
	}

	
	@Override public Set<VisualProperty<?>> getAllVisualProperties() {
		return new HashSet<VisualProperty<?>>(visualPropertyMap.keySet());
	}

	
	@Override public Collection<VisualProperty<?>> getAllDescendants(final VisualProperty<?> prop) {
		if(prop == null)
			throw new NullPointerException("Target visual property cannot be null.");
		
		if(!this.visualPropertyMap.containsKey(prop))
			throw new IllegalArgumentException("No such Visual Property in the Lexicon.");
		
		return getChildNodes(prop);
	}
	

	@Override public VisualProperty<NullDataType> getRootVisualProperty() {
		return this.rootVisualProperty;
	}
	
	
	private Set<VisualProperty<?>> getChildNodes(VisualProperty<?> prop) {
		final VisualLexiconNode node = visualPropertyMap.get(prop);
		final Set<VisualProperty<?>> children = new HashSet<VisualProperty<?>>();
		
		// if this is a leaf node, return empty set
		if(node.getChildren().size() == 0)
			return children;
		
		Collection<VisualLexiconNode> currentChildren = node.getChildren();
		for(VisualLexiconNode nd: currentChildren)
			children.add(nd.getVisualProperty());

		for(VisualLexiconNode nd: currentChildren)
			children.addAll(getChildNodes(nd.getVisualProperty()));
		
		return children;
	}
	
	/**
	 * Insert a Visual Property to the tree.
	 * 
	 * @param vp
	 * @param parent
	 */
	protected void addVisualProperty(final VisualProperty<?> vp, final VisualProperty<?> parent) {
		if(this.visualPropertyMap.containsKey(vp))
			throw new IllegalStateException("The key " + vp.getIdString() + " already exists in the lexicon.");
		
		if(parent == null)
			throw new NullPointerException("Parent cannot be null.");
		
		final VisualLexiconNode parentNode = this.visualPropertyMap.get(parent);
		
		if(parentNode == null)
			throw new IllegalArgumentException("Parent does not exist in the lexicon: " + parent.getDisplayName());
		
		final VisualLexiconNode newNode = this.nodeFactory.createNode(vp, parentNode);
		this.visualPropertyMap.put(vp, newNode);
	}
	
	
	@Override public VisualLexiconNode getVisualLexiconNode(final VisualProperty<?> vp) {
		return this.visualPropertyMap.get(vp);
	}
}
