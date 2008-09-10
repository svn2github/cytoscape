package org.cytoscape.view.model2;

import java.awt.Shape;
import java.awt.Paint;

import org.cytoscape.model.network.Identifiable;

/**
 * The base interface that defines the methods used to set visual properties
 * for nodes, edges, and networks.  
 */
public interface View<T> extends Identifiable {

	/**
	 * The VisualProperty object identifies which visual property to
	 * set and the Object determines the value.  
	 * We should probably consider doing something more type safe like
	 * what we're doing for Attributes.
	 */
	public <T> void setVisualProperty(VisualProperty<T> vp, T o);

	/**
	 * Getting visual properties in this way incurs lots of casting.
	 * We should probably consider doing something more type safe like
	 * what we're doing for Attributes.
	 */
	public <T> T getVisualProperty(VisualProperty<T> vp);

	public T getSource();
}
