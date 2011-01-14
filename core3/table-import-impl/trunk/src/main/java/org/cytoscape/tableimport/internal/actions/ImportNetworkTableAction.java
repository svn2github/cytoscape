
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.tableimport.internal.actions;


import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.tableimport.internal.ui.ImportTextTableDialog;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.swing.GUITaskManager;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.xml.bind.JAXBException;


/**
 *
 */
public class ImportNetworkTableAction extends AbstractCyAction {
	
	private final static long serialVersionUID = 1205939869461298L;
	/**
	 * Creates a new ImportNetworkTableAction object.
	 */
	public ImportNetworkTableAction(CySwingApplication desktop,CyApplicationManager appMgr,
			CyNetworkManager netMgr,
			CyProperty<Bookmarks> bookmarksProp, BookmarksUtil bookmarksUtil,
			GUITaskManager guiTaskManagerServiceRef, CyProperty cytoscapePropertiesServiceRef,
			CyTableManager tblMgr, FileUtil fileUtilService, OpenBrowser openBrowserService) {
		super("Network from Table (Text/MS Excel)...", appMgr);
		setPreferredMenu("File.Import");
		
		//
		CytoscapeServices.desktop = desktop;
		CytoscapeServices.bookmarksUtil = bookmarksUtil;
		CytoscapeServices.cytoscapePropertiesServiceRef= cytoscapePropertiesServiceRef;
		CytoscapeServices.guiTaskManagerServiceRef = guiTaskManagerServiceRef;
		CytoscapeServices.tblMgr =tblMgr;
		CytoscapeServices.theBookmarks = bookmarksProp.getProperties();
		CytoscapeServices.openBrowser = openBrowserService;
		CytoscapeServices.fileUtil = fileUtilService;
		CytoscapeServices.appMgr = appMgr;
		CytoscapeServices.netMgr = netMgr;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ImportTextTableDialog iad;
		
		try {
			iad = new ImportTextTableDialog(CytoscapeServices.desktop.getJFrame(), true, ImportTextTableDialog.NETWORK_IMPORT);
			iad.pack();
			iad.setLocationRelativeTo(CytoscapeServices.desktop.getJFrame());
			iad.setVisible(true);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
