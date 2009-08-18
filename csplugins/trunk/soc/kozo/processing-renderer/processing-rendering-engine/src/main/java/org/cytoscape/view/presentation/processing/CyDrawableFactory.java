package org.cytoscape.view.presentation.processing;

import processing.core.PApplet;


public interface CyDrawableFactory<T extends CyDrawable> {
	
	public CyDrawable getInstance();
	
	public Class<T> getDrawableClass();
	
	public void setPaernt(final PApplet parent);

}
