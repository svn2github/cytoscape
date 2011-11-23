package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPathProperties;
import org.cytoscape.cpathsquared.internal.CPathWebService;
import org.cytoscape.cpathsquared.internal.CPathWebServiceListener;

import cpath.service.jaxb.SearchResponse;

/**
 * Main GUI Panel for Searching a cPath Instance.
 *
 * @author Ethan Cerami.
 */
public class CPathSearchPanel extends JPanel implements CPathWebServiceListener {
    protected InteractionBundleModel interactionBundleModel;
    protected PathwayTableModel pathwayTableModel;
    protected CPathWebService webApi;
    private JPanel searchBoxPanel;
    private JPanel searchHitsPanel = null;
    private JPanel cards;
	private final CPath2Factory factory;

    /**
     * Constructor.
     *
     * @param webApi CPathWebService API.
     */
    public CPathSearchPanel(CPathWebService webApi, CPath2Factory factory) {
    	this.factory = factory;
    	
        //  Store the web API model
        this.webApi = webApi;

        //  Create shared model classes
        interactionBundleModel = new InteractionBundleModel();
        pathwayTableModel = new PathwayTableModel();

        //  Create main Border Layout
        setLayout(new BorderLayout());

        //  Create North Panel:  Search Box
        searchBoxPanel = factory.createSearchBoxPanel(webApi);
        add(searchBoxPanel, BorderLayout.NORTH);

        cards = new JPanel(new CardLayout());
        searchHitsPanel = createSearchResultsPanel();

        JPanel aboutPanel = createAboutPanel();
        cards.add (aboutPanel, "ABOUT");
        cards.add(searchHitsPanel, "HITS");
        add(cards, BorderLayout.CENTER);
        webApi.addApiListener(this);
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
        JTextPane textPane = SearchHitDetailsPanel.createHtmlTextPane(factory.getOpenBrowser());
        textPane.setText(CPathProperties.blurb);
        aboutPanel.add(textPane, BorderLayout.CENTER);
        return aboutPanel;
    }

    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId) {
    }

    public void searchCompletedForPhysicalEntities(SearchResponse peSearchResponse) {
        if (!peSearchResponse.isEmpty()) {
            if (!searchHitsPanel.isVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        CardLayout cl = (CardLayout)(cards.getLayout());
                        cl.show(cards, "HITS");                }
                });
            }
        }
    }

    public void requestInitiatedForParentSummaries(String primaryId) {
        //  Currently no-op
    }

    /**
     * Initialize the Focus.  Can only be called after component has been
     * packed and displayed.
     */
    public void initFocus() {
        searchBoxPanel.requestFocusInWindow();
    }

    /**
     * Creates the Search Results Split Pane.
     *
     * @return JSplitPane Object.
     */
    private JPanel createSearchResultsPanel() {
        JPanel hitListPanel = factory.createSearchHitsPanel(this.interactionBundleModel,
                this.pathwayTableModel, webApi);
        return hitListPanel;
    }

	public void requestCompletedForParentSummaries(String primaryId,
			SearchResponse parents) {
	}
}
