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
package SFLDLoader;

// System imports
import javax.swing.JOptionPane;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

/**
 * The SFLDLoader class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class SFLDLoader extends CytoscapePlugin {
	final static float VERSION = 0.1;
	static JDialog sfldQueryDialog = null;

  /**
   * Create our action and add it to the plugins menu
   */
  public SFLDLoader() {
		JMenu menu = new JMenu("SFLD Loader");
		JMenuItem loader = new JMenuItem("Load network from SFLD");
		loader.addMenuListener(new SFLDLoaderMenuListener(null));
		menu.add(loader);

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);
		System.out.println("SFLD Loader "+VERSION+" initialized");

  }

	protected JDialog createSFLDQueryDialog() {
	}

	/**
	 * The SFLDLoaderMenuListener launches the loader dialog
	 */
	public class SFLDLoaderMenuListener implements MenuListener {

		/**
		 * Create the menu listener
		 *
		 */
		SFLDLoaderMenuListener() {
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
			// See if the dialog is already created
			if (sfldQueryDialog != null) {
				// Yes, pop it up
				return;
			}
			// No, create it
			sfldQueryDialog = createSFLDQueryDialog();
		}
	}
}
