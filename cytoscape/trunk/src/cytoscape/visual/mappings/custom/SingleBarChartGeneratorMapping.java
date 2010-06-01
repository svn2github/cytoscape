package cytoscape.visual.mappings.custom;

import java.awt.Component;
import java.util.Map;
import java.util.Properties;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.mappings.AbstractMapping;
import cytoscape.visual.mappings.CustomMapping;
import cytoscape.visual.parsers.ValueParser;

public class SingleBarChartGeneratorMapping extends
		AbstractMapping<CyCustomGraphics<?>> implements
		CustomMapping {

	private final CustomGraphicsBuilder builder;
	
	public SingleBarChartGeneratorMapping(Class<CyCustomGraphics<?>> rangeClass,
			final String controllingAttrName, final String targetPropertyName) {
		super(rangeClass, controllingAttrName);
		this.builder = new SimpleVectorBarBuilder(targetPropertyName);
		// Create editor component here.
	}


	@Override
	public CyCustomGraphics<?> calculateRangeValue(Map<String, Object> attrBundle) {
		if(attrBundle == null || attrBundle.get(controllingAttrName) == null)
			return null;
		final Object data = attrBundle.get(controllingAttrName);
		return builder.getGraphics(data);
	}

	
	@Override
	public void applyProperties(Properties props, String baseKey,
			ValueParser<CyCustomGraphics<?>> parser) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public Object clone() {
		return null;
	}

	@Override
	public Properties getProperties(String baseKey) {
		return null;
	}
	

	@Override
	public Component getMappingEditor() {
		return null;
	}

}
