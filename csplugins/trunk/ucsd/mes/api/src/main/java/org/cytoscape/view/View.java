package org.cytoscape.view;

import java.awt.Shape;
import java.awt.Paint;

public interface View  {
	public void setVisualProperty(VisualProperty vp, Object o);
	public Object getVisualProperty(VisualProperty vp, Object o);

	// maybe we can figure out how to have a custom graphic visual property?
	public int addCustomGraphic(Shape s, Paint p);
	public void removeCustomGraphic(int i);
}
