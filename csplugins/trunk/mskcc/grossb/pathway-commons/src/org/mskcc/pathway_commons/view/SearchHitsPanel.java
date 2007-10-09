package org.mskcc.pathway_commons.view;

import org.mskcc.pathway_commons.schemas.search_response.SearchHitType;
import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;
import org.mskcc.pathway_commons.task.SelectPhysicalEntity;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.util.List;

/**
 * Search Hits Panel.
 *
 * @author Ethan Cerami.
 */
class SearchHitsPanel extends JPanel implements PathwayCommonsWebApiListener {
    private DefaultListModel peListModel;
    private JList peList;
    private SearchResponseType peSearchResponse;
    private Document summaryDocument;

    public SearchHitsPanel(DefaultTableModel interactionTableModel, DefaultTableModel
            pathwayTableModel, PathwayCommonsWebApi webApi) {
        webApi.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JTextPane summaryTextPane = createTextArea();
        summaryDocument = summaryTextPane.getDocument();
        JScrollPane summaryPane = encloseInJScrollPane("Summary", summaryTextPane);

        peListModel = new DefaultListModel();

        peList = new JList(peListModel);
        peList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peList.setPrototypeCellValue("12345678901234567890");
        JScrollPane hitListPane = new JScrollPane(peList);
        hitListPane.setBorder(new TitledBorder("Search Results"));

        JSplitPane splitPane = new JSplitPane (JSplitPane.VERTICAL_SPLIT, hitListPane,
                summaryPane);
        splitPane.setDividerLocation(300);
        this.add(splitPane);
        createListener(interactionTableModel, pathwayTableModel, summaryTextPane);
    }

    /**
     * Indicates that user has initiated a phsyical entity search.
     *
     * @param keyword        Keyword.
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     * @param startIndex     Start Index.
     */
    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex) {
        //  Currently no-op
    }

    /**
     * Indicates that a search for physical entities has just completed.
     *
     * @param peSearchResponse PhysicalEntitySearchResponse Object.
     */
    public void searchCompletedForPhysicalEntities(SearchResponseType peSearchResponse) {
        //  store for later reference
        this.peSearchResponse = peSearchResponse;

        //  Populate the hit list
        List<SearchHitType> searchHits = peSearchResponse.getSearchHit();
        peListModel.setSize(searchHits.size());
        int i = 0;
        for (SearchHitType searchHit : searchHits) {
            String name = searchHit.getName();
            peListModel.setElementAt(name, i++);
        }

        //  Select the first item in the list
        if (searchHits.size() > 0) {
            peList.setSelectedIndex(0);
        }
    }

    /**
     * Listen for list selection events.
     *
     * @param interactionTableModel InteractionTableModel.
     * @param pathwayTableModel     PathwayTableModel.
     */
    private void createListener(final DefaultTableModel interactionTableModel,
            final DefaultTableModel pathwayTableModel, final JTextPane summaryPane) {
        peList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = peList.getSelectedIndex();
                SelectPhysicalEntity selectTask = new SelectPhysicalEntity();
                selectTask.selectPhysicalEntity(peSearchResponse, selectedIndex,
                        interactionTableModel, pathwayTableModel, summaryDocument, summaryPane);
            }
        });
    }

    /**
     * Encloses the specified JTextPane in a JScrollPane.
     *
     * @param title    Title of Area.
     * @param textPane JTextPane Object.
     * @return JScrollPane Object.
     */
    private JScrollPane encloseInJScrollPane(String title, JTextPane textPane) {
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(new TitledBorder(title));
        return scrollPane;
    }

    /**
     * Creates a JTextArea with correct line wrap settings.
     *
     * @return JTextArea Object.
     */
    private JTextPane createTextArea() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        return textPane;
    }
}