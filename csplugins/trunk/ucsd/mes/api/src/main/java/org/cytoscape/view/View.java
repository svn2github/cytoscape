package org.cytoscape.view;

import java.awt.Shape;
import java.awt.Paint;

import org.cytoscape.model.network.Identifiable;

/**
 * The base interface that defines the methods used to set visual properties
 * for nodes, edges, and networks.  
 */
public interface View extends Identifiable {

	/**
	 * The VisualProperty object identifies which visual property to
	 * set and the Object determines the value.  
	 * We should probably consider doing something more type safe like
	 * what we're doing for Attributes.
	 */
	public void setVisualProperty(VisualProperty vp, Object o);

	/**
	 * Getting visual properties in this way incurs lots of casting.
	 * We should probably consider doing something more type safe like
	 * what we're doing for Attributes.
	 */
	public Object getVisualProperty(VisualProperty vp, Object o);

	/**
	 * Maybe we can figure out how to have a custom graphic visual property?
	 * This just recapitulates the old scheme for setting custom graphics which
	 * isn't fantastic.
	 */
	public int addCustomGraphic(Shape s, Paint p);
	public void removeCustomGraphic(int i);
}
