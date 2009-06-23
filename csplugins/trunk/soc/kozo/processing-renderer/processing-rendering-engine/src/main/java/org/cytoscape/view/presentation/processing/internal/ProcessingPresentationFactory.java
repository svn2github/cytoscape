package org.cytoscape.view.presentation.processing.internal;

import java.awt.Window;

import javax.swing.JDialog;

import org.cytoscape.view.model.CyNetworkView;
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
		ProcessingNetworkRenderer presentation = new ProcessingNetworkRenderer(400);
		System.out.println("* Creating Processing Dialog");
		if(frame instanceof JDialog){
			JDialog window = (JDialog)frame;
			window.add(presentation);
			window.pack();
			window.setVisible(true);
			
			System.out.println("* Creating Processing Dialog OK!!!!!");
		}
	}

	public NetworkRenderer getPresentation(CyNetworkView view) {
		return new ProcessingNetworkRenderer(400);
	}

	public void visualPropertySet(VisualProperty vp, Object value) {
		// TODO Auto-generated method stub

	}

	public void handleEvent(NetworkViewChangedEvent e) {
		// TODO Auto-generated method stub

	}

}
