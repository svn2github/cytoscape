package org.cytoscape.view.presentation.property;

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.DiscreteRangeImpl;
import org.cytoscape.view.presentation.internal.property.values.LineTypeImpl;
import org.cytoscape.view.presentation.property.values.LineType;

public class LineTypeVisualProperty extends AbstractVisualProperty<LineType> {
	
	public static final LineType SOLID = new LineTypeImpl("Solid", "SOLID");
	public static final LineType LONG_DASH = new LineTypeImpl("Dash", "LONG_DASH");
	public static final LineType EQUAL_DASH = new LineTypeImpl("Equal Dash", "EQUAL_DASH");
	public static final LineType DASH_DOT = new LineTypeImpl( "Dash Dot", "DASH_DOT");
	public static final LineType DOT = new LineTypeImpl("Dots", "DOT");
//	public static final LineType ZIGZAG("zigzag", new ZigzagStroke(1.0f)),
//	public static final LineType SINEWAVE("sinewave", new SineWaveStroke(1.0f)),
//	public static final LineType VERTICAL_SLASH("vertical_slash",new VerticalSlashStroke(1.0f,PipeStroke.Type.VERTICAL)),
//	public static final LineType FORWARD_SLASH("forward_slash",new ForwardSlashStroke(1.0f,PipeStroke.Type.FORWARD)),
//	public static final LineType BACKWARD_SLASH("backward_slash",new BackwardSlashStroke(1.0f,PipeStroke.Type.BACKWARD)),
//	public static final LineType PARALLEL_LINES("parallel_lines", new ParallelStroke(1.0f)),
//	public static final LineType CONTIGUOUS_ARROW("contiguous_arrow", new ContiguousArrowStroke(1.0f)),
//	public static final LineType SEPARATE_ARROW("separate_arrow", new SeparateArrowStroke(1.0f));
	
	private static DiscreteRange<LineType> LINE_TYPE_RANGE;
	private static final Set<LineType> lineTypes;
	
	static {
		lineTypes = new HashSet<LineType>();
		
		lineTypes.add(SOLID);
		lineTypes.add(LONG_DASH);
		lineTypes.add(EQUAL_DASH);
		lineTypes.add(DASH_DOT);
		lineTypes.add(DOT);
		
		LINE_TYPE_RANGE = new DiscreteRangeImpl<LineType>(LineType.class, new HashSet<LineType>(lineTypes));
	}

	public LineTypeVisualProperty(LineType defaultValue,
			String id, String displayName, Class<?> targetObjectDataType) {
		super(defaultValue, LINE_TYPE_RANGE, id, displayName, targetObjectDataType);
	}

	@Override
	public String toSerializableString(LineType value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LineType parseSerializableString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
