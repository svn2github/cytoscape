/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package structureViz;

// System imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPopupMenu;
import java.util.List;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

// giny imports
import ding.view.DGraphView;
import giny.view.NodeView;
import ding.view.NodeContextMenuListener;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

// structureViz imports
import structureViz.commands.StructureVizCommandHandler;
import structureViz.ui.StructureVizMenuListener;

/**
 * The StructureViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class StructureViz extends CytoscapePlugin 
  implements NodeContextMenuListener, PropertyChangeListener {

	public static final double VERSION = 1.0;
	public static final int NONE = 0;
	public static final int OPEN = 1;
	public static final int CLOSE = 2;
	public static final int ALIGN = 3;
	public static final int EXIT = 4;
	public static final int COMPARE = 5;
	public static final int SALIGN = 6;
	public static final int SELECTRES = 7;
	public static final int FINDMODELS = 8;

	private CyLogger logger = null;

  /**
   * Create our action and add it to the plugins menu
   */
  public StructureViz() {
		logger = CyLogger.getLogger(StructureViz.class);

		try {
			// Set ourselves up to listen for new networks
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
	
			// Add ourselves to the current network context menu
			((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(this);
		} catch (ClassCastException e) {
			logger.error(e.getMessage());
		}
	    
		JMenu menu = new JMenu("Sequence/Structure Tools");
		menu.addMenuListener(new StructureVizMenuListener(null, logger));

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

		// Finally, register our command handler
		new StructureVizCommandHandler("structureviz", logger);

  }

	/**
 	 * Implements the property change listener support
 	 *
 	 * @param evt the event that triggered us
 	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			// Add menu to the context dialog
			((CyNetworkView)evt.getNewValue()).addNodeContextMenuListener(this);
		}
	}

	/**
	 * Implements addNodecontextMenuItems
	 * @param nodeView the nodeView of the node to add our context menu to
	 * @param pmenu the popup menu
	 */
	public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu pmenu) {

		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		JMenu menu = new JMenu("Structure Visualization");
		menu.addMenuListener(new StructureVizMenuListener(nodeView, logger));
		pmenu.add(menu);
	}
}
