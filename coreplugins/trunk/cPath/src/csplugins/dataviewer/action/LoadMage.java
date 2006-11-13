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
package csplugins.dataviewer.action;

import csplugins.dataviewer.mage.MageData;
import csplugins.dataviewer.mage.MageParser;
import csplugins.dataviewer.task.LoadMageTask;
import csplugins.dataviewer.ui.MageDialog;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Loads MAGE-ML File from Local File System.
 *
 * @author Ethan Cerami
 */
public class LoadMage extends BaseAction {

    /**
     * User has selected menu item for Importing MAGE-ML Files.
     *
     * @param event ActionEvent Object.
     */
    public void actionPerformed(ActionEvent event) {
        CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
        if (cyNetwork == null || cyNetwork.getNodeCount() == 0) {
            String msg = "This PlugIn imports MAGE-ML data into "
                    + "an existing Cytoscape Network.\n"
                    + "Please load a Cytoscape network first, "
                    + "and then try again.";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), msg,
                    "Please Load Cytoscape Network",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            CyFileFilter filter = new CyFileFilter();
            filter.addExtension("xml");
            filter.setDescription("MAGE-ML Files");

            // get the file name
            final String name;
            name = FileUtil.getFile("Load MAGE-ML File",
                    FileUtil.LOAD, new CyFileFilter[]{filter}).toString();
            if (name != null) {
                MageParser parser = new MageParser();
                File file = new File(name);
                LoadMageTask task = new LoadMageTask(file);

                //  Configure JTask
                JTaskConfig config = new JTaskConfig();
                config.setOwner(Cytoscape.getDesktop());
                config.setAutoDispose(true);

                //  Execute Task via TaskManager
                //  This automatically pops-open a JTask Dialog Box.
                //  This method will block until the JTask Dialog Box is disposed.
                boolean success = TaskManager.executeTask(task, config);
                if (success) {
                    MageData mageData = task.getMageData();
                    MageDialog dialog = new MageDialog(mageData);
                }
            }
        }
    }
}