package ding.view;

import giny.view.EdgeView;

import java.awt.*;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;

import java.util.*;

import javax.swing.JPopupMenu;


/**
 * an interface for responding to PhoebeCanvasDropEvents.
 */
public interface EdgeContextMenuListener extends EventListener {
    /**
     * method for responding to a drop
     */
    void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu menu);
}
