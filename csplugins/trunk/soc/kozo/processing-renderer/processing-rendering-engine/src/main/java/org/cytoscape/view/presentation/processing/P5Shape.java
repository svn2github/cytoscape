package org.cytoscape.view.presentation.processing;

public class P5Shape {
	private final Class<? extends CyDrawable> drawableType;
	private final String name;
	
	public P5Shape(String name, Class<? extends CyDrawable> drawableType) {
		this.name = name;
		
		System.out.println("============== Setting Class Type: " + drawableType);
		this.drawableType = drawableType;
		System.out.println("============== Setting Class Type Finished!!!!!!!!!!!!!: " + drawableType);
	}
	
	public Class<? extends CyDrawable> getDrawableType() {
		return drawableType;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
