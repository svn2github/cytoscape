package org.cytoscape.view.presentation.processing.internal;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.VisualItemRenderer;
import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public abstract class AbstractRenderer<T extends View<?>> implements VisualItemRenderer<T> {

	protected final PApplet p;
	protected final VisualLexicon lexicon;
	
	public AbstractRenderer(PApplet p) {
		this.p = p;
		this.lexicon = buildLexicon();
	}

	public VisualLexicon getVisualLexicon() {
		return lexicon;
	}
	
	protected abstract VisualLexicon buildLexicon();
	
	public abstract CyDrawable render(T view);
}
