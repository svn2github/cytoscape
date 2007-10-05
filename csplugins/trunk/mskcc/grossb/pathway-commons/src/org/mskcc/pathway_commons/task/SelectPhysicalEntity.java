package org.mskcc.pathway_commons.task;

import org.mskcc.pathway_commons.web_service.model.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.*;

/**
 * Indicates that the user has selected a physical entity from the list of search results.
 *
 * @author Ethan Cerami.
 */
public class SelectPhysicalEntity {

    /**
     * Select the Phsyical Entity specified by the selected index.
     * @param peSearchResponse          PhysicalEntitySearchResponse peSearchResponse.
     * @param selectedIndex             Selected Index.
     * @param interactionTableModel     Interaction Table Model.
     * @param pathwayTableModel         Pathway Table Model.
     * @param summaryDocumentModel      Summary Document Model.
     */
    public void selectPhysicalEntity(PhysicalEntitySearchResponse peSearchResponse,
            int selectedIndex, DefaultTableModel interactionTableModel, DefaultTableModel
            pathwayTableModel, Document summaryDocumentModel) {
        if (peSearchResponse != null) {
            ArrayList<PhysicalEntitySummary> peList = peSearchResponse.
                    getPhysicalEntitySummartList();
            PhysicalEntitySummary pe = peList.get(selectedIndex);


            try {
                summaryDocumentModel.remove(0, summaryDocumentModel.getLength());
            } catch (BadLocationException e) {
            }
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            //StyleConstants.setForeground(attrs, Color.BLACK);
            //StyleConstants.setBold(attrs, true);
            try {
                if (pe.getDescription() != null) {
                    summaryDocumentModel.insertString(0, pe.getDescription(), attrs);
                }
                Organism organism = pe.getOrganism();
                if (organism != null) {
                    String speciesName = organism.getSpeciesName();
                    summaryDocumentModel.insertString(0, "[" +
                            speciesName+"]\n\n", attrs);
                }
                summaryDocumentModel.insertString(0, pe.getName()+"\n\n", attrs);
            } catch (BadLocationException e) {
            }
            updatePathwayData(pe, pathwayTableModel);
            updateInteractionData(pe, interactionTableModel);
        }
    }

    /**
     * Updates Interaction Data.
     * @param pe                        Physical Entity Summary Object.
     * @param interactionTableModel     Interaction Table Model.
     */
    private void updateInteractionData(PhysicalEntitySummary pe, DefaultTableModel
            interactionTableModel) {
        ArrayList<InteractionBundleSummary> interactionList =
                pe.getInteractionBundleList();
        Vector dataVector = interactionTableModel.getDataVector();
        dataVector.removeAllElements();
        if (interactionList != null) {
            interactionTableModel.setRowCount(interactionList.size());
            interactionTableModel.setColumnCount(3);
            for (int i = 0; i < interactionList.size(); i++) {
                InteractionBundleSummary interactionBundle = interactionList.get(i);
                interactionTableModel.setValueAt
                        (interactionBundle.getDataSourceName(), i, 0);
                interactionTableModel.setValueAt
                        (interactionBundle.getNumInteractions(), i, 1);
                interactionTableModel.setValueAt(Boolean.FALSE, i, 2);
            }
        }
    }

    /**
     * Updates Pathway Data.
     * @param pe                    Physical Entity Summary Object.
     * @param pathwayTableModel     Pathway Table Model.
     */
    private void updatePathwayData(PhysicalEntitySummary pe,
            DefaultTableModel pathwayTableModel) {
        ArrayList<PathwaySummary> pathwayList = pe.getPathwayList();
        Vector dataVector = pathwayTableModel.getDataVector();
        dataVector.removeAllElements();

        if (pathwayList != null) {
            pathwayTableModel.setRowCount(pathwayList.size());
            pathwayTableModel.setColumnCount(3);
            for (int i = 0; i < pathwayList.size(); i++) {
                PathwaySummary pathway = pathwayList.get(i);
                pathwayTableModel.setValueAt(pathway.getDataSourceName(), i, 0);
                pathwayTableModel.setValueAt(pathway.getName(), i, 1);
                pathwayTableModel.setValueAt(Boolean.FALSE, i, 2);
            }
        }
    }
}