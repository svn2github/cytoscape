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
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import csplugins.enhanced.search.IndexAndSearchTask;

// import csplugins.enhanced.search.ReindexTask;

// public class EnhancedSearchPanel extends JPanel implements ActionListener {
public class EnhancedSearchPanel extends JPanel {

	private JTextField searchField;

	private JLabel label;

	private static final String ESP_LABEL = "ESP 2.9.0:  ";

	private static final String SEARCH_MENU_ITEM = "Search";

	private static final String REINDEX_MENU_ITEM = "Re-index and search";

	private static final String SEARCH_TOOLTIP = "Perform search";

	private static final String REINDEX_TOOLTIP = "<html>"
			+ "Refresh the network index and perform search." + "<br>"
			+ "This option is useful after changes to attributes." + "</html>";

	private static final String ESP_ENABLED_TOOLTIP = "<html>"
			+ "Enter search query and press return. " + "<br>"
			+ "Right click for more options." + "</html>";

	private static final String ESP_DISABLED_TOOLTIP = "Please select or load a network to activate search functionality";

	/**
	 * Constructor.
	 */
	public EnhancedSearchPanel() {

		// Must use BoxLayout, as we want to control width of all components.
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		label = createEnhancedSearchLabel();
		searchField = createSearchField();
		createPopupMenu();

		add(label);
		add(searchField);

		// Add Right Buffer, to prevent config button from occasionally
		// being partially obscured.
		add(Box.createHorizontalStrut(5));

		// Enable EnhancedSearch if a network is loaded
		disableESP();
		final CyNetwork currNetwork = Cytoscape.getCurrentNetwork();
		if (currNetwork != Cytoscape.getNullNetwork()) {
			enableESP();
		}
	}

	/**
	 * Disables ESP
	 */
	public void disableESP() {
		searchField.setText("");
		searchField.setToolTipText(ESP_DISABLED_TOOLTIP);
		searchField.setEnabled(false);
	}

	/**
	 * Enables ESP
	 */
	public void enableESP() {
		searchField.setToolTipText(ESP_ENABLED_TOOLTIP);
		searchField.setEnabled(true);
	}

	/**
	 * Creates Search Field
	 */
	private JTextField createSearchField() {

		searchField = new JTextField(30);
		Dimension size = new Dimension(1, 30);
		searchField.setPreferredSize(size);
		// searchField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		searchField.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				performSearch(false);
			}
		});

		// Set Max Size of TextField to match preferred size
		searchField.setMaximumSize(searchField.getPreferredSize());

		return searchField;
	}

	/**
	 * Creates Popup Menu, which is activated by right-clicking the search field
	 */
	private void createPopupMenu() {
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();

		// Add 'search' menu item
		menuItem = new JMenuItem(SEARCH_MENU_ITEM);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performSearch(false);
			}
		});
		menuItem.setToolTipText(SEARCH_TOOLTIP);
		popup.add(menuItem);

		// Add 'Reindex and search' menu item
		menuItem = new JMenuItem(REINDEX_MENU_ITEM);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performSearch(true);
			}
		});
		menuItem.setToolTipText(REINDEX_TOOLTIP);
		popup.add(menuItem);

		// Add listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);
		searchField.addMouseListener(popupListener);
	}

	/**
	 * Creates Enhanced Search Label
	 */
	private JLabel createEnhancedSearchLabel() {

		JLabel label = new JLabel(ESP_LABEL);
		label.setBorder(new EmptyBorder(0, 5, 0, 0));
		label.setForeground(Color.GRAY);

		// Fix width of label
		label.setMaximumSize(label.getPreferredSize());

		return label;
	}

	/**
	 * Returns the query string, trimmed
	 */
	public String getQuery() {
		String query = searchField.getText();
		query = query.trim();

		return query;
	}

	/**
	 * Execute search query in a new task
	 */
	private void performSearch(Boolean reindex) {
		String query = getQuery();
		if (query.length() > 0) {
			final CyNetwork currNetwork = Cytoscape.getCurrentNetwork();

			// Mark the network for reindexing, if requested
			if (reindex) {
				final EnhancedSearch enhancedSearch = EnhancedSearchFactory
						.getGlobalEnhancedSearchInstance();
				enhancedSearch.setNetworkIndexStatus(currNetwork,
						EnhancedSearch.REINDEX);
			}

			// Define a new IndexAndSearchTask
			IndexAndSearchTask task = new IndexAndSearchTask(currNetwork, query);

			// Set task parameters
			JTaskConfig config = new JTaskConfig();
			config.setAutoDispose(true);
			config.displayStatus(true);
			config.displayTimeElapsed(true);
			config.displayCancelButton(true);
			config.setOwner(Cytoscape.getDesktop());
			config.setModal(true);

			// Execute the task via the task manager
			TaskManager.executeTask(task, config);

		}
	}

	/**
	 * Displays the popup menu on mouse right-click if search field is enabled
	 */
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			if (searchField.isEnabled()) {
				showPopup(e);
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (searchField.isEnabled()) {
				showPopup(e);
			}
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
