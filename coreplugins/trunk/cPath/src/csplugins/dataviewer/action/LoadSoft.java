/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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

import csplugins.dataviewer.task.LoadSoftTask;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

/**
 * Loads GEO SOFT Files from Local File System or from the Web.
 *
 * @author Ethan Cerami
 */
public class LoadSoft extends BaseAction {
    /**
     * Action Command:  Load GEO SOFT from Web.
     */
    public static final String ACTION_IMPORT_SOFT_WEB = "load_soft_web";

    /**
     * Action Command:  Load GEO SOFT from File.
     */
    public static final String ACTION_IMPORT_SOFT_FILE = "load_soft_file";

    /**
     * Receives Menu Selection Event.
     *
     * @param event Action Event
     */
    public void actionPerformed(ActionEvent event) {
        try {
            String command = event.getActionCommand();
            CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
            if (cyNetwork.getNodeCount() > 0) {
                if (command.equals(LoadSoft.ACTION_IMPORT_SOFT_FILE)) {
                    importSoftFile();
                } else if (command.equals
                        (LoadSoft.ACTION_IMPORT_SOFT_WEB)) {
                    importSoftWeb();
                }
            } else {
                String msg =
                        "This PlugIn imports expression data into "
                                + "an existing Cytoscape Network.\n"
                                + "Please load a Cytoscape network first, "
                                + "and then try again.";
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), msg,
                        "Please Load Cytoscape Network",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports a GEO SOFT Document from the Web.
     */
    private void importSoftWeb() {
        URL url = this.getURL("Enter URL for GEO SOFT Document:  ");
        if (url != null) {
            LoadSoftTask task = new LoadSoftTask(url);
            startTask(task);
        }
    }

    /**
     * Imports a GEO SOFT Document from the Local File System.
     */
    private void importSoftFile() {
        String xml[] = new String[]{"soft"};
        String fileString = this.getFile(xml, "SOFT Files (*.soft)");
        if (fileString != null) {
            File file = new File(fileString);
            LoadSoftTask task = new LoadSoftTask(file);
            startTask(task);
        }
    }

    /**
     * Starts Task in New Thread.
     */
    private void startTask(LoadSoftTask task) {
        //  Configure JTask
        JTaskConfig config = new JTaskConfig();
        config.setOwner(Cytoscape.getDesktop());
        config.setAutoDispose(true);

        //  Execute Task via TaskManager
        //  This automatically pops-open a JTask Dialog Box.
        //  This method will block until the JTask Dialog Box is disposed.
        boolean success = TaskManager.executeTask(task, config);
    }
}