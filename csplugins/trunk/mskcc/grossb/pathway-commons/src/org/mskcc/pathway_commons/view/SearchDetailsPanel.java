package org.mskcc.pathway_commons.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.mskcc.pathway_commons.web_service.CPathProtocol;
import org.mskcc.pathway_commons.view.model.InteractionTableModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;
import org.mskcc.pathway_commons.util.NetworkUtil;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchDetailsPanel extends JPanel {

    /**
     * Constructor.
     *
     * @param interactionTableModel InteractionTableModel Object.
     * @param pathwayTableModel     PathwayTableModel Object.
     */
    public SearchDetailsPanel(InteractionTableModel interactionTableModel,
            PathwayTableModel pathwayTableModel) {
        GridLayout gridLayout = new GridLayout(1, 0);
        setLayout(gridLayout);
        //JPanel interactionPane = createInteractionBundleTable(interactionTableModel);
        JPanel pathwayPane = createPathwayPane(pathwayTableModel);
        add(pathwayPane);
        //add(interactionPane);
    }

    private JPanel createPathwayPane(PathwayTableModel pathwayTableModel) {
        JPanel pathwayPane = new JPanel(new BorderLayout());

        GradientHeader header = new GradientHeader("Step 3:  Download");
        pathwayPane.add(header, BorderLayout.NORTH);

        JScrollPane pathwayTable = createPathwayTable(pathwayTableModel);
        pathwayPane.add(pathwayTable, BorderLayout.CENTER);
        JLabel label = new JLabel ("> Double-click pathway to download.");
        Font font = label.getFont();
        Font newFont = new Font(font.getFamily(), Font.PLAIN, font.getSize()-2);
        label.setFont(newFont);
        pathwayPane.add(label, BorderLayout.SOUTH);
        return pathwayPane;
    }

    /**
     * Creats the Interaction Bundle Table.
     *
     * @return JScrollPane Object.
     */
    private JPanel createInteractionBundleTable(InteractionTableModel interactionTableModel) {
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
    private JScrollPane createPathwayTable(final PathwayTableModel pathwayTableModel) {
        final JTable pathwayTable = new JTable(pathwayTableModel);
        pathwayTable.setAutoCreateColumnsFromModel(true);
        pathwayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pathwayTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rows[] = pathwayTable.getSelectedRows();
                    if (rows.length > 0) {
                        downloadPathway(rows, pathwayTableModel);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(pathwayTable);
        return scrollPane;
    }

    /**
     * Downloads a single pathway in a new thread.
     * @param rows                  Selected row.
     * @param pathwayTableModel     Pathway Table Model.
     */
    private void downloadPathway(int[] rows, PathwayTableModel pathwayTableModel) {
        long internalId = pathwayTableModel.getInternalId(rows[0]);
        CPathProtocol protocol = new CPathProtocol();
        protocol.setCommand(CPathProtocol.COMMAND_GET_RECORD_BY_CPATH_ID);
        protocol.setQuery(Long.toString(internalId));
        protocol.setFormat(CPathProtocol.FORMAT_BIOPAX);
        String uri = protocol.getURI();
        System.out.println("Connecting to:  " + uri);
        NetworkUtil networkUtil = new NetworkUtil(uri, null, false, null);
        networkUtil.start();
    }
}