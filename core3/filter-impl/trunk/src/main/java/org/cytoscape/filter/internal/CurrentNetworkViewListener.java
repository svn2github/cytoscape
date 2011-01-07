package org.cytoscape.filter.internal;
import org.cytoscape.filter.internal.filters.view.FilterMainPanel;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;


public class CurrentNetworkViewListener implements SetCurrentNetworkViewListener {
	
	private FilterMainPanel filterPanel;

	public CurrentNetworkViewListener(FilterMainPanel filterPanel) {
		this.filterPanel = filterPanel;
	}
	
	@Override
	public void handleEvent(SetCurrentNetworkViewEvent e) {
		filterPanel.handleNetworkFocused(e.getNetworkView());
	}
}
