package org.cytoscape.io.write;


/**
 * A specialization of {@link CyWriterFactory} that allows a {@link org.cytoscape.model.CyTable} to
 * be specified and written.
 * @CyAPI.Spi.Interface
 */
public interface CyTableWriterFactory<C extends CyTableWriterContext> extends CyWriterFactory<C> {
}
