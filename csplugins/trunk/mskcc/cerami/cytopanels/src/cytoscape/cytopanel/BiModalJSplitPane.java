package cytoscape.cytopanel;

import javax.swing.*;
import java.awt.*;

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
     * Indicates which startupSize to perform.
     */
	public static final int STARTUP_HIDE_RIGHT = 0;
	public static final int STARTUP_HIDE_BOTTOM = 1;

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
     * Saved dimension for left component and right components.
     */
    private Dimension topleftComponentSavedDimension;
    private Dimension bottomrightComponentSavedDimension;
	
	/**
	 * Values used to determine the type of hide split to perform
     */
	private static final int MODE_HIDE_SPLIT_LEFT = 0;
	private static final int MODE_HIDE_SPLIT_RIGHT = 1;
	private static final int MODE_HIDE_SPLIT_BOTTOM = 2;
	private static final int MODE_HIDE_SPLIT_TOP = 3;

	/**
	 * String used to compare against os.name System property - 
	 * to determine if we are running on Windows platform.
     */
	static final String WINDOWS = "windows";

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
        currentMode = initialMode;
		frame = f;

        //  Remove the Border, by Default
        setBorder(null);
        setOneTouchExpandable(false);

        //  Store the Default Divider Size, as this may vary by platform
        defaultDividerSize = this.getDividerSize();

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
    public void setMode(CytoPanel cytoPanel, int newMode) {
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
                hideSplit(cytoPanel);
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
     * Sets window sizes for startupu.
	 * @param mode HideMode
     */
    public void setStartupSizes(int mode) {

		if (mode == STARTUP_HIDE_RIGHT){
			JComponent left = (JComponent)getLeftComponent();
			JComponent right = (JComponent)getRightComponent();
			CytoPanel cp = (CytoPanel)right;
			topleftComponentSavedDimension = left.getSize();
			bottomrightComponentSavedDimension = right.getSize();
			cp.setStartupSizes(mode);
			left.setMinimumSize(new Dimension((int)(left.getSize().getWidth()+right.getSize().getWidth()+getDefaultDividerSize()), (int)getSize().getHeight()));
			left.setPreferredSize(new Dimension((int)(left.getSize().getWidth()+right.getSize().getWidth()+getDefaultDividerSize()), (int)getSize().getHeight()));
			right.setMinimumSize(new Dimension(cp.getMaxWidthTabs()+getDefaultDividerSize(), (int)right.getSize().getHeight()));
		}
		else if (mode == STARTUP_HIDE_BOTTOM){
			JComponent top = (JComponent)getTopComponent();
			JComponent bottom = (JComponent)getBottomComponent();
			CytoPanel cp = (CytoPanel)bottom;
			topleftComponentSavedDimension = top.getSize();
			bottomrightComponentSavedDimension = bottom.getSize();
			cp.setStartupSizes(mode);
			top.setMinimumSize(new Dimension((int)top.getSize().getWidth(), (int)(top.getSize().getHeight()+bottom.getSize().getHeight())));
			top.setPreferredSize(new Dimension((int)top.getSize().getWidth(), (int)(top.getSize().getHeight()+bottom.getSize().getHeight())));
			bottom.setMinimumSize(new Dimension((int)bottom.getSize().getWidth(), cp.getMaxHeightTabs()+getDefaultDividerSize()));
		}
		resetToPreferredSizes();
    }

    /**
     * Restores window sizes after startup
	 * @param mode previous HideMode
     */
    public void restoreStartupSizes(int mode) {
		if (mode == STARTUP_HIDE_RIGHT){
			JComponent left = (JComponent)getLeftComponent();
			JComponent right = (JComponent)getRightComponent();
			CytoPanel cp = (CytoPanel)right;
			left.setMinimumSize(topleftComponentSavedDimension);
			left.setPreferredSize(topleftComponentSavedDimension);
			right.setMinimumSize(bottomrightComponentSavedDimension);
			cp.restoreStartupSizes();
		}
		else if (mode == STARTUP_HIDE_BOTTOM){
			JComponent top = (JComponent)getTopComponent();
			JComponent bottom = (JComponent)getBottomComponent();
			CytoPanel cp = (CytoPanel)bottom;
			top.setMinimumSize(topleftComponentSavedDimension);
			top.setPreferredSize(topleftComponentSavedDimension);
			bottom.setMinimumSize(bottomrightComponentSavedDimension);
			cp.restoreStartupSizes();
		}
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
		setDividerLocation(savedDividerLocation);
        setDividerSize(defaultDividerSize);
        validateParent();
    }

    /**
     * Hides the Split.
     * @param cytoPanel used to determine type of hide to perform
     */
    private void hideSplit(CytoPanel cytoPanel) {

		// save the current divider location before we change it
		savedDividerLocation = this.getDividerLocation();

		// determine which hide split to perform and do it
		switch (getHideType(cytoPanel)){
		case MODE_HIDE_SPLIT_LEFT:
			hideSplitTopLeft(cytoPanel, MODE_HIDE_SPLIT_LEFT);
			break;
		case MODE_HIDE_SPLIT_RIGHT:
			hideSplitBottomRight(cytoPanel, MODE_HIDE_SPLIT_RIGHT);
			break;
		case MODE_HIDE_SPLIT_TOP:
			hideSplitTopLeft(cytoPanel, MODE_HIDE_SPLIT_TOP);
			break;
		case MODE_HIDE_SPLIT_BOTTOM:
			hideSplitBottomRight(cytoPanel, MODE_HIDE_SPLIT_BOTTOM);
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
    private int getHideType(CytoPanel cytoPanel) {
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
    private void hideSplitTopLeft(CytoPanel cytoPanel, int hideMode){
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
    private void hideSplitBottomRight(CytoPanel cytoPanel, int hideMode){

		// compute divider location
		double newDividerLocation = 1.0;
		Rectangle r = this.getBounds();

		// are we preserving tab width or height ?
		if (hideMode == MODE_HIDE_SPLIT_RIGHT) {
			int maxWidth = cytoPanel.getMaxWidthTabs();
			newDividerLocation -=  (r.getWidth() <= 0) ? 0.0 : maxWidth/r.getWidth();
		}
		else{
			int maxHeight = cytoPanel.getMaxHeightTabs();
			newDividerLocation -=  (r.getHeight() <= 0) ? 0.0 : maxHeight/r.getHeight();
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
}
