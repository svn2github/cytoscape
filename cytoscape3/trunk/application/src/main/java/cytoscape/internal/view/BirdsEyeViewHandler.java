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
package cytoscape.internal.view;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.view.presentation.PresentationFactory;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.events.NetworkViewDestroyedEvent;
import org.cytoscape.session.events.NetworkViewDestroyedListener;
import org.cytoscape.session.events.SetCurrentNetworkEvent;
import org.cytoscape.session.events.SetCurrentNetworkListener;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;

/**
 * This class handles the creation of the BirdsEyeView navigation object 
 * and handles the events which change view seen. 
 */
class BirdsEyeViewHandler implements 
	SetCurrentNetworkListener,
	SetCurrentNetworkViewListener,
	NetworkViewDestroyedListener
	{
	final NavigationPresentation bev;
	FrameListener frameListener = new FrameListener();
	final NetworkViewManager viewmgr;
	final CyNetworkManager netmgr;
	final PresentationFactory prefact;
	final Component bevHolder; 

	/**
	 * Creates a new BirdsEyeViewHandler object.
	 * @param desktopPane The JDesktopPane of the NetworkViewManager. Can be null.
	 */
	BirdsEyeViewHandler(final NetworkViewManager viewmgr, final CyNetworkManager netmgr, PresentationFactory defaultFactory) {
		this.viewmgr = viewmgr;
		this.netmgr = netmgr;
		
		//TODO: remove this.  Sync. timing and get this from view manager.
		this.prefact = defaultFactory;
		JDesktopPane desktopPane = viewmgr.getDesktopPane();

		bevHolder = new JPanel();

		bev = prefact.addNavigationPresentation(bevHolder, desktopPane);
		bev.changeView(netmgr.getCurrentNetworkView());
 
 		desktopPane.addComponentListener(new DesktopListener());
	}

	/**
	 * Listens for NETWORK_VIEW_FOCUSED, NETWORK_VIEW_FOCUS, NETWORK_VIEW_DESTROYED,
	 * and CYTOSCAPE_INITIALIZED events and changes the network view accordingly.
	 *
	 * @param e The event triggering this method. 
	 */
	public void handleEvent(SetCurrentNetworkEvent e) {
		bev.changeView(netmgr.getCurrentNetworkView());
		setFocus();
	}

	public void handleEvent(SetCurrentNetworkViewEvent e) {
		bev.changeView(netmgr.getCurrentNetworkView());
		setFocus();
	}

	public void handleEvent(NetworkViewDestroyedEvent e) {
		bev.changeView(netmgr.getCurrentNetworkView());
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
		for(int i = 0; i < listeners.length; i++)
			if (listeners[i] == frameListener)
				hasListener = true;

		if (!hasListener)
			frame.addComponentListener(frameListener);
	}

	/**
	 * Returns a birds eye view component.
	 * @return The component that contains the birds eye view.
	 */
	Component getBirdsEyeView() {
		return bevHolder;
	}

	/**
	 * Repaint a JInternalFrame whenever it is moved.
	 */
	class FrameListener extends ComponentAdapter {
		public void componentMoved(ComponentEvent e) { bevHolder.repaint(); }
	}

	/**
	 * Repaint the JDesktopPane whenever its size has changed.
	 */
	class DesktopListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) { bevHolder.repaint(); }
	}
}
