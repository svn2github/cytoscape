package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_TITLE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.view.presentation.NetworkRenderer;
import org.cytoscape.view.presentation.PresentationFactory;

public class ProcessingPresentationFactory implements PresentationFactory,
		NetworkViewChangedListener {
	
	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1920, 1080);

	public ProcessingPresentationFactory() {

	}

	public NavigationPresentation addNavigationPresentation(
			Object targetComponent, Object navBounds) {
		// TODO Auto-generated method stub
		return null;
	}

	public NetworkRenderer addPresentation(Object frame, CyNetworkView view) {
		// Check parameter
		if ( view == null )
			throw new IllegalArgumentException("Cannot create presentation for null CyNetworkView.");
		
		System.out.println("====== Creating Processing Dialog =========");
		ProcessingNetworkRenderer presentation = null;
		
		if(frame instanceof Component){
			Component c = (Component) frame;
			final Dimension size = c.getSize();
			presentation = new ProcessingNetworkRenderer(DEFAULT_WINDOW_SIZE, view);
			
			String title = view.getVisualProperty(NETWORK_TITLE);
			
			JFrame window = (JFrame)frame;
			window.setTitle(title);
			window.setLayout(new BorderLayout());
			window.add(presentation, BorderLayout.CENTER);
			presentation.init();
			
			window.setPreferredSize(DEFAULT_WINDOW_SIZE);
			//window.add(presentation);
			window.pack();
			window.setLocationByPlatform(true);
			window.setVisible(true);
			
			System.out.println("* Creating Processing Dialog OK!!!!!");
		}
		
		return presentation;
	}

	public NetworkRenderer createPresentation(CyNetworkView view) {
		return new ProcessingNetworkRenderer(DEFAULT_WINDOW_SIZE, view);
	}

	public void visualPropertySet(VisualProperty vp, Object value) {
		// TODO Auto-generated method stub

	}

	public void handleEvent(NetworkViewChangedEvent e) {
		// TODO Auto-generated method stub

	}

}
