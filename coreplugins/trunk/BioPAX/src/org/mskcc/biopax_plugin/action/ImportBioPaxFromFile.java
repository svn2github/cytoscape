// $Id: ImportBioPaxFromFile.java,v 1.7 2006/06/15 22:06:02 grossb Exp $
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
package org.mskcc.biopax_plugin.action;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import org.mskcc.biopax_plugin.task.ImportBioPax;
import org.mskcc.biopax_plugin.util.cytoscape.CytoscapeWrapper;
import org.mskcc.biopax_plugin.util.cytoscape.NetworkListener;
import org.mskcc.biopax_plugin.view.BioPaxContainer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Imports a BioPAX Document from a File.
 *
 * @author Ethan Cerami.
 */
public class ImportBioPaxFromFile implements ActionListener {

    /**
     * User Has Selected Menu Item to Retrieve BioPAX from a File.
     *
     * @param e ActionEvent Object.
     */
    public void actionPerformed(ActionEvent e) {
        // Create FileFilters
        CyFileFilter bioPaxFilter = new CyFileFilter();

        // Add accepted File Extensions
        bioPaxFilter.addExtension("xml");
        bioPaxFilter.addExtension("owl");
        bioPaxFilter.setDescription("BioPAX files");

        // Get the file name
        File file = FileUtil.getFile("Load BioPAX File",
                FileUtil.LOAD, new CyFileFilter[]{bioPaxFilter});

        if (file != null) {
            ImportBioPax task = new ImportBioPax(file);
            executeTask(task);
        }
    }

    /**
     * Executes the Import BioPAX Task.
     *
     * @param task ImportBioPaxTask Object.
     */
    public static void executeTask(ImportBioPax task) {
        //  Configure JTask
        JTaskConfig config = new JTaskConfig();
        config.setAutoDispose(false);
        config.displayStatus(true);
        config.displayTimeElapsed(true);
        config.displayCloseButton(true);
        config.setOwner(Cytoscape.getDesktop());

        //  Execute Task via TaskManager
        //  This automatically pops-open a JTask Dialog Box.
        //  This method will block until the JTask Dialog Box
        //  is disposed.
        boolean success = TaskManager.executeTask(task, config);

        //  If successful, set up UI
        if (success) {
            if (task.getCyNetwork() != null) {
                //  Initialize the UI Elements
                CytoscapeWrapper.initBioPaxPlugInUI();

                //  Register Network with Network Listener
                CyNetwork cyNetwork = task.getCyNetwork();
                BioPaxContainer bpContainer = BioPaxContainer.getInstance();
                NetworkListener networkListener = bpContainer.getNetworkListener();
                networkListener.registerNetwork(cyNetwork);
            }
        }
    }
}
