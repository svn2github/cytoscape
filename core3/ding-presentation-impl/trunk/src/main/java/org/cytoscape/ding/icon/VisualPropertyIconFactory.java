package org.cytoscape.ding.icon;

import java.awt.Color;

import javax.swing.Icon;

import org.cytoscape.ding.LineStyle;
import org.cytoscape.ding.NodeShape;
import org.cytoscape.ding.customgraphics.CyCustomGraphics;
import org.cytoscape.view.model.VisualProperty;


/**
 * Static factory for icons.
 * 
 * @author kono
 *
 */
public class VisualPropertyIconFactory {	
	
	public static <V> Icon createIcon(VisualProperty<V> vp, V value, int w, int h) {
		if(value == null)
			return null;
		
		Icon icon = null;
		
		if(value instanceof Color) {
			icon = new ColorIcon((Color) value, w, h, value.toString());
		} else if(value instanceof NodeShape) {
			icon = new NodeIcon(((NodeShape) value).getShape(), w, h, ((NodeShape) value).getShapeName());
		} else if(value instanceof LineStyle) {
			icon = new LineTypeIcon(((LineStyle) value).getStroke(5), w, h, ((LineStyle) value).name());
		} else if(value instanceof CyCustomGraphics) {
			icon = new CustomGraphicsIcon(((CyCustomGraphics) value), w, h, ((CyCustomGraphics) value).getDisplayName());
		} else {
			icon = new TextIcon(value, w, h, value.toString());
		}
		
		return icon;
	}
}
