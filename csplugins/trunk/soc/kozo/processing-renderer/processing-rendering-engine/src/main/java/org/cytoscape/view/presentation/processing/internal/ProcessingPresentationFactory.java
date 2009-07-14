package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_TITLE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.view.presentation.PresentationFactory;
import org.cytoscape.view.presentation.Renderer;
import org.cytoscape.view.presentation.processing.P5Renderer;

import processing.core.PApplet;

public class ProcessingPresentationFactory implements PresentationFactory,
		NetworkViewChangedListener {

	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1900, 1000);

	private final Map<Class<?>, Class<? extends P5Renderer<?>>> rendererMap;

	public ProcessingPresentationFactory() {
		rendererMap = new HashMap<Class<?>, Class<? extends P5Renderer<?>>>();

		// This is the default renderer for network.
		rendererMap.put(CyNetwork.class, ProcessingNetworkRenderer.class);
	}

	public void registerPresentation(Class<?> targetDataType,
			Class<P5Renderer<?>> rendererClass) {
		rendererMap.put(targetDataType, rendererClass);
	}

	public NavigationPresentation addNavigationPresentation(
			Object targetComponent, Object navBounds) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends View<?>> Renderer<T> addPresentation(Container frame,
			T view) {
		// Check parameter
		if (view == null)
			throw new IllegalArgumentException(
					"Cannot create presentation for null view model.");
		
		Class<? extends P5Renderer<?>> rendererClass = null;
		
		for(Class<?> key :rendererMap.keySet()) {
			if(key.isAssignableFrom(view.getSource().getClass())) {
				rendererClass = rendererMap.get(key);
			}
		}
		
		if (rendererClass == null)
			throw new IllegalArgumentException(
					"Cannot create presentation for "
							+ view.getSource().getClass().toString() + ":  Could not find implementation.");

		System.out.println("====== Creating Processing Dialog =========");

		

		P5Renderer<?> renderer = null;
		try {
			Constructor<? extends P5Renderer<?>> ct = rendererClass
					.getConstructor(Container.class, View.class);
			

			renderer = ct.newInstance(frame, view);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		ProcessingNetworkRenderer rend = new ProcessingNetworkRenderer(frame, (View<CyNetwork>) view);
		// window.setTitle(title);
		frame.setLayout(new BorderLayout());
		frame.setSize(DEFAULT_WINDOW_SIZE);
		frame.setPreferredSize(DEFAULT_WINDOW_SIZE);
		frame.add(rend.getComponent(), BorderLayout.CENTER);
		((PApplet)rend).init();
		((JFrame) frame).pack();
		
		((JFrame) frame).setLocationByPlatform(true);
		((JFrame) frame).setVisible(true);
		((JFrame) frame).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		System.out.println(" ========= Processing Dialog Had been created.");

		return (Renderer<T>) rend;
	}

	public void visualPropertySet(VisualProperty<?> vp, Object value) {
		// TODO Auto-generated method stub

	}

	public void handleEvent(NetworkViewChangedEvent e) {
		// TODO Auto-generated method stub

	}

	public <T extends View<?>> Renderer<T> createPresentation(T view) {
		// TODO Auto-generated method stub
		return null;
	}

}
