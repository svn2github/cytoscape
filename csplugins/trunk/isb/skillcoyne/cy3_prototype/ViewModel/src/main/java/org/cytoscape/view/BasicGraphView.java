package org.cytoscape.view;

import org.cytoscape.GraphObject;

public interface BasicGraphView {

	public void setVisualProperty(GraphObject Obj, VisualProperty VP);
	
	public void setSelectionState(GraphObject Obj, boolean selected);
	
}
