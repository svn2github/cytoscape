/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package browser.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import browser.AttributeModel;
import browser.DataObjectType;
import browser.DataTableModel;
import browser.MultiDataEditAction;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;


/**
 *
 */
public class ModPanel extends JPanel implements ActionListener {
	// Backend data objects
	private CyAttributes data;
	private DataTableModel tableModel;
	private DataObjectType objectType;

	// Set/Modify
	JComboBox attributeModifyBox;
	JTextField field1;
	JTextField field2;
	JComboBox actionBox;
	JButton applyModify;
	JLabel between;
	JLabel field2Label;

	// Copy
	JComboBox attributeCopyFromBox;
	JComboBox attributeCopytoBox;
	JButton copyGo;

	// Delete
	JComboBox attributeClearBox;
	JButton clearGo;

	/**
	 * Creates a new ModPanel object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param tableModel  DOCUMENT ME!
	 * @param graphObjectType  DOCUMENT ME!
	 */
	public ModPanel(final DataTableModel tableModel, final DataObjectType graphObjectType) {
		this.data = graphObjectType.getAssociatedAttribute();
		this.tableModel = tableModel;
		this.objectType = graphObjectType;

		setLayout(new GridBagLayout());

		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;

		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		JTabbedPane tabs = new JTabbedPane();
		add(tabs, gridBagConstraints);

		// create Operation panel
		JPanel operation = new JPanel();
		operation.setLayout(new java.awt.GridBagLayout());

		tabs.add("Operation", operation);
		attributeModifyBox = createAttributeBox();
		attributeModifyBox.setEditable(false);

		if (attributeModifyBox.getItemCount() != 0) {
			attributeModifyBox.setSelectedIndex(0);
		}

		attributeModifyBox.setToolTipText("Select attribute name.");

		field1 = new JTextField(18);
		field2 = new JTextField(18);
		field2.setVisible(false);

		actionBox = new JComboBox();
		actionBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					actionChanged();
				}
			});

		for (ActionName action : ActionName.values()) {
			if ((action.equals(ActionName.COPY) == false)
			    && (action.equals(ActionName.CLEAR) == false)) {
				actionBox.addItem(action.getDisplayName());
			}
		}

		actionBox.setEditable(false);

		if (actionBox.getItemCount() != 0) {
			actionBox.setSelectedIndex(0);
		}

		applyModify = new JButton("GO");

		applyModify.addActionListener(this);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		operation.add(actionBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		operation.add(attributeModifyBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
		between = new JLabel("by/to");
		operation.add(between, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
		field2Label = new JLabel("to");
		operation.add(field2Label, gridBagConstraints);
		field2Label.setVisible(false);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		operation.add(field1, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 5;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		operation.add(field2, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 6;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		operation.add(applyModify, gridBagConstraints);

		// create Copy Panel
		JPanel copy = new JPanel(new java.awt.GridBagLayout());
		tabs.add("Copy", copy);
		attributeCopyFromBox = createAttributeBox();
		attributeCopytoBox = createAttributeBox();
		attributeCopytoBox.setEditable(true);
		copyGo = new JButton("GO");

		copyGo.addActionListener(this);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		copy.add(new JLabel("Copy From:"), gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.5;

		copy.add(attributeCopyFromBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		copy.add(new JLabel("To:"), gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		copy.add(attributeCopytoBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		copy.add(copyGo, gridBagConstraints);

		// create Delete Panel
		JPanel clear = new JPanel(new java.awt.GridBagLayout());
		tabs.add("Clear", clear);
		attributeClearBox = createAttributeBox();
		clearGo = new JButton("GO");

		clearGo.addActionListener(this);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		clear.add(new JLabel("Clear"), gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		clear.add(attributeClearBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);

		clear.add(clearGo, gridBagConstraints);
	}

	protected void actionChanged() {
		final ActionName action = ActionName.getValueOf(actionBox.getSelectedItem().toString());

		if (between == null)
			return;

		if (action.equals(ActionName.REPLACE)) {
			between.setText("From: ");
			field2Label.setVisible(true);
			field2.setVisible(true);
			field1.setVisible(true);
		} else if (action.equals(ActionName.TO_LOWER) || action.equals(ActionName.TO_UPPER)) {
			between.setText("Convert to upper/lower case ");
			field2Label.setVisible(false);
			field1.setVisible(false);
			field2.setVisible(false);
		} else if (action.equals(ActionName.REMOVE)) {
			between.setText("Pattern: ");
			field2Label.setVisible(false);
			field1.setVisible(true);
			field2.setVisible(false);
		} else if (action.equals(ActionName.MUL) || action.equals(ActionName.DIV)) {
			between.setText("by ");
			field1.setVisible(true);
			field2Label.setVisible(false);
			field2.setVisible(false);
		} else {
			between.setText("to ");
			field1.setVisible(true);
			field2Label.setVisible(false);
			field2.setVisible(false);
		}
	}

	private JComboBox createAttributeBox() {
		JComboBox box = new JComboBox(new AttributeModel(data));
		Dimension newSize = new Dimension(100, (int) box.getPreferredSize().getHeight());
		box.setMaximumSize(newSize);
		box.setPreferredSize(newSize);

		return box;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		final MultiDataEditAction edit;

		//
		// Operations
		//

		// 1. Set, Add, Mul and Div operations
		if (e.getSource() == applyModify) {
			String input = field1.getText();

			if (ActionName.getValueOf(actionBox.getSelectedItem().toString())
			              .equals(ActionName.REPLACE)) {
				input = field1.getText() + "\t" + field2.getText();
			}

			edit = new MultiDataEditAction(input,
			                               ActionName.getValueOf(actionBox.getSelectedItem()
			                                                              .toString()),
			                               tableModel.getObjects(),
			                               (String) attributeModifyBox.getSelectedItem(), null,
			                               null, objectType, tableModel);
		}
		// 2. Copy operation
		else if (e.getSource() == copyGo) {
			edit = new MultiDataEditAction(null, ActionName.COPY, tableModel.getObjects(),
			                               (String) attributeCopytoBox.getSelectedItem(),
			                               (String) attributeCopyFromBox.getSelectedItem(), null,
			                               objectType, tableModel);
		}
		// 3. Delete (Clear?) operation
		else {
			edit = new MultiDataEditAction(null, ActionName.CLEAR, tableModel.getObjects(),
			                               (String) attributeClearBox.getSelectedItem(), null,
			                               null, objectType, tableModel);
		}

		cytoscape.util.undo.CyUndo.getUndoableEditSupport().postEdit(edit);

		Cytoscape.getSwingPropertyChangeSupport().firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);

	}
}
