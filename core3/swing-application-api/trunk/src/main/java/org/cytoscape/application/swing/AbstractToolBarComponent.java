package org.cytoscape.application.swing;


public abstract class AbstractToolBarComponent implements ToolBarComponent{

	protected float toolbarGravity = 100.0f; // end of toolbar
	

	//public AbstractToolBarComponent(){
		
	//}
	
	public void setToolBarGravity(float gravity) {
		toolbarGravity = gravity;
	}

	public float getToolBarGravity() {
		return toolbarGravity;
	}
}
