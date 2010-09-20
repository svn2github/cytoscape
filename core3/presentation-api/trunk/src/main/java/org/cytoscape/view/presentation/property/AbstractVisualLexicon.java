package org.cytoscape.view.presentation.property;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;


/**
 * Basic tree implementation of a visual lexicon.  All rendering engine should use this class
 * to implement their own version of lexicon.
 * 
 * This tree has minimum set of method required to implement VizMapper.
 * 
 * This is an immutable tree, except the rendering engine's root.
 * 
 * @author kono
 *
 */
public abstract class AbstractVisualLexicon implements VisualLexicon {

	//
	private final Map<String, VisualProperty<?>> visualPropertyMap;
	
	// Root of this tree.
	protected final VisualProperty<NullDataType> rootVisualProperty;
	

	/**
	 * Insert a root node to the tree and build it.
	 * 
	 * @param rootVisualProperty
	 */
	public AbstractVisualLexicon(final VisualProperty<NullDataType> rootVisualProperty) {
		this.visualPropertyMap = new HashMap<String, VisualProperty<?>>();
		this.rootVisualProperty = rootVisualProperty;
		
		visualPropertyMap.put(rootVisualProperty.getIdString(), rootVisualProperty);
	}

	
	// Returns all visual properties as a set.
	public Set<VisualProperty<?>> getAllVisualProperties() {
		return new HashSet<VisualProperty<?>>(visualPropertyMap.values());
	}

	
	@Override
	public Collection<VisualProperty<?>> getAllDescendants(final VisualProperty<Visualizable> prop) {
		if(prop == null)
			throw new NullPointerException("Target visual property cannot be null.");
		
		if(!this.visualPropertyMap.containsValue(prop))
			throw new IllegalArgumentException("No such Visual Property in the Lexicon.");
		
		return getChildNodes(prop);
	}
	

	@Override
	public VisualProperty<NullDataType> getRootVisualProperty() {
		return this.rootVisualProperty;
	}
	
	
	private Set<VisualProperty<?>> getChildNodes(VisualProperty<?> prop) {
		final Set<VisualProperty<?>> children = new HashSet<VisualProperty<?>>();
		
		// if this is a leaf node, return empty set
		if(prop.getChildren().size() == 0)
			return children;
		
		Collection<VisualProperty<?>> currentChildren = prop.getChildren();
		children.addAll(currentChildren);
		for(VisualProperty<?> vp: currentChildren)
			children.addAll(getChildNodes(vp));
		
		return children;
	}
	
	protected void addVisualProperty(final VisualProperty<?> vp) {
		this.visualPropertyMap.put(vp.getIdString(), vp);
	}
}
