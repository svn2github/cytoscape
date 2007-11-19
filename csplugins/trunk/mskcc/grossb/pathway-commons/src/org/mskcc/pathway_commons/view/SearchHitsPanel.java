package org.mskcc.pathway_commons.view;

import org.mskcc.pathway_commons.schemas.search_response.ExtendedRecordType;
import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;
import org.mskcc.pathway_commons.schemas.summary_response.SummaryResponseType;
import org.mskcc.pathway_commons.task.SelectPhysicalEntity;
import org.mskcc.pathway_commons.web_service.cPathWebApi;
import org.mskcc.pathway_commons.web_service.cPathWebApiListener;
import org.mskcc.pathway_commons.view.model.InteractionBundleModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;
import org.mskcc.pathway_commons.view.model.RecordList;
import cytoscape.Cytoscape;

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
public class SearchHitsPanel extends JPanel implements cPathWebApiListener {
    private DefaultListModel peListModel;
    private JList peList;
    private SearchResponseType peSearchResponse;
    private Document summaryDocument;
    private String currentKeyword;
    private InteractionBundleModel interactionBundleModel;
    private PathwayTableModel pathwayTableModel;
    private JTextPane summaryTextPane;
    private PhysicalEntityDetailsPanel detailsPanel;
	private JLayeredPane appLayeredPane;
    private JButton detailsButton;
	private PopupPanel popup;
    private HashMap <Long, RecordList> parentRecordsMap;

    /**
     * Constructor.
     * @param interactionBundleModel    Interaction Table Model.
     * @param pathwayTableModel         Pathway Table Model.
     * @param webApi                    cPath Web API.
     */
    public SearchHitsPanel(InteractionBundleModel interactionBundleModel, PathwayTableModel
            pathwayTableModel, cPathWebApi webApi) {
        this.interactionBundleModel = interactionBundleModel;
        this.pathwayTableModel = pathwayTableModel;
		appLayeredPane = Cytoscape.getDesktop().getRootPane().getLayeredPane();
        webApi.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //  Create the Summary Panel, but don't show it yet
        detailsPanel = new PhysicalEntityDetailsPanel(this);
        summaryDocument = detailsPanel.getDocument();
        summaryTextPane = detailsPanel.getTextPane();

		// create popup window
		popup = new PopupPanel(appLayeredPane, detailsPanel);
		appLayeredPane.add(popup, new Integer(appLayeredPane.getIndexOf(this) + 1));

        //  Create the Hit List
        peListModel = new DefaultListModel();
        peList = createHitJList(peListModel);
        JScrollPane hitListPane = new JScrollPane(peList);

        detailsButton = createDetailsButton();
        GradientHeader header = new GradientHeader("Step 2:  Select Gene", detailsButton);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(header);

        //  Create Search Details Panel
        SearchDetailsPanel detailsPanel = new SearchDetailsPanel(interactionBundleModel,
                pathwayTableModel);

        //  Create the Split Pane
        JSplitPane splitPane = new JSplitPane (JSplitPane.VERTICAL_SPLIT, hitListPane,
                detailsPanel);
        splitPane.setDividerLocation(60);
        splitPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(splitPane);
        createListener(interactionBundleModel, pathwayTableModel, summaryTextPane);
    }

    private JButton createDetailsButton() {
        URL url = GradientHeader.class.getResource ("resources/stock_zoom-16.png");
        ImageIcon detailsIcon = new ImageIcon(url);
        JButton button = new JButton (detailsIcon);
        button.setToolTipText("View Gene Details");
        button.setOpaque(false);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                togglePopup();
            }
        });

        return button;
    }

    private JList createHitJList(DefaultListModel peListModel) {
        JList peList = new JList(peListModel);
        peList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peList.setPrototypeCellValue("12345678901234567890");
        return peList;
    }

	/**
	 * Our implementation of setBounds().  Required to affect
	 * popup window position and/or transition effect if this 
	 * panel's bounds change.
	 */
	public void setBounds(int x, int y, int width, int height) {

		if (getX() != x || getY() != y || getWidth() != width || getHeight() != height) {
			if (popup.isVisible()) {
				popup.cancelTransition();
			}
		}
		super.setBounds(x,y,width, height);
	}

	public void togglePopup() {
        if (!popup.isVisible()) {
            detailsButton.setToolTipText("Hide Gene Details");

			int MARGIN = 30;
			int popupWIDTH = (int)this.getSize().getWidth();
			int popupHEIGHT = (int)(this.getSize().getHeight() * .75);
			int desktopLocationX = Cytoscape.getDesktop().getLocationOnScreen().x;
			int desktopLocationY = Cytoscape.getDesktop().getLocationOnScreen().y;
			int desktopInsets = Cytoscape.getDesktop().getInsets().top + Cytoscape.getDesktop().getInsets().bottom;
			int popupX = getLocationOnScreen().x - desktopLocationX - popupWIDTH - MARGIN;
			int popupY = getLocationOnScreen().y - desktopLocationY;
			popup.setBounds(popupX, popupY, popupWIDTH, popupHEIGHT);
            popup.setCurtain(popupX+desktopLocationX, popupY+desktopLocationY+desktopInsets, popupWIDTH, popupHEIGHT);
			popup.fadeIn();
        } else {
			detailsButton.setToolTipText("View Gene Details");
			popup.fadeOut();
        }
	}

    /**
     * Indicates that user has initiated a phsyical entity search.
     *
     * @param keyword        Keyword.
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     * @param startIndex     Start Index.
     */
    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex) {
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
                String name = searchHit.getName();
                peListModel.setElementAt(name, i++);
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
}