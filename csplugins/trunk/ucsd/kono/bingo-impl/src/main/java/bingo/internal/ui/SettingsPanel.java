package bingo.internal.ui;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that extends JPanel and takes care of the GUI-objects
 * * needed for the settings panel.   
 **/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.util.swing.OpenBrowser;

import bingo.internal.BingoAlgorithm;
import bingo.internal.BingoParameters;
import bingo.internal.HelpButtonActionListener;
import bingo.internal.SaveSettingsButtonActionListener;
import bingo.internal.SettingsPanelActionListener;

/******************************************************************
 * SettingsPanel.java:       Steven Maere & Karel Heymans (c) March 2005
 * -------------------
 *
 * Class that extends JPanel and takes care of the GUI-objects
 * needed for the settings panel.
 ******************************************************************/

/**
 * Class that extends JPanel and takes care of the GUI-objects needed for a
 * userfriendly settings panel with the options of using graph or text input,
 * choosing a test set name, a test, a correction, an annotation file, an
 * ontology file, a significance level, the GO categories shown in the graph,
 * reference set options and the option of saving a data-file.
 */
public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = -4120042298597419552L;

	/**
	 * the height of the panel
	 */
	private static final int DIM_HEIGHT = 800;
	/**
	 * the width of the panel
	 */
	private static final int DIM_WIDTH = 550;

	/**
	 * the bingo directory path
	 */
	private String bingoDir;

	/**
	 * Help button ; opens bingo website
	 */
	private JButton helpButton;

	/**
	 * Save settings button ; saves current settings as default
	 */
	private JButton saveSettingsButton;

	/**
	 * Panel for text or graph input
	 */
	private TextOrGraphPanel textOrGraphPanel;
	/**
	 * Panel for overrepresentation/underrepresentation .
	 */
	private OverUnderPanel overUnderPanel;
	/**
	 * JComboBox with the possible tests.
	 */
	private VizPanel vizPanel;
	/**
	 * JComboBox with the possible tests.
	 */
	private JComboBox testBox;
	/**
	 * JComboBox with the possible corrections.
	 */
	private JComboBox correctionBox;
	/**
	 * JComboBox with the possible number of categories in the graph.
	 */
	private JComboBox categoriesBox;
	/**
	 * JComboBox with the options against what cluster should be tested.
	 */
	private ChooseRefSetPanel clusterVsPanel;
	/**
	 * JTextField for input of the desired significance level.
	 */
	private JTextField alphaField;
	/**
	 * JLabel nameLabel
	 */
	private JLabel nameLabel;
	/**
	 * JLabel overUnderLabel
	 */
	private JLabel overUnderLabel;
	/**
	 * JLabel testLabel
	 */
	private JLabel testLabel;
	/**
	 * JLabel correctionLabel
	 */
	private JLabel correctionLabel;
	/**
	 * JLabel alphaLabel
	 */
	private JLabel alphaLabel;
	/**
	 * JLabel ontologyLabel
	 */
	private JLabel ontologyLabel;
	/**
	 * JLabel namespaceLabel
	 */
	private JLabel namespaceLabel;
	/**
	 * JLabel annotationLabel
	 */
	private JLabel annotationLabel;
	private JLabel ecLabel;
	/**
	 * JLabel categoriesLabel
	 */
	private JLabel categoriesLabel;
	/**
	 * JLabel clusterVsLabel
	 */
	private JLabel clusterVsLabel;
	/**
	 * JButton the bingo button
	 */
	private JButton bingoButton;
	/**
	 * SettingsOpenPanel for choosing the annotation file
	 */
	private ChooseAnnotationPanel annotationPanel;

	/**
	 * ChooseOntologyPanel for choosing the ontology file
	 */
	private ChooseOntologyPanel ontologyPanel;

	private ChooseNamespacePanel namespacePanel;
	/**
	 * text field for naming test cluster
	 */
	private JTextField nameField;
	/*
	 * text field for evidence codes to be removed
	 */
	private JTextField ecField;

	/**
	 * SettingsSavePanel for option of saving data-file
	 */
	private SaveResultsPanel dataPanel;

	public static final String[] testsArray = { BingoAlgorithm.NONE, BingoAlgorithm.HYPERGEOMETRIC,
		BingoAlgorithm.BINOMIAL };

	public static final String[] correctionArray = { BingoAlgorithm.NONE, BingoAlgorithm.BENJAMINI_HOCHBERG_FDR,
		BingoAlgorithm.BONFERRONI };
	public static final String[] categoriesArray = { BingoAlgorithm.CATEGORY,
		BingoAlgorithm.CATEGORY_BEFORE_CORRECTION, BingoAlgorithm.CATEGORY_CORRECTION };
	public static final String[] clusterVsArray = { BingoAlgorithm.GENOME, BingoAlgorithm.GRAPH, BingoAlgorithm.CUSTOM };

	private BingoParameters params;
	private Properties bingo_props;

	String overunder_label = "Do you want to assess over- or underrepresentation:";
	String clustername_label = "Cluster name:";
	String test_label = "Select a statistical test:";
	String correction_label = "Select a multiple testing correction:";
	String sig_label = "Choose a significance level:";
	String category_label = "Select the categories to be visualized:";
	String ref_label = "Select reference set:";
	String annotation_label = "Select organism/annotation:";
	String ontology_label = "Select ontology file:";
	String namespace_label = "Select namespace:";
	String ec_label = "Discard the following evidence codes:";
	
	private final CySwingAppAdapter adapter;
	private final OpenBrowser openBrowserService;

	/**
	 * This constructor creates the panel with its swing-components.
	 */
	public SettingsPanel(final String bingoDir, final CySwingAppAdapter adapter, OpenBrowser openBrowserService) {
		super();
		this.bingoDir = bingoDir;
		this.adapter= adapter;
		this.openBrowserService = openBrowserService;

		// Create a new bingo parameter set
		try {
			params = new BingoParameters(bingoDir);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error opening the properties file." + "\n"
					+ "Please make sure that there is bingo_gui.properties file" + "\n"
					+ "in the bingo.jar or in your cytoscape plugins directory.");
		}
		bingo_props = params.getbingo_props();

		// create the JComponents.
		makeJComponents();

		setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
		setOpaque(false);
		// create border.
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "BiNGO settings", 0, 0,
				new Font("bingo settings", Font.BOLD, 16), Color.black));

		// Layout with GridBagLayout.
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;

		JPanel dummyPanel = new JPanel();
		dummyPanel.setOpaque(false);
		dummyPanel.setPreferredSize(new Dimension(50, 20));
		c.gridx = 0;
		gridbag.setConstraints(dummyPanel, c);
		add(dummyPanel);

		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(saveSettingsButton, c);
		add(saveSettingsButton);
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(helpButton, c);
		add(helpButton);

		JPanel dummyPanel2 = new JPanel();
		dummyPanel2.setOpaque(false);
		dummyPanel2.setPreferredSize(new Dimension(50, 20));
		c.gridx = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(dummyPanel2, c);
		add(dummyPanel2);

		c.weighty = 1;
		c.weightx = 100;
		c.gridx = 1;
		c.gridwidth = 2;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.HORIZONTAL;

		gridbag.setConstraints(nameLabel, c);
		add(nameLabel);

		gridbag.setConstraints(nameField, c);
		add(nameField);

		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.weighty = 100;
		gridbag.setConstraints(textOrGraphPanel, c);
		add(textOrGraphPanel);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.weighty = 1;

		gridbag.setConstraints(overUnderLabel, c);
		add(overUnderLabel);

		gridbag.setConstraints(overUnderPanel, c);
		add(overUnderPanel);

		gridbag.setConstraints(vizPanel, c);
		add(vizPanel);

		gridbag.setConstraints(testLabel, c);
		add(testLabel);

		gridbag.setConstraints(testBox, c);
		add(testBox);

		gridbag.setConstraints(correctionLabel, c);
		add(correctionLabel);

		gridbag.setConstraints(correctionBox, c);
		add(correctionBox);

		gridbag.setConstraints(alphaLabel, c);
		add(alphaLabel);

		gridbag.setConstraints(alphaField, c);
		add(alphaField);

		gridbag.setConstraints(categoriesLabel, c);
		add(categoriesLabel);

		gridbag.setConstraints(categoriesBox, c);
		add(categoriesBox);

		gridbag.setConstraints(clusterVsLabel, c);
		add(clusterVsLabel);

		// include custom ref set option
		gridbag.setConstraints(clusterVsPanel, c);
		add(clusterVsPanel);

		gridbag.setConstraints(ontologyLabel, c);
		add(ontologyLabel);

		gridbag.setConstraints(ontologyPanel, c);
		add(ontologyPanel);

		gridbag.setConstraints(namespaceLabel, c);
		add(namespaceLabel);

		gridbag.setConstraints(namespacePanel, c);
		add(namespacePanel);

		gridbag.setConstraints(annotationLabel, c);
		add(annotationLabel);

		gridbag.setConstraints(annotationPanel, c);
		add(annotationPanel);

		gridbag.setConstraints(ecLabel, c);
		add(ecLabel);

		gridbag.setConstraints(ecField, c);
		add(ecField);

		gridbag.setConstraints(dataPanel, c);
		add(dataPanel);

		gridbag.setConstraints(bingoButton, c);
		add(bingoButton);

		validate();
	}

	/**
	 * Method that makes the necessary JComponents for the SettingsPanel. The
	 * used JComponents are: a textfield for the cluster name, a
	 * TextOrGraphPanel for choosing between text and graph input, two
	 * comboboxes for the distributions and the corrections, a textfield for the
	 * alpha, two comboboxes for the option against what cluster should be
	 * tested and one for the option how many categories must be displayed int
	 * the graph, two SettingsOpenPanels for choosing the annotation and
	 * ontology file, a SettingsSavePanel for the data-file and a button to
	 * start the calculations.
	 */
	public void makeJComponents() {

		helpButton = new JButton("Help");
		helpButton.setMnemonic(KeyEvent.VK_H);
		helpButton.addActionListener(new HelpButtonActionListener(this, openBrowserService));

		saveSettingsButton = new JButton("Save settings as default");
		saveSettingsButton.setMnemonic(KeyEvent.VK_S);
		saveSettingsButton.addActionListener(new SaveSettingsButtonActionListener(this));

		// JComboboxes.
		testBox = new JComboBox(testsArray);
		testBox.setSelectedItem(bingo_props.getProperty("tests_def"));
		correctionBox = new JComboBox(correctionArray);
		correctionBox.setSelectedItem(bingo_props.getProperty("correction_def"));
		categoriesBox = new JComboBox(categoriesArray);
		categoriesBox.setSelectedItem(bingo_props.getProperty("categories_def"));
		clusterVsPanel = new ChooseRefSetPanel(this, bingoDir, clusterVsArray, bingo_props.getProperty("refset_def"));

		// JTextField.
		alphaField = new JTextField(bingo_props.getProperty("signif_def"));
		nameField = new JTextField("");

		// OverUnderPanel
		overUnderPanel = new OverUnderPanel();

		// OverUnderPanel
		vizPanel = new VizPanel();

		// TextOrGraphPanel
		textOrGraphPanel = new TextOrGraphPanel();

		// JLabels.
		overUnderLabel = new JLabel(overunder_label);
		nameLabel = new JLabel(clustername_label);
		testLabel = new JLabel(test_label);
		correctionLabel = new JLabel(correction_label);
		alphaLabel = new JLabel(sig_label);
		categoriesLabel = new JLabel(category_label);
		clusterVsLabel = new JLabel(ref_label);
		testLabel.setForeground(Color.black);
		correctionLabel.setForeground(Color.black);
		alphaLabel.setForeground(Color.black);
		categoriesLabel.setForeground(Color.black);
		clusterVsLabel.setForeground(Color.black);
		ecLabel = new JLabel(ec_label);

		// annotationPanel.
		annotationLabel = new JLabel(annotation_label);
		annotationPanel = new ChooseAnnotationPanel(this, bingoDir, params.getSpeciesLabels(),
				bingo_props.getProperty("species_def"));

		// evidence code Field
		ecField = new JTextField();

		// ontologyPanel.
		namespaceLabel = new JLabel(namespace_label);
		namespacePanel = new ChooseNamespacePanel(this, bingoDir, params.getNamespaceLabels(),
				bingo_props.getProperty("namespace_def"));

		ontologyLabel = new JLabel(ontology_label);
		ontologyPanel = new ChooseOntologyPanel(this, bingoDir, params.getOntologyLabels(),
				bingo_props.getProperty("ontology_file_def"));

		// Creating SettingsSavePanels.
		dataPanel = new SaveResultsPanel("Data", this, bingoDir);

		// the bingo-button to start the calculations.
		bingoButton = new JButton("Start BiNGO");
		bingoButton.setMnemonic(KeyEvent.VK_B);
		bingoButton.addActionListener(new SettingsPanelActionListener(params, this, adapter));
	}

	public TextOrGraphPanel getTextOrGraphPanel() {
		return textOrGraphPanel;
	}

	public OverUnderPanel getOverUnderPanel() {
		return overUnderPanel;
	}

	public VizPanel getVizPanel() {
		return vizPanel;
	}

	/**
	 * Get the testBox.
	 * 
	 * @return JCombobox testBox.
	 */
	public JComboBox getTestBox() {
		return testBox;
	}

	/**
	 * Get the correctionBox.
	 * 
	 * @return JCombobox correctionBox.
	 */
	public JComboBox getCorrectionBox() {
		return correctionBox;
	}

	/**
	 * Get the categoriesBox.
	 * 
	 * @return JCombobox categoriesBox.
	 */
	public JComboBox getCategoriesBox() {
		return categoriesBox;
	}

	/**
	 * Get the clusterVsBox.
	 * 
	 * @return JCombobox clusterVsBox.
	 */
	public ChooseRefSetPanel getClusterVsPanel() {
		return clusterVsPanel;
	}

	/**
	 * Get the alphaField.
	 * 
	 * @return JTextField alphaField.
	 */
	public JTextField getAlphaField() {
		return alphaField;
	}

	/**
	 * Get the nameField.
	 * 
	 * @return JTextField alphaField.
	 */

	public JTextField getNameField() {
		return nameField;
	}

	public JTextField getEcField() {
		return ecField;
	}

	/**
	 * Get the annotationPanel.
	 * 
	 * @return SettingsOpenPanel annotationPanel.
	 */
	public ChooseAnnotationPanel getAnnotationPanel() {
		return annotationPanel;
	}

	/**
	 * Get the ontologyPanel.
	 * 
	 * @return ChooseOntologyPanel ontologyPanel.
	 */
	public ChooseOntologyPanel getOntologyPanel() {
		return ontologyPanel;
	}

	public ChooseNamespacePanel getNamespacePanel() {
		return namespacePanel;
	}

	/**
	 * Get the dataPanel.
	 * 
	 * @return SettingsSavePanel dataPanel.
	 */
	public SaveResultsPanel getDataPanel() {
		return dataPanel;
	}

	/** get the bingo directory path */
	public String getbingoDir() {
		return bingoDir;
	}

	public Properties getbingoProps() {
		return bingo_props;
	}

	public BingoParameters getParams() {
		return params;
	}
}
