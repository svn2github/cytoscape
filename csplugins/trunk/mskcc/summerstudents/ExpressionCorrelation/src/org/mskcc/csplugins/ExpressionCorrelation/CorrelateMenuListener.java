package org.mskcc.csplugins.ExpressionCorrelation;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Elena Potylitsine
 * * Authors: Gary Bader, Elena Potylitsine, Chris Sander, Weston Whitaker
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
 * Created by IntelliJ IDEA.
 * User: Elena Potylitsine
 * Date: Jul 13, 2004
 * Time: 4:34:43 PM
 * * Description: This CorrelateMenuListener listens to the selections made by the user.
 * *It checks that an Expression Data Matrix is loaded before execution of the commands.
 * *It displays a warning window explaining that the Pearson Correaltion coefficient
 * *is not an optimal algorithm for condition matrixes that contain fewer then 4 parameters
 */

public class CorrelateMenuListener implements ActionListener {

    String selection = ""; //the menu item selected
    public int performEvent = 0; //the event id selected

    public void actionPerformed(ActionEvent event) {

        //listens for the menu and depending on selection gets source
        CorrelateSimilarityNetwork network = new CorrelateSimilarityNetwork();
        JMenuItem source = (JMenuItem) (event.getSource());
        selection = source.getText();

        if (Cytoscape.getExpressionData() == null) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "You must load an Expression Matrix File to run this plugin.", "ALERT!!!", JOptionPane.ERROR_MESSAGE);
        } else {
            int colNumber = network.getNumberOfCols(); //number of conditions in the condition network
            int rowNumber = network.getNumberOfRows();
            int CANCEL_OPTION = 1;

            String warning =
                    "The expresssion data contains less then 4 conditions (" + colNumber + " conditions found)." + '\n' +
                    "The Pearson Correlation calculation will not produce" + '\n' +
                    "reliable results for correlating the gene matrix." + '\n' +
                    "Would you like to proceed?";


            String warning2 =
                    "The expresssion data contains less then 4 genes (" + rowNumber + " genes found)." + '\n' +
                    "The Pearson Correlation calculation will not produce" + '\n' +
                    "reliable results for correlating the condition matrix." + '\n' +
                    "Would you like to proceed?";


            //must only come up in case when < 4 genes and try to do condition matrix
            //the vectors for condition matrix will be of length < 4 not enough
            if (rowNumber < 4 && (selection.equals("Construct Correlation Network") ||
                    selection.equals("Condition Network: Preview Histogram") ||
                    selection.equals("Condition Network: Using Defaults"))) {


                Object[] options = {warning2, };
                CANCEL_OPTION = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), options, "NOT ENOUGH GENES", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
            //must only come up in case when < 4 conditions and try to do gene matrix
            //the vectors for condition matrix will be of length < 4 not enough
            else if (colNumber < 4 && (selection.equals("Construct Correlation Network") ||
                    selection.equals("Gene Network: Preview Histogram") ||
                    selection.equals("Gene Network: Using Defaults"))) {
                Object[] options = {warning, };
                CANCEL_OPTION = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), options, "NOT ENOUGH CONDITIONS", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
            if (CANCEL_OPTION != 2) {//procede if not canceled
                performEvent = setEvent(selection);
            }
            System.out.println("the selection was: " + selection);
            //  Create a Correlate Task
            Task task = new CorrelateTask(performEvent, network);

            //  Configure JTask
            JTaskConfig config = new JTaskConfig();
            config.setOwner(Cytoscape.getDesktop());
            config.displayCloseButton(true);
            config.displayCancelButton(true);
            config.displayStatus(true);

            //  Execute Task via TaskManager
            //  This automatically pops-open a JTask Dialog Box.
            //  This method will block until the JTask Dialog Box is disposed.
            boolean success = TaskManager.executeTask(task, config);
            
        }
    }

    public int setEvent(String selection) {
        int newEvent = 0;
        if (selection.equals("Construct Correlation Network")) {
            newEvent = 1; //construct both row and column
        }
        if (selection.equals("Condition Network: Preview Histogram")) {
            newEvent = 3; //construct histogram + col
        }
        if (selection.equals("Condition Network: Using Defaults")) {
            newEvent = 2; //construct col similarity matrix using defaults
        }
        if (selection.equals("Gene Network: Preview Histogram")) {
            newEvent = 5; //construct histogram + row
        }
        if (selection.equals("Gene Network: Using Defaults")) {
            newEvent = 4; //construct row similarity matrix using defaults
        }
        return newEvent;
    }


}

