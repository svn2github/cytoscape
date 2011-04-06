package org.cytoscape.view.presentation.property;

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.DiscreteRangeImpl;
import org.cytoscape.view.presentation.property.values.LineType;

public class LineTypeVisualProperty extends AbstractVisualProperty<LineType> {
	
	// Default basic line types.  Others will be provided from rendering engines.
	public static final LineType SOLID = new LineTypeImpl("Solid", "SOLID");
	public static final LineType LONG_DASH = new LineTypeImpl("Dash", "LONG_DASH");
	public static final LineType EQUAL_DASH = new LineTypeImpl("Equal Dash", "EQUAL_DASH");
	public static final LineType DASH_DOT = new LineTypeImpl( "Dash Dot", "DASH_DOT");
	public static final LineType DOT = new LineTypeImpl("Dots", "DOT");

	
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
	
	private static final class LineTypeImpl extends AbstractVisualPropertyValue implements LineType {

		public LineTypeImpl(String displayName, String serializableString) {
			super(displayName, serializableString);
		}

		@Override
		public VisualPropertyValue parseSerializableString(String serializableString) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
