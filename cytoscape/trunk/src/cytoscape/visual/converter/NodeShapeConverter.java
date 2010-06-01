package cytoscape.visual.converter;

import cytoscape.visual.NodeShape;

public class NodeShapeConverter implements ValueToStringConverter {

	@Override
	public String toString(Object value) {
		if(value instanceof NodeShape)
			return NodeShape.getNodeShapeText((NodeShape) value);
		else
			return "";
	}

	@Override
	public Class<?> getType() {
		return NodeShape.class;
	}

}
