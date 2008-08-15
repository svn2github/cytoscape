package org.cytoscape.coreplugin.cpath2.view;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import org.cytoscape.coreplugin.cpath2.task.ExecutePhysicalEntitySearch;
import org.cytoscape.coreplugin.cpath2.view.model.Organism;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Vector;

/**
 * Search Box Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchBoxPanel extends JPanel {
    private JButton searchButton;
    private CPathWebService webApi;
    private static final String ENTER_TEXT = "Enter Gene Name or ID";
    private PulsatingBorder pulsatingBorder;
    private JComboBox organismComboBox;

    /**
     * Constructor.
     *
     * @param webApi CPath Web Service Object.
     */
    public SearchBoxPanel(CPathWebService webApi) {
        this.webApi = webApi;
        GradientHeader header = new GradientHeader("Step 1:  Search");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        add (header);
        add (Box.createVerticalStrut(5));

        JPanel centerPanel = new JPanel();
        BoxLayout boxLayoutMain = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        centerPanel.setLayout(boxLayoutMain);
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        final JTextField searchField = createSearchField();

        pulsatingBorder = new PulsatingBorder (searchField);
        searchField.setBorder (BorderFactory.createCompoundBorder(searchField.getBorder(),
                pulsatingBorder));

        organismComboBox = createOrganismComboBox();
        searchButton = createSearchButton(searchField);

        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JEditorPane label = new JEditorPane ("text/html", "Examples:  <a href='TP53'>TP53</a>, " +
                "<a href='BRCA1'>BRCA1</a>, or <a href='SRY'>SRY</a>.");
        label.setEditable(false);
        label.setOpaque(false);
        label.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        label.addHyperlinkListener(new HyperlinkListener() {

            // Update search box with activated example.
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    searchField.setText(hyperlinkEvent.getDescription());
                }
            }
        });
        
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = label.getFont();
        Font newFont = new Font (font.getFamily(), font.getStyle(), font.getSize()-2);
        label.setFont(newFont);
        label.setBorder(new EmptyBorder(5,3,3,3));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(searchField);

        organismComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(organismComboBox);
        centerPanel.add(searchButton);

        add(centerPanel);
        add(label);
    }

    /**
     * Creates the Organism Combo Box.
     *
     * @return JComboBox Object.
     */
    private JComboBox createOrganismComboBox() {
        //  Organism List is currently hard-coded.
        Vector organismList = new Vector();
        organismList.add(new Organism("All Organisms", -1));
        CPathProperties props = CPathProperties.getInstance();
        organismList.addAll(props.getOrganismList());
        DefaultComboBoxModel organismComboBoxModel = new DefaultComboBoxModel(organismList);
        JComboBox organismComboBox = new JComboBox(organismComboBoxModel);
        organismComboBox.setToolTipText("Select Organism");
        organismComboBox.setMaximumSize(new Dimension(200, 9999));
        organismComboBox.setPrototypeDisplayValue("12345678901234567");
        return organismComboBox;
    }

    /**
     * Creates the Search Field and associated listener(s)
     *
     * @return JTextField Object.
     */
    private JTextField createSearchField() {
        final JTextField searchField = new JTextField(ENTER_TEXT.length());
        searchField.setText(ENTER_TEXT);
        searchField.setToolTipText(ENTER_TEXT);
        searchField.setMaximumSize(new Dimension(200, 9999));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent focusEvent) {
                if (searchField.getText() != null
                        && searchField.getText().startsWith("Enter")) {
                    searchField.setText("");
                }
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == 10) {
                    Organism organism = (Organism) organismComboBox.getSelectedItem();
                    executeSearch(searchField.getText(), organism.getNcbiTaxonomyId(),
                            organism.getSpeciesName());
                }
            }
        });
        return searchField;
    }

    /**
     * Creates the Search Button and associated action listener.
     *
     * @param searchField JTextField searchField
     * @return
     */
    private JButton createSearchButton(final JTextField searchField) {
        URL url = GradientHeader.class.getResource("/images/run_tool.gif");
        ImageIcon icon = new ImageIcon(url);
        //searchButton = new JButton(icon);
        searchButton = new JButton("Search");
        searchButton.setToolTipText("Execute Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Organism organism = (Organism) organismComboBox.getSelectedItem();
                executeSearch(searchField.getText(), organism.getNcbiTaxonomyId(),
                        organism.getSpeciesName());
            }
        });
        return searchButton;
    }

    private void executeSearch(String keyword, int ncbiTaxonomyId, String speciesName) {
        Window window = Cytoscape.getDesktop();
        if (keyword == null || keyword.trim().length() == 0
                || keyword.startsWith(ENTER_TEXT)) {
            JOptionPane.showMessageDialog(window, "Please enter a Gene Name or ID.",
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        } else {
            ExecutePhysicalEntitySearch search = new ExecutePhysicalEntitySearch
                    (webApi, keyword.trim(), ncbiTaxonomyId);
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setAutoDispose(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.displayCancelButton(true);
            jTaskConfig.displayCloseButton(false);
            jTaskConfig.setOwner(window);
            TaskManager.executeTask(search, jTaskConfig);
            if (search.getNumMatchesFound() == 0) {
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                        "No matches found for:  " + keyword + " [" + speciesName + "]" +
                        "\nPlease try a different search term and/or organism filter.",
                        "No matches found.",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Initializes Focus to the Search Button.
     */
    public void initFocus() {
        searchButton.requestFocusInWindow();
    }
}
