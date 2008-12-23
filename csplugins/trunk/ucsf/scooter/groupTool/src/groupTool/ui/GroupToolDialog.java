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

import java.util.ArrayList;
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
import cytoscape.CyNetwork;
import cytoscape.CyNode;

import groupTool.ui.GroupTableModel;

/**
 * The GroupToolDialog is the dialog displayed to the user to allow them
 * to manipulate all groups, reguardless of what their viewer is.
 */
public class GroupToolDialog extends JDialog 
                             implements ActionListener, 
                                        CellEditorListener, 
                                        ListSelectionListener,
                                        CyGroupChangeListener {

	// Dialog components
	private JLabel titleLabel;
	private JTable groupTable;
	private JPanel buttonBox;
	private JButton createButton;
	private JButton deleteButton;
	private JButton createByAttributesButton;
	private JButton clearButton;
	private JButton clearAllButton;
	private JButton selectButton;
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
		ListSelectionModel lsm = groupTable.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lsm.addListSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane(groupTable);
		groupTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// lots more goes here
		dataPanel.add(scrollPane);

		// Create the button box
		JPanel buttonBox = new JPanel();
		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		createButton = new JButton("Create");
		createButton.setActionCommand("create");
		createButton.setEnabled(true);
		createButton.addActionListener(this);

		deleteButton = new JButton("Delete");
		deleteButton.setActionCommand("delete");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(this);

		createByAttributesButton = new JButton("Create By Attributes");
		createByAttributesButton.setActionCommand("createByAttributes");
		createByAttributesButton.setEnabled(true);
		createByAttributesButton.addActionListener(this);

		selectButton = new JButton("Select");
		selectButton.setActionCommand("select");
		selectButton.setEnabled(false);
		selectButton.addActionListener(this);

		clearButton = new JButton("Clear");
		clearButton.setActionCommand("clear");
		clearButton.setEnabled(false);
		clearButton.addActionListener(this);

		clearAllButton = new JButton("Clear All");
		clearAllButton.setActionCommand("clearall");
		clearAllButton.setEnabled(true);
		clearAllButton.addActionListener(this);

		buttonBox.add(createButton);
		buttonBox.add(deleteButton);
		buttonBox.add(createByAttributesButton);
		buttonBox.add(selectButton);
		buttonBox.add(clearButton);
		buttonBox.add(clearAllButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.setMaximumSize(new Dimension(1000,45));
		
		dataPanel.add(buttonBox);
		setContentPane(dataPanel);

		// Set up ourselves as listeners
		CyGroupManager.addGroupChangeListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			CyGroupManager.removeGroupChangeListener(this);
			this.dispose();
		} else if ("create".equals(e.getActionCommand())) {
			CyNetwork network = Cytoscape.getCurrentNetwork();
			List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());
			List<CyGroup> groupList = CyGroupManager.getGroupList();
			String groupName = JOptionPane.showInputDialog("Please enter a name for this group");
			if (groupName == null) return;
			CyGroup group = CyGroupManager.createGroup(groupName, currentNodes, null);
		} else if ("delete".equals(e.getActionCommand())) {
			// Don't listen until we're done
			CyGroupManager.removeGroupChangeListener(this);
			int rows[] = groupTable.getSelectedRows();
			for (int row = 0; row < rows.length; row++) {
				String groupName = (String) groupTable.getValueAt(rows[row], 0);
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group == null) continue;
				CyGroupManager.removeGroup(group);
			}
			tableModel.updateTable();
			CyGroupManager.addGroupChangeListener(this);
		} else if ("createByAttributes".equals(e.getActionCommand())) {
			// Get the list of group viewers
			CreateByAttributeDialog d = new CreateByAttributeDialog(Cytoscape.getDesktop());
			d.pack();
			d.setLocationRelativeTo(Cytoscape.getDesktop());
			d.setVisible(true);
		} else if ("select".equals(e.getActionCommand()) 
		           || "clear".equals(e.getActionCommand())) {
			int rows[] = groupTable.getSelectedRows();
			if (rows == null) return;
			int cols[] = groupTable.getSelectedColumns();
			for (int row = 0; row < rows.length; row++) {
				String groupName = (String) groupTable.getValueAt(rows[row], 0);
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group == null) continue;

				boolean state = true;
				for (int col = 0; col < cols.length; col++) {

					if ("clear".equals(e.getActionCommand())) 
						state = false;

					if (cols[col] == 1 || cols[col] == 0) {
						Cytoscape.getCurrentNetwork().setSelectedNodeState(group.getGroupNode(), state);
						Cytoscape.getCurrentNetwork().setSelectedNodeState(group.getNodes(), state);
					}
					if (cols[col] == 2 || cols[col] == 0)
						Cytoscape.getCurrentNetwork().setSelectedEdgeState(group.getInnerEdges(), state);
					if (cols[col] == 3 || cols[col] == 0)
						Cytoscape.getCurrentNetwork().setSelectedEdgeState(group.getOuterEdges(), state);
				}
			}
			Cytoscape.getCurrentNetworkView().updateView();
		} else if ("clearall".equals(e.getActionCommand())) {
			Cytoscape.getCurrentNetwork().unselectAllEdges();
			Cytoscape.getCurrentNetwork().unselectAllNodes();
			Cytoscape.getCurrentNetworkView().updateView();
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

	public void editingCanceled(ChangeEvent e) { }

	public void editingStopped(ChangeEvent e) {
		int row = groupTable.getSelectedRow();
		int col = groupTable.getSelectedColumn();
		if (row == -1 || col != 4) return;

		DefaultCellEditor editor = (DefaultCellEditor)e.getSource();
		String viewerName = (String) editor.getCellEditorValue();
		String oldViewerName = (String)groupTable.getValueAt(row,col);
		if (viewerName.equals(oldViewerName)) return;
		CyGroup group = tableModel.getGroupAtRow(row);

		// Set the new viewer
		CyGroupManager.setGroupViewer(group, viewerName, Cytoscape.getCurrentNetworkView(), true);
	}

	public void valueChanged (ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) return;
		if (groupTable.getSelectedRowCount() > 0) {
			clearButton.setEnabled(true);
			selectButton.setEnabled(true);
			deleteButton.setEnabled(true);
		} else {
			clearButton.setEnabled(false);
			selectButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
	}

	public void groupChanged (CyGroup group, CyGroupChangeListener.ChangeType change) {
		// Whatever changed, we just update our table
		tableModel.updateTable();
	}
}
