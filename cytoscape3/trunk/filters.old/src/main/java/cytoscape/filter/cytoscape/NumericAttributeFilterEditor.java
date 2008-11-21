
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

package cytoscape.filter.cytoscape;

import cytoscape.filter.model.Filter;
import cytoscape.filter.view.FilterEditor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */
public class NumericAttributeFilterEditor extends FilterEditor implements ActionListener,
                                                                          FocusListener,
                                                                          ItemListener {
	/**
	 * This is the Name that will go in the Tab
	 * and is returned by the "toString" method
	 */
	protected JTextField nameField;
	protected JComboBox classBox;
	protected JTextField searchField;
	protected JComboBox attributeBox;
	protected JComboBox comparisonBox;
	protected String identifier;
	protected NumericAttributeFilter filter;
	protected Number DEFAULT_SEARCH_NUMBER = new Double(0);
	protected String DEFAULT_FILTER_NAME = "Numeric: ";
	protected String DEFAULT_SELECTED_ATTRIBUTE = "";
	protected String DEFAULT_COMPARISON = NumericAttributeFilter.EQUAL;
	protected String DEFAULT_CLASS = NumericAttributeFilter.NODE;
	protected Class NODE_CLASS;
	protected Class EDGE_CLASS;
	protected Class NUMBER_CLASS;
	protected Class filterClass;
	protected ComboBoxModel nodeAttributeModel;
	protected ComboBoxModel edgeAttributeModel;

	/**
	 * Creates a new NumericAttributeFilterEditor object.
	 */
	public NumericAttributeFilterEditor() {
		super();

		try {
			NUMBER_CLASS = Class.forName("java.lang.Number");
			NODE_CLASS = Class.forName("org.cytoscape.Node");
			EDGE_CLASS = Class.forName("org.cytoscape.Edge");
			filterClass = Class.forName("cytoscape.filter.cytoscape.NumericAttributeFilter");
			nodeAttributeModel = new NodeAttributeComboBoxModel(NUMBER_CLASS);
			edgeAttributeModel = new EdgeAttributeComboBoxModel(NUMBER_CLASS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//this.objectAttributes = network.getNodeAttributes();
		setLayout(new GridBagLayout());
		identifier = "Numeric Filter";
		setBorder(new TitledBorder(getFilterID()));

		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();

		JLabel lbFilterName = new JLabel("Filter Name");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbFilterName, gridBagConstraints);

		nameField = new JTextField(15);
		nameField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
		add(nameField, gridBagConstraints);

		nameField.addActionListener(this);
		nameField.addFocusListener(this);

		JLabel lbSelectType = new JLabel("Select graph objects of type ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
		add(lbSelectType, gridBagConstraints);

		classBox = new JComboBox();
		//classBox.setPreferredSize(new Dimension(50,22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
		add(classBox, gridBagConstraints);

		classBox.addItem(NumericAttributeFilter.NODE);
		classBox.addItem(NumericAttributeFilter.EDGE);
		classBox.setEditable(false);
		classBox.addItemListener(this);

		JLabel lbNumberAttr = new JLabel(" with a value for numeric attribute ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
		add(lbNumberAttr, gridBagConstraints);

		attributeBox = new JComboBox();
		attributeBox.setMinimumSize(new java.awt.Dimension(100, 18));
		attributeBox.setPreferredSize(new java.awt.Dimension(100, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
		add(attributeBox, gridBagConstraints);

		attributeBox.setEditable(false);
		attributeBox.addItemListener(this);

		JLabel lbThatIs = new JLabel(" that is ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
		add(lbThatIs, gridBagConstraints);

		comparisonBox = new JComboBox();
		comparisonBox.setPreferredSize(new Dimension(50, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		add(comparisonBox, gridBagConstraints);

		comparisonBox.addItem(NumericAttributeFilter.LESS);
		comparisonBox.addItem(NumericAttributeFilter.EQUAL);
		comparisonBox.addItem(NumericAttributeFilter.GREATER);
		comparisonBox.setSelectedIndex(0);
		comparisonBox.setEditable(false);
		comparisonBox.addItemListener(this);

		searchField = new JTextField(10);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
		add(searchField, gridBagConstraints);

		searchField.setEditable(true);
		searchField.addActionListener(this);
		searchField.addFocusListener(this);
	}

	//----------------------------------------//
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
	public String toString() {
		return identifier;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFilterID() {
		return NumericAttributeFilter.FILTER_ID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return NumericAttributeFilter.FILTER_DESCRIPTION;
	}

	/**
	 * Create a new filter with the given name initialized to the default values
	 */
	public Filter createDefaultFilter() {
		return new NumericAttributeFilter(DEFAULT_COMPARISON, DEFAULT_CLASS,
		                                  DEFAULT_SELECTED_ATTRIBUTE, DEFAULT_SEARCH_NUMBER,
		                                  DEFAULT_FILTER_NAME);
	}

	/**
	 * Accepts a Filter for editing
	 * Note that this Filter must be a Filter that can be edited
	 * by this Filter editor.
	 */
	public void editFilter(Filter filter) {
		if (filter instanceof NumericAttributeFilter) {
			// good, this Filter is of the right type
			this.filter = (NumericAttributeFilter) filter;
			setFilterName(this.filter.toString());
			setSearchNumber(this.filter.getSearchNumber());
			setSelectedAttribute(this.filter.getSelectedAttribute());
			setSelectedClass(this.filter.getClassType());
			setSelectedComparison(this.filter.getComparison());
			updateName();
		}
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
	public Number getSearchNumber() {
		return filter.getSearchNumber();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param searchNumber DOCUMENT ME!
	 */
	public void setSearchNumber(Number searchNumber) {
		filter.setSearchNumber(searchNumber);
		searchField.setText(searchNumber.toString());
	}

	// Selected Attribute ////////////////////////////////
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getSelectedAttribute() {
		return filter.getSelectedAttribute();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param new_attr DOCUMENT ME!
	 */
	public void setSelectedAttribute(String new_attr) {
		filter.setSelectedAttribute(new_attr);
		attributeBox.removeItemListener(this);
		attributeBox.setSelectedItem(new_attr);
		attributeBox.addItemListener(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getSelectedClass() {
		return filter.getClassType();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param newClass DOCUMENT ME!
	 */
	public void setSelectedClass(String newClass) {
		filter.setClassType(newClass);
		attributeBox.removeItemListener(this);

		if (newClass == NumericAttributeFilter.NODE) {
			attributeBox.setModel(nodeAttributeModel);
			attributeBox.setSelectedItem(getSelectedAttribute());
		} // end of if ()
		else {
			attributeBox.setModel(edgeAttributeModel);
			attributeBox.setSelectedItem(getSelectedAttribute());
		} // end of else

		attributeBox.addItemListener(this);
		classBox.removeItemListener(this);
		classBox.setSelectedItem(newClass);
		classBox.addItemListener(this);
		setSelectedAttribute((String) attributeBox.getSelectedItem());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getSelectedComparison() {
		return filter.getComparison();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param comparison DOCUMENT ME!
	 */
	public void setSelectedComparison(String comparison) {
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
	public void itemStateChanged(ItemEvent e) {
		handleEvent(e);
	}

	private void handleEvent(AWTEvent e) {
		if (e.getSource() == nameField) {
			setFilterName(nameField.getText());
		} // end of if ()
		else {
			if (e.getSource() == searchField) {
				String numberString = searchField.getText();
				Number searchNumber = null;

				try {
					searchNumber = new Double(numberString);
				} catch (Exception except) {
					searchNumber = DEFAULT_SEARCH_NUMBER;
					searchField.setText(searchNumber.toString());
				}

				setSearchNumber(searchNumber);
			} else if (e.getSource() == attributeBox) {
				setSelectedAttribute((String) attributeBox.getSelectedItem());
			} else if (e.getSource() == classBox) {
				setSelectedClass((String) classBox.getSelectedItem());
			} else if (e.getSource() == comparisonBox) {
				setSelectedComparison((String) comparisonBox.getSelectedItem());
			}

			updateName();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void focusGained(FocusEvent e) {
	}
	;

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
	 */
	public void updateName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getSelectedClass() + " : ");
		buffer.append(getSelectedAttribute());
		buffer.append(getSelectedComparison());
		buffer.append(getSearchNumber());
		setFilterName(buffer.toString());
	}
}
