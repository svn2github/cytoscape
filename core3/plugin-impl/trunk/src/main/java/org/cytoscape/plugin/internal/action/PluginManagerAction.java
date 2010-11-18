/*
 File: PreferenceAction.java

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

//-------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//-------------------------------------------------------------------------
package org.cytoscape.plugin.internal.action;

import java.awt.event.ActionEvent;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoscapeVersion;
import org.cytoscape.plugin.internal.ui.PluginManageDialog;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.work.swing.GUITaskManager;
import java.util.Properties;


/**
 *
 */
public class PluginManagerAction extends AbstractCyAction {
	private final static long serialVersionUID = 12022346993206L;
	private CySwingApplication desktop;
	private BookmarksUtil bookmarksUtil;
	private Bookmarks theBookmarks;
	private GUITaskManager guiTaskManagerServiceRef;
	private CyProperty cytoscapePropertiesServiceRef;
	
	/**
	 * Creates a new BookmarkAction object.
	 */
	public PluginManagerAction(CySwingApplication desktop, CyApplicationManager appMgr, CytoscapeVersion version,
			CyProperty<Bookmarks> bookmarksProp, BookmarksUtil bookmarksUtil, GUITaskManager guiTaskManagerServiceRef
			, CyProperty cytoscapePropertiesServiceRef) {
				
		super("Plugin manager", appMgr);

		this.desktop = desktop;

		this.theBookmarks = bookmarksProp.getProperties();	
		this.bookmarksUtil = bookmarksUtil;
		this.guiTaskManagerServiceRef = guiTaskManagerServiceRef;

		// Note: We need pass cyConfigDir = ".cytoscape" and cyConfigVerDir to PluginManager.java
		this.cytoscapePropertiesServiceRef = cytoscapePropertiesServiceRef;
				
		// initialize version
		org.cytoscape.plugin.internal.util.CytoscapeVersion.version = version.getVersion();

		setPreferredMenu("Plugins");
		
		
		// For debug only
		//PluginManageDialog dlg = new PluginManageDialog(desktop.getJFrame(), theBookmarks, this.bookmarksUtil, 
		//		this.guiTaskManagerServiceRef);
		//dlg.setVisible(true);
	}


	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		PluginManageDialog dlg = new PluginManageDialog(desktop.getJFrame(), theBookmarks, this.bookmarksUtil, 
				this.guiTaskManagerServiceRef);
		dlg.setVisible(true);
	}
}
