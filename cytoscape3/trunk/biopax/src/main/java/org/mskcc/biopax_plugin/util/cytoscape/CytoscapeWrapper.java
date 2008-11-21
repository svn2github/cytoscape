// $Id: CytoscapeWrapper.java,v 1.7 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.util.cytoscape;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import org.mskcc.biopax_plugin.view.BioPaxContainer;
import org.mskcc.biopax_plugin.view.BioPaxDetailsPanel;

import javax.swing.*;
import java.net.URL;


/**
 * Wrapper Class for Cytoscape 2.1 and 2.2 Specific Code.
 *
 * @author Ethan Cerami.
 */
public class CytoscapeWrapper {
	private static boolean bioPaxUIInitialized = false;

	/**
	 * Sets the Status Bar Message.
	 * Feature only available in Cytoscape 2.2.
	 *
	 * @param msg User msg.
	 */
	public static void setStatusBarMsg(String msg) {
		//cytoscape 2.1 code
		//  BioPaxDetailsPanel bpPanel = BioPaxDetailsPanel.getInstance();
		//  bpPanel.setStatusBarMsg(msg);

		// cytoscape 2.2 code
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		desktop.setStatusBarMsg(msg);
	}

	/**
	 * Clears the Status Bar Message.
	 * Feature only available in Cytoscape 2.2.
	 */
	public static void clearStatusBar() {
		// cytoscape 2.1 code
		// BioPaxDetailsPanel bpPanel = BioPaxDetailsPanel.getInstance();
		// bpPanel.clearStatusBarMsg();

		// cytoscape 2.2 code
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		desktop.clearStatusBar();
	}

	/**
	 * Activates the BioPaxPlugIn Tab in a Cytopanel.
	 * Feature only available in Cytoscape 2.2.
	 *
	 * @param bpContainer BioPaxContainer Object.
	 */
	public static void activateBioPaxPlugInTab(BioPaxContainer bpContainer) {
		// cytoscape 2.2 code
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel(BioPaxContainer.CYTO_PANEL_LOCATION);
		int index = cytoPanel.indexOfComponent(bpContainer);
		cytoPanel.setSelectedIndex(index);
	}

	/**
	 * Initializes the BioPax PlugIn UI.
	 */
	public static void initBioPaxPlugInUI() {
		if (!bioPaxUIInitialized) {
			//  cytoscape 2.1 code
			//  BioPaxDetailsWindow bpWindow =
			//      BioPaxDetailsWindow.getInstance();

			//  cytoscape 2.2 code
			final BioPaxContainer bpContainer = BioPaxContainer.getInstance();
			final CytoscapeDesktop desktop = Cytoscape.getDesktop();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    CytoPanel cytoPanel = desktop.getCytoPanel(BioPaxContainer.CYTO_PANEL_LOCATION);
                    URL url = BioPaxDetailsPanel.class.getResource("/images/read_obj.gif");
                    Icon icon = new ImageIcon(url);
                    cytoPanel.add("Node Details", icon, bpContainer, "Node Details");
                    cytoPanel.setState(CytoPanelState.DOCK);
                    bioPaxUIInitialized = true;
                }
            });
        }
    }
}
