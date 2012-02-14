package org.cytoscape.io.write;


/**
 * A specialization of {@link CyWriterFactory} that allows a property Object to
 * be specified and written. See {@link org.cytoscape.property.CyProperty} for details on the type of Object.
 * @CyAPI.Spi.Interface
 */
public interface CyPropertyWriterFactory<C extends CyPropertyWriterContext> extends CyWriterFactory<C> {
}
