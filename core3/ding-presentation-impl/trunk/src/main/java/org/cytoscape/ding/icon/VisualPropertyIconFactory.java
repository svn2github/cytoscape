package org.cytoscape.ding.icon;

import java.awt.Color;

import javax.swing.Icon;

import org.cytoscape.ding.IconFactory;
import org.cytoscape.ding.LineStyle;
import org.cytoscape.ding.NodeShape;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;

public class VisualPropertyIconFactory implements IconFactory {

	
	
	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int w, int h) {
		
		Icon icon = null;
		
		if(value instanceof Color) {
			
		} else if(value instanceof NodeShape) {
			icon = new NodeIcon(((NodeShape) value).getShape(), w, h, ((NodeShape) value).getShapeName(), VisualPropertyIcon.DEFAULT_ICON_COLOR);
		} else if(value instanceof LineStyle) {
			
		} else {
			
		}
		
		return icon;
	}
}
