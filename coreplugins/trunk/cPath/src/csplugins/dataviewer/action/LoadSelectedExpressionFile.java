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

import csplugins.dataviewer.task.LoadExpressionDataTask;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * User has requested that the specified expression data file be loaded.
 *
 * @author Ethan Cerami.
 */
public class LoadSelectedExpressionFile implements ActionListener {
    private JComboBox comboBox;
    private File mageFile;

    /**
     * Constructor.
     *
     * @param mageFile MAGE-ML File.
     * @param comboBox Combo Box, containing user selection.
     */
    public LoadSelectedExpressionFile(File mageFile, JComboBox comboBox) {
        this.mageFile = mageFile;
        this.comboBox = comboBox;
    }

    /**
     * User initiates action.
     *
     * @param actionEvent ActionEvent Object.
     */
    public void actionPerformed(ActionEvent actionEvent) {
        String fileName = (String) comboBox.getSelectedItem();
        File file = new File(mageFile.getParentFile(), fileName);
        LoadExpressionDataTask task = new LoadExpressionDataTask(file.getAbsolutePath());

        //  Configure JTask
        JTaskConfig config = new JTaskConfig();
        config.setOwner(Cytoscape.getDesktop());
        config.displayStatus(true);
        config.setAutoDispose(true);

        //  Execute Task via TaskManager
        //  This automatically pops-open a JTask Dialog Box.
        //  This method will block until the JTask Dialog Box is disposed.
        boolean success = TaskManager.executeTask(task, config);
    }
}