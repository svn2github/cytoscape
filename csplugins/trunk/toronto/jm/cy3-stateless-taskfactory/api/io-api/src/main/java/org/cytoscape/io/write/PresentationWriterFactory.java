package org.cytoscape.io.write;


import org.cytoscape.view.presentation.RenderingEngine;

/**
 * A specialization of {@link CyWriterFactory} that allows a View 
 * rendered by the specified {@link RenderingEngine} to
 * be specified and written.
 * @CyAPI.Spi.Interface
 */
public interface PresentationWriterFactory<C extends PresentationWriterContext> extends CyWriterFactory<C> {
}
