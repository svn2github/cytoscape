package org.cytoscape.presentation.internal;

import org.cytoscape.presentation.NetworkPresentationFactory;
import org.cytoscape.presentation.TextPresentation;
import org.cytoscape.presentation.SwingPresentation;
import org.cytoscape.presentation.NetworkPresentationFactory;
import org.cytoscape.viewmodel.CyNetworkView;

/**
 * 
 */
public class NetworkPresentationFactoryImpl implements NetworkPresentationFactory {
    public TextPresentation getTextPresentationFor(CyNetworkView view){
	return new AdjMatrixTextRenderer(view);
    }
    public SwingPresentation getSwingPresentationFor(CyNetworkView view){
	throw new RuntimeException("not implemented");
    }
}
