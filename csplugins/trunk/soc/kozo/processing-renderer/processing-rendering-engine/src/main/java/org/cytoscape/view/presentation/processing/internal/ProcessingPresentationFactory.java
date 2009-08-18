package org.cytoscape.view.presentation.processing.internal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.view.presentation.PresentationFactory;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.processing.CyDrawableManager;

public class ProcessingPresentationFactory implements PresentationFactory,
		NetworkViewChangedListener {

	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1200,
			900);
	
	private final CyDrawableManager manager;
	
	public ProcessingPresentationFactory(CyDrawableManager manager) {
		this.manager = manager;
	}

	// private final Map<Class<?>, Class<? extends P5Renderer<?>>> rendererMap;

	// public ProcessingPresentationFactory() {
	// rendererMap = new HashMap<Class<?>, Class<? extends P5Renderer<?>>>();
	//
	// // This is the default renderer for network.
	// rendererMap.put(CyNetwork.class, ProcessingNetworkRenderer.class);
	// }
	//
	// public void registerPresentation(Class<?> targetDataType,
	// Class<P5Renderer<?>> rendererClass) {
	// rendererMap.put(targetDataType, rendererClass);
	// }

	public NavigationPresentation addNavigationPresentation(
			Object targetComponent, Object navBounds) {
		// TODO Auto-generated method stub
		return null;
	}

	public RenderingEngine addPresentation(final Object target,
			final View<?> view) {
		// Check parameter
		if (view == null)
			throw new IllegalArgumentException(
					"Cannot create presentation for null view model.");

		System.out.println("\n\n\n\n\n!!!!!!!!!! Calling: ");
		

		final ProcessingNetworkRenderer rend = new ProcessingNetworkRenderer(
				DEFAULT_WINDOW_SIZE, (CyNetworkView) view, manager);
		rend.init();
		
		if(target instanceof JFrame == false) return rend;
		
		final JFrame frame = (JFrame) target;
		System.out.println("\n\n\n\n\n!!!!!!!!!! Init Done! ");
		
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				System.out.println("\n\n\n\n\n!!!!!!!!!! Running new thread! ");
				frame.setLayout(new BorderLayout());
				frame.setSize(DEFAULT_WINDOW_SIZE);
				frame.setPreferredSize(DEFAULT_WINDOW_SIZE);
				frame.add(rend, BorderLayout.CENTER);

				frame.pack();

				frame.setLocationByPlatform(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				frame.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						// Run this on another thread than the AWT event queue
						// to
						// make sure the call to Animator.stop() completes
						// before
						// exiting
						new Thread(new Runnable() {

							public void run() {
								System.out.println("========== P closed.");
								rend.stop();
								System.out
										.println("========== P Finished!==============");
								// System.gc();
							}
						}).start();
					}
				});

				frame.setVisible(true);

				System.out
						.println(" ========= Processing Dialog Had been created.");
			}

		});

		return rend;

	}

	public void visualPropertySet(VisualProperty<?> vp, Object value) {
		// TODO Auto-generated method stub

	}

	public void handleEvent(NetworkViewChangedEvent e) {
		// TODO Auto-generated method stub

	}

}
