package cytoscape.visual.mappings.custom;

import java.awt.Component;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cytoscape.visual.mappings.AbstractMapping;
import cytoscape.visual.mappings.CustomMapping;
import cytoscape.visual.parsers.ValueParser;

import cytoscape.visual.customgraphic.CyCustomGraphics;

public class ChartGeneratorMapping extends
		AbstractMapping<CyCustomGraphics<?>> implements
		CustomMapping<CyCustomGraphics<?>> {

	private List<String> controllingAttributeNames;

	public ChartGeneratorMapping(Class<CyCustomGraphics<?>> rangeClass,
			List<String> controllingAttributeNames, final CustomGraphicsBuilder<Number> builder) {
		super(rangeClass, controllingAttributeNames.get(0));

		this.controllingAttributeNames = controllingAttributeNames;

	}

	@Override
	public void applyProperties(Properties props, String baseKey,
			ValueParser<CyCustomGraphics<?>> parser) {
		// TODO Auto-generated method stub

	}

	@Override
	public CyCustomGraphics<?> calculateRangeValue(Map<String, Object> attrBundle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object clone() {
		return null;
	}

	@Override
	public Properties getProperties(String baseKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getControllingAttributeNames() {
		return this.controllingAttributeNames;
	}

	@Override
	public Component getEditor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CyCustomGraphics<?> calculateRangeValueFromMultipleAttr(Map<String, ?> attributeValues) {
		// TODO Auto-generated method stub
		return null;
	}

}
