package org.mskcc.csplugins.ExpressionCorrelation;

import org.jmathplot.gui.Plot2DPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

/* * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
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
 * * Description: CorrelateHistogramWindow displayes the expression data distribution.
 * * In the histogram window the user can select the low and high cutoffs by manually
 * * typing them into the appropriate "Cutoff" text boxes.
 * * The user can choose to use only one set of cutoffs by deselecting the "low" or "high" checkbox.
 * * The user can select the number or percent of interactions to be displayed
 * * by typing into the "Enter" text box and choosing "Number of Interactions" or "Percent of Interactions".
*/


public class CorrelateHistogramWindow extends JDialog {

    private CorrelateSimilarityNetwork network;	//Instance of the Similarity network function
    JFormattedTextField lowCutoffValue; 	//Formated field for input of the low cutoff value
    JFormattedTextField highCutoffValue;	//Formated field for input of the high cutoff value
    JFormattedTextField interactionsValue;	//Formated field to hold the number or percent of interactions

    JCheckBox lowCheckBox;				//Allows the user to select to use low cuttofs or not
    JCheckBox highCheckBox;				//Allows th user to selct to use high cutoffs or not
    JComboBox percentNumberComboBox; //combo box for choosing either interaction number or percent

    boolean isRow = false; 			//set to false to display column histogram, set to true to display row histogram
    double[] cutoffs = new double[2]; // first element is negative cutoff; 2nd element is positive cutoff
    Number valueLow;					//Holds the low value from the text field
    Number valueHigh;					//Holds the high value from the text field
    Number valueInteractions;			//Holds either the percent or the number of interactions value
    String selectedString = "Number of Interactions";  //represents the choice from the combo box

    /**
     * One histogram window is generated for either the column or row similarity matrix calculation
     *
     * @param parentFrame - instance of the Cytoscape frame being used to display the histogram
     * @param row         - boolean variable is true if the matrix being worked on is the row matrix
     * @param newNetwork  - instance of the SimilarityMatrix
     */
    public CorrelateHistogramWindow(Frame parentFrame, boolean row, CorrelateSimilarityNetwork newNetwork) {

        super(parentFrame, "Matrix Parameters", false);
        setResizable(false);
        isRow = row;
        network = newNetwork;

        if (isRow) {
            network.loadRowCutoffs();	//Loads previosly saved user row cuttofs from the singleton class
            network.rowHistogram(); 	// get image and values
        } else {
            network.loadColCutoffs();	//Loads previously saved user column cutoffs from the singleton class
            network.colHistogram(); 	// get image and values
        }
        if (network.cancelled())
        {
            return;
        }
        DecimalFormat decFormat = new DecimalFormat();
        decFormat.setParseIntegerOnly(true);

        //main panel for dialog box
        JPanel panel = new JPanel(new BorderLayout());

        //cutoffs pannel
        JPanel cutOfPanel = new JPanel(new BorderLayout());
        JPanel cutOfSubPanel = new JPanel(new BorderLayout(20, 4));

        //Low Cutoff options
        lowCutoffValue = new JFormattedTextField(new DecimalFormat("-0.000")) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };

        lowCutoffValue.setColumns(4); // 3 + space for the neative sign
        lowCutoffValue.addPropertyChangeListener("value", new CorrelateHistogramWindow.formattedTextFieldAction());
        String tipLow = "Look at the Histogram and set the low cutoff for the network that you want displayed.\n" +
                "Or deselect the checkbox if you do not want a low cutoff value";
        lowCutoffValue.setToolTipText(tipLow);
        cutoffs = network.getCutoffs(isRow);
        lowCutoffValue.setText(Double.toString(cutoffs[0]));
        JLabel lowCutoff = new JLabel("Low Cutoff");

        lowCheckBox = new JCheckBox("Low", false) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        lowCheckBox.addItemListener(new CorrelateHistogramWindow.lowCheckBoxAction());
        lowCheckBox.setToolTipText("If checked, allows you to set the low cutoff value. \n" +
                "If not, a low cuttof value will not be used in the percent calculation");
        lowCheckBox.setSelected(true);

        JPanel labelFieldPanelLow = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        labelFieldPanelLow.setToolTipText(tipLow);
        labelFieldPanelLow.add(lowCutoff);       	//Adds laber
        labelFieldPanelLow.add(lowCutoffValue); 	//Adds text box
        labelFieldPanelLow.add(lowCheckBox);      	//Adds check box
        cutOfSubPanel.add(labelFieldPanelLow, BorderLayout.WEST);

        //High cutoff options
        highCutoffValue = new JFormattedTextField(new DecimalFormat("0.000")) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };

        highCutoffValue.setColumns(3);
        highCutoffValue.addPropertyChangeListener("value", new CorrelateHistogramWindow.formattedTextFieldAction());
        String tipHigh = "Look at the Histogram and set the high cutoff for the network that you want displayed.\n" +
                "Or deselect the checkbox if you do not want a high cutoff value";
        highCutoffValue.setToolTipText(tipHigh);
        cutoffs = network.getCutoffs(isRow);
        highCutoffValue.setText(Double.toString(cutoffs[1]));
        JLabel highCutoff = new JLabel("High Cutoff");

        highCheckBox = new JCheckBox("High", false) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        highCheckBox.addItemListener(new CorrelateHistogramWindow.highCheckBoxAction());
        highCheckBox.setToolTipText("If checked, allows you to set the high cutoff value. \n" +
                "If not, a high cuttof value will not be used in the percent calculation");
        highCheckBox.setSelected(true);

        JPanel labelFieldPanelHigh = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        labelFieldPanelHigh.setToolTipText(tipHigh);
        labelFieldPanelHigh.add(highCutoff);			//Adds label
        labelFieldPanelHigh.add(highCutoffValue);		//Adds text box
        labelFieldPanelHigh.add(highCheckBox);			//Adds check box
        cutOfSubPanel.add(labelFieldPanelHigh, BorderLayout.EAST);


        //Number of interactions versus percent of interactions options
        JPanel Interactions = new JPanel(new BorderLayout(25, 4));

        interactionsValue = new JFormattedTextField(new DecimalFormat("########.0####")) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };

        interactionsValue.setColumns(6);
        interactionsValue.addPropertyChangeListener("value", new CorrelateHistogramWindow.formattedTextFieldAction());
        String tipSize = "Select either the number of interactions you want displayed.\n" +
                "Or the percent of interactions you want diplayed";
        interactionsValue.setToolTipText(tipSize);
        cutoffs = network.getCutoffs(isRow);
        interactionsValue.setText(Integer.toString(network.getNumberOfInteractions(isRow, cutoffs)));
        JLabel size = new JLabel("Enter: ");

        JPanel labelFieldPanelsize = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        labelFieldPanelsize.setToolTipText(tipSize);
        labelFieldPanelsize.add(size);
        labelFieldPanelsize.add(interactionsValue);
        Interactions.add(labelFieldPanelsize, BorderLayout.WEST);

        String tipInteractions = "Check this box to create a Network with the Number of Interactions \n"
                + "you want displayed and to set the appropriate cutoffs";

        percentNumberComboBox = new JComboBox();
        percentNumberComboBox.addItem("Number of Interactions");
        percentNumberComboBox.addItem("Percent of Interactions");
        percentNumberComboBox.addItemListener(new CorrelateHistogramWindow.percentNumberComboBox_Action());
        percentNumberComboBox.setToolTipText(tipInteractions);

        JPanel labelFieldPanelPercentNumber = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        labelFieldPanelPercentNumber.setToolTipText(tipInteractions);
        labelFieldPanelPercentNumber.add(percentNumberComboBox);
        Interactions.add(labelFieldPanelPercentNumber, BorderLayout.EAST);
        Interactions.setBorder(BorderFactory.createEtchedBorder());

        //Ok and Cancel Options
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton OKButton = new JButton("OK");
        OKButton.addActionListener(new CorrelateHistogramWindow.OKAction(this));
        bottomPanel.add(OKButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CorrelateHistogramWindow.cancelAction(this));
        bottomPanel.add(cancelButton);

        cutOfPanel.add(cutOfSubPanel, BorderLayout.NORTH);
        cutOfPanel.add(Interactions, BorderLayout.SOUTH);
        cutOfPanel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(cutOfPanel, BorderLayout.NORTH);
        Plot2DPanel histoPlot = new Plot2DPanel(network.getHistogram(isRow), "Similarity  Histogram", "HISTOGRAM");
        panel.add(histoPlot, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(panel);
    }

    /**
     * Listens to chages to the low text field window
     * if a change occurs ->changes the interactions field window as well
     * makes sure the value entered is within bounds otherwise sets value to original cutoffs value
     */
    public void lowGetSet() {
        double value;
        valueLow = (Number) lowCutoffValue.getValue();

        if (valueLow != null) {
            value = -valueLow.doubleValue();
            System.out.println("The low in the box is: " + value);
            //System.out.println("The low value in the text: "+ value);
            if ((value < 0) && (value >= -1)) {
                if (isRow)
                    network.setRowNegCutoff(value);
                else
                    network.setColNegCutoff(value);

                getSet();
                cutoffs = network.getCutoffs(isRow);
                System.out.println("Low change: " + isRow + " | " + cutoffs[0] + " + " + cutoffs[1]);
                lowCutoffValue.setText(Double.toString(cutoffs[0]));
            }
        }
    }

    /**
     * Listens to chages to the high text field window
     * if a change occurs ->changes the interactions field window as well
     * makes sure the value entered is within bounds otherwise sets value to original cutoffs value
     */
    public void highGetSet() {
        double value;
        int high = 1;
        valueHigh = (Number) highCutoffValue.getValue();
        System.out.println("I am in HIGH");
        if (valueHigh != null) {

            value = valueHigh.doubleValue();
            System.out.println("The high in the box is: " + value);
            //System.out.println("The high value in the text: "+ value + isRow);
            if ((value > 0) && (value <= 1)) {
                if (isRow)
                    network.setRowPosCutoff(value);
                else
                    network.setColPosCutoff(value);

                getSet();
                cutoffs = network.getCutoffs(isRow);
                System.out.println("High change: " + isRow + " | " + cutoffs[0] + " + " + cutoffs[1]);
                highCutoffValue.setText(Double.toString(cutoffs[1]));
            }
        }
    }

    /**
     * gets cutoffs and sets interaction number
     */
    public void getSet() {
        cutoffs = network.getCutoffs(isRow);
        selectedString = (String) percentNumberComboBox.getSelectedItem();
        if (selectedString.equals("Number of Interactions")) {
            System.out.println("Number of interactions cutoffs: " + cutoffs[0] + " | " + cutoffs[1]);
            interactionsValue.setText(Integer.toString(network.getNumberOfInteractions(isRow, cutoffs)));
        } else if (selectedString.equals("Percent of Interactions")) {
            interactionsValue.setText(Double.toString(network.getPercentOfInteractions(isRow, cutoffs)));
            System.out.println("Percent of interactions cutoffs: " + cutoffs[0] + " | " + cutoffs[1]);
        }
    }

    /**
     * Listens to chages to the interactions text field window
     * if a change occurs ->changes the high and low field windows as well
     * makes sure the value entered is within bounds otherwise sets value to original cutoffs value
     */
    public void interactionsGetSet() {
        double doubleValue;
        int intValue;
        valueInteractions = (Number) interactionsValue.getValue();
        selectedString = (String) percentNumberComboBox.getSelectedItem();
        //System.out.println("The selected choice is: "+selectedString);
        if (selectedString.equals("Number of Interactions") && (valueInteractions != null)) {	//System.out.println("number of interactions not null"); //for now
            intValue = valueInteractions.intValue();
            if (intValue >= 0) {
                System.out.println("The intValue: " + intValue);
                network.setCutoffsInteractions(isRow, intValue);
                cutoffs = network.getCutoffs(isRow);
                System.out.println("Interactions cutoffs are:" + cutoffs[0] + " " + cutoffs[1]);
                lowCutoffValue.setText(Double.toString(cutoffs[0]));
                highCutoffValue.setText(Double.toString(cutoffs[1]));
                getSet();
            }
        } else if (selectedString.equals("Percent of Interactions") && (valueInteractions != null)) {
            //System.out.println("percent interactions not null"); //for now
            doubleValue = valueInteractions.doubleValue();
            if (doubleValue < 0 || doubleValue > 1.0) {

                doubleValue = 1.0;
                network.setCutoffsPercent(isRow, doubleValue);
                cutoffs = network.getCutoffs(isRow);
                interactionsValue.setText(Double.toString(network.getPercentOfInteractions(isRow, cutoffs)));
            }
            //System.out.println("The doubleValue: "+doubleValue);
            network.setCutoffsPercent(isRow, doubleValue);
            cutoffs = network.getCutoffs(isRow);
            lowCutoffValue.setText(Double.toString(cutoffs[0]));
            highCutoffValue.setText(Double.toString(cutoffs[1]));
        }

    }


    /**
     * Listens to chages in any of the windows
     * redirects action depending on change
     */
    private class formattedTextFieldAction implements PropertyChangeListener {
        /**
         * @param e - property change event variable of the text field, activated if field is changed
         */
        public void propertyChange(PropertyChangeEvent e) {
            Object source = e.getSource();
            if (source == lowCutoffValue) {
                lowGetSet();
            } else if (source == highCutoffValue) {
                highGetSet();
            } else if (source == interactionsValue) {
                interactionsGetSet();
            }
        }
    }


    /**
     * Listens to chages in checkbox state
     * sets whether to use or not the negative cutoffs in creating the network
     */
    private class lowCheckBoxAction implements ItemListener {
        /**
         * @param e - activated if checkbox state changed
         */
        public void itemStateChanged(ItemEvent e) {
            double neg_value = -1.0;
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                if (isRow) {
                    network.setRowNegUse(false);
                } else {
                    network.setColNegUse(false);
                }
                getSet();
                lowCutoffValue.setEnabled(false);
            } else {
                if (isRow)
                    network.setRowNegUse(true);
                else
                    network.setColNegUse(true);
                if (interactionsValue != null)
                    getSet();
                lowCutoffValue.setEnabled(true);
            }

        }

    }


    /**
     * Listens to chages in checkbox state
     * sets whether to use or not the positive cutoffs in creating the network
     */
    private class highCheckBoxAction implements ItemListener {
        /**
         * @param e - activated if check box state changed
         */
        public void itemStateChanged(ItemEvent e) {
            double posValue = 1.0;
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                if (isRow) {

                    network.setRowPosUse(false);
                    //network.setRowPosCutoff(posValue);
                } else {
                    network.setColPosUse(false);
                    //network.setColPosCutoff(posValue);
                }
                getSet();
                //highCutoffValue.setText(Double.toString(1.0));
                highCutoffValue.setEnabled(false);
            } else {
                if (isRow)
                    network.setRowPosUse(true);
                else
                    network.setColPosUse(true);
                //valueInteractions = (Number) interactionsValue.getValue();
                //if(valueInteractions != null)
                //interactionsGetSet();
                if (interactionsValue != null)
                    getSet();
                highCutoffValue.setEnabled(true);
            }
        }
    }

    /**
     * Listens to chages in combobox state
     * converts between percent and number of interactions upon change of selction
     */
    private class percentNumberComboBox_Action implements ItemListener {
        /**
         * @param e - activated if text box state changed
         */
        public void itemStateChanged(ItemEvent e) {
            String selectedString = (String) percentNumberComboBox.getSelectedItem();
            if (selectedString.equals("Number of Interactions")) {
                interactionsValue.setText(Integer.toString(network.getNumberOfInteractions(isRow, cutoffs)));
            } else if (selectedString.equals("Percent of Interactions")) {
                interactionsValue.setText(Double.toString(network.getPercentOfInteractions(isRow, cutoffs)));
            }
        }
    }

    /**
     * Listens to Ok button action
     * Upon activation retreives the latest cutoffs saves them in the singleton class
     * and begins the timed an monitored progress of the network creation
     */
    private class OKAction extends AbstractAction {
        private JDialog dialog;

        OKAction(JDialog popup) {
            super();
            this.dialog = popup;
        }

        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
            int viewThreshold = 1000;
            int event = 2;
            try
            {
                viewThreshold = Integer.parseInt(CytoscapeInit.getProperties().getProperty("viewThreshold"));
                System.out.println("viewThreshold is " + viewThreshold);
            } catch (NumberFormatException nfe)
            {
                viewThreshold = 0;
            }
            if (isRow) {
                interactionsGetSet();
                network.saveRowCutoffs();//Saves the row cutoffs in singleton
                event = 4;
            } else {
                interactionsGetSet();
                network.saveColCutoffs(); //Saves the column cutoffs in singleton
            }

            cutoffs = network.getCutoffs(isRow);
            
            //  Create a Correlate Task
            Task task = new CorrelateTask(event, network);

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
            
            cutoffs = network.getCutoffs(isRow);
            //System.out.println("The cutoffs selected are: "+cutoffs[0]+" | "+cutoffs[1]);
        }
    }

    /**
     *
     */
    private class cancelAction extends AbstractAction {
        private JDialog dialog;

        cancelAction(JDialog popup) {
            super();
            this.dialog = popup;
        }

        /**
         * @param e - activated if button pressed
         */
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    }

}