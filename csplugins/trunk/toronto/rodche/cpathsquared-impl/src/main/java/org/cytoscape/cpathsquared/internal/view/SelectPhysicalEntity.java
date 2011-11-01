package org.cytoscape.cpathsquared.internal.view;

import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import cpath.service.jaxb.*;

import org.apache.commons.lang.StringUtils;

/**
 * Indicates that the user has selected a physical entity from the list of search results.
 *
 * @author Ethan Cerami.
 */
public class SelectPhysicalEntity {
    private HashMap<String, RecordList> parentRecordsMap;

    /**
     * Constructor.
     *
     * @param parentRecordsMap  RecordList.
     */
    public SelectPhysicalEntity (HashMap<String, RecordList> parentRecordsMap) {
        this.parentRecordsMap = parentRecordsMap;
    }

    /**
     * Select the Phsyical Entity specified by the selected index.
     *
     * @param peSearchResponse      SearchResponseType peSearchResponse.
     * @param selectedIndex         Selected Index.
     * @param interactionBundleModel Interaction Table Model.
     * @param pathwayTableModel     Pathway Table Model.
     * @param summaryDocumentModel  Summary Document Model.
     */
    public void selectPhysicalEntity(SearchResponse peSearchResponse,
            int selectedIndex, InteractionBundleModel interactionBundleModel, PathwayTableModel
            pathwayTableModel, Document summaryDocumentModel,
            JTextPane textPane, JComponent textPaneOwner) {
        if (peSearchResponse != null) {
            java.util.List<SearchHit> searchHits = peSearchResponse.getSearchHit();
            SearchHit searchHit = searchHits.get(selectedIndex);

            StringBuffer html = new StringBuffer();
            html.append("<html>");

            html.append ("<h2>" + searchHit.getName() + "</h2>");

            List<String> organism = searchHit.getOrganism();
            if (organism != null && !organism.isEmpty()) {
                html.append ("<H3>" + StringUtils.join(organism, ",") + "</H3>");
            }

            String primeExcerpt = searchHit.getExcerpt();
            if (primeExcerpt != null) {
                html.append("<H4>Matching Excerpt(s):</H4>");
                html.append("<span class='excerpt'>" + primeExcerpt + "</span><BR>") ;
            }

            html.append ("</html>");
            textPane.setText(html.toString());
            textPane.setCaretPosition(0);
            updatePathwayData(searchHit, pathwayTableModel);
            updateInteractionData(searchHit, interactionBundleModel);
			textPaneOwner.repaint();
        }
    }

    /**
     * Updates Interaction Data.
     *
     * @param searchHit             Search Hit Object.
     * @param interactionBundleModel Interaction Bundle Model.
     */
    private void updateInteractionData(SearchHit searchHit, InteractionBundleModel
            interactionBundleModel) {
        RecordList recordList = parentRecordsMap.get(searchHit.getUri());
        if (recordList != null) {
            interactionBundleModel.setRecordList(recordList);
        } else {
            SearchResponse summaryResponseType = new SearchResponse();
            recordList = new RecordList(summaryResponseType);
            interactionBundleModel.setRecordList(recordList);
        }
        interactionBundleModel.setPhysicalEntityName(searchHit.getName());
    }

    /**
     * Updates Pathway Data.
     *
     * @param searchHit         SearchHit Object.
     * @param pathwayTableModel Pathway Table Model.
     */
    private void updatePathwayData(SearchHit searchHit, PathwayTableModel pathwayTableModel) 
    {
        List<String> pathwayList = searchHit.getPathway();

        pathwayTableModel.getDataVector().removeAllElements();

        if (!pathwayList.isEmpty()) {
            pathwayTableModel.setRowCount(pathwayList.size());
            pathwayTableModel.resetInternalIds(pathwayList.size());
            //  Only set the column count, if it is not already set.
            //  If we reset the column count, the user-modified column widths are lost.
            if (pathwayTableModel.getColumnCount() != 2) {
                pathwayTableModel.setColumnCount(2);
            }
            if (pathwayList.size() == 0) {
                pathwayTableModel.setRowCount(1);
                pathwayTableModel.setValueAt("No pathways found.", 0, 0);    
            } else {
                for (int i = 0; i < pathwayList.size(); i++) {
                    String pathway = pathwayList.get(i);
                    //TODO get pathway's name, datasource, etc
                    pathwayTableModel.setValueAt("TODO: name", i, 0);
                    pathwayTableModel.setValueAt("TODO: datasource", i, 1);
                    pathwayTableModel.setInternalId(i, pathway);
                }
            }
        }
    }
}