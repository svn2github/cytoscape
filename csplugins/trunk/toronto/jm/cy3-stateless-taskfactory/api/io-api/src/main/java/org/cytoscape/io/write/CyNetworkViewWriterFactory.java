package org.cytoscape.io.write;

import org.cytoscape.view.model.CyNetworkView;

/**
 * A specialization of {@link CyWriterFactory} that allows a 
 * {@link CyNetworkView} to be specified and written.
 * @CyAPI.Spi.Interface
 */
public interface CyNetworkViewWriterFactory<C extends CyNetworkViewWriterContext> extends CyWriterFactory<C> {
}
