package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Listener;
import org.cytoscape.cpathsquared.internal.view.GuiUtils.ToolTipsSearchHitsJList;

import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;
import cpath.service.jaxb.TraverseEntry;
import cpath.service.jaxb.TraverseResponse;


final class SearchResultsPanel extends JPanel implements CPath2Listener 
{
    private final SearchResultsFilterPanel filterPanel;
	
	private JList resList;
    private String currentKeyword;  
    private JTextPane summaryTextPane;
    private DetailsPanel detailsPanel;
    private JScrollPane ppwListScrollPane;

	public SearchResultsPanel() 
    {
		// add this search/get events listener to the api
		CPath2Factory.addApiListener(this);
		
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //  Create Info Panel (the first tab)
        detailsPanel = new DetailsPanel();
        summaryTextPane = detailsPanel.getTextPane();
        
        //create parent pathways panel (the second tab)
        final JList ppwList = new JList(new DefaultListModel());
        ppwList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ppwList.setPrototypeCellValue("12345678901234567890");
        JPanel ppwListPane = new JPanel();
        ppwListPane.setLayout(new BorderLayout());
        ppwListScrollPane = new JScrollPane(ppwList);
        ppwListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        ppwListPane.add(ppwListScrollPane, BorderLayout.CENTER);
                      
        // make (south) tabs
        JTabbedPane southPane = new JTabbedPane(); 
        southPane.add("Summary", detailsPanel);
        southPane.add("Parent Pathways", ppwListPane);
  
        // search hits list
        resList = new ToolTipsSearchHitsJList();
        resList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resList.setPrototypeCellValue("12345678901234567890");
        // define a list item selection listener which updates the details panel, etc..
        resList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
            	ToolTipsSearchHitsJList l = (ToolTipsSearchHitsJList) listSelectionEvent.getSource();
                int selectedIndex = l.getSelectedIndex();
                //  ignore the "unselect" event.
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (selectedIndex >=0) {
                    	SearchHit item = (SearchHit) l.getModel().getElementAt(selectedIndex);
                		// get/create and show hit's summary
                		String summary = GuiUtils.getSearchHitSummary(item);
                		summaryTextPane.setText(summary);
                		summaryTextPane.setCaretPosition(0);
                		
                		// TODO update pathways list in a Task...
                		DefaultListModel m = (DefaultListModel) ppwList.getModel();
						m.clear();
						if (!item.getPathway().isEmpty()) {
							TraverseResponse tres = CPath2Factory.traverse(
									"Named/displayName", item.getPathway());
							if (tres != null) 
								for (TraverseEntry e : tres.getTraverseEntry())
									m.addElement(e.getValue().get(0)); // there is at most one name per entry
						}
							
               			
//               			repaint();
                    }
                }
            }
        });
        
        JPanel hitListPane = new JPanel();
        hitListPane.setLayout(new BorderLayout());
        JScrollPane hitListScrollPane = new JScrollPane(resList);
        hitListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // make (north) tabs       
        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hitListScrollPane, southPane);
        vSplit.setDividerLocation(200);
        hitListPane.add(vSplit, BorderLayout.CENTER);
        
        //  Create search results extra filtering panel
        this.filterPanel = new SearchResultsFilterPanel(resList);

        //  Create the Split Pane
        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, hitListPane);
        hSplit.setDividerLocation(200);
        hSplit.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(hSplit);
        
    }

    /**
     * Indicates that user has initiated a phsyical entity search.
     *
     * @param keyword        Keyword.
     * @param organism NCBI Taxonomy ID.
     */
    public void searchInitiated(String keyword, Set<String> organism, Set<String> datasource) {
        this.currentKeyword = keyword;
    }

    /**
     * Indicates that a search for physical entities has just completed.
     *
     * @param searchResponse
     * 
     */
    public void searchCompleted(final SearchResponse searchResponse) {
        if (!searchResponse.isEmpty()) {
    		// init/reset the hits list
    		DefaultListModel listModel = (DefaultListModel) resList.getModel();
    		listModel.clear();
    		listModel.setSize(0);
    		List<SearchHit> searchHits = searchResponse.getSearchHit();
			if (searchHits.size() > 0) {
				for (SearchHit searchHit : searchHits) {
					listModel.addElement(searchHit);
				}
			}
            filterPanel.update(searchResponse);
            
        } else {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    Window window = SwingUtilities.getWindowAncestor(SearchResultsPanel.this);
                    JOptionPane.showMessageDialog(window, "No matches found for:  "
                        + currentKeyword + ".  Please try again.", "Search Results",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }
 
}