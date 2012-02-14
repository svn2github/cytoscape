package org.cytoscape.io.write;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

public class CyNetworkViewWriterContextImpl extends CyWriterContextImpl implements CyNetworkViewWriterContext {

	private CyNetwork network;
	private CyNetworkView view;

	@Override
	public void setNetwork(CyNetwork network) {
		this.network = network;
		
		// Let's keep it consistent!
		if (network != null && view != null && !network.equals(view.getModel())) {
			view = null;
		}
	}
	
	@Override
	public CyNetwork getNetwork() {
		return network;
	}
	
	@Override
	public void setNetworkView(CyNetworkView view) {
		this.view = view;
		
		if (view != null) {
			this.network = view.getModel();
		}
	}
	
	@Override
	public CyNetworkView getNetworkView() {
		return view;
	}

}
