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
    MCODEParameterSet currentParamsCopy; // stores current parameters - populates panel fields

    DecimalFormat decFormat; // used in the formatted text fields

    //Scope
    public final static String NETWORK = "network";
    public final static String NODE = "node";
    public final static String NODE_SET = "node set";
    public String scope = NETWORK;

    MCODECollapsablePanel clusterFindingPanel;
    JPanel clusterFindingPanelForNetworkScope;
    JPanel clusterFindingPanelForNodeScope;

    //resetable UI elements

    //Scoring
    JCheckBox includeLoopsCheckBox;
    JFormattedTextField degreeCutOffFormattedTextField;
    JFormattedTextField kCoreFormattedTextField;
    //cluster finding
    JRadioButton optimizeOption; // only for network scope
    JRadioButton customizeOption;
    JCheckBox preprocessCheckBox; // only for node and node set scopes
    JCheckBox haircutCheckBox;
    JCheckBox fluffCheckBox;
    JFormattedTextField fluffNodeDensityCutOffFormattedTextField;
    JFormattedTextField maxDepthFormattedTextField;
    //TODO: remove the node score cutoff perameter from here
    //JFormattedTextField nodeScoreCutOffFormattedTextField;


    /**
     * The actual parameter change dialog that builds the UI
     */
    public MCODEMainPanel() {
        setLayout(new BorderLayout());

        //get the current parameters
        currentParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy();

        decFormat = new DecimalFormat();
        decFormat.setParseIntegerOnly(true);

        //create the three main panels: scope, advanced options, and bottom
        JPanel scopePanel = createScopePanel();
        MCODECollapsablePanel advancedOptionsPanel = createAdvancedOptionsPanel();
        JPanel bottomPanel = createBottomPanel();

        //Since the advanced options panel is being added to the center of this border layout
        //it will stretch it's height to fit the main panel.  To prevent this we create an
        //additional border layout panel and add advanced options to it's north compartment
        JPanel advancedOptionsContainer = new JPanel(new BorderLayout());
        advancedOptionsContainer.add(advancedOptionsPanel, BorderLayout.NORTH);

        //Add all the vertically alligned components to the main panel
        add(scopePanel, BorderLayout.NORTH);
        add(advancedOptionsContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a JPanel containing scope radio buttons
     * @return panel containing the scope option buttons
     */
    private JPanel createScopePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Scope"));

        JLabel scopeStarter = new JLabel("Find cluster(s)...");
        JRadioButton scopeNetwork = new JRadioButton("in Whole Network", scope.equals(NETWORK));
        JRadioButton scopeNode = new JRadioButton("from Selected Node", scope.equals(NODE));
        JRadioButton scopeNodeSet = new JRadioButton("from Selected Node Set", scope.equals(NODE_SET));

        scopeNetwork.setActionCommand(NETWORK);
        scopeNode.setActionCommand(NODE);
        scopeNodeSet.setActionCommand(NODE_SET);

        scopeNetwork.addActionListener(new scopeAction());
        scopeNode.addActionListener(new scopeAction());
        scopeNodeSet.addActionListener(new scopeAction());

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
     * Creates a collapsable panel that holds 2 other collapsable panels for network scoring and cluster finding parameter inputs
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createAdvancedOptionsPanel() {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel("Advanced Options");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Network scoring collapsable panel
        MCODECollapsablePanel networkScoringPanel = createNetworkScoringPanel();

        //Cluster finding collapsable panel
        clusterFindingPanel = createClusterFindingPanel();

        panel.add(networkScoringPanel);
        panel.add(clusterFindingPanel);

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a collapsable panel that holds network scoring parameter inputs
     * @return panel containing the network scoring parameter inputs
     */
    private MCODECollapsablePanel createNetworkScoringPanel() {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel("Network Scoring");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        //Include loops input
        JLabel includeLoopsLabel = new JLabel("Include Loops");
        includeLoopsCheckBox = new JCheckBox() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        includeLoopsCheckBox.addItemListener(new MCODEMainPanel.includeLoopsCheckBoxAction());
        String includeLoopsTip = "If checked, MCODE will include loops (self-edges) in the neighborhood\n" +
                "density calculation.  This is expected to make a small difference in the results.";
        includeLoopsCheckBox.setToolTipText(includeLoopsTip);
        includeLoopsCheckBox.setSelected(currentParamsCopy.isIncludeLoops());

        JPanel includeLoopsPanel = new JPanel() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        includeLoopsPanel.setLayout(new BorderLayout());
        includeLoopsPanel.setToolTipText(includeLoopsTip);

        includeLoopsPanel.add(includeLoopsLabel, BorderLayout.WEST);
        includeLoopsPanel.add(includeLoopsCheckBox, BorderLayout.EAST);

        //Degree cutoff input
        JLabel degreeCutOffLabel = new JLabel("Degree Cutoff");
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

        JPanel degreeCutOffPanel = new JPanel() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        degreeCutOffPanel.setLayout(new BorderLayout());
        degreeCutOffPanel.setToolTipText(degreeCutOffTip);

        degreeCutOffPanel.add(degreeCutOffLabel, BorderLayout.WEST);
        degreeCutOffPanel.add(degreeCutOffFormattedTextField, BorderLayout.EAST);

        //K-Core input
        JLabel kCoreLabel = new JLabel("K-Core");
        kCoreFormattedTextField = new JFormattedTextField(decFormat) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        kCoreFormattedTextField.setColumns(3);
        kCoreFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.formattedTextFieldAction());
        String kCoreTip = "WRITE ME PLEASE!";
        kCoreFormattedTextField.setToolTipText(kCoreTip);
        kCoreFormattedTextField.setText((new Integer(currentParamsCopy.getKCore()).toString()));

        JPanel kCorePanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        kCorePanel.setToolTipText(kCoreTip);

        kCorePanel.add(kCoreLabel, BorderLayout.WEST);
        kCorePanel.add(kCoreFormattedTextField, BorderLayout.EAST);

        //add the components to the panel
        panel.add(includeLoopsPanel);
        panel.add(degreeCutOffPanel);
        panel.add(kCorePanel);

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a collapsable panel that holds 2 other collapsable panels for either customizing or optimized cluster finding parameters
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createClusterFindingPanel() {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel("Cluster Finding");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        customizeOption = new JRadioButton("Customize", true);
        optimizeOption = new JRadioButton("Optimize");
        ButtonGroup clusterFindingOptions = new ButtonGroup();
        clusterFindingOptions.add(customizeOption);
        clusterFindingOptions.add(optimizeOption);

        //customize parameters panel
        MCODECollapsablePanel customizeClusterFindingPanel = createCustomizeClusterFindingPanel(customizeOption);
        //optimize parameters panel
        MCODECollapsablePanel optimizeClusterFindingPanel = createOptimizeClusterFindingPanel(optimizeOption);

        panel.add(customizeClusterFindingPanel);
        panel.add(optimizeClusterFindingPanel);
        this.clusterFindingPanelForNetworkScope = panel;
        
        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }
    /**
     * Creates a collapsable panel that holds cluster finding parameter inputs, placed within the cluster finding collapsable panel
     * @param component Any JComponent that may appear in the titled border of the panel
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createCustomizeClusterFindingPanel(JRadioButton component) {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel(component);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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

        //Preprocess (only in second (and 3rd?) scope - from seed)
        JLabel preprocessLabel = new JLabel("Preprocess network");
        preprocessCheckBox = new JCheckBox() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        preprocessCheckBox.addItemListener(new MCODEMainPanel.preprocessCheckBoxAction());
        String preprocessTip = "If checked, MCODE will limit cluster expansion to the\n" +
                "direct neighborhood of the spawning node.  If unchecked, the cluster will be allowed\n" +
                "to branch out to denser regions of the network.";
        preprocessCheckBox.setToolTipText(preprocessTip);
        preprocessCheckBox.setSelected(currentParamsCopy.isPreprocessNetwork());

        JPanel preprocessPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        preprocessPanel.setToolTipText(preprocessTip);

        preprocessPanel.add(preprocessLabel, BorderLayout.WEST);
        preprocessPanel.add(preprocessCheckBox, BorderLayout.EAST);

        preprocessPanel.setVisible(!scope.equals(NETWORK));

        //Haircut Input
        JLabel haircutLabel = new JLabel("Haircut");
        haircutCheckBox = new JCheckBox() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        haircutCheckBox.addItemListener(new MCODEMainPanel.haircutCheckBoxAction());
        String haircutTip = "If checked, MCODE will give clusters a haircut\n" +
                "(remove singly connected nodes).";
        haircutCheckBox.setToolTipText(haircutTip);
        haircutCheckBox.setSelected(currentParamsCopy.isHaircut());

        JPanel haircutPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        haircutPanel.setToolTipText(haircutTip);

        haircutPanel.add(haircutLabel, BorderLayout.WEST);
        haircutPanel.add(haircutCheckBox, BorderLayout.EAST);

        //Fluff Input
        JLabel fluffLabel = new JLabel("Fluff");
        fluffCheckBox = new JCheckBox() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        fluffCheckBox.addItemListener(new MCODEMainPanel.fluffCheckBoxAction());
        String fluffTip = "If checked, MCODE will fluff clusters\n" +
                "(expand core cluster one neighbour shell outwards according to fluff\n" +
                "density cutoff). This is done after the optional haircut step.";
        fluffCheckBox.setToolTipText(fluffTip);
        fluffCheckBox.setSelected(currentParamsCopy.isFluff());

        JPanel fluffPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        fluffPanel.setToolTipText(fluffTip);

        fluffPanel.add(fluffLabel, BorderLayout.WEST);
        fluffPanel.add(fluffCheckBox, BorderLayout.EAST);

        //Fluff node density cutoff input
        JLabel fluffNodeDensityCutOffLabel = new JLabel("   Node Density Cutoff");
        //fluffNodeDensityCutOffLabel.setEnabled(currentParamsCopy.isFluff());
        fluffNodeDensityCutOffFormattedTextField = new JFormattedTextField(new DecimalFormat("0.000")) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        fluffNodeDensityCutOffFormattedTextField.setColumns(3);
        fluffNodeDensityCutOffFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.formattedTextFieldAction());
        String fluffNodeDensityCutoffTip = "Sets the fluff density cutoff for expanding a cluster according to the unadjusted\n" +
                "node density (clustering coefficient) after the cluster has already been defined by the algorithm.\n" +
                "This allows clusters to slightly overlap at their edges. This parameter is only valid if fluffing\n" +
                "is turned on. A higher value will expand the cluster more.";
        fluffNodeDensityCutOffFormattedTextField.setToolTipText(fluffNodeDensityCutoffTip);
        fluffNodeDensityCutOffFormattedTextField.setText((new Double(currentParamsCopy.getFluffNodeDensityCutOff()).toString()));
        //fluffNodeDensityCutOffFormattedTextField.setEnabled(currentParamsCopy.isFluff());

        JPanel fluffNodeDensityCutOffPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        fluffNodeDensityCutOffPanel.setToolTipText(fluffNodeDensityCutoffTip);

        fluffNodeDensityCutOffPanel.add(fluffNodeDensityCutOffLabel, BorderLayout.WEST);
        fluffNodeDensityCutOffPanel.add(fluffNodeDensityCutOffFormattedTextField, BorderLayout.EAST);

        fluffNodeDensityCutOffPanel.setVisible(currentParamsCopy.isFluff());

        //Max depth input
        JLabel maxDepthLabel = new JLabel("Max. Depth");
        maxDepthFormattedTextField = new JFormattedTextField(decFormat) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        maxDepthFormattedTextField.setColumns(3);
        maxDepthFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.formattedTextFieldAction());
        String maxDepthTip = "Sets the maximum depth from a seed node in the network to search to expand a cluster.\n" +
                "By default this is set to an arbitrarily large number. Set this to a small number\n" +
                "to limit cluster size.";
        maxDepthFormattedTextField.setToolTipText(maxDepthTip);
        maxDepthFormattedTextField.setText((new Integer(currentParamsCopy.getMaxDepthFromStart()).toString()));

        JPanel maxDepthPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        maxDepthPanel.setToolTipText(maxDepthTip);

        maxDepthPanel.add(maxDepthLabel, BorderLayout.WEST);
        maxDepthPanel.add(maxDepthFormattedTextField, BorderLayout.EAST);

        //Add all inputs to the panel
        panel.add(preprocessPanel);
        panel.add(haircutPanel);
        panel.add(fluffPanel);
        panel.add(fluffNodeDensityCutOffPanel);
        panel.add(maxDepthPanel);
        this.clusterFindingPanelForNodeScope = panel;

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a collapsable panel that holds a benchmark file input, placed within the cluster finding collapsable panel
     * @param component Any JComponent that may appear in the titled border of the panel
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createOptimizeClusterFindingPanel(JRadioButton component) {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel(component);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel benchmarkStarter = new JLabel("Benchmark file location");

        JPanel benchmarkStarterPanel = new JPanel(new BorderLayout());
        benchmarkStarterPanel.add(benchmarkStarter, BorderLayout.WEST);

        JFormattedTextField benchmarkFileLocation = new JFormattedTextField();
        JButton browseButton = new JButton("Browse...");

        JPanel fileChooserPanel = new JPanel(new BorderLayout());
        fileChooserPanel.add(benchmarkFileLocation, BorderLayout.CENTER);
        fileChooserPanel.add(browseButton, BorderLayout.EAST);

        panel.add(benchmarkStarterPanel);
        panel.add(fileChooserPanel);

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a panel that holds buttons at the bottom of the main panel (analyze, quit)
     * @return panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(new MCODEScoreAndFindAction(currentParamsCopy));

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new MCODEMainPanel.quitAction());

        panel.add(analyzeButton);
        panel.add(quitButton);

        return panel;
    }

    /**
     * Makes sure that appropriate advanced options inputs are added and removed
     * depending on which scope is selected
     * TODO: the optimization panel should not simply be removed, should consider a different cluster finding panel structure alltogether
     */
    private class scopeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            scope = e.getActionCommand();
            if (scope.equals(NETWORK)) {
                //remove preprocess input
                preprocessCheckBox.getParent().setVisible(false);

                optimizeOption.getParent().getParent().setVisible(true);
                //clusterFindingPanel.getContentPane().remove(clusterFindingPanelForNodeScope);
                //clusterFindingPanel.getContentPane().add(clusterFindingPanelForNetworkScope, BorderLayout.NORTH);
            } else {
                //add preprocess input
                preprocessCheckBox.getParent().setVisible(true);

                optimizeOption.getParent().getParent().setVisible(false);
                //clusterFindingPanel.getContentPane().remove(clusterFindingPanelForNetworkScope);
                //clusterFindingPanel.getContentPane().add(clusterFindingPanelForNodeScope, BorderLayout.NORTH);
                customizeOption.setSelected(true);
            }
        }
    }

    private void optimizeToggle (boolean show) {
        if (show) {
            //optimizeOption.getParent().getParent().setVisible(false);
            clusterFindingPanel.getContentPane().remove(clusterFindingPanelForNetworkScope);
            clusterFindingPanel.getContentPane().add(clusterFindingPanelForNodeScope, BorderLayout.NORTH);
            customizeOption.setSelected(true);
        } else {
            //optimizeOption.getParent().getParent().setVisible(true);
            clusterFindingPanel.getContentPane().remove(clusterFindingPanelForNodeScope);
            clusterFindingPanel.getContentPane().add(clusterFindingPanelForNetworkScope, BorderLayout.NORTH);
        }
    }

    /**
     * Action for the quit button (does not save parameters)
     */
    private class quitAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            //dialog.dispose();
            //close all open panels
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
            } else if (source == kCoreFormattedTextField) {
                Number value = (Number) kCoreFormattedTextField.getValue();
                if ((value != null) && (value.intValue() > 2)) { //TODO: what should the lowest possible k-core be? -> for validation of input
                    currentParamsCopy.setKCore(value.intValue());
                }
            } else if (source == maxDepthFormattedTextField) {
                Number value = (Number) maxDepthFormattedTextField.getValue();
                if ((value != null) && (value.intValue() > 0)) {
                    currentParamsCopy.setMaxDepthFromStart(value.intValue());
                }/* TODO: remove node score cutoff action when this perameter is editable from another panel
            } else if (source == nodeScoreCutOffFormattedTextField) {
                Number value = (Number) nodeScoreCutOffFormattedTextField.getValue();
                if ((value != null) && (value.doubleValue() >= 0.0)) {
                    currentParamsCopy.setNodeScoreCutOff(value.doubleValue());
                }*/
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
     * Handles setting of the fluff parameter and showing or hiding of the fluff node density cutoff input
     */
    private class fluffCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setFluff(false);
            } else {
                currentParamsCopy.setFluff(true);
            }
            fluffNodeDensityCutOffFormattedTextField.getParent().setVisible(currentParamsCopy.isFluff());
            //fluffNodeDensityCutOffFormattedTextField.setEnabled(currentParamsCopy.isFluff());
            //fluffNodeDensityCutOffLabel.setEnabled(currentParamsCopy.isFluff());
        }
    }

    /**
     * Handles setting of the preprocess network parameter
     */
    private class preprocessCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setPreprocessNetwork(false);
            } else {
                currentParamsCopy.setPreprocessNetwork(true);
            }
        }
    }
}