package org.mskcc.pathway_commons.view;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.ArrayList;

import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;
import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySearchResponse;
import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySummary;
import org.mskcc.pathway_commons.web_service.model.PathwaySummary;
import org.mskcc.pathway_commons.web_service.model.InteractionBundleSummary;
import org.mskcc.pathway_commons.task.ExecutePhysicalEntitySearch;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * Main GUI Panel for Searching Pathway Commons.
 *
 * @author Ethan Cerami.
 */
public class PathwayCommonsSearchPanel extends JPanel implements PathwayCommonsWebApiListener {
    protected Document summaryDocument;
    protected Document matchingExcerptsDocument;
    protected DefaultTableModel interactionTableModel;
    protected DefaultTableModel pathwayTableModel;
    protected DefaultListModel peListModel;
    protected Document searchDocument;
    protected ComboBoxModel organismComboBoxModel;
    protected PathwayCommonsWebApi webApi;
    private JTable pathwayTable;
    private JButton searchButton;
    private PhysicalEntitySearchResponse peSearchResponse;
    private JList peList;

    /**
     * Constructor.
     */
    public PathwayCommonsSearchPanel(PathwayCommonsWebApi webApi) {
        this.webApi = webApi;

        //  Set JGoodies Theme
        setLookAndFeel();

        //  Create main Border Layout
        this.setLayout(new BorderLayout());

        //  Create North Panel:  Search Box
        JPanel searchBoxPanel = createSearchBoxPanel();
        this.add(searchBoxPanel, BorderLayout.NORTH);

        //  Create Center Panel:  Search Results
        JSplitPane splitPane = createSearchResultsPanel();
        this.add(splitPane, BorderLayout.CENTER);

        //  Create Souther Panel:  Download
        JPanel downloadPanel = createDownloadPanel();
        this.add(downloadPanel, BorderLayout.SOUTH);

        webApi.addApiListener(this);
    }

    public void searchInitiatedForPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex) {
    }

    public void searchCompletedForPhysicalEntities(PhysicalEntitySearchResponse peSearchResponse) {
        this.peSearchResponse = peSearchResponse;
        ArrayList<PhysicalEntitySummary> peSummaryList =
                peSearchResponse.getPhysicalEntitySummartList();
        peListModel.setSize(peSummaryList.size());
        int i = 0;
        for (PhysicalEntitySummary peSummary: peSummaryList) {
            String name = peSummary.getName();
            peListModel.setElementAt(name, i++);
        }
        if (peSummaryList.size() > 0) {
            peList.setSelectedIndex(0);
        }
    }

    /**
     * Initialize the Focus.  Can only be called after component has been packed and displayed.
     */
    public void initFocus() {
        searchButton.requestFocusInWindow();
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
     * Creates the Search Results Panel.
     * @return JSplitPane Object.
     */
    private JSplitPane createSearchResultsPanel() {

        //  Create the Search Hits Panel
        JPanel hitListPanel = createHitListPanel();

        //  Create the Search Details Panel
        JPanel detailsPanel = createSearchDetailsPanel();

        //  Create the split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            hitListPanel, detailsPanel);
        return splitPane;
    }

    /**
     * Creates the Search Details Panel.
     * @return JPanel Object.
     */
    private JPanel createSearchDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        GridLayout gridLayout = new GridLayout (2,0);
        detailsPanel.setLayout(gridLayout);

        JTextArea matchingExcerptsTextArea = createTextArea();
        matchingExcerptsDocument = matchingExcerptsTextArea.getDocument();

        //JScrollPane matchingExcerptsPane = encloseInJScrollPane("Matching Excerpts",
        //        matchingExcerptsTextArea);
        JScrollPane interactionPane = createInteractionBundleTable();
        JScrollPane pathwayPane = createPathwayTable();
        detailsPanel.add(interactionPane);
        detailsPanel.add(pathwayPane);
        //detailsPanel.add(summaryPane);
        //detailsPanel.add(matchingExcerptsPane);
        return detailsPanel;
    }

    /**
     * Creates the Search Hits List Panel.
     * @return JScrollPane Object.
     */
    private JPanel createHitListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextArea summaryTextArea = createTextArea();
        summaryDocument = summaryTextArea.getDocument();
        JScrollPane summaryPane = encloseInJScrollPane("Summary", summaryTextArea);

        peListModel = new DefaultListModel();

        peList = new JList(peListModel);
        peList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peList.setPrototypeCellValue("12345678901234567890");
        JScrollPane hitListPane = new JScrollPane(peList);
        hitListPane.setBorder(new TitledBorder("Search Results"));

        panel.add(hitListPane);
        panel.add(summaryPane);

        peList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedIndex = peList.getSelectedIndex();

                if (PathwayCommonsSearchPanel.this.peSearchResponse != null) {
                    ArrayList<PhysicalEntitySummary> peList =
                            PathwayCommonsSearchPanel.this.peSearchResponse.
                                    getPhysicalEntitySummartList();
                    PhysicalEntitySummary pe = peList.get(selectedIndex);
                    ArrayList <PathwaySummary> pathwayList = pe.getPathwayList();
                    ArrayList <InteractionBundleSummary> interactionList =
                            pe.getInteractionBundleList();

                    Vector dataVector = interactionTableModel.getDataVector();
                    dataVector.removeAllElements();

                    dataVector = pathwayTableModel.getDataVector();
                    dataVector.removeAllElements();

                    if (pathwayList != null) {
                        pathwayTableModel.setRowCount(pathwayList.size());
                        pathwayTableModel.setColumnCount(3);
                        for (int i=0; i<pathwayList.size(); i++) {
                            PathwaySummary pathway = pathwayList.get(i);
                            pathwayTableModel.setValueAt(pathway.getDataSourceName(), i, 0);
                            pathwayTableModel.setValueAt(pathway.getName(), i, 1);
                            pathwayTableModel.setValueAt(Boolean.FALSE, i, 2);
                        }
                    }

                    if (interactionList != null) {
                        interactionTableModel.setRowCount(interactionList.size());
                        interactionTableModel.setColumnCount(3);
                        for (int i=0; i<interactionList.size(); i++) {
                            InteractionBundleSummary interactionBundle = interactionList.get(i);
                            interactionTableModel.setValueAt
                                    (interactionBundle.getDataSourceName(), i, 0);
                            interactionTableModel.setValueAt
                                    (interactionBundle.getNumInteractions(), i, 1);
                            interactionTableModel.setValueAt(Boolean.FALSE, i, 2);
                        }
                    }
                }

            }
        });

        return panel;
    }

    /**
     * Creates the Search Box Panel.
     * @return JPanel Object.
     */
    private JPanel createSearchBoxPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Search Pathway Commons"));
        BoxLayout boxLayout = new BoxLayout (panel, BoxLayout.X_AXIS);
        panel.setLayout(boxLayout);
        final JTextField searchField  = new JTextField(20);
        searchField.setText("Enter Protein Name or ID");
        searchField.setMaximumSize(new Dimension(200, 9999));
        searchDocument = searchField.getDocument();
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent focusEvent) {
                if (searchField.getText() != null
                        && searchField.getText().startsWith("Enter")) {
                    searchField.setText("");
                }
            }
        });

        //  Organism List is currently hard-coded.
        Vector organismList = new Vector();
        organismList.add("Human");
        organismList.add("Mouse");

        organismComboBoxModel = new DefaultComboBoxModel(organismList);
        JComboBox organismComboBox = new JComboBox(organismComboBoxModel);
        organismComboBox.setMaximumSize(new Dimension(200, 9999));
        organismComboBox.setPrototypeDisplayValue("12345678901234567890");
        JButton helpButton = new JButton("Help");
        searchButton = createSearchButton(searchField);

        panel.add(searchField);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(organismComboBox);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(searchButton);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(helpButton);
        return panel;
    }

    private JButton createSearchButton(final JTextField searchField) {
        //  Search Button Action
        searchButton = new JButton("Go!");

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ExecutePhysicalEntitySearch search = new ExecutePhysicalEntitySearch
                        (webApi, searchField.getText(), -1, 1);
                JTaskConfig jTaskConfig = new JTaskConfig();
                jTaskConfig.setAutoDispose(true);
                jTaskConfig.displayCancelButton(false);
                jTaskConfig.displayCloseButton(false);
                //jTaskConfig.setOwner(Cytoscape.getDesktop());
                TaskManager.executeTask(search, jTaskConfig);
            }
        });
        return searchButton;
    }

    /**
     * Encloses the specified JTextArea in a JScrollPane.
     * @param title     Title of Area.
     * @param textArea  JTextArea Object.
     * @return JScrollPane Object.
     */
    private JScrollPane encloseInJScrollPane (String title, JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new TitledBorder(title));
        return scrollPane;
    }

    /**
     * Creates a JTextArea with correct line wrap settings.
     * @return JTextArea Object.
     */
    private JTextArea createTextArea () {
        String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
                + "Pellentesque et arcu tincidunt massa interdum convallis. Nullam "
                + "cursus elementum est. Aenean gravida massa vel odio. Duis felis "
                + "purus, lobortis vitae, nonummy vitae, hendrerit in, neque. Etiam "
                + "eget nisi ac massa tempor scelerisque. Duis vel nisl sed erat bibendum "
                + "interdum. Aliquam quis est. Vivamus lacus risus, tempus a, euismod "
                + "fermentum, semper in, ante. Aenean vulputate dui ac sem. Fusce ac "
                + "urna nec ipsum vulputate dictum. Aenean eget neque vitae pede "
                + "porttitor ultrices. Maecenas dapibus nibh ac leo. Aliquam bibendum "
                + "accumsan massa.\n"
                + "Sed sagittis turpis a velit. Nam pharetra vestibulum dolor. "
                + "Donec accumsan. Nullam interdum pede a metus. Aenean lorem leo, "
                + "aliquam eget, rutrum sed, elementum sit amet, magna. Vivamus aliquam"
                + "enim at mauris. Maecenas congue tempor dui. Nam sit amet pede sed "
                + "metus ullamcorper dignissim. Ut mollis odio vitae libero. Mauris "
                + "ultrices. Aenean dignissim, dui id fringilla aliquam, dolor mauris "
                + "tincidunt felis, id interdum nulla tortor vitae purus.";
        JTextArea textArea = new JTextArea (text);
        textArea.setColumns(20);
        textArea.setRows(10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    /**
     * Creats the Interaction Bundle Table.
     * @return JScrollPane Object.
     */
    private JScrollPane createInteractionBundleTable() {
        interactionTableModel = new InteractionTableModel();
        JTable table = new JTable(interactionTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new TitledBorder("Interactions"));
        return scrollPane;
    }

    /**
     * Creates the Pathway Table.
     * @return JScrollPane Object.
     */
    private JScrollPane createPathwayTable() {
        pathwayTableModel = new PathwayTableModel();
        pathwayTable = new JTable(pathwayTableModel);
        pathwayTable.setAutoCreateColumnsFromModel(true);
        pathwayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(pathwayTable);
        scrollPane.setBorder(new TitledBorder("Pathways"));
        return scrollPane;
    }

    /**
     * Creates the Download Panel.
     * @return JPanel Object.
     */
    private JPanel createDownloadPanel () {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(new TitledBorder ("Download"));
        JButton button = new JButton ("Download");

        Vector networkList = new Vector();
        networkList.add("Download all selected interactions / pathways to new network");
        networkList.add("Download and merge with:  BRCA1 Network");
        JComboBox networkComboBox = new JComboBox(networkList);
        networkComboBox.setMaximumSize(new Dimension(400, 9999));

        panel.add(Box.createHorizontalGlue());
        panel.add(networkComboBox);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(button);
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

/**
 * Interaction Table Model.
 */
class InteractionTableModel extends DefaultTableModel {

    public InteractionTableModel () {
        super();
        Vector columnNames = new Vector();
        columnNames.add("Data Source");
        columnNames.add("Num Interactions");
        columnNames.add("Select");
        this.setColumnIdentifiers(columnNames);
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 2) {
            return true;
        } else {
            return false;
        }
    }

    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 2) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }
}

/**
 * Pathway Table Model.
 *
 * @author Ethan Cerami
 */
class PathwayTableModel extends DefaultTableModel {

    public PathwayTableModel () {
        super ();
        Vector columnNames = new Vector();
        columnNames.add("Data Source");
        columnNames.add("Pathway");
        columnNames.add("Select");
        this.setColumnIdentifiers(columnNames);
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 2) {
            return true;
        } else {
            return false;
        }
    }

    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 2) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }
}