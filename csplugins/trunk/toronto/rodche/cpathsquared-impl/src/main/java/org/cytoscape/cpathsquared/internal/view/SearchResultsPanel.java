package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.application.swing.events.CytoPanelStateChangedEvent;
import org.cytoscape.application.swing.events.CytoPanelStateChangedListener;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Properties;
import org.cytoscape.cpathsquared.internal.CPath2;
import org.cytoscape.cpathsquared.internal.CPath2Listener;
import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTask;
import org.cytoscape.work.TaskFactory;

import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;


public class SearchResultsPanel extends JPanel 
implements CPath2Listener, CytoPanelStateChangedListener 
{
    private JList resList;
    private JList ppwList;
    private JList molList;
//    private HashMap <String, Map<String,String>> memberDetailsMap; //TODO map: search hits - participant/component names, xrefs, entity ref's ids, etc.
    private Document summaryDocument;
    private String currentKeyword;
    private ResultsModel resultsModel;
    private JTextPane summaryTextPane;
    private DetailsPanel detailsPanel;
    private JScrollPane ppwListScrollPane;
    private JScrollPane molListScrollPane;
	private JLayeredPane appLayeredPane;
//	private CytoPanelState cytoPanelState;


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
        summaryDocument = detailsPanel.getDocument();
        summaryTextPane = detailsPanel.getTextPane();
        
        //create parent pathways panel (the second tab)
        ppwList = new JListWithToolTips(new DefaultListModel());
        ppwList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ppwList.setPrototypeCellValue("12345678901234567890");
        JPanel ppwListPane = new JPanel();
        ppwListPane.setLayout(new BorderLayout());
        ppwListScrollPane = new JScrollPane(ppwList);
        ppwListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        ppwListPane.add(ppwListScrollPane, BorderLayout.CENTER);
        
        //create participants (the third tab is about Entity References, Complexes, and Genes...)
        molList = new JListWithToolTips(new DefaultListModel());
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
        resList = new JListWithToolTips(new DefaultListModel());
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
        JPanel filterPanel = CPath2Factory.createSearchResultsFilterPanel(resultsModel);

        //  Create the Split Pane
        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, hitListPane);
        hSplit.setDividerLocation(200);
        hSplit.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(hSplit);
        
        createSelectListener();
                   
//		// listener for cytopanel events
//		CytoPanel cytoPanel = application.getCytoPanel(CytoPanelName.EAST);
//		cytoPanelState = cytoPanel.getState();
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
               
            // set the all-results model - used for hits list filtering
            resultsModel.setRecordList(new RecordList(searchResponse));

            //  init/reset the hits list
            updateHitsList(searchResponse);
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
	public final void updateHitsList(final SearchResponse searchResponse) {
		// init/reset the hits list
		DefaultListModel listModel = (DefaultListModel) resList.getModel();
		listModel.clear();

		List<SearchHit> searchHits = searchResponse.getSearchHit();
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
                    	(new SelectEntity()).selectItem(
                        		(SearchHit)resList.getModel().getElementAt(selectedIndex),
                                summaryDocument, summaryTextPane, appLayeredPane);
                    }
                }
            }
        });
    }

    
	@Override
	public void handleEvent(CytoPanelStateChangedEvent e) {
//		cytoPanelState = e.getNewState();
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
    
}