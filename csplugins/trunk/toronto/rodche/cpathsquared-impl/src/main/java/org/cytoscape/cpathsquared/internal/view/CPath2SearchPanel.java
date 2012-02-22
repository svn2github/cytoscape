package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.cytoscape.cpathsquared.internal.CPath2;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Properties;
import org.cytoscape.cpathsquared.internal.CPath2Listener;

import cpath.service.jaxb.SearchResponse;

/**
 * Main GUI Panel for Searching a cPath Instance.
 *
 */
public class CPath2SearchPanel extends JPanel implements CPath2Listener {
    private JPanel searchQueryPanel;
    private JPanel searchResultsPanel;
    private JPanel cards;

    /**
     * Constructor.
     */
    public CPath2SearchPanel() { 

        //  Create main Border Layout
        setLayout(new BorderLayout());

        //  Create North Panel:  Search Box
        searchQueryPanel = CPath2Factory.createSearchQueryPanel();
        add(searchQueryPanel, BorderLayout.NORTH);

        cards = new JPanel(new CardLayout());
        searchResultsPanel = new SearchResultsPanel();

        cards.add (createAboutPanel(), "ABOUT");
        cards.add(searchResultsPanel, "HITS");
        add(cards, BorderLayout.CENTER);
        CPath2.addApiListener(this);
        this.setMinimumSize(new Dimension (300,40));
    }

    public void showAboutPanel() {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, "ABOUT");
    }

    private JPanel createAboutPanel() {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BorderLayout());
        GradientHeader header = new GradientHeader("About");

        aboutPanel.add(header, BorderLayout.NORTH);
        JTextPane textPane = DetailsPanel.createHtmlTextPane(CPath2Factory.getOpenBrowser());
        textPane.setText(CPath2Properties.blurb);
        aboutPanel.add(textPane, BorderLayout.CENTER);
        return aboutPanel;
    }

    public void searchInitiated(String keyword, Set<String> organism, Set<String> datasource) {
    }

    public void searchCompleted(SearchResponse peSearchResponse) {
        if (!peSearchResponse.isEmpty()) {
            if (!searchResultsPanel.isVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        CardLayout cl = (CardLayout)(cards.getLayout());
                        cl.show(cards, "HITS");                }
                });
            }
        }
    }

    /**
     * Initialize the Focus.  Can only be called after component has been
     * packed and displayed.
     */
    public void initFocus() {
        searchQueryPanel.requestFocusInWindow();
    }

}
