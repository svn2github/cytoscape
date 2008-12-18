package org.cytoscape.presentation;

import org.cytoscape.viewmodel.CyNetworkView;

/**
 * 
 */
public interface NetworkPresentationFactory {
    TextPresentation getTextPresentationFor(CyNetworkView view);
    SwingPresentation getSwingPresentationFor(CyNetworkView view);
}
