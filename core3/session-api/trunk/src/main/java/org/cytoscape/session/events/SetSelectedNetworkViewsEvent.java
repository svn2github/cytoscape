
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;

import java.util.List;

/**
 * 
 */
public final class SetSelectedNetworkViewsEvent extends AbstractCyEvent<CyNetworkManager> {
	private final List<CyNetworkView> views;
	public SetSelectedNetworkViewsEvent(final CyNetworkManager source, final List<CyNetworkView> views) {
		super(source, SetSelectedNetworkViewsListener.class);
		this.views = views;
	}
	public List<CyNetworkView> getNetworkViews() {
		return views;
	}
}
