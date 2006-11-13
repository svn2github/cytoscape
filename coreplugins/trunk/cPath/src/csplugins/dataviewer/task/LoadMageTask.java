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
package csplugins.dataviewer.task;

import csplugins.dataviewer.mage.MageData;
import csplugins.dataviewer.mage.MageParser;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;

/**
 * Long-running Task to load a local MAGE-ML File.
 *
 * @author Ethan Cerami.
 */
public class LoadMageTask implements Task {
    private File file;
    private TaskMonitor taskMonitor = null;
    private MageData mageData;

    /**
     * Constructor.
     *
     * @param file File Object.
     */
    public LoadMageTask(File file) {
        this.file = file;
    }

    /**
     * Runs the Task.
     */
    public void run() {
        taskMonitor.setPercentCompleted(-1);
        MageParser parser = new MageParser();
        try {
            taskMonitor.setStatus("Loading MAGE-ML File:  " + file.getName());
            mageData = parser.parseFile(file);
            taskMonitor.setPercentCompleted(100);
        } catch (IOException e) {
            taskMonitor.setException(e, "Failed to load MAGE-ML File");
        } catch (JDOMException e) {
            taskMonitor.setException(e, "Failed to load MAGE-ML File");
        }
    }

    /**
     * Return MAGE-ML Data.
     *
     * @return MAGE_ML Data Object.
     */
    public MageData getMageData() {
        return mageData;
    }

    /**
     * Halts the Task:  Not supported.
     */
    public void halt() {
        //  Not supported
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
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
        return "Loading MAGE-ML File";
    }
}