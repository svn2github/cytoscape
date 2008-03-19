/*
 File: NetworkViewManager.java

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
package cytoscape.view;

import org.cytoscape.GraphPerspective;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import org.cytoscape.view.GraphView;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * 
 */
public class NetworkViewManager implements PropertyChangeListener,
		InternalFrameListener {
	private JDesktopPane desktopPane;

	private Map<String, JInternalFrame> networkViewMap;

	private Map<JInternalFrame, String> componentMap;

	protected CytoscapeDesktop cytoscapeDesktop;

	protected SwingPropertyChangeSupport pcs;

	/**
	 * Creates a new NetworkViewManager object.
	 * 
	 * @param desktop
	 *            DOCUMENT ME!
	 */
	public NetworkViewManager(CytoscapeDesktop desktop) {
		this.cytoscapeDesktop = desktop;
		desktopPane = new JDesktopPane();
		pcs = new SwingPropertyChangeSupport(this);

		// add Help hooks
		CyHelpBroker.getHelpBroker().enableHelp(desktopPane,
				"network-view-manager", null);

		networkViewMap = new HashMap<String, JInternalFrame>();
		componentMap = new HashMap<JInternalFrame, String>();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}


	/**
	 * Given a GraphView, returns the internal frame.
	 * 
	 * @param view
	 *            GraphView
	 * @return JInternalFrame
	 * @throws IllegalArgumentException
	 */
	public JInternalFrame getInternalFrame(GraphView view)
			throws IllegalArgumentException {
		// check args
		if (view == null) {
			throw new IllegalArgumentException(
					"NetworkViewManager.getInternalFrame(), argument is null");
		}

		// outta here
		return networkViewMap.get(view.getIdentifier());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param network
	 *            DOCUMENT ME!
	 */
	public void updateNetworkTitle(GraphPerspective network) {
		JInternalFrame frame = networkViewMap.get(network.getIdentifier());

		frame.setTitle(network.getTitle());
		frame.repaint();
	}

	/**
	 * Fire Events when a Managed Network View gets the Focus.
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		// System.out.println("NetworkViewManager: internalFrameActivated ");
		String network_id = componentMap.get(e.getInternalFrame());

		if (network_id == null) {
			return;
		}

		firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, null,
				network_id);
	}

	/**
	 * Fire Events when a Managed Network View gets the Focus.
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
		internalFrameActivated(e);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	/**
	 * This handles all of the incoming PropertyChangeEvents. If you are going
	 * to have multiple NetworkViewManagers, then this method should be extended
	 * such that the desired behaviour is achieved, assuming of course that you
	 * want your NetworkViewManagers to behave differently.
	 */
	public void propertyChange(PropertyChangeEvent e) {

		// handle focus event
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS) {
			String network_id = (String) e.getNewValue();
			e = null;
			unsetFocus(); // in case the newly focused network doesn't have a
							// view
			setFocus(network_id);

			if (this.getDesktopPane() != null) {
				Cytoscape.getCurrentNetworkView().addTransferComponent(this.getDesktopPane());
			}
		}

		// handle putting a newly created GraphView into a Container
		else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			GraphView new_view = (GraphView) e.getNewValue();
			createContainer(new_view);
			e = null;
		}

		// handle a NetworkView destroyed
		else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED) {
			GraphView view = (GraphView) e.getNewValue();
			removeView(view);
			e = null;
		}
	}

	/**
	 * Fires a PropertyChangeEvent
	 */
	public void firePropertyChange(String property_type, Object old_value,
			Object new_value) {
		pcs.firePropertyChange(new PropertyChangeEvent(this, property_type,
				old_value, new_value));
	}

	/**
	 * Used to unset the focus of all the views. This is for the situation when
	 * a network is focused but the network doesn't have a view.
	 */
	protected void unsetFocus() {
		for (JInternalFrame f : networkViewMap.values()) {
			try {
				f.setSelected(false);
			} catch (PropertyVetoException pve) {
				System.out
						.println("NetworkViewManager: Couldn't unset focus for internal frame.");
			}
		}
	}

	/**
	 * Sets the focus of the passed network, if possible The Network ID
	 * corresponds to the GraphView.getGraphPerspective().getIdentifier()
	 */
	protected void setFocus(String network_id) {
		if (networkViewMap.containsKey(network_id)) {
			try {
				networkViewMap.get(network_id).setIcon(false);
				networkViewMap.get(network_id).show();
				// fires internalFrameActivated
				networkViewMap.get(network_id).setSelected(true);
			} catch (Exception e) {
				System.err.println("Network View unable to be focused");
			}
		}
	}

	protected void removeView(GraphView view) {
		try {
			networkViewMap.get(view.getGraphPerspective().getIdentifier()).dispose();
		} catch (Exception e) {
			System.err.println("Network View unable to be killed");
		}

		networkViewMap.remove(view.getGraphPerspective().getIdentifier());
	}

	/**
	 * Contains a GraphView.
	 */
	protected void createContainer(final GraphView view) {
		if (networkViewMap.containsKey(view.getGraphPerspective().getIdentifier())) {
			// already contains
			return;
		}

		// create a new InternalFrame and put the GraphViews Component into
		// it
		JInternalFrame iframe = new JInternalFrame(view.getTitle(), true, true,
				true, true);
		iframe.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				Cytoscape.destroyNetworkView(view);
			}
		});
		desktopPane.add(iframe);


		iframe.setContentPane( view.getContainer(iframe.getLayeredPane()) );

		iframe.pack();
		iframe.setSize(400, 400);

		// maximize the frame if the specified property is set
		try {
			String max = CytoscapeInit.getProperties().getProperty(
					"maximizeViewOnCreate");

			if ((max != null) && Boolean.parseBoolean(max))
				iframe.setMaximum(true);
		} catch (PropertyVetoException pve) {
			pve.printStackTrace();
		}

		iframe.setVisible(true);
		iframe.addInternalFrameListener(this);

		networkViewMap.put(view.getGraphPerspective().getIdentifier(), iframe);
		componentMap.put(iframe, view.getGraphPerspective().getIdentifier());

		firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, null, view
				.getGraphPerspective().getIdentifier());
	}
}
