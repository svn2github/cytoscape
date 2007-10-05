package org.mskcc.pathway_commons.view;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.view.model.PathwayTableModel;
import org.mskcc.pathway_commons.view.model.InteractionTableModel;

/**
 * Main GUI Panel for Searching Pathway Commons.
 *
 * @author Ethan Cerami.
 */
public class PathwayCommonsSearchPanel extends JPanel {
    protected DefaultTableModel interactionTableModel;
    protected DefaultTableModel pathwayTableModel;
    protected PathwayCommonsWebApi webApi;
    private JPanel searchBoxPanel;

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

        //  Set JGoodies Theme
        setLookAndFeel();

        //  Create main Border Layout
        setLayout(new BorderLayout());

        //  Create North Panel:  Search Box
        searchBoxPanel = new SearchBoxPanel (webApi);
        add(searchBoxPanel, BorderLayout.NORTH);

        //  Create Center Panel:  Search Results
        JSplitPane splitPane = createSearchResultsPanel();
        add(splitPane, BorderLayout.CENTER);

        //  Create Southern Panel:  Download
        JPanel downloadPanel = createDownloadPanel();
        this.add(downloadPanel, BorderLayout.SOUTH);
    }

    /**
     * Initialize the Focus.  Can only be called after component has been packed and displayed.
     */
    public void initFocus() {
        searchBoxPanel.requestFocusInWindow();
    }

    /**
     * Sets the appropriate Look and Feel.
     */
    private void setLookAndFeel() {
        PlasticLookAndFeel.setPlasticTheme(new SkyBlue());
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception e) {}
    }

    /**
     * Creates the Search Results Split Pane.
     * @return JSplitPane Object.
     */
    private JSplitPane createSearchResultsPanel() {

        //  Create the Search Hits Panel
        JPanel hitListPanel = new SearchHitsPanel(this.interactionTableModel,
                this.pathwayTableModel, webApi);

        //  Create the Search Details Panel
        JPanel detailsPanel = new SearchDetailsPanel(this.interactionTableModel,
                this.pathwayTableModel);

        //  Create the split pane
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, hitListPanel, detailsPanel);
    }

    /**
     * Creates the Download Panel.
     * @return JPanel Object.
     */
    private JPanel createDownloadPanel () {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(new TitledBorder ("Download Options"));
        //JButton button = new JButton ("Download");

        Vector networkList = new Vector();
        networkList.add("Download all selected interactions / pathways to new network");
        networkList.add("Download and merge with:  BRCA1 Network");
        JComboBox networkComboBox = new JComboBox(networkList);
        networkComboBox.setMaximumSize(new Dimension(400, 9999));

        panel.add(Box.createHorizontalGlue());
        panel.add(networkComboBox);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        //panel.add(button);
        return panel;
    }

    /**
     * Main Method.  Used for debugging purposes only.
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