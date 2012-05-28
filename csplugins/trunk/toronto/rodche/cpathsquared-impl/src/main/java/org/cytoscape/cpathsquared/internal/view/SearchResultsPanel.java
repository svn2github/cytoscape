package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Properties;
import org.cytoscape.cpathsquared.internal.CPath2;
import org.cytoscape.cpathsquared.internal.CPath2Listener;
import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTask;
import org.cytoscape.work.TaskFactory;

import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;


public class SearchResultsPanel extends JPanel implements CPath2Listener 
{
    private JList resList;
    private JList ppwList;
    private JList molList;
    
//    private HashMap <String, Map<String,String>> memberDetailsMap; //TODO map: search hits - participant/component names, xrefs, entity ref's ids, etc.
    private String currentKeyword;
    
    private final ResultsModel resultsModel;
    
    private JTextPane summaryTextPane;
    private DetailsPanel detailsPanel;
    private JScrollPane ppwListScrollPane;
    private JScrollPane molListScrollPane;
	private JLayeredPane appLayeredPane;


	public SearchResultsPanel() 
    {
        resultsModel = new ResultsModel();
        CySwingApplication application = CPath2Factory.getCySwingApplication();
		appLayeredPane = application.getJFrame().getRootPane().getLayeredPane();
        // add this search/get events listener to the api
		CPath2.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //  Create Info Panel (the first tab)
        detailsPanel = CPath2Factory.createDetailsPanel(this);
        summaryTextPane = detailsPanel.getTextPane();
        
        //create parent pathways panel (the second tab)
        ppwList = new ToolTipsSearchHitsJList(new DefaultListModel());
        ppwList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ppwList.setPrototypeCellValue("12345678901234567890");
        JPanel ppwListPane = new JPanel();
        ppwListPane.setLayout(new BorderLayout());
        ppwListScrollPane = new JScrollPane(ppwList);
        ppwListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        ppwListPane.add(ppwListScrollPane, BorderLayout.CENTER);
        
        //create participants (the third tab is about Entity References, Complexes, and Genes...)
        molList = new ToolTipsSearchHitsJList(new DefaultListModel());
        molList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        molList.setPrototypeCellValue("12345678901234567890");
        JPanel molListPane = new JPanel();
        molListPane.setLayout(new BorderLayout());
        molListScrollPane = new JScrollPane(molList);
        molListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        molListPane.add(molListScrollPane, BorderLayout.CENTER);
        
        // make tabs
        JTabbedPane southPane = new JTabbedPane(); 
        southPane.add("Summary", detailsPanel);
        southPane.add("Parent Pathways", ppwListPane);
        southPane.add("Molecules", molListPane);
  
        // search hits list
        resList = new ToolTipsSearchHitsJList(new DefaultListModel());
        resList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resList.setPrototypeCellValue("12345678901234567890");
        JPanel hitListPane = new JPanel();
        hitListPane.setLayout(new BorderLayout());
        JScrollPane hitListScrollPane = new JScrollPane(resList);
        hitListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        GradientHeader header = new GradientHeader("Hits");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        hitListPane.add(header, BorderLayout.NORTH);
        
        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hitListScrollPane, southPane);
        vSplit.setDividerLocation(200);
        hitListPane.add(vSplit, BorderLayout.CENTER);
        
        //  Create search results extra filtering panel
        SearchResultsFilterPanel filterPanel = 
        	(SearchResultsFilterPanel) CPath2Factory.createSearchResultsFilterPanel(resultsModel, resList);

        //  Create the Split Pane
        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, hitListPane);
        hSplit.setDividerLocation(200);
        hSplit.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(hSplit);
        
        createSelectListener();
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
            resultsModel.setSearchResponse(searchResponse);
            updateHitsList();
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

    
    /**
     * Updates the hits list after new search or filtering.
     * 
     * @param searchResponse
     */
	private final void updateHitsList() {
		// init/reset the hits list
		DefaultListModel listModel = (DefaultListModel) resList.getModel();
		listModel.clear();
		List<SearchHit> searchHits = resultsModel.getSearchResponse().getSearchHit();
		listModel.setSize(searchHits.size());
		int i = 0;
		for (SearchHit searchHit : searchHits) {
			listModel.setElementAt(searchHit, i++);
		}
	}
    

    private final void createSelectListener() 
    {
        resList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = resList.getSelectedIndex();
                //  Ignore the "unselect" event.
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (selectedIndex >=0) {
                    	selectHit((SearchHit)resList.getModel().getElementAt(selectedIndex));
                    }
                }
            }
        });
    }

    
	private void selectHit(SearchHit item) {
		if (item == null) {
			return;
		}

		// get/create and show hit's summary
		String summary = resultsModel.summaryMap.get(item.getUri());
		if (summary == null) {
			StringBuilder html = new StringBuilder();
			html.append("<html>");

			if (item.getName() != null)
				html.append("<h2>" + item.getName() + "</h2>");
			html.append("<h3>Class: " + item.getBiopaxClass() + 
				"</h3><h3>URI: " + item.getUri() + "</h3>");

			List<String> items = item.getOrganism();
			if (items != null && !items.isEmpty()) {
				html.append("<H3>Organisms:<br/>"
					+ StringUtils.join(items, "<br/>") + "</H3>");
			}

			items = item.getPathway();
			if (items != null && !items.isEmpty()) {
				html.append("<H3>Pathway URIs:<br/>"
						+ StringUtils.join(items, "<br/>") + "</H3>");
			}

			items = item.getDataSource();
			if (items != null && !items.isEmpty()) {
				html.append("<H3>Data sources:<br/>"
						+ StringUtils.join(items, "<br/>") + "</H3>");
			}

			String primeExcerpt = item.getExcerpt();
			if (primeExcerpt != null) {
				html.append("<H4>Matched in</H4>");
				html.append("<span class='excerpt'>" + primeExcerpt
						+ "</span><BR>");
			}

			// TODO add more details here

			html.append("</html>");
			summary = html.toString();
			resultsModel.summaryMap.put(item.getUri(), summary);
		}

		summaryTextPane.setText(summary);
		summaryTextPane.setCaretPosition(0);
		appLayeredPane.repaint();

		
		
		// TODO update pathways list
		
		

		// TODO update mol. list

	}
	
	
    /**
     * TODO assign to a button or link
     * 
     * Downloads a single pathway in a new thread.
     * @param rows             Selected row.
     * @param pathwayModel     Pathway Model.
     */
    private final void downloadPathway(int[] rows, DefaultTableModel model) {
    	if(rows.length < 1) {
    		return;
    	}
    	
        int i= rows[0];
    	SearchHit hit = (SearchHit) model.getDataVector().get(i);
        String internalId = hit.getUri();
        String title = model.getValueAt(i, 0)
        	+ " (" + model.getValueAt(i, 1) + ")";

        OutputFormat format;
        //TODO add EXTENDED_BINARY_SIF
        if (CPath2Properties.downloadMode == CPath2Properties.DOWNLOAD_BIOPAX) {
        	format = OutputFormat.BIOPAX;
        } else {
            format = OutputFormat.BINARY_SIF;
        }

        TaskFactory taskFactory = CPath2Factory.newTaskFactory(new ExecuteGetRecordByCPathIdTask(
        	new String[]{internalId}, format, title));
        CPath2Factory.getTaskManager().execute(taskFactory.createTaskIterator());
    }
    
    
    static class ToolTipsSearchHitsJList extends JList {

        public ToolTipsSearchHitsJList(ListModel listModel) {
            super(listModel);
        }

        @Override
    	public String getToolTipText(MouseEvent mouseEvent) {
    		int index = locationToIndex(mouseEvent.getPoint());
    		if (-1 < index) {
    			SearchHit record = (SearchHit) getModel().getElementAt(index);
    			StringBuilder html = new StringBuilder();
    			html.append("<html><table cellpadding=10><tr><td>");
    			html.append("<B>").append(record.getBiopaxClass());
    			if(!record.getDataSource().isEmpty())
    				html.append("&nbsp;").append(record.getDataSource().toString());
    			if(!record.getOrganism().isEmpty())
    				html.append("&nbsp;").append(record.getOrganism().toString());
    			html.append("</B>&nbsp;");
    			html.append("</td></tr></table></html>");
    			return html.toString();
    		} else {
    			return null;
    		}
    	}

    }
    
    static class ToolTipsNameValuePairJList extends JList {

        public ToolTipsNameValuePairJList(ListModel listModel) {
            super(listModel);
        }

        @Override
    	public String getToolTipText(MouseEvent mouseEvent) {
    		int index = locationToIndex(mouseEvent.getPoint());
    		if (-1 < index) {
    			StringBuilder html = new StringBuilder();
    			html.append("<html><table cellpadding=10><tr><td>");
    			html.append("<B>").append("TODO");
    			html.append("</B>&nbsp;");
    			html.append("</td></tr></table></html>");
    			return html.toString();
    		} else {
    			return null;
    		}
    	}

    }
}