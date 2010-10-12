package cytoscape.visual.converter;

import giny.view.ObjectPosition;

public class ObjectPositionConverter implements ValueToStringConverter {

	public String toString(Object value) {
		
		if(value instanceof ObjectPosition)
			return ((ObjectPosition) value).shortString();
		else
			return "";
	}

	
	public Class<?> getType() {
		return ObjectPosition.class;
	}

}