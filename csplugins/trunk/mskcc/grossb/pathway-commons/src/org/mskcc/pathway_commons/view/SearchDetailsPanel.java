package org.mskcc.pathway_commons.view;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

import org.mskcc.pathway_commons.web_service.CPathProtocol;
import org.mskcc.pathway_commons.view.model.InteractionBundleModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;
import org.mskcc.pathway_commons.view.model.RecordList;
import org.mskcc.pathway_commons.view.tree.CheckNode;
import org.mskcc.pathway_commons.view.tree.JTreeWithCheckNodes;
import org.mskcc.pathway_commons.util.NetworkUtil;
import org.mskcc.pathway_commons.filters.ChainedFilter;
import org.mskcc.pathway_commons.filters.DataSourceFilter;
import org.mskcc.pathway_commons.filters.EntityTypeFilter;
import org.mskcc.pathway_commons.schemas.summary_response.RecordType;
import cytoscape.Cytoscape;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchDetailsPanel extends JPanel {
    private CheckNode dataSourceFilter;
    private CheckNode interactionTypeFilter;
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

        JPanel footer = new JPanel();
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));
        createInteractionDownloadButton(footer);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private void createInteractionDownloadButton(JPanel footer) {
        JButton button = new JButton ("Download Interactions");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
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
                try {
                    List<RecordType> passedRecordList =  chainedFilter.filter
                            (interactionBundleModel.getRecordList().
                            getSummaryResponse().getRecord());
                    if (passedRecordList.size() == 0) {
                        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                "Your current filter settings result in 0 matching interactions.  "
                                + "\nPlease check your filter settings and try again.",
                                "No matches.", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JFrame frame = new JFrame();
                        Container contentPane = frame.getContentPane();
                        contentPane.setLayout(new BorderLayout());
                        GradientHeader header = new GradientHeader ("I will now fetch the following "
                                + passedRecordList.size()
                                + " records:  ");
                        contentPane.add(header, BorderLayout.NORTH);
                        StringBuffer buf = new StringBuffer();
                        for (RecordType type:  passedRecordList) {
                            buf.append (type.getPrimaryId() + ":  " + type.getName() + "\n");
                        }
                        JTextArea textArea = new JTextArea();
                        textArea.setRows(10);
                        textArea.setColumns(50);
                        textArea.setEditable(false);
                        textArea.setText(buf.toString());
                        JScrollPane scrollPane = new JScrollPane (textArea);
                        scrollPane.setBorder (new EmptyBorder (5,5,5,5));
                        contentPane.add(scrollPane, BorderLayout.CENTER);
                        frame.pack();
                        frame.setLocationRelativeTo(Cytoscape.getDesktop());
                        frame.setVisible(true);
                    }
                } catch (NullPointerException e) {
                }
            }
        });
        footer.add(button);
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

class CategoryCount {
    private String categoryName;
    private int count;

    public CategoryCount (String categoryName, int count) {
        this.categoryName = categoryName;
        this.count = count;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCount() {
        return count;
    }

    public String toString() {
        return categoryName + ":  " + count;
    }
}