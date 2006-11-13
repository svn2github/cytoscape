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
package csplugins.dataviewer.task;

import csplugins.dataviewer.mapper.MapStateInformationToGraph;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import org.mskcc.dataservices.bio.StateInformation;
import org.mskcc.dataservices.live.state.ReadSoftFromFileOrWeb;
import org.mskcc.dataservices.services.ReadStateInformation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Task to Import GEO SOFT Data.
 *
 * @author Ethan Cerami
 */
public class LoadSoftTask implements Task {
    private URL url;
    private File file;
    private StringBuffer msg = new StringBuffer();
    private static final String TASK_TITLE = "Loading Expression Data File";
    private Component parentFrame;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     *
     * @param url URL location of GEO SOFT File.
     */
    public LoadSoftTask(URL url) {
        this.url = url;
    }

    /**
     * Constructor.
     *
     * @param file File location of GEO SOFT File.
     */
    public LoadSoftTask(File file) {
        this.file = file;
    }

    /**
     * Halts the Task:  Not supported.
     */
    public void halt() {
        //  Not supported.
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor Task Monitor Object.
     * @throws IllegalThreadStateException Illegal State Error.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets Title of Task.
     *
     * @return Title of Task.
     */
    public String getTitle() {
        if (file != null) {
            return TASK_TITLE + ": " + file.getName();
        } else {
            return TASK_TITLE;
        }
    }

    /**
     * Sets Parent Frame.
     *
     * @param parentFrame Parent Frame.
     */
    public void setParentFrame(Component parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Imports SOFT Data.
     */
    public void run() {
        try {
            taskMonitor.setPercentCompleted(-1);
            ReadStateInformation service = new ReadSoftFromFileOrWeb();
            StateInformation stateInfo = null;
            if (file != null) {
                stateInfo = service.getStateInformation(file.toString());
            } else {
                stateInfo = service.getStateInformation(url.toString());
            }
            //  Map StateInformation to Graph
            MapStateInformationToGraph mapper = new MapStateInformationToGraph
                    (stateInfo, Cytoscape.getCurrentNetwork());
            mapper.doMapping();
            setInformationMessage(stateInfo, mapper.getNumMatches());
        } catch (Throwable e) {
            taskMonitor.setException(e, "Could not load expression data file.");
        }
    }

    /**
     * Sets Informational HTML Message.
     *
     * @param stateInfo State Information object.
     */
    private void setInformationMessage(StateInformation stateInfo,
            int numMatches) {
        msg.append("Retrieved Expression Data from:  ");
        if (file != null) {
            msg.append(file.getName());
        } else {
            msg.append(url.toString());
        }

        ArrayList matrices = stateInfo.getMatrices();
        if (matrices.size() > 0) {
            msg.append("\n\nNumber of Entities Read:  " + matrices.size());
        }

        CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
        msg.append("\nNumber of nodes in the current network:  "
                + cyNetwork.getNodeCount());
        msg.append("\nNumber of nodes which now contain expression data:  "
                + numMatches);

        if (numMatches == 0) {
            msg.append("\n\nError!  In order to "
                    + "import expression data into an existing Cytoscape network,\nthe "
                    + "network data file and the expression data file must use "
                    + "common identifiers.\nCheck the README.txt file "
                    + "that accompanies this PlugIn for more details.");
        } else {
            msg.append("\n\nTo view expression data:\n\n1. Select a node.  "
                    + "\n2.  Under the Node Attribute browser, click 'Select Attributes'."
                    + "\n3.  Select one or more experimental conditions.");
        }
        updateUser(msg.toString());
    }

    /**
     * Update User about what just happend.
     *
     * @param msg Message.
     */
    private void updateUser(final String msg) {
        Runnable runnable = new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame, msg, "Expression Data Reader",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
}