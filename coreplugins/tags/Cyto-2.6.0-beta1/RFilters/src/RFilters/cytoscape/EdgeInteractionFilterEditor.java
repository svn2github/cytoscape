
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

import giny.model.GraphPerspective;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.event.SwingPropertyChangeSupport;


/**
 * This filter will pass nodes based on the edges that
 * they have.
 */
public class EdgeInteractionFilterEditor extends FilterEditor implements ActionListener,
                                                                         FocusListener,
                                                                         ItemListener {
	/**
	 * This is the Name that will go in the Tab
	 * and is returned by the "toString" method
	 */
	protected String identifier;
	protected Class filterClass;
	protected JTextField nameField;
	protected JComboBox filterBox;
	protected JComboBox targetBox;
	protected EdgeInteractionFilter filter;
	protected String DEFAULT_FILTER_NAME = "Edge Interaction: ";
	protected int DEFAULT_FILTER = -1;
	protected String DEFAULT_TARGET = EdgeInteractionFilter.SOURCE;

	/**
	 * Creates a new EdgeInteractionFilterEditor object.
	 */
	public EdgeInteractionFilterEditor() {
		super();

		try {
			filterClass = Class.forName("filter.cytoscape.EdgeInteractionFilter");
		} catch (Exception e) {
			e.printStackTrace();
		}

		identifier = "Edge Interactions";
		setBorder(new TitledBorder("Edge Interaction Filter"));
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(450, 125));

		GridBagConstraints gridBagConstraints;

		JLabel lbFilterName = new JLabel("Filter Name");
		lbFilterName.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbFilterName, gridBagConstraints);

		nameField = new JTextField(15);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(nameField, gridBagConstraints);

		nameField.addActionListener(this);
		nameField.addFocusListener(this);

		JLabel SelectEdge = new JLabel("Select edges with a node ");
		SelectEdge.setFocusable(false);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(SelectEdge, gridBagConstraints);

		targetBox = new JComboBox();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(targetBox, gridBagConstraints);

		targetBox.addItem(EdgeInteractionFilter.SOURCE);
		targetBox.addItem(EdgeInteractionFilter.TARGET);
		targetBox.addItem(EdgeInteractionFilter.EITHER);
		targetBox.addItemListener(this);

		JLabel lbPassesFilter = new JLabel("which passes the filter ");
		lbPassesFilter.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(lbPassesFilter, gridBagConstraints);

		filterBox = new JComboBox(FilterManager.defaultManager().getComboBoxModel());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(filterBox, gridBagConstraints);

		filterBox.addItemListener(this);
	}

	//----------------------------------------//
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
		return EdgeInteractionFilter.FILTER_ID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return EdgeInteractionFilter.FILTER_DESCRIPTION;
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
	public Filter createDefaultFilter() {
		return new EdgeInteractionFilter(DEFAULT_FILTER, DEFAULT_TARGET, DEFAULT_FILTER_NAME);
	}

	/**
	 * Accepts a Filter for editing
	 * Note that this Filter must be a Filter that can be edited
	 * by this Filter editor.
	 */
	public void editFilter(Filter filter) {
		if (this.filter == null) {
		} // end of if ()

		if (filter instanceof EdgeInteractionFilter) {
			// good, this Filter is of the right type
			this.filter = (EdgeInteractionFilter) filter;
			setFilterName(this.filter.toString());
			setSelectedFilter(this.filter.getFilter());
			setTarget(this.filter.getTarget());
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
	public String getTarget() {
		return filter.getTarget();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param target DOCUMENT ME!
	 */
	public void setTarget(String target) {
		filter.setTarget(target);
		targetBox.removeItemListener(this);
		targetBox.setSelectedItem(target);
		targetBox.addItemListener(this);
	}

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
			filterBox.removeItemListener(this);
			filterBox.setSelectedItem(FilterManager.defaultManager().getFilter(newFilter));
			filterBox.addItemListener(this);
			filter.setFilter(FilterManager.defaultManager()
			                              .getFilterID((Filter) filterBox.getSelectedItem()));
		}
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
	public void handleEvent(EventObject e) {
		if (e.getSource() == nameField) {
			setFilterName(nameField.getText());
		} else if (e.getSource() == filterBox) {
			setSelectedFilter(FilterManager.defaultManager()
			                               .getFilterID((Filter) filterBox.getSelectedItem()));
		} else if (e.getSource() == targetBox) {
			setTarget((String) targetBox.getSelectedItem());
		}
	}
}
