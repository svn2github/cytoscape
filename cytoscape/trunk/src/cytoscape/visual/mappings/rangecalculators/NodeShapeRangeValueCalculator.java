package cytoscape.visual.mappings.rangecalculators;

import cytoscape.visual.NodeShape;
import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.parsers.NodeShapeParser;
import cytoscape.visual.parsers.ValueParser;

public class NodeShapeRangeValueCalculator implements
		RangeValueCalculator<NodeShape> {

	private ValueParser<NodeShape> parser;

	public NodeShapeRangeValueCalculator() {
		parser = new NodeShapeParser();
	}

	@Override
	public NodeShape getRange(Object attrValue) {
		if (attrValue instanceof String) {
			NodeShape obj = parser.parseStringValue((String) attrValue);
			System.out.println("Parsed OBj ======= " + obj);
			return obj;
		}
		return null;
	}

	@Override
	public boolean isCompatible(Class<?> type) {
		if(type.isAssignableFrom(NodeShape.class))
			return true;
		else
			return false;
	}

	
}
