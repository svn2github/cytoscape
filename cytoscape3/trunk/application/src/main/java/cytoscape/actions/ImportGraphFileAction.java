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

package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.dialogs.ImportNetworkDialog;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.layout.CyLayouts;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Imports a graph of arbitrary type. The types of graphs allowed are defined by
 * the ImportHandler.
 */
public class ImportGraphFileAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869779868L;

	private CytoscapeDesktop desktop;
	private CyReaderManager rdmgr;
	private GraphViewFactory gvf;
	private CyLayouts cyLayouts;

	/**
	 * Constructor.
	 */
	public ImportGraphFileAction(CytoscapeDesktop desktop, CyReaderManager rdmgr, GraphViewFactory gvf, CyLayouts cyLayouts ) {
		super("Network (multiple file types)...");
		setPreferredMenu("File.Import");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_L, ActionEvent.CTRL_MASK);

		setName("load");
		this.desktop = desktop;
		this.rdmgr = rdmgr;
		this.gvf = gvf;
		this.cyLayouts = cyLayouts;
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
			fd = new ImportNetworkDialog(desktop, true, rdmgr.getFileFilters());
		} catch (Exception e1) {
			System.out.println("start dialog error");
			e1.printStackTrace();
			System.out.println("end dialog error");
			JOptionPane.showMessageDialog(fd, "Exception: " + e1.getMessage(), "ERROR",
			 	                             JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		fd.pack();
		fd.setLocationRelativeTo(desktop);
		fd.setVisible(true);

		if (fd.getStatus() == false) {
			return;
		}

		if (fd.isRemote()) {
			String URLstr = fd.getURLStr();
			System.out.println("URL: "+URLstr);
			try {
				LoadNetworkTask.loadURL(new URL(URLstr), false, rdmgr, gvf, cyLayouts,desktop);
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
	
					LoadNetworkTask.loadFile(files[i], skipMessage, rdmgr, gvf, cyLayouts,desktop);
				}
	
				if (files.length != 1) {
					JOptionPane messagePane = new JOptionPane();
					messagePane.setLocation(desktop.getLocationOnScreen());
					messagePane.showMessageDialog(desktop, messages.toArray(),
					                              "Multiple Network Files Loaded",
					                              JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
}
