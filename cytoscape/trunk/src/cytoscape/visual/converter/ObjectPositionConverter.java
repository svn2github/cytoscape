package cytoscape.visual.converter;

import giny.view.ObjectPosition;

public class ObjectPositionConverter implements ValueToStringConverter {

	@Override
	public String toString(Object value) {
		if(value instanceof ObjectPosition)
			return ((ObjectPosition) value).shortString();
		else
			return "";
	}

	@Override
	public Class<?> getType() {
		return ObjectPosition.class;
	}

}