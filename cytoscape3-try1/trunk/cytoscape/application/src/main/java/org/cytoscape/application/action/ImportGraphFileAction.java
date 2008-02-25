/*
 File: ImportGraphFileAction.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

// $Revision: 11609 $
// $Date: 2007-09-28 12:23:13 -0700 (Fri, 28 Sep 2007) $
// $Author: scooter $
package org.cytoscape.application.action;



import cytoscape.data.readers.GMLException;

import cytoscape.data.servers.BioDataServer;



import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;



import java.awt.event.ActionEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import javax.xml.bind.JAXBException;

import org.cytoscape.application.dialog.CyMenus;
import org.cytoscape.application.dialog.ImportNetworkDialog;
import org.cytoscape.application.init.CytoscapeInit;
import org.cytoscape.application.util.CyNetworkNaming;
import org.cytoscape.application.util.Cytoscape;
import org.cytoscape.io.network.GMLReader;
import org.cytoscape.io.network.GraphReader;
import org.cytoscape.io.network.ImportHandler;
import org.cytoscape.io.network.InteractionsReader;
import org.cytoscape.io.network.XGMMLReader;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.view.network.CyGraphLOD;
import org.cytoscape.view.network.DingNetworkView;


/**
 * Imports a graph of arbitrary type. The types of graphs allowed are defined by
 * the ImportHandler.
 */
public class ImportGraphFileAction extends CytoscapeAction {
	protected CyMenus windowMenu;

	/**
	 * Constructor.
	 *
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public ImportGraphFileAction(CyMenus windowMenu) {
		super("Network (multiple file types)...");
		setPreferredMenu("File.Import");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_L, ActionEvent.CTRL_MASK);
		this.windowMenu = windowMenu;

		setName("load");
	}

	/**
	 * Constructor.
	 *
	 * @param windowMenu
	 *            WindowMenu Object.
	 * @param label
	 *            boolean label.
	 */
	public ImportGraphFileAction(CyMenus windowMenu, boolean label) {
		super();
		this.windowMenu = windowMenu;
	}

	/**
	 * User-initiated action to load a CyNetwork into Cytoscape. If successfully
	 * loaded, fires a PropertyChange event with
	 * property=Cytoscape.NETWORK_LOADED, old_value=null, and new_value=a three
	 * element Object array containing:
	 * <OL>
	 * <LI>first element = CyNetwork loaded
	 * <LI>second element = URI of the location from which the network was
	 * loaded
	 * <LI>third element = an Integer representing the format in which the
	 * Network was loaded (e.g., Cytoscape.FILE_SIF).
	 * </OL>
	 *
	 * @param e
	 *            ActionEvent Object.
	 */
	public void actionPerformed(ActionEvent e) {
		// open new dialog
		ImportNetworkDialog fd = null;

		try {
			fd = new ImportNetworkDialog(Cytoscape.getDesktop(), true);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		fd.pack();
		fd.setLocationRelativeTo(Cytoscape.getDesktop());
		fd.setVisible(true);

		if (fd.getStatus() == false) {
			return;
		}

		if (fd.isRemote()) {
			String URLstr = fd.getURLStr();
			System.out.println("URL: "+URLstr);
			try {
				LoadNetworkTask.loadURL(new URL(URLstr), false);
			} catch (MalformedURLException e3) {
				JOptionPane.showMessageDialog(fd, "URL error!", "Warning",
			 	                             JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			final File[] files = fd.getFiles();
			boolean skipMessage = false;

			if ((files != null) && (files.length != 0)) {
				if (files.length != 1) {
					skipMessage = true;
				}

				List<String> messages = new ArrayList<String>();
				messages.add("Successfully loaded the following files:");
				messages.add(" ");

				for (int i = 0; i < files.length; i++) {
					if (fd.isRemote() == true) {
						messages.add(fd.getURLStr());
					} else {
						messages.add(files[i].getName());
					}
	
					LoadNetworkTask.loadFile(files[i], skipMessage);
				}
	
				if (files.length != 1) {
					JOptionPane messagePane = new JOptionPane();
					messagePane.setLocation(Cytoscape.getDesktop().getLocationOnScreen());
					messagePane.showMessageDialog(Cytoscape.getDesktop(), messages.toArray(),
					                              "Multiple Network Files Loaded",
					                              JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
}
