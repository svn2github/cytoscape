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
import java.util.Iterator;
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

	public AlignmentTableModel(Chimera chimeraObject, List structures, 
														 AlignStructuresDialog asDialog) {
		this.chimeraObject = chimeraObject;
		this.allStructures = structures;
		this.asDialog = asDialog;
	}

	public int getRowCount() { 
		if (referenceStructure == null)
			return 0;
		return matchStructures.size(); 
	}

	public int getColumnCount() { return 4; }

	public Object getValueAt(int row, int col) {
		if (referenceStructure == null) return null;

		String matchStruct = ((Structure)matchStructures.get(row)).name();
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

	public boolean isCellEditable(int row, int col) {return false;}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Class getColumnClass(int c) {
		if (c == 0) return String.class;
		if (c == 1) return Integer.class;
		return Double.class;
	}

	public void setReferenceStruct (String refStruct) {
		this.referenceStructure = refStruct;
		if (refStruct == null) {
			this.matchStructures = null;
			this.resultsMap = null;
		} else {
			this.matchStructures = new ArrayList();
			this.resultsMap = new HashMap();
			Iterator iter = allStructures.iterator();
			while (iter.hasNext()) {
				Structure structure = (Structure)iter.next();
				if (structure.name().equals(refStruct)) continue;
				matchStructures.add(structure);
			}
		}
		// Update the table
		fireTableDataChanged();
	}

	public void updateTable() { fireTableDataChanged(); }

	public void setResults (String matchStruct, float[] results) {
		resultsMap.put(matchStruct, results);
	}

	public List getSelectedStructures() {
		return selectedStructures;
	}

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

