package org.cytoscape.coreplugin.cpath2.view;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.net.URL;

import org.cytoscape.coreplugin.cpath2.view.model.InteractionBundleModel;
import org.cytoscape.coreplugin.cpath2.view.model.PathwayTableModel;
import org.cytoscape.coreplugin.cpath2.view.model.RecordList;
import org.cytoscape.coreplugin.cpath2.view.model.NetworkWrapper;
import org.cytoscape.coreplugin.cpath2.view.tree.JTreeWithCheckNodes;
import org.cytoscape.coreplugin.cpath2.view.tree.CheckNode;
import org.cytoscape.coreplugin.cpath2.filters.ChainedFilter;
import org.cytoscape.coreplugin.cpath2.filters.DataSourceFilter;
import org.cytoscape.coreplugin.cpath2.filters.EntityTypeFilter;
import org.cytoscape.coreplugin.cpath2.task.ExecuteGetRecordByCPathId;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.BasicRecordType;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebServiceImpl;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.web_service.CPathResponseFormat;
import org.cytoscape.coreplugin.cpath2.util.NetworkMergeUtil;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchDetailsPanel extends JPanel {
    private InteractionBundleModel interactionBundleModel;

    /**
     * Constructor.
     *
     * @param interactionBundleModel InteractionBundleModel Object.
     * @param pathwayTableModel     PathwayTableModel Object.
     */
    public SearchDetailsPanel(InteractionBundleModel interactionBundleModel,
            PathwayTableModel pathwayTableModel) {
        this.interactionBundleModel = interactionBundleModel;
        GradientHeader header = new GradientHeader("Step 3:  Select Network(s)");
        setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel interactionPanel = new InteractionBundlePanel(interactionBundleModel);
        JPanel pathwayPane = createPathwayPane(pathwayTableModel);
        Font font = tabbedPane.getFont();
        Font newFont = new Font (font.getFamily(), Font.PLAIN, font.getSize()-2);
        tabbedPane.setFont(newFont);

        tabbedPane.add("Pathways", pathwayPane);
        tabbedPane.add("Interaction Networks", interactionPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createPathwayPane(PathwayTableModel pathwayTableModel) {
        JPanel pathwayPane = new JPanel(new BorderLayout());
        JScrollPane pathwayTable = createPathwayTable(pathwayTableModel);
        pathwayPane.add(pathwayTable, BorderLayout.CENTER);
        JLabel label = new JLabel ("> Double-click pathway to retrieve.");
        label.setForeground(Color.BLUE);
        Font font = label.getFont();
        Font newFont = new Font(font.getFamily(), Font.PLAIN, font.getSize()-2);
        label.setFont(newFont);
        pathwayPane.add(label, BorderLayout.SOUTH);
        return pathwayPane;
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
        try {
            NetworkWrapper mergeNetwork = null;
            NetworkMergeUtil mergeUtil = new NetworkMergeUtil();
            if (mergeUtil.mergeNetworksExist()) {
                mergeNetwork = mergeUtil.promptForNetworkToMerge();
                if (mergeNetwork == null) {
                    return;
                }
            }

            long internalId = pathwayTableModel.getInternalId(rows[0]);
            String title = pathwayTableModel.getValueAt(rows[0], 0)
                    + " (" + pathwayTableModel.getValueAt(rows[0], 1) + ")";
            long ids[] = new long[1];
            ids[0] = internalId;

            CPathWebService webApi = CPathWebServiceImpl.getInstance();
            ExecuteGetRecordByCPathId task;

            CPathResponseFormat format;
            CPathProperties config = CPathProperties.getInstance();
            if (config.getDownloadMode() == CPathProperties.DOWNLOAD_FULL_BIOPAX) {
                format = CPathResponseFormat.BIOPAX;
            } else {
                format = CPathResponseFormat.BINARY_SIF;
            }

            if (mergeNetwork != null && mergeNetwork.getNetwork() != null) {
                task = new ExecuteGetRecordByCPathId(webApi, ids, format,
                        title, mergeNetwork.getNetwork());
            } else {
                task = new ExecuteGetRecordByCPathId(webApi, ids, format, title);
            }

            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(true);
            jTaskConfig.displayCancelButton(true);
            TaskManager.executeTask(task, jTaskConfig);
        } catch (IndexOutOfBoundsException e) {
            //  Ignore
        }
    }
}

