package ding.view;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import giny.view.EdgeView;

import javax.swing.JPopupMenu;

import java.awt.Point;

/**
 * an interface for responding to PhoebeCanvasDropEvents.  
 */
public interface EdgeContextMenuListener extends EventListener {


	/**
	 * method for responding to a drop
	 * @param event the PhoebeCanvasDropEvent
	 */
	void addEdgeContextMenuItems (EdgeView edgeView, JPopupMenu menu);

}
