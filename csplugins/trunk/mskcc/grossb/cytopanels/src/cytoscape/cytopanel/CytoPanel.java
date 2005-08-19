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
import java.awt.FontMetrics;
import java.awt.BorderLayout;

import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;

import javax.swing.Icon;
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

import cytoscape.cytopanel.util.CytoPanelUtil;
import cytoscape.cytopanel.ICytoPanel;

/**
 * The CytoPanel class extends JPanel to provide the following functionality:
 * <UL>
 * <LI> Floating/Docking of Panel.
 * <UL>
 *
 * CytoPanel also implements ICytoPanel interface.
 *
 * @author Benjamin Gross.
 */
public class CytoPanel extends JPanel implements ICytoPanel {

	/**
	 * The JTabbedPane we hide.
	 */
	private JTabbedPane tabbedPane;
	/**
	 * Our state.
	 */
	private int cytoPanelState;

	/**
	 * Our compass direction.
	 */
	private int compassDirection;

	/**
	 * Reference to CytoPanelContainer we live in.
	 */
	private CytoPanelContainer cytoPanelContainer;

	/**
	 * External window used to hold the floating CytoPanel.
	 */
	private JFrame externalFrame;

    /**
     * The float icon.
     */
    private ImageIcon floatIcon;

    /**
     * The dock icon.
     */
    private ImageIcon dockIcon;

	/**
	 * The label which contains the tab title - not sure if its needed.
	 */
	private JLabel floatLabel;

	/**
	 * The float/dock button.
	 */
	private JButton floatButton;

	/**
	 * The float/dock button.
	 */
	private final int FLOAT_PANEL_SCALE_FACTOR = 3;

	/**
	 * Color of the dock/float button panel.
	 */
    private Color FLOAT_PANEL_COLOR = new Color(204, 204, 204);

	/* the following constants should probably move into common constants class */

	/**
	 * The float button tool tip.
	 */
	private static final String TOOL_TIP_FLOAT = "Float Window";

	/**
	 * The dock button tool tip.
	 */
	private static final String TOOL_TIP_DOCK = "Dock Window";

    /**
     * Location of our icons.
     */
    private static final String RESOURCE_DIR = "resources";

    /**
     * The float icon gif filename.
     */
    private static final String FLOAT_GIF = "float.gif";

    /**
     * The dock icon gif filename.
     */
    private static final String DOCK_GIF = "pin.gif";

    /**
     * The file separator character.
     */
    private static final String FILE_SEPARATOR = "/";

    /**
     * Constructor.
	 *
     * @param compassDirection  Compass direction of this CytoPanel.
     * @param tabPlacement      Tab placement of this CytoPanel.
     */
    public CytoPanel(int compassDirection, int tabPlacement, int cytoPanelState){

		// init some member vars
		tabbedPane = new JTabbedPane(tabPlacement);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		if (compassDirection == SwingConstants.NORTH ||
			compassDirection == SwingConstants.EAST  ||
			compassDirection == SwingConstants.WEST  ||
			compassDirection == SwingConstants.SOUTH){
			this.compassDirection = compassDirection;
		}
		else{
			throw new IllegalArgumentException("Illegal Argument:  "
											   + compassDirection +
											   ".  Must be one of:  SwingConstants.{NORTH,SOUTH,EAST,WEST.");
		}

		// init the icons
		initIcons();

		// construct our panel
		constructPanel();

		// to hidden by default 
		setState(cytoPanelState);
    }

	/**
	 * Sets CytoPanelContainer interface reference.
	 *
     * @param cytoPanelContainer Reference to CytoPanelContainer
	 */
	public void setCytoPanelContainer(CytoPanelContainer cytoPanelContainer){
		
		// set our cytoPanelContainerReference
		this.cytoPanelContainer = cytoPanelContainer;
	}

    /**
     * Adds a new Component to the CytoPanel
     *
     * @param title     Copmone title.
	 * @param icon      Icon
     * @param component Component reference.
     * @param tip       Tool tip text.
     */
    public void add(String title, Icon icon, Component component, String tip){

		// add tab to JTabbedPane (string icon, component, tip)
		tabbedPane.addTab(title, null, component, tip);
    }

    /**
     * Sets the state of the CytoPanel.
     *
     * @param state A CytoPanelConstants state.
     */
    public void setState(int cytoPanelState){
		boolean success = false;

		switch (cytoPanelState){
		case CytoPanelConstants.CYTOPANEL_STATE_SHOW:
			showCytoPanel();
			success = true;
			break;
		case CytoPanelConstants.CYTOPANEL_STATE_HIDE:
			hideCytoPanel();
			success = true;
			break;
	    case CytoPanelConstants.CYTOPANEL_STATE_FLOAT:
		case CytoPanelConstants.CYTOPANEL_STATE_DOCK:
			flipFloatingStatus();
			success = true;
			break;
		}

		// houston we have a problem
		if (!success){
			// made it here, houston, we have a problem
			throw new IllegalArgumentException("Illegal Argument:  "
											   + cytoPanelState +
											   ".  is unknown.  Please see the CytoPanelConstants class.");
		}

		// set our new state
		this.cytoPanelState = cytoPanelState;
	}

    /**
     * Gets the state of the CytoPanel.
     *
	 * @return cytoPanelState A CytoPanel Constants state
     */
    public int getState(){
		return cytoPanelState;
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
	 * Returns the proper title based on our compass direction.
	 *
	 * @returns A title string
	 */
	private String getTitle(){
		switch (compassDirection){
		case SwingConstants.NORTH:
            return CytoPanelConstants.CYTOPANEL_TITLE_NORTH;
		case SwingConstants.SOUTH:
            return CytoPanelConstants.CYTOPANEL_TITLE_SOUTH;
		case SwingConstants.EAST:
            return CytoPanelConstants.CYTOPANEL_TITLE_EAST;
		case SwingConstants.WEST:
            return CytoPanelConstants.CYTOPANEL_TITLE_WEST;
		}
		return null;
	}

	/**
	 * Shows the CytoPanel.
	 */
	private void showCytoPanel() {

		// if we are already visible, lets bail.
		if (isVisible()) return;

		// make ourselves visible
		setVisible(true);

		//  if our parent is a BiModalSplitPane, show the split
		Container parent = this.getParent();
		if (parent instanceof BiModalJSplitPane) {
			BiModalJSplitPane biModalSplitPane = (BiModalJSplitPane) parent;
 			biModalSplitPane.setMode(BiModalJSplitPane.MODE_SHOW_SPLIT);
		}
	}

	/**
	 * Hides the CytoPanel.
	 */
	private void hideCytoPanel() {

		// if we are visible, hide
		if (isVisible()){

			// hide ourselves
			setVisible(false);

			//  if our Parent Container is a BiModalSplitPane, hide the split
			Container parent = this.getParent();
			if (parent instanceof BiModalJSplitPane) {
				BiModalJSplitPane biModalSplitPane = (BiModalJSplitPane) parent;
				biModalSplitPane.setMode(BiModalJSplitPane.MODE_HIDE_SPLIT);
			}
		}
	}


	/**
	 * Constructs this CytoPanel.
	 */
	void constructPanel(){

		// init our components
		initLabel();
		initButton();

		// add label and button components to yet another panel, 
		// so we can layout properly
		JPanel floatDockPanel = new JPanel(new BorderLayout());
			
		// set float dock panel attributes
		floatDockPanel.add(floatLabel, BorderLayout.WEST);
		floatDockPanel.add(floatButton, BorderLayout.EAST);
		floatDockPanel.setBorder(new EmptyBorder(2, 2, 2, 6));
		floatDockPanel.setBackground(FLOAT_PANEL_COLOR);
		// set preferred size - we can use float or dock icon diminsions - they are the same
		FontMetrics fm = floatLabel.getFontMetrics(floatLabel.getFont());
		floatDockPanel.setMinimumSize(new Dimension((int)((fm.stringWidth(getTitle()) + floatIcon.getIconWidth())*FLOAT_PANEL_SCALE_FACTOR),
													 floatIcon.getIconHeight()));
		floatDockPanel.setPreferredSize(new Dimension((int)((fm.stringWidth(getTitle()) + floatIcon.getIconWidth())*FLOAT_PANEL_SCALE_FACTOR),
													  floatIcon.getIconHeight()+2));

		// use the border layout for this CytoPanel
		setLayout(new BorderLayout());
		add(floatDockPanel, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Initializes the label.
	 */
	private void initLabel() {
		floatLabel = new JLabel(getTitle());
		floatLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		floatLabel.setBackground(FLOAT_PANEL_COLOR);
		floatLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
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
		floatButton.setBackground(FLOAT_PANEL_COLOR);

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
					floatButton.setBackground(FLOAT_PANEL_COLOR);
				}
		});

		floatButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					flipFloatingStatus();
				}
		});
	}

    /**
     * Float/Dock cytoPanel, depending on current status.
     */
	private void flipFloatingStatus() {
		if (isFloating()){
			// remove cytopanel from external view
			externalFrame.remove(this);
			// add this cytopanel back to cytopanel container
			if (cytoPanelContainer == null){
				System.out.println("CytoPanel::flipFloatingStatus() -" +
								   "cytoPanelContainer reference has not been set!");
				System.exit(1);
			}
			cytoPanelContainer.insertCytoPanel(this, compassDirection);

			// dispose of the external frame
			externalFrame.dispose();

			// set proper button icon/text
			floatButton.setIcon(floatIcon);
			floatButton.setToolTipText(TOOL_TIP_FLOAT);

			// set float label text
			floatLabel.setText(getTitle());

			// set our new state
			this.cytoPanelState = CytoPanelConstants.CYTOPANEL_STATE_DOCK;
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
			externalFrame.setTitle(getTitle());

			// set proper button icon/text
			floatButton.setIcon(dockIcon);
			floatButton.setToolTipText(TOOL_TIP_DOCK);

			// set float label text
			floatLabel.setText("");

			// set location of external frame
			setLocationOfExternalFrame(externalFrame);
			// lets show it
			externalFrame.show();

			// set our new state
			this.cytoPanelState = CytoPanelConstants.CYTOPANEL_STATE_FLOAT;
 		}

		// turn off the border
		floatButton.setBorderPainted(false);
		
		// re-layout
		this.validate();
	}

	/**
	 * Are we floating ?
	 */
	private boolean isFloating() {
		return (cytoPanelState == CytoPanelConstants.CYTOPANEL_STATE_FLOAT);
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
														   compassDirection,
														   false);

        externalWindow.setLocation(p);
        externalWindow.show();
    }



}
