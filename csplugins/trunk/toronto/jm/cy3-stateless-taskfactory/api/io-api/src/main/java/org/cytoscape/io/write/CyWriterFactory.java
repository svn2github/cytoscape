package org.cytoscape.io.write;

import org.cytoscape.io.CyFileFilterProvider;
import org.cytoscape.work.TaskFactory;

/**
 * CyWriterFactory defines the base methods for specifying output and
 * for generating a Task to write the actual output.  Instantiations
 * of CyWriterFactories are meant to be singleton objects registered
 * as OSGi services.
 * @CyAPI.Spi.Interface
 */
public interface CyWriterFactory<C extends CyWriterContext> extends CyFileFilterProvider, TaskFactory<C> {
	CyWriter createWriterTask(C context);
}
