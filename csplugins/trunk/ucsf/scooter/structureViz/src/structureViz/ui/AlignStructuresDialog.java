/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
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
package structureViz.ui;

// System imports
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.WindowConstants.*;
import javax.swing.border.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.event.*;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import giny.view.NodeView;

// StructureViz imports
import structureViz.model.AlignmentTableModel;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;
import structureViz.model.TableSorter;
import structureViz.actions.CyChimera;
import structureViz.actions.Chimera;
import structureViz.actions.Align;

/**
 * The AlignStructuresDialog is the dialog displayed to the user to allow them
 * to select a reference structure, all of the structures to align to that
 * reference structure, and the options controlling the alignment
 */
public class AlignStructuresDialog extends JDialog implements ActionListener {
	// Instance variables
	Chimera chimeraObject;
	List structures;
	boolean status;
	boolean useChains;
	Object referenceStruct;

	// Dialog components
	private JLabel titleLabel;
	private JTable resultsTable;
	private JPanel buttonBox;
	private JPanel checkBoxes;
	private JButton alignButton;
	private	JCheckBox showSequence;
	private JCheckBox assignResults;

	// Models
	private AlignmentTableModel tableModel;

	/**
	 * Create an AlignStructuresDialog
	 *
	 * @param parent the Frame acting as the parent of this Dialog
	 * @param object the Chimera interface object
	 * @param structures the List of structures open in Chimera
	 */
	public AlignStructuresDialog (Dialog parent, Chimera object, List structures) {
		super(parent, false);
		chimeraObject = object;
		this.structures = structures;
		useChains = true;
		if (structures.size() > 0 && structures.get(0) instanceof Structure)
			useChains = false;
		initComponents();
		status = false;
	}

	/**
	 * Set the reference structure for the pairwise alignments
	 *
	 * @param ref the reference structure
	 */
	public void setReferenceStruct(Object ref) {
		this.referenceStruct = ref;
		// update the table model
		tableModel.setReferenceStruct(ref);
/*
		if (ref instanceof Structure)
			tableModel.setReferenceStruct(((Structure)ref).name());
		else if (ref instanceof ChimeraChain) {
			tableModel.setReferenceStruct(referenceStruct.toString());
		}
*/
	}

	/**
	 * This method is called to set the <b>enabled</b> state of the
	 * align button.  We only want to enable this button when we have
	 * a reference structure and at least one structure to align to it.
	 *
	 * @param value "true" to enable the button, "false" to disable it
	 */
	public void setAlignEnabled(boolean value) {
		if (alignButton != null) alignButton.setEnabled(value);
	}

	/**
	 * Initialize all of the graphical components of the dialog
	 */
	private void initComponents() {
		this.setTitle("Cytoscape/Chimera Structure Alignment Dialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create a panel for the main content
		JPanel dataPanel = new JPanel();
		BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS);
		dataPanel.setLayout(layout);

		// Create the menu for the reference structure
		JPanel refBox = new JPanel();
		JComboBox refStruct = new JComboBox(structureList(structures));
		refStruct.addActionListener(new setRefStruct());
		refBox.add(refStruct);

		Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "Reference Structure");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);

		refBox.setBorder(titleBorder);
		refBox.setMaximumSize(new Dimension(1000,80));
		dataPanel.add(refBox);

		// Create the results table
		tableModel = new AlignmentTableModel(chimeraObject, structures, this);

		TableSorter sorter = new TableSorter(tableModel);
		JTable results = new JTable(sorter);
		sorter.setTableHeader(results.getTableHeader());

		ListSelectionModel lsm = results.getSelectionModel();
		lsm.addListSelectionListener(tableModel);

		JScrollPane scrollPane = new JScrollPane(results);
		results.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// lots more goes here
		dataPanel.add(scrollPane);

		// Create the checkbox
		JPanel checkBoxes = new JPanel(new GridLayout(2, 1));
		showSequence = new JCheckBox("Show sequence panel for each alignment");
		checkBoxes.add(showSequence);
		assignResults = new JCheckBox("Assign results to Cytoscape edge attributes");
		checkBoxes.add(assignResults);
		checkBoxes.setBorder(new CompoundBorder(
									BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
									new EmptyBorder(10,10,10,10)));
		checkBoxes.setMaximumSize(new Dimension(1000,80));
		dataPanel.add(checkBoxes);

		// Create the button box
		JPanel buttonBox = new JPanel();
		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		alignButton = new JButton("Align");
		alignButton.setActionCommand("align");
		alignButton.setEnabled(false);
		alignButton.addActionListener(this);
		buttonBox.add(doneButton);
		buttonBox.add(alignButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.setMaximumSize(new Dimension(1000,35));
		
		dataPanel.add(buttonBox);
		setContentPane(dataPanel);
	}
	
	/**
	 * Return an array of objects that represent a List of structures, with the
	 * first element being a String that indicates that nothing is selected.
	 *
	 * @param structures a List of structures to be included in the menu
	 * @return an array of Objects to be used in a menu
	 */
	private Object[] structureList(List structures) {
		Object[] structureList = new Object[structures.size()+1];
		int index = 0;
		structureList[index++] = new String("      ---------");
		for (Object st: structures) {
			structureList[index++] = st;
		}
		return structureList;
	}
	
	/**
	 * The method called to actually execute the command.
	 */
	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			setVisible(false);
		}
		else if ("align".equals(e.getActionCommand())) {
			Align alignment = new Align(chimeraObject);

			if (showSequence.isSelected()) {
				alignment.setShowSequence(true);
			}

			if (assignResults.isSelected()) {
				alignment.setCreateEdges(true);
			}

			if (!useChains) {
				Structure rStruct = (Structure)referenceStruct;
				if (chimeraObject.getModel(rStruct.name()) == null) {
					chimeraObject.open(rStruct);
				}

				List<Structure> matchList = tableModel.getSelectedStructures();
				for (Structure match: matchList) {
					if (chimeraObject.getModel(match.name()) == null) {
						chimeraObject.open(match);
					}
				}
				chimeraObject.modelChanged();

				// Align them
				alignment.align(rStruct, matchList);

				// Display the results
				for (Structure structure: matchList) {
					float[] results = alignment.getResults(structure.name());
					tableModel.setResults(structure.name(), results);
				}
				tableModel.updateTable();
			} else {
				ChimeraStructuralObject refChain = (ChimeraStructuralObject)referenceStruct;
				List<ChimeraStructuralObject> matchList = tableModel.getSelectedStructures();

				// Align them
				alignment.align(refChain, matchList);

				// Display the results
				for (ChimeraStructuralObject chain: matchList) {
					float[] results = alignment.getResults(chain.toString());
					tableModel.setResults(chain.toString(), results);
				}
				tableModel.updateTable();
			}
		}
	}

	private class setRefStruct implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox)e.getSource();
			Object sel = cb.getSelectedItem();
			if (sel instanceof Structure) {
				setReferenceStruct((Structure)sel);
			} else if (sel instanceof ChimeraChain) {
				setReferenceStruct((ChimeraChain)sel);
			} else {
				setReferenceStruct(null);
			}
		}
	}
}

