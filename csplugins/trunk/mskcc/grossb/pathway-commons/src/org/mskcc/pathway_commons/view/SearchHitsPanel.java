package org.mskcc.pathway_commons.view;

import org.mskcc.pathway_commons.schemas.search_response.SearchHitType;
import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;
import org.mskcc.pathway_commons.task.SelectPhysicalEntity;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;
import org.mskcc.pathway_commons.view.model.InteractionTableModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.util.List;
import java.awt.*;

/**
 * Search Hits Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchHitsPanel extends JPanel implements PathwayCommonsWebApiListener {
    private DefaultListModel peListModel;
    private JList peList;
    private SearchResponseType peSearchResponse;
    private Document summaryDocument;
    private String currentKeyword;
    private JScrollPane hitListPane;
    private InteractionTableModel interactionTableModel;
    private PathwayTableModel pathwayTableModel;
    private JTextPane summaryTextPane;

    public SearchHitsPanel(InteractionTableModel interactionTableModel, PathwayTableModel
            pathwayTableModel, PathwayCommonsWebApi webApi) {
        this.interactionTableModel = interactionTableModel;
        this.pathwayTableModel = pathwayTableModel;
        webApi.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        summaryTextPane = createTextArea();
        summaryDocument = summaryTextPane.getDocument();
        JScrollPane summaryPane = encloseInJScrollPane("Summary", summaryTextPane);

        peListModel = new DefaultListModel();

        peList = new JList(peListModel);
        peList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peList.setPrototypeCellValue("12345678901234567890");
        hitListPane = new JScrollPane(peList);
        hitListPane.setBorder(new TitledBorder("Search Results"));

        JSplitPane splitPane = new JSplitPane (JSplitPane.VERTICAL_SPLIT, hitListPane,
                summaryPane);
        splitPane.setDividerLocation(200);
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
        this.currentKeyword = keyword;
    }

    /**
     * Indicates that a search for physical entities has just completed.
     *
     * @param peSearchResponse PhysicalEntitySearchResponse Object.
     */
    public void searchCompletedForPhysicalEntities(final SearchResponseType peSearchResponse) {

        if (peSearchResponse.getTotalNumHits() > 0) {
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
                SelectPhysicalEntity selectTask = new SelectPhysicalEntity();
                selectTask.selectPhysicalEntity(peSearchResponse, 0,
                        interactionTableModel, pathwayTableModel, summaryDocument, 
                        summaryTextPane);
            }

            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    hitListPane.setBorder(new TitledBorder
                            ("Search Results [Num hits: "
                            + peSearchResponse.getTotalNumHits() + "]"));
                }
            });

        } else {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    Window window = SwingUtilities.getWindowAncestor(SearchHitsPanel.this);
                    JOptionPane.showMessageDialog(window, "No matches found for:  "
                            + currentKeyword, "Search Results", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    /**
     * Listen for list selection events.
     *
     * @param interactionTableModel InteractionTableModel.
     * @param pathwayTableModel     PathwayTableModel.
     */
    private void createListener(final InteractionTableModel interactionTableModel,
            final PathwayTableModel pathwayTableModel, final JTextPane textPane) {
        peList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = peList.getSelectedIndex();
                if (selectedIndex >=0) {
                    SelectPhysicalEntity selectTask = new SelectPhysicalEntity();
                    selectTask.selectPhysicalEntity(peSearchResponse, selectedIndex,
                            interactionTableModel, pathwayTableModel, summaryDocument, textPane);
                }
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