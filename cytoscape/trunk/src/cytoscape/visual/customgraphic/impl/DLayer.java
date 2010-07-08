package cytoscape.visual.customgraphic.impl;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.customgraphic.Layer;

public class DLayer implements Layer {
	
	private final CustomGraphic layer;
	private final int zOrder;
	
	public DLayer(final CustomGraphic layer, final int zOrder) {
		this.layer = layer;
		this.zOrder = zOrder;
	}

	@Override
	public int getZorder() {
		return zOrder;
	}
	
	public Object getLayerObject() {
		return layer;
	}

}
