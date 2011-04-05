
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

package org.cytoscape.search.internal;

import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.search.internal.ui.EnhancedSearchPanel;
import org.cytoscape.work.swing.GUITaskManager;

public class EnhancedSearchPlugin implements SetCurrentNetworkViewListener, NetworkAboutToBeDestroyedListener, 
					SessionLoadedListener
{
	private CySwingApplication desktopApp;
	private CyApplicationManager netmgr;
	private CyTableManager tableMgr;
	private GUITaskManager taskMgr;
	private EnhancedSearchManager searchMgr;
	private static boolean initialized = false;

	public EnhancedSearchPlugin(CySwingApplication desktopApp, CyApplicationManager netmgr, 
			CyTableManager tableMgr, GUITaskManager taskMgr) {

		this.desktopApp = desktopApp;
		this.netmgr = netmgr;
		this.tableMgr = tableMgr;
		this.taskMgr = taskMgr;
	}

	private void init(){		
		searchMgr = new EnhancedSearchManager();
		// Add a text-field and a search button on tool-bar
		EnhancedSearchPanel searchPnl = new EnhancedSearchPanel(netmgr, tableMgr, searchMgr, taskMgr);
		searchPnl.setVisible(true);
		desktopApp.getJToolBar().add(searchPnl);

		initialized = true;
	}

	@Override
	public void handleEvent(SetCurrentNetworkViewEvent e) {
		// Show the Enhanced Search text-field only when a network view is presented in screen
		if (!initialized){
			init();	
		}
	}
	
	@Override
	public void handleEvent(SessionLoadedEvent e) {
		//System.out.println("\n\tEnhanceSearch: Got event --- SessionLoadedEvent");
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		//CyNetwork network = e.getNetwork();
		//System.out.println("\n\tEnhanceSearch: Got event --- NetworkAboutToBeDestroyedEvent");	
	}
}
