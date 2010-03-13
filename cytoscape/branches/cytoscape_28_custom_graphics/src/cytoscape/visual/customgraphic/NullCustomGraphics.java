package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.util.Collection;

import cytoscape.render.stateful.CustomGraphic;

/**
 * Null object for Custom Graphics
 * 
 * @author kono
 *
 */
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


	@Override
	public Image getImage() {
		return null;
	}

}
