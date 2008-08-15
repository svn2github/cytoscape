
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
public class StringPatternFilterEditor extends FilterEditor implements ActionListener,
                                                                       FocusListener, ItemListener {
	/**
	 * This is the Name that will go in the Tab
	 * and is returned by the "toString" method
	 */
	protected String identifier;
	protected JTextField nameField;
	protected JComboBox classBox;
	protected JTextField searchField;
	protected JComboBox attributeBox;
	protected StringPatternFilter filter;
	protected String DEFAULT_SEARCH_STRING = "";
	protected String DEFAULT_FILTER_NAME = "Pattern: ";
	protected String DEFAULT_SELECTED_ATTRIBUTE = "";
	protected Class NODE_CLASS;
	protected Class EDGE_CLASS;
	protected Class STRING_CLASS;
	protected String DEFAULT_CLASS = StringPatternFilter.NODE;
	protected Class filterClass;
	protected ComboBoxModel nodeAttributeModel;
	protected ComboBoxModel edgeAttributeModel;

	/**
	 * Creates a new StringPatternFilterEditor object.
	 */
	public StringPatternFilterEditor() {
		super();

		try {
			STRING_CLASS = Class.forName("java.lang.String");
			NODE_CLASS = Class.forName("org.cytoscape.Node");
			EDGE_CLASS = Class.forName("org.cytoscape.Edge");
			filterClass = Class.forName("cytoscape.filter.cytoscape.StringPatternFilter");
			nodeAttributeModel = new NodeAttributeComboBoxModel(STRING_CLASS);
			edgeAttributeModel = new EdgeAttributeComboBoxModel(STRING_CLASS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(new GridBagLayout());
		identifier = "String Filter";
		setBorder(new TitledBorder(getFilterID()));

		java.awt.GridBagConstraints gridBagConstraints;

		JLabel lbFilterName = new JLabel("Filter Name");
		lbFilterName.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbFilterName, gridBagConstraints);

		nameField = new JTextField(15);
		nameField.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(nameField, gridBagConstraints);

		nameField.addActionListener(this);
		nameField.addFocusListener(this);

		JLabel lbSelectType = new JLabel("Select graph objects of type ");
		lbSelectType.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbSelectType, gridBagConstraints);

		classBox = new JComboBox();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		//gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(classBox, gridBagConstraints);

		classBox.addItem(StringPatternFilter.NODE);
		classBox.addItem(StringPatternFilter.EDGE);
		classBox.setEditable(false);
		classBox.addItemListener(this);

		JLabel lbTextAttr = new JLabel(" with a value for text attribute ");
		lbTextAttr.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbTextAttr, gridBagConstraints);

		attributeBox = new JComboBox();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(attributeBox, gridBagConstraints);

		attributeBox.setEditable(false);
		attributeBox.addItemListener(this);

		JLabel lbMatchesPattern = new JLabel(" that matches the pattern ");
		lbMatchesPattern.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbMatchesPattern, gridBagConstraints);

		searchField = new JTextField(10);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
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
	public String getDescription() {
		return StringPatternFilter.FILTER_DESCRIPTION;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFilterID() {
		return StringPatternFilter.FILTER_ID;
	}

	/**
	 * Creates a new filter initialized to the default values with the given name
	 */
	public Filter createDefaultFilter() {
		return new StringPatternFilter(DEFAULT_CLASS, DEFAULT_SELECTED_ATTRIBUTE,
		                               DEFAULT_SEARCH_STRING, DEFAULT_FILTER_NAME);
	}

	/**
	 * Accepts a Filter for editing
	 * Note that this Filter must be a Filter that can be edited
	 * by this Filter editor.
	 */
	public void editFilter(Filter filter) {
		if (filter instanceof StringPatternFilter) {
			// good, this Filter is of the right type
			this.filter = (StringPatternFilter) filter;
			setSearchString(this.filter.getSearchString());
			setFilterName(this.filter.toString());
			setSelectedAttribute(this.filter.getSelectedAttribute());
			setSelectedClass(this.filter.getClassType());
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
		return identifier;
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
	public String getSearchString() {
		return filter.getSearchString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param search_string DOCUMENT ME!
	 */
	public void setSearchString(String search_string) {
		filter.setSearchString(search_string);
		searchField.setText(search_string);
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
			attributeBox.setSelectedItem(filter.getSelectedAttribute());
		} // end of if ()
		else {
			attributeBox.setModel(edgeAttributeModel);
			attributeBox.setSelectedItem(filter.getSelectedAttribute());
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
	 * @param e DOCUMENT ME!
	 */
	public void handleEvent(AWTEvent e) {
		if (e.getSource() == nameField) {
			setFilterName(nameField.getText());
		} else {
			if (e.getSource() == searchField) {
				setSearchString(searchField.getText());
			} else if (e.getSource() == nameField) {
				setFilterName(nameField.getText());
			} else if (e.getSource() == attributeBox) {
				setSelectedAttribute((String) attributeBox.getSelectedItem());
			} else if (e.getSource() == classBox) {
				setSelectedClass((String) classBox.getSelectedItem());
			}

			updateName();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ae DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent ae) {
		handleEvent(ae);
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
		buffer.append(getSelectedAttribute() + " ~ ");
		buffer.append(getSearchString());
		setFilterName(buffer.toString());
	}
}
