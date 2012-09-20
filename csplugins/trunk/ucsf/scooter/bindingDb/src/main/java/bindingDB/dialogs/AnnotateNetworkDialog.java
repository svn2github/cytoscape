/* vim: set ts=2: */
/**
 * Copyright (c) 2012 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package bindingDB.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingWorker;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.task.util.TaskManager;

import bindingDB.tasks.AnnotateNetworkTask;
import bindingDB.tasks.TranslateIdentifiersTask;
import bindingDB.tasks.BridgeDBUtils;

public class AnnotateNetworkDialog extends JDialog implements ActionListener {
	private CyLogger logger = null;
	private int TOP = 10;
	private int LEFT = 10;
	private int COLUMN1 = 175;
	private int WIDTH = 500;
	private int HEIGHT = 225;
	private int RIGHT = WIDTH-20;

	private boolean haveCyThesaurus = false;

	private boolean selectedOnly = false;

	JComboBox typeList;
	JComboBox idList;
	JComboBox speciesList;
	JSlider cutoffSlider;
	JLabel speciesLabel;

	// Default species list
	static final String[] defaultSpecies = {"Human"};
	// Default type list
	static final String[] defaultTypes = {"Uniprot/TrEMBL"};

	static final String[] loadingString = {"Loading..."};

	public AnnotateNetworkDialog(CyLogger logger, boolean selectedOnly) { 
		super(Cytoscape.getDesktop(), "Annotate network"); 
		this.logger = logger;
		this.selectedOnly = selectedOnly;

		haveCyThesaurus = BridgeDBUtils.haveCyThesaurus();
		initComponents();
		this.pack();
		this.setSize(WIDTH, HEIGHT);
	}

	private void initComponents() {
		// Create a panel for the identifier stuff
		JPanel attrPanel = new JPanel();
		attrPanel.setLayout(null);

		int y = TOP;

		// The identifier to use for UniProt.  We'll either use this identifier directly
		// or use it to translate to UniProt
		{
			JLabel idLabel = new JLabel("Identifier");
			idLabel.setToolTipText("The attribute that represents the identifier to use.");
			attrPanel.add(idLabel);
			idLabel.setBounds(LEFT, y, idLabel.getPreferredSize().width, 25);

			idList = new JComboBox(getAttributeList());
			idList.setToolTipText("Select the attribute to use as an identifier.");
			attrPanel.add(idList);
			idList.setBounds(COLUMN1, y, idList.getPreferredSize().width, 25);
		}

		// Get the species list here so we can register the right services
		String[] species = getSpeciesList();

		y += 25;

		// Get the type of the identifier.  If it's uniprot, we won't translate
		// otherwise, we'll have CyThesaurus translate it.
		{
			JLabel typeLabel = new JLabel("Identifier type");
			attrPanel.add(typeLabel);
			typeLabel.setBounds(LEFT, y, typeLabel.getPreferredSize().width, 25);

			if (haveCyThesaurus) {
				typeList = new JComboBox(getTypeList("Human"));
				typeList.setToolTipText("If the attribute type is not Uniprot/TrEMBL, it will be converted");
				typeList.addActionListener(this);
			} else {
				typeList = new JComboBox(defaultTypes);
				typeList.setSelectedItem("Uniprot/TrEMBL");
				typeList.setToolTipText("CyThesaurus is unavailable -- only Uniprot/TrEMBL is supported");
			}
			typeList.setEnabled(false);
			attrPanel.add(typeList);
			typeList.setBounds(COLUMN1, y, 150, 25);
		}

		// Show the species.  This is only enabled if the Idenfier type is not Uniprot/trembl
		// and CyThesaurus is available
		if (haveCyThesaurus) {
			y += 25;
			speciesLabel = new JLabel("Species");
			speciesLabel.setEnabled(false);
			attrPanel.add(speciesLabel);
			speciesLabel.setBounds(LEFT, y, speciesLabel.getPreferredSize().width, 25);

			speciesList = new JComboBox(species);
			speciesList.setSelectedItem("Human");
			speciesList.setEnabled(false);
			speciesList.setToolTipText("If the attribute type is not Uniprot/TrEMBL, it will be converted");
			speciesList.addActionListener(this);
			attrPanel.add(speciesList);
			speciesList.setBounds(COLUMN1, y, speciesList.getPreferredSize().width, 25);
		}

		y += 35;

		// Add a slider for the affinity cutoff
		{
			JLabel cutoffLabel = new JLabel("Affinity cutoff (in nM)");
			attrPanel.add(cutoffLabel);
			cutoffLabel.setBounds(LEFT, y, cutoffLabel.getPreferredSize().width, 25);

			cutoffSlider = new JSlider(0, 100, 10);
			cutoffSlider.setMajorTickSpacing(100);
			cutoffSlider.setMinorTickSpacing(10);
			cutoffSlider.setPaintTicks(true);
			cutoffSlider.setPaintLabels(true);
			attrPanel.add(cutoffSlider);
			cutoffSlider.setBounds(COLUMN1, y, RIGHT-COLUMN1, cutoffSlider.getPreferredSize().height);
		}

		// Create a panel to report on the results

		y += 50;
		// Create the button box
		JPanel buttonBox = new JPanel();
		buttonBox.setLayout(null);
		JButton annotateButton = new JButton("Annotate");
		annotateButton.setActionCommand("annotate");
		annotateButton.addActionListener(this);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);

		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.add(annotateButton);
		annotateButton.setBounds(20, 10, annotateButton.getPreferredSize().width, annotateButton.getPreferredSize().height);
		buttonBox.add(cancelButton);
		cancelButton.setBounds(WIDTH-150, 10, cancelButton.getPreferredSize().width, cancelButton.getPreferredSize().height);
		attrPanel.add(buttonBox);
		buttonBox.setBounds(LEFT, y, WIDTH-30, 50);
		setContentPane(attrPanel);
		attrPanel.setBounds(LEFT, TOP, WIDTH, y+50);
	}

	public void actionPerformed(ActionEvent e) {

		// typeList changed?
		if (e.getSource() == typeList) {
			// Get the value that's set
			if (typeList.getSelectedItem().equals("Uniprot/TrEMBL")) {
				speciesList.setEnabled(false);
				speciesLabel.setEnabled(false);
			} else if (typeList.getSelectedItem().equals("Guess")) {
				// TODO: At some point, we need to add the guess type functionality....
			} else {
				speciesList.setEnabled(true);
				speciesLabel.setEnabled(true);
			}
			return;

		// Species changed?
		} else if (e.getSource() == speciesList) {
			// We changed the species -- update typelist
			updateTypesList(getTypeList((String)speciesList.getSelectedItem()));
			return;
		}

		if (e.getActionCommand().equals("cancel")) {
			dispose();
			return;
		}

		if (e.getActionCommand().equals("annotate")) {
			String type = (String)typeList.getSelectedItem();
			String id = (String)idList.getSelectedItem();
			double cutoff = (double)cutoffSlider.getValue();

			if (!type.equals("Uniprot/TrEMBL")) {
				// Translate identifiers if we have CyThesaurus
				AnnotateNetworkTask annotate = new AnnotateNetworkTask("Uniprot/TrEMBL", cutoff, logger, selectedOnly);
				TranslateIdentifiersTask translate = new TranslateIdentifiersTask(annotate, type, id, logger);
				TaskManager.executeTask(translate, translate.getDefaultTaskConfig());
			} else {
				// Annotate the network
				AnnotateNetworkTask annotate = new AnnotateNetworkTask(id, cutoff, logger, selectedOnly);
				TaskManager.executeTask(annotate, annotate.getDefaultTaskConfig());
			}
			dispose();
			return;
		}
	}

	private String[] getAttributeList() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		// Get the list of node attributes
		String[] attrNames = nodeAttributes.getAttributeNames();
		Arrays.sort(attrNames);
		List<String>attrList = new ArrayList<String>();
		for (String attr: attrNames) {
			byte type = nodeAttributes.getType(attr);
			// Only add attribute types that can be used for identifiers
			if (type == CyAttributes.TYPE_STRING ||
			    type == CyAttributes.TYPE_INTEGER ||
			    type == CyAttributes.TYPE_FLOATING ||
			    type == CyAttributes.TYPE_SIMPLE_LIST) {
				attrList.add(attr);
			}
		}

		return attrList.toArray(new String[1]);
	}

	private String[] getTypeList(final String species) {
		if (!haveCyThesaurus) {
			return defaultTypes;
		}

		TypesLoader loader = new TypesLoader(species);
		loader.execute();

		if (typeList != null)
			typeList.setEnabled(false);

		return loadingString;
	}

	private String[] getSpeciesList() {
		String[] species = BridgeDBUtils.getSpeciesList(logger);
		if (species == null) {
			String[] unavailable = {"BridgeDB is Unavailable"};
			return unavailable;
		}
		return species;
	}

	private void updateTypesList(String[] types) {
		typeList.removeActionListener(this);
		DefaultComboBoxModel model = (DefaultComboBoxModel)typeList.getModel();
		model.removeAllElements();
			
		for (String t: types) {
			model.addElement(t);
			if (t.equals("Uniprot/TrEMBL")) {
				typeList.setSelectedItem(t);
			}
		}
		typeList.addActionListener(this);
	}

	class TypesLoader extends SwingWorker<String[], Void> {
		String species;

		public TypesLoader(String species) {
			this.species = species;
		}

		@Override
		public String[] doInBackground() {
			return BridgeDBUtils.getSupportedTypes(logger, species);
		}

		@Override
		public void done() {
			String[] types = defaultTypes;
			boolean gotResult = true;
			try {
				types = get();
			} catch (InterruptedException ignore) {
			} catch (java.util.concurrent.ExecutionException e) {
				logger.error("Unable to load types for species "+species+": "+e.getMessage());
				gotResult = false;
			}

			updateTypesList(types);
			typeList.setEnabled(true);
		}
	}
}
