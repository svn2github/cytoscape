package org.mskcc.pathway_commons.view;

import org.mskcc.pathway_commons.schemas.search_response.SearchHitType;
import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;
import org.mskcc.pathway_commons.task.SelectPhysicalEntity;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;
import org.mskcc.pathway_commons.view.model.InteractionTableModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

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
import java.net.URL;

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
    private PhysicalEntityDetailsPanel detailsPanel;
	private JLayeredPane appLayeredPane;
    private JButton detailsButton;

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
        detailsPanel = new PhysicalEntityDetailsPanel();
        summaryDocument = detailsPanel.getDocument();
        summaryTextPane = detailsPanel.getTextPane();

		// create popup window
		appLayeredPane.add(detailsPanel, appLayeredPane.getIndexOf(this) + 1);

        //  Create the Hit List
        peListModel = new DefaultListModel();
        peList = createHitJList(peListModel);
        JScrollPane hitListPane = new JScrollPane(peList);

        detailsButton = createDetailsButton();
        GradientHeader header = new GradientHeader("Step 2:  Select Gene", detailsButton);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(header);

        //  Create Search Details Panel
        SearchDetailsPanel detailsPanel = new SearchDetailsPanel(interactionTableModel,
                pathwayTableModel);

        //  Create the Split Pane
        JSplitPane splitPane = new JSplitPane (JSplitPane.VERTICAL_SPLIT, hitListPane,
                detailsPanel);
        splitPane.setDividerLocation(200);
        splitPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(splitPane);
        createListener(interactionTableModel, pathwayTableModel, summaryTextPane);
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

	private void togglePopup() {
        if (!detailsPanel.isVisible()) {
            int MARGIN = 30;
			int popupWIDTH = (int)this.getSize().getWidth();
			int popupHEIGHT = (int)(this.getSize().getHeight() * .50);
			int desktopLocationX = Cytoscape.getDesktop().getLocationOnScreen().x;
			int desktopLocationY = Cytoscape.getDesktop().getLocationOnScreen().y;
			int popupX = getLocationOnScreen().x - desktopLocationX - popupWIDTH - MARGIN;
			int popupY = getLocationOnScreen().y - desktopLocationY;
			detailsPanel.setBounds(popupX, popupY, popupWIDTH, popupHEIGHT);
            detailsPanel.setVisible(true);
            detailsButton.setToolTipText("Hide Gene Details");

            Animator animator = new Animator (300);
            animator.addTarget(new PropertySetter(detailsPanel, "alpha", 0.0f, 1.0f));
            animator.start();
        } else {
            detailsPanel.setVisible(false);
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
                        interactionTableModel, pathwayTableModel, summaryDocument,
                            textPane, appLayeredPane);
                }
            }
        });
    }
}