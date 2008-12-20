package org.cytoscape.presentation.internal;

import org.cytoscape.presentation.NetworkPresentationFactory;
import org.cytoscape.presentation.TextPresentation;
import org.cytoscape.presentation.SwingPresentation;
import org.cytoscape.presentation.NetworkPresentationFactory;
import org.cytoscape.viewmodel.CyNetworkView;

import org.osgi.framework.BundleContext;

/**
 * 
 */
public class NetworkPresentationFactoryImpl implements NetworkPresentationFactory {
        private BundleContext bundleContext;

	/**
	 * For setter injection (hmm. whats that?)
	 */
	public NetworkPresentationFactoryImpl() {
	}
    public void setBundleContext(BundleContext bundleContext) {
	this.bundleContext = bundleContext;
    }
    public BundleContext getBundleContext() {
	return bundleContext;
    }


	/**
	 * Creates a new CyNetworkFactoryImpl object.
	 *
	 * @param h  DOCUMENT ME!
	 */
    public NetworkPresentationFactoryImpl(final BundleContext bundleContext) {
		if (bundleContext == null)
			throw new NullPointerException("bundleContext is null");
		this.bundleContext = bundleContext;
	}

    public TextPresentation getTextPresentationFor(CyNetworkView view){
	return new AdjMatrixTextRenderer(view, bundleContext);
    }
    public SwingPresentation getSwingPresentationFor(CyNetworkView view){
	throw new RuntimeException("not implemented");
    }
}
