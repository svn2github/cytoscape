package org.cytoscape.view.presentation.processing;

import java.awt.Paint;

import org.cytoscape.view.presentation.processing.visualproperty.LineStyle;

public interface PEdge {
	
	public void setLineStyle();
	public LineStyle getLineStyle();
	
	public void setPaint();
	public Paint getPaint();
	
	public void setWidth(float width);
	public float getWidth();
	
	
}
