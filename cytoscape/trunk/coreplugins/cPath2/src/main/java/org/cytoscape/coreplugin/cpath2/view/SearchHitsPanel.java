package org.cytoscape.coreplugin.cpath2.view;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.*;
import cytoscape.view.CytoscapeDesktop;

import org.cytoscape.coreplugin.cpath2.task.SelectPhysicalEntity;
import org.cytoscape.coreplugin.cpath2.view.model.PathwayTableModel;
import org.cytoscape.coreplugin.cpath2.view.model.InteractionBundleModel;
import org.cytoscape.coreplugin.cpath2.view.model.RecordList;
import org.cytoscape.coreplugin.cpath2.view.model.ExtendedRecordWrapper;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebServiceListener;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.SearchResponseType;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.ExtendedRecordType;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.SummaryResponseType;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import java.util.List;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * Search Hits Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchHitsPanel extends JPanel implements CPathWebServiceListener, CytoPanelListener {
    private DefaultListModel peListModel;
    private JList peList;
    private SearchResponseType peSearchResponse;
    private Document summaryDocument;
    private String currentKeyword;
    private InteractionBundleModel interactionBundleModel;
    private PathwayTableModel pathwayTableModel;
    private JTextPane summaryTextPane;
    private PhysicalEntityDetailsPanel peDetailsPanel;
	private JLayeredPane appLayeredPane;
    private HashMap <Long, RecordList> parentRecordsMap;
	private CytoPanelState cytoPanelState;
    private JFrame detailsFrame;

    /**
     * Constructor.
     * @param interactionBundleModel    Interaction Table Model.
     * @param pathwayTableModel         Pathway Table Model.
     * @param webApi                    cPath Web API.
     */
    public SearchHitsPanel(InteractionBundleModel interactionBundleModel, PathwayTableModel
            pathwayTableModel, CPathWebService webApi) {
        this.interactionBundleModel = interactionBundleModel;
        this.pathwayTableModel = pathwayTableModel;
		appLayeredPane = Cytoscape.getDesktop().getRootPane().getLayeredPane();
        webApi.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //  Create the Summary Panel, but don't show it yet
        peDetailsPanel = new PhysicalEntityDetailsPanel(this);
        summaryDocument = peDetailsPanel.getDocument();
        summaryTextPane = peDetailsPanel.getTextPane();

		// create popup window
//		modalPanel = new ModalPanel();
//		popup = new PopupPanel(appLayeredPane, peDetailsPanel, modalPanel);
//		appLayeredPane.add(modalPanel, 1000);
//		appLayeredPane.add(popup, 1000);

        //  Create the Hit List
        peListModel = new DefaultListModel();
        peList = createHitJList(peListModel);

        JPanel hitListPane = new JPanel();
        hitListPane.setLayout(new BorderLayout());
        JScrollPane hitListScrollPane = new JScrollPane(peList);
        hitListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        GradientHeader header = new GradientHeader("Step 2:  Select");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        hitListPane.add(header, BorderLayout.NORTH);
        JSplitPane internalPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hitListScrollPane,
                peDetailsPanel);
        internalPanel.setDividerLocation(100);
        hitListPane.add(internalPanel, BorderLayout.CENTER);

        //  Create Search Details Panel
        SearchDetailsPanel detailsPanel = new SearchDetailsPanel(interactionBundleModel,
                pathwayTableModel);

        //  Create the Split Pane
        JSplitPane splitPane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT, hitListPane,
                detailsPanel);
        splitPane.setDividerLocation(200);
        splitPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(splitPane);
        createListener(interactionBundleModel, pathwayTableModel, summaryTextPane);

		// listener for cytopanel events
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		cytoPanel.addCytoPanelListener(this);
		cytoPanelState = cytoPanel.getState();
    }

    private JList createHitJList(DefaultListModel peListModel) {
        JList peList = new JListWithToolTips(peListModel);
        peList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peList.setPrototypeCellValue("12345678901234567890");
        return peList;
    }

    /**
     * Indicates that user has initiated a phsyical entity search.
     *
     * @param keyword        Keyword.
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     */
    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId) {
        this.currentKeyword = keyword;
    }

    /**
     * Indicates that a search for physical entities has just completed.
     *
     * @param peSearchResponse PhysicalEntitySearchResponse Object.
     */
    public void searchCompletedForPhysicalEntities(final SearchResponseType peSearchResponse) {

        if (peSearchResponse.getTotalNumHits() > 0) {

            //  Reset parent summary map
            parentRecordsMap = new HashMap<Long, RecordList>();
            
            //  store for later reference
            this.peSearchResponse = peSearchResponse;

            //  Populate the hit list
            List<ExtendedRecordType> searchHits = peSearchResponse.getSearchHit();
            peListModel.setSize(searchHits.size());
            int i = 0;
            for (ExtendedRecordType searchHit : searchHits) {
                ExtendedRecordWrapper wrapper = new ExtendedRecordWrapper (searchHit);
                peListModel.setElementAt(wrapper, i++);
            }
        } else {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    Window window = SwingUtilities.getWindowAncestor(SearchHitsPanel.this);
                    JOptionPane.showMessageDialog(window, "No matches found for:  "
                            + currentKeyword + ".  Please try again.", "Search Results",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    public void requestInitiatedForParentSummaries(long primaryId) {
        //  Currently no-op
    }

    public void requestCompletedForParentSummaries(long primaryId,
            SummaryResponseType summaryResponse) {
        //  Store parent summaries for later reference

        RecordList recordList = new RecordList(summaryResponse);
        parentRecordsMap.put(primaryId, recordList);

        //  If we have just received parent summaries for the first search hit, select it.
        if (peSearchResponse != null) {
            List <ExtendedRecordType> searchHits = peSearchResponse.getSearchHit();
            if (searchHits.size() > 0) {
                ExtendedRecordType searchHit = searchHits.get(0);
                if (primaryId == searchHit.getPrimaryId()) {
                    peList.setSelectedIndex(0);
                    SelectPhysicalEntity selectTask = new SelectPhysicalEntity(parentRecordsMap);
                    selectTask.selectPhysicalEntity(peSearchResponse, 0,
                            interactionBundleModel, pathwayTableModel, summaryDocument,
                                                    summaryTextPane, appLayeredPane);
                }
            }
        }
    }

    /**
     * Listen for list selection events.
     *
     * @param interactionBundleModel InteractionBundleModel.
     * @param pathwayTableModel     PathwayTableModel.
     */
    private void createListener(final InteractionBundleModel interactionBundleModel,
            final PathwayTableModel pathwayTableModel, final JTextPane textPane) {
        peList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = peList.getSelectedIndex();
                //  Ignore the "unselect" event.
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (selectedIndex >=0) {
                        SelectPhysicalEntity selectTask = new SelectPhysicalEntity(parentRecordsMap);
                        selectTask.selectPhysicalEntity(peSearchResponse, selectedIndex,
                                interactionBundleModel, pathwayTableModel, summaryDocument,
                                textPane, appLayeredPane);
                    }
                }
            }
        });
    }

	//
	// cytopanel listener implementation
	//

	/**
	 * Notifies the listener on a change in the CytoPanel state.
	 *
	 * @param newState The new CytoPanel state - see CytoPanelState class.
	 */
	public void onStateChange(CytoPanelState newState) {
		cytoPanelState = newState;
	}

	/**
	 * Notifies the listener when a new component on the CytoPanel is selected.
	 *
	 * @param componentIndex The index of the component selected.
	 */
	public void onComponentSelected(int componentIndex) {}

	/**
	 * Notifies the listener when a component is added to the CytoPanel.
	 *
	 * @param count The number of components on the CytoPanel after the add.
	 */
	public void onComponentAdded(int count) {}

	/**
	 * Notifies the listener when a component is removed from the CytoPanel.
	 *
	 * @param count The number of components on the CytoPanel after the remove.
	 */
	public void onComponentRemoved(int count) {}
}