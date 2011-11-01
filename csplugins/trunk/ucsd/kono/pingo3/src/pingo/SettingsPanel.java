package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 * * Authors: Steven Maere
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.plugin.CyPluginAdapter;

import pingo.ui.TermSearchGUI;
import BiNGO.BiNGOplugin;
import BiNGO.BingoAlgorithm;

/******************************************************************
 * SettingsPanel.java:       Steven Maere & Karel Heymans (c) 2005-2010
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

	private static final long serialVersionUID = -7696941490574153076L;

	private static final String DEF_MESSAGE = "Double-click to select GO Term(s)...";

	/*--------------------------------------------------------------
	FIELDS.
	--------------------------------------------------------------*/

	/**
	 * the height of the panel
	 */
	private static final int DIM_HEIGHT = 800;
	/**
	 * the width of the panel
	 */
	private static final int DIM_WIDTH = 550;

	public final String CURRENTGRAPH = "Current Cytoscape network";
	/**
	 * the PiNGO directory path
	 */
	private String pingoDir;

	/**
	 * Help button ; opens PiNGO website
	 */
	private JButton helpButton;
	private JButton goButton;

	/**
	 * Save settings button ; saves current settings as default
	 */
	private JButton saveSettingsButton;

	/**
	 * Panel for go Cat input
	 */
	private JTextField startGoCatInputField;
	private JTextField filterGoCatInputField;
	private JTextField targetGoCatInputField;

	/**
	 * Buttons for searching GO ID
	 */
	private JButton startGoCatButton;
	private JButton filterGoCatButton;
	private JButton targetGoCatButton;

	/**
	 * Panel for gene description input
	 */
	private GeneDescriptionPanel geneDescriptionPanel;

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
	 * JComboBox with the options against what cluster should be tested.
	 */
	private ChooseRefSetPanel clusterVsPanel;
	private ChooseGraphPanel graphPanel;
	/**
	 * JTextField for input of the desired significance level.
	 */
	private JTextField alphaField;
	/**
	 * JLabel nameLabel
	 */
	private JLabel nameLabel;

	private JLabel startGoCatLabel;
	private JLabel filterGoCatLabel;
	private JLabel targetGoCatLabel;

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
	 * JLabel clusterVsLabel
	 */
	private JLabel clusterVsLabel;
	private JLabel graphLabel;
	private JLabel geneDescriptionLabel;
	/**
	 * JButton the pingo button
	 */
	private JButton pingoButton;
	/**
	 * ChooseAnnotationPanel for choosing the annotation file
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

	private JButton goTreeButton;

	/**
	 * SaveResultsPanel for option of saving data-file
	 */
	private SaveResultsPanel dataPanel;

	public static final String[] testsArray = {
			// BingoAlgorithm.NONE,
			BingoAlgorithm.HYPERGEOMETRIC, BingoAlgorithm.BINOMIAL };
	// BingoAlgorithm.PARENT_CHILD_INTERSECTION};
	public static final String[] correctionArray = {
			// BingoAlgorithm.NONE,
			BingoAlgorithm.BENJAMINI_HOCHBERG_FDR, BingoAlgorithm.BONFERRONI };
	public static final String[] categoriesArray = { BingoAlgorithm.CATEGORY,
			BingoAlgorithm.CATEGORY_BEFORE_CORRECTION, BingoAlgorithm.CATEGORY_CORRECTION };
	public static final String[] clusterVsArray = { BingoAlgorithm.GENOME, BingoAlgorithm.GRAPH, BingoAlgorithm.CUSTOM };
	public static final String[] geneDescriptionArray = { BingoAlgorithm.NONE, BingoAlgorithm.CUSTOM };
	public final String[] graphArray = { this.CURRENTGRAPH, BingoAlgorithm.CUSTOM };

	private PingoParameters params;
	private Properties pingo_props;

	String clustername_label = "Name:";
	String graph_label = "Choose network to analyze:";
	String startgocat_label = "Start GO categories :";
	String filtergocat_label = "Filter GO categories :";
	String targetgocat_label = "Target GO categories :";
	String genedescription_label = "Choose gene description file (optional):";
	String test_label = "Select a statistical test:";
	String correction_label = "Select a multiple testing correction:";
	String sig_label = "Choose a significance level:";
	String ref_label = "Select reference set:";
	String annotation_label = "Select organism/annotation:";
	String ontology_label = "Select ontology file:";
	String namespace_label = "Select namespace:";
	String ec_label = "Discard the following evidence codes:";

	private final Window parent;

	private final CyPluginAdapter adapter;

	/**
	 * This constructor creates the panel with its swing-components.
	 */
	public SettingsPanel(Window parent, String pingoDir, final CyPluginAdapter adapter) {
		super();
		this.adapter = adapter;
		this.parent = parent;
		this.pingoDir = pingoDir;

		// Create a new Pingo parameter set
		try {
			params = new PingoParameters(pingoDir);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error opening the properties file." + "\n"
					+ "Please make sure that there is pingo_gui.properties file" + "\n"
					+ "in the PiNGO.jar or in your cytoscape plugins directory.");
		}
		pingo_props = params.getPingo_props();

		// create the JComponents.
		makeJComponents();

		setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
		setOpaque(true);
		// create border.
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "PiNGO settings", 0, 0,
				new Font("PiNGO settings", Font.BOLD, 16), Color.black));

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
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(goButton, c);
		add(goButton);
		c.gridx = 3;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(helpButton, c);
		add(helpButton);

		JPanel dummyPanel2 = new JPanel();
		dummyPanel2.setOpaque(false);
		dummyPanel2.setPreferredSize(new Dimension(50, 20));
		c.gridx = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(dummyPanel2, c);
		add(dummyPanel2);

		c.weighty = 1;
		c.weightx = 100;
		c.gridx = 1;
		c.gridwidth = 3;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.HORIZONTAL;

		gridbag.setConstraints(nameLabel, c);
		add(nameLabel);

		gridbag.setConstraints(nameField, c);
		add(nameField);

		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(startGoCatLabel, c);
		add(startGoCatLabel);
		c.gridx = 3;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(startGoCatButton, c);
		add(startGoCatButton);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.weighty = 1;
		gridbag.setConstraints(startGoCatInputField, c);
		add(startGoCatInputField);

		/*
		 * startGoCatInputField.setEditable(true);
		 * startGoCatInputField.setText(DEF_MESSAGE);
		 * startGoCatInputField.addMouseListener(new GoTreeDialogListener(
		 * startGoCatInputField));
		 * startGoCatInputField.setForeground(Color.BLUE);
		 * startGoCatInputField.setToolTipText
		 * ("Double-click to open ontology term search window...");
		 */

		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(filterGoCatLabel, c);
		add(filterGoCatLabel);
		c.gridx = 3;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(filterGoCatButton, c);
		add(filterGoCatButton);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.weighty = 1;
		gridbag.setConstraints(filterGoCatInputField, c);
		add(filterGoCatInputField); /*
									 * filterGoCatInputField.setEditable(true);
									 * filterGoCatInputField
									 * .setText(DEF_MESSAGE);
									 * filterGoCatInputField
									 * .addMouseListener(new
									 * GoTreeDialogListener(
									 * filterGoCatInputField));
									 * filterGoCatInputField
									 * .setForeground(Color.BLUE);
									 * filterGoCatInputField.setToolTipText(
									 * "Double-click to open ontology term search window..."
									 * );
									 */
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(targetGoCatLabel, c);
		add(targetGoCatLabel);
		c.gridx = 3;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(targetGoCatButton, c);
		add(targetGoCatButton);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.weighty = 1;
		gridbag.setConstraints(targetGoCatInputField, c);
		add(targetGoCatInputField);
		/*
		 * targetGoCatInputField.setEditable(true);
		 * targetGoCatInputField.setText(DEF_MESSAGE);
		 * targetGoCatInputField.addMouseListener(new GoTreeDialogListener(
		 * targetGoCatInputField));
		 * targetGoCatInputField.setForeground(Color.BLUE);
		 * targetGoCatInputField
		 * .setToolTipText("Double-click to open ontology term search window..."
		 * );
		 */
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.weighty = 1;

		gridbag.setConstraints(vizPanel, c);
		add(vizPanel);

		gridbag.setConstraints(graphLabel, c);
		add(graphLabel);

		// include custom ref set option
		gridbag.setConstraints(graphPanel, c);
		add(graphPanel);

		gridbag.setConstraints(geneDescriptionLabel, c);
		add(geneDescriptionLabel);

		// include custom ref set option
		gridbag.setConstraints(geneDescriptionPanel, c);
		add(geneDescriptionPanel);

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

		gridbag.setConstraints(pingoButton, c);
		add(pingoButton);

		validate();
	}

	/*----------------------------------------------------------------
	PAINTCOMPONENT.
	----------------------------------------------------------------*/

	/**
	 * Paintcomponent method that paints the panel.
	 * 
	 * @param g
	 *            Graphics-object.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	/*----------------------------------------------------------------
	METHODS.
	----------------------------------------------------------------*/

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

		// JTextField.
		alphaField = new JTextField(pingo_props.getProperty("signif_def"));
		nameField = new JTextField();
		if (pingo_props.getProperty("start_go_def").equals(BingoAlgorithm.NONE)) {
			startGoCatInputField = new JTextField();
		} else {
			startGoCatInputField = new JTextField(pingo_props.getProperty("start_go_def"));
		}
		if (pingo_props.getProperty("filter_go_def").equals(BingoAlgorithm.NONE)) {
			filterGoCatInputField = new JTextField();
		} else {
			filterGoCatInputField = new JTextField(pingo_props.getProperty("filter_go_def"));
		}
		if (pingo_props.getProperty("target_go_def").equals(BingoAlgorithm.NONE)) {
			targetGoCatInputField = new JTextField();
		} else {
			targetGoCatInputField = new JTextField(pingo_props.getProperty("target_go_def"));
		}

		// JButton

		helpButton = new JButton("Help");
		helpButton.setMnemonic(KeyEvent.VK_H);
		helpButton.addActionListener(new HelpButtonActionListener(this));

		goButton = new JButton("Search Amigo");
		goButton.setMnemonic(KeyEvent.VK_G);
		goButton.addActionListener(new AmigoButtonActionListener(this));

		startGoCatButton = new JButton("Search IDs");
		startGoCatButton.addActionListener(new GoSearchButtonActionListener(startGoCatInputField));

		filterGoCatButton = new JButton("Search IDs");
		filterGoCatButton.addActionListener(new GoSearchButtonActionListener(filterGoCatInputField));

		targetGoCatButton = new JButton("Search IDs");
		targetGoCatButton.addActionListener(new GoSearchButtonActionListener(targetGoCatInputField));

		saveSettingsButton = new JButton("Save settings as default");
		saveSettingsButton.setMnemonic(KeyEvent.VK_S);
		saveSettingsButton.addActionListener(new SaveSettingsButtonActionListener(this));

		// JComboboxes.
		testBox = new JComboBox(testsArray);
		testBox.setSelectedItem(pingo_props.getProperty("tests_def"));
		correctionBox = new JComboBox(correctionArray);
		correctionBox.setSelectedItem(pingo_props.getProperty("correction_def"));

		graphPanel = new ChooseGraphPanel(this, pingoDir, graphArray, pingo_props.getProperty("graph_def"));

		geneDescriptionPanel = new GeneDescriptionPanel(this, pingoDir, geneDescriptionArray,
				pingo_props.getProperty("genedescription_def"));

		clusterVsPanel = new ChooseRefSetPanel(this, pingoDir, clusterVsArray, pingo_props.getProperty("refset_def"));

		// JLabels.
		nameLabel = new JLabel(clustername_label);
		startGoCatLabel = new JLabel(startgocat_label);
		filterGoCatLabel = new JLabel(filtergocat_label);
		targetGoCatLabel = new JLabel(targetgocat_label);
		testLabel = new JLabel(test_label);
		correctionLabel = new JLabel(correction_label);
		alphaLabel = new JLabel(sig_label);
		clusterVsLabel = new JLabel(ref_label);
		graphLabel = new JLabel(graph_label);
		geneDescriptionLabel = new JLabel(genedescription_label);
		testLabel.setForeground(Color.black);
		correctionLabel.setForeground(Color.black);
		alphaLabel.setForeground(Color.black);
		clusterVsLabel.setForeground(Color.black);
		graphLabel.setForeground(Color.black);
		ecLabel = new JLabel(ec_label);

		// vizPanel
		vizPanel = new VizPanel(pingo_props.getProperty("visual_def"), pingo_props.getProperty("tab_def"),
				pingo_props.getProperty("star_def"));

		// annotationPanel.
		annotationLabel = new JLabel(annotation_label);
		annotationPanel = new ChooseAnnotationPanel(this, pingoDir, params.getSpeciesLabels(),
				pingo_props.getProperty("species_def"));

		// evidence code Field
		ecField = new JTextField();

		// ontologyPanel.

		namespaceLabel = new JLabel(namespace_label);
		namespacePanel = new ChooseNamespacePanel(this, pingoDir, params.getNamespaceLabels(),
				pingo_props.getProperty("namespace_def"));

		ontologyLabel = new JLabel(ontology_label);
		ontologyPanel = new ChooseOntologyPanel(this, pingoDir, params.getOntologyLabels(),
				pingo_props.getProperty("ontology_file_def"));

		// Creating SaveResultsPanels.
		dataPanel = new SaveResultsPanel("Data", this, pingoDir, pingo_props.getProperty("outputdir_def"), new Boolean(
				pingo_props.getProperty("file_output")));

		// the PiNGO-button to start the calculations.
		pingoButton = new JButton("Start PiNGO");
		pingoButton.setMnemonic(KeyEvent.VK_B);
		pingoButton.addActionListener(new SettingsPanelActionListener(params, this, adapter));

	}

	/*----------------------------------------------------------------
	GETTERS.
	----------------------------------------------------------------*/

	JTextField getStartGoCatInputField() {
		return startGoCatInputField;
	}

	JTextField getFilterGoCatInputField() {
		return filterGoCatInputField;
	}

	JTextField getTargetGoCatInputField() {
		return targetGoCatInputField;
	}

	VizPanel getVizPanel() {
		return vizPanel;
	}

	/**
	 * Get the testBox.
	 * 
	 * @return JCombobox testBox.
	 */

	JComboBox getTestBox() {
		return testBox;
	}

	/**
	 * Get the correctionBox.
	 * 
	 * @return JCombobox correctionBox.
	 */
	JComboBox getCorrectionBox() {
		return correctionBox;
	}

	/**
	 * Get the clusterVsBox.
	 * 
	 * @return JCombobox clusterVsBox.
	 */
	ChooseRefSetPanel getClusterVsPanel() {
		return clusterVsPanel;
	}

	ChooseGraphPanel getGraphPanel() {
		return graphPanel;
	}

	GeneDescriptionPanel getGeneDescriptionPanel() {
		return geneDescriptionPanel;
	}

	/**
	 * Get the alphaField.
	 * 
	 * @return JTextField alphaField.
	 */
	JTextField getAlphaField() {
		return alphaField;
	}

	/**
	 * Get the nameField.
	 * 
	 * @return JTextField alphaField.
	 */

	JTextField getNameField() {
		return nameField;
	}

	JTextField getEcField() {
		return ecField;
	}

	/**
	 * Get the annotationPanel.
	 * 
	 * @return ChooseAnnotationPanel annotationPanel.
	 */
	ChooseAnnotationPanel getAnnotationPanel() {
		return annotationPanel;
	}

	/**
	 * Get the ontologyPanel.
	 * 
	 * @return ChooseOntologyPanel ontologyPanel.
	 */
	ChooseOntologyPanel getOntologyPanel() {
		return ontologyPanel;
	}

	ChooseNamespacePanel getNamespacePanel() {
		return namespacePanel;
	}

	/**
	 * Get the dataPanel.
	 * 
	 * @return SaveResultsPanel dataPanel.
	 */
	SaveResultsPanel getDataPanel() {
		return dataPanel;
	}

	/** get the PiNGO directory path */
	String getPingoDir() {
		return pingoDir;
	}

	Properties getPingoProps() {
		return pingo_props;
	}

	PingoParameters getParams() {
		return params;
	}

	private class GoSearchButtonActionListener implements ActionListener {

		private final JTextField field;

		public GoSearchButtonActionListener(final JTextField field) {
			this.field = field;
		}

		public void actionPerformed(ActionEvent e) {
			showGOTreeDialog();
		}

		private void showGOTreeDialog() {
			final TermSearchGUI dialog = new TermSearchGUI(parent, createURL());
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);

			final String originalTerm = field.getText();
			final String goTerm = dialog.getTerm();

			if (goTerm != null) {
				if (goTerm.trim().length() == 0)
					return;
				else if (originalTerm.equals(DEF_MESSAGE))
					field.setText(goTerm);
				else if (originalTerm.isEmpty() || originalTerm.substring(originalTerm.length() - 1).matches("\\s"))
					field.setText(originalTerm + goTerm);
				else
					field.setText(originalTerm + " " + goTerm);
			}
			dialog.dispose();
		}

		private URL createURL() {
			final URL targetURL;
			final String selectedItem = ontologyPanel.getSpecifiedOntology();

			System.out.println("Selected = " + selectedItem);
			System.out.println("Selected item = " + ontologyPanel.getSelection());
			if (selectedItem.equals("Custom...") == false)
				targetURL = BiNGOplugin.class.getClassLoader().getResource(selectedItem);
			else {
				final File localFile = new File(ontologyPanel.getSelection());
				try {
					targetURL = localFile.toURI().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
			}

			System.out.println("GOT new URL = " + targetURL);
			return targetURL;
		}
	}

	/*
	 * private class GoTreeDialogListener extends MouseAdapter {
	 * 
	 * private final JTextField field;
	 * 
	 * public GoTreeDialogListener(final JTextField field) { this.field = field;
	 * }
	 * 
	 * @Override public void mouseClicked(MouseEvent e) { if(e.getClickCount()
	 * == 2) showGOTreeDialog(); }
	 * 
	 * private void showGOTreeDialog() { final TermSearchGUI dialog = new
	 * TermSearchGUI(parent, createURL()); dialog.setLocationRelativeTo(parent);
	 * dialog.setVisible(true);
	 * 
	 * final String originalTerm = field.getText(); final String goTerm =
	 * dialog.getTerm();
	 * 
	 * if (goTerm != null) { if (goTerm.trim().length() == 0) return; else if
	 * (originalTerm.equals(DEF_MESSAGE)) field.setText(goTerm); else
	 * field.setText(originalTerm + goTerm); } dialog.dispose(); }
	 * 
	 * private URL createURL() { final URL targetURL; final String selectedItem
	 * = ontologyPanel.getSpecifiedOntology();
	 * 
	 * System.out.println("Selected = " + selectedItem);
	 * System.out.println("Selected item = " + ontologyPanel.getSelection());
	 * if(selectedItem.equals("Custom...") == false) targetURL =
	 * BiNGOplugin.class.getClassLoader().getResource(selectedItem); else {
	 * final File localFile = new File(ontologyPanel.getSelection()); try {
	 * targetURL = localFile.toURI().toURL(); } catch (MalformedURLException e)
	 * { e.printStackTrace(); return null; } }
	 * 
	 * System.out.println("GOT new URL = " + targetURL); return targetURL; } }
	 */
}
