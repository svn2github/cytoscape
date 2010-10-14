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
import org.cytoscape.view.model.Visualizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractVisualLexicon implements VisualLexicon {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractVisualLexicon.class);

	//
	private final Map<VisualProperty<?>, VisualLexiconNode> visualPropertyMap;
	
	// Root of this tree.
	protected final VisualProperty<NullDataType> rootVisualProperty;
	
	protected final VisualLexiconNodeFactory nodeFactory;
	

	/**
	 * Insert a root node to the tree and build it.
	 * 
	 * @param rootVisualProperty
	 */
	public AbstractVisualLexicon(final VisualProperty<NullDataType> rootVisualProperty, final VisualLexiconNodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
		
		this.visualPropertyMap = new HashMap<VisualProperty<?>, VisualLexiconNode>();
		this.rootVisualProperty = rootVisualProperty;
		final VisualLexiconNode rootNode = this.nodeFactory.createNode(rootVisualProperty, null);
		
		visualPropertyMap.put(rootVisualProperty, rootNode);
	}

	
	// Returns all visual properties as a set.
	public Set<VisualProperty<?>> getAllVisualProperties() {
		return new HashSet<VisualProperty<?>>(visualPropertyMap.keySet());
	}
	
	
//	/**
//	 * Add a new VP as a leaf.
//	 * 
//	 * @param prop
//	 * @param parent
//	 */
//	void insertVisualProperty(final VisualProperty<?> prop, final VisualProperty<?> parent) {
//		//Sanity check
//		if(prop == null)
//			throw new NullPointerException("Cannot add null to the lexicon tree.");
//		if(parent == null)
//			throw new NullPointerException("Parent Visual Property should not be null.");
//		
//		if(this.visualPropertyMap.containsValue(prop))
//			throw new IllegalArgumentException("The Visual Property already exists: " + prop.getDisplayName());
//		
//		if(!this.visualPropertyMap.containsValue(parent))
//			throw new IllegalArgumentException("Parent Visual Property does not exist in the tree.");
//		
//		this.visualPropertyMap.put(prop.getIdString(), prop);
//		parent.getChildren().add(prop);
//	}

	
	@Override
	public Collection<VisualProperty<?>> getAllDescendants(final VisualProperty<?> prop) {
		if(prop == null)
			throw new NullPointerException("Target visual property cannot be null.");
		
		if(!this.visualPropertyMap.containsKey(prop))
			throw new IllegalArgumentException("No such Visual Property in the Lexicon.");
		
		return getChildNodes(prop);
	}
	

	@Override
	public VisualProperty<NullDataType> getRootVisualProperty() {
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
