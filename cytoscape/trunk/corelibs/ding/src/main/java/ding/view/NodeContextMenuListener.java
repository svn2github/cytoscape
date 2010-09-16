/*
 * Created on Jun 13, 2005
 *
 * an interface for responding to PhoebeCanvasDropEvents.
 */
package ding.view;

import giny.view.NodeView;

import java.awt.*;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;

import java.util.*;

import javax.swing.JPopupMenu;


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
