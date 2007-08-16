package csplugins.enhanced.search;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.lucene.search.Hits;
import org.apache.lucene.store.RAMDirectory;

import csplugins.enhanced.search.util.EnhancedSearchUtils;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

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
	public void indexingInProgress() {
		disableAllEnhancedSearchButtons();
		searchField.setToolTipText("Indexing network.  Please wait...");
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

		// Define search field
		searchField = new JTextField(30);
		searchField.setEnabled(false);
		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String query = getQuery();
				System.out.println(query);
				if (!query.trim().isEmpty()) {
					final CyNetwork currNetwork = Cytoscape.getCurrentNetwork();
					indexAndSearch(currNetwork, query);
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
		return query;
	}

	public void indexAndSearch(CyNetwork network, String query) {

		// Index the given network
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(network);
		RAMDirectory idx = indexHandler.getIndex();

		// Perform search
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);
		Hits hits = queryHandler.ExecuteQuery(query);

		// Display results
		EnhancedSearchUtils.displayResults(network, hits);
		queryHandler.close();
	}

}
