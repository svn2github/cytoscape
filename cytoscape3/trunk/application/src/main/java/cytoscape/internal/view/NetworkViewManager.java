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
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.session.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.session.events.SetCurrentNetworkEvent;
import org.cytoscape.session.events.SetCurrentNetworkListener;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cytoscape.view.CyHelpBroker;

/**
 * 
 */
public class NetworkViewManager implements InternalFrameListener,
		NetworkViewAddedListener, NetworkViewAboutToBeDestroyedListener,
		SetCurrentNetworkViewListener, SetCurrentNetworkListener {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkViewManager.class);
	
	private final JDesktopPane desktopPane;

	private final Map<Long, JInternalFrame> networkViewMap;
	private final Map<Long, RenderingEngine<CyNetwork>> presentationMap;

	private final Map<JInternalFrame, Long> componentMap;

	protected final int MINIMUM_WIN_WIDTH = 200;
	protected final int MINIMUM_WIN_HEIGHT = 200;

	private final CyNetworkManager netmgr;

	private Long currentViewId;

	private Properties props;

	private CyHelpBroker help;
	
	// Supports multiple presentations
	private Map<String, RenderingEngineFactory<CyNetwork>> factories;
	
	private RenderingEngineFactory<CyNetwork> currentRenderingEngineFactory;
	
	//TODO: discuss the name and key of props.
	private static final String ID = "id";
	
	//TODO: for now, use this as default.  But in the future, we should provide UI to select presentation.
	private static final String DEFAULT_PRESENTATION = "ding";

	private Map<CyNetwork,RowSetMicroListener> nameListeners;
	private CyServiceRegistrar registrar;
	
	
	/**
	 * Creates a new NetworkViewManager object.
	 * 
	 * @param desktop
	 *            DOCUMENT ME!
	 */
	public NetworkViewManager(CyNetworkManager netmgr, Properties props,
			CyHelpBroker help, CyServiceRegistrar registrar) {
		this.factories = new HashMap<String, RenderingEngineFactory<CyNetwork>>();
		
		this.netmgr = netmgr;
		this.props = props;
		this.registrar = registrar;
		desktopPane = new JDesktopPane();

		// add Help hooks
		help.getHelpBroker().enableHelp(desktopPane, "network-view-manager",
				null);

		networkViewMap = new HashMap<Long, JInternalFrame>();
		presentationMap = new HashMap<Long, RenderingEngine<CyNetwork>>();
		componentMap = new HashMap<JInternalFrame, Long>();
		currentViewId = null;

		nameListeners = new HashMap<CyNetwork,RowSetMicroListener>();
	}
	
	
	/**
	 * Dynamically adding rendering engine factory. 
	 * 
	 * @param factory
	 * @param props
	 */
	public void addPresentationFactory(RenderingEngineFactory<CyNetwork> factory, Map props) {
		logger.info("Adding New Rendering Engine Factory...");
		
		Object rendererID = props.get(ID);
		if(rendererID == null)
			throw new IllegalArgumentException("Renderer ID is null.");
		
		factories.put(rendererID.toString(), factory);
		if(currentRenderingEngineFactory == null && rendererID.equals(DEFAULT_PRESENTATION)) {
			currentRenderingEngineFactory = factory;
			logger.info(rendererID + " is registered as default rendering engine.");
		}
		
		logger.info("New Rendering Engine is Available: " + rendererID);
	}
	
	
	public void removePresentationFactory(RenderingEngineFactory<CyNetwork> factory, Map props) {
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
		return networkViewMap.get(view.getModel().getSUID());
	}

	
	/**
	 * DOCUMENT ME!
	 * 
	 * @param network
	 *            DOCUMENT ME!
	 */
	private void updateNetworkTitle(CyNetwork network) {
		JInternalFrame frame = networkViewMap.get(network.getSUID());

		if ( frame != null ) {
			frame.setTitle(network.attrs().get("name", String.class));
			frame.repaint();
		}
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
		logger.info("Attempting to set current network view model: View Model ID = " + e.getNetworkView().getSUID());
		setFocus(e.getNetworkView().getModel().getSUID());
	}
	

	public void handleEvent(SetCurrentNetworkEvent e) {
		logger.info("Attempting to set current network model: Model ID = " + e.getNetwork().getSUID());
		setFocus(e.getNetwork().getSUID());		
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
				logger.error("NetworkViewManager: Couldn't unset focus for internal frame.", pve);
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
		logger.info("Network view destroyed: View ID = " + nvde.getNetworkView());
		removeView(nvde.getNetworkView());
	}

	
	/**
	 * Adding new network view model to this manager.
	 * Then, create default presentation.
	 */
	public void handleEvent(final NetworkViewAddedEvent nvae) {
		logger.info("Adding view to manager: NetworkViewManager: View ID = "
				+ nvae.getNetworkView().getSUID());
		createContainer(nvae.getNetworkView());
	}

	
	protected void removeView(CyNetworkView view) {
		try {
			networkViewMap.get(view.getModel().getSUID()).dispose();
		} catch (Exception e) {
			System.err.println("Network View unable to be killed");
		}

		networkViewMap.remove(view.getModel().getSUID());
		RowSetMicroListener rsml = nameListeners.remove(view.getModel());
		if ( rsml != null )
			registrar.unregisterService(rsml, RowSetMicroListener.class);
	}

	/**
	 * Create a visualization container and add presentation to it.
	 * 
	 */
	protected void createContainer(final CyNetworkView view) {
		if (networkViewMap.containsKey(view.getModel().getSUID())) {
			// already contains
			return;
		}

		// create a new InternalFrame and put the CyNetworkView Component into
		// it
		final JInternalFrame iframe = new JInternalFrame(view
				.getVisualProperty(NETWORK_TITLE), true, true, true, true);
		iframe.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				netmgr.destroyNetworkView(view);
			}
		});
		desktopPane.add(iframe);

		logger.info("Rendering view model: " + view.getSUID());
		this.presentationMap.put(view.getModel().getSUID(), this.currentRenderingEngineFactory.render(iframe, view));

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

		networkViewMap.put(view.getModel().getSUID(), iframe);
		componentMap.put(iframe, view.getModel().getSUID());

		Long sourceNetwork = view.getModel().getSUID();
		//netmgr.setCurrentNetworkView(sourceNetwork);
		netmgr.setCurrentRenderingEngine(presentationMap.get(sourceNetwork));

		updateNetworkTitle( view.getModel() );	

		RowSetMicroListener rsml = new AbstractNetworkNameListener( view.getModel() ) {
			public void updateNetworkName(CyNetwork net, String name) {
                updateNetworkTitle(net);
			}
		};
		
		registrar.registerService( rsml, RowSetMicroListener.class, new Properties() );
		nameListeners.put(view.getModel(), rsml );
	}
}
