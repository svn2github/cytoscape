
package org.cytoscape.dnd;


import java.awt.Point;
import java.awt.datatransfer.Transferable;

import org.cytoscape.task.NodeViewTaskFactory;

/**
 * An extension of TaskFactory that provides support for
 * tasks to deal with drag and drop.
 */
public interface DropNodeViewTaskFactory extends NodeViewTaskFactory {

	/**
	 * Sets the drop information for a TaskFactory. 
	 * @param t The transferable object that was dropped.
	 * @param pt The point at which the object was dropped.
	 */
	public void setDropInformation (Transferable t, Point pt);
}
