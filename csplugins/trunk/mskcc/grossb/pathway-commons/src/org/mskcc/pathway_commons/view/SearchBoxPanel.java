package org.mskcc.pathway_commons.view;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import org.mskcc.pathway_commons.task.ExecutePhysicalEntitySearch;
import org.mskcc.pathway_commons.web_service.PathwayCommonsWebApi;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Search Box Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchBoxPanel extends JPanel {
    private JButton searchButton;
    private PathwayCommonsWebApi webApi;
    private static final String ENTER_TEXT = "Enter Protein Name or ID";

    /**
     * Constructor.
     *
     * @param webApi PathwayCommmons Web API.
     */
    public SearchBoxPanel(PathwayCommonsWebApi webApi) {
        this.webApi = webApi;
        setBorder(new TitledBorder("Search"));
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        final JTextField searchField = createSearchField();
        JComboBox organismComboBox = createOrganismComboBox();
        JButton helpButton = new JButton("Help");
        helpButton.setToolTipText("Help");
        searchButton = createSearchButton(searchField);

        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(searchField);
        add(Box.createRigidArea(new Dimension(0, 5)));
        organismComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(organismComboBox);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        buttonPanel.add(helpButton);
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
        organismList.add("Human");
        DefaultComboBoxModel organismComboBoxModel = new DefaultComboBoxModel(organismList);
        JComboBox organismComboBox = new JComboBox(organismComboBoxModel);
        organismComboBox.setToolTipText("Select Organism");
        organismComboBox.setMaximumSize(new Dimension(200, 9999));
        organismComboBox.setPrototypeDisplayValue("12345678901234567890");
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
                    executeSearch(searchField.getText());
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
        searchButton = new JButton("Go!");
        searchButton.setToolTipText("Execute Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeSearch(searchField.getText());
            }
        });
        return searchButton;
    }

    private void executeSearch(String keyword) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (keyword == null || keyword.trim().length() == 0
                || keyword.startsWith(ENTER_TEXT)) {
            JOptionPane.showMessageDialog(window, "Please enter a protein name or ID",
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        } else {
            ExecutePhysicalEntitySearch search = new ExecutePhysicalEntitySearch
                    (webApi, keyword.trim(), -1, 1);
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setAutoDispose(true);
            jTaskConfig.displayCancelButton(false);
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
