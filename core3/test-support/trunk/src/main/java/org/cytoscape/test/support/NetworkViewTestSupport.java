
package org.cytoscape.test.support;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.internal.ColumnOrientedNetworkViewFactoryImpl;

public class NetworkViewTestSupport extends NetworkTestSupport {

	protected CyNetworkViewFactory viewFactory;

	public NetworkViewTestSupport() {
		super();
		viewFactory = new ColumnOrientedNetworkViewFactoryImpl( eventHelper );
	}

	public CyNetworkView getNetworkView() {
		return viewFactory.getNetworkViewFor( getNetwork() );
	}
}


