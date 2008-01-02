/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
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
package clusterMaker;

// System imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// giny imports
import giny.view.NodeView;
import ding.view.*;

// Cytoscape imports
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

// clusterMaker imports

/**
 * The ClusterMaker class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterMaker extends CytoscapePlugin {
	static final double VERSION = 0.1;

  /**
   * Create our action and add it to the plugins menu
   */
  public ClusterMaker() {

		JMenu menu = new JMenu("Cluster Tools");
		menu.addMenuListener(new ClusterMakerMenuListener());

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);
		System.out.println("clusterMaker "+VERSION+" initialized");

  }

	/**
	 * The StructureMakerMenuListener provides the interface to the clusterMaker
	 * plugin menu.
	 */
	public class ClusterMakerMenuListener implements MenuListener {
		private ClusterMakerCommandListener staticHandle;

		/**
		 * Create the StructureMaker menu listener
		 *
		 */
		ClusterMakerMenuListener() {
			this.staticHandle = new ClusterMakerCommandListener(0, null);
		}

	  public void menuCanceled (MenuEvent e) {};
		public void menuDeselected (MenuEvent e) {};

		/**
		 * Process the selected menu
		 *
		 * @param e the MenuEvent for the selected menu
		 */
		public void menuSelected (MenuEvent e)
		{
			JMenu m = (JMenu)e.getSource();
			// Clear the menu
			Component[] subMenus = m.getMenuComponents();
			for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }

			// Add our menu items
			{
			}
		}


		/**
		 * Add a submenu item to an existing menu
		 *
		 * @param menu the JMenu to add the new submenu to
		 * @param label the label for the submenu
		 * @param command the command to execute when selected
		 * @param userData data associated with the menu
		 */
		private void addSubMenu(JMenu menu, String label, int command, Object userData) {
			ClusterMakerCommandListener l = new ClusterMakerCommandListener(command, userData);
			JMenuItem item = new JMenuItem(label);
			item.addActionListener(l);
		  menu.add(item);
		}
	}
	
  /**
   * This class gets attached to the menu item.
   */
  static class ClusterMakerCommandListener implements ActionListener {
  	private static final long serialVersionUID = 1;
		private int command;
		private Object userData = null; // Either a Structure or an ArrayList

		ClusterMakerCommandListener(int command, Object userData) {
			this.command = command;
			this.userData = userData;
		}

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
			String label = ae.getActionCommand();
		}
  }
}
