package csplugins.mcode;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

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

    MCODEMainPanelAction trigger;

    DecimalFormat decFormat; // used in the formatted text fields

    //These are used to dynamically toggle the way cluster finding content is organized based
    //on the scope that is selected.  For network scope, the user has the option of using a
    //benchmark or customizing the parameters while for the other scopes, benchmarks are not
    //appropriate.
    MCODECollapsablePanel clusterFindingPanel;
    MCODECollapsablePanel customizeClusterFindingPanel;
    JPanel clusterFindingContent;
    JPanel customizeClusterFindingContent;

    //resetable UI elements

    //Scoring
    JCheckBox includeLoopsCheckBox;
    JFormattedTextField degreeCutOffFormattedTextField;
    JFormattedTextField kCoreFormattedTextField;
    JFormattedTextField nodeScoreCutoffFormattedTextField;
    //cluster finding
    JRadioButton optimizeOption; // only for network scope
    JRadioButton customizeOption;
    JCheckBox preprocessCheckBox; // only for node and node set scopes
    JCheckBox haircutCheckBox;
    JCheckBox fluffCheckBox;
    JFormattedTextField fluffNodeDensityCutOffFormattedTextField;
    JFormattedTextField maxDepthFormattedTextField;

    /**
     * The actual parameter change dialog that builds the UI
     *
     * @param trigger A reference to the action that triggered the initiation of this class
     */
    public MCODEMainPanel(MCODEMainPanelAction trigger) {
        this.trigger = trigger;
        setLayout(new BorderLayout());

        //get the current parameters
        currentParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy();
        currentParamsCopy.setDefaultParams();

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

        //TODO: REMOVE THIS WHEN BENCHMARKING IS IMPLEMENTED {
        //remove content with 2 options
        clusterFindingPanel.getContentPane().remove(clusterFindingContent);
        //add customize content
        clusterFindingPanel.getContentPane().add(customizeClusterFindingContent, BorderLayout.NORTH);
        //TODO: }
    }

    /**
     * Creates a JPanel containing scope radio buttons
     *
     * @return panel containing the scope option buttons
     */
    private JPanel createScopePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Scope"));

        JLabel scopeStarter = new JLabel("Find cluster(s)...");
        JRadioButton scopeNetwork = new JRadioButton("in Whole Network", currentParamsCopy.getScope().equals(MCODEParameterSet.NETWORK));
        JRadioButton scopeNode = new JRadioButton("from Selected Node", currentParamsCopy.getScope().equals(MCODEParameterSet.NODE));
        JRadioButton scopeNodeSet = new JRadioButton("from Selected Node Set", currentParamsCopy.getScope().equals(MCODEParameterSet.NODE_SET));

        scopeNetwork.setActionCommand(MCODEParameterSet.NETWORK);
        scopeNode.setActionCommand(MCODEParameterSet.NODE);
        scopeNodeSet.setActionCommand(MCODEParameterSet.NODE_SET);

        scopeNetwork.addActionListener(new ScopeAction());
        scopeNode.addActionListener(new ScopeAction());
        scopeNodeSet.addActionListener(new ScopeAction());

        ButtonGroup scopeOptions = new ButtonGroup();
        scopeOptions.add(scopeNetwork);
        scopeOptions.add(scopeNode);
        scopeOptions.add(scopeNodeSet);

        panel.add(scopeStarter);
        panel.add(scopeNetwork);
        panel.add(scopeNode);
        //panel.add(scopeNodeSet);//TODO: uncomment here when node set algorithm is complete
        
        return panel;
    }

    /**
     * Creates a collapsable panel that holds 2 other collapsable panels for network scoring and cluster finding parameter inputs
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
        clusterFindingPanel = createClusterFindingPanel();

        panel.add(networkScoringPanel);
        panel.add(clusterFindingPanel);

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a collapsable panel that holds network scoring parameter inputs
     *
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
        includeLoopsCheckBox.addItemListener(new MCODEMainPanel.IncludeLoopsCheckBoxAction());
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
        degreeCutOffFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
        String degreeCutOffTip = "Sets the degree cutoff below which a node will not be scored.\n" +
                "Nodes with a degree equal or higher to this value will be scored.\n" +
                "By default this is set to 2. Valid values are 2 or higher to prevent singly connected nodes\n" +
                "from getting an artificially high node score.";
        degreeCutOffFormattedTextField.setToolTipText(degreeCutOffTip);
        degreeCutOffFormattedTextField.setText((new Integer(currentParamsCopy.getDegreeCutoff()).toString()));

        JPanel degreeCutOffPanel = new JPanel() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        degreeCutOffPanel.setLayout(new BorderLayout());
        degreeCutOffPanel.setToolTipText(degreeCutOffTip);

        degreeCutOffPanel.add(degreeCutOffLabel, BorderLayout.WEST);
        degreeCutOffPanel.add(degreeCutOffFormattedTextField, BorderLayout.EAST);

        //add the components to the panel
        panel.add(includeLoopsPanel);
        panel.add(degreeCutOffPanel);

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a collapsable panel that holds 2 other collapsable panels for either customizing or optimized cluster finding parameters
     *
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createClusterFindingPanel() {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel("Cluster Finding");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        customizeOption = new JRadioButton("Customize", !currentParamsCopy.isOptimize());
        optimizeOption = new JRadioButton("Optimize", currentParamsCopy.isOptimize());
        ButtonGroup clusterFindingOptions = new ButtonGroup();
        clusterFindingOptions.add(customizeOption);
        clusterFindingOptions.add(optimizeOption);

        customizeOption.addActionListener(new ClusterFindingAction());
        optimizeOption.addActionListener(new ClusterFindingAction());

        //customize parameters panel
        customizeClusterFindingPanel = createCustomizeClusterFindingPanel(customizeOption);
        //optimize parameters panel
        MCODECollapsablePanel optimizeClusterFindingPanel = createOptimizeClusterFindingPanel(optimizeOption);

        panel.add(customizeClusterFindingPanel);
        panel.add(optimizeClusterFindingPanel);
        
        this.clusterFindingContent = panel;
        
        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a collapsable panel that holds cluster finding parameter inputs, placed within the cluster finding collapsable panel
     *
     * @param component Any JComponent that may appear in the titled border of the panel
     * @return collapsablePanel
     */
    private MCODECollapsablePanel createCustomizeClusterFindingPanel(JRadioButton component) {
        MCODECollapsablePanel collapsablePanel = new MCODECollapsablePanel(component);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Preprocess (only in second (and 3rd?) scope - from seed)
        JLabel preprocessLabel = new JLabel("Preprocess network");
        preprocessCheckBox = new JCheckBox() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        preprocessCheckBox.addItemListener(new MCODEMainPanel.PreprocessCheckBoxAction());
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

        preprocessPanel.setVisible(!currentParamsCopy.getScope().equals(MCODEParameterSet.NETWORK));

        //Node Score Cutoff
        JLabel nodeScoreCutoffLabel = new JLabel("Node Score Cutoff");
        nodeScoreCutoffFormattedTextField = new JFormattedTextField(new DecimalFormat("0.000")) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        nodeScoreCutoffFormattedTextField.setColumns(3);
        nodeScoreCutoffFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
        String nodeScoreCutoffTip = "Sets the node score cutoff for expanding a cluster as a percentage from the seed node score.\n" +
                "This is the most important parameter to control the size of MCODE clusters,\n" +
                "with smaller values creating smaller clusters.";
        nodeScoreCutoffFormattedTextField.setToolTipText(nodeScoreCutoffTip);
        nodeScoreCutoffFormattedTextField.setText((new Double(currentParamsCopy.getNodeScoreCutoff()).toString()));

        JPanel nodeScoreCutoffPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        nodeScoreCutoffPanel.setToolTipText(nodeScoreCutoffTip);

        nodeScoreCutoffPanel.add(nodeScoreCutoffLabel, BorderLayout.WEST);
        nodeScoreCutoffPanel.add(nodeScoreCutoffFormattedTextField, BorderLayout.EAST);

        //K-Core input
        JLabel kCoreLabel = new JLabel("K-Core");
        kCoreFormattedTextField = new JFormattedTextField(decFormat) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        kCoreFormattedTextField.setColumns(3);
        kCoreFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
        String kCoreTip = "WRITE ME PLEASE!";//TODO: Write k-core tooltip
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

        //Haircut Input
        JLabel haircutLabel = new JLabel("Haircut");
        haircutCheckBox = new JCheckBox() {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        haircutCheckBox.addItemListener(new MCODEMainPanel.HaircutCheckBoxAction());
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
        fluffCheckBox.addItemListener(new MCODEMainPanel.FluffCheckBoxAction());
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
        fluffNodeDensityCutOffFormattedTextField = new JFormattedTextField(new DecimalFormat("0.000")) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        fluffNodeDensityCutOffFormattedTextField.setColumns(3);
        fluffNodeDensityCutOffFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
        String fluffNodeDensityCutoffTip = "Sets the fluff density cutoff for expanding a cluster according to the unadjusted\n" +
                "node density (clustering coefficient) after the cluster has already been defined by the algorithm.\n" +
                "This allows clusters to slightly overlap at their edges. This parameter is only valid if fluffing\n" +
                "is turned on. A higher value will expand the cluster more.";
        fluffNodeDensityCutOffFormattedTextField.setToolTipText(fluffNodeDensityCutoffTip);
        fluffNodeDensityCutOffFormattedTextField.setText((new Double(currentParamsCopy.getFluffNodeDensityCutoff()).toString()));

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
        maxDepthFormattedTextField.addPropertyChangeListener("value", new MCODEMainPanel.FormattedTextFieldAction());
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
        panel.add(nodeScoreCutoffPanel);
        panel.add(kCorePanel);
        panel.add(maxDepthPanel);

        this.customizeClusterFindingContent = panel;

        collapsablePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsablePanel;
    }

    /**
     * Creates a collapsable panel that holds a benchmark file input, placed within the cluster finding collapsable panel
     *
     * @param component the radio button that appears in the titled border of the panel
     * @return A collapsable panel holding a file selection input
     * @see MCODECollapsablePanel
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
     * Utility method that creates a panel for buttons at the bottom of the <code>MCODEMainPanel</code>
     *
     * @return a flow layout panel containing the analyze and quite buttons
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(new MCODEScoreAndFindAction(currentParamsCopy));

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new MCODEMainPanel.QuitAction(this));

        panel.add(analyzeButton);
        panel.add(quitButton);

        return panel;
    }

    /**
     * Handles the press of a scope option. Makes sure that appropriate advanced options
     * inputs are added and removed depending on which scope is selected
     */
    private class ScopeAction extends AbstractAction {
        /*
        //TODO: UNCOMMENT THIS ACTION EVENT HANDLER WHEN BENCHMARKING IS IMPLEMENTED, and delete the one below
        public void actionPerformed(ActionEvent e) {
            String scope = e.getActionCommand();
            if (scope.equals(MCODEParameterSet.NETWORK)) {
                //remove preprocess input
                preprocessCheckBox.getParent().setVisible(false);

                //We want to have a layered structure such that when network scope is selected, the cluster finding
                //content allows the user to choose between optimize and customize.  When the other scopes are selected
                //the user should only see the customize cluster parameters content.
                //Here we ensured that these two contents are toggled depending on the scope selection.
                clusterFindingPanel.getContentPane().remove(customizeClusterFindingContent);
                //add content with 2 options
                clusterFindingPanel.getContentPane().add(clusterFindingContent, BorderLayout.NORTH);
                //need to re-add the customize content to its original container
                customizeClusterFindingPanel.getContentPane().add(customizeClusterFindingContent, BorderLayout.NORTH);
            } else {
                //add preprocess input
                preprocessCheckBox.getParent().setVisible(true);
                //since only one option will be left, it must be selected so that its content is visible
                customizeOption.setSelected(true);
                //remove content with 2 options
                clusterFindingPanel.getContentPane().remove(clusterFindingContent);
                //add customize content; this automatically removes it from its original container
                clusterFindingPanel.getContentPane().add(customizeClusterFindingContent, BorderLayout.NORTH);


            }
            currentParamsCopy.setScope(scope);
        }
        */
        //TODO: DELETE THIS WHEN BENCHMARKING IS IMPLEMENTED {
        //TEMPORARY ACTION EVENT HANDLER
        public void actionPerformed(ActionEvent e) {
            String scope = e.getActionCommand();
            if (scope.equals(MCODEParameterSet.NETWORK)) {
                //remove preprocess input
                preprocessCheckBox.getParent().setVisible(false);
            } else {
                //add preprocess input
                preprocessCheckBox.getParent().setVisible(true);
            }
            currentParamsCopy.setScope(scope);
        }
    }

    /**
     * Sets the optimization parameter depending on which radio button is selected (cusomize/optimize)
     */
    private class ClusterFindingAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (optimizeOption.isSelected()) {
                currentParamsCopy.setOptimize(true);
            } else {
                currentParamsCopy.setOptimize(false);
            }
        }
    }

    /**
     * Handles the press of the quit button
     */
    private class QuitAction extends AbstractAction {
        MCODEMainPanel mainPanel;
        MCODEResultsPanel component;

        /**
         *
         * @param mainPanel
         */
        QuitAction (MCODEMainPanel mainPanel) {
            this.mainPanel = mainPanel;
        }

        public void actionPerformed(ActionEvent e) {
            //close all open panels
            CytoscapeDesktop desktop = Cytoscape.getDesktop();
            //we want to ask the user if they want to dispose of their results, if all results are closed then we
            //will also close the main panel, otherwise, as long as there are results open, the main panel will
            //stay open as well
            boolean resultsClosed = true;

            CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
            for (int c = cytoPanel.getCytoPanelComponentCount() - 1; c >= 0; c--) {
                cytoPanel.setSelectedIndex(c);
                Component component = cytoPanel.getSelectedComponent();
                String componentTitle;
                if (component instanceof MCODEResultsPanel) {
                    this.component = (MCODEResultsPanel) component;
                    componentTitle = this.component.getResultsTitle();
                    String message = "You are about to dispose of " + componentTitle + ".\nDo you wish to continue?";
                    int result = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), new Object[] {message}, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (result == JOptionPane.YES_OPTION){
                        cytoPanel.remove(component);
                    } else {
                        resultsClosed = false;
                    }
                }
            }
            if (cytoPanel.getCytoPanelComponentCount() == 0) {
                cytoPanel.setState(CytoPanelState.HIDE);
            }
            if (resultsClosed) {
                cytoPanel = desktop.getCytoPanel(SwingConstants.WEST);
                cytoPanel.remove(mainPanel);
                trigger.setOpened(false);
            }
        }
    }

    /**
     * Handles setting of the include loops parameter
     */
    private class IncludeLoopsCheckBoxAction implements ItemListener {
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
    private class FormattedTextFieldAction implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            JFormattedTextField source = (JFormattedTextField) e.getSource();

            String message = "The value you have entered is invalid.\n";
            boolean invalid = false;
            
            if (source == degreeCutOffFormattedTextField) {
                Number value = (Number) degreeCutOffFormattedTextField.getValue();
                if ((value != null) && (value.intValue() > 1)) {
                    currentParamsCopy.setDegreeCutoff(value.intValue());
                } else {
                    source.setValue(new Integer (2));
                    message += "The degree cutoff must be greater than 1.";
                    invalid = true;
                }
            } else if (source == nodeScoreCutoffFormattedTextField) {
                Number value = (Number) nodeScoreCutoffFormattedTextField.getValue();
                if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1.0)) {
                    currentParamsCopy.setNodeScoreCutoff(value.doubleValue());
                } else {
                    source.setValue(new Double (currentParamsCopy.getNodeScoreCutoff()));
                    message += "The node score cutoff must be between 0 and 1.";
                    invalid = true;
                }
            } else if (source == kCoreFormattedTextField) {
                Number value = (Number) kCoreFormattedTextField.getValue();
                if ((value != null) && (value.intValue() > 1)) {
                    currentParamsCopy.setKCore(value.intValue());
                } else {
                    source.setValue(new Integer (2));
                    message += "The K-Core must be greater than 1.";
                    invalid = true;
                }
            } else if (source == maxDepthFormattedTextField) {
                Number value = (Number) maxDepthFormattedTextField.getValue();
                if ((value != null) && (value.intValue() > 0)) {
                    currentParamsCopy.setMaxDepthFromStart(value.intValue());
                } else {
                    source.setValue(new Integer (1));
                    message += "The maximum depth must be greater than 0.";
                    invalid = true;
                }
            } else if (source == fluffNodeDensityCutOffFormattedTextField) {
                Number value = (Number) fluffNodeDensityCutOffFormattedTextField.getValue();
                if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1.0)) {
                    currentParamsCopy.setFluffNodeDensityCutoff(value.doubleValue());
                } else {
                    source.setValue(new Double (currentParamsCopy.getFluffNodeDensityCutoff()));
                    message += "The fluff node density cutoff must be between 0 and 1.";
                    invalid = true;
                }
            }
            if (invalid) {
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
            }
        }
    }

    /**
     * Handles setting of the haircut parameter
     */
    private class HaircutCheckBoxAction implements ItemListener {
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
    private class FluffCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setFluff(false);
            } else {
                currentParamsCopy.setFluff(true);
            }
            fluffNodeDensityCutOffFormattedTextField.getParent().setVisible(currentParamsCopy.isFluff());
        }
    }

    /**
     * Handles setting of the preprocess network parameter
     */
    private class PreprocessCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setPreprocessNetwork(false);
            } else {
                currentParamsCopy.setPreprocessNetwork(true);
            }
        }
    }
}