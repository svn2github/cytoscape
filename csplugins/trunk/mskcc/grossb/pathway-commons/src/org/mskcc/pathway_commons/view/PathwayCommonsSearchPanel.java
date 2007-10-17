package org.mskcc.pathway_commons.view;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;
import org.mskcc.pathway_commons.view.model.InteractionTableModel;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;
import org.mskcc.pathway_commons.schemas.search_response.SearchResponseType;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Vector;

/**
 * Main GUI Panel for Searching Pathway Commons.
 *
 * @author Ethan Cerami.
 */
public class PathwayCommonsSearchPanel extends JPanel implements PathwayCommonsWebApiListener {
    protected InteractionTableModel interactionTableModel;
    protected PathwayTableModel pathwayTableModel;
    protected PathwayCommonsWebApi webApi;
    private JPanel searchBoxPanel;
    private JPanel searchHitsPanel = null;
    private JPanel cards;

    /**
     * Constructor.
     *
     * @param webApi PathwayCommons Web API.
     */
    public PathwayCommonsSearchPanel(PathwayCommonsWebApi webApi) {

        //  Store the web API model
        this.webApi = webApi;

        //  Create shared model classes
        interactionTableModel = new InteractionTableModel();
        pathwayTableModel = new PathwayTableModel();

        //  Create main Border Layout
        setLayout(new BorderLayout());

        //  Create North Panel:  Search Box
        searchBoxPanel = new SearchBoxPanel(webApi);
        add(searchBoxPanel, BorderLayout.NORTH);

        cards = new JPanel(new CardLayout());
        searchHitsPanel = createSearchResultsPanel();

        JPanel aboutPanel = createAboutPanel();
        cards.add (aboutPanel, "ABOUT");
        cards.add(searchHitsPanel, "HITS");
        add(cards, BorderLayout.CENTER);
        webApi.addApiListener(this);
    }

    private JPanel createAboutPanel() {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BorderLayout());
        TitledBorder border = GuiUtils.createTitledBorder("About");
        aboutPanel.setBorder(border);
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        Document doc = textPane.getDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, " is a convenient point of access to biological pathway "
                + "information collected from public pathway databases, which you can "
                + "browse or search. \n\nPathways include biochemical reactions, complex "
                + "assembly, transport and catalysis events, and physical interactions "
                + "involving proteins, DNA, RNA, small molecules and complexes.", attrs);
            StyleConstants.setBold(attrs, true);
            doc.insertString(0, "Pathway Commons", attrs);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        aboutPanel.add(textPane, BorderLayout.CENTER);
        return aboutPanel;
    }

    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex) {
    }

    public void searchCompletedForPhysicalEntities(SearchResponseType peSearchResponse) {
        if (!searchHitsPanel.isVisible()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    CardLayout cl = (CardLayout)(cards.getLayout());
                    cl.show(cards, "HITS");                }
            });
        }
    }

    /**
     * Initialize the Focus.  Can only be called after component has been
     * packed and displayed.
     */
    public void initFocus() {
        searchBoxPanel.requestFocusInWindow();
    }

    /**
     * Creates the Search Results Split Pane.
     *
     * @return JSplitPane Object.
     */
    private JPanel createSearchResultsPanel() {
        JPanel hitListPanel = new SearchHitsPanel(this.interactionTableModel,
                this.pathwayTableModel, webApi);
        return hitListPanel;
    }

    /**
     * Creates the Download Panel.
     *
     * @return JPanel Object.
     */
    private JPanel createDownloadPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(new TitledBorder("Download Options"));
        //JButton button = new JButton ("Download");

        Vector networkList = new Vector();
        networkList.add("Download all selected interactions / pathways to new network");
        networkList.add("Download and merge with:  BRCA1 Network");
        JComboBox networkComboBox = new JComboBox(networkList);
        networkComboBox.setMaximumSize(new Dimension(400, 9999));

        panel.add(Box.createHorizontalGlue());
        panel.add(networkComboBox);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        return panel;
    }

    /**
     * Main Method.  Used for debugging purposes only.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        PathwayCommonsSearchPanel form = new PathwayCommonsSearchPanel(
                new PathwayCommonsWebApi());
        frame.getContentPane().add(form);
        frame.pack();
        form.initFocus();
        frame.setVisible(true);
    }
}