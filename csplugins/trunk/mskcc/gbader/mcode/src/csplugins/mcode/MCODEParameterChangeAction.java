package csplugins.mcode;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

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
 * Action to change the current parameters
 */
public class MCODEParameterChangeAction implements ActionListener {
    /**
     * This method is called when the user wants to change the MCODE parameters.
     *
     * @param event Menu Item Selected.
     */
    public void actionPerformed(ActionEvent event) {
        //display parameter panel in left cytopanel
        CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CytoPanel cytoPanel = desktop.getCytoPanel (SwingConstants.WEST);
        //MCODEParameterChangePanel paramChangePanel = new MCODEParameterChangePanel();
        MCODEMainPanel paramChangePanel = new MCODEMainPanel();
        //Incase we choose to have an icon for the MCODE panel at some point
        URL iconURL = this.getClass().getResource("resources/icon_note_large.gif");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            String tip = "MCODE Scoring/Complex-Finding Parameters";
            cytoPanel.add("MCODE PlugIn", icon, paramChangePanel, tip);
        } else {
            cytoPanel.add("MCODE PlugIn", paramChangePanel);
        }
        int index = cytoPanel.indexOfComponent(paramChangePanel);
        cytoPanel.setSelectedIndex(index);
        cytoPanel.setState(CytoPanelState.DOCK);
    }
}
