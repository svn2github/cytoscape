
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

package filter.cytoscape;

import ViolinStrings.Strings;

import cytoscape.*;
import cytoscape.CyNetwork;

import cytoscape.data.*;

import filter.model.*;

import filter.view.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.event.SwingPropertyChangeSupport;


/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class BooleanMetaFilterEditor extends FilterEditor implements ItemListener, FocusListener,
                                                                     ActionListener,
                                                                     ListSelectionListener {
	/**
	 * This is the Name that will go in the Tab
	 * and is returned by the "toString" method
	 */
	protected String identifier;
	protected JTextField nameField;
	protected JList filterList;
	protected JComboBox comparisonBox;
	protected JCheckBox negationBox;
	protected Set filters;
	protected Vector listModel;
	protected BooleanMetaFilter filter;
	protected String DEFAULT_FILTER_NAME = "BooleanMeta: ";
	protected String DEFAULT_COMPARISON = BooleanMetaFilter.AND;
	protected int[] DEFAULT_FILTERS = new int[0];
	protected boolean DEFAULT_NEGATION = false;
	protected Class filterClass;

	/**
	 * Creates a new BooleanMetaFilterEditor object.
	 */
	public BooleanMetaFilterEditor() {
		super();

		try {
			filterClass = Class.forName("filter.cytoscape.BooleanMetaFilter");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.filters = filters;
		identifier = "Boolean Meta-Filter";
		setBorder(new TitledBorder("Boolean Meta-Filter"));

		setLayout(new java.awt.GridBagLayout());

		JLabel lbFilterName = new JLabel("Filter Name");
		nameField = new JTextField(15);
		nameField.setMaximumSize(new Dimension(131, 19));
		nameField.setText(identifier);
		nameField.addActionListener(this);
		nameField.addFocusListener(this);

		JLabel lb_Select_objects_that_pass = new JLabel("Select objects that pass ");

		comparisonBox = new JComboBox();
		comparisonBox.setMinimumSize(new Dimension(105, 19));
		comparisonBox.setPreferredSize(new Dimension(105, 19));

		comparisonBox.addItem(BooleanMetaFilter.AND);
		comparisonBox.addItem(BooleanMetaFilter.OR);
		comparisonBox.addItem(BooleanMetaFilter.XOR);
		comparisonBox.setSelectedIndex(0);
		comparisonBox.setEditable(false);
		comparisonBox.addItemListener(this);

		JLabel lb_of_the_selected_filters = new JLabel(" of the selected filters");

		filterList = new JList(FilterManager.defaultManager());
		filterList.addListSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane(filterList);

		negationBox = new JCheckBox("Negate?");

		java.awt.GridBagConstraints gridBagConstraints;

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
		add(lbFilterName, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(nameField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
		add(lb_Select_objects_that_pass, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
		add(comparisonBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		add(lb_of_the_selected_filters, gridBagConstraints);

		scrollPane.setViewportView(filterList);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		add(scrollPane, gridBagConstraints);

		negationBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		negationBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
		add(negationBox, gridBagConstraints);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return identifier;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return BooleanMetaFilter.FILTER_DESCRIPTION;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class getFilterClass() {
		return filterClass;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFilterID() {
		return BooleanMetaFilter.FILTER_ID;
	}

	/**
	 * Accepts a Filter for editing
	 * Note that this Filter must be a Filter that can be edited
	 * by this Filter editor.
	 */
	public void editFilter(Filter filter) {
		if (filter instanceof BooleanMetaFilter) {
			// good, this Filter is of the right type
			this.filter = (BooleanMetaFilter) filter;
			setFilters(this.filter.getFilters());
			setComparison(this.filter.getComparison());
			setFilterName(this.filter.toString());
			setNegation(this.filter.getNegation());
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Filter createDefaultFilter() {
		return new BooleanMetaFilter(DEFAULT_FILTERS, DEFAULT_COMPARISON, DEFAULT_FILTER_NAME,
		                             DEFAULT_NEGATION);
	}

	//----------------------------------------//
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFilterName() {
		return filter.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 */
	public void setFilterName(String name) {
		nameField.setText(name);
		filter.setIdentifier(name);
	}

	// Search String /////////////////////////////////////
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getFilters() {
		return filter.getFilters();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param array DOCUMENT ME!
	 */
	public void setFilters(int[] array) {
		filterList.removeListSelectionListener(this);
		filterList.clearSelection();

		for (int idx = 0; idx < array.length; idx++) {
			int index = FilterManager.defaultManager()
			                         .indexOf(FilterManager.defaultManager().getFilter(array[idx]));

			if (index > -1) {
				filterList.addSelectionInterval(index, index);
			}
		}

		filterList.addListSelectionListener(this);

		Object[] selectedObjects = filterList.getSelectedValues();
		int[] selectedFilters = new int[selectedObjects.length];

		for (int idx = 0; idx < selectedFilters.length; idx++) {
			selectedFilters[idx] = FilterManager.defaultManager()
			                                    .getFilterID((Filter) selectedObjects[idx]);
		} // end of for ()

		filter.setFilters(selectedFilters);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean getNegation() {
		return negationBox.isSelected();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param negation DOCUMENT ME!
	 */
	public void setNegation(boolean negation) {
		filter.setNegation(negation);
		negationBox.removeItemListener(this);
		negationBox.setSelected(negation);
		negationBox.addItemListener(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getComparison() {
		return filter.getComparison();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param comparison DOCUMENT ME!
	 */
	public void setComparison(String comparison) {
		filter.setComparison(comparison);
		comparisonBox.removeItemListener(this);
		comparisonBox.setSelectedItem(comparison);
		comparisonBox.addItemListener(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		handleEvent(e);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void focusGained(FocusEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void focusLost(FocusEvent e) {
		handleEvent(e);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void itemStateChanged(ItemEvent e) {
		handleEvent(e);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void handleEvent(EventObject e) {
		if (e.getSource() == nameField) {
			setFilterName(nameField.getText());
		} else if (e.getSource() == filterList) {
			Object[] selectedObjects = filterList.getSelectedValues();
			int[] selectedFilters = new int[selectedObjects.length];

			for (int idx = 0; idx < selectedFilters.length; idx++) {
				selectedFilters[idx] = FilterManager.defaultManager()
				                                    .getFilterID((Filter) selectedObjects[idx]);
			} // end of for ()

			setFilters(selectedFilters);
		} else if (e.getSource() == comparisonBox) {
			setComparison((String) comparisonBox.getSelectedItem());
		} else if (e.getSource() == negationBox) {
			setNegation(negationBox.isSelected());
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void valueChanged(ListSelectionEvent e) {
		handleEvent(e);
	}
}
