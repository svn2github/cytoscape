package org.cytoscape.filter.internal;
import org.cytoscape.filter.internal.filters.view.FilterMainPanel;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedListener;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;


public class FilterPanelStateSynchronizer implements SetCurrentNetworkViewListener, NetworkAddedListener, NetworkDestroyedListener {
	
	private FilterMainPanel filterPanel;

	public FilterPanelStateSynchronizer(FilterMainPanel filterPanel) {
		this.filterPanel = filterPanel;
	}
	
	@Override
	public void handleEvent(SetCurrentNetworkViewEvent e) {
		filterPanel.handleNetworkFocused(e.getNetworkView());
	}

	@Override
	public void handleEvent(NetworkDestroyedEvent e) {
		filterPanel.enableForNetwork();
		filterPanel.updateFeedbackTableModel();
	}

	@Override
	public void handleEvent(NetworkAddedEvent e) {
		filterPanel.enableForNetwork();
		filterPanel.updateFeedbackTableModel();
	}
}
