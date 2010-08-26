package org.cytoscape.view.model.internal;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NodeViewChangeMicroListener;
import org.cytoscape.view.model.VisualProperty;

public class NodeViewImpl extends ViewImpl<CyNode> {

	public NodeViewImpl(CyNode model, CyEventHelper cyEventHelper) {
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
		
		cyEventHelper.getMicroListener(NodeViewChangeMicroListener.class, this).nodeVisualPropertySet(this, vp, value);
	}

}
