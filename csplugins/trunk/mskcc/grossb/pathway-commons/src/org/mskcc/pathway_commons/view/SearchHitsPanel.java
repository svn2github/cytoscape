package org.mskcc.pathway_commons.view;

import org.mskcc.pathway_commons.schemas.search_response.SearchHitType;
import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;
import org.mskcc.pathway_commons.task.SelectPhysicalEntity;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;
import org.mskcc.pathway_commons.view.model.InteractionTableModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;

import cytoscape.Cytoscape;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Search Hits Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchHitsPanel extends JPanel implements PathwayCommonsWebApiListener {
    private DefaultListModel peListModel;
    private JList peList;
    private SearchResponseType peSearchResponse;
    private Document summaryDocument;
    private String currentKeyword;
    private InteractionTableModel interactionTableModel;
    private PathwayTableModel pathwayTableModel;
    private JTextPane summaryTextPane;
    private PhysicalEntityDetailsPanel summaryPanel;
	private PopupPanel popup;
	private JLayeredPane appLayeredPane;

    /**
     * Constructor.
     * @param interactionTableModel     Interaction Table Model.
     * @param pathwayTableModel         Pathway Table Model.
     * @param webApi                    Pathway Commons Web API.
     */
    public SearchHitsPanel(InteractionTableModel interactionTableModel, PathwayTableModel
            pathwayTableModel, PathwayCommonsWebApi webApi) {
        this.interactionTableModel = interactionTableModel;
        this.pathwayTableModel = pathwayTableModel;
		appLayeredPane = Cytoscape.getDesktop().getRootPane().getLayeredPane();
        webApi.addApiListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //  Create the Summary Panel, but don't show it yet
        summaryPanel = new PhysicalEntityDetailsPanel();
        summaryDocument = summaryPanel.getDocument();
        summaryTextPane = summaryPanel.getTextPane();

		// create popup windown
		popup = new PopupPanel(appLayeredPane, summaryPanel, Color.black);
		appLayeredPane.add(popup, appLayeredPane.getIndexOf(this) + 1);

        //  Create the Hit List
        peListModel = new DefaultListModel();
        peList = createHitJList(peListModel);
        JScrollPane hitListPane = new JScrollPane(peList);
        TitledBorder border = GuiUtils.createTitledBorder("Step 2:  Select Gene");
        hitListPane.setBorder(border);

        //  Create Search Details Panel
        SearchDetailsPanel detailsPanel = new SearchDetailsPanel(interactionTableModel,
                pathwayTableModel);

        //  Create the Split Pane
        JSplitPane splitPane = new JSplitPane (JSplitPane.VERTICAL_SPLIT, hitListPane,
                detailsPanel);
        splitPane.setDividerLocation(200);
        this.add(splitPane);
        createListener(interactionTableModel, pathwayTableModel, summaryTextPane);
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

    private JList createHitJList(DefaultListModel peListModel) {
        JList peList = new JList(peListModel);
        peList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peList.setPrototypeCellValue("12345678901234567890");

        peList.addMouseListener(new MouseAdapter() {

            /**
             * Mouse has entered the JList.
             * @param mouseEvent Mouse Event.
             */
            public void mouseEntered(MouseEvent mouseEvent) {
                createPopup();
            }

            /**
             * User pressed click within the JList.
             * @param mouseEvent Mouse Event.
             */
            public void mouseClicked(MouseEvent mouseEvent) {
                createPopup();
            }

            /**
             * Mouse has existed the JList.
             * @param mouseEvent Mouse Event.
             */
            public void mouseExited(MouseEvent mouseEvent) {
				if (popup.isVisible()) {
                    Timer timer = new Timer(1000, new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
							popup.fadeOut();
                        }
						});
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });


        return peList;
    }

	private void createPopup() {

		if (!popup.isVisible()) {
			int MARGIN = 30;
			int popupWIDTH = (int)this.getSize().getWidth();
			int popupHEIGHT = (int)(this.getSize().getHeight() * .75);
			int desktopLocationX = Cytoscape.getDesktop().getLocationOnScreen().x;
			int desktopLocationY = Cytoscape.getDesktop().getLocationOnScreen().y;
			int desktopInsets = Cytoscape.getDesktop().getInsets().top + Cytoscape.getDesktop().getInsets().bottom;
			int popupX = getLocationOnScreen().x - desktopLocationX - popupWIDTH - MARGIN;
			int popupY = getLocationOnScreen().y - desktopLocationY;
            popup.setCurtain(popupX+desktopLocationX, popupY+desktopLocationY+desktopInsets, popupWIDTH, popupHEIGHT);
			popup.setBounds(popupX, popupY, popupWIDTH, popupHEIGHT);
			popup.fadeIn();
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
            //  store for later reference
            this.peSearchResponse = peSearchResponse;

            //  Populate the hit list
            List<SearchHitType> searchHits = peSearchResponse.getSearchHit();
            peListModel.setSize(searchHits.size());
            int i = 0;
            for (SearchHitType searchHit : searchHits) {
                String name = searchHit.getName();
                peListModel.setElementAt(name, i++);
            }

            //  Select the first item in the list
            if (searchHits.size() > 0) {
                peList.setSelectedIndex(0);
                SelectPhysicalEntity selectTask = new SelectPhysicalEntity();
                selectTask.selectPhysicalEntity(peSearchResponse, 0,
                        interactionTableModel, pathwayTableModel, summaryDocument, 
												summaryTextPane, appLayeredPane);
            }
        } else {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    Window window = SwingUtilities.getWindowAncestor(SearchHitsPanel.this);
                    JOptionPane.showMessageDialog(window, "No matches found for:  "
                            + currentKeyword, "Search Results",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    /**
     * Listen for list selection events.
     *
     * @param interactionTableModel InteractionTableModel.
     * @param pathwayTableModel     PathwayTableModel.
     */
    private void createListener(final InteractionTableModel interactionTableModel,
            final PathwayTableModel pathwayTableModel, final JTextPane textPane) {
        peList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = peList.getSelectedIndex();
                if (selectedIndex >=0) {
                    SelectPhysicalEntity selectTask = new SelectPhysicalEntity();
                    selectTask.selectPhysicalEntity(peSearchResponse, selectedIndex,
													interactionTableModel, pathwayTableModel, summaryDocument, textPane, appLayeredPane);
                }
            }
        });
    }

    /**
     * Encloses the specified JTextPane in a JScrollPane.
     *
     * @param title    Title of Area.
     * @param textPane JTextPane Object.
     * @return JScrollPane Object.
     */
    private JScrollPane encloseInJScrollPane(String title, JTextPane textPane) {
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(new TitledBorder(title));
        return scrollPane;
    }

    /**
     * Creates a JTextArea with correct line wrap settings.
     *
     * @return JTextArea Object.
     */
    private JTextPane createTextArea() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        return textPane;
    }
}