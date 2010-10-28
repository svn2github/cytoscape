package org.cytoscape.session.events;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;


/**
 * An event indicating that a network view has been set to current.
 */
public final class SetCurrentNetworkViewEvent extends AbstractNetworkViewEvent {

	/**
	 * Constructor.
	 * @param source The application manager firing this event.
	 * @param view The network view that has been set as current.
	 */
	public SetCurrentNetworkViewEvent(final CyApplicationManager source, final CyNetworkView view) {
		super(source, SetCurrentNetworkViewListener.class, view);
	}
}
