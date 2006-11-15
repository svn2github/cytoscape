package csplugins.mcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * The parameter change cytpanel which the user can use to select scope and change the scoring and finding parameters
 */
public class MCODEMainPanel extends JPanel {
    //Parameters for MCODE
    MCODEParameterSet currentParamsCopy;    //stores current parameters - populates dialog box fields

    DecimalFormat decFormat;

    JRadioButton scopeNetwork;
    JRadioButton scopeNode;
    JRadioButton scopeNodeSet;

    //resetable UI elements

    //scoring
    JCheckBox includeLoopsCheckBox;
    JFormattedTextField degreeCutOffFormattedTextField;
    //cluster finding
    JFormattedTextField maxDepthFormattedTextField;
    JFormattedTextField nodeScoreCutOffFormattedTextField;
    JCheckBox haircutCheckBox;
    JCheckBox fluffCheckBox;
    JFormattedTextField fluffNodeDensityCutOffFormattedTextField;
    //directed mode
    JCheckBox processCheckBox;

    /**
     * The actual parameter change dialog that builds the UI
     */
    public MCODEMainPanel() {
        setLayout(new BorderLayout());
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //get the current parameters
        currentParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy();

        decFormat = new DecimalFormat();
        decFormat.setParseIntegerOnly(true);

        //create the three main panels: scope, advanced options, and bottom
        JPanel scopePanel = createScopePanel();
        MCODECollapsablePanel advancedOptionsPanel = createAdvancedOptionsPanel();
        JPanel bottomPanel = createBottomPanel();

        //Add all the vertically alligned components to the main panel
        add(scopePanel, BorderLayout.NORTH);
        add(advancedOptionsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Generates a JPanel with all the scope components
     * @return panel containing the scope option buttons
     */
    private JPanel createScopePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Scope"));

        JLabel scopeStarter = new JLabel("Find cluster(s)...");
        scopeNetwork = new JRadioButton("in Whole Network", true);
        scopeNode = new JRadioButton("from Selected Node");
        scopeNodeSet = new JRadioButton("from Selected Node Set");

        ButtonGroup scopeOptions = new ButtonGroup();
        scopeOptions.add(scopeNetwork);
        scopeOptions.add(scopeNode);
        scopeOptions.add(scopeNodeSet);

        panel.add(scopeStarter);
        panel.add(scopeNetwork);
        panel.add(scopeNode);
        panel.add(scopeNodeSet);
        
        return panel;
    }

    /**
     *
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createAdvancedOptionsPanel() {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel("Advanced Options");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Network scoring collapsable panel
        MCODECollapsablePanel networkScoringPanel = createNetworkScoringPanel();

        //Cluster finding collapsable panel
        MCODECollapsablePanel clusterFindingPanel = createClusterFindingPanel();

        panel.add(networkScoringPanel);
        panel.add(clusterFindingPanel);

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Generates a JPanel with all the network scoring components
     * @return panel containing the network scoring parameter inputs
     */
    private MCODECollapsablePanel createNetworkScoringPanel() {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel("Network Scoring");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        //Include loops input
        includeLoopsCheckBox = new JCheckBox() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        includeLoopsCheckBox.setSelected(false);
        includeLoopsCheckBox.addItemListener(new MCODEMainPanel.includeLoopsCheckBoxAction());
        String includeLoopsTip = "If checked, MCODE will include loops (self-edges) in the neighborhood\n" +
                "density calculation.  This is expected to make a small difference in the results.";
        includeLoopsCheckBox.setToolTipText(includeLoopsTip);
        includeLoopsCheckBox.setSelected(currentParamsCopy.isIncludeLoops());
        JLabel includeLoopsLabel = new JLabel("Include Loops");
        JPanel includeLoopsPanel = new JPanel() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        includeLoopsPanel.setLayout(new BorderLayout());
        includeLoopsPanel.setToolTipText(includeLoopsTip);
        includeLoopsPanel.add(includeLoopsLabel, BorderLayout.WEST);
        includeLoopsPanel.add(includeLoopsCheckBox, BorderLayout.EAST);
        panel.add(includeLoopsPanel);

        //Degree cutoff input
        degreeCutOffFormattedTextField = new JFormattedTextField(decFormat) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        degreeCutOffFormattedTextField.setColumns(3);
        degreeCutOffFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.formattedTextFieldAction());
        String degreeCutOffTip = "Sets the degree cutoff below which a node will not be scored.\n" +
                "Nodes with a degree equal or higher to this value will be scored.\n" +
                "By default this is set to 2. Valid values are 2 or higher to prevent singly connected nodes\n" +
                "from getting an artificially high node score.";
        degreeCutOffFormattedTextField.setToolTipText(degreeCutOffTip);
        degreeCutOffFormattedTextField.setText((new Integer(currentParamsCopy.getDegreeCutOff()).toString()));
        JLabel degreeCutOffLabel = new JLabel("Degree Cutoff");
        JPanel degreeCutOffPanel = new JPanel() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        degreeCutOffPanel.setLayout(new BorderLayout());
        degreeCutOffPanel.setToolTipText(degreeCutOffTip);
        degreeCutOffPanel.add(degreeCutOffLabel, BorderLayout.WEST);
        degreeCutOffPanel.add(degreeCutOffFormattedTextField, BorderLayout.EAST);
        panel.add(degreeCutOffPanel);

        //K-core input
        //TODO:k-core

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     *
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createClusterFindingPanel() {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel("Cluster Finding");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JRadioButton customizeOption = new JRadioButton("Customize", true);
        JRadioButton optimizeOption = new JRadioButton("Optimize (Benchmark)");
        ButtonGroup clusterFindingOptions = new ButtonGroup();
        clusterFindingOptions.add(customizeOption);
        clusterFindingOptions.add(optimizeOption);

        //customize parameters panel
        MCODECollapsablePanel customizeClusterFindingPanel = createCustomizeClusterFindingPanel(customizeOption);
        //optimize parameters panel
        MCODECollapsablePanel optimizeClusterFindingPanel = createOptimizeClusterFindingPanel(optimizeOption);

        panel.add(customizeClusterFindingPanel);
        panel.add(optimizeClusterFindingPanel);

        //directed mode panel
        /*
        JPanel directedModePanel = new JPanel();
        processCheckBox = new JCheckBox("Preprocess network", false) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        processCheckBox.addItemListener(new MCODEMainPanel.processCheckBoxAction());
        processCheckBox.setToolTipText("If checked, MCODE will limit cluster expansion to the\n" +
                "direct neighborhood of the spawning node.  If unchecked, the cluster will be allowed\n" +
                "to branch out to denser regions of the network.");
        processCheckBox.setSelected(currentParamsCopy.isPreprocessNetwork());
        directedModePanel.add(processCheckBox);

        //JTabbedPane tabbedPane = new JTabbedPane();
        //tabbedPane.addTab("Network Scoring", null, scorePanel, "Set parameters for scoring stage (Stage 1)");
        //tabbedPane.addTab("Find Clusters", null, findPanel, "Set parameters for cluster finding stage (Stage 2)");
        //TODO: uncomment below when directed mode is implemented
        //tabbedPane.addTab("Directed Mode", null, directedModePanel, "Set parameters for directed mode");
        */
        
        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }
    /**
     *
     * @param component Any JComponent that may appear in the titled border of the panel
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createCustomizeClusterFindingPanel(JComponent component) {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel(component);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        //nodeScoreCutOffFormattedTextField = new JFormattedTextField(new DecimalFormat("0.000")) {
        //    public JToolTip createToolTip() {
        //        return new JMultiLineToolTip();
        //    }
        //};
        //nodeScoreCutOffFormattedTextField.setColumns(3);
        //nodeScoreCutOffFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.formattedTextFieldAction());
        //String tipText3 = "Sets the node score cutoff for expanding a cluster as a percentage from the seed node score.\n" +
        //        "This is the most important parameter to control the size of MCODE clusters,\n" +
        //        "with smaller values creating smaller clusters.";
        //nodeScoreCutOffFormattedTextField.setToolTipText(tipText3);
        //nodeScoreCutOffFormattedTextField.setText((new Double(currentParamsCopy.getNodeScoreCutOff()).toString()));
        //JLabel nodeScoreCutOffLabel = new JLabel("Node Score Cutoff");
        //JPanel labelFieldPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
        //    public JToolTip createToolTip() {
        //        return new JMultiLineToolTip();
        //    }
        //};
        //labelFieldPanel3.setToolTipText(tipText3);
        //labelFieldPanel3.add(nodeScoreCutOffLabel);
        //labelFieldPanel3.add(nodeScoreCutOffFormattedTextField);

        haircutCheckBox = new JCheckBox("Haircut", false) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        haircutCheckBox.addItemListener(new MCODEMainPanel.haircutCheckBoxAction());
        haircutCheckBox.setToolTipText("If checked, MCODE will give clusters a haircut\n" +
                "(remove singly connected nodes).");
        haircutCheckBox.setSelected(currentParamsCopy.isHaircut());
        panel.add(haircutCheckBox);

        fluffNodeDensityCutOffFormattedTextField = new JFormattedTextField(new DecimalFormat("0.000")) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        fluffNodeDensityCutOffFormattedTextField.setColumns(3);
        fluffNodeDensityCutOffFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.formattedTextFieldAction());
        String tipText4 = "Sets the fluff density cutoff for expanding a cluster according to the unadjusted\n" +
                "node density (clustering coefficient) after the cluster has already been defined by the algorithm.\n" +
                "This allows clusters to slightly overlap at their edges. This parameter is only valid if fluffing\n" +
                "is turned on. A higher value will expand the cluster more.";
        fluffNodeDensityCutOffFormattedTextField.setToolTipText(tipText4);
        fluffNodeDensityCutOffFormattedTextField.setText((new Double(currentParamsCopy.getFluffNodeDensityCutOff()).toString()));
        fluffNodeDensityCutOffFormattedTextField.setEnabled(currentParamsCopy.isFluff());
        JLabel fluffNodeDensityCutOffLabel = new JLabel("Fluff Node Density Cutoff");
        JPanel labelFieldPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        labelFieldPanel4.setToolTipText(tipText4);
        labelFieldPanel4.add(fluffNodeDensityCutOffLabel);
        labelFieldPanel4.add(fluffNodeDensityCutOffFormattedTextField);
        panel.add(labelFieldPanel4);

        fluffCheckBox = new JCheckBox("Fluff", false) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        fluffCheckBox.addItemListener(new MCODEMainPanel.fluffCheckBoxAction());
        fluffCheckBox.setToolTipText("If checked, MCODE will fluff clusters\n" +
                "(expand core cluster one neighbour shell outwards according to fluff\n" +
                "density cutoff). This is done after the optional haircut step.");
        fluffCheckBox.setSelected(currentParamsCopy.isFluff());
        panel.add(fluffCheckBox);

        //mainOptionsPanel.setBorder(BorderFactory.createTitledBorder("Main Options"));
        //fluffOptionsPanel.setBorder(BorderFactory.createTitledBorder("Fluff Options"));
        //mainOptionsPanel.add(mainOptionsSubPanel, BorderLayout.NORTH);
        //mainOptionsPanel.add(fluffOptionsPanel, BorderLayout.SOUTH);
        //findPanel.add(mainOptionsPanel, BorderLayout.NORTH);

        maxDepthFormattedTextField = new JFormattedTextField(decFormat) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        maxDepthFormattedTextField.setColumns(3);
        maxDepthFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.formattedTextFieldAction());
        String tipText5 = "Sets the maximum depth from a seed node in the network to search to expand a cluster.\n" +
                "By default this is set to an arbitrarily large number. Set this to a small number\n" +
                "to limit cluster size.";
        maxDepthFormattedTextField.setToolTipText(tipText5);
        maxDepthFormattedTextField.setText((new Integer(currentParamsCopy.getMaxDepthFromStart()).toString()));
        JLabel maxDepthLabel = new JLabel("Max. Depth");
        JPanel labelFieldPanel5 = new JPanel() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        labelFieldPanel5.setToolTipText(tipText5);
        labelFieldPanel5.add(maxDepthLabel);
        labelFieldPanel5.add(maxDepthFormattedTextField);

        panel.add(labelFieldPanel5);

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     *
     * @param component description
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createOptimizeClusterFindingPanel(JComponent component) {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel(component);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     *
     * @return panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton OKButton = new JButton("Analyze");
        OKButton.addActionListener(new MCODEMainPanel.OKAction(this));

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new MCODEMainPanel.cancelAction(this));

        panel.add(OKButton);
        panel.add(quitButton);

        return panel;
    }

    /**
     * Saves the currently set parameters
     */
    private void saveParams() {
        MCODECurrentParameters.getInstance().setParams(currentParamsCopy);
    }

    /**
     * Action for the OK button (saves parameters)
     */
    private class OKAction extends AbstractAction {
        private JPanel dialog;

        OKAction(JPanel popup) {
            super();
            this.dialog = popup;
        }

        public void actionPerformed(ActionEvent e) {
            saveParams();
            //dialog.dispose();
        }
    }

    /**
     * Action for the cancel button (does not save parameters)
     */
    private class cancelAction extends AbstractAction {
        private JPanel dialog;

        cancelAction(JPanel popup) {
            super();
            this.dialog = popup;
        }

        public void actionPerformed(ActionEvent e) {
            //dialog.dispose();
        }
    }

    /**
     * Handles setting of the include loops parameter
     */
    private class includeLoopsCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setIncludeLoops(false);
            } else {
                currentParamsCopy.setIncludeLoops(true);
            }
        }
    }

    /**
     * Handles setting for the text field parameters that are numbers.
     * Makes sure that the numbers make sense.
     */
    private class formattedTextFieldAction implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            Object source = e.getSource();
            if (source == degreeCutOffFormattedTextField) {
                Number value = (Number) degreeCutOffFormattedTextField.getValue();
                if ((value != null) && (value.intValue() > 1)) {
                    currentParamsCopy.setDegreeCutOff(value.intValue());
                }
            } else if (source == maxDepthFormattedTextField) {
                Number value = (Number) maxDepthFormattedTextField.getValue();
                if ((value != null) && (value.intValue() > 0)) {
                    currentParamsCopy.setMaxDepthFromStart(value.intValue());
                }
            } else if (source == nodeScoreCutOffFormattedTextField) {
                Number value = (Number) nodeScoreCutOffFormattedTextField.getValue();
                if ((value != null) && (value.doubleValue() >= 0.0)) {
                    currentParamsCopy.setNodeScoreCutOff(value.doubleValue());
                }
            } else if (source == fluffNodeDensityCutOffFormattedTextField) {
                Number value = (Number) fluffNodeDensityCutOffFormattedTextField.getValue();
                if ((value != null) && (value.doubleValue() >= 0.0)) {
                    currentParamsCopy.setFluffNodeDensityCutOff(value.doubleValue());
                }
            }
        }
    }

    /**
     * Handles setting of the haircut parameter
     */
    private class haircutCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setHaircut(false);
            } else {
                currentParamsCopy.setHaircut(true);
            }
        }
    }

    /**
     * Handles setting of the fluff parameter
     */
    private class fluffCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setFluff(false);
            } else {
                currentParamsCopy.setFluff(true);
            }
            fluffNodeDensityCutOffFormattedTextField.setEnabled(currentParamsCopy.isFluff());
        }
    }

    /**
     * Handles setting of the preprocess network parameter
     */
    private class processCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setPreprocessNetwork(false);
            } else {
                currentParamsCopy.setPreprocessNetwork(true);
            }
        }
    }
}
