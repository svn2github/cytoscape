/*
 * Created on Jun 13, 2005
 *
 * an interface for responding to PhoebeCanvasDropEvents.
 */
package org.cytoscape.view;

import javax.swing.*;
import java.util.EventListener;


/**
 * @author Allan Kuchinsky
 *
 */

/**
 * an interface for responding to PhoebeCanvasDropEvents.
 */
public interface NodeContextMenuListener extends EventListener {
	/**
	 * method for responding to a drop
	 */
	void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu);
}
