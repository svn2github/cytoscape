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

import org.cytoscape.coreplugin.cpath2.view.model.InteractionBundleModel;
import org.cytoscape.coreplugin.cpath2.view.model.PathwayTableModel;
import org.cytoscape.coreplugin.cpath2.view.model.RecordList;
import org.cytoscape.coreplugin.cpath2.view.tree.JTreeWithCheckNodes;
import org.cytoscape.coreplugin.cpath2.view.tree.CheckNode;
import org.cytoscape.coreplugin.cpath2.filters.ChainedFilter;
import org.cytoscape.coreplugin.cpath2.filters.DataSourceFilter;
import org.cytoscape.coreplugin.cpath2.filters.EntityTypeFilter;
import org.cytoscape.coreplugin.cpath2.task.ExecuteGetRecordByCPathId;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.BasicRecordType;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchDetailsPanel extends JPanel {
    private CheckNode dataSourceFilter;
    private CheckNode interactionTypeFilter;
    private InteractionBundleModel interactionBundleModel;
    private JLabel matchingInteractionsLabel;
    private JButton retrieveButton;

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
        JScrollPane interactionPanel = createInteractionBundlePanel(interactionBundleModel);
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
    private JScrollPane createInteractionBundlePanel(final InteractionBundleModel
            interactionBundleModel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        matchingInteractionsLabel = new JLabel ("Matching Interactions:  N/A");
        matchingInteractionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = matchingInteractionsLabel.getFont();
        Font newFont = new Font (font.getFamily(), Font.BOLD,  font.getSize());
        matchingInteractionsLabel.setFont(newFont);
        matchingInteractionsLabel.setBorder(new EmptyBorder(5,10,5,5));
        panel.add(matchingInteractionsLabel);

        final CheckNode rootNode = new CheckNode("All Filters");
        final JTreeWithCheckNodes tree = new JTreeWithCheckNodes(rootNode);
        tree.setOpaque(false);

        CollapsablePanel filterPanel = new CollapsablePanel("Filters (Optional)");
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.getContentPane().add(tree);

        panel.add(filterPanel);
        addObserver(interactionBundleModel, matchingInteractionsLabel, tree, rootNode);

        JPanel footer = new JPanel();
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));
        createInteractionDownloadButton(footer);
        panel.add(footer);
        JScrollPane scrollPane = new JScrollPane (panel);
        return scrollPane;
    }

    private void addObserver(final InteractionBundleModel interactionBundleModel,
            final JLabel matchingInteractionsLabel, final JTreeWithCheckNodes tree,
            final CheckNode rootNode) {
        interactionBundleModel.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                RecordList recordList = interactionBundleModel.getRecordList();
                matchingInteractionsLabel.setText("Matching Interactions:  "
                        + recordList.getNumRecords());

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
                        CategoryCount categoryCount = new CategoryCount(key,
                                dataSourceMap.get(key));
                        CheckNode dataSourceNode = new CheckNode(categoryCount, false, true);
                        dataSourceFilter.add(dataSourceNode);
                    }
                    dataSourceFilter.setSelected(true);
                }

                //  Create Entity Type Filter
                if (entityTypeMap.size() > 0) {
                    interactionTypeFilter = new CheckNode ("Filter by Interaction Type");
                    rootNode.add(interactionTypeFilter);
                    for (String key:  entityTypeMap.keySet()) {
                        CategoryCount categoryCount = new CategoryCount(key,
                                entityTypeMap.get(key));
                        CheckNode dataSourceNode = new CheckNode(categoryCount, false, true);
                        interactionTypeFilter.add(dataSourceNode);
                    }
                }
                DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
                tree.setModel(treeModel);
                treeModel.addTreeModelListener(new TreeModelListener() {

                    /**
                     * Respond to user check node selections.
                     * @param treeModelEvent Tree Model Event Object.
                     */
                    public void treeNodesChanged(TreeModelEvent treeModelEvent) {
                        List<BasicRecordType> passedRecordList =  executeFilter();
                        if (passedRecordList != null) {
                            matchingInteractionsLabel.setText("Matching Interactions:  "
                                + passedRecordList.size());
                            if (passedRecordList.size() > 0) {
                                retrieveButton.setEnabled(true);
                            } else {
                                retrieveButton.setEnabled(false);
                            }
                        }
                    }

                    public void treeNodesInserted(TreeModelEvent treeModelEvent) {
                        //  no-op
                    }

                    public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
                        //  no-op
                    }

                    public void treeStructureChanged(TreeModelEvent treeModelEvent) {
                        //  no-op
                    }
                });

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
    }

    private void createInteractionDownloadButton(JPanel footer) {
        retrieveButton = new JButton ("Retrieve Interactions");
        retrieveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                List<BasicRecordType> passedRecordList =  executeFilter();
                    if (passedRecordList.size() == 0) {
                        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                "Your current filter settings result in 0 matching interactions.  "
                                + "\nPlease check your filter settings and try again.",
                                "No matches.", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        DownloadDetails detailsFrame = new DownloadDetails(passedRecordList,
                                interactionBundleModel.getPhysicalEntityName());
                        detailsFrame.setVisible(true);
                    }
            }
        });
        footer.add(retrieveButton);
    }

    private List<BasicRecordType> executeFilter() {
        Set <String> dataSourceSet = new HashSet<String>();
        Set <String> entityTypeSet = new HashSet<String>();
        int childCount = dataSourceFilter.getChildCount();
        for (int i=0; i< childCount; i++) {
            CheckNode checkNode = (CheckNode) dataSourceFilter.getChildAt(i);
            CategoryCount categoryCount = (CategoryCount) checkNode.getUserObject();
            String dataSource = categoryCount.getCategoryName();
            if (checkNode.isSelected()) {
                dataSourceSet.add(dataSource);
            }
        }
        childCount = interactionTypeFilter.getChildCount();
        for (int i=0; i< childCount; i++) {
            CheckNode checkNode = (CheckNode) interactionTypeFilter.getChildAt(i);
            CategoryCount categoryCount = (CategoryCount) checkNode.getUserObject();
            String entityType = categoryCount.getCategoryName();
            if (checkNode.isSelected()) {
                entityTypeSet.add(entityType);
            }
        }
        ChainedFilter chainedFilter = new ChainedFilter();
        DataSourceFilter dataSourceFilter = new DataSourceFilter(dataSourceSet);
        EntityTypeFilter entityTypeFilter = new EntityTypeFilter(entityTypeSet);
        chainedFilter.addFilter(dataSourceFilter);
        chainedFilter.addFilter(entityTypeFilter);
        List<BasicRecordType> passedRecordList;
        try {
            passedRecordList =  chainedFilter.filter (interactionBundleModel.getRecordList().
                    getSummaryResponse().getRecord());
        } catch (NullPointerException e) {
            passedRecordList = null;
        }
        return passedRecordList;
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
            long internalId = pathwayTableModel.getInternalId(rows[0]);
            String title = pathwayTableModel.getValueAt(rows[0], 0)
                    + " (" + pathwayTableModel.getValueAt(rows[0], 1) + ")";
            long ids[] = new long[1];
            ids[0] = internalId;

            CPathWebService webApi = CPathWebService.getInstance();
            ExecuteGetRecordByCPathId task = new ExecuteGetRecordByCPathId(webApi, ids, title);
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

