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
package structureViz.model;

// System imports
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.Position;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import giny.view.NodeView;

// StructureViz imports
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;
import structureViz.actions.CyChimera;
import structureViz.actions.Chimera;
import structureViz.actions.Align;
import structureViz.ui.AlignStructuresDialog;

/**
 * The AlignmentTableModel class provides the table model used by the AlignStructuresDialog
 *
 * @author scooter
 * @see AlignStructuresDialog
 */
public class AlignmentTableModel extends AbstractTableModel implements ListSelectionListener {

	public static final int NOREFERENCE = 0;
	public static final int REFERENCE = 1;
	public static final int MATCHLIST = 2;
	public static final int RESULTS = 3;
	private static final String[] columnNames = {"Match Structures", "Aligned Pairs", "RMSD", "Score"};

	private String referenceStructure = null;
	private List matchStructures = null;
	private Chimera chimeraObject = null;
	private List allStructures = null;
	private List selectedStructures = null;
	private HashMap resultsMap;
	private int state = NOREFERENCE;
	AlignStructuresDialog asDialog = null;

	/**
	 * Create the table model
	 *
	 * @param chimeraObject the Chimera object that links to Chimera
	 * @param structures the list of Structures involved
	 * @param asDialog a back-pointer to the dialog itself
	 */
	public AlignmentTableModel(Chimera chimeraObject, List structures, 
														 AlignStructuresDialog asDialog) {
		this.chimeraObject = chimeraObject;
		this.allStructures = structures;
		this.asDialog = asDialog;
	}

	/**
	 * Return the number of rows in the table
	 *
	 * @return number of rows as an integer
	 */
	public int getRowCount() { 
		if (referenceStructure == null)
			return 0;
		return matchStructures.size(); 
	}

	/**
	 * Return the number of columns in the table
	 *
	 * @return 4
	 */
	public int getColumnCount() { return 4; }

	/**
	 * Return the value at the requested row and column.  In our case
	 * the row provides information about our Structure and the column
	 * indicates the specific data we want.
	 *
	 * @param row the row number
	 * @param col the column number
	 * @return an Object which represents the value at the requested 
	 * row and column
	 */
	public Object getValueAt(int row, int col) {
		if (referenceStructure == null) return null;

		Object match = matchStructures.get(row);
		String matchStruct;
		if (match instanceof Structure)
			matchStruct = ((Structure)match).name();
		else
			matchStruct = ((ChimeraStructuralObject)match).toString();
		if (col == 0) {
			return matchStruct;
		} else {
			if (resultsMap.containsKey(matchStruct)) {
				float[] results = (float[])resultsMap.get(matchStruct);
				if (col == 1) {
					return new Integer((int)results[Align.PAIRS]);
				} else if (col == 2) {
					return new Double(results[Align.RMSD]);
				} else if (col == 3) {
					return new Double(results[Align.SCORE]);
				}
			}
		}
		return null;
	}

	/**
	 * This method indicates whether this cell is editable.  We
	 * always return false.
	 *
	 * @param row row number as an integer
	 * @param col column number as an integer
	 * @return false
	 */
	public boolean isCellEditable(int row, int col) {return false;}

	/**
	 * Return the name of a column.
	 *
	 * @param col column number as an integer
	 * @return column name as a String
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Get the object class of a column.  This is used to determine how
	 * the columns will be displayed
	 *
	 * @param c the column number as an integer
	 * @return object Class of this column
	 */
	public Class getColumnClass(int c) {
		if (c == 0) return String.class;
		if (c == 1) return Integer.class;
		return Double.class;
	}

	/**
	 * Set the reference structure to use for the alignments
	 *
	 * @param refStruct the name of the structure
	 */
	public void setReferenceStruct (String refStruct) {
		this.referenceStructure = refStruct;
		if (refStruct == null) {
			this.matchStructures = null;
			this.resultsMap = null;
		} else {
			this.matchStructures = new ArrayList();
			this.resultsMap = new HashMap();
			for (Object structure: allStructures) {
				if (structure.toString().equals(refStruct)) continue;
				matchStructures.add(structure);
			}
		}
		// Update the table
		fireTableDataChanged();
	}

	/**
	 * Force the table to update
	 */
	public void updateTable() { fireTableDataChanged(); }

	/**
	 * Sets the results from an alignment in the table.
	 *
	 * @param matchStruct the name of the structure that was aligned
	 * @param results the 3 result values from the alignment
	 */
	public void setResults (String matchStruct, float[] results) {
		resultsMap.put(matchStruct, results);
	}

	/**
	 * Get the Structures that have been selected by the user
	 *
	 * @return a List of selected structures
	 */
	public List getSelectedStructures() {
		return selectedStructures;
	}

	/**
	 * This method is called whenever a value in the table is changed.
	 * It is used to detect selection and add the selection to the list
	 * of structures to be used for the alignment
	 *
	 * @param e a ListSelectionEvent
	 */
	public void valueChanged (ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		// Get the list of selected structures
		if (lsm.isSelectionEmpty()) {
			// Tell the dialog
			selectedStructures = null;
			asDialog.setAlignEnabled(false);
		} else {
			selectedStructures = new ArrayList();
			for (int i = 0; i < matchStructures.size(); i++) {
				if (lsm.isSelectedIndex(i)) {
					selectedStructures.add(matchStructures.get(i));
				}
			}
			asDialog.setAlignEnabled(true);
		}
	}
}

