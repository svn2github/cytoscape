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

package csplugins.enhanced.search;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import csplugins.enhanced.search.IndexAndSearchTask;

public class EnhancedSearchPanel extends JPanel {

	private JTextField searchField;

	private JLabel label;

	private JButton clearButton;

	private static final String ENHANCED_SEARCH_STRING = "EnhancedSearch:  ";

	private static final String CLEAR_STRING = "Clear";

	/**
	 * Constructor.
	 */
	public EnhancedSearchPanel() {

		// Must use BoxLayout, as we want to control width
		// of all components.
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		clearButton = createClearButton();
		searchField = createSearchField();
		label = createEnhancedSearchLabel();

		add(label);
		add(searchField);
		add(clearButton);

		// Add Right Buffer, to prevent config button from occassionally
		// being partially obscured.
		add(Box.createHorizontalStrut(5));

		enableAllEnhancedSearchButtons();
	}

	/**
	 * No Network Current Available.
	 */
	public void noNetworkLoaded() {
		disableAllEnhancedSearchButtons();
		searchField.setToolTipText("Please select or load a network");
	}

	/**
	 * Indexing Operating in Progress.
	 */
	public void queryInProgress() {
		disableAllEnhancedSearchButtons();
		searchField.setToolTipText("Query in progress. Please wait...");
	}

	/**
	 * Disables all Enhanced Search Buttons.
	 */
	private void disableAllEnhancedSearchButtons() {
		searchField.setText("");
		searchField.setEnabled(false);
		searchField.setVisible(true);
		clearButton.setEnabled(false);
	}

	/**
	 * Enables all Enhanced Search Buttons.
	 */
	public void enableAllEnhancedSearchButtons() {
		searchField.setToolTipText("Enter search string");
		searchField.setEnabled(true);
		clearButton.setEnabled(true);
	}

	/**
	 * Creates Clear Button.
	 * 
	 * @return JButton Object.
	 */
	private JButton createClearButton() {
		JButton button = new JButton(CLEAR_STRING);
		button.setToolTipText("Clear the query line");
		button.setEnabled(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				searchField.setText("");
				searchField.requestFocus();
			}
		});
		button.setBorderPainted(false);

		return button;
	}

	/**
	 * Creates Search Field
	 * 
	 * @return JTextField Object.
	 */
	private JTextField createSearchField() {
		
		searchField = new JTextField(30);
		Dimension size = new Dimension(1, 30);
		searchField.setPreferredSize(size);
//		searchField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		searchField.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

		searchField.setEnabled(false);
		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String query = getQuery();
				System.out.println("query: " + query);
				if (query.length() > 0) {
					final CyNetwork currNetwork = Cytoscape.getCurrentNetwork();

					IndexAndSearchTask task = new IndexAndSearchTask(
							currNetwork, query);
					JTaskConfig config = new JTaskConfig();
					config.setAutoDispose(true);
					config.displayStatus(true);
					config.displayTimeElapsed(true);
					config.displayCancelButton(true);
					config.setOwner(Cytoscape.getDesktop());
					config.setModal(true);

					// Execute Task via TaskManager
					// This automatically pops-open a JTask Dialog Box.
					// This method will block until the JTask Dialog Box is
					// disposed.
					TaskManager.executeTask(task, config);

				} else {
					return;
				}
			}
		});

		searchField.setToolTipText("Please select or load a network to "
				+ "activate search functionality.");
		// Set Max Size of TextField to match preferred size
		searchField.setMaximumSize(searchField.getPreferredSize());

		return searchField;
	}

	/**
	 * Creates Search Label.
	 */
	private JLabel createEnhancedSearchLabel() {

		JLabel label = new JLabel(ENHANCED_SEARCH_STRING);
		label.setBorder(new EmptyBorder(0, 5, 0, 0));
		label.setForeground(Color.GRAY);

		// Fix width of label
		label.setMaximumSize(label.getPreferredSize());

		return label;
	}

	public String getQuery() {
		String query = searchField.getText();
		query = query.trim();

		return query;
	}
}
