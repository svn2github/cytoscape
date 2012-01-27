
package org.cytoscape.io.read;

import org.cytoscape.io.CyFileFilterProvider;
import org.cytoscape.work.TaskFactory;

/**
 * A super interface that allows the input stream to be set for reader
 * task factories.
 * @CyAPI.Spi.Interface
 */
public interface InputStreamTaskFactory<C extends InputStreamTaskContext> extends TaskFactory<C>, CyFileFilterProvider {
}
