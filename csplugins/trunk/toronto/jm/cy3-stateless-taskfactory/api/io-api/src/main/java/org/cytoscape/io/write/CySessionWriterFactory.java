package org.cytoscape.io.write;



/**
 * A specialization of {@link CyWriterFactory} that allows a {@link org.cytoscape.session.CySession} to
 * be specified and written.
 * @CyAPI.Spi.Interface
 */
public interface CySessionWriterFactory extends CyWriterFactory<CySessionWriterContext> {
}
