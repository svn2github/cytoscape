package org.cytoscape.view;

import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;

import org.cytoscape.view.renderers.NodeRenderer;

/** A Visual Property that has a finite site of discrete values.
 * The set of values is dynamically extendible via OSGi 
 */
public class DiscreteVisualProperty implements VisualProperty {
	private String name;
	private Class dataType = DiscreteValue.class; // Data type
	private Class underlyingDataType; // Data type of the values
	private boolean isNodeProp; // indicates whether or not property is for a node or edge
	private Object [] values;
	private Map<Object, Icon> icons;
	private DependentVisualPropertyCallback callback;
	
	// note: default value is the first one; number of values and icons must match
	public DiscreteVisualProperty(final String name, final Class dataType, final boolean isNodeProp, Object [] values, Map<Object, Icon>icons) {
		this(name, dataType, isNodeProp, values, icons, null);
	}
	public DiscreteVisualProperty(final String name, final Class dataType, final boolean isNodeProp, Object [] values, Map<Object, Icon>icons,
			DependentVisualPropertyCallback callback) {
		this.underlyingDataType = dataType;
		this.isNodeProp = isNodeProp;
		this.name = name;
		this.values = values;
		this.icons = icons;
		this.callback = callback;
	}

	public void applyToEdgeView(EdgeView ev, Object o) {
		// FIXME FIXME FIXME: this will be replaced with a simple "ev.setVisualAttribute(o)" -- infact, such a method isn't even needed in VisualProperty
		// the following is only needed until we refactor the ViewModel layer & rendering
		if ((o == null) || (ev == null)){
			return;
		} else if (name.equals("EDGE_LINE_STYLE")){
			if (((Stroke) o) != ev.getStroke() )
				ev.setStroke((Stroke) o);
		} else if (name.equals("EDGE_SRCARROW_SHAPE")){
			final int newSourceEnd = -((Integer) o).intValue();
			if (newSourceEnd != ev.getSourceEdgeEnd() ) {
				ev.setSourceEdgeEnd(newSourceEnd);
			}
		} else if (name.equals("EDGE_TGTARROW_SHAPE")){
			final int newTargetEnd = -((Integer) o).intValue();
			if (newTargetEnd != ev.getTargetEdgeEnd()) {
				ev.setTargetEdgeEnd(newTargetEnd);
			}
		}	
	}

	public void applyToNodeView(NodeView nv, Object o) {
		// FIXME FIXME FIXME: this will be replaced with a simple "ev.setVisualAttribute(o)" -- infact, such a method isn't even needed in VisualProperty
		// the following is only needed until we refactor the ViewModel layer & rendering
		if ((o == null) || (nv == null)) {
			return;
		} else if (name.equals("NODE_SHAPE")){
			final int newShape = ((Integer) o).intValue();
			
			if (nv.getShape() != newShape)
				nv.setShape(newShape);
		} else if (name.equals("NODE_RENDERER")){
			NodeRenderer newRenderer = (NodeRenderer) o;
			if (nv.getRenderer() != newRenderer)
				nv.setRenderer(newRenderer);
		} 
	}

	public Class getDataType() {
		return dataType;
	}

	public Object getDefaultAppearanceObject() {
		if (values.length > 0)
			return values[0];
		else
			return null;
	}

	public Icon getDefaultIcon() {
		if (values.length > 0)
			return icons.get(values[0]);
		else
			return null;
	}

	public Icon getIcon(Object value) {
		return icons.get(value);
	}

	public Map<Object, Icon> getIconSet() {
		//return icons; //FIXME: shouldn't this work?
		return new HashMap<Object, Icon>(icons);
	}

	public String getName() {
		return name;
	}

	public boolean isNodeProp() {
		return isNodeProp;
	}

	public Object parseProperty(Properties props, String baseKey) {
		// FIXME FIXME: do IO later 
		return null;
	}
	public Object parseStringValue(String string){
		return null; // FIXME FIXME FIXME
	}
	public DependentVisualPropertyCallback dependentVisualPropertyCallback(){
		return callback;
	}
}
