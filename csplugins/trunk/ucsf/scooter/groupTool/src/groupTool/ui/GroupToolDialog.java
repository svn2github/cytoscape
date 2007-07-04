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

import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.WindowConstants.*;
import javax.swing.border.*;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.event.*;

import cytoscape.groups.*;
import cytoscape.Cytoscape;

import groupTool.ui.GroupTableModel;

/**
 * The GroupToolDialog is the dialog displayed to the user to allow them
 * to manipulate all groups, reguardless of what their viewer is.
 */
public class GroupToolDialog extends JDialog 
                             implements ActionListener, 
                                        CellEditorListener, 
                                        CyGroupChangeListener {

	// Dialog components
	private JLabel titleLabel;
	private JTable groupTable;
	private JPanel buttonBox;
	private JButton reloadButton;
	private JButton doneButton;

	// Models
	private GroupTableModel tableModel;

	/**
	 * Create a GroupToolDialog
	 *
	 * @param parent the Frame acting as the parent of this Dialog
	 */
	public GroupToolDialog (Frame parent) {
		super(parent, false);
		initComponents();
	}

	/**
	 * Initialize all of the graphical components of the dialog
	 */
	private void initComponents() {
		this.setTitle("Group Dialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create a panel for the main content
		JPanel dataPanel = new JPanel();
		BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS);
		dataPanel.setLayout(layout);

		// Create the group table
		this.tableModel = new GroupTableModel(this);

		TableSorter sorter = new TableSorter(tableModel);
		this.groupTable = new JTable(sorter);
		sorter.setTableHeader(groupTable.getTableHeader());

		setUpViewerRenderer(groupTable, groupTable.getColumnModel().getColumn(4));
		groupTable.setCellSelectionEnabled(true);

		JScrollPane scrollPane = new JScrollPane(groupTable);
		groupTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// lots more goes here
		dataPanel.add(scrollPane);

		// Create the button box
		JPanel buttonBox = new JPanel();
		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		reloadButton = new JButton("Reload");
		reloadButton.setActionCommand("reload");
		reloadButton.setEnabled(true);
		reloadButton.addActionListener(this);
		buttonBox.add(doneButton);
		buttonBox.add(reloadButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		dataPanel.add(buttonBox);
		setContentPane(dataPanel);

		// Set up ourselves as listeners
		CyGroupManager.addGroupChangeListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			CyGroupManager.removeGroupChangeListener(this);
			this.dispose();
		} else if ("reload".equals(e.getActionCommand())) {
			tableModel.updateTable();
		}
	}

	private void setUpViewerRenderer(JTable table, TableColumn viewerColumn) {
		JComboBox vBox = new JComboBox();
		Collection<CyGroupViewer> viewers = CyGroupManager.getGroupViewers();
		Iterator <CyGroupViewer> vIter = viewers.iterator();
		while (vIter.hasNext()) {
			CyGroupViewer gv = vIter.next();
			vBox.addItem(gv.getViewerName());
		}
		DefaultCellEditor editor = new DefaultCellEditor(vBox);
		editor.addCellEditorListener(this);
		viewerColumn.setCellEditor(editor);
	}

	public void editingCanceled(ChangeEvent e) {
		System.out.println("editingCanceled at row "+groupTable.getSelectedRow()+" and column "+groupTable.getSelectedColumn());
	}

	public void editingStopped(ChangeEvent e) {
		int row = groupTable.getSelectedRow();
		int col = groupTable.getSelectedColumn();
		System.out.println("editingStopped at row "+row+" and column "+col);
		if (row == -1 || col == -1) return;

		DefaultCellEditor editor = (DefaultCellEditor)e.getSource();
		String viewerName = (String) editor.getCellEditorValue();
		System.out.println("editor value is "+viewerName);
		String oldViewerName = (String)groupTable.getValueAt(row,col);
		if (viewerName.equals(oldViewerName)) return;
		System.out.println("Need to update viewer");
		CyGroup group = tableModel.getGroupAtRow(row);

		// Set the new viewer
		CyGroupManager.setGroupViewer(group, viewerName, Cytoscape.getCurrentNetworkView(), true);
	}

	public void groupChanged (CyGroup group, CyGroupChangeListener.ChangeType change) {
		// Whatever changed, we just update our table
		tableModel.updateTable();
	}
}
