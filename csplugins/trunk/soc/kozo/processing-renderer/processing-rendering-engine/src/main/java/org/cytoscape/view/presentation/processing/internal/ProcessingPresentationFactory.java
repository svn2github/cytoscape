package org.cytoscape.view.presentation.processing.internal;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.ViewChangeListener;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.view.presentation.NetworkRenderer;
import org.cytoscape.view.presentation.PresentationFactory;

public class ProcessingPresentationFactory implements PresentationFactory,
		NetworkViewChangedListener {

	public ProcessingPresentationFactory() {

	}

	public NavigationPresentation addNavigationPresentation(
			Object targetComponent, Object navBounds) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPresentation(Object frame, CyNetworkView view) {
		// TODO Auto-generated method stub

	}

	public NetworkRenderer getPresentation(CyNetworkView view) {
		// TODO Auto-generated method stub
		return null;
	}

	public void visualPropertySet(VisualProperty vp, Object value) {
		// TODO Auto-generated method stub

	}

	public void handleEvent(NetworkViewChangedEvent e) {
		// TODO Auto-generated method stub

	}

}
