package org.cytoscape.view.presentation.property;

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.DiscreteRangeImpl;
import org.cytoscape.view.presentation.internal.property.values.NodeShapeImpl;
import org.cytoscape.view.presentation.property.values.NodeShape;

public final class NodeShapeVisualProperty extends AbstractVisualProperty<NodeShape> {

	// Presets
	public static final NodeShape RECTANGLE = new NodeShapeImpl("Rectangle", "RECTANGLE");
	public static final NodeShape ROUND_RECTANGLE = new NodeShapeImpl("Round Rectangle", "ROUND_RECTANGLE");
	public static final NodeShape TRIANGLE = new NodeShapeImpl("Triangle", "TRIANGLE");
	public static final NodeShape PARALLELOGRAM = new NodeShapeImpl("Parallelogram", "PARALLELOGRAM");
	public static final NodeShape DIAMOND = new NodeShapeImpl("Diamond", "DIAMOND");
	public static final NodeShape ELLIPSE = new NodeShapeImpl("Ellipse", "ELLIPSE");
	public static final NodeShape HEXAGON = new NodeShapeImpl("Hexagon", "HEXAGON");
	public static final NodeShape OCTAGON = new NodeShapeImpl("Octagon", "OCTAGON");

	private static final DiscreteRange<NodeShape> NODE_SHAPE_RANGE;
	
	private static final Set<NodeShape> DEFAULT_SHAPES;

	static {
		DEFAULT_SHAPES = new HashSet<NodeShape>();
		DEFAULT_SHAPES.add(RECTANGLE);
		DEFAULT_SHAPES.add(ROUND_RECTANGLE);
		DEFAULT_SHAPES.add(TRIANGLE);
		DEFAULT_SHAPES.add(PARALLELOGRAM);
		DEFAULT_SHAPES.add(DIAMOND);
		DEFAULT_SHAPES.add(ELLIPSE);
		DEFAULT_SHAPES.add(HEXAGON);
		DEFAULT_SHAPES.add(OCTAGON);
		
		NODE_SHAPE_RANGE = new DiscreteRangeImpl<NodeShape>(NodeShape.class, new HashSet<NodeShape>(DEFAULT_SHAPES));
	}

	public NodeShapeVisualProperty(NodeShape defaultValue, String id,
			String displayName, Class<?> targetObjectDataType) {
		super(defaultValue, NODE_SHAPE_RANGE, id, displayName,
				targetObjectDataType);
	}

	@Override
	public String toSerializableString(NodeShape value) {
		return value.getSerializableString();
	}

	@Override
	public NodeShape parseSerializableString(String value) {
		// TODO
		return null;
	}
	
	public static boolean isDefaultShape(final NodeShape shape) {
		if(DEFAULT_SHAPES.contains(shape))
			return true;
		else
			return false;
	}
}
