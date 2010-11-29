package org.cytoscape.ding;

import javax.swing.Icon;

import org.cytoscape.view.model.VisualProperty;

public interface IconFactory {
	
	<V> Icon createIcon(VisualProperty<V> vp, V value, int w, int h);

}
