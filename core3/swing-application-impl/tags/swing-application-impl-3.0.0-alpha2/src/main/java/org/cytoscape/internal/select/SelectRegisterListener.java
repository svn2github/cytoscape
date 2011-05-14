
package org.cytoscape.internal.select;


import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;

public class SelectRegisterListener implements NetworkViewAddedListener {

	private CyEventHelper eventHelper;

	public SelectRegisterListener(CyEventHelper eventHelper) {
		this.eventHelper = eventHelper;
	}

	public void handleEvent(NetworkViewAddedEvent e) {
		final CyNetworkView view = e.getNetworkView();

		for ( View<CyNode> nv : view.getNodeViews() )
			eventHelper.addMicroListener( new SelectNodeViewUpdater(nv), RowSetMicroListener.class, nv.getModel().getCyRow() );

		for ( View<CyEdge> ev : view.getEdgeViews() ) 
			eventHelper.addMicroListener( new SelectEdgeViewUpdater(ev), RowSetMicroListener.class, ev.getModel().getCyRow() );
	}
}
