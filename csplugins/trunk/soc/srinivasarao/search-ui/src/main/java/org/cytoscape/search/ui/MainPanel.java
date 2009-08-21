package org.cytoscape.search.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.cytoscape.session.CyNetworkManager;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel resultsLabel = null;
	private JRadioButton selectButton = null;
	private JRadioButton showButton = null;
	private JRadioButton hideButton = null;
	private JTextField searchField = null;
	private SearchComboBox searchBox = null;
	private JRadioButton orButton = null;
	private JRadioButton andButton = null;
	private JButton searchButton = null;
	private ButtonGroup resultGroup = null;
	private ButtonGroup operatorGroup = null;

	private CyNetworkManager netmgr;

	private static final String ESP_LABEL = "ESP:  ";

	// private static final String SEARCH_MENU_ITEM = "Search";

	private static final String REINDEX_MENU_ITEM = "Re-index and search";

	private static final String CLEAR_ALL_MENU_ITEM = "Clear All";

	private static final String CLEAR_ALL_TOOLTIP = "Clears the Search Field and also the Query Builder";

	private static final String CLEAR_HISTORY_TOOLTIP = "Clears the Search History";

	private static final String CLEAR_HISTORY_MENU_ITEM = "Clear History";

	// private static final String SEARCH_TOOLTIP = "Perform search";

	private static final String REINDEX_TOOLTIP = "<html>"
			+ "Refresh the network index and perform search." + "<br>"
			+ "This option is useful after changes to attributes." + "</html>";

	private static final String ESP_ENABLED_TOOLTIP = "<html>"
			+ "Enter search query and press return. " + "<br>"
			+ "Right click for more options." + "</html>";

	private static final String ESP_DISABLED_TOOLTIP = "Please select or load a network to activate search functionality";

	/**
	 * This is the default constructor
	 */
	public MainPanel(CyNetworkManager nm) {
		super();
		initialize();
		this.netmgr = nm;
		if (netmgr.getCurrentNetwork() == null)
			disableSearch();
	}

	public void disableSearch() {
		searchField.setText("");
		searchBox.setSelectedItem(null);
		searchField.setEnabled(false);
		searchField.setToolTipText(ESP_DISABLED_TOOLTIP);
		searchButton.setEnabled(false);
	}

	public void enableSearch() {
		searchField.setEnabled(true);
		searchButton.setEnabled(true);
		searchField.setToolTipText(ESP_ENABLED_TOOLTIP);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.gridx = 3;
		gridBagConstraints8.gridy = 2;
		gridBagConstraints8.insets = new Insets(2, 0, 5, 5);
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.gridy = 2;
		gridBagConstraints7.insets = new Insets(2, 0, 5, 0);
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.gridy = 2;
		gridBagConstraints6.insets = new Insets(2, 5, 5, 0);
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.BOTH;
		gridBagConstraints5.gridy = 1;
		gridBagConstraints5.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.weighty = 1.0;
		gridBagConstraints5.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints5.gridx = 0;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 3;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.insets = new Insets(5, 0, 0, 5);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.insets = new Insets(5, 0, 0, 0);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.insets = new Insets(5, 0, 0, 0);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 4;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;

		resultsLabel = new JLabel(ESP_LABEL);
		resultsLabel.setText("Results");
		this.setSize(590, 493);
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		this.add(resultsLabel, gridBagConstraints);
		this.add(getSelectButton(), gridBagConstraints1);
		this.add(getShowButton(), gridBagConstraints2);
		this.add(getHideButton(), gridBagConstraints3);
		this.add(getSearchBox(), gridBagConstraints5);
		this.add(getOrButton(), gridBagConstraints6);
		this.add(getAndButton(), gridBagConstraints7);
		this.add(getSearchButton(), gridBagConstraints8);
		createButtonGroups();
		createPopupMenu();
		this.add(Box.createRigidArea(null), gc);
	}

	/**
	 * This method initializes selectButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getSelectButton() {
		if (selectButton == null) {
			selectButton = new JRadioButton();
			selectButton.setText("Select");
			selectButton.setActionCommand("Select");
		}
		return selectButton;
	}

	/**
	 * This method initializes showButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getShowButton() {
		if (showButton == null) {
			showButton = new JRadioButton();
			showButton.setText("Show");
			showButton.setActionCommand("Show");
		}
		return showButton;
	}

	/**
	 * This method initializes hideButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getHideButton() {
		if (hideButton == null) {
			hideButton = new JRadioButton();
			hideButton.setText("Hide");
			hideButton.setActionCommand("Hide");
		}
		return hideButton;
	}

	public SearchComboBox getSearchBox() {
		if (searchBox == null) {
			searchBox = new SearchComboBox(netmgr);
			searchField = (JTextField) searchBox.getEditor()
					.getEditorComponent();
			searchField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SearchPanelFactory.getGlobalInstance(netmgr).performSearch(
							false);
					// searchField.setText("");
					// searchBox.setSelectedItem(null);
					// SearchPanelFactory.getGlobalInstance(netmgr).clearAll();
				}
			});
		}
		return searchBox;
	}

	/**
	 * 
	 * @param item
	 */
	public void addtoHistory(String item) {
		searchBox.addMenuItem(item);
	}

	/**
	 * This method initializes orButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getOrButton() {
		if (orButton == null) {
			orButton = new JRadioButton();
			orButton.setText("OR");
			orButton.setActionCommand("OR");
			orButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (orButton.isSelected()) {
						SearchPanelFactory.getGlobalInstance(netmgr)
								.updateSearchField();
					}
				}
			});
		}
		return orButton;
	}

	/**
	 * This method initializes andButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getAndButton() {
		if (andButton == null) {
			andButton = new JRadioButton();
			andButton.setText("AND");
			andButton.setActionCommand("AND");
			andButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (andButton.isSelected()) {
						SearchPanelFactory.getGlobalInstance(netmgr)
								.updateSearchField();
					}
				}
			});
		}
		return andButton;
	}

	/**
	 * This method initializes searchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setText("Search");
			searchButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SearchPanelFactory.getGlobalInstance(netmgr).performSearch(
							false);
					// searchField.setText("");
					// searchBox.setSelectedItem(null);
					// SearchPanelFactory.getGlobalInstance(netmgr).clearAll();
				}
			});
		}
		return searchButton;
	}

	private void createButtonGroups() {
		resultGroup = new ButtonGroup();
		resultGroup.add(selectButton);
		resultGroup.add(showButton);
		resultGroup.add(hideButton);
		operatorGroup = new ButtonGroup();
		operatorGroup.add(andButton);
		operatorGroup.add(orButton);
		selectButton.setSelected(true);
		orButton.setSelected(true);
	}

	public String getResult() {
		String res = resultGroup.getSelection().getActionCommand();
		System.out.println("In getresult:" + res);
		return res;
	}

	public String getOperator() {
		String res = operatorGroup.getSelection().getActionCommand();
		return res;
	}

	public String getQuery() {
		String query = searchField.getText();
		return query;
	}

	public void setSearchText(String query) {
		searchField.setText(query);
	}

	public void clearAll(){
		orButton.setSelected(true);
		selectButton.setSelected(true);
		searchField.setText("");
		searchBox.setSelectedItem(null);
	}
	
	private void createPopupMenu() {
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();

		// Add 'Clear All' menu item
		menuItem = new JMenuItem(CLEAR_ALL_MENU_ITEM);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearAll();
				SearchPanelFactory.getGlobalInstance(netmgr).clearAll();
			}
		});
		menuItem.setToolTipText(CLEAR_ALL_TOOLTIP);
		popup.add(menuItem);

		// Add 'Clear History' menu item
		menuItem = new JMenuItem(CLEAR_HISTORY_MENU_ITEM);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchBox.clearAllItems();
			}
		});
		menuItem.setToolTipText(CLEAR_HISTORY_TOOLTIP);
		popup.add(menuItem);

		// Add 'Reindex and search' menu item
		menuItem = new JMenuItem(REINDEX_MENU_ITEM);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchPanelFactory.getGlobalInstance(netmgr)
						.performSearch(true);
				clearAll();
				SearchPanelFactory.getGlobalInstance(netmgr).clearAll();

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
}
