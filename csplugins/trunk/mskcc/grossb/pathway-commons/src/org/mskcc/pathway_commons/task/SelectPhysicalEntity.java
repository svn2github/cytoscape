package org.mskcc.pathway_commons.task;

import org.mskcc.pathway_commons.schemas.search_response.*;
import org.mskcc.pathway_commons.view.SearchDetailsPanel;
import org.mskcc.pathway_commons.view.model.InteractionTableModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.*;
import java.util.List;
import java.util.Vector;
import java.awt.*;

import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.Cytoscape;

/**
 * Indicates that the user has selected a physical entity from the list of search results.
 *
 * @author Ethan Cerami.
 */
public class SelectPhysicalEntity {
    private static boolean initialized = false;

    /**
     * Select the Phsyical Entity specified by the selected index.
     *
     * @param peSearchResponse      SearchResponseType peSearchResponse.
     * @param selectedIndex         Selected Index.
     * @param interactionTableModel Interaction Table Model.
     * @param pathwayTableModel     Pathway Table Model.
     * @param summaryDocumentModel  Summary Document Model.
     */
    public void selectPhysicalEntity(SearchResponseType peSearchResponse,
            int selectedIndex, InteractionTableModel interactionTableModel, PathwayTableModel
            pathwayTableModel, Document summaryDocumentModel, JTextPane textPane) {
        if (peSearchResponse != null) {
            java.util.List<SearchHitType> searchHits = peSearchResponse.getSearchHit();
            SearchHitType searchHit = searchHits.get(selectedIndex);

            try {
                summaryDocumentModel.remove(0, summaryDocumentModel.getLength());
            } catch (BadLocationException e) {
            }
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            try {
                java.util.List<String> commentList = searchHit.getComment();
                StringBuffer commentBuf = new StringBuffer();
                if (commentList != null) {
                    for (int i = commentList.size() - 1; i >= 0; i--) {
                        commentBuf.append(commentList.get(i) + "\n\n");
                    }
                }
                summaryDocumentModel.insertString(0, commentBuf.toString(), attrs);
                OrganismType organism = searchHit.getOrganism();
                StyleConstants.setForeground(attrs, Color.BLUE);
                StyleConstants.setBold(attrs, true);
                if (organism != null) {
                    String speciesName = organism.getSpeciesName();
                    summaryDocumentModel.insertString(0, "[" +
                            speciesName + "]\n\n", attrs);
                }
                StyleConstants.setFontSize(attrs, 18);
                summaryDocumentModel.insertString(0, searchHit.getName() + "\n", attrs);
                textPane.setCaretPosition(0);
            } catch (BadLocationException e) {
            }
            updatePathwayData(searchHit, pathwayTableModel);
            updateInteractionData(searchHit, interactionTableModel);
        }
    }

    /**
     * Updates Interaction Data.
     *
     * @param searchHit             Search Hit Object.
     * @param interactionTableModel Interaction Table Model.
     */
    private void updateInteractionData(SearchHitType searchHit, DefaultTableModel
            interactionTableModel) {
        List<InteractionBundleType> interactionBundleList =
                searchHit.getInteractionBundleList().getInteractionBundle();
        Vector dataVector = interactionTableModel.getDataVector();
        dataVector.removeAllElements();
        if (interactionBundleList != null) {
            interactionTableModel.setRowCount(interactionBundleList.size());
            interactionTableModel.setColumnCount(2);
            for (int i = 0; i < interactionBundleList.size(); i++) {
                InteractionBundleType interactionBundle = interactionBundleList.get(i);
                interactionTableModel.setValueAt
                        (interactionBundle.getDataSource().getName(), i, 0);
                interactionTableModel.setValueAt
                        (interactionBundle.getNumInteractions(), i, 1);
            }
        }
    }

    /**
     * Updates Pathway Data.
     *
     * @param searchHit         SearchHit Object.
     * @param pathwayTableModel Pathway Table Model.
     */
    private void updatePathwayData(SearchHitType searchHit, PathwayTableModel pathwayTableModel) {
        List<PathwayType> pathwayList = searchHit.getPathwayList().getPathway();

        Vector dataVector = pathwayTableModel.getDataVector();
        dataVector.removeAllElements();

        if (pathwayList != null) {
            pathwayTableModel.setRowCount(pathwayList.size());
            pathwayTableModel.resetInternalIds(pathwayList.size());
            //  Only set the column count, if it is not already set.
            //  If we reset the column count, the user-modified column widths are lost.
            if (pathwayTableModel.getColumnCount() != 2) {
                pathwayTableModel.setColumnCount(2);
            }
            for (int i = 0; i < pathwayList.size(); i++) {
                PathwayType pathway = pathwayList.get(i);
                pathwayTableModel.setValueAt(pathway.getDataSource().getName(), i, 0);
                pathwayTableModel.setValueAt(pathway.getName(), i, 1);
                pathwayTableModel.setInternalId(i, pathway.getPrimaryId());
            }
        }
    }
}