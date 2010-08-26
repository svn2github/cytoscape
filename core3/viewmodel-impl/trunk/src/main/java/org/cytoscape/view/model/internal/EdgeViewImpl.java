package org.cytoscape.view.model.internal;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.EdgeViewChangeMicroListener;
import org.cytoscape.view.model.VisualProperty;

public class EdgeViewImpl extends ViewImpl<CyEdge> {

	public EdgeViewImpl(CyEdge model, CyEventHelper cyEventHelper) {
		super(model, cyEventHelper);
		// TODO Auto-generated constructor stub
	}

	@Override
	public <T, V extends T> void setVisualProperty(
			VisualProperty<? extends T> vp, V value) {
		if(value == null)
			this.visualProperties.remove(vp);
		else
			this.visualProperties.put(vp, value);
		
		cyEventHelper.getMicroListener(EdgeViewChangeMicroListener.class, this).edgeVisualPropertySet(this, vp, value);
	}

}
