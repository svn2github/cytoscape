package cytoscape.cytopanel;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

/**
 * The BiModalJSplitPane Object extends JSplitPane to provide two modes:
 * <UL>
 * <LI>MODE_SHOW_SPLIT:  The split in the split pane appears as it normally
 *                       would, and the user can resize the split pane as
 *                       needed.
 * <LI>MODE_HIDE_SPLIT:  The split in the split pane is hidden, and the
 *                       user cannot resize the split pane.
 * </UL>
 *
 * NOTE: only one CytoPanel is supported per BiModalJSplitPane.
 *
 * @author Ethan Cerami.
 */
public class BiModalJSplitPane extends JSplitPane implements CytoPanelContainer {

    /**
     * Mode:  Show the Split Pane.
     */
    public static final int MODE_SHOW_SPLIT = 1;

    /**
     * Mode:  Hide the Split Pane.
     */
    public static final int MODE_HIDE_SPLIT = 2;

    /**
     * Mode Property;  used by property listeners.
     */
    public static final String MODE_PROPERTY = "MODE_PROPERTY";

    /**
     * Reference application frame.
     */
    private JFrame frame;

	/**
	 * Reference to our CytoPanel
	 */
	private CytoPanel cytoPanel;

    /**
     * The Current Mode Value.
     */
    private int currentMode;

    /**
     * The Default Divider Size, as this may vary by platform.
     */
    private int defaultDividerSize;

    /**
     * The Divider Location just before adjusting it.
     */
    private int savedDividerLocation;

	/**
	 * Values used to determine the type of hide split to perform
     */
	private static final int MODE_HIDE_SPLIT_LEFT = 0;
	private static final int MODE_HIDE_SPLIT_RIGHT = 1;
	private static final int MODE_HIDE_SPLIT_BOTTOM = 2;
	private static final int MODE_HIDE_SPLIT_TOP = 3;

    /**
     * Constructor.
     *
	 * @param frame          The Application Frame.
     * @param orientation    JSplitPane Orientation.
     *                       JSplitPane.HORIZONTAL_SPLIT or
     *                       JSplitPane.VERTICAL_SPLIT.
     * @param initialMode    Initial Mode.
     *                       MODE_SHOW_SPLIT or
     *                       MODE_HIDE_SPLIT.
     * @param leftComponent  Left/Top Component.
     * @param rightComponent Right/Bottom Component.
	 *
     */
    public BiModalJSplitPane(JFrame f, int orientation, int initialMode,
            Component leftComponent, Component rightComponent) {
        super(orientation, leftComponent, rightComponent);

		// init some member vars
        currentMode = initialMode;
		frame = f;
		cytoPanel = leftComponent instanceof CytoPanel ?
			(CytoPanel) leftComponent : (CytoPanel) rightComponent;

		// add component listener to get resize events
		addComponentListener();

        //  Remove the Border, by Default
        setBorder(null);
        setOneTouchExpandable(false);

        //  Store the Default Divider Size, as this may vary by platform
        defaultDividerSize = getDividerSize();

        //  Hide Split, if requested
        if (initialMode == MODE_HIDE_SPLIT) {
            this.setDividerSize(0);
        }
    }

    /**
     * Gets the default divider size
     * @return MODE_SHOW_SPLIT or MODE_HIDE_SPLIT.
     */
	public int getDefaultDividerSize() {
		return defaultDividerSize;
	}

    /**
     * Sets the Current Mode.
     * @param newMode MODE_SHOW_SPLIT or MODE_HIDE_SPLIT.
     */
    public void setMode(int newMode) {
        //  Check Parameters
        if (newMode != MODE_SHOW_SPLIT && newMode != MODE_HIDE_SPLIT) {
            throw new IllegalArgumentException("Illegal Argument:  "
                    + newMode + ".  Must be one of:  MODE_SHOW_SPLIT or "
                    + " MODE_HIDE_SPLIT.");
        }
        int oldMode = currentMode;

        //  Only act on Change in Mode Status
        if (newMode != currentMode) {
            if (newMode == MODE_HIDE_SPLIT) {
                hideSplit(false);
            } else if (newMode == MODE_SHOW_SPLIT) {
                showSplit();
            }
            this.currentMode = newMode;

            //  Fire the Property Change to all Registered Listeners
            this.firePropertyChange(MODE_PROPERTY, oldMode, newMode);
		}
    }

    /**
     * Gets the Current Mode.
     * @return MODE_SHOW_SPLIT or MODE_HIDE_SPLIT.
     */
    public int getMode() {
        return currentMode;
    }

    /**
     * Inserts CytoPanel at Specified Compass Direction.
     *
     * @param cytoPanel        CytoPanel Object.
     * @param compassDirection SwingConstants integer value.
     */
	public void insertCytoPanel(CytoPanel cytoPanel, int compassDirection) {
		boolean success = false;

		switch (compassDirection){
		case SwingConstants.NORTH:
            this.setTopComponent(cytoPanel);
			success = true;
			break;
		case SwingConstants.SOUTH:
            this.setBottomComponent(cytoPanel);
			success = true;
			break;
		case SwingConstants.EAST:
            this.setRightComponent(cytoPanel);
			success = true;
			break;
		case SwingConstants.WEST:
            this.setLeftComponent(cytoPanel);
			success = true;
			break;
		}

		// houston we have a problem
		if (!success){
			// made it here, houston, we have a problem
			throw new IllegalArgumentException("Illegal Argument:  "
											   + compassDirection +
											   ".  Must be one of:  SwingConstants.{NORTH,SOUTH,EAST,WEST.");
		}
	}

    /**
     * Gets Location of Container, in screen coordinates.
     *
     * @return Point Object.
     */
    public Point getLocationOnScreen() {
		return frame.getLocationOnScreen();
	}

    /**
     * Gets Bounds of Container, relative to parent component.
     *
     * @return Rectangle Object.
     */
    public Rectangle getBounds() {
		return frame.getBounds();
	}

    /**
     * Shows the Split.
     */
    private void showSplit() {

		// if cytopanel has no tabs, its invisible
		if (cytoPanel.getTabCount() == 0){
			cytoPanel.setVisible(false);
			return;
		}

		setDividerLocation(savedDividerLocation);
        setDividerSize(defaultDividerSize);
        validateParent();
    }

    /**
     * Hides the Split.
     * @param cytoPanel used to determine type of hide to perform
     */
    private void hideSplit(boolean resizing) {

		// if cytopanel has no tabs, its invisible
		if (cytoPanel.getTabCount() == 0){
			cytoPanel.setVisible(false);
			return;
		}

		// if we are just resizing, do not save new divider location
		if (!resizing){
			savedDividerLocation = getDividerLocation();
		}

		// determine which hide split to perform and do it
		switch (getHideType()){
		case MODE_HIDE_SPLIT_LEFT:
			hideSplitTopLeft(MODE_HIDE_SPLIT_LEFT);
			break;
		case MODE_HIDE_SPLIT_RIGHT:
			hideSplitBottomRight(MODE_HIDE_SPLIT_RIGHT);
			break;
		case MODE_HIDE_SPLIT_TOP:
			hideSplitTopLeft(MODE_HIDE_SPLIT_TOP);
			break;
		case MODE_HIDE_SPLIT_BOTTOM:
			hideSplitBottomRight(MODE_HIDE_SPLIT_BOTTOM);
			break;
		}

		// just for sanity
		validateParent();
	}

    /**
     * Determines type of hide spilt to perform.
     * @param cytoPanel used to determine type of hide to perform
     * @return MODE_HIDE_SPLIT_LEFT or MODE_HIDE_SPLIT_RIGHT or MODE_HIDE_SPLIT_BOTTOM MODE_HIDE_SPLIT_TOP
     */
    private int getHideType() {
		// top bottom
		if (getOrientation() == VERTICAL_SPLIT){
			return (cytoPanel == getTopComponent()) ? MODE_HIDE_SPLIT_TOP : MODE_HIDE_SPLIT_BOTTOM;
		}
		// left right
		else{
			return (cytoPanel == getLeftComponent()) ? MODE_HIDE_SPLIT_LEFT : MODE_HIDE_SPLIT_RIGHT;
		}
	}

    /**
     * Hides the Split (tabs on left or top)
     * @param cytoPanel used to get tab dimensions
	 * @param hideMode left or top (width or height)
     */
    private void hideSplitTopLeft(int hideMode){

		// get max width or max height of tabs on cytopanel
		int tabSize = (hideMode == MODE_HIDE_SPLIT_LEFT) ?
			cytoPanel.getMaxWidthTabs() : cytoPanel.getMaxHeightTabs();

		// compute divider location
		int newDividerLocation = tabSize + defaultDividerSize;

		// move split pane divider all the way to left or top without hiding tabs
		setDividerLocation(newDividerLocation);
        setDividerSize(0);
	}

    /**
     * Hides the Split (tabs on right or bottom)
     * @param cytoPanel used to get tab dimensions
	 * @param hideMode bottom or right (height or width)
     */
    private void hideSplitBottomRight(int hideMode){

		// compute divider location
		Dimension d = getSize();
		int newDividerLocation;

		// are we preserving tab width or height ?
		if (hideMode == MODE_HIDE_SPLIT_RIGHT) {
			newDividerLocation = (int)d.getWidth() - cytoPanel.getMaxWidthTabs() - defaultDividerSize;
		}
		else{
			newDividerLocation = (int)d.getHeight() - cytoPanel.getMaxHeightTabs() - defaultDividerSize;
		}

		// move splitpane divider all the way to bottom or right without hiding tabs
		setDividerLocation(newDividerLocation);
        setDividerSize(0);
	}

    /**
     * Validates the Parent Container.
     */
    private void validateParent() {
        Container container = this.getParent();
        if (container != null) {
            container.validate();
        }
    }

    /**
     * Add a component listener to the app frame to get windows resize events.
     */
    private void addComponentListener() {
        frame.addComponentListener(new ComponentAdapter() {

            /**
             * Frame is resized.
             *
             * @param e Component Event.
             */
            public void componentResized(ComponentEvent e) {
				if (currentMode == MODE_HIDE_SPLIT){
					hideSplit(true);
				}
            }
        });
    }
}
