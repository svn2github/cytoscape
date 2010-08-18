
package cytoscape.internal.select;


import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;

import org.cytoscape.service.util.CyServiceRegistrar;

import java.util.Properties;

public class SelectRegisterListener implements NetworkViewAddedListener {

	private CyServiceRegistrar registrar;

	public SelectRegisterListener(CyServiceRegistrar registrar) {
		this.registrar = registrar;
	}

	public void handleEvent(NetworkViewAddedEvent e) {
		CyNetworkView view = e.getNetworkView();

		for ( View<CyNode> nv : view.getNodeViews() )
			registrar.registerService( new SelectNodeViewUpdater(nv), RowSetMicroListener.class, new Properties() );

		for ( View<CyEdge> ev : view.getEdgeViews() ) 
			registrar.registerService( new SelectEdgeViewUpdater(ev), RowSetMicroListener.class, new Properties() );
	}
}
