package org.cytoscape.view.presentation.processing;

public class P5Shape {
	private final Class<? extends CyDrawable> drawableType;
	private final String name;
	
	public P5Shape(String name, Class<? extends CyDrawable> drawableType) {
		this.name = name;
		this.drawableType = drawableType;
	}
	
	public Class<? extends CyDrawable> getDrawableType() {
		return drawableType;
	}
	
	public String getName() {
		return name;
	}

}
