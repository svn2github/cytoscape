package org.cytoscape.coreplugin.cpath2.view;

import org.cytoscape.coreplugin.cpath2.view.model.InteractionBundleModel;
import org.cytoscape.coreplugin.cpath2.view.model.PathwayTableModel;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebServiceListener;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebServiceImpl;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.SearchResponseType;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.SummaryResponseType;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI Panel for Searching a cPath Instance.
 *
 * @author Ethan Cerami.
 */
public class cPathSearchPanel extends JPanel implements CPathWebServiceListener {
    protected InteractionBundleModel interactionBundleModel;
    protected PathwayTableModel pathwayTableModel;
    protected CPathWebService webApi;
    private JPanel searchBoxPanel;
    private JPanel searchHitsPanel = null;
    private JPanel cards;

    /**
     * Constructor.
     *
     * @param webApi CPathWebService API.
     */
    public cPathSearchPanel(CPathWebService webApi) {

        //  Store the web API model
        this.webApi = webApi;

        //  Create shared model classes
        interactionBundleModel = new InteractionBundleModel();
        pathwayTableModel = new PathwayTableModel();

        //  Create main Border Layout
        setLayout(new BorderLayout());

        //  Create North Panel:  Search Box
        searchBoxPanel = new SearchBoxPanel(webApi);
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
        JTextPane textPane = PhysicalEntityDetailsPanel.createHtmlTextPane();
        textPane.setText(CPathProperties.getInstance().getCPathBlurb());
        aboutPanel.add(textPane, BorderLayout.CENTER);
        return aboutPanel;
    }

    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId) {
    }

    public void searchCompletedForPhysicalEntities(SearchResponseType peSearchResponse) {
        if (peSearchResponse.getTotalNumHits() > 0) {
            if (!searchHitsPanel.isVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        CardLayout cl = (CardLayout)(cards.getLayout());
                        cl.show(cards, "HITS");                }
                });
            }
        }
    }

    public void requestInitiatedForParentSummaries(long primaryId) {
        //  Currently no-op
    }

    public void requestCompletedForParentSummaries(long primaryId,
            SummaryResponseType summaryResponse) {
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
        JPanel hitListPanel = new SearchHitsPanel(this.interactionBundleModel,
                this.pathwayTableModel, webApi);
        return hitListPanel;
    }

    /**
     * Main Method.  Used for debugging purposes only.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        cPathSearchPanel form = new cPathSearchPanel(
                CPathWebServiceImpl.getInstance());
        frame.getContentPane().add(form);
        frame.pack();
        form.initFocus();
        frame.setVisible(true);
    }
}