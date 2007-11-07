package org.mskcc.pathway_commons.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.Observable;

import org.mskcc.pathway_commons.web_service.CPathProtocol;
import org.mskcc.pathway_commons.view.model.InteractionBundleModel;
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
     * @param interactionBundleModel InteractionBundleModel Object.
     * @param pathwayTableModel     PathwayTableModel Object.
     */
    public SearchDetailsPanel(InteractionBundleModel interactionBundleModel,
            PathwayTableModel pathwayTableModel) {
        GradientHeader header = new GradientHeader("Step 3:  Select Network(s)");
        setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel interactionPanel = createInteractionBundlePanel(interactionBundleModel);
        JPanel pathwayPane = createPathwayPane(pathwayTableModel);
        tabbedPane.add("Pathways", pathwayPane);
        tabbedPane.add("Interaction Networks", interactionPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createPathwayPane(PathwayTableModel pathwayTableModel) {
        JPanel pathwayPane = new JPanel(new BorderLayout());
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
    private JPanel createInteractionBundlePanel(final InteractionBundleModel
            interactionBundleModel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JLabel label = new JLabel ("Number of Interactions:  N/A");
        label.setBorder(new EmptyBorder(5,5,5,5));
        panel.add(label, BorderLayout.NORTH);

        interactionBundleModel.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                label.setText("Number of Interactions:  "
                        + interactionBundleModel.getNumInteractions());
            }
        });
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
        NetworkUtil networkUtil = new NetworkUtil(uri, null, false, null);
        networkUtil.start();
    }
}