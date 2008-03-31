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
package clusterViz;

// System imports
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.util.List;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;

// clusterViz imports
import clusterViz.ui.ClusterVizView;

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterViz extends CytoscapePlugin {
	static final double VERSION = 0.1;

	static final int NONE = 1;
	static final int LAUNCH = 1;

	ClusterVizView cvView = null;

  /**
   * Create our action and add it to the plugins menu
   */
  public ClusterViz() {

		JMenu menu = new JMenu("ClusterViz");
		addSubMenu(menu, "Launch clusterViz", LAUNCH, null);
		
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);
		System.out.println("clusterMaker "+VERSION+" initialized");

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
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new ClusterVizCommandListener(command, userData));
	  menu.add(item);
	}

	/**
	 * Inner class to handle commands
	 */
	class ClusterVizCommandListener implements ActionListener {
		int command;
		Object userData;

		public ClusterVizCommandListener(int command, Object userData) {
			this.command = command;
			this.userData = userData;
		}

		public void actionPerformed(ActionEvent e) {
			// Create the dialog
			if (command == LAUNCH) {
				if (cvView == null) {
					cvView = new ClusterVizView();
					cvView.startup();
					cvView.setVisible(true);
				} else {
					cvView.setVisible(true);
				}
			}
		}
	}
}
