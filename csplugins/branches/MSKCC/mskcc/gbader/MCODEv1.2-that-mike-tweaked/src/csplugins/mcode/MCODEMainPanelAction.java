package csplugins.mcode;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.visual.VisualMappingManager;

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
 * Action to display the main panel where scope is chosen and scoring and
 * finding parameters are modified
 */
public class MCODEMainPanelAction implements ActionListener {
    boolean opened = false;
    MCODEMainPanel mainPanel;
    VisualMappingManager vmm;
    MCODEVisualStyle MCODEVS;

    public MCODEMainPanelAction() {
        MCODEVS = new MCODEVisualStyle("MCODE");
        vmm = Cytoscape.getVisualMappingManager();
    }

    /**
     * This method is called when the user wants to start MCODE.
     *
     * @param event Menu Item Selected.
     */
    public void actionPerformed(ActionEvent event) {
        //display MCODEMainPanel in left cytopanel
        CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.WEST);

        //First we must make sure that the plugin is not already open
        if (!opened) {
            //if the MCODE visual style has not already been loaded, we load it
            if (!vmm.getCalculatorCatalog().getVisualStyleNames().contains("MCODE")) {
                vmm.getCalculatorCatalog().addVisualStyle(MCODEVS);
            }
            //The style is not actually applied until a result is produced (in MCODEScoreAndFindAction)

            mainPanel = new MCODEMainPanel(this, MCODEVS);
            URL iconURL = MCODEPlugin.class.getResource("resources/logo2.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                String tip = "MCODE Network Scoring/Cluster Finding Parameters";
                cytoPanel.add("", icon, mainPanel, tip);
            } else {
                cytoPanel.add("MCODE PlugIn", mainPanel);
            }
        } else {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "The MCODE PlugIn is already running.");
        }
    
        int index = cytoPanel.indexOfComponent(mainPanel);
        cytoPanel.setSelectedIndex(index);
        cytoPanel.setState(CytoPanelState.DOCK);
        setOpened(true);
    }

    /**
     * Allows the MCODE plugin to limit the number of open instances of the MCODEMainPanel to 1. If the plugin is being
     * closed, then it sets the visual style to default if the MCODE visual style was last used.
     */
    public void setOpened(boolean opened) {
        this.opened = opened;
        if (!isOpened() && vmm.getVisualStyle() == MCODEVS) {
            vmm.setVisualStyle("default");
            //TODO: non functonal code for removing a visual style...vmm.getCalculatorCatalog().removeVisualStyle("MCODE");
            vmm.applyAppearances();
        }
    }

    public boolean isOpened() {
        return opened;
    }
}