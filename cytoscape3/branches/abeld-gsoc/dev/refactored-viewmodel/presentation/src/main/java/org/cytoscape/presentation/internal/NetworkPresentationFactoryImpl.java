package org.cytoscape.presentation.internal;

import org.cytoscape.presentation.NetworkPresentationFactory;
import org.cytoscape.viewmodel.CyNetworkView;

/**
 * 
 */
public class NetworkPresentationFactoryImpl implements NetworkPresentationFactory {
    TextPresentation getTextPresentationFor(CyNetworkView view){
	return new AdjMatrixTextRenderer(view);
    }
    SwingPresentation getSwingPresentationFor(CyNetworkView view){
	throw new Exception("not implemented");
    }
}
