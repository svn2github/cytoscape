
package org.cytoscape.dnd;


import org.cytoscape.task.NetworkViewTaskFactory;

/**
 * An extension of TaskFactory that provides support for
 * tasks to deal with drag and drop.
 * @CyAPI.Spi.Interface
 */
public interface DropNetworkViewTaskFactory<C extends DropNetworkViewTaskContext> extends NetworkViewTaskFactory<C> {
}
