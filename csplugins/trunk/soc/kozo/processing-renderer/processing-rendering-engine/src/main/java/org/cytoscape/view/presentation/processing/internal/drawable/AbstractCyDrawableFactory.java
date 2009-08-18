package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.CyDrawableFactory;

import processing.core.PApplet;

public abstract class AbstractCyDrawableFactory<T extends CyDrawable> implements CyDrawableFactory<T> {

	protected PApplet p;
	protected Class<T> type;

	public AbstractCyDrawableFactory() {
		this(null);
	}
	
	public AbstractCyDrawableFactory(PApplet p) {
		this.p = p;
	}

	// Should be implemented by concrete classes.
	public abstract CyDrawable getInstance();

	public Class<T> getDrawableClass() {
		return this.type;
	}
	
	public void setPaernt(PApplet parent) {
		this.p = parent;
	}

}
