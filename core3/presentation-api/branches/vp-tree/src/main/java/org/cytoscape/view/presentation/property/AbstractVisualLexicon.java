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
		if(this.visualPropertyMap.containsKey(vp.getIdString()))
			throw new IllegalStateException("The key " + vp.getIdString() + " already exists in the lexicon.");
		
		this.visualPropertyMap.put(vp.getIdString(), vp);
	}
}
