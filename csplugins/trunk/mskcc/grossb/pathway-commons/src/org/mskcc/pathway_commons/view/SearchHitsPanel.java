package org.mskcc.pathway_commons.view;

import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySummary;
import org.mskcc.pathway_commons.web_service.model.PathwaySummary;
import org.mskcc.pathway_commons.web_service.model.InteractionBundleSummary;
import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySearchResponse;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;
import org.mskcc.pathway_commons.task.SelectPhysicalEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Search Hits Panel.
 *
 * @author Ethan Cerami.
 */
class SearchHitsPanel extends JPanel implements PathwayCommonsWebApiListener {
    private DefaultListModel peListModel;
    private JList peList;
    private PhysicalEntitySearchResponse peSearchResponse;
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

        add(hitListPane);
        add(summaryPane);
        createListener(interactionTableModel, pathwayTableModel);
    }

    /**
     * Indicates that user has initiated a phsyical entity search.
     * @param keyword           Keyword.
     * @param ncbiTaxonomyId    NCBI Taxonomy ID.
     * @param startIndex        Start Index.
     */
    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex) {
        //  Currently no-op
    }

    /**
     * Indicates that a search for physical entities has just completed.
     * @param peSearchResponse PhysicalEntitySearchResponse Object.
     */
    public void searchCompletedForPhysicalEntities(PhysicalEntitySearchResponse peSearchResponse) {
        //  store for later reference
        this.peSearchResponse = peSearchResponse;

        //  Populate the hit list
        ArrayList<PhysicalEntitySummary> peSummaryList =
                peSearchResponse.getPhysicalEntitySummartList();
        peListModel.setSize(peSummaryList.size());
        int i = 0;
        for (PhysicalEntitySummary peSummary: peSummaryList) {
            String name = peSummary.getName();
            peListModel.setElementAt(name, i++);
        }

        //  Select the first item in the list
        if (peSummaryList.size() > 0) {
            peList.setSelectedIndex(0);
        }
    }

    /**
     * Listen for list selection events.
     * @param interactionTableModel InteractionTableModel.
     * @param pathwayTableModel
     */
    private void createListener(final DefaultTableModel interactionTableModel,
            final DefaultTableModel pathwayTableModel) {
        peList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = peList.getSelectedIndex();
                SelectPhysicalEntity selectTask = new SelectPhysicalEntity();
                selectTask.selectPhysicalEntity(peSearchResponse, selectedIndex,
                        pathwayTableModel, interactionTableModel, summaryDocument);
            }
        });
    }

    /**
     * Encloses the specified JTextPane in a JScrollPane.
     * @param title     Title of Area.
     * @param textPane  JTextPane Object.
     * @return JScrollPane Object.
     */
    private JScrollPane encloseInJScrollPane (String title, JTextPane textPane) {
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(new TitledBorder(title));
        return scrollPane;
    }

    /**
     * Creates a JTextArea with correct line wrap settings.
     * @return JTextArea Object.
     */
    private JTextPane createTextArea () {
        JTextPane textPane = new JTextPane ();
        textPane.setEditable(false);
        return textPane;
    }
}