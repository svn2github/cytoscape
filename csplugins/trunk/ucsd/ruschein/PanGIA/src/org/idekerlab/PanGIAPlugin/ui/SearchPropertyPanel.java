package org.idekerlab.PanGIAPlugin.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;

import org.idekerlab.PanGIAPlugin.SearchParameters;
import org.idekerlab.PanGIAPlugin.SearchTask;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.attr.MultiHashMapDefinitionListener;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyHelpBroker;
import cytoscape.view.cytopanels.CytoPanel;
import org.idekerlab.PanGIAPlugin.ScalingMethodX;
import org.idekerlab.PanGIAPlugin.utilities.CyCollapsiblePanel;

import com.lowagie.text.Font;


/**
 * @author kono, ghannum
 * 
 * 5/25/10: Removed restrictions on machine-generated sections. (Greg) 
 */
public class SearchPropertyPanel extends JPanel implements MultiHashMapDefinitionListener, PropertyChangeListener {
	private static final long serialVersionUID = -3352470909434196700L;

	private static final double DEF_ALPHA = 1.6;
	private static final double DEF_ALPHA_MUL = 0.0;
	private static final double DEF_COMPLEX_REWARD = 0;
	private static final String DEF_DEGREE = "2";
	private static final double DEF_CUTOFF = 20.0;
	private static final double DEF_PVALUE_THRESHOLD = 90;
	private static final int DEF_NUMBER_OF_SAMPLES = 1000;
	private static final String DEFAULT_ATTRIBUTE = "none";
	
	private Container container;
	private SearchParameters parameters;


	/** Creates new form SearchPropertyPanel */
	public SearchPropertyPanel() {
		initComponents(); // the main panel
		initComponents2(); // parameter panel

		final ItemListener updateSearchButton = new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					updateSearchButtonState();
				}
			};
		physicalEdgeAttribComboBox.addItemListener(updateSearchButton);
		geneticEdgeAttribComboBox.addItemListener(updateSearchButton);
		physicalNetworkPanel.addItemListener(updateSearchButton);
		geneticNetworkPanel.addItemListener(updateSearchButton);

		// Add parameter panel to collapsibilePanel
		CyCollapsiblePanel collapsiblePanel = new CyCollapsiblePanel("Advanced");
		collapsiblePanel.setToolTipText("Set advanced search options.");
		collapsiblePanel.getContentPane().add(pnlParameter);

		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;

		this.parameterPanel.add(collapsiblePanel, gridBagConstraints);
         
		// Set the button size the same
		closeButton.setPreferredSize(new java.awt.Dimension(75, 23));
		aboutButton.setPreferredSize(new java.awt.Dimension(75, 23));
		//helpButton.setPreferredSize(new java.awt.Dimension(75, 23));
		searchButton.setPreferredSize(new java.awt.Dimension(75, 23));
		 
		// about button is a place holder for now, hide it
		this.aboutButton.setVisible(true);
		
		Cytoscape.getEdgeAttributes().getMultiHashMapDefinition()
			.addDataDefinitionListener(this);

		updateAttributeLists();
		updateScalingMethods();
		updateFilteringOptions(null);

		// Set defaults
		//this.alphaTextField.setText(Double.toString(DEF_ALPHA));
		//this.alphaMultiplierTextField.setText(Double.toString(DEF_ALPHA_MUL));
		this.complexRewardTextField.setText(Double.toString(DEF_COMPLEX_REWARD));
		this.degreeTextField.setText(DEF_DEGREE);
		this.edgeFilterTextField.setText(Double.toString(DEF_PVALUE_THRESHOLD));
	
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
			this.updateAttributeLists();
	}
	
	public SearchParameters getParameters() {
		return parameters;
	}

                       
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        topPane = new javax.swing.JPanel();
        physicalEdgeLabel = new javax.swing.JLabel();
        physicalEdgeAttribComboBox = new javax.swing.JComboBox();
        lbPhysicalNetwork = new javax.swing.JLabel();
        lbPhysicalScale = new javax.swing.JLabel();
        phyScalingMethodComboBox = new javax.swing.JComboBox();
        edgeAttributePanel = new javax.swing.JPanel();
        geneticEdgeLabel = new javax.swing.JLabel();
        geneticEdgeAttribComboBox = new javax.swing.JComboBox();
        lbGeneticNetwork = new javax.swing.JLabel();
        lbGeneticScale = new javax.swing.JLabel();
        genScalingMethodComboBox = new javax.swing.JComboBox();
        parameterPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        //helpButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();

        parameterErrorTextArea= new JTextArea();
        
        setLayout(new java.awt.GridBagLayout());

        topPane.setLayout(new java.awt.GridBagLayout());

        topPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Physical Network"));
        topPane.setToolTipText("Specify information relating to the physical interaction network.");
        physicalEdgeLabel.setText("Attribute:");
        physicalEdgeLabel.setToolTipText("Choose an edge attribute representing the physical interaction scores.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        topPane.add(physicalEdgeLabel, gridBagConstraints);

        physicalEdgeAttribComboBox.setToolTipText("Choose an edge attribute representing the physical interaction scores.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        topPane.add(physicalEdgeAttribComboBox, gridBagConstraints);

        lbPhysicalNetwork.setText("Network:");
        lbPhysicalNetwork.setToolTipText("Choose a network which contains edges representing physical interactions.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 5);
        topPane.add(lbPhysicalNetwork, gridBagConstraints);

        physicalNetworkPanel.setComboBoxToolTip("Choose a network which contains edges representing physical interactions.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        topPane.add(physicalNetworkPanel, gridBagConstraints);

        lbPhysicalScale.setText("Scale:");
        lbPhysicalScale.setToolTipText("Choose an algorithm for scaling edge scores. Upper/lower refers to the numeric direction which will be regarded as more significant.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        topPane.add(lbPhysicalScale, gridBagConstraints);

        phyScalingMethodComboBox.setToolTipText("Choose an algorithm for scaling edge scores. Upper/lower refers to the numeric direction which will be regarded as more significant.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        topPane.add(phyScalingMethodComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(topPane, gridBagConstraints);

        edgeAttributePanel.setLayout(new java.awt.GridBagLayout());
        edgeAttributePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Genetic Network"));
        edgeAttributePanel.setToolTipText("Specify information relating to the genetic interaction network.");

        geneticEdgeLabel.setText("Attribute:");
        geneticEdgeLabel.setToolTipText("Choose an edge attribute representing the genetic interaction scores.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        edgeAttributePanel.add(geneticEdgeLabel, gridBagConstraints);

        geneticEdgeAttribComboBox.setToolTipText("Choose an edge attribute representing the genetic interaction scores.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        geneticEdgeAttribComboBox.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	updateFilteringOptions(evt);
            }
        });
        
        edgeAttributePanel.add(geneticEdgeAttribComboBox, gridBagConstraints);

        lbGeneticNetwork.setText("Network:");
        lbGeneticNetwork.setToolTipText("Choose a network which contains edges representing genetic interactions.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        edgeAttributePanel.add(lbGeneticNetwork, gridBagConstraints);

        geneticNetworkPanel.setComboBoxToolTip("Choose a network which contains edges representing genetic interactions.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        edgeAttributePanel.add(geneticNetworkPanel, gridBagConstraints);

        lbGeneticScale.setText("Scale:");
        lbGeneticScale.setToolTipText("Choose an algorithm for scaling edge scores. Upper/lower refers to the numeric direction which will be regarded as more significant.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        edgeAttributePanel.add(lbGeneticScale, gridBagConstraints);

        genScalingMethodComboBox.setToolTipText("Choose an algorithm for scaling edge scores. Upper/lower refers to the numeric direction which will be regarded as more significant.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        edgeAttributePanel.add(genScalingMethodComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(edgeAttributePanel, gridBagConstraints);

        parameterPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(parameterPanel, gridBagConstraints);

        //ParamaterErrorLabel
        parameterErrorTextArea.setText("");
        parameterErrorTextArea.setWrapStyleWord(true);
        parameterErrorTextArea.setEditable(false);
        parameterErrorTextArea.setForeground(Color.blue);
        parameterErrorTextArea.setFont(parameterErrorTextArea.getFont().deriveFont(Font.BOLD));
        parameterErrorTextArea.setToolTipText("This issue must be addressed before a search can be performed.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(parameterErrorTextArea, gridBagConstraints);
        
        //Button panel
        buttonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        /*
        helpButton.setText("Help");
        helpButton.setToolTipText("Get help for PanGIA.");
        CyHelpBroker.getHelpBroker().enableHelpOnButton(helpButton, "Topic", null);

        buttonPanel.add(helpButton);
        */
        aboutButton.setText("About");
        aboutButton.setToolTipText("Learn more about PanGIA.");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(aboutButton);

        closeButton.setText("Close");
        closeButton.setToolTipText("Close the PanGIA plugin.");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(closeButton);

        searchButton.setText("Search");
        searchButton.setToolTipText("Perform a PanGIA search using the specified options.");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(searchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(buttonPanel, gridBagConstraints);

    }// </editor-fold>                        
    

	
    // The following UI is for parameter panel
    private void initComponents2() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlParameter = new javax.swing.JPanel();
        scorePanel = new javax.swing.JPanel();
        
        /*
        alphaLabel = new javax.swing.JLabel();
        alphaMultiplierLabel = new javax.swing.JLabel();
        alphaTextField = new javax.swing.JTextField();
        alphaMultiplierTextField = new javax.swing.JTextField();
        */
        
        degreeLabel = new javax.swing.JLabel();
        degreeTextField = new javax.swing.JTextField();
        
        complexRewardLabel = new JLabel();
        complexRewardSlider = new JSlider();
        complexRewardSliderLabels = new JLabel();
        complexRewardTextField = new JTextField();
        
        lbPlaceHolder1 = new javax.swing.JLabel();
        
        edgeFilteringPanel = new javax.swing.JPanel();
        pValueThresholdLabel = new javax.swing.JLabel();
        edgeFilterTextField = new javax.swing.JTextField();
        edgeFilterSlider = new JSlider();
        edgeFilterSliderLabels = new JLabel();
        lbPlaceHolder2 = new javax.swing.JLabel();
        lbPlaceHolder3 = new javax.swing.JLabel();
        trainingCheckBoxPhysical = new JCheckBox();
        trainingCheckBoxGenetic = new JCheckBox();
        trainingLabel = new JLabel();
        trainingLabelPhysical = new JLabel();
        trainingLabelGenetic = new JLabel();
        annotationCheckBox = new JCheckBox();
        annotationLabel = new JLabel();
        lbComplexFile = new javax.swing.JLabel();
        annotationAttribComboBox = new javax.swing.JComboBox();
        annotationThresholdLabel = new JLabel();
        annotationThresholdTextField = new JTextField();
        reportPathLabel = new javax.swing.JLabel();
        reportPathTextField = new javax.swing.JTextField();
        reportPathButton = new JButton();

        trainingPanel = new javax.swing.JPanel();
        reportPanel = new javax.swing.JPanel();
        
        pnlParameter.setLayout(new java.awt.GridBagLayout());
        
        final java.awt.event.KeyListener textFieldKeyListener = new java.awt.event.KeyListener() {
            public void keyPressed(java.awt.event.KeyEvent evt) {}
            public void keyTyped(java.awt.event.KeyEvent evt) {}
            public void keyReleased(java.awt.event.KeyEvent evt) {textFieldActionPerformed(evt);}
        };

        //ScorePanel
        scorePanel.setLayout(new java.awt.GridBagLayout());

        scorePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Parameters"));
        scorePanel.setToolTipText("Specify parameters relating to the search procedure.");
        
        complexRewardLabel.setText("Module size:");
        complexRewardLabel.setToolTipText("Module size parameters AB. (Module reward = AB * size ^ |AB|)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5,3, 20);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        scorePanel.add(complexRewardLabel, gridBagConstraints);
        
        complexRewardSliderLabels.setText("Smaller          Larger");
        complexRewardSliderLabels.setToolTipText(complexRewardLabel.getToolTipText());
        complexRewardSliderLabels.setFont(edgeFilterSliderLabels.getFont().deriveFont(10.0f));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5,3, 10);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        scorePanel.add(complexRewardSliderLabels, gridBagConstraints);
        
        complexRewardSlider.setToolTipText(complexRewardLabel.getToolTipText());
        complexRewardSlider.setPreferredSize(new java.awt.Dimension(100, 25));
        complexRewardSlider.setExtent(0);
        complexRewardSlider.setMinimum(0);
        complexRewardSlider.setMaximum(600);
        complexRewardSlider.setValue(300);
        complexRewardSlider.setMajorTickSpacing(100);
        complexRewardSlider.setMinorTickSpacing(100);
        complexRewardSlider.setPaintLabels(false);
        complexRewardSlider.setPaintTicks(false);
        complexRewardSlider.setPaintTrack(true);
        complexRewardSlider.setEnabled(true);
        complexRewardSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
            	complexRewardSliderMoved(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        scorePanel.add(complexRewardSlider, gridBagConstraints);
        
        complexRewardTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        complexRewardTextField.addKeyListener(textFieldKeyListener);
        complexRewardTextField.setToolTipText(complexRewardSlider.getToolTipText());
        complexRewardTextField.addKeyListener(new java.awt.event.KeyListener(){
        	public void keyPressed(java.awt.event.KeyEvent evt){
        	}
        	public void keyTyped(java.awt.event.KeyEvent evt){
        		
        	}
        	public void keyReleased(java.awt.event.KeyEvent evt){
        		complexRewardTextChanged(evt);
        	}
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        scorePanel.add(complexRewardTextField, gridBagConstraints);
        
        /*
        alphaLabel.setText("Alpha (Exponent):");
        alphaLabel.setToolTipText("The exponent for rewarding module size. (reward = multiplier * moduleSize^exponent)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5,3, 0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        scorePanel.add(alphaLabel, gridBagConstraints);

        alphaMultiplierLabel.setText("Beta (Multiplier):");
        alphaMultiplierLabel.setToolTipText("The multiplier for rewarding module size. (reward = multiplier * moduleSize^exponent)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        scorePanel.add(alphaMultiplierLabel, gridBagConstraints);

        alphaTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        alphaTextField.addKeyListener(textFieldKeyListener);
        alphaTextField.setToolTipText(alphaLabel.getToolTipText());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        scorePanel.add(alphaTextField, gridBagConstraints);

        alphaMultiplierTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        alphaMultiplierTextField.addKeyListener(textFieldKeyListener);
        alphaMultiplierTextField.setToolTipText(alphaMultiplierLabel.getToolTipText());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        scorePanel.add(alphaMultiplierTextField, gridBagConstraints);
         */
        
        degreeLabel.setText("Network filter degree (optional):");
        degreeLabel.setToolTipText("Remove nodes in the physical network which are distant from any node in the genetic network. The maximum distance allowed is the filter degree. (ex. 1 means the physical node, or any of its neighbors, must be present in the genetic network)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        scorePanel.add(degreeLabel, gridBagConstraints);
        
        degreeTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        degreeTextField.addKeyListener(textFieldKeyListener);
        degreeTextField.setToolTipText(degreeLabel.getToolTipText());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        scorePanel.add(degreeTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx=2;
        gridBagConstraints.weightx = 1.0;
        scorePanel.add(lbPlaceHolder1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlParameter.add(scorePanel, gridBagConstraints);

        //EdgeFilteringPanel
        edgeFilteringPanel.setLayout(new java.awt.GridBagLayout());
        edgeFilteringPanel.setToolTipText("Specify options for filtering PanGIA results.");
        edgeFilteringPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Reporting"));
        
        /*
        pValueThresholdLabel.setText("Percentile Threshold:");
        pValueThresholdLabel.setToolTipText("The percentile above which edges should be included in the results.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        edgeFilteringPanel.add(pValueThresholdLabel, gridBagConstraints);
         */
        
        edgeFilterSliderLabels.setText("More                    Less");
        edgeFilterSliderLabels.setToolTipText("Strength of the edge filter. (0 returns all edges)");
        edgeFilterSliderLabels.setFont(edgeFilterSliderLabels.getFont().deriveFont(12.0f));
        edgeFilterSliderLabels.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        edgeFilteringPanel.add(edgeFilterSliderLabels, gridBagConstraints);
        
        edgeFilterSlider.setToolTipText("Strength of the edge filter. (0 returns all edges)");
        edgeFilterSlider.setPreferredSize(new java.awt.Dimension(200, 25));
        edgeFilterSlider.setExtent(0);
        edgeFilterSlider.setMinimum(0);
        edgeFilterSlider.setMaximum(100);
        edgeFilterSlider.setValue(90);
        edgeFilterSlider.setMajorTickSpacing(10);
        edgeFilterSlider.setMinorTickSpacing(5);
        edgeFilterSlider.setPaintLabels(false);
        edgeFilterSlider.setPaintTicks(true);
        edgeFilterSlider.setPaintTrack(true);
        edgeFilterSlider.setEnabled(false);
        edgeFilterSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
            	edgeFilterSliderMoved(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 10);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        edgeFilteringPanel.add(edgeFilterSlider, gridBagConstraints);
        
        edgeFilterTextField.setToolTipText("Strength of the edge filter. (0 returns all edges)");
        edgeFilterTextField.addKeyListener(new java.awt.event.KeyListener(){
        	public void keyPressed(java.awt.event.KeyEvent evt){
        	}
        	public void keyTyped(java.awt.event.KeyEvent evt){
        		
        	}
        	public void keyReleased(java.awt.event.KeyEvent evt){
        		edgeFilterTextChanged(evt);
        	}
        });
        edgeFilterTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        edgeFilteringPanel.add(edgeFilterTextField, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        edgeFilteringPanel.add(lbPlaceHolder2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlParameter.add(edgeFilteringPanel, gridBagConstraints);

        
        //TrainingPanel
        trainingPanel.setLayout(new java.awt.GridBagLayout());
        trainingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Annotation"));
        trainingPanel.setToolTipText("Specify options for module training and annotation.");
        
        lbComplexFile.setText("Annotation attribute:");
        lbComplexFile.setToolTipText("Select the node attribute which provides annotation information.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        trainingPanel.add(lbComplexFile, gridBagConstraints);
        
        annotationAttribComboBox.setToolTipText(lbComplexFile.getToolTipText());
        annotationAttribComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	trainingComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        trainingPanel.add(annotationAttribComboBox, gridBagConstraints);
        
        trainingLabel.setText("Train PanGIA:");
        trainingLabel.setToolTipText("Train the edge attribute scores against a reference annotation.");
        trainingLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        trainingPanel.add(trainingLabel, gridBagConstraints);
        
        trainingCheckBoxPhysical.setSelected(false);
        trainingCheckBoxPhysical.setToolTipText(trainingLabel.getToolTipText());
        trainingCheckBoxPhysical.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        trainingPanel.add(trainingCheckBoxPhysical, gridBagConstraints);
        
        trainingLabelPhysical.setText("Physical");
        trainingLabelPhysical.setToolTipText("Train the edge attribute scores against a reference annotation.");
        trainingLabelPhysical.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 3, 0);
        trainingPanel.add(trainingLabelPhysical, gridBagConstraints);
        
        trainingCheckBoxGenetic.setSelected(false);
        trainingCheckBoxGenetic.setToolTipText(trainingLabel.getToolTipText());
        trainingCheckBoxGenetic.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 90, 3, 0);
        trainingPanel.add(trainingCheckBoxGenetic, gridBagConstraints);
        
        trainingLabelGenetic.setText("Genetic");
        trainingLabelGenetic.setToolTipText("Train the edge attribute scores against a reference annotation.");
        trainingLabelGenetic.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 110, 3, 0);
        trainingPanel.add(trainingLabelGenetic, gridBagConstraints);
        
        annotationLabel.setText("Label modules:");
        annotationLabel.setToolTipText("Label the modules using a reference annotation.");
        annotationLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        trainingPanel.add(annotationLabel, gridBagConstraints);
        
        annotationCheckBox.setSelected(false);
        annotationCheckBox.setToolTipText(annotationLabel.getToolTipText());
        annotationCheckBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        annotationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	annotationCheckBoxActionPerformed(evt);
            }
        });
        trainingPanel.add(annotationCheckBox, gridBagConstraints);
        
        annotationThresholdLabel.setText("Labeling Threshold:");
        annotationThresholdLabel.setToolTipText("Choose a threshold based on the Jaccard overlap score for annotating modules.");
        annotationThresholdLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        trainingPanel.add(annotationThresholdLabel, gridBagConstraints);
        
        annotationThresholdTextField.setText("0.8");
        annotationThresholdTextField.setToolTipText("Choose a threshold based on the Jaccard overlap score for annotating modules.");
        annotationThresholdTextField.addKeyListener(textFieldKeyListener);
        annotationThresholdTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        annotationThresholdTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        trainingPanel.add(annotationThresholdTextField, gridBagConstraints);
                    
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlParameter.add(trainingPanel, gridBagConstraints);
        
        //Report panel
        reportPanel.setLayout(new java.awt.GridBagLayout());
        reportPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Report"));
        reportPanel.setToolTipText("Specify a report location.");
        
        reportPathLabel.setText("Report folder path:");
        reportPathLabel.setToolTipText("Select a path for the report folder.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        reportPanel.add(reportPathLabel, gridBagConstraints);
        
        reportPathTextField.setText("");
        reportPathTextField.setToolTipText("");
        reportPathTextField.setPreferredSize(new java.awt.Dimension(150, 28));
        reportPathTextField.setEditable(false);
        reportPathTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        reportPanel.add(reportPathTextField, gridBagConstraints);
        
        reportPathButton.setText("...");
        reportPathButton.setToolTipText("Select a path for the report folder.");
        reportPathButton.setPreferredSize(new java.awt.Dimension(25, 25));
        reportPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	reportPathButtionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        reportPanel.add(reportPathButton, gridBagConstraints);
        
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlParameter.add(reportPanel, gridBagConstraints);
        
        //Placeholder
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlParameter.add(lbPlaceHolder3, gridBagConstraints);
    }                        
  
	private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// Build parameter object
		if (buildSearchParameters() == false)
			return;

		// Run search algorithm

		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(false);
		jTaskConfig.setModal(false);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(new SearchTask(parameters), jTaskConfig);
	}


	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// Close parent tab
		final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(
										SwingConstants.WEST);
		cytoPanel.remove(this.getParent().getParent());
	}

	private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
		
		JOptionPane pane = new JOptionPane("PanGIA is f@$%ing sweet!", JOptionPane.PLAIN_MESSAGE);
		pane.createDialog(null,"About PanGIA v1.0").setVisible(true);
		
	}                                           

	private void textFieldActionPerformed(java.awt.event.KeyEvent evt) {
		updateSearchButtonState();
	}
	
	private void edgeFilterSliderMoved(javax.swing.event.ChangeEvent evt)
	{
		if (!edgeFilterSliderEventLock)
		{
			edgeFilterTextField.setText(String.valueOf(edgeFilterSlider.getValue()));
			updateSearchButtonState();
		}
	}
	
	private void complexRewardSliderMoved(javax.swing.event.ChangeEvent evt)
	{
		if (!complexRewardSliderEventLock)
		{
			complexRewardTextField.setText(String.valueOf(complexRewardSlider.getValue()/601.0*6.0-3.0).substring(0, 4));
			updateSearchButtonState();
		}
	}
	
	private void edgeFilterTextChanged(java.awt.event.KeyEvent evt)
	{
		try
		{
			double val = Double.valueOf(edgeFilterTextField.getText());
			edgeFilterSliderEventLock = true;
			edgeFilterSlider.setValue((int)Math.round(val));
			edgeFilterSliderEventLock = false;
			//edgeFilterTextField.setText(String.valueOf((int)Math.round(val)));
		} catch (Exception e){}
		
		updateSearchButtonState();
	}
	
	private void complexRewardTextChanged(java.awt.event.KeyEvent evt)
	{
		try
		{
			double val = Double.valueOf(complexRewardTextField.getText());
			complexRewardSliderEventLock = true;
			complexRewardSlider.setValue((int)Math.round((val+3.0)*(601/6)));
			complexRewardSliderEventLock = false;
			//complexRewardTextField.setText(String.valueOf(val));
		} catch (Exception e){}
		
		updateSearchButtonState();
	}
	
	private void reportPathButtionActionPerformed(java.awt.event.ActionEvent evt) {
		
		JFileChooser reportFileChooser = new JFileChooser(".");
		reportFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		reportFileChooser.setMultiSelectionEnabled(false);
		reportFileChooser.setDialogTitle("Choose a report path");
		reportFileChooser.setSelectedFile(new File(new File("PanGIA_Report.html").getAbsoluteFile().getName()));
		int returnVal = reportFileChooser.showSaveDialog(reportPathButton);
		
		if (returnVal==JFileChooser.APPROVE_OPTION)
		{
			File f = reportFileChooser.getSelectedFile();
			
			this.reportPath = f.getAbsolutePath();
			reportPathTextField.setText(f.getName());
			reportPathTextField.setToolTipText(this.reportPath);
			reportPathTextField.setEnabled(true);
		}else
		{
			this.reportPath = "";
			reportPathTextField.setText("");
			reportPathTextField.setToolTipText("");
			reportPathTextField.setEnabled(false);
		}		
		
		updateSearchButtonState();
	}
	
	
	private void trainingComboBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		annotationCheckBoxActionPerformed(evt);
	}
	
	private void annotationCheckBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		boolean attribChosen = annotationAttribComboBox.getSelectedIndex()>=0;
		
		if (attribChosen)
		{
			trainingLabel.setEnabled(true);
			trainingCheckBoxPhysical.setEnabled(true);
			trainingLabelPhysical.setEnabled(true);
			trainingCheckBoxGenetic.setEnabled(true);
			trainingLabelGenetic.setEnabled(true);
			annotationLabel.setEnabled(true);
			annotationCheckBox.setEnabled(true);
			
			if (annotationCheckBox.isEnabled())
			{
				annotationThresholdTextField.setEnabled(annotationCheckBox.isSelected());
				annotationThresholdLabel.setEnabled(annotationCheckBox.isSelected());
			}
		}else
		{
			trainingLabel.setEnabled(false);
			trainingCheckBoxPhysical.setEnabled(false);
			trainingLabelPhysical.setEnabled(false);
			trainingCheckBoxGenetic.setEnabled(false);
			trainingLabelGenetic.setEnabled(false);
			annotationThresholdTextField.setEnabled(false);
			annotationThresholdLabel.setEnabled(false);
			annotationLabel.setEnabled(false);
			annotationCheckBox.setEnabled(false);
		}
		
		updateSearchButtonState();
	}

    // Variables declaration - do not modify                     
    private javax.swing.JButton aboutButton;
    
    /*
    private javax.swing.JLabel alphaLabel;
    private javax.swing.JLabel alphaMultiplierLabel;
    private javax.swing.JTextField alphaMultiplierTextField;
    private javax.swing.JTextField alphaTextField;
    */
    
    private JLabel complexRewardLabel;
    private boolean complexRewardSliderEventLock = false;
    private JSlider complexRewardSlider;
    private JLabel complexRewardSliderLabels;
    private JTextField complexRewardTextField;
    
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel degreeLabel;
    private javax.swing.JTextField degreeTextField;
    private javax.swing.JPanel edgeAttributePanel;
    private javax.swing.JPanel edgeFilteringPanel;
    private javax.swing.JComboBox geneticEdgeAttribComboBox;
    private javax.swing.JLabel geneticEdgeLabel;
    private javax.swing.JLabel lbPlaceHolder1;
    private javax.swing.JLabel lbPlaceHolder2;
    private javax.swing.JLabel lbPlaceHolder3;
    private javax.swing.JLabel pValueThresholdLabel;
    private javax.swing.JTextField edgeFilterTextField;
    private javax.swing.JComboBox physicalEdgeAttribComboBox;
    private javax.swing.JLabel physicalEdgeLabel;
    private javax.swing.JPanel scorePanel;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel topPane;
    private javax.swing.JPanel pnlParameter;
    private javax.swing.JPanel parameterPanel;
    private javax.swing.JLabel lbGeneticNetwork;
    private javax.swing.JLabel lbPhysicalNetwork;
    private javax.swing.JPanel trainingPanel;
    private JCheckBox trainingCheckBoxPhysical;
    private JCheckBox trainingCheckBoxGenetic;
    private JLabel trainingLabel;
    private JLabel trainingLabelPhysical;
    private JLabel trainingLabelGenetic;
    private JCheckBox annotationCheckBox;
    private JLabel annotationLabel;
    private javax.swing.JLabel lbComplexFile;
    private javax.swing.JComboBox annotationAttribComboBox;
    private JLabel annotationThresholdLabel;
    private JTextField annotationThresholdTextField;
    
    private javax.swing.JComboBox genScalingMethodComboBox;
    private javax.swing.JComboBox phyScalingMethodComboBox;
    private javax.swing.JLabel lbGeneticScale;
    private javax.swing.JLabel lbPhysicalScale;
    
    private JPanel reportPanel;
    private JLabel reportPathLabel;
    private JTextField reportPathTextField;
    private JButton reportPathButton;
    private String reportPath="";
        
    private JTextArea parameterErrorTextArea;
    
    private JLabel edgeFilterSliderLabels;
    private boolean edgeFilterSliderEventLock = false;
    private JSlider edgeFilterSlider;
    // End of variables declaration                     
               
  
	private NetworkSelectorPanelX physicalNetworkPanel = new NetworkSelectorPanelX();
	private NetworkSelectorPanelX geneticNetworkPanel = new NetworkSelectorPanelX();
	
	public void updateAttributeLists() {
		// Save current selection
		final Object geneticSelected = geneticEdgeAttribComboBox.getSelectedItem();
		final Object physicalSelected = physicalEdgeAttribComboBox.getSelectedItem();
		final Object annotSelected = annotationAttribComboBox.getSelectedItem();

		// Reset the children
		geneticEdgeAttribComboBox.removeAllItems();
		physicalEdgeAttribComboBox.removeAllItems();
		annotationAttribComboBox.removeAllItems();

		physicalEdgeAttribComboBox.addItem(DEFAULT_ATTRIBUTE);
		geneticEdgeAttribComboBox.addItem(DEFAULT_ATTRIBUTE);
		
		
		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		final Set<String> edgeAttrNames = new TreeSet<String>(Arrays
								      .asList(edgeAttr.getAttributeNames()));

		boolean isGeneticSelectedExist = false;
		boolean isPhysicalSelectedExist = false;
		for (String name : edgeAttrNames) {
			// Use only double or int attributes
			final byte attribType = edgeAttr.getMultiHashMapDefinition().getAttributeValueType(name);
			if ((attribType == MultiHashMapDefinition.TYPE_FLOATING_POINT || attribType == MultiHashMapDefinition.TYPE_INTEGER) && edgeAttr.getUserVisible(name)) {
				geneticEdgeAttribComboBox.addItem(name);
				physicalEdgeAttribComboBox.addItem(name);
				if (name.equals(geneticSelected))
					isGeneticSelectedExist = true;
				if (name.equals(physicalSelected))
					isPhysicalSelectedExist = true;
			}
		}
		
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		final Set<String> nodeAttrNames = new TreeSet<String>(Arrays
								      .asList(nodeAttr.getAttributeNames()));
		
		
		
		boolean isAnnotSelectedExist = false;
		for (String name : nodeAttrNames) {
			// Use only string attributes
			final byte attribType = nodeAttr.getMultiHashMapDefinition().getAttributeValueType(name);
			if (attribType == MultiHashMapDefinition.TYPE_STRING && nodeAttr.getUserVisible(name) && !name.equals("canonicalName")) {
				annotationAttribComboBox.addItem(name);
				if (name.equals(geneticSelected))
					isAnnotSelectedExist = true;
			}
		}

		if (isGeneticSelectedExist)
			geneticEdgeAttribComboBox.setSelectedItem(geneticSelected);
		if (isPhysicalSelectedExist)
			physicalEdgeAttribComboBox.setSelectedItem(physicalSelected);
		
		if (isAnnotSelectedExist)
			annotationAttribComboBox.setSelectedItem(annotSelected);

		updateSearchButtonState();
	}

	private void updateScalingMethods() {
		for (final ScalingMethodX method : ScalingMethodX.values()) {
			phyScalingMethodComboBox.addItem(method.getDisplayString());
			genScalingMethodComboBox.addItem(method.getDisplayString());
		}
	}

	/***
	 * 5/26/10: Error checking moved to updateSearchButtonState. Invalid parameters should never be allowed into this function.(Greg) 
	 * @return
	 */
	private boolean buildSearchParameters() {
		parameters = new SearchParameters();
		
		// Set networks
		parameters.setPhysicalNetwork(physicalNetworkPanel.getSelectedNetwork());
		parameters.setGeneticNetwork(geneticNetworkPanel.getSelectedNetwork());

		// Set edge attributes.
		final Object geneticEdgeItem  = geneticEdgeAttribComboBox.getSelectedItem();
		final Object physicalEdgeItem = physicalEdgeAttribComboBox.getSelectedItem();
		
		String geneticEdgeAttrName = geneticEdgeItem.toString();
		String physicalEdgeAttrName = physicalEdgeItem.toString();

		if (geneticEdgeAttrName.equalsIgnoreCase(DEFAULT_ATTRIBUTE)){
			geneticEdgeAttrName = "";
		}
		if (physicalEdgeAttrName.equalsIgnoreCase(DEFAULT_ATTRIBUTE)){
			physicalEdgeAttrName = "";
		}
		
		parameters.setGeneticEdgeAttrName(geneticEdgeAttrName);
		parameters.setPhysicalEdgeAttrName(physicalEdgeAttrName);

		parameters.setPhysicalScalingMethod((String)phyScalingMethodComboBox.getSelectedItem());
		parameters.setGeneticScalingMethod((String)genScalingMethodComboBox.getSelectedItem());
		
		//parameters.setAlpha(Double.parseDouble(alphaTextField.getText()));
		//parameters.setAlphaMultiplier(Double.parseDouble(alphaMultiplierTextField.getText()));
		parameters.setAlpha(Math.abs(Double.parseDouble(complexRewardTextField.getText())));
		parameters.setAlphaMultiplier(Double.parseDouble(complexRewardTextField.getText()));

		final String degree = degreeTextField.getText();
		if (degree.length() > 0) parameters.setPhysicalNetworkFilterDegree(Integer.parseInt(degree));
		else parameters.setPhysicalNetworkFilterDegree(-1);

		if (edgeFilterTextField.isEnabled())
		{
			final double pValueThreshold = Double.parseDouble(edgeFilterTextField.getText());
			parameters.setPValueThreshold(pValueThreshold);
		}else parameters.setPValueThreshold(Double.NaN);

		//final int numberOfSamples = Integer.parseInt(numberOfSamplesTextField.getText());
		//parameters.setNumberOfSamples(numberOfSamples);
		parameters.setNumberOfSamples(1000);
		
		parameters.setAnnotationThreshold(Double.valueOf(annotationThresholdTextField.getText()));
		parameters.setComplexAnnotation(annotationCheckBox.isSelected());
		parameters.setComplexTrainingPhysical(trainingCheckBoxPhysical.isSelected());
		parameters.setComplexTrainingGenetic(trainingCheckBoxGenetic.isSelected());
		
		if (annotationAttribComboBox.getSelectedItem()==null) parameters.setAnnotationAttrName("");
		else parameters.setAnnotationAttrName(annotationAttribComboBox.getSelectedItem().toString());
		
		parameters.setReportPath(reportPath);
		
		return true;
	}

	public void attributeDefined(String attrName) {
		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		if (edgeAttr.getMultiHashMapDefinition()
		    .getAttributeValueType(attrName) == MultiHashMapDefinition.TYPE_FLOATING_POINT) {
			geneticEdgeAttribComboBox.addItem(attrName);
			physicalEdgeAttribComboBox.addItem(attrName);
		}

	}

	public void attributeUndefined(String attrName) {
		geneticEdgeAttribComboBox.removeItem(attrName);
		physicalEdgeAttribComboBox.removeItem(attrName);
	}

	public void setContainer(final Container container) {
		this.container = container;
	}
	
	public void updateFilteringOptions(java.awt.event.ActionEvent evt)
	{
		final String geneticAttrName = (String)geneticEdgeAttribComboBox.getSelectedItem();
		
		if (geneticAttrName==null || geneticAttrName.equals(DEFAULT_ATTRIBUTE))
		{
			//lbNumberOfSamples.setEnabled(false);
		    //numberOfSamplesTextField.setEnabled(false);
			edgeFilterSliderLabels.setEnabled(false);
			edgeFilterSlider.setEnabled(false);
		    pValueThresholdLabel.setEnabled(false);
		    edgeFilterTextField.setEnabled(false);
		}else
		{
			edgeFilterSliderLabels.setEnabled(true);
			edgeFilterSlider.setEnabled(true);
			//lbNumberOfSamples.setEnabled(true);
		    //numberOfSamplesTextField.setEnabled(true);
		    pValueThresholdLabel.setEnabled(true);
		    edgeFilterTextField.setEnabled(true);
		}
	}

	private void updateSearchButtonState() {
		final String geneticAttrName = (String)geneticEdgeAttribComboBox.getSelectedItem();
		final String physicalAttrName = (String)physicalEdgeAttribComboBox.getSelectedItem();
		if (geneticAttrName == null || physicalAttrName == null)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose physical and genetic attributes.");
			return;
		}

		final CyNetwork physicalNetwork = physicalNetworkPanel.getSelectedNetwork();
		final CyNetwork geneticNetwork = geneticNetworkPanel.getSelectedNetwork();
		
		if (physicalNetwork == null && geneticNetwork==null)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose physical and genetic networks.");
			return;
		}
		
		if (physicalNetwork == null)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a physical network.");
			return;
		}
		
		if (geneticNetwork == null)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a genetic network.");
			return;
		}
		
		if (physicalNetwork==geneticNetwork && geneticAttrName.equals(physicalAttrName))
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose different networks or attributes.");
			return;
		}
		
		
		String physicalSelected = physicalEdgeAttribComboBox.getSelectedItem().toString();
		if (!physicalSelected.trim().equalsIgnoreCase(DEFAULT_ATTRIBUTE) && (Cytoscape.getEdgeAttributes().getType(physicalSelected) != CyAttributes.TYPE_INTEGER &&
			     Cytoscape.getEdgeAttributes().getType(physicalSelected) != CyAttributes.TYPE_FLOATING))
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose physical edge scores of type integer or float.");
			return;
		}
		
		String geneticSelected = geneticEdgeAttribComboBox.getSelectedItem().toString();
		if (!geneticSelected.trim().equalsIgnoreCase(DEFAULT_ATTRIBUTE) && (Cytoscape.getEdgeAttributes().getType(geneticSelected) != CyAttributes.TYPE_INTEGER &&
			     Cytoscape.getEdgeAttributes().getType(geneticSelected) != CyAttributes.TYPE_FLOATING))
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose genetic edge scores of type integer or float.");
			return;
		}
		
		if ((annotationCheckBox.isSelected() || trainingCheckBoxPhysical.isSelected() || trainingCheckBoxGenetic.isSelected()) && annotationAttribComboBox.getSelectedIndex()<0)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("To use annotation, please choose an annotation node attribute.");
			return;
		}
		
		
		//TextField validity
		/*
		try{Double.parseDouble(alphaTextField.getText());}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a valid value for Alpha.");
			return;
		}
		
		try{Double.parseDouble(alphaMultiplierTextField.getText());}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a valid value for Beta.");
			return;
		}*/
		
		
		try{Double.parseDouble(complexRewardTextField.getText());}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a valid value for module size.");
			return;
		}
		
		if (Double.parseDouble(complexRewardTextField.getText())<-3 || Double.parseDouble(complexRewardTextField.getText())>3)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please set module size in the range [-3,3].");
			return;
		}
		
		
		if (degreeTextField.getText().length()>0)
		{
			try
			{
				int d = Integer.parseInt(degreeTextField.getText());
				if (d<0)
				{
					searchButton.setEnabled(false);
					parameterErrorTextArea.setText("Please choose a positive value for degree filter.");
					return;
				}
			
			}
			catch (NumberFormatException e)
			{
				searchButton.setEnabled(false);
				parameterErrorTextArea.setText("Please choose a valid value for degree filter.");
				return;
			}
		}
			
		try
		{
			double p = Double.parseDouble(edgeFilterTextField.getText());
			if (p<0 || p>100)
			{
				searchButton.setEnabled(false);
				parameterErrorTextArea.setText("Please set percentile threshold in the range [0,100].");
				return;
			}
		
		}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a valid value for percentile threshold.");
			return;
		}
		
		/*
		try
		{
			int n = Integer.parseInt(numberOfSamplesTextField.getText());
			if (n<=0)
			{
				searchButton.setEnabled(false);
				parameterErrorTextArea.setText("Please choose a positive value for number of samples.");
				return;
			}
		
		}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a valid value for number of samples.");
			return;
		}*/
		
		
		if (annotationCheckBox.isSelected())
		{
			try
			{
				double p = Double.parseDouble(annotationThresholdTextField.getText());
				if (p<0 || p>1)
				{
					searchButton.setEnabled(false);
					parameterErrorTextArea.setText("Please set labeling threshold in the range [0,1].");
					
					return;
				}
			
			}
			catch (NumberFormatException e)
			{
				searchButton.setEnabled(false);
				parameterErrorTextArea.setText("Please choose a valid value for labeling threshold.");
				return;
			}
		}
		
		if (!reportPath.equals("") && new File(reportPath).exists() && new File(reportPath).isDirectory())
		{
			searchButton.setEnabled(false);
			parameterErrorTextArea.setText("Please choose a valid report path.");
			return;
		}
		
		
		parameterErrorTextArea.setText("");
		searchButton.setEnabled(true);
	}
}
