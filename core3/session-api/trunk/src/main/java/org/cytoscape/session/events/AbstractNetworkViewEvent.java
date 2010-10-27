package org.cytoscape.session.events;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;


/**
 *  Base class for all derived concrete event classes classes in this package that require a CyNetworkView.
 */
class AbstractNetworkViewEvent extends AbstractCyEvent<CyApplicationManager> {
	private final CyNetworkView view;

	AbstractNetworkViewEvent(final CyApplicationManager source, final Class listenerClass, final CyNetworkView view) {
		super(source, listenerClass);

		if (view == null)
			throw new NullPointerException("the \"view\" parameter must never be null!");
		this.view = view;
	}

	final public CyNetworkView getNetworkView() {
		return view;
	}
}
