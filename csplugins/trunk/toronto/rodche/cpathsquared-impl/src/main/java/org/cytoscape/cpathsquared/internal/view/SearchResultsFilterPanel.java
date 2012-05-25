package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.filters.ChainedFilter;
import org.cytoscape.cpathsquared.internal.filters.DataSourceFilter;
import org.cytoscape.cpathsquared.internal.filters.EntityTypeFilter;
import org.cytoscape.cpathsquared.internal.filters.OrganismFilter;

import cpath.service.jaxb.SearchHit;


public class SearchResultsFilterPanel extends JPanel {
    private JLabel matchingItemsLabel;
    private ResultsModel model;
    private CheckNode typeFilter;
    private CheckNode dataSourceFilter;
    private CheckNode organismFilter;
    private JTreeWithCheckNodes tree;
    private CollapsablePanel filterTreePanel;
    private JButton applyButton;
	
	public SearchResultsFilterPanel(ResultsModel resultsModel) {
        this.model = resultsModel;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        matchingItemsLabel = new JLabel("Matching entities:  N/A");
        matchingItemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = matchingItemsLabel.getFont();
        Font newFont = new Font(font.getFamily(), Font.BOLD, font.getSize());
        matchingItemsLabel.setFont(newFont);
        matchingItemsLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        add(matchingItemsLabel);

        final CheckNode rootNode = new CheckNode("All Filters");
        tree = new JTreeWithCheckNodes(rootNode);
        tree.setOpaque(false);

        filterTreePanel = new CollapsablePanel("BioPAX Filters");
        filterTreePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterTreePanel.getContentPane().add(tree);

        JScrollPane scrollPane = new JScrollPane(filterTreePanel);
        add(scrollPane);
        
        addObserver(resultsModel, matchingItemsLabel, tree, rootNode);
        
        createApplyFiltersButton();
    }

    /**
     * Expands all Nodes.
     */
    public void expandAllNodes() {
        filterTreePanel.setCollapsed(false);
        
        typeFilter.setSelected(true);
        TreePath path = new TreePath(typeFilter.getPath());
        tree.expandPath(path);
        
        dataSourceFilter.setSelected(true);
        path = new TreePath(dataSourceFilter.getPath());
        tree.expandPath(path);
        
        organismFilter.setSelected(true);
        path = new TreePath(organismFilter.getPath());
        tree.expandPath(path);
    }

    private void addObserver(final ResultsModel model,
            final JLabel matchingHitsLabel, final JTreeWithCheckNodes tree,
            final CheckNode rootNode) {
        model.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                RecordList recordList = model.getRecordList();
                matchingHitsLabel.setText("Matching entities:  "
                        + recordList.getNumRecords());

                if (recordList.getNumRecords() == 0) {
                    filterTreePanel.setVisible(false);
                    applyButton.setVisible(false);
                } else {
                    filterTreePanel.setVisible(true);
                    applyButton.setVisible(true);
                }
                
                
                Map<String, Integer> typeMap = recordList.getTypeMap();
                Map<String, Integer> organismMap = recordList.getOrganismMap();
                Map<String, Integer> dataSourceMap = recordList.getDataSourceMap();

                //  Store current expansion states
                boolean typeFilterExpanded = false;
                if (typeFilter != null) {
                    TreePath path = new TreePath(typeFilter.getPath());
                    typeFilterExpanded = tree.isExpanded(path);
                }
                boolean dataSourceFilterExpanded = false;
                if (dataSourceFilter != null) {
                    TreePath path = new TreePath(dataSourceFilter.getPath());
                    dataSourceFilterExpanded = tree.isExpanded(path);
                }
                boolean organismFilterExpanded = false;
                if (organismFilter != null) {
                    TreePath path = new TreePath(organismFilter.getPath());
                    organismFilterExpanded = tree.isExpanded(path);
                }

                //  Remove all children
                rootNode.removeAllChildren();

                // Create Filters
                if (typeMap.size() > 0) {
                    typeFilter = new CheckNode("by BioPAX Type");
                    rootNode.add(typeFilter);
                    for (String key : typeMap.keySet()) {
                        CategoryCount categoryCount = new CategoryCount(key, typeMap.get(key));
                        CheckNode typeNode = new CheckNode(categoryCount, false, true);
                        typeFilter.add(typeNode);
                    }
                }
                if (organismMap.size() > 0) {
                    organismFilter = new CheckNode("by Organism");
                    rootNode.add(organismFilter);
                    for (String key : organismMap.keySet()) {
                        CategoryCount categoryCount = new CategoryCount(key, organismMap.get(key));
                        CheckNode organismNode = new CheckNode(categoryCount, false, true);
                        organismFilter.add(organismNode);
                    }
                }
                if (dataSourceMap.size() > 0) {
                	dataSourceFilter = new CheckNode("by Datasource");
                    rootNode.add(dataSourceFilter);
                    for (String key : dataSourceMap.keySet()) {
                        CategoryCount categoryCount = new CategoryCount(key, dataSourceMap.get(key));
                        CheckNode dataSourceNode = new CheckNode(categoryCount, false, true);
                        dataSourceFilter.add(dataSourceNode);
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
                            matchingHitsLabel.setText("Matching entities:  "
                                    + passedRecordList.size());
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
                if (typeFilterExpanded) {
                    TreePath path = new TreePath(typeFilter.getPath());
                    tree.expandPath(path);
                }
                if (organismFilterExpanded) {
                    TreePath path = new TreePath(organismFilter.getPath());
                    tree.expandPath(path);
                }
                if (dataSourceFilterExpanded) {
                    TreePath path = new TreePath(dataSourceFilter.getPath());
                    tree.expandPath(path);
                }
            }
        });
    }

    private List<SearchHit> executeFilter() {
        Set<String> entityTypeSet = new HashSet<String>();
        Set<String> entityOrganismSet = new HashSet<String>();
        Set<String> entityDataSourceSet = new HashSet<String>();
        
		if (typeFilter != null) {
			int childCount = typeFilter.getChildCount();
			for (int i = 0; i < childCount; i++) {
				CheckNode checkNode = (CheckNode) typeFilter.getChildAt(i);
				CategoryCount categoryCount = (CategoryCount) checkNode.getUserObject();
				String entityType = categoryCount.getCategoryName();
				if (checkNode.isSelected()) {
					entityTypeSet.add(entityType);
				}
			}
		}
		
		if (organismFilter != null) {
			int childCount = organismFilter.getChildCount();
			for (int i = 0; i < childCount; i++) {
				CheckNode checkNode = (CheckNode) organismFilter.getChildAt(i);
				CategoryCount categoryCount = (CategoryCount) checkNode.getUserObject();
				String entityType = categoryCount.getCategoryName();
				if (checkNode.isSelected()) {
					entityOrganismSet.add(entityType);
				}
			}
		}
		
		if (dataSourceFilter != null) {
			int childCount = dataSourceFilter.getChildCount();
			for (int i = 0; i < childCount; i++) {
				CheckNode checkNode = (CheckNode) dataSourceFilter.getChildAt(i);
				CategoryCount categoryCount = (CategoryCount) checkNode.getUserObject();
				String entityType = categoryCount.getCategoryName();
				if (checkNode.isSelected()) {
					entityDataSourceSet.add(entityType);
				}
			}
		}
		
		
        ChainedFilter chainedFilter = new ChainedFilter();
        EntityTypeFilter entityTypeFilter = new EntityTypeFilter(entityTypeSet);
        chainedFilter.addFilter(entityTypeFilter);
        DataSourceFilter dataSourceFilter = new DataSourceFilter(entityDataSourceSet);
        chainedFilter.addFilter(dataSourceFilter);
        OrganismFilter organismFilter = new OrganismFilter(entityOrganismSet);
        chainedFilter.addFilter(organismFilter);
        
        List<SearchHit> passedRecordList;
        passedRecordList = chainedFilter.filter(model.getRecordList().getHits());
        
        return passedRecordList;
    }
    
    //TODO must apply to the hits list!
    private final void createApplyFiltersButton() {
        applyButton = new JButton("Apply Filter");
        applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
                List<SearchHit> passedRecordList = executeFilter();
                if (passedRecordList.size() == 0) {
                    JOptionPane.showMessageDialog(CPath2Factory.getCySwingApplication().getJFrame(),
                            "Your current filter settings result in 0 matching entities.  "
                           + "\nPlease check your filter settings and try again.",
                            "No matches.", JOptionPane.INFORMATION_MESSAGE);
                } else {
//                    DownloadDetails detailsFrame = factory.createDownloadDetails(passedRecordList);
//                    if (dialog != null) {
//                            SwingUtilities.invokeLater(new Runnable() {
//                                public void run() {
//                                    dialog.dispose();
//                                }
//                            });
//                    }
//                    detailsFrame.setVisible(true);
	
                }
            }
        });
        
        add(applyButton);
    }
}
