
package org.cytoscape.test.support;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.internal.NetworkViewFactoryImpl;

public class NetworkViewTestSupport extends NetworkTestSupport {

	protected CyNetworkViewFactory viewFactory;

	public NetworkViewTestSupport() {
		super();
		viewFactory = new NetworkViewFactoryImpl( eventHelper, new StubServiceRegistrar() );
	}

	public CyNetworkView getNetworkView() {
		return viewFactory.getNetworkView( getNetwork() );
	}
}


