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
package org.cytoscape.coreplugin.cpath.task;

// imports
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.data.ExpressionData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;

/**
 * Task to Load New Expression Data File.
 *
 * This is exact copy of class taken from
 * cytoscape.actions.LoadExpressionMatrixAction.java.
 *
 * @author Cytoscape Dev Team.
 */
public class LoadExpressionDataTask implements Task {
    private TaskMonitor taskMonitor;
    private String fileName;

    /**
     * Constructor.
     * @param fileName File name containing expression data.
     */
    public LoadExpressionDataTask(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Executes the Task.
     */
    public void run() {
        taskMonitor.setStatus("Analyzing Expression Data File...");
        try {
            //  Before passing along to the Expression Data object, test that file
            //  actually exists
            File file = new File (fileName);
            FileReader reader = new FileReader(file);

            //  Read in Expression Data File
            ExpressionData expressionData = new ExpressionData(fileName,
                    taskMonitor);
            Cytoscape.setExpressionData(expressionData);

            //  Copy Expression Data to Attributes
            taskMonitor.setStatus("Mapping Expression Data to "
                    + "Node Attributes...");
            expressionData.copyToAttribs(Cytoscape.getNodeAttributes(), taskMonitor);
            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED,
                    null, null);
            Cytoscape.firePropertyChange(Cytoscape.EXPRESSION_DATA_LOADED,
                    null, expressionData);

            //  We are done;  inform user of expression data details.
            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus(expressionData.getDescription());
        } catch (FileNotFoundException e) {
            taskMonitor.setException(e,
                    "Cannot find file -->  " + fileName);
        } catch (IOException e) {
            taskMonitor.setException(e,
                    "Unable to load expression matrix file.");
        }
    }

    /**
     * Halts the Task:  Not Currently Implemented.
     */
    public void halt() {
        //   Task can not currently be halted.
    }

    /**
     * Sets the Task Monitor.
     * @param taskMonitor Task Monitor Object.
     * @throws IllegalThreadStateException Illegal State Error.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     * @return Task Title.
     */
    public String getTitle() {
        return new String("Loading Expression Data");
    }
}