package csplugins.mcode.internal.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import csplugins.mcode.internal.ui.MCODEMainPanel;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

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
 ** User: Gary Bader
 ** Date: Feb 6, 2004
 ** Time: 4:54:53 PM
 ** Description: Action to change the current parameters
 **/

/**
 * Action to display the main panel where scope is chosen and scoring and
 * finding parameters are modified
 */
public class MCODEMainPanelAction implements ActionListener {
    MCODEMainPanel mainPanel;

    public MCODEMainPanelAction() {
		mainPanel = new MCODEMainPanel();
    }

    /**
     * This method is called when the user wants to start MCODE.
     *
     * @param event Menu Item Selected.
     */
    public void actionPerformed(ActionEvent event) {
    		//display MCODEMainPanel in left cytopanel
    		final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
    		
        int index = cytoPanel.indexOfComponent(mainPanel);
		
		if ( index <= 0 ) {
			cytoPanel.add("MCODE PlugIn", mainPanel);
	       	index = cytoPanel.indexOfComponent(mainPanel);
		}

		// if adding the cytopanel somehow fails...
		if ( index <= 0 ) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
			 "Problem creating CytoPanel!  MCODE is broken.", 
			 "MCODE Error", JOptionPane.ERROR_MESSAGE); 	
			return;
		} 

        cytoPanel.setSelectedIndex(index);
        cytoPanel.setState(CytoPanelState.DOCK);
    }
}
