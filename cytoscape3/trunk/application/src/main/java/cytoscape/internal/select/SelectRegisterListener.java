
package cytoscape.internal.select;


import java.util.Properties;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class SelectRegisterListener implements NetworkViewAddedListener {

	private CyEventHelper eventHelper;

	public SelectRegisterListener(CyEventHelper eventHelper) {
		this.eventHelper = eventHelper;
	}

	public void handleEvent(NetworkViewAddedEvent e) {
		final CyNetworkView view = e.getNetworkView();

		for ( View<CyNode> nv : view.getNodeViews() )
			eventHelper.addMicroListener( new SelectNodeViewUpdater(nv), RowSetMicroListener.class, nv.getModel().attrs() );

		for ( View<CyEdge> ev : view.getEdgeViews() ) 
			eventHelper.addMicroListener( new SelectEdgeViewUpdater(ev), RowSetMicroListener.class, ev.getModel().attrs() );
	}
}
