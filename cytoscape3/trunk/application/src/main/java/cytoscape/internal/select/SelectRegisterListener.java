
package cytoscape.internal.select;


import java.util.Properties;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class SelectRegisterListener implements NetworkViewAddedListener {

	private CyServiceRegistrar registrar;

	public SelectRegisterListener(CyServiceRegistrar registrar) {
		this.registrar = registrar;
	}

	public void handleEvent(NetworkViewAddedEvent e) {
		final CyNetworkView view = e.getNetworkView();
		
		// FIXME: do not register all nodes.  Instead, register 
		
		for ( View<CyNode> nv : view.getNodeViews() )
			registrar.registerService( new SelectNodeViewUpdater(nv), RowSetMicroListener.class, new Properties() );

		for ( View<CyEdge> ev : view.getEdgeViews() ) 
			registrar.registerService( new SelectEdgeViewUpdater(ev), RowSetMicroListener.class, new Properties() );
	}
}
