package org.cytoscape.view.model.internal;

import java.util.Collection;
import java.util.HashSet;

import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualProperty;

public class VisualLexiconNodeImpl implements VisualLexiconNode {
	
	protected final VisualProperty<?> vp;
	
	protected final VisualLexiconNode parent;
	protected final Collection<VisualLexiconNode> children;
	
	
	public VisualLexiconNodeImpl(final VisualProperty<?> vp, final VisualLexiconNode parent) {
		this.vp = vp;
		this.parent = parent;
		this.children = new HashSet<VisualLexiconNode>();
		
		if(parent != null)
			parent.getChildren().add(this);
		
	}

	@Override
	public VisualProperty<?> getVisualProperty() {
		return vp;
	}

	@Override
	public VisualLexiconNode getParent() {
		return parent;
	}

	@Override
	public Collection<VisualLexiconNode> getChildren() {
		return children;
	}

}
