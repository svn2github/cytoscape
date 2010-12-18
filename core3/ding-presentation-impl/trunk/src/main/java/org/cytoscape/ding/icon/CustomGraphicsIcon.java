package org.cytoscape.ding.icon;

import org.cytoscape.ding.customgraphics.CyCustomGraphics;

public class CustomGraphicsIcon extends VisualPropertyIcon<CyCustomGraphics> {

	private static final long serialVersionUID = -216647303312376087L;
	
	
	public CustomGraphicsIcon(CyCustomGraphics value, int width, int height,
			String name) {
		super(value, width, height, name);
		
		this.setImage(value.getRenderedImage());
	}
}
