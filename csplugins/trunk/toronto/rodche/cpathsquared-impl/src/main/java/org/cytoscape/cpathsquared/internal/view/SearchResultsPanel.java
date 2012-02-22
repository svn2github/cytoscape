package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.util.HashMap;
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
import javax.swing.text.Document;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.application.swing.events.CytoPanelStateChangedEvent;
import org.cytoscape.application.swing.events.CytoPanelStateChangedListener;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.cpathsquared.internal.CPath2WebServiceListener;

import cpath.client.CPath2Client;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;


public class SearchResultsPanel extends JPanel 
implements CPath2WebServiceListener, CytoPanelStateChangedListener 
{
    private DefaultListModel memberListModel;
    private JList resList;
    private SearchResponse searchResponse;
    private Document summaryDocument;
    private String currentKeyword;
    private ResultsModel resultsModel;
    private JTextPane summaryTextPane;
    private DetailsPanel detailsPanel;
	private JLayeredPane appLayeredPane;
    private HashMap <String, RecordList> parentRecordsMap;
	private CytoPanelState cytoPanelState;
    private JFrame detailsFrame;
	private final CPath2Factory factory;


	public SearchResultsPanel(CPath2WebService webApi, CPath2Factory factory) 
    {
    	this.factory = factory;
        this.resultsModel = new ResultsModel();
        CySwingApplication application = factory.getCySwingApplication();
		appLayeredPane = application.getJFrame().getRootPane().getLayeredPane();
        webApi.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //  Create the Summary Panel, but don't show it yet
        detailsPanel = factory.createDetailsPanel(this);
        summaryDocument = detailsPanel.getDocument();
        summaryTextPane = detailsPanel.getTextPane();

        //  Create the members (previously it was 'hits') List
        memberListModel = new DefaultListModel();
        resList = new JListWithToolTips(memberListModel);
        resList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resList.setPrototypeCellValue("12345678901234567890");

        JPanel hitListPane = new JPanel();
        hitListPane.setLayout(new BorderLayout());
        JScrollPane hitListScrollPane = new JScrollPane(resList);
        hitListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        GradientHeader header = new GradientHeader("Members");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        hitListPane.add(header, BorderLayout.NORTH);
        JSplitPane internalPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hitListScrollPane, detailsPanel);
        internalPanel.setDividerLocation(100);
        hitListPane.add(internalPanel, BorderLayout.CENTER);
        
        //  Create search results/networks panel
        JPanel networksPanel = new JPanel();
        networksPanel.setLayout(new BorderLayout());
        networksPanel.add( new GradientHeader("Select"), BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel hitsPanel = factory.createSearchHitsPanel(resultsModel);
        JPanel pathwayPanel = factory.createTopPathwaysPanel(CPath2Client.newInstance().getTopPathways(), webApi);
        Font font = tabbedPane.getFont();
        tabbedPane.setFont(new Font(font.getFamily(), Font.PLAIN, font.getSize()-2));
        tabbedPane.add("Search Results", hitsPanel);
        tabbedPane.add("Top Pathways", pathwayPanel);
        //TODO add more tabs: "networks by organism", "networks by datasource"
        networksPanel.add(tabbedPane, BorderLayout.CENTER);

        //  Create the Split Pane
        JSplitPane splitPane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT, 
        	hitListPane, networksPanel);
        splitPane.setDividerLocation(200);
        splitPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(splitPane);

        createListener(resultsModel);
        
		// listener for cytopanel events
		CytoPanel cytoPanel = application.getCytoPanel(CytoPanelName.EAST);
		cytoPanelState = cytoPanel.getState();
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

            //  Reset parent summary map
            parentRecordsMap = new HashMap<String, RecordList>();
            
            //  store for later reference
            this.searchResponse = searchResponse;
            
            resultsModel.setRecordList(new RecordList(searchResponse));

            //  Populate the hit list
            List<SearchHit> searchHits = searchResponse.getSearchHit();
            memberListModel.setSize(searchHits.size());
            int i = 0;
            for (SearchHit searchHit : searchHits) {
                memberListModel.setElementAt(searchHit, i++);
            }
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


    private final void createListener(final ResultsModel results) 
    {
        resList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = resList.getSelectedIndex();
                //  Ignore the "unselect" event.
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (selectedIndex >=0) {
                        SelectMemberEntity selectTask = new SelectMemberEntity();
                        selectTask.selectItem(selectedIndex, results.getRecordList(),
                                summaryDocument, summaryTextPane, appLayeredPane);
                    }
                }
            }
        });
    }

    
	@Override
	public void handleEvent(CytoPanelStateChangedEvent e) {
		cytoPanelState = e.getNewState();
	}
}