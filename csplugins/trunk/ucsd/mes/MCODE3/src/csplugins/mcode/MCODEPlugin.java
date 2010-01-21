/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * Code written by: Gary Bader
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 ** Date: Jan.20.2003
 ** Description: Cytoscape Plug In that clusters a graph according to the MCODE
 ** algorithm.
 **/
package csplugins.mcode;

import cytoscape.Cytoscape;

import cytoscape.plugin.CytoscapePlugin;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import csplugins.mcode.internal.action.MCODEAboutAction;
import csplugins.mcode.internal.action.MCODEHelpAction;
import csplugins.mcode.internal.action.MCODEMainPanelAction;

/**
 * MCODE Network Clustering Plug In.
 * Clusters a graph.
 * 
 * Refactored by Kei Ono for Group compatibility.
 *
 * @author Gary Bader
 * @author Vuk Pavlovic
 */
public class MCODEPlugin extends CytoscapePlugin {
	
	// Name of the viewer
	public static final String DEFAULT_VIEWER_NAME = "moduleFinderViewer";
	
	/**
	 * Constructor for the MCODE plugin.
	 */
	public MCODEPlugin() {
		//set-up menu options in plugins menu
		JMenu menu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
		JMenuItem item;

		//MCODE submenu
		JMenu submenu = new JMenu("MCODE");

		//MCODE panel
		item = new JMenuItem("Start MCODE");
		item.addActionListener(new MCODEMainPanelAction());
		submenu.add(item);

		//submenu.addSeparator();

		//Help box
		item = new JMenuItem("Help");
		item.addActionListener(new MCODEHelpAction());
		submenu.add(item);

		//About box
		item = new JMenuItem("About");
		item.addActionListener(new MCODEAboutAction());
		submenu.add(item);

		menu.add(submenu);
	}
}
