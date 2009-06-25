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
package cytoscape.internal.view;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_TITLE;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.NetworkRenderer;
import org.cytoscape.view.presentation.PresentationFactory;

import cytoscape.CyNetworkManager;
import cytoscape.events.NetworkViewAboutToBeDestroyedEvent;
import cytoscape.events.NetworkViewAboutToBeDestroyedListener;
import cytoscape.events.NetworkViewAddedEvent;
import cytoscape.events.NetworkViewAddedListener;
import cytoscape.events.SetCurrentNetworkEvent;
import cytoscape.events.SetCurrentNetworkListener;
import cytoscape.events.SetCurrentNetworkViewEvent;
import cytoscape.events.SetCurrentNetworkViewListener;
import cytoscape.view.CyHelpBroker;

/**
 * 
 */
public class NetworkViewManager implements InternalFrameListener,
		NetworkViewAddedListener, NetworkViewAboutToBeDestroyedListener,
		SetCurrentNetworkViewListener, SetCurrentNetworkListener {
	
	private final JDesktopPane desktopPane;

	private final Map<Long, JInternalFrame> networkViewMap;
	private final Map<Long, NetworkRenderer> presentationMap;

	private final Map<JInternalFrame, Long> componentMap;

	protected final int MINIMUM_WIN_WIDTH = 200;
	protected final int MINIMUM_WIN_HEIGHT = 200;

	private final CyNetworkManager netmgr;

	private Long currentViewId;

	private Properties props;

	private CyHelpBroker help;
	
	// Supports multiple presentations
	private Map<String, PresentationFactory> factories;
	
	private PresentationFactory currentPresentationFactory;
	
	//TODO: discuss the name and key of props.
	private static final String ID = "id";
	
	//TODO: for now, use this as default.  But in the future, we should provide UI to select presentation.
	private static final String DEFAULT_PRESENTATION = "ding";

	
	/**
	 * Creates a new NetworkViewManager object.
	 * 
	 * @param desktop
	 *            DOCUMENT ME!
	 */
	public NetworkViewManager(CyNetworkManager netmgr, Properties props,
			CyHelpBroker help) {
		this.factories = new HashMap<String, PresentationFactory>();
		
		this.netmgr = netmgr;
		this.props = props;
		desktopPane = new JDesktopPane();

		// add Help hooks
		help.getHelpBroker().enableHelp(desktopPane, "network-view-manager",
				null);

		networkViewMap = new HashMap<Long, JInternalFrame>();
		presentationMap = new HashMap<Long, NetworkRenderer>();
		componentMap = new HashMap<JInternalFrame, Long>();
		currentViewId = null;
	}
	
	/**
	 * Dynamically adding presentation factory.
	 * This is necessary to support multiple presentations.
	 * 
	 * 
	 * @param factory
	 * @param props
	 */
	public void addPresentationFactory(PresentationFactory factory, Map props) {
		System.out.print("\n\n\n Adding New Rendering Engine >>>>>>>>>>");
		
		Object rendererID = props.get(ID);
		if(rendererID == null) {
			throw new IllegalArgumentException("Renderer ID is null.");
		}
		
		factories.put(rendererID.toString(), factory);
		if(currentPresentationFactory == null && rendererID.equals(DEFAULT_PRESENTATION)) {
			currentPresentationFactory = factory;
		}
		
		System.out.println(">>>> New Rendering Engine is Available: " + rendererID +"\n\n\n");
	}
	
	public void removePresentationFactory(PresentationFactory factory, Map props) {
		factories.remove(props.get(ID));
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
	 * Given a CyNetworkView, returns the internal frame.
	 * 
	 * @param view
	 *            CyNetworkView
	 * @return JInternalFrame
	 * @throws IllegalArgumentException
	 */
	public JInternalFrame getInternalFrame(CyNetworkView view)
			throws IllegalArgumentException {
		// check args
		if (view == null) {
			throw new IllegalArgumentException(
					"NetworkViewManager.getInternalFrame(), argument is null");
		}

		// outta here
		return networkViewMap.get(view.getSource().getSUID());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param network
	 *            DOCUMENT ME!
	 */
	public void updateNetworkTitle(CyNetwork network) {
		JInternalFrame frame = networkViewMap.get(network.getSUID());

		frame.setTitle(network.attrs().get("name", String.class));
		frame.repaint();
	}

	/**
	 * Fire Events when a Managed Network View gets the Focus.
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		// System.out.println("NetworkViewManager: internalFrameActivated ");
		Long network_id = componentMap.get(e.getInternalFrame());

		if (network_id == null)
			return;

		netmgr.setCurrentNetworkView(network_id);
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

	public void handleEvent(SetCurrentNetworkViewEvent e) {
		System.out
				.println("NetworkViewManager - attempting to set current network view ");

		Long sourceNetwork = e.getNetworkView().getSource().getSUID();
		setFocus(sourceNetwork);
	}

	public void handleEvent(SetCurrentNetworkEvent e) {
		System.out
				.println("NetworkViewManager - attempting to set current network");

		Long sourceNetwork = e.getNetwork().getSUID();
		setFocus(sourceNetwork);
		
	}

	private void setFocus(Long network_id) {
		// make sure we're not redundant
		if (currentViewId != null && currentViewId.equals(network_id)) {
			System.out.println("setfocus return - redund");
			return;
		}

		currentViewId = network_id;

		// unset focus on frames
		for (JInternalFrame f : networkViewMap.values()) {
			try {
				f.setSelected(false);
			} catch (PropertyVetoException pve) {
				System.out
						.println("NetworkViewManager: Couldn't unset focus for internal frame.");
			}
		}

		// set focus
		if (networkViewMap.containsKey(network_id)) {
			try {
				System.out.println("should updated selection");
				JInternalFrame curr = networkViewMap.get(network_id);

				curr.setIcon(false);
				curr.show();
				// fires internalFrameActivated
				curr.setSelected(true);

				CyNetworkView view = netmgr.getNetworkView(network_id);
				// TODO ensure that this gets done when the presentationFactory
				// adds a presentation to the internal frame
				// if ( view != null )
				// view.addTransferComponent(desktopPane);
			} catch (Exception ex) {
				System.err.println("Network View unable to be focused");
			}
		} else {
			// System.out.println("asdf");
		}
	}

	public void handleEvent(NetworkViewAboutToBeDestroyedEvent nvde) {
		System.out.println("NetworkViewManager - network view destroyed ");
		removeView(nvde.getNetworkView());
	}

	public void handleEvent(NetworkViewAddedEvent nvae) {
		System.out.println("NetworkViewManager - network view added: "
				+ nvae.getNetworkView().getSource().getSUID());
		createContainer(nvae.getNetworkView());
	}

	protected void removeView(CyNetworkView view) {
		try {
			networkViewMap.get(view.getSource().getSUID()).dispose();
		} catch (Exception e) {
			System.err.println("Network View unable to be killed");
		}

		networkViewMap.remove(view.getSource().getSUID());
	}

	/**
	 * Contains a CyNetworkView.
	 */
	protected void createContainer(final CyNetworkView view) {
		if (networkViewMap.containsKey(view.getSource().getSUID())) {
			// already contains
			return;
		}

		// create a new InternalFrame and put the CyNetworkView Component into
		// it
		JInternalFrame iframe = new JInternalFrame(view
				.getVisualProperty(NETWORK_TITLE), true, true, true, true);
		iframe.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				netmgr.destroyNetworkView(view);
			}
		});
		desktopPane.add(iframe);

		// iframe.setContentPane( view.getContainer(iframe.getLayeredPane()) );
		this.presentationMap.put(view.getSource().getSUID(), this.currentPresentationFactory.addPresentation(iframe, view));

		iframe.pack();

		// create cascade iframe
		int x = 0;
		int y = 0;
		JInternalFrame refFrame = null;
		JInternalFrame[] allFrames = desktopPane.getAllFrames();

		if (allFrames.length > 1) {
			refFrame = allFrames[0];
		}

		if (refFrame != null) {
			x = refFrame.getLocation().x + 20;
			y = refFrame.getLocation().y + 20;
		}

		if (x > (desktopPane.getWidth() - MINIMUM_WIN_WIDTH)) {
			x = desktopPane.getWidth() - MINIMUM_WIN_WIDTH;
		}
		if (y > (desktopPane.getHeight() - MINIMUM_WIN_HEIGHT)) {
			y = desktopPane.getHeight() - MINIMUM_WIN_HEIGHT;
		}

		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		iframe.setBounds(x, y, 400, 400);

		// maximize the frame if the specified property is set
		try {
			String max = props.getProperty("maximizeViewOnCreate");

			if ((max != null) && Boolean.parseBoolean(max))
				iframe.setMaximum(true);
		} catch (PropertyVetoException pve) {
			pve.printStackTrace();
		}

		iframe.setVisible(true);
		iframe.addInternalFrameListener(this);

		networkViewMap.put(view.getSource().getSUID(), iframe);
		componentMap.put(iframe, view.getSource().getSUID());

		Long sourceNetwork = view.getSource().getSUID();
		netmgr.setCurrentNetworkView(sourceNetwork);
		netmgr.setCurrentPresentation(presentationMap.get(sourceNetwork));
	}
}
