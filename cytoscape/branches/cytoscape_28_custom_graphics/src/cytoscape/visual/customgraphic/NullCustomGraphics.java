package cytoscape.visual.customgraphic;

import java.util.Collection;

import cytoscape.render.stateful.CustomGraphic;

public class NullCustomGraphics implements CyCustomGraphics<CustomGraphic> {

	private static final String NAME = "Empty Custom Graphics";
	
	@Override
	public Collection<CustomGraphic> getCustomGraphics() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return NAME;
	}

}
