package org.cytoscape.view.presentation.processing.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.view.presentation.PresentationFactory;
import org.cytoscape.view.presentation.Renderer;
import org.cytoscape.view.presentation.processing.P5Renderer;

public class ProcessingPresentationFactory implements PresentationFactory,
		NetworkViewChangedListener {

	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1200,
			1000);

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

	public <T extends View<?>> Renderer<T> addPresentation(
			final Container frame2, final T view) {
		// Check parameter
//		if (view == null)
//			throw new IllegalArgumentException(
//					"Cannot create presentation for null view model.");

		// Class<? extends P5Renderer<?>> rendererClass = null;
		//		
		// for(Class<?> key :rendererMap.keySet()) {
		// if(key.isAssignableFrom(view.getSource().getClass())) {
		// rendererClass = rendererMap.get(key);
		// }
		// }
		//		
		// if (rendererClass == null)
		// throw new IllegalArgumentException(
		// "Cannot create presentation for "
		// + view.getSource().getClass().toString() +
		// ":  Could not find implementation.");
		//
		// System.out.println("====== Creating Processing Dialog =========");
		//
		//		
		//
		// P5Renderer<?> renderer = null;
		// try {
		// Constructor<? extends P5Renderer<?>> ct = rendererClass
		// .getConstructor(Container.class, View.class);
		//			
		//
		// renderer = ct.newInstance(frame, view);
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NoSuchMethodException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		//		
		System.out.println("\n\n\n\n\n!!!!!!!!!! Calling: ");

		System.out.println("\n\n\n\n\n!!!!!!!!!! Done! ");
		// window.setTitle(title);
		
//		 JFrame f = (JFrame) frame;
//		 PAppletFrameListener listener = new PAppletFrameListener(rend);
//		 f.addWindowListener(listener);

		show((JFrame) frame2);


		//		

		return null;
		//return (Renderer<T>) rend;
	}
	
	private void show(final JFrame frame) {
		final ProcessingNetworkRenderer rend = new ProcessingNetworkRenderer(
				DEFAULT_WINDOW_SIZE, null);
		rend.init();
		 EventQueue.invokeLater(new Runnable() {
	            

			public void run() {
				// TODO Auto-generated method stub
				
				frame.setLayout(new BorderLayout());
				frame.setSize(DEFAULT_WINDOW_SIZE);
				frame.setPreferredSize(DEFAULT_WINDOW_SIZE);
				frame.add(rend.getComponent(), BorderLayout.CENTER);
				
				frame.pack();

				frame.setLocationByPlatform(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				frame.addWindowListener(new WindowAdapter() {

		            @Override
		            public void windowClosing(WindowEvent e) {
		                // Run this on another thread than the AWT event queue to
		                // make sure the call to Animator.stop() completes before
		                // exiting
		                new Thread(new Runnable() {

		                    public void run() {
		                    	System.out.println("========== P closed.");
		            			rend.stop();
		            			System.out.println("========== P Finished!==============");
		            			//System.gc();
		                    }
		                }).start();
		            }
		        });
				
				
				frame.setVisible(true);
				
				System.out.println(" ========= Processing Dialog Had been created.");
			}

		});
		
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
