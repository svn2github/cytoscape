package cytoscape.cytopanel;

import cytoscape.cytopanel.buttons.CustomButton;
import cytoscape.cytopanel.buttons.VTextIcon;
import cytoscape.cytopanel.util.CytoPanelUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.ArrayList;
import java.net.URL;

public class CytoPanel extends JComponent {

	/**
	 * Reference to CytoPanelContainer we live within
	 */
	private CytoPanelContainer cytoPanelContainer;

    /**
     * The Button Panel contains Buttons that activate specific tabs.
     */
    private JPanel buttonPanel;

    /**
     * The Component Panel contain the tab contents.
     * It uses a CardLayout Manager.
     */
    private JPanel cytoPanelPanel;

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

	/**
	 * Tab currently with focus
	 */
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
	 * External Windown used to hold the floating CytoPanel
	 */
	private JFrame externalFrame;

    /**
     * Current Floating Status of CytoPanel.
     */
    private boolean isFloating = false;

    /**
     * Header Label
     */
    private ArrayList contentPanelList;

    /**
     * Float Icon.
     */
    private ImageIcon floatIcon;

    /**
     * Dock Icon.
     */
    private ImageIcon dockIcon;

	/* the following constants maybe should move into common constants class */
	
    /**
     * Location of Resources, e.g. GIF Icons
     */
    private static final String RESOURCE_DIR = "resources";

    /**
     * Float GIF File Name
     */
    private static final String FLOAT_GIF = "float.gif";

    /**
     * Dock GIF File Name
     */
    private static final String DOCK_GIF = "pin.gif";

    /**
     * File Separator
     */
    private static final String FILE_SEPARATOR = "/";

    /**
     * Constructor.
     * @param tabPlacement  Tab Placement int value.
     */
    public CytoPanel(int tabPlacement) {

		// init some member vars
        this.pageList = new ArrayList();
		this.contentPanelList = new ArrayList();

        // Validate tabPlacement values
		if (tabPlacement != JTabbedPane.LEFT &&
			tabPlacement != JTabbedPane.RIGHT &&
			tabPlacement != JTabbedPane.TOP &&
			tabPlacement != JTabbedPane.BOTTOM){
			throw new IllegalArgumentException("Illegal Argument:  "
											   + tabPlacement +
											   ".  Must be one of:  JTabbedPane.{TOP,BOTTOM,LEFT,RIGHT}.");
		}
        this.tabPlacement = tabPlacement;
        model = new DefaultSingleSelectionModel();

		// Initialize all Icons
		initIcons();

        //  Initialize the GUI
        initUI();
    }

	/**
	 * Sets CytoPanelContainer inteface reference
     * @param cytoPanelContainer Reference to CytoPanelContainer
	 */
	public void setCytoPanelContainer(CytoPanelContainer cytoPanelContainer){
		// set our cytoPanelContainerReference
		this.cytoPanelContainer = cytoPanelContainer;
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

		// create floatDockPaneList
		ContentPanel contentPanel = new ContentPanel(c, title, c.getBackground());
		contentPanelList.add(contentPanel);

        //  Add the Component to the Component Panel
        //cytoPanelPanel.add(c, title);
		cytoPanelPanel.add(contentPanel, title);

        //  Create a Page Object
        //Page page = new Page (this, title, c, toolTipText, button);
		Page page = new Page (this, title, contentPanel, toolTipText, button);
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
        cytoPanelPanel.setVisible(true);

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
        cytoPanelPanel.setVisible(false);
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
                if (!isFloating && model.isSelected()) {
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
                cardLayout.show(cytoPanelPanel, action);

                //  Reset all the Background Colors
                applyBackgroundColors();
            }
        });
    }

	/**
	 * Gets button index (in button panel), by button title
	 */
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
     * Initialize all Icons.
     */
    private void initIcons() {
		// url to float icon
        URL floatIconURL = CytoPanel.class.getResource
                (RESOURCE_DIR + FILE_SEPARATOR + FLOAT_GIF);
		// url to docking icon
        URL dockIconURL = CytoPanel.class.getResource
                (RESOURCE_DIR + FILE_SEPARATOR + DOCK_GIF);

		// create our icon objects
        floatIcon = new ImageIcon(floatIconURL);
        dockIcon = new ImageIcon(dockIconURL);
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
        cytoPanelPanel = new JPanel();
        cardLayout = new CardLayout();
        cytoPanelPanel.setLayout(cardLayout);

        //  Use the Border Layout for this Component
        this.setLayout(new BorderLayout());

		// layout cytopanel 
        if (tabPlacement == JTabbedPane.LEFT) {
            this.add(buttonPanelContainer, BorderLayout.WEST);
            this.add(cytoPanelPanel, BorderLayout.CENTER);
        } else if (tabPlacement == JTabbedPane.RIGHT) {
            this.add(buttonPanelContainer, BorderLayout.EAST);
            this.add(cytoPanelPanel, BorderLayout.CENTER);
        } else if (tabPlacement == JTabbedPane.BOTTOM) {
            this.add(buttonPanelContainer, BorderLayout.SOUTH);
            this.add(cytoPanelPanel, BorderLayout.CENTER);
        }
    }

    /**
     * Float/Dock cytoPanel, depending on current status.
     */
	private void flipFloatingStatus() {
		if (isFloating){
			// remove cytopanel from external view
			externalFrame.remove(this);
			// add this cytopanel back to cytopanel container
			if (cytoPanelContainer == null){
				System.out.println("CytoPanel::flipFloatingStatus() -" +
								   "cytoPanelContainer reference has not been set!");
				System.exit(1);
			}
			cytoPanelContainer.insertCytoPanel(this, getCompassDirection());
			// dispose of the external frame
			externalFrame.dispose();
			// for each tab, set the correct button tooltip and icon
			for (int lc = 0; lc < contentPanelList.size(); lc++) {
				ContentPanel panel = (ContentPanel) contentPanelList.get(lc);
				panel.getButton().setIcon(floatIcon);
				panel.getButton().setToolTipText(ContentPanel.TOOL_TIP_FLOAT);
			}
		}
		else{
			// new frame to place this CytoPanel
			externalFrame = new JFrame();

			// add listener to handle when window is closed
			addWindowListener();

			//  Add CytoPanel to the New External Frame
			Container contentPane = externalFrame.getContentPane();
			contentPane.add(this, BorderLayout.CENTER);
			externalFrame.setSize(this.getSize());
			externalFrame.validate();

			// for each tab, set the correct button tooltip and icon
			for (int lc = 0; lc < contentPanelList.size(); lc++) {
				ContentPanel panel = (ContentPanel) contentPanelList.get(lc);
				panel.getButton().setIcon(dockIcon);
				panel.getButton().setToolTipText(ContentPanel.TOOL_TIP_DOCK);
			}

			// set location of external frame
			setLocationOfExternalFrame(externalFrame);
			// lets show it
			externalFrame.show();
 
		}
		
		// set our floating status
		isFloating = !isFloating;

		// re-layout
		this.validate();
	}

    /**
     * Returns compass direction based on location of tabs.
     */
	private int getCompassDirection() {
		switch (tabPlacement){
		case JTabbedPane.LEFT:
			return SwingConstants.WEST;
		case JTabbedPane.RIGHT:
			return SwingConstants.EAST;
		case JTabbedPane.TOP:
			return SwingConstants.NORTH;
		case JTabbedPane.BOTTOM:
			return SwingConstants.SOUTH;
		}
		// should never get here, tabPlacement validated in constructior
		return -1;
	}

    /**
     * Adds the Correct Window Listener.
     */
    private void addWindowListener() {
        externalFrame.addWindowListener(new WindowAdapter() {

            /**
             * Window is Closing.
             *
             * @param e Window Event.
             */
            public void windowClosing(WindowEvent e) {
                flipFloatingStatus();
            }
        });
    }

    /**
     * Sets the Location of the External Frame.
     *
     * @param externalWindow ExternalFrame Object.
     */
    private void setLocationOfExternalFrame(JFrame externalWindow) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimension = tk.getScreenSize();

        //  Get Absolute Location and Bounds, relative to Screen
        Rectangle containerBounds = cytoPanelContainer.getBounds();
        containerBounds.setLocation(cytoPanelContainer.getLocationOnScreen());

        Point p = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
														   containerBounds,
														   externalWindow.getSize(),
														   getCompassDirection(),
														   false);

        externalWindow.setLocation(p);
        externalWindow.show();
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

    private class ContentPanel extends JPanel {
		private Color color;
		private Component content;
        private JLabel headerLabel;
        private JButton floatButton;
		private static final String TOOL_TIP_FLOAT = "Float Window";
		private static final String TOOL_TIP_DOCK = "Dock Window";

        ContentPanel(Component c, String title, Color color) {
			// init member vars
			this.color = color;
			this.content = c;

			// setup our label and button components
			initLabel(title);
			initButton();

			// add label and button components to yet another panel, 
			// so we can layout properly
			JPanel floatDockPanel = new JPanel(new BorderLayout());
			floatDockPanel.add(headerLabel, BorderLayout.WEST);
			floatDockPanel.add(floatButton, BorderLayout.EAST);;
			floatDockPanel.setBackground(color);
			floatDockPanel.setBorder(new EmptyBorder(2, 2, 2, 6));
			floatDockPanel.setBackground(color);

			// use the border layout for this ContentPanel

			setLayout(new BorderLayout());
			add(floatDockPanel, BorderLayout.NORTH);
			add(c, BorderLayout.CENTER);
			setBackground(color);
        }

        public Component getContent() {
            return content;
        }

        public JButton getButton() {
            return floatButton;
        }

		private void initLabel(String title) {
			headerLabel = new JLabel(title);
			headerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			headerLabel.setBackground(color);
			headerLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
		}
		
		private void initButton() {
			//  Create Float / Dock Button
			floatButton = new JButton();
			floatButton.setIcon(floatIcon);
			floatButton.setToolTipText(TOOL_TIP_FLOAT);
			floatButton.setRolloverEnabled(true);

			//  Set 0 Margin All-Around and setBorderPainted to false
			//  so that button appears as small as possible
			floatButton.setMargin(new Insets(0, 0, 0, 0));
			floatButton.setBorder(new EmptyBorder(1, 1, 1, 1));
			floatButton.setBorderPainted(false);
			floatButton.setSelected(false);
			floatButton.setBackground(color);

			//  When User Hovers Over Button, highlight it with a gray box
			floatButton.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) {
						floatButton.setBorder(new LineBorder(Color.GRAY, 1));
						floatButton.setBorderPainted(true);
						floatButton.setBackground(Color.LIGHT_GRAY);
					}

					public void mouseExited(MouseEvent e) {
						floatButton.setBorder(new EmptyBorder(1, 1, 1, 1));
						floatButton.setBorderPainted(false);
						floatButton.setBackground(color);
					}
			});

			floatButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						flipFloatingStatus();
					}
			});
		}
	}
}    // end CytoPanel
