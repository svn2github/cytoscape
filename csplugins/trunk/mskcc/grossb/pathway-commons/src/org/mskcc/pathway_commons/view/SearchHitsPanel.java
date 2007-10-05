package org.mskcc.pathway_commons.view;

import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySummary;
import org.mskcc.pathway_commons.web_service.model.PathwaySummary;
import org.mskcc.pathway_commons.web_service.model.InteractionBundleSummary;
import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySearchResponse;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;

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
    DefaultListModel peListModel;
    private JList peList;
    private PhysicalEntitySearchResponse peSearchResponse;

    public SearchHitsPanel(DefaultTableModel interactionTableModel, DefaultTableModel
            pathwayTableModel, PathwayCommonsWebApi webApi) {
        webApi.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JTextArea summaryTextArea = createTextArea();
        Document summaryDocument = summaryTextArea.getDocument();
        JScrollPane summaryPane = encloseInJScrollPane("Summary", summaryTextArea);

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

                if (SearchHitsPanel.this.peSearchResponse != null) {
                    ArrayList<PhysicalEntitySummary> peList =
                            SearchHitsPanel.this.peSearchResponse.
                                    getPhysicalEntitySummartList();
                    PhysicalEntitySummary pe = peList.get(selectedIndex);
                    ArrayList <PathwaySummary> pathwayList = pe.getPathwayList();
                    ArrayList <InteractionBundleSummary> interactionList =
                            pe.getInteractionBundleList();

                    Vector dataVector = interactionTableModel.getDataVector();
                    dataVector.removeAllElements();

                    dataVector = pathwayTableModel.getDataVector();
                    dataVector.removeAllElements();

                    if (pathwayList != null) {
                        pathwayTableModel.setRowCount(pathwayList.size());
                        pathwayTableModel.setColumnCount(3);
                        for (int i=0; i<pathwayList.size(); i++) {
                            PathwaySummary pathway = pathwayList.get(i);
                            pathwayTableModel.setValueAt(pathway.getDataSourceName(), i, 0);
                            pathwayTableModel.setValueAt(pathway.getName(), i, 1);
                            pathwayTableModel.setValueAt(Boolean.FALSE, i, 2);
                        }
                    }

                    if (interactionList != null) {
                        interactionTableModel.setRowCount(interactionList.size());
                        interactionTableModel.setColumnCount(3);
                        for (int i=0; i<interactionList.size(); i++) {
                            InteractionBundleSummary interactionBundle = interactionList.get(i);
                            interactionTableModel.setValueAt
                                    (interactionBundle.getDataSourceName(), i, 0);
                            interactionTableModel.setValueAt
                                    (interactionBundle.getNumInteractions(), i, 1);
                            interactionTableModel.setValueAt(Boolean.FALSE, i, 2);
                        }
                    }
                }

            }
        });
    }

    /**
     * Encloses the specified JTextArea in a JScrollPane.
     * @param title     Title of Area.
     * @param textArea  JTextArea Object.
     * @return JScrollPane Object.
     */
    private JScrollPane encloseInJScrollPane (String title, JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new TitledBorder(title));
        return scrollPane;
    }

    /**
     * Creates a JTextArea with correct line wrap settings.
     * @return JTextArea Object.
     */
    private JTextArea createTextArea () {
        String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
                + "Pellentesque et arcu tincidunt massa interdum convallis. Nullam "
                + "cursus elementum est. Aenean gravida massa vel odio. Duis felis "
                + "purus, lobortis vitae, nonummy vitae, hendrerit in, neque. Etiam "
                + "eget nisi ac massa tempor scelerisque. Duis vel nisl sed erat bibendum "
                + "interdum. Aliquam quis est. Vivamus lacus risus, tempus a, euismod "
                + "fermentum, semper in, ante. Aenean vulputate dui ac sem. Fusce ac "
                + "urna nec ipsum vulputate dictum. Aenean eget neque vitae pede "
                + "porttitor ultrices. Maecenas dapibus nibh ac leo. Aliquam bibendum "
                + "accumsan massa.\n"
                + "Sed sagittis turpis a velit. Nam pharetra vestibulum dolor. "
                + "Donec accumsan. Nullam interdum pede a metus. Aenean lorem leo, "
                + "aliquam eget, rutrum sed, elementum sit amet, magna. Vivamus aliquam"
                + "enim at mauris. Maecenas congue tempor dui. Nam sit amet pede sed "
                + "metus ullamcorper dignissim. Ut mollis odio vitae libero. Mauris "
                + "ultrices. Aenean dignissim, dui id fringilla aliquam, dolor mauris "
                + "tincidunt felis, id interdum nulla tortor vitae purus.";
        JTextArea textArea = new JTextArea (text);
        textArea.setColumns(20);
        textArea.setRows(10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
}