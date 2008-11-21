
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
import cytoscape.filter.model.FilterManager;
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
public class NodeTopologyFilterEditor extends FilterEditor implements ActionListener, FocusListener,
                                                                      ItemListener {
	/**
	 * This is the Name that will go in the Tab
	 * and is returned by the "toString" method
	 */
	protected String identifier;
	protected NodeTopologyFilter filter;
	protected JTextField nameField;
	protected JComboBox filterBox;
	protected JTextField distanceField;
	protected JTextField countField;
	protected String DEFAULT_FILTER_NAME = "NodeTopology: ";
	protected Integer DEFAULT_DISTANCE = Integer.valueOf(1);
	protected Integer DEFAULT_COUNT = Integer.valueOf(1);
	protected int DEFAULT_FILTER = 0; // this is the SelectAllFilter
	protected Class NODE_CLASS;
	protected Class EDGE_CLASS;
	protected Class NUMBER_CLASS;
	protected Class DEFAULT_CLASS;
	protected Class filterClass;

	/**
	 * Creates a new NodeTopologyFilterEditor object.
	 */
	public NodeTopologyFilterEditor() {
		super();

		try {
			filterClass = Class.forName("cytoscape.filter.cytoscape.NodeTopologyFilter");
		} catch (Exception e) {
			e.printStackTrace();
		}

		identifier = "Topology Filter";
		setBorder(new TitledBorder("Node Topology Filter"));

		setLayout(new GridBagLayout());

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
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(nameField, gridBagConstraints);

		nameField.setText(identifier);
		nameField.addActionListener(this);
		nameField.addFocusListener(this);

		JLabel lbSelectWith = new JLabel("Select nodes with at least");
		lbSelectWith.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbSelectWith, gridBagConstraints);

		countField = new JTextField(10);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(countField, gridBagConstraints);

		countField.setEditable(true);
		countField.addActionListener(this);
		countField.addFocusListener(this);

		JLabel lbNeighbors = new JLabel("neighbors ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
		add(lbNeighbors, gridBagConstraints);

		JLabel lbWithinDistance = new JLabel("within distance ");
		lbWithinDistance.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbWithinDistance, gridBagConstraints);

		distanceField = new JTextField(10);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(distanceField, gridBagConstraints);

		distanceField.setEditable(true);
		distanceField.addActionListener(this);
		distanceField.addFocusListener(this);

		JLabel lbPassFilter = new JLabel("that pass the filter ");
		lbPassFilter.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbPassFilter, gridBagConstraints);

		filterBox = new JComboBox();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(filterBox, gridBagConstraints);

		filterBox.addItemListener(this);
		filterBox.setModel(FilterManager.defaultManager().getComboBoxModel());
		filterBox.setEditable(false);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void resetFilterBoxModel() {
		filterBox.setModel(FilterManager.defaultManager().getComboBoxModel());
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
		return NodeTopologyFilter.FILTER_ID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return NodeTopologyFilter.FILTER_DESCRIPTION;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Filter createDefaultFilter() {
		return new NodeTopologyFilter(DEFAULT_COUNT, DEFAULT_DISTANCE, DEFAULT_FILTER,
		                              DEFAULT_FILTER_NAME);
	}

	/**
	 * Accepts a Filter for editing
	 * Note that this Filter must be a Filter that can be edited
	 * by this Filter editor.
	 */
	public void editFilter(Filter filter) {
		if (filter instanceof NodeTopologyFilter) {
			// good, this Filter is of the right type
			resetFilterBoxModel();
			this.filter = (NodeTopologyFilter) filter;
			setFilterName(this.filter.toString());
			setSelectedFilter(this.filter.getFilter());
			setDistance(this.filter.getDistance());
			setCount(this.filter.getCount());
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
		filter.setIdentifier(name);
		nameField.setText(name);
	}

	// Search String /////////////////////////////////////
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getSelectedFilter() {
		return filter.getFilter();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param newFilter DOCUMENT ME!
	 */
	public void setSelectedFilter(int newFilter) {
		if (filter != null) {
			filter.setFilter(newFilter);
			filterBox.removeItemListener(this);
			filterBox.setSelectedItem(FilterManager.defaultManager().getFilter(newFilter));
			filterBox.addItemListener(this);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Integer getCount() {
		return filter.getCount();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param count DOCUMENT ME!
	 */
	public void setCount(Integer count) {
		filter.setCount(count);
		countField.setText(count.toString());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Integer getDistance() {
		return filter.getDistance();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param distance DOCUMENT ME!
	 */
	public void setDistance(Integer distance) {
		filter.setDistance(distance);
		distanceField.setText(distance.toString());
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
	public void handleEvent(AWTEvent e) {
		if (e.getSource() == nameField) {
			setFilterName(nameField.getText());
		} else if (e.getSource() == filterBox) {
			setSelectedFilter(FilterManager.defaultManager()
			                               .getFilterID((Filter) filterBox.getSelectedItem()));
		} else if (e.getSource() == countField) {
			Integer count = null;

			try {
				count = Integer.valueOf(countField.getText());
			} catch (NumberFormatException nfe) {
				count = DEFAULT_COUNT;
			}

			setCount(count);
		} else if (e.getSource() == distanceField) {
			Integer distance = null;

			try {
				distance = Integer.valueOf(distanceField.getText());
			} catch (NumberFormatException nfe) {
				distance = DEFAULT_DISTANCE;
			}

			setDistance(distance);
		}

		updateName();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void updateName() {
		String newName = "NodeTopology:>=" + countField.getText().trim() + "~"
		                 + distanceField.getText().trim();
		setFilterName(newName);
	}
}
