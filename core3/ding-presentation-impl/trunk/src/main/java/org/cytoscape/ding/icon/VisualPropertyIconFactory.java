package org.cytoscape.ding.icon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.Icon;

import org.cytoscape.ding.NodeShape;
import org.cytoscape.ding.ObjectPosition;
import org.cytoscape.ding.customgraphics.CyCustomGraphics;
import org.cytoscape.view.model.VisualProperty;


/**
 * Static factory for icons.
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
		} else if(value instanceof Stroke) {
			icon = new StrokeIcon((Stroke) value, w, h, value.toString());
		} else if(value instanceof CyCustomGraphics) {
			icon = new CustomGraphicsIcon(((CyCustomGraphics) value), w, h, ((CyCustomGraphics) value).getDisplayName());
		} else if(value instanceof ObjectPosition) {
			icon = new ObjectPositionIcon((ObjectPosition) value, w, h, "Label");
		}  else if(value instanceof Font) {
			icon = new FontFaceIcon((Font) value, w, h, "");
		} else {
			// If not found, use text as icon.
			icon = new TextIcon(value, w, h, value.toString());
		}
		
		return icon;
	}
}
