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
import cytoscape.view.cytopanels.CytoPanel;
import org.idekerlab.PanGIAPlugin.ScalingMethodX;
import org.idekerlab.PanGIAPlugin.utilities.CyCollapsiblePanel;

import com.lowagie.text.Font;


/**
 * @author kono
 * 
 * 5/25/10: Removed restrictions on machine-generated sections. (Greg) 
 */
public class SearchPropertyPanel extends JPanel implements MultiHashMapDefinitionListener, PropertyChangeListener {
	private static final long serialVersionUID = -3352470909434196700L;

	private static final double DEF_ALPHA = 1.6;
	private static final double DEF_ALPHA_MUL = 1.0;
	private static final String DEF_DEGREE = "";
	private static final double DEF_CUTOFF = 20.0;
	private static final double DEF_PVALUE_THRESHOLD = 0.05;
	private static final int DEF_NUMBER_OF_SAMPLES = 10000;
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
		collapsiblePanel.getContentPane().add(pnlParameter);

		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;

		this.parameterPanel.add(collapsiblePanel, gridBagConstraints);
         
		// Set the button size the same
		closeButton.setPreferredSize(new java.awt.Dimension(67, 23));
		aboutButton.setPreferredSize(new java.awt.Dimension(67, 23));
		closeButton.setPreferredSize(new java.awt.Dimension(67, 23));
		searchButton.setPreferredSize(new java.awt.Dimension(67, 23));
		 
		// about button is a place holder for now, hide it
		this.aboutButton.setVisible(false);
		
		Cytoscape.getEdgeAttributes().getMultiHashMapDefinition()
			.addDataDefinitionListener(this);

		updateAttributeLists();
		updateScalingMethods();

		// Set defaults
		this.alphaTextField.setText(Double.toString(DEF_ALPHA));
		this.alphaMultiplierTextField.setText(Double.toString(DEF_ALPHA_MUL));
		this.degreeTextField.setText(DEF_DEGREE);
		this.pValueThresholdTextField.setText(Double.toString(DEF_PVALUE_THRESHOLD));
		this.numberOfSamplesTextField.setText(Integer.toString(DEF_NUMBER_OF_SAMPLES));
	
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
        helpButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();

        parameterErrorLabel= new JLabel();
        
        setLayout(new java.awt.GridBagLayout());

        topPane.setLayout(new java.awt.GridBagLayout());

        topPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Physical Network"));
        physicalEdgeLabel.setText("Attribute:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        topPane.add(physicalEdgeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        topPane.add(physicalEdgeAttribComboBox, gridBagConstraints);

        lbPhysicalNetwork.setText("Network:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        topPane.add(lbPhysicalNetwork, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        topPane.add(physicalNetworkPanel, gridBagConstraints);

        lbPhysicalScale.setText("Scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        topPane.add(lbPhysicalScale, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        topPane.add(phyScalingMethodComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(topPane, gridBagConstraints);

        edgeAttributePanel.setLayout(new java.awt.GridBagLayout());

        edgeAttributePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Genetic Network"));
        geneticEdgeLabel.setText("Attribute:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        edgeAttributePanel.add(geneticEdgeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        edgeAttributePanel.add(geneticEdgeAttribComboBox, gridBagConstraints);

        lbGeneticNetwork.setText("Network:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        edgeAttributePanel.add(lbGeneticNetwork, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        edgeAttributePanel.add(geneticNetworkPanel, gridBagConstraints);

        lbGeneticScale.setText("Scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        edgeAttributePanel.add(lbGeneticScale, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
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
        parameterErrorLabel.setText("");
        parameterErrorLabel.setForeground(Color.red);
        parameterErrorLabel.setFont(parameterErrorLabel.getFont().deriveFont(Font.BOLD));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(parameterErrorLabel, gridBagConstraints);
        
        //Button panel
        buttonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(helpButton);

        aboutButton.setText("About");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(aboutButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(closeButton);

        searchButton.setText("Search");
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
        alphaLabel = new javax.swing.JLabel();
        alphaMultiplierLabel = new javax.swing.JLabel();
        degreeLabel = new javax.swing.JLabel();
        alphaTextField = new javax.swing.JTextField();
        alphaMultiplierTextField = new javax.swing.JTextField();
        degreeTextField = new javax.swing.JTextField();
        lbPlaceHolder1 = new javax.swing.JLabel();
        edgeFilteringPanel = new javax.swing.JPanel();
        pValueThresholdLabel = new javax.swing.JLabel();
        pValueThresholdTextField = new javax.swing.JTextField();
        lbPlaceHolder2 = new javax.swing.JLabel();
        lbNumberOfSamples = new javax.swing.JLabel();
        numberOfSamplesTextField = new javax.swing.JTextField();
        lbPlaceHolder3 = new javax.swing.JLabel();
        trainingCheckBox = new JCheckBox();
        trainingLabel = new JLabel();
        annotationCheckBox = new JCheckBox();
        annotationLabel = new JLabel();
        lbComplexFile = new javax.swing.JLabel();
        complexFileTextField = new javax.swing.JTextField();
        complexFileButton = new javax.swing.JButton();
        annotationThresholdLabel = new JLabel();
        annotationThresholdTextField = new JTextField();

        trainingPanel = new javax.swing.JPanel();
        
        pnlParameter.setLayout(new java.awt.GridBagLayout());
        
        final java.awt.event.KeyListener textFieldKeyListener = new java.awt.event.KeyListener() {
            public void keyPressed(java.awt.event.KeyEvent evt) {}
            public void keyTyped(java.awt.event.KeyEvent evt) {}
            public void keyReleased(java.awt.event.KeyEvent evt) {alphaTextFieldActionPerformed(evt);}
        };

        //ScorePanel
        scorePanel.setLayout(new java.awt.GridBagLayout());

        scorePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Score Parameters"));
        alphaLabel.setText("Alpha:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        scorePanel.add(alphaLabel, gridBagConstraints);

        alphaMultiplierLabel.setText("Alpha Multiplier:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scorePanel.add(alphaMultiplierLabel, gridBagConstraints);

        degreeLabel.setText("Network filter degree (optional):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scorePanel.add(degreeLabel, gridBagConstraints);

        alphaTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        alphaTextField.addKeyListener(textFieldKeyListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scorePanel.add(alphaTextField, gridBagConstraints);

        alphaMultiplierTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        alphaMultiplierTextField.addKeyListener(textFieldKeyListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scorePanel.add(alphaMultiplierTextField, gridBagConstraints);

        degreeTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        degreeTextField.addKeyListener(textFieldKeyListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scorePanel.add(degreeTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        scorePanel.add(lbPlaceHolder1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlParameter.add(scorePanel, gridBagConstraints);

        //EdgeFilteringPanel
        edgeFilteringPanel.setLayout(new java.awt.GridBagLayout());

        edgeFilteringPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Filtering"));
        pValueThresholdLabel.setText("Percentile Threshold:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        edgeFilteringPanel.add(pValueThresholdLabel, gridBagConstraints);

        pValueThresholdTextField.setText("0.05");
        pValueThresholdTextField.addKeyListener(textFieldKeyListener);
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pValueThresholdTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        edgeFilteringPanel.add(pValueThresholdTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        edgeFilteringPanel.add(lbPlaceHolder2, gridBagConstraints);

        lbNumberOfSamples.setText("Number of samples:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        edgeFilteringPanel.add(lbNumberOfSamples, gridBagConstraints);

        numberOfSamplesTextField.setText("10000");
        numberOfSamplesTextField.addKeyListener(textFieldKeyListener);
        numberOfSamplesTextField.setPreferredSize(new java.awt.Dimension(70, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        edgeFilteringPanel.add(numberOfSamplesTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlParameter.add(edgeFilteringPanel, gridBagConstraints);

        
        //TrainingPanel
        trainingPanel.setLayout(new java.awt.GridBagLayout());
        trainingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Annotation"));
        
        trainingLabel.setText("Annotation training:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        trainingPanel.add(trainingLabel, gridBagConstraints);
        
        
        trainingCheckBox.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        trainingCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	trainingCheckBoxActionPerformed(evt);
            }
        });
        
        trainingPanel.add(trainingCheckBox, gridBagConstraints);
        
        
        annotationLabel.setText("Annotation labeling:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        trainingPanel.add(annotationLabel, gridBagConstraints);
        
        annotationCheckBox.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        
        annotationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	annotationCheckBoxActionPerformed(evt);
            }
        });
        
        trainingPanel.add(annotationCheckBox, gridBagConstraints);
        
        
        
        lbComplexFile.setText("Annotation file:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        trainingPanel.add(lbComplexFile, gridBagConstraints);
        
        complexFileTextField.setText("");
        complexFileTextField.setPreferredSize(new java.awt.Dimension(80, 30));
        complexFileTextField.setEditable(false);
        complexFileTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        trainingPanel.add(complexFileTextField, gridBagConstraints);
        
        complexFileButton.setText("...");
        complexFileButton.setPreferredSize(new java.awt.Dimension(25, 25));
        complexFileButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        //gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        trainingPanel.add(complexFileButton, gridBagConstraints);
        
        complexFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	complexFileButtonActionPerformed(evt);
            }
        });
        
        annotationThresholdLabel.setText("Labeling Threshold:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        trainingPanel.add(annotationThresholdLabel, gridBagConstraints);
        
        annotationThresholdTextField.setText("0.8");
        annotationThresholdTextField.addKeyListener(textFieldKeyListener);
        annotationThresholdTextField.setPreferredSize(new java.awt.Dimension(50, 25));
        annotationThresholdTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        trainingPanel.add(annotationThresholdTextField, gridBagConstraints);
                    
        
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlParameter.add(trainingPanel, gridBagConstraints);
        
        //Placeholder
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
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
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(false);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(new SearchTask(parameters), jTaskConfig);
	}

	private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}


	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// Close parent tab
		final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(
										SwingConstants.WEST);
		cytoPanel.remove(this.getParent().getParent());
	}

	private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
		// TODO add your handling code here:	    
	}                                           

	private void alphaTextFieldActionPerformed(java.awt.event.KeyEvent evt) {
		updateSearchButtonState();
	}
	
	private void complexFileButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		JFileChooser complexFileChooser = new JFileChooser();
		complexFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		complexFileChooser.setMultiSelectionEnabled(false);
		complexFileChooser.setCurrentDirectory(new File("."));
		int returnVal = complexFileChooser.showOpenDialog(complexFileButton);
		
		if (returnVal==JFileChooser.APPROVE_OPTION)
		{
			File f = complexFileChooser.getSelectedFile();
			this.complexFilePath = f.getAbsolutePath();
			complexFileTextField.setText(f.getName());
		}else
		{
			this.complexFilePath = "";
			complexFileTextField.setText("");
		}
		
		updateSearchButtonState();
	}
	
	private void trainingCheckBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		annotationCheckBoxActionPerformed(evt);
	}
	
	private void annotationCheckBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (trainingCheckBox.isSelected() || annotationCheckBox.isSelected())
		{
			complexFileTextField.setEnabled(true);
		    complexFileButton.setEnabled(true);
		}else
		{
			complexFileTextField.setEnabled(false);
		    complexFileButton.setEnabled(false);
		}
		
		annotationThresholdTextField.setEnabled(annotationCheckBox.isSelected());
		
		updateSearchButtonState();
	}

    // Variables declaration - do not modify                     
    private javax.swing.JButton aboutButton;
    private javax.swing.JLabel alphaLabel;
    private javax.swing.JLabel alphaMultiplierLabel;
    private javax.swing.JTextField alphaMultiplierTextField;
    private javax.swing.JTextField alphaTextField;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel degreeLabel;
    private javax.swing.JTextField degreeTextField;
    private javax.swing.JPanel edgeAttributePanel;
    private javax.swing.JPanel edgeFilteringPanel;
    private javax.swing.JComboBox geneticEdgeAttribComboBox;
    private javax.swing.JLabel geneticEdgeLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel lbNumberOfSamples;
    private javax.swing.JLabel lbPlaceHolder1;
    private javax.swing.JLabel lbPlaceHolder2;
    private javax.swing.JLabel lbPlaceHolder3;
    private javax.swing.JTextField numberOfSamplesTextField;
    private javax.swing.JLabel pValueThresholdLabel;
    private javax.swing.JTextField pValueThresholdTextField;
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
    private JCheckBox trainingCheckBox;
    private JLabel trainingLabel;
    private JCheckBox annotationCheckBox;
    private JLabel annotationLabel;
    private javax.swing.JLabel lbComplexFile;
    private javax.swing.JTextField complexFileTextField;
    private javax.swing.JButton complexFileButton;
    private String complexFilePath = "";
    private JLabel annotationThresholdLabel;
    private JTextField annotationThresholdTextField;
    
    private javax.swing.JComboBox genScalingMethodComboBox;
    private javax.swing.JComboBox phyScalingMethodComboBox;
    private javax.swing.JLabel lbGeneticScale;
    private javax.swing.JLabel lbPhysicalScale;
    
    private JLabel parameterErrorLabel;
    // End of variables declaration                     
               
  
	private NetworkSelectorPanelX physicalNetworkPanel = new NetworkSelectorPanelX();
	private NetworkSelectorPanelX geneticNetworkPanel = new NetworkSelectorPanelX();
	
	public void updateAttributeLists() {
		// Save current selection
		final Object geneticSelected = geneticEdgeAttribComboBox.getSelectedItem();
		final Object physicalSelected = physicalEdgeAttribComboBox.getSelectedItem();

		// Reset the children
		geneticEdgeAttribComboBox.removeAllItems();
		physicalEdgeAttribComboBox.removeAllItems();

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
			if (attribType == MultiHashMapDefinition.TYPE_FLOATING_POINT || attribType == MultiHashMapDefinition.TYPE_INTEGER) {
				geneticEdgeAttribComboBox.addItem(name);
				physicalEdgeAttribComboBox.addItem(name);
				if (name.equals(geneticSelected))
					isGeneticSelectedExist = true;
				if (name.equals(physicalSelected))
					isPhysicalSelectedExist = true;
			}
		}

		if (isGeneticSelectedExist)
			geneticEdgeAttribComboBox.setSelectedItem(geneticSelected);
		if (isPhysicalSelectedExist)
			physicalEdgeAttribComboBox.setSelectedItem(physicalSelected);

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
		
		parameters.setAlpha(Double.parseDouble(alphaTextField.getText()));

		parameters.setAlphaMultiplier(Double.parseDouble(alphaMultiplierTextField.getText()));

		final String degree = degreeTextField.getText();
		if (degree.length() > 0) parameters.setPhysicalNetworkFilterDegree(Integer.parseInt(degree));
		else parameters.setPhysicalNetworkFilterDegree(-1);

		final double pValueThreshold = Double.parseDouble(pValueThresholdTextField.getText());
		parameters.setPValueThreshold(pValueThreshold);

		final int numberOfSamples = Integer.parseInt(numberOfSamplesTextField.getText());
		parameters.setNumberOfSamples(numberOfSamples);
		
		parameters.setAnnotationThreshold(Double.valueOf(annotationThresholdTextField.getText()));
		parameters.setComplexAnnotation(annotationCheckBox.isSelected());
		parameters.setComplexTraining(trainingCheckBox.isSelected());
		parameters.setComplexFile(complexFilePath);
		
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

	private void updateSearchButtonState() {
		final String geneticAttrName = (String)geneticEdgeAttribComboBox.getSelectedItem();
		final String physicalAttrName = (String)physicalEdgeAttribComboBox.getSelectedItem();
		if (geneticAttrName == null || physicalAttrName == null)
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Must choose physical and genetic attributes.");
			return;
		}

		if (geneticAttrName.equals(physicalAttrName) && !geneticAttrName.equals(DEFAULT_ATTRIBUTE))
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("<HTML>Error: Physical and genetic attributes<BR>cannot be the same.</HTML>");
			return;
		}
		
		final CyNetwork physicalNetwork = physicalNetworkPanel.getSelectedNetwork();
		final CyNetwork geneticNetwork = geneticNetworkPanel.getSelectedNetwork();
		
		if (physicalNetwork == null)
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Must choose a physical network.");
			return;
		}
		
		if (geneticNetwork == null)
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Must choose a genetic network.");
			return;
		}
		
		String physicalSelected = physicalEdgeAttribComboBox.getSelectedItem().toString();
		if (!physicalSelected.trim().equalsIgnoreCase(DEFAULT_ATTRIBUTE) && (Cytoscape.getEdgeAttributes().getType(physicalSelected) != CyAttributes.TYPE_INTEGER &&
			     Cytoscape.getEdgeAttributes().getType(physicalSelected) != CyAttributes.TYPE_FLOATING))
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("<HTML>Error: Physical edge score must<BR>be of type integer or float.</HTML>");
			return;
		}
		
		String geneticSelected = geneticEdgeAttribComboBox.getSelectedItem().toString();
		if (!geneticSelected.trim().equalsIgnoreCase(DEFAULT_ATTRIBUTE) && (Cytoscape.getEdgeAttributes().getType(geneticSelected) != CyAttributes.TYPE_INTEGER &&
			     Cytoscape.getEdgeAttributes().getType(geneticSelected) != CyAttributes.TYPE_FLOATING))
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("<HTML>Error: Genetic edge score must<BR>be of type integer or float.</HTML>");
			return;
		}
		
		if ((annotationCheckBox.isSelected() || trainingCheckBox.isSelected()) && complexFilePath.equals(""))
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Annotation requires an annotation file.");
			return;
		}
		
		
		//TextField validity
		try{Double.parseDouble(alphaTextField.getText());}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Invalid value for Alpha.");
			return;
		}
		
		try{Double.parseDouble(alphaMultiplierTextField.getText());}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Invalid value for Alpha Multiplier.");
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
					parameterErrorLabel.setText("Error: degree filter must be positive.");
					return;
				}
			
			}
			catch (NumberFormatException e)
			{
				searchButton.setEnabled(false);
				parameterErrorLabel.setText("Error: Invalid value for degree filter.");
				return;
			}
		}
			
		try
		{
			double p = Double.parseDouble(pValueThresholdTextField.getText());
			if (p<0 || p>1)
			{
				searchButton.setEnabled(false);
				parameterErrorLabel.setText("<HTML>Error: Percentile threshold must<BR>fall in the range [0,1].</HTML>");
				return;
			}
		
		}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Invalid value for Percentile Threshold.");
			return;
		}
		
		try
		{
			int n = Integer.parseInt(numberOfSamplesTextField.getText());
			if (n<=0)
			{
				searchButton.setEnabled(false);
				parameterErrorLabel.setText("Error: Number of samples must be positive.");
				return;
			}
		
		}
		catch (NumberFormatException e)
		{
			searchButton.setEnabled(false);
			parameterErrorLabel.setText("Error: Invalid value for Number of samples.");
			return;
		}
		
		
		if (annotationCheckBox.isSelected())
		{
			try
			{
				double p = Double.parseDouble(annotationThresholdTextField.getText());
				if (p<0 || p>1)
				{
					searchButton.setEnabled(false);
					parameterErrorLabel.setText("<HTML>Error: Labeling threshold must<BR>fall in the range [0,1].</HTML>");
					
					return;
				}
			
			}
			catch (NumberFormatException e)
			{
				searchButton.setEnabled(false);
				parameterErrorLabel.setText("Error: Invalid value for Labeling threshold.");
				return;
			}
		}
		
		
		parameterErrorLabel.setText("");
		searchButton.setEnabled(true);
	}
}
