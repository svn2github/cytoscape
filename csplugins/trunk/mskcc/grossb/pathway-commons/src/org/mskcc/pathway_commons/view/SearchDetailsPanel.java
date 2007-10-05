package org.mskcc.pathway_commons.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
class SearchDetailsPanel extends JPanel {

    /**
     * Constructor.
     * @param interactionTableModel     InteractionTableModel Object.
     * @param pathwayTableModel         PathwayTableModel Object.
     */
    public SearchDetailsPanel(DefaultTableModel interactionTableModel,
            DefaultTableModel pathwayTableModel) {
        GridLayout gridLayout = new GridLayout (2,0);
        setLayout(gridLayout);
        JScrollPane interactionPane = createInteractionBundleTable(interactionTableModel);
        JScrollPane pathwayPane = createPathwayTable(pathwayTableModel);
        add(pathwayPane);
        add(interactionPane);
    }

    /**
     * Creats the Interaction Bundle Table.
     * @return JScrollPane Object.
     */
    private JScrollPane createInteractionBundleTable(DefaultTableModel interactionTableModel) {
        JTable table = new JTable(interactionTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new TitledBorder("Interactions"));
        return scrollPane;
    }

    /**
     * Creates the Pathway Table.
     * @return JScrollPane Object.
     */
    private JScrollPane createPathwayTable(DefaultTableModel pathwayTableModel) {
        JTable pathwayTable = new JTable(pathwayTableModel);
        pathwayTable.setAutoCreateColumnsFromModel(true);
        pathwayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(pathwayTable);
        scrollPane.setBorder(new TitledBorder("Pathways"));
        return scrollPane;
    }
}