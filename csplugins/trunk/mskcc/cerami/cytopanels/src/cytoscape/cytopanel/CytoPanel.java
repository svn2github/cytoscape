package cytoscape.cytopanel;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.BorderLayout;

import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

import java.util.ArrayList;
import java.net.URL;

import cytoscape.cytopanel.buttons.VTextIcon;
import cytoscape.cytopanel.util.CytoPanelUtil;

/**
 * The CytoPanel class extends JTabbedPane to provide the following functionality:
 * <UL>
 * <LI> Opening/Closing of draws (tabs).
 * <LI> Floating/Docking of Panel.
 * <UL>
 *
 * @author Benjamin Gross.
 */
public class CytoPanel extends JTabbedPane {

	/**
	 * Reference to CytoPanelContainer, the component we live inside
	 */
	private CytoPanelContainer cytoPanelContainer;

    /**
     * List of contentPanels - a content panel component contains
	 * a float/dock bar and the component passed into addTab()
     */
    private ArrayList contentPanelList;

	/**
	 * External Window used to hold the floating CytoPanel.
	 */
	private JFrame externalFrame;

    /**
     * Current Floating Status of CytoPanel.
     */
    private boolean isFloating = false;

    /**
     * Float Icon.
     */
    private ImageIcon floatIcon;

    /**
     * Dock Icon.
     */
    private ImageIcon dockIcon;


	/**
	 * Color of dock/float button panel
	 */
    private Color DOCK_FLOAT_PANEL_COLOR = new Color(204, 204, 204);

	/**
	 * prevIndex maintains the index of the last tab selected.
     */
	private int prevIndex;

	/**
     * Is this Cytopanel draw currently opened ?
	 */
	private boolean drawOpened;

	/* the following constants should probably move into common constants class */

	/**
	 * String used to compare against os.name System property - 
	 * to determine if we are running on Windows platform.
     */
	static final String WINDOWS = "windows";
	
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
        super(tabPlacement);

		// init some member vars
		prevIndex = -1;
		this.contentPanelList = new ArrayList();

		// Initialize all Icons
		initIcons();
    }

	/**
	 * Sets CytoPanelContainer interface reference
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

		// create ContentPanel
		ContentPanel contentPanel = new ContentPanel(c, title, DOCK_FLOAT_PANEL_COLOR);
		// add it to our list of content panes
		contentPanelList.add(contentPanel);

		// if windows platform and left or right tab location, use custom vertical text class
		if (isWindows() && tabPlacement != JTabbedPane.BOTTOM && tabPlacement != JTabbedPane.TOP ){
			int rotate = (tabPlacement == JTabbedPane.LEFT) ? VTextIcon.ROTATE_LEFT : VTextIcon.ROTATE_RIGHT;
			VTextIcon textIcon = new VTextIcon(c, title, rotate);
			super.addTab(null, textIcon, contentPanel, toolTipText);
		}
		else{
			super.addTab(title, null, contentPanel, toolTipText);
		}

	    // if this is the first tab, select it
		if (prevIndex == -1){
			prevIndex = 0;
			setSelectedIndex(0);
		}
    }

	/**
	 * Open the Tab Drawer.
	 */
	public void openTabDrawer() {
		//  If the Parent Container is a BiModalSplitPane, show the split
		Container parent = this.getParent();
		if (parent instanceof BiModalJSplitPane) {
			BiModalJSplitPane biModalSplitPane = (BiModalJSplitPane) parent;
 			biModalSplitPane.setMode(this, BiModalJSplitPane.MODE_SHOW_SPLIT);
		}
		drawOpened = true;
	}

	/**
	 * Close the Tab Drawer.
	 */
	public void closeTabDrawer() {
		//  If the Parent Container is a BiModalSplitPane, hide the split
		Container parent = this.getParent();
		if (parent instanceof BiModalJSplitPane) {
			BiModalJSplitPane biModalSplitPane = (BiModalJSplitPane) parent;
			biModalSplitPane.setMode(this, BiModalJSplitPane.MODE_HIDE_SPLIT);
		}
		drawOpened = false;
	}

	/**
     * Cet max width of tabs
     * @return max width (in pixels) of largest tab on cytopanel
     */
    public int getMaxWidthTabs(){
		int maxWidth = 0;
		for (int lc = 0; lc < getTabCount(); lc++){
			Rectangle r = getBoundsAt(lc);
			if (r != null && r.getWidth() > maxWidth) maxWidth = (int)r.getWidth();
		}
		return maxWidth;
	}

    /**
     * Cet max height of tabs
     * @return max height (in pixels) of largest tab on cytopanel
     */
    public int getMaxHeightTabs(){
		int maxHeight = 0;
		for (int lc = 0; lc < getTabCount(); lc++){
			Rectangle r = getBoundsAt(lc);
			if (r != null && r.getHeight() > maxHeight) maxHeight = (int)r.getHeight();
		}
		return maxHeight;
	}

	/**
	 * Mouse event handler - used to open/close draws.
     * @param evt       The Mouse Event.
	 */
	protected void processMouseEvent(MouseEvent evt)
    {
		// only process mouse clicked events
		if (evt.getID() == MouseEvent.MOUSE_CLICKED){
			// determine selected index(tab)
			Component c = getSelectedComponent();
			int selectedIndex = indexAtLocation(evt.getX(), evt.getY());
			// based on tab selected open or close draw
			if (selectedIndex != -1){
				if (selectedIndex == prevIndex){
					if (drawOpened){
						closeTabDrawer();
					}
					else{
						openTabDrawer();
					}
				}
				else{
					openTabDrawer();
				}
				prevIndex = selectedIndex;
			}
		}

		// set frame title 
		if (isFloating){
			String title = externalFrame.getTitle();
			String tabTitle = getTitleAt(getSelectedIndex());
			if (title.compareTo(tabTitle) != 0){
				externalFrame.setTitle(tabTitle);
			}
		}
		// give base class chance to process event
		super.processMouseEvent(evt);
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
	 * Determines if we are running on Windows platform.
	 */
	private boolean isWindows() {
		String os = System.getProperty("os.name");
		return os.regionMatches(true, 0, WINDOWS, 0, WINDOWS.length());
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
		else {
			// new frame to place this CytoPanel
			externalFrame = new JFrame();

			// add listener to handle when window is closed
			addWindowListener();

			//  Add CytoPanel to the New External Frame
			Container contentPane = externalFrame.getContentPane();
			contentPane.add(this, BorderLayout.CENTER);
			externalFrame.setSize(this.getSize());
			externalFrame.validate();

			// set proper title of frame
			externalFrame.setTitle(getTitleAt(getSelectedIndex()));

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

	/**
	 * The ContentPanel class contains is a JPanel that contains
	 * a float/dock button and the component passed into CytoPanel.addTab()
	 */
    private class ContentPanel extends JPanel {

		/**
		 * Background color of float/dock button and label.
		 */
		private Color color;

		/**
		 * The component passed into CytoPanel.addTab().
		 */
		private Component content;

		/**
		 * The label which contains the tab title - not sure if its needed.
		 */
        private JLabel headerLabel;

		/**
		 * The float/dock button.
		 */
        private JButton floatButton;

		/**
		 * The float button tool tip.
		 */
		private static final String TOOL_TIP_FLOAT = "Float Window";

		/**
		 * The dock button tool tip.
		 */
		private static final String TOOL_TIP_DOCK = "Dock Window";

		/**
		 * Our constructor (addTab() component, title of label, background color)
		 */
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

		/**
		 * Method to return addTab component.
		 */
        public Component getContent() {
            return content;
        }

		/**
		 * Method to return ref to float/dock button.
		 */
        public JButton getButton() {
            return floatButton;
        }

		/**
		 * Method to return ref to header label.
		 */
        public JLabel getHeaderLabel() {
            return headerLabel;
        }

		/**
		 * Initializes the label.
		 */
		private void initLabel(String title) {
			headerLabel = new JLabel(title);
			headerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			headerLabel.setBackground(color);
			headerLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
		}
		
		/**
		 * Initializes the button.
		 */
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
}
