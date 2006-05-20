/*
 * Created on Jun 13, 2005
 *
 * an interface for responding to PhoebeCanvasDropEvents.  
 */
package ding.view;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import giny.view.NodeView;



import javax.swing.JPopupMenu;

import java.awt.Point;

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
	 * @param event the PhoebeCanvasDropEvent
	 */
	void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu);

}
