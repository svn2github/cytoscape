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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

// Cytoscape imports
import cytoscape.*;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.URLUtil;

// SFLDloader imports
import SFLDLoader.ui.SFLDQueryDialog;
import SFLDLoader.model.Superfamily;
import SFLDLoader.model.Family;
import SFLDLoader.model.Subgroup;


/**
 * The SFLDLoader class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class SFLDLoader extends CytoscapePlugin {
	final static double VERSION = 1.0;
	static JDialog sfldQueryDialog = null;
	static List superFamilies = null;
	public final String URLBase = "http://sfld.rbvi.ucsf.edu/cgi-bin/SFLDvm.py";
	// public final String URLBase = "http://sfldtest.rbvi.ucsf.edu/cgi-bin/SFLDvm.py";
	static JMenuItem loadMenu = null;
	private CyLogger logger = null;

  /**
   * Create our action and add it to the plugins menu
   */
  public SFLDLoader() {
		JMenu menu = new JMenu("SFLD Loader");
		loadMenu = new JMenuItem("Initializing...");
		loadMenu.addActionListener(new SFLDLoaderMenuListener());
		loadMenu.setEnabled(false);
		menu.add(loadMenu);

		logger = CyLogger.getLogger(SFLDLoader.class);

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

		// Load the initial data from the SFLD
		SFLDEnumerator enumerator = new SFLDEnumerator();
		enumerator.start();

  }

	/**
	 * The SFLDLoaderMenuListener launches the loader dialog
	 */
	public class SFLDLoaderMenuListener implements ActionListener {

		/**
		 * Create the menu listener
		 *
		 */
		SFLDLoaderMenuListener() {
		}

		/**
		 * Process the selected menu
		 *
		 * @param e the MenuEvent for the selected menu
		 */
		public void actionPerformed (ActionEvent e)
		{
			// See if the dialog is already created
			if (sfldQueryDialog != null) {
				// Yes, pop it up
				sfldQueryDialog.setVisible(true);
				return;
			}
			// No, create it
			sfldQueryDialog = new SFLDQueryDialog(superFamilies, URLBase, logger);
			sfldQueryDialog.pack();
			sfldQueryDialog.setLocationRelativeTo(Cytoscape.getDesktop());
			sfldQueryDialog.setVisible(true);
		}
	}

	/**
	 * The SFLDEnumerator is a thread that loads the initial data set, parse
	 * the XML and fill out the model information
	 */
	public class SFLDEnumerator extends Thread {

		public SFLDEnumerator() {
			// Why do we need to do this?
			// Superfamily superFoo = new Superfamily("none",-1);
			// Subgroup subFoo = new Subgroup("none",-1);
			// Family familyFoo = new Family("none",-1);
		}

		public void run() {
			logger.info("Initializing SFLD enumeration");

			DocumentBuilder builder = null;
			Document enumeration = null;
			InputStream input = null;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
			} catch (Exception e) {
				logger.error("Unable to create a new document: "+e.getMessage());
				return;
			}
			try {
				input = URLUtil.getBasicInputStream(new URL(URLBase+"?query=enumerate&level=all&id=0"));
				enumeration = builder.parse(input);
				// enumeration = builder.parse("file:///Users/scooter/Desktop/SFLDvm.py.xml");
			} catch (Exception e) {
				logger.error("Unable to enumerate SFLD database: "+e.getMessage());
				return;
			}
			NodeList superNodes = enumeration.getElementsByTagName("superfamily");
			superFamilies = new ArrayList(superNodes.getLength());
			for (int i = 0; i < superNodes.getLength(); i++) {
				superFamilies.add(new Superfamily(superNodes.item(i)));
			}
			// Sort the list (and descendents)
			Object[] sortable = superFamilies.toArray();
			Arrays.sort(sortable);
			superFamilies = Arrays.asList(sortable);
			logger.info("SFLD enumeration complete");
			loadMenu.setText("Browse SFLD...");
			loadMenu.setEnabled(true);
		}
	}
}
