/*
 File: BirdsEyeViewHandler.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.internal.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.events.SetCurrentNetworkEvent;
import org.cytoscape.session.events.SetCurrentNetworkListener;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewDestroyedListener;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the creation of the BirdsEyeView navigation object and
 * handles the events which change view seen.
 */
public class BirdsEyeViewHandler implements SetCurrentNetworkListener,
		SetCurrentNetworkViewListener, NetworkViewDestroyedListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BirdsEyeViewHandler.class);

	// BEV is just a special implementation of RenderingEngine.
	private final RenderingEngineFactory<CyNetwork> bevFactory;

	private final Map<CyNetworkView, RenderingEngine<CyNetworkView>> engineMap;

	FrameListener frameListener = new FrameListener();
	final NetworkViewManager viewmgr;
	final CyApplicationManager appMgr;
	final CyNetworkViewManager netViewMgr;

	final Container bevPanel;

	/**
	 * Creates a new BirdsEyeViewHandler object.
	 * 
	 * @param desktopPane
	 *            The JDesktopPane of the NetworkViewManager. Can be null.
	 */
	public BirdsEyeViewHandler(final NetworkViewManager viewmgr,
			final CyApplicationManager appMgr, CyNetworkViewManager netViewMgr,
			RenderingEngineFactory<CyNetwork> defaultFactory) {
		this.viewmgr = viewmgr;
		this.appMgr = appMgr;
		this.netViewMgr = netViewMgr;

		final JDesktopPane desktopPane = viewmgr.getDesktopPane();
		bevPanel = new JPanel();
		Dimension panelSize = new Dimension(280, 280);
		bevPanel.setPreferredSize(panelSize);
		bevPanel.setSize(panelSize);
		bevPanel.setBackground(Color.white);

		engineMap = new HashMap<CyNetworkView, RenderingEngine<CyNetworkView>>();

		bevFactory = defaultFactory;
		desktopPane.addComponentListener(new DesktopListener());
	}

	/**
	 * Listens for NETWORK_VIEW_FOCUSED, NETWORK_VIEW_FOCUS,
	 * NETWORK_VIEW_DESTROYED, and CYTOSCAPE_INITIALIZED events and changes the
	 * network view accordingly.
	 * 
	 * @param e
	 *            The event triggering this method.
	 */
	public void handleEvent(SetCurrentNetworkEvent e) {
		// This is guaranteed not to be null.
		final CyNetwork network = e.getNetwork();
		
		logger.debug("Got SetCurrentNetworkEvent.  BEV New Network = "
				+ network.getSUID());
		bevPanel.removeAll();

//		final CyNetworkView view = netViewMgr.getNetworkView(network.getSUID());
//		if(view != null) {
//			RenderingEngine<CyNetwork> engine = bevFactory.render(bevPanel,
//					netViewMgr.getNetworkView(network.getSUID()));
//			setFocus();
//		} else {
//			logger.warn("######### NO View exists for network = "
//					+ network);
//		}
	}

	public void handleEvent(SetCurrentNetworkViewEvent e) {
		final CyNetworkView view = e.getNetworkView();
		
		logger.debug("Got SetCurrentNetworkViewEvent.  BEV New Network = "
				+ view.getModel().getSUID());
		
//		if(view != null) {
//			RenderingEngine<CyNetwork> engine = bevFactory.render(bevPanel,
//					view);
//			setFocus();
//		} else {
//			logger.warn("######### NO View exists in the source");
//		}
//		
	}

	public void handleEvent(NetworkViewDestroyedEvent e) {
//		logger.debug("!!!!!!!!!! NetworkViewDestroyedEvent +++++++++++");
//		// Cleanup the visualization container
//		bevPanel.removeAll();
//		RenderingEngine<CyNetwork> engine = bevFactory.render(bevPanel,
//				appMgr.getCurrentNetworkView());
	}



	private void setFocus() {
		JDesktopPane desktopPane = viewmgr.getDesktopPane();
		if (desktopPane == null)
			return;

		JInternalFrame frame = desktopPane.getSelectedFrame();
		if (frame == null)
			return;

		boolean hasListener = false;
		ComponentListener[] listeners = frame.getComponentListeners();
		for (int i = 0; i < listeners.length; i++)
			if (listeners[i] == frameListener)
				hasListener = true;

		if (!hasListener)
			frame.addComponentListener(frameListener);
	}

	/**
	 * Returns a birds eye view component.
	 * 
	 * @return The component that contains the birds eye view.
	 */
	Component getBirdsEyeView() {
		return bevPanel;
	}

	/**
	 * Repaint a JInternalFrame whenever it is moved.
	 */
	class FrameListener extends ComponentAdapter {
		public void componentMoved(ComponentEvent e) {
			bevPanel.repaint();
		}
	}

	/**
	 * Repaint the JDesktopPane whenever its size has changed.
	 */
	class DesktopListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			bevPanel.repaint();
		}
	}
}
