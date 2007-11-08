package org.mskcc.pathway_commons.view;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

import org.mskcc.pathway_commons.web_service.CPathProtocol;
import org.mskcc.pathway_commons.view.model.InteractionBundleModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;
import org.mskcc.pathway_commons.view.model.RecordList;
import org.mskcc.pathway_commons.view.tree.CheckNode;
import org.mskcc.pathway_commons.view.tree.JTreeWithCheckNodes;
import org.mskcc.pathway_commons.util.NetworkUtil;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchDetailsPanel extends JPanel {
    private CheckNode dataSourceFilter;
    private CheckNode interactionTypeFilter;

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

        final CheckNode rootNode = new CheckNode("Filters (optional)");
        final JTreeWithCheckNodes tree = new JTreeWithCheckNodes(rootNode);

        JScrollPane scrollPane = new JScrollPane (tree);
        panel.add(scrollPane, BorderLayout.CENTER);

        interactionBundleModel.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                RecordList recordList = interactionBundleModel.getRecordList();
                label.setText("Number of Interactions:  "+ recordList.getNumRecords());

                TreeMap<String, Integer> dataSourceMap = recordList.getDataSourceMap();
                TreeMap<String, Integer> entityTypeMap = recordList.getEntityTypeMap();

                //  Store current expansion states
                boolean dataSourceFilterExpanded = false;
                if (dataSourceFilter != null) {
                    TreePath path = new TreePath (dataSourceFilter.getPath());
                    dataSourceFilterExpanded = tree.isExpanded(path);
                }
                boolean interactionTypeFilterExpanded = false;
                if (interactionTypeFilter != null) {
                    TreePath path = new TreePath (interactionTypeFilter.getPath());
                    interactionTypeFilterExpanded = tree.isExpanded(path);
                }

                //  Remove all children
                rootNode.removeAllChildren();
                
                //  Create Data Source Filter
                if (dataSourceMap.size() > 0) {
                    dataSourceFilter = new CheckNode ("Filter by Data Source");
                    rootNode.add(dataSourceFilter);
                    for (String key:  dataSourceMap.keySet()) {
                        CheckNode dataSourceNode = new CheckNode(key + ": "
                            + dataSourceMap.get(key), false, true);
                        dataSourceFilter.add(dataSourceNode);
                    }
                    dataSourceFilter.setSelected(true);
                }

                //  Create Entity Type Filter
                if (entityTypeMap.size() > 0) {
                    interactionTypeFilter = new CheckNode ("Filter by Interaction Type");
                    rootNode.add(interactionTypeFilter);
                    for (String key:  entityTypeMap.keySet()) {
                        CheckNode dataSourceNode = new CheckNode(key + ": "
                            + entityTypeMap.get(key), false, true);
                        interactionTypeFilter.add(dataSourceNode);
                    }
                }
                DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
                tree.setModel(treeModel);

                //  Restore expansion state.
                if (dataSourceFilterExpanded) {
                    TreePath path = new TreePath (dataSourceFilter.getPath());
                    tree.expandPath(path);
                }
                if (interactionTypeFilterExpanded) {
                    TreePath path = new TreePath (interactionTypeFilter.getPath());
                    tree.expandPath(path);
                }
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