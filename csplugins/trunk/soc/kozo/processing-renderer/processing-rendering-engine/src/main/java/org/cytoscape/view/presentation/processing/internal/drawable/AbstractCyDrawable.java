package org.cytoscape.view.presentation.processing.internal.drawable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public abstract class AbstractCyDrawable implements CyDrawable {
	
	protected final PApplet p;
	protected final List<CyDrawable> children;
	protected Set<Class<?>> compatibleDataType;
	
	protected VisualLexicon lexicon;
	
	protected boolean selected = false;
	protected boolean fastRendering = false;
	
	protected float size;
	protected float r, g, b, alpha;
	protected Color selectedColor;

	public AbstractCyDrawable(final PApplet p) {
		this.p  = p;
		this.children = new ArrayList<CyDrawable>();
		this.compatibleDataType = new HashSet<Class<?>>();
	}
	
	abstract public void draw();
	abstract public void setContext(View<?> viewModel);
	
	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		//TODO how can we implement this?
	}

	public void addChild(CyDrawable child) {
		this.children.add(child);
	}

	public void setDetailFlag(boolean flag) {
		this.fastRendering = flag;
	}
	
	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return children;
	}
	
	public Set<Class<?>> getCompatibleModels() {
		return compatibleDataType;
	}
}
