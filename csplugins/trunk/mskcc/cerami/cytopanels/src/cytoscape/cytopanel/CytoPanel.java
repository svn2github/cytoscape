package cytoscape.cytopanel;

import cytoscape.cytopanel.buttons.CustomButton;
import cytoscape.cytopanel.buttons.VTextIcon;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.ArrayList;

public class CytoPanel extends JComponent {

    /**
     * The Button Panel contains Buttons that activate specific tabs.
     */
    private JPanel buttonPanel;

    /**
     * The Component Panel contain the tab contents.
     * It uses a CardLayout Manager.
     */
    private JPanel componentPanel;

    /**
     * Tab Placement.
     */
    private int tabPlacement;

    /**
     * The Card Layout, used to Flip between different Tabs.
     */
    private CardLayout cardLayout;

    /**
     * Selection Model Object.
     */
    private SingleSelectionModel model;

    /**
     * List of Tabs.
     */
    private ArrayList pageList;

    private String currentTab;

    /**
     * Background Color of Tab When Selected
     */
    private static final Color COLOR_TAB_SELECTED = new Color(204, 204, 255);

    /**
     * Background Color of Tab When Not Selected
     */
    private static final Color COLOR_TAB_NOT_SELECTED = new Color
            (240, 240, 240);

    /**
     * Constructor.
     * @param tabPlacement  Tab Placement int value.
     */
    public CytoPanel(int tabPlacement) {
        this.pageList = new ArrayList();

        // TODO:  Validate tabPlacement values

        this.tabPlacement = tabPlacement;
        model = new DefaultSingleSelectionModel();

        //  Initialize the GUI
        initUI();
    }

    /**
     * Adds a New Tab.
     *
     * @param title       Title of Tab.
     * @param c           Component object.
     * @param toolTipText Tool Tip Text.
     */
    public void addTab(String title, Component c, String toolTipText) {

        //  Create a Custom Button
        JButton button = null;
        if (tabPlacement == JTabbedPane.LEFT) {
            button = new CustomButton(title, VTextIcon.ROTATE_LEFT);
        } else if (tabPlacement == JTabbedPane.RIGHT) {
            button = new CustomButton(title, VTextIcon.ROTATE_RIGHT);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            button = new CustomButton(title);
        }

        //  Set the Tool Tip Text
        button.setToolTipText(toolTipText);

        //  Add the Appropriate Action Listener
        addActionListener(button);

        //  Add the Button to the ButtonPanel
        buttonPanel.add(button);
        buttonPanel.add(Box.createRigidArea(new Dimension(2, 5)));

        //  Add the Component to the Component Panel
        componentPanel.add(c, title);

        //  Create a Page Object
        Page page = new Page (this, title, c, toolTipText, button);
        pageList.add(page);

        //  If this is the first tab, select it.
        if (pageList.size() == 1) {
            model.setSelectedIndex(0);
            button.setBackground(COLOR_TAB_SELECTED);
            currentTab = title;
        }
    }

    /**
     * Open the Tab Drawer.
     */
    public void openTabDrawer() {
        componentPanel.setVisible(true);

        //  If the Parent Container is a BiModalSplitPane, show the split
        Container parent = this.getParent();
        if (parent instanceof BiModalJSplitPane) {
            BiModalJSplitPane biModalSplitPane = (BiModalJSplitPane) parent;
            biModalSplitPane.setMode(BiModalJSplitPane.MODE_SHOW_SPLIT);
        }
    }

    /**
     * Close the Tab Drawer.
     */
    public void closeTabDrawer() {
        componentPanel.setVisible(false);
        model.clearSelection();
        this.applyBackgroundColors();

        //  If the Parent Container is a BiModalSplitPane, hide the split
        Container parent = this.getParent();
        if (parent instanceof BiModalJSplitPane) {
            BiModalJSplitPane biModalSplitPane = (BiModalJSplitPane) parent;
            biModalSplitPane.setMode(BiModalJSplitPane.MODE_HIDE_SPLIT);
        }
    }

    /**
     * Adds the Appropriate Action Listeners.
     *
     * @param button JButton Object.
     */
    private void addActionListener(JButton button) {

        //  Uses an Internal Class
        button.addActionListener(new ActionListener() {

            /**
             * Button was Clicked.
             *
             * @param e ActionEvent Object
             */
            public void actionPerformed(ActionEvent e) {

                //  Get the Source of the Event
                JButton button = (JButton) e.getSource();
                String action = e.getActionCommand();

                //  Select the Correct Button in the Button Group
                //  buttonGroup.setSelected(button.getModel(), true);

                //  Determine the Next State in the State Machine
                if (model.isSelected()) {
                    if (currentTab.equals(action)) {
                        model.clearSelection();
                        closeTabDrawer();
                    }
                } else if (!model.isSelected()) {
                    int index = getIndexValueByTitle (action);
                    model.setSelectedIndex(index);
                    openTabDrawer();
                }

                // Store Current Action
                currentTab = action;

                //  Show the Correct Component in the Card Layout
                cardLayout.show(componentPanel, action);

                //  Reset all the Background Colors
                applyBackgroundColors();
            }
        });
    }

    private int getIndexValueByTitle (String targetTitle) {
        int targetIndex = -1;
        for (int i=0; i<pageList.size(); i++) {
            Page page = (Page) pageList.get(i);
            if (page.getTitle().equals(targetTitle)) {
                targetIndex = i;
            }
        }
        return targetIndex;
    }

    /**
     * Sets the Background of All Tab Backgrounds to indicate current State.
     */
    private void applyBackgroundColors() {

        //  Iterate through all Buttons
        for (int i=0; i<pageList.size(); i++) {
            Page page = (Page) pageList.get(i);
            JButton button = page.getButton();

            if (i == model.getSelectedIndex()) {
                button.setBackground(COLOR_TAB_SELECTED);
            } else {
                button.setBackground(COLOR_TAB_NOT_SELECTED);
            }
        }
    }

    /**
     * Initializes the GUI.
     */
    private void initUI() {

        //  Create a Button Panel, for containing all buttons that
        //  activate specific tabs.

        //  Create a Generic Container, so that we can generate
        //  nice looking borders
        JPanel buttonPanelContainer = new JPanel();
        buttonPanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanelContainer.setBorder(new EmptyBorder(3, 1, 3, 1));

        //  Create the Button Panel Itself
        buttonPanel = new JPanel();
        buttonPanelContainer.add(buttonPanel);

        //  The ButtonPanel uses a BoxLayout, because we want the
        //  layout manager to respect the sizes of all buttons.
        BoxLayout boxLayout = null;
        if (tabPlacement == JTabbedPane.LEFT
                || tabPlacement == JTabbedPane.RIGHT) {
            boxLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            boxLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
        }
        buttonPanel.setLayout(boxLayout);

        //  Create the Component Panel.
        //  The Component Panel uses a CardLayout so that we can flip
        //  through all the tabs one at a time.
        componentPanel = new JPanel();
        cardLayout = new CardLayout();
        componentPanel.setLayout(cardLayout);

        //  Use the Border Layout for this Component
        this.setLayout(new BorderLayout());

        if (tabPlacement == JTabbedPane.LEFT) {
            this.add(buttonPanelContainer, BorderLayout.WEST);
            this.add(componentPanel, BorderLayout.CENTER);
        } else if (tabPlacement == JTabbedPane.RIGHT) {
            this.add(buttonPanelContainer, BorderLayout.EAST);
            this.add(componentPanel, BorderLayout.CENTER);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            this.add(buttonPanelContainer, BorderLayout.SOUTH);
            this.add(componentPanel, BorderLayout.CENTER);
        }
    }

    private class Page {
        private CytoPanel parent;
        private String title;
        private Component component;
        private String tip;
        private JButton button;

        Page(CytoPanel parent, String title, Component component, String tip,
                JButton button) {
            this.parent = parent;
            this.title = title;
            this.component = component;
            this.tip = tip;
            this.button = button;
        }

        public CytoPanel getParent() {
            return parent;
        }

        public String getTitle() {
            return title;
        }

        public Component getComponent() {
            return component;
        }

        public String getTip() {
            return tip;
        }

        public JButton getButton() {
            return button;
        }
    }
}

