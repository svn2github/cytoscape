
package cytoscape.internal.select;


import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;

public class SelectRegisterListener implements NetworkViewAddedListener {

	public void handleEvent(NetworkViewAddedEvent e) {
		CyNetworkView view = e.getNetworkView();

		for ( View<CyNode> nv : view.getNodeViews() )
			nv.getSource().attrs().addRowListener( new SelectNodeViewUpdater(nv) );

		for ( View<CyEdge> ev : view.getEdgeViews() ) 
			ev.getSource().attrs().addRowListener( new SelectEdgeViewUpdater(ev) );
	}
}
