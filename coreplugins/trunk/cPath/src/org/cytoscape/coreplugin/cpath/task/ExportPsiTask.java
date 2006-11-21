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
package org.cytoscape.coreplugin.cpath.task;

import org.cytoscape.coreplugin.cpath.mapper.MapGraphToInteractions;
import org.cytoscape.coreplugin.cpath.ui.ErrorDisplay;
import csplugins.task.BaseTask;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import org.mskcc.dataservices.mapper.MapInteractionsToPsi;
import org.mskcc.dataservices.schemas.psi.EntrySet;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Task to Import PSI-MI Data.
 *
 * @author Ethan Cerami
 */
public class ExportPsiTask extends BaseTask {
    private File file;
    private CyNetwork cyNetwork;
    private Component parentFrame;
    private static final String TASK_TITLE = "Exporting to PSI-MI";

    /**
     * Constructor.
     *
     * @param cyNetwork CyNetwork Object
     * @param file      File to Save to.
     */
    public ExportPsiTask(CyNetwork cyNetwork, File file) {
        super(TASK_TITLE);
        this.cyNetwork = cyNetwork;
        this.file = file;
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
     * Import PSI Data.
     *
     * @throws Exception All Exceptions.
     */
    public void executeTask() throws Exception {
        try {
            this.setProgressMessage("Exporting to PSI-MI Format.");
            this.setMaxProgressValue(3);

            this.setProgressValue(0);
            CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
            MapGraphToInteractions mapper1 =
                    new MapGraphToInteractions(cyNetwork);
            mapper1.doMapping();
            this.setProgressValue(1);

            ArrayList interactions = mapper1.getInteractions();
            MapInteractionsToPsi mapper2 =
                    new MapInteractionsToPsi(interactions);
            mapper2.doMapping();
            this.setProgressValue(2);

            EntrySet entrySet = mapper2.getPsiXml();
            FileWriter fileWriter = new FileWriter(file);
            entrySet.marshal(fileWriter);
            String msg = "PSI-MI written to file:  " + file;
            this.setProgressValue(3);
            updateUser(msg);
        } catch (Exception e) {
            showError(e);
            throw e;
        }
    }

    /**
     * Update User about what just happend.
     *
     * @param msg Message.
     */
    private void updateUser(final String msg) {
        Runnable runnable = new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame, msg);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Show Error Message.
     *
     * @param exception Exception.
     */
    private void showError(final Throwable exception) {
        Runnable runnable = new Runnable() {
            public void run() {
                ErrorDisplay errorDisplay =
                        new ErrorDisplay((JFrame) parentFrame);
                errorDisplay.displayError(exception);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
}