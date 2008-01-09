package org.cytoscape.coreplugin.cpath2.view;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import org.cytoscape.coreplugin.cpath2.task.ExecutePhysicalEntitySearch;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.view.model.Organism;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.timing.Animator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.net.URL;

/**
 * Search Box Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchBoxPanel extends JPanel {
    private JButton searchButton;
    private CPathWebService webApi;
    private static final String ENTER_TEXT = "Enter Gene Name or ID";
    private Animator animator;
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
        final JTextField searchField = createSearchField();

        pulsatingBorder = new PulsatingBorder (searchField);
        searchField.setBorder (BorderFactory.createCompoundBorder(searchField.getBorder(),
                pulsatingBorder));

        PropertySetter setter = new PropertySetter (pulsatingBorder, "thickness",
                0.0f, 1.0f);
        animator = new Animator (900, Animator.INFINITE,
                Animator.RepeatBehavior.REVERSE, setter);
        animator.start();
        
        organismComboBox = createOrganismComboBox();
        searchButton = createSearchButton(searchField);

        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel ("Examples:  TP53, BRCA1, or SRY.");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = label.getFont();
        Font newFont = new Font (font.getFamily(), font.getStyle(), font.getSize()-2);
        label.setFont(newFont);
        label.setBorder(new EmptyBorder(5,3,3,3));

        add(searchField);
        add (label);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        //searchButton.setBorder(new EmptyBorder (0,6,7,0));
        searchButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        organismComboBox.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        buttonPanel.add(organismComboBox);
        buttonPanel.add(searchButton);
        add(buttonPanel);
    }

    /**
     * Creates the Organism Combo Box.
     *
     * @return JComboBox Object.
     */
    private JComboBox createOrganismComboBox() {
        //  Organism List is currently hard-coded.
        Vector organismList = new Vector();
        organismList.add(new Organism("Human", 9606));
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
                    executeSearch(searchField.getText(), organism.getNcbiTaxonomyId());
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
        URL url = GradientHeader.class.getResource("resources/run_tool.gif");
        ImageIcon icon = new ImageIcon(url);
        //searchButton = new JButton(icon);
        searchButton = new JButton("Search");
        searchButton.setToolTipText("Execute Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Organism organism = (Organism) organismComboBox.getSelectedItem();
                executeSearch(searchField.getText(), organism.getNcbiTaxonomyId());
            }
        });
        return searchButton;
    }

    private void executeSearch(String keyword, int ncbiTaxonomyId) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (keyword == null || keyword.trim().length() == 0
                || keyword.startsWith(ENTER_TEXT)) {
            JOptionPane.showMessageDialog(window, "Please enter a Gene Name or ID.",
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        } else {
            ExecutePhysicalEntitySearch search = new ExecutePhysicalEntitySearch
                    (webApi, keyword.trim(), ncbiTaxonomyId, 1);
            if (animator.isRunning()) {
                pulsatingBorder.setThickness(0);
                animator.stop();
            }
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setAutoDispose(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.displayCancelButton(true);
            jTaskConfig.displayCloseButton(false);
            jTaskConfig.setOwner(window);
            TaskManager.executeTask(search, jTaskConfig);
        }
    }

    /**
     * Initializes Focus to the Search Button.
     */
    public void initFocus() {
        searchButton.requestFocusInWindow();
    }
}
