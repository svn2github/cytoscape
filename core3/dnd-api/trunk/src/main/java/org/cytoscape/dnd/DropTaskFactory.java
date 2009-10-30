
package org.cytoscape.dnd;


import java.awt.Point;
import java.awt.datatransfer.Transferable;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;

/**
 * APIs in Cytoscape 3.x are defined by their interfaces. Java interfaces
 * are used by OSGi to define services.  Any Java interface can define a
 * service, meaning any class that implements an interface, can be registered
 * as a service.  Using objects as services through their interfaces rather
 * than through their implementation class directly helps ensure that we
 * write modular and extensible code.
 */
public interface DropTaskFactory extends TaskFactory {

	/**
	 * 
	 *t
	 * @param n The network to be analyzed.
	 * @return A collection of "analyzed" nodes.
	 */
	public void setDropInformation (CyNetworkView view, Transferable t, Point pt);
}
