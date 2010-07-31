package org.cytoscape.coreplugin.cpath2.view;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import org.cytoscape.coreplugin.cpath2.filters.ChainedFilter;
import org.cytoscape.coreplugin.cpath2.filters.DataSourceFilter;
import org.cytoscape.coreplugin.cpath2.filters.EntityTypeFilter;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.BasicRecordType;
import org.cytoscape.coreplugin.cpath2.view.model.InteractionBundleModel;
import org.cytoscape.coreplugin.cpath2.view.model.RecordList;
import org.cytoscape.coreplugin.cpath2.view.tree.CheckNode;
import org.cytoscape.coreplugin.cpath2.view.tree.JTreeWithCheckNodes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/**
 * Interaction Bundle Panel.
 *
 * @author Ethan Cerami
 */
public class InteractionBundlePanel extends JPanel {
    private JLabel matchingInteractionsLabel;
    private InteractionBundleModel interactionBundleModel;
    private CheckNode dataSourceFilter;
    private CheckNode interactionTypeFilter;
    private JButton retrieveButton;
    private JTreeWithCheckNodes tree;
    private CollapsablePanel filterPanel;
    private JDialog dialog;
    private CyNetwork mergeNetwork;

    public InteractionBundlePanel(InteractionBundleModel
            interactionBundleModel, JDialog dialog) {
        this(interactionBundleModel);
        this.dialog = dialog;
    }

    public InteractionBundlePanel(InteractionBundleModel
            interactionBundleModel, CyNetwork mergeNetwork, JDialog dialog) {
        this(interactionBundleModel);
        this.dialog = dialog;
        this.mergeNetwork = mergeNetwork;
    }

    public InteractionBundlePanel(InteractionBundleModel
            interactionBundleModel) {
        this.interactionBundleModel = interactionBundleModel;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        matchingInteractionsLabel = new JLabel("Matching Interactions:  N/A");
        matchingInteractionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = matchingInteractionsLabel.getFont();
        Font newFont = new Font(font.getFamily(), Font.BOLD, font.getSize());
        matchingInteractionsLabel.setFont(newFont);
        matchingInteractionsLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        panel.add(matchingInteractionsLabel);

        final CheckNode rootNode = new CheckNode("All Filters");
        tree = new JTreeWithCheckNodes(rootNode);
        tree.setOpaque(false);

        filterPanel = new CollapsablePanel("Filters (Optional)");
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.getContentPane().add(tree);

        panel.add(filterPanel);
        addObserver(interactionBundleModel, matchingInteractionsLabel, tree, rootNode);

        JPanel footer = new JPanel();
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));
        createInteractionDownloadButton(footer);
        panel.add(footer);
        JScrollPane scrollPane = new JScrollPane(panel);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Expands all Nodes.
     */
    public void expandAllNodes() {
        filterPanel.setCollapsed(false);
        dataSourceFilter.setSelected(true);
        interactionTypeFilter.setSelected(true);
        TreePath path = new TreePath(dataSourceFilter.getPath());
        tree.expandPath(path);
        path = new TreePath(interactionTypeFilter.getPath());
        tree.expandPath(path);
    }

    private void addObserver(final InteractionBundleModel interactionBundleModel,
            final JLabel matchingInteractionsLabel, final JTreeWithCheckNodes tree,
            final CheckNode rootNode) {
        interactionBundleModel.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                RecordList recordList = interactionBundleModel.getRecordList();
                matchingInteractionsLabel.setText("Matching Interactions:  "
                        + recordList.getNumRecords());

                if (recordList.getNumRecords() == 0) {
                    filterPanel.setVisible(false);
                    retrieveButton.setVisible(false);
                } else {
                    filterPanel.setVisible(true);
                    retrieveButton.setVisible(true);
                }

                TreeMap<String, Integer> dataSourceMap = recordList.getDataSourceMap();
                TreeMap<String, Integer> entityTypeMap = recordList.getEntityTypeMap();

                //  Store current expansion states
                boolean dataSourceFilterExpanded = false;
                if (dataSourceFilter != null) {
                    TreePath path = new TreePath(dataSourceFilter.getPath());
                    dataSourceFilterExpanded = tree.isExpanded(path);
                }
                boolean interactionTypeFilterExpanded = false;
                if (interactionTypeFilter != null) {
                    TreePath path = new TreePath(interactionTypeFilter.getPath());
                    interactionTypeFilterExpanded = tree.isExpanded(path);
                }

                //  Remove all children
                rootNode.removeAllChildren();

                //  Create Data Source Filter
                if (dataSourceMap.size() > 0) {
                    dataSourceFilter = new CheckNode("Filter by Data Source");
                    rootNode.add(dataSourceFilter);
                    for (String key : dataSourceMap.keySet()) {
                        CategoryCount categoryCount = new CategoryCount(key,
                                dataSourceMap.get(key));
                        CheckNode dataSourceNode = new CheckNode(categoryCount, false, true);
                        dataSourceFilter.add(dataSourceNode);
                    }
                    dataSourceFilter.setSelected(true);
                }

                //  Create Entity Type Filter
                if (entityTypeMap.size() > 0) {
                    interactionTypeFilter = new CheckNode("Filter by Interaction Type");
                    rootNode.add(interactionTypeFilter);
                    for (String key : entityTypeMap.keySet()) {
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
                     *
                     * @param treeModelEvent Tree Model Event Object.
                     */
                    public void treeNodesChanged(TreeModelEvent treeModelEvent) {
                        java.util.List<BasicRecordType> passedRecordList = executeFilter();
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
                    TreePath path = new TreePath(dataSourceFilter.getPath());
                    tree.expandPath(path);
                }
                if (interactionTypeFilterExpanded) {
                    TreePath path = new TreePath(interactionTypeFilter.getPath());
                    tree.expandPath(path);
                }
            }
        });
    }

    private void createInteractionDownloadButton(JPanel footer) {
        retrieveButton = new JButton("Retrieve Interactions");
        retrieveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                List<BasicRecordType> passedRecordList = executeFilter();
                if (passedRecordList.size() == 0) {
                    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                            "Your current filter settings result in 0 matching interactions.  "
                                    + "\nPlease check your filter settings and try again.",
                            "No matches.", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    DownloadDetails detailsFrame = new DownloadDetails(passedRecordList,
                            interactionBundleModel.getPhysicalEntityName());
                    if (dialog != null) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    dialog.dispose();
                                }
                            });
                    }
                    if (mergeNetwork == null) {
                        detailsFrame.setVisible(true);
                    } else {
                        detailsFrame.downloadInteractions(mergeNetwork);
                    }
                }
            }
        });
        footer.add(retrieveButton);
    }

    private List<BasicRecordType> executeFilter() {
        Set<String> dataSourceSet = new HashSet<String>();
        Set<String> entityTypeSet = new HashSet<String>();
        int childCount = dataSourceFilter.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CheckNode checkNode = (CheckNode) dataSourceFilter.getChildAt(i);
            CategoryCount categoryCount = (CategoryCount) checkNode.getUserObject();
            String dataSource = categoryCount.getCategoryName();
            if (checkNode.isSelected()) {
                dataSourceSet.add(dataSource);
            }
        }
        childCount = interactionTypeFilter.getChildCount();
        for (int i = 0; i < childCount; i++) {
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
            passedRecordList = chainedFilter.filter(interactionBundleModel.getRecordList().
                    getSummaryResponse().getRecord());
        } catch (NullPointerException e) {
            passedRecordList = null;
        }
        return passedRecordList;
    }
}
