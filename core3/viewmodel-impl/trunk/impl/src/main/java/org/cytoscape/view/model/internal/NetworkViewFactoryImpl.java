package org.cytoscape.view.model.internal;

import java.util.Properties;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;

public class NetworkViewFactoryImpl implements CyNetworkViewFactory {

	private final String VIEW_THRESHOLD = "viewThreshold";

	/**
	 * By default, this value will be used as the View Threshold.
	 */
	private static final int DEF_VIEW_THRESHOLD = 3000;

	private final CyEventHelper eventHelper;
	private final CyServiceRegistrar registrar;
	private final Properties props;

	/**
	 * For injection, use this constructor.
	 * 
	 * @param eventHelper
	 */
	public NetworkViewFactoryImpl(final CyEventHelper eventHelper, final CyServiceRegistrar registrar,
			final CyProperty<Properties> prop) {

		if (eventHelper == null)
			throw new NullPointerException("CyEventHelper is null");
		this.eventHelper = eventHelper;

		if (registrar == null)
			throw new NullPointerException("CyServiceRegistrar is null");

		this.registrar = registrar;

		this.props = prop.getProperties();
	}

	@Override
	public CyNetworkView getNetworkView(final CyNetwork network) {
		return getNetworkView(network, true);
	}

	@Override
	public CyNetworkView getNetworkView(final CyNetwork network, final Boolean useThreshold) {

		CyNetworkView view;

		if (!useThreshold) {
			view = new NetworkViewImpl(network, eventHelper);
			registrar.registerAllServices(view, new Properties());
			return view;
		}

		final int viewThreshold = getViewThreshold();
		final int objectCount = network.getEdgeCount() + network.getNodeCount();
		if (viewThreshold < objectCount)
			view = new NullCyNetworkView(network);
		else {
			view = new NetworkViewImpl(network, eventHelper);
			registrar.registerAllServices(view, new Properties());
		}

		return view;
	}

	private int getViewThreshold() {
		final String vts = props.getProperty(VIEW_THRESHOLD);
		int threshold;
		try {
			threshold = Integer.parseInt(vts);
		} catch (Exception e) {
			threshold = DEF_VIEW_THRESHOLD;
		}

		return threshold;
	}
}
