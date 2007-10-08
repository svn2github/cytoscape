package org.mskcc.pathway_commons.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
class SearchDetailsPanel extends JPanel {

    /**
     * Constructor.
     *
     * @param interactionTableModel InteractionTableModel Object.
     * @param pathwayTableModel     PathwayTableModel Object.
     */
    public SearchDetailsPanel(DefaultTableModel interactionTableModel,
            DefaultTableModel pathwayTableModel) {
        GridLayout gridLayout = new GridLayout(2, 0);
        setLayout(gridLayout);
        JPanel interactionPane = createInteractionBundleTable(interactionTableModel);
        JScrollPane pathwayPane = createPathwayTable(pathwayTableModel);
        add(pathwayPane);
        add(interactionPane);
    }

    /**
     * Creats the Interaction Bundle Table.
     *
     * @return JScrollPane Object.
     */
    private JPanel createInteractionBundleTable(DefaultTableModel interactionTableModel) {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Interactions"));
        panel.setLayout(new BorderLayout());
        final JTable interactionTable = new JTable(interactionTableModel);
        interactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        interactionTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rows[] = interactionTable.getSelectedRows();
                    JOptionPane.showMessageDialog(new JFrame(), "Downloading Interaction Bundle:  "
                            + rows[0]);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(interactionTable);
        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton downloadAll = new JButton("Download All Interactions");
        internalPanel.add(downloadAll);
        panel.add(internalPanel, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the Pathway Table.
     *
     * @return JScrollPane Object.
     */
    private JScrollPane createPathwayTable(DefaultTableModel pathwayTableModel) {
        final JTable pathwayTable = new JTable(pathwayTableModel);
        pathwayTable.setAutoCreateColumnsFromModel(true);
        pathwayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pathwayTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rows[] = pathwayTable.getSelectedRows();
                    JOptionPane.showMessageDialog(new JFrame(), "Downloading Pathway:  " + rows[0]);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(pathwayTable);
        scrollPane.setBorder(new TitledBorder("Pathways (double-click to download)"));
        return scrollPane;
    }
}