package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.filters.ChainedFilter;
import org.cytoscape.cpathsquared.internal.filters.EntityTypeFilter;

import cpath.service.jaxb.SearchHit;


public class SearchHitsPanel extends JPanel {
    private JLabel matchingItemsLabel;
    private ResultsModel model;
    private CheckNode typeFilter;
    private JButton retrieveButton;
    private JTreeWithCheckNodes tree;
    private CollapsablePanel filterPanel;
    private JDialog dialog;
	private final CPath2Factory factory;

    public SearchHitsPanel(ResultsModel
            interactionBundleModel, JDialog dialog, CPath2Factory factory) {
        this(interactionBundleModel, factory);
        this.dialog = dialog;
    }

    public SearchHitsPanel(ResultsModel
            interactionBundleModel, CPath2Factory factory) {
        this.factory = factory;
        this.model = interactionBundleModel;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        matchingItemsLabel = new JLabel("Matching Interactions:  N/A");
        matchingItemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = matchingItemsLabel.getFont();
        Font newFont = new Font(font.getFamily(), Font.BOLD, font.getSize());
        matchingItemsLabel.setFont(newFont);
        matchingItemsLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        panel.add(matchingItemsLabel);

        final CheckNode rootNode = new CheckNode("All Filters");
        tree = new JTreeWithCheckNodes(rootNode);
        tree.setOpaque(false);

        filterPanel = new CollapsablePanel("Filters (Optional)");
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.getContentPane().add(tree);

        panel.add(filterPanel);
        addObserver(interactionBundleModel, matchingItemsLabel, tree, rootNode);

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
        typeFilter.setSelected(true);
        TreePath path = new TreePath(typeFilter.getPath());
        tree.expandPath(path);
    }

    private void addObserver(final ResultsModel model,
            final JLabel matchingInteractionsLabel, final JTreeWithCheckNodes tree,
            final CheckNode rootNode) {
        model.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                RecordList recordList = model.getRecordList();
                matchingInteractionsLabel.setText("Matching Interactions:  "
                        + recordList.getNumRecords());

                if (recordList.getNumRecords() == 0) {
                    filterPanel.setVisible(false);
                    retrieveButton.setVisible(false);
                } else {
                    filterPanel.setVisible(true);
                    retrieveButton.setVisible(true);
                }

                TreeMap<String, Integer> entityTypeMap = recordList.getEntityTypeMap();

                //  Store current expansion states
                boolean interactionTypeFilterExpanded = false;
                if (typeFilter != null) {
                    TreePath path = new TreePath(typeFilter.getPath());
                    interactionTypeFilterExpanded = tree.isExpanded(path);
                }

                //  Remove all children
                rootNode.removeAllChildren();

                 //  Create Entity Type Filter
                if (entityTypeMap.size() > 0) {
                    typeFilter = new CheckNode("Filter by Interaction Type");
                    rootNode.add(typeFilter);
                    for (String key : entityTypeMap.keySet()) {
                        CategoryCount categoryCount = new CategoryCount(key,
                                entityTypeMap.get(key));
                        CheckNode dataSourceNode = new CheckNode(categoryCount, false, true);
                        typeFilter.add(dataSourceNode);
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
                        java.util.List<SearchHit> passedRecordList = executeFilter();
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
                if (interactionTypeFilterExpanded) {
                    TreePath path = new TreePath(typeFilter.getPath());
                    tree.expandPath(path);
                }
            }
        });
    }

    private void createInteractionDownloadButton(JPanel footer) {
        retrieveButton = new JButton("Retrieve Interactions");
        retrieveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
                List<SearchHit> passedRecordList = executeFilter();
                if (passedRecordList.size() == 0) {
                    JOptionPane.showMessageDialog(factory.getCySwingApplication().getJFrame(),
                            "Your current filter settings result in 0 matching interactions.  "
                                    + "\nPlease check your filter settings and try again.",
                            "No matches.", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    DownloadDetails detailsFrame = factory.createDownloadDetails(passedRecordList);
                    if (dialog != null) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    dialog.dispose();
                                }
                            });
                    }
                    detailsFrame.setVisible(true);
                }
            }
        });
        footer.add(retrieveButton);
    }

    private List<SearchHit> executeFilter() {
        Set<String> entityTypeSet = new HashSet<String>();
        
		if (typeFilter != null) {
			int childCount = typeFilter.getChildCount();
			for (int i = 0; i < childCount; i++) {
				CheckNode checkNode = (CheckNode) typeFilter
						.getChildAt(i);
				CategoryCount categoryCount = (CategoryCount) checkNode
						.getUserObject();
				String entityType = categoryCount.getCategoryName();
				if (checkNode.isSelected()) {
					entityTypeSet.add(entityType);
				}
			}
		}
        ChainedFilter chainedFilter = new ChainedFilter();
        EntityTypeFilter entityTypeFilter = new EntityTypeFilter(entityTypeSet);
        chainedFilter.addFilter(entityTypeFilter);
        List<SearchHit> passedRecordList;
        try {
            passedRecordList = chainedFilter.filter(model.getRecordList().getHits());
        } catch (NullPointerException e) {
            passedRecordList = null;
        }
        return passedRecordList;
    }
}
