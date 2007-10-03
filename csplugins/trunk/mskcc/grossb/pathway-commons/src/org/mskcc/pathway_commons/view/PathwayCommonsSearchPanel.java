package org.mskcc.pathway_commons.view;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApiListener;
import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySearchResponse;

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
    private JButton searchButton;

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
        peListModel.removeAllElements();
        for (int i=0; i<interactionTableModel.getRowCount(); i++) {
            interactionTableModel.removeRow(i);
        }
        for (int i=0; i<pathwayTableModel.getRowCount(); i++) {
            pathwayTableModel.removeRow(i);
        }
        try {
            summaryDocument.remove(0, summaryDocument.getLength());
            matchingExcerptsDocument.remove(0, matchingExcerptsDocument.getLength());
        } catch (BadLocationException e) {
        }
    }

    public void searchCompletedForPhysicalEntities(PhysicalEntitySearchResponse peSearchResponse) {

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
        JScrollPane hitListPanel = createHitListPanel();

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
        GridLayout gridLayout = new GridLayout (4,0);
        detailsPanel.setLayout(gridLayout);

        JTextArea summaryTextArea = createTextArea();
        summaryDocument = summaryTextArea.getDocument();
        JTextArea matchingExcerptsTextArea = createTextArea();
        matchingExcerptsDocument = matchingExcerptsTextArea.getDocument();

        JScrollPane summaryPane = encloseInJScrollPane("Summary", summaryTextArea);
        JScrollPane matchingExceprtsPane = encloseInJScrollPane("Matching Excerpts",
                matchingExcerptsTextArea);
        JScrollPane interactionPane = createInteractionBundleTable();
        JScrollPane pathwayPane = createPathwayTable();
        detailsPanel.add(summaryPane);
        detailsPanel.add(matchingExceprtsPane);
        detailsPanel.add(interactionPane);
        detailsPanel.add(pathwayPane);
        return detailsPanel;
    }

    /**
     * Creates the Search Hits List Panel.
     * @return JScrollPane Object.
     */
    private JScrollPane createHitListPanel() {
        peListModel = new DefaultListModel();
        peListModel.addElement("Protein 1");
        peListModel.addElement("Protein 2");
        peListModel.addElement("Protein 3");
        peListModel.addElement("Protein 4");
        
        JList list = new JList(peListModel);
        list.setPrototypeCellValue("12345678901234567890");
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(new TitledBorder("Search Results"));
        return scrollPane;
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
        searchButton = new JButton("Go!");
        JButton helpButton = new JButton("Help");

        //  Search Button Action
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                webApi.searchPhysicalEntities(searchField.getText(), -1, 1);
            }
        });

        panel.add(searchField);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(organismComboBox);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(searchButton);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(helpButton);
        return panel;
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
        textArea.setColumns(40);
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
        JTable table = new JTable(pathwayTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
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
//    private Object[][] data = {
//        {"Reactome", "45", Boolean.FALSE},
//        {"MSKCC Cancer Cell Map", "12", Boolean.FALSE},
//        {"HPRD", "127", Boolean.FALSE}
//    };

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
}

/**
 * Pathway Table Model.
 *
 * @author Ethan Cerami
 */
class PathwayTableModel extends DefaultTableModel {
    //    private Object[][] data = {
    //        {"NCI-Nature", "BARD1 Signaling", Boolean.FALSE},
    //        {"Reactome", "Cell Cycle Checkpoints", Boolean.FALSE},
    //        {"Reactome", "Cell Cycle Mitotic", Boolean.FALSE}

    public PathwayTableModel () {
        super();
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
}