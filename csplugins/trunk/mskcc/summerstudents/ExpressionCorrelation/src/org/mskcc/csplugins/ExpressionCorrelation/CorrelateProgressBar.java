package org.mskcc.csplugins.ExpressionCorrelation;

import cytoscape.Cytoscape;

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
 * * User: Elena Potylitsine
 * * Date: Jul 7, 2004
 * * Time: 3:35:11 PM
 * * Description: CorrelateProgressBar for timing of long running events
 * *
 * *
 */

public class CorrelateProgressBar {
    private CorrelateSimilarityNetwork network;
    final CorrelateProgressBarDialog progressBarDialog = new CorrelateProgressBarDialog(Cytoscape.getDesktop());
    Timer timer;                            //Instance of timer
    boolean colDone = false;                //becomes true when the column network is complete
    public int performEvent = 0;            /**Sets which event thread to run and time
     *case 1: Run and time both the column and row networks
     *case 2: Run and time the column network
     *case 3: Run and time the column histogram
     *case 4: Run and time the row network
     *case 5: Run and time the row histogram
     */

    /**
     * progress_bar runs and monitores the event thread chosen
     *
     * @param newEvent   - the chosen event thread to run
     * @param newNetwork - instance of network
     */
    public void CorrelateProgressBar(int newEvent, CorrelateSimilarityNetwork newNetwork) {
//set up progress bar
        network = newNetwork;
        progressBarDialog.setIndeterminate(false);
        progressBarDialog.pack();
        progressBarDialog.setVisible(true);

        performEvent = newEvent;

        //Set up the timer class
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(progressBarDialog.isCancelled()) {
                    network.cancel();
                    progressBarDialog.dispose();
                    timer.stop();
               }
                switch (performEvent) {
                    case 1:
                        if (colDone)  //case one for doing both timing column and the row network creation
                            rowProgress();
                        else
                            colProgress();
                        break;
                    case 2:
                        colProgress();
                        break;  //timing of the column network creation
                    case 3:
                        colProgress();
                        break;  //timing of the histogram
                    case 4:
                        rowProgress();
                        break;  //timing of the  row network creation
                    case 5:
                        rowProgress();
                        break;
                }
            }
        });

        //threaded not to interfere with cytoscape
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                switch (performEvent) {
                    case 1:
                        colRun();
                        rowRun();
                        break;
                    case 2:
                        colRun();
                        break;
                    case 3:
                        colHistogram();
                        break;
                    case 4:
                        rowRun();
                        break;
                    case 5:
                        rowHistogram();
                        break;
                }
                if (progressBarDialog.isCancelled()) {
                    progressBarDialog.dispose();
                }
                return null;
            }

            /**
             * Called on the event dispatching thread (not on the worker thread)
             * after the <code>construct</code> method has returned.
             */
            public void finished() {
                //Can do gui work here
                System.out.println("The network is complete");
                //progressBarDialog.setString("I am all DONE :)");
                timer.stop();
                progressBarDialog.dispose();
            }

        };
        worker.start();
        timer.start();

    }

//timing for the condition matrix calculation
    public void colProgress() {

        progressBarDialog.setValue(network.getColCurrentStep());
        int steps = 0;
        if (progressBarDialog.getIndeterminate()) //if in indeterminate mode then get steps
        {
            progressBarDialog.setIndeterminate(false);
            steps = network.getColTotalSteps();
            progressBarDialog.setLengthOfTask(steps);
        }
        if (network.colIsDone()) {
            progressBarDialog.setString("Loading the Matrix");
            progressBarDialog.setIndeterminate(true);
            colDone = true;
        }
    }

    //timing for the gene matrix calculation
    public void rowProgress() {
        progressBarDialog.setValue(network.getRowCurrentStep());
        int steps = 0;
        if (progressBarDialog.getIndeterminate()) //if in indeterminate mode then get steps
        {
            progressBarDialog.setIndeterminate(false);
            steps = network.getRowTotalSteps();
            progressBarDialog.setLengthOfTask(steps);
        }
        if (network.rowIsDone()) {
            progressBarDialog.setString("Loading the Matrix");
            progressBarDialog.setIndeterminate(true);
        }
    }

    //the condition matrix calculation
    public void colRun() {
        if (Cytoscape.getCurrentNetwork().getExpressionData() != null) {
            progressBarDialog.setIndeterminate(true);
            progressBarDialog.setString("Calculating the Columns");
            network.calcCols();
        }
    }

    //the gene matrix calculation
    public void rowRun() {
        if (Cytoscape.getCurrentNetwork().getExpressionData() != null) {

            progressBarDialog.setString("Calculating the Rows");
            System.out.println("starting rows");
            progressBarDialog.setIndeterminate(true);
            network.calcRows();
        }
    }

    //the histogram calculations
    public void colHistogram() {
        if (Cytoscape.getCurrentNetwork().getExpressionData() != null) {

            progressBarDialog.setString("Creating Column Histogram");
            System.out.println("starting col histogram");
            progressBarDialog.setIndeterminate(true);
            CorrelateHistogramWindow histogram = new CorrelateHistogramWindow(Cytoscape.getDesktop(), false, network, progressBarDialog); //not row histogram
            if (progressBarDialog.isCancelled()) {
                return;
            }
            histogram.pack();
            histogram.setVisible(true);
        }
    }

    public void rowHistogram() {
        if (Cytoscape.getCurrentNetwork().getExpressionData() != null) {
            progressBarDialog.setString("Creating Row Histogram");
            System.out.println("starting row histogram");
            progressBarDialog.setIndeterminate(true);
            CorrelateHistogramWindow histogram = new CorrelateHistogramWindow(Cytoscape.getDesktop(), true, network, progressBarDialog); //row histogram
            if (progressBarDialog.isCancelled()) {
                return;
            }
            histogram.pack();
            histogram.setVisible(true);
        }
    }
}