
package org.cytoscape.dnd;


import org.cytoscape.task.NodeViewTaskFactory;

/**
 * An extension of TaskFactory that provides support for
 * tasks to deal with drag and drop.
 * @CyAPI.Spi.Interface
 */
public interface DropNodeViewTaskFactory<C extends DropNodeViewTaskContext> extends NodeViewTaskFactory<C> {
}
