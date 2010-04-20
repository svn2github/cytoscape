package cytoscape.visual.mappings.custom;

import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.experimental.GradientRectangleCustomGraphics;

public class SimpleVectorBarBuilder implements CustomGraphicsBuilder {
	
	private final String targetPropName;
	
	public SimpleVectorBarBuilder(String targetPropName) {
		this.targetPropName = targetPropName;
	}

	@Override
	public CyCustomGraphics<?> getGraphics(Object data) {
		final CyCustomGraphics<?> graphics = new GradientRectangleCustomGraphics();
		graphics.getProps().get(targetPropName).setValue(data);
		graphics.update();
		
		return graphics;
	}
}
