package org.cytoscape.search.internal;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.EnhancedSearch;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.TaskManager;

public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel searchLabel = null;
	private JTextField searchField = null;
	private JButton searchButton = null;

	private static final String ESP_LABEL = "ESP:  "; // @jve:decl-index=0:

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

	private CyNetwork net = null;

	private CyNetworkManager netmgr;

	private TaskManager tm = null;

	/**
	 * This is the default constructor
	 */
	public SearchPanel() {
		super();
		initialize();
	}

	public void setup(CyNetworkManager nm) { // This is a temporary argument
												// needs to be changed
		this.netmgr = nm;
		// Enable EnhancedSearch if a network is loaded
		disableESP();
		final CyNetwork currNetwork = netmgr.getCurrentNetwork();
		if (currNetwork != null) {
			enableESP();
		}

	}

	public void setup(CyNetwork net) {
		this.net = net;
		enableESP();
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
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		searchLabel = new JLabel(ESP_LABEL);
		searchLabel.setBounds(new Rectangle(68, 27, 157, 32));
		searchLabel.setText("       Search Panel");
		this.setSize(327, 534);
		this.setLayout(null);
		this.add(searchLabel, null);
		this.add(getSearchField(), null);
		this.add(getSearchButton(), null);
		createPopupMenu();
	}

	/**
	 * This method initializes searchField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSearchField() {
		if (searchField == null) {
			searchField = new JTextField();
			searchField.setBounds(new Rectangle(31, 76, 209, 31));
			searchField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					performSearch(false);
				}
			});
		}
		return searchField;
	}

	/**
	 * This method initializes searchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setBounds(new Rectangle(53, 137, 84, 30));
			searchButton.setText(" Search");
			searchButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					performSearch(false);
				}
			});
		}
		return searchButton;
	}

	private void performSearch(Boolean reindex) {
		String query = getQuery();
		System.out.println(query);
		if (query.length() > 0) {
			// final CyNetwork currNetwork = netmgr.getCurrentNetwork();
			final CyNetwork currNetwork = net;
			// Mark the network for reindexing, if requested
			if (reindex) {
				final EnhancedSearch enhancedSearch = new EnhancedSearchFactoryImpl()
						.getGlobalEnhancedSearchInstance();
				enhancedSearch.setNetworkIndexStatus(currNetwork,
						EnhancedSearch.REINDEX);
			}

			// Define a new IndexAndSearchTask
			IndexAndSearchTaskImpl task = new IndexAndSearchTaskImpl(
					currNetwork, query);
			task.run();
			// Execute the task via the task manager

			// tm.execute(task);

		}
	}

	/**
	 * Returns the query string, trimmed
	 */
	public String getQuery() {
		String query = searchField.getText();
		query = query.trim();

		return query;
	}

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

} // @jve:decl-index=0:visual-constraint="255,19"
