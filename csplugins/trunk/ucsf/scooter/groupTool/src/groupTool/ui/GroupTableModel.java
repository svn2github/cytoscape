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
package groupTool.ui;

import javax.swing.table.AbstractTableModel;

// System imports
import java.util.List;
import java.util.ArrayList;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;


/**
 * The GroupTableModel class provides the table model used by the GroupToolDialog
 *
 * @author scooter
 * @see GroupToolDialog
 */
public class GroupTableModel 
             extends AbstractTableModel {

	private GroupToolDialog groupDialog = null;
	private List<CyGroup> groupList = null;

	/**
	 * Create the table model
	 *
	 * @param groupDialog a back-pointer to the dialog itself
	 */
	public GroupTableModel(GroupToolDialog groupDialog) {
		this.groupDialog = groupDialog;
		this.groupList = CyGroupManager.getGroupList();
	}

	/**
	 * Return the number of rows in the table
	 *
	 * @return number of rows as an integer
	 */
	public int getRowCount() { 
		return groupList.size(); 
	}

	/**
	 * Return the number of columns in the table
	 *
	 * @return 5
	 */
	public int getColumnCount() { return Columns.columnCount(); }

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
		CyGroup group = groupList.get(row);

		Columns column = Columns.getColumn(col);
		switch (column) {

		case NAME:
			return group.getGroupName();
		case NETWORK:
			CyNetwork net = group.getNetwork();
			if (net != null)
				return net.getTitle();
			return "Global";
		case INTERNAL:
			return new Integer(group.getInnerEdges().size());
		case EXTERNAL:
			return new Integer(group.getOuterEdges().size());
		case NODES:
			return new Integer(group.getNodes().size());
		case VIEWER:
			return group.getViewer();

		}

		return null;
	}

	/**
	 * This method indicates whether this cell is editable.
	 *
	 * @param row row number as an integer
	 * @param col column number as an integer
	 * @return false
	 */
	public boolean isCellEditable(int row, int col) {
		Columns column = Columns.getColumn(col);
		if (column == Columns.VIEWER || column == Columns.NETWORK) 
			return true;

		return false;
	}

	/**
	 * Return the name of a column.
	 *
	 * @param col column number as an integer
	 * @return column name as a String
	 */
	public String getColumnName(int col) {
		return Columns.getColumnName(col);
	}

	/**
	 * Get the object class of a column.  This is used to determine how
	 * the columns will be displayed
	 *
	 * @param c the column number as an integer
	 * @return object Class of this column
	 */
	public Class getColumnClass(int c) {
		return Columns.getColumn(c).getColumnClass();
	}

	public CyGroup getGroupAtRow(int row) {
		return groupList.get(row);
	}

	/**
	 * Force the table to update
	 */
	public void updateTable() {
		this.groupList = CyGroupManager.getGroupList();
		fireTableDataChanged(); 
	}
}

