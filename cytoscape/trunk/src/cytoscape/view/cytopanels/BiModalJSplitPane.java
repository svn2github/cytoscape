/*============================================================================*/

$Id$

/*============================================================================*/

// our package
package cytoscape.view.cytopanel;

// imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

/**
 * The BiModalJSplitPane class extends JSplitPane to provide two modes:
 * <UL>
 * <LI>MODE_SHOW_SPLIT:  The split in the split pane appears as it normally
 *                       would, and the user can resize the split pane as
 *                       needed.
 * <LI>MODE_HIDE_SPLIT:  The split in the split pane is hidden, and the
 *                       user cannot resize the split pane.
 * </UL>
 *
 * BIModalJSplitPane also implements the CytoPanelContainer interface.
 *
 * @author Ethan Cerami, Ben Gross 
 */
public class BiModalJSplitPane extends JSplitPane implements CytoPanelContainer {

    /**
     * Reference application frame.
     */
    private JFrame frame;

    /**
     * Available modes of the BiModalJSplitPane.
     */
    public static final int MODE_SHOW_SPLIT = 1;
    public static final int MODE_HIDE_SPLIT = 2;

    /**
     * Property listener modes.
     */
    public static final String MODE_PROPERTY = "MODE_PROPERTY";

    /**
     * The current mode.
     */
    private int currentMode;

    /**
     * The default divider size.
     */
    private int defaultDividerSize;

    /**
     * Constructor.
     *
     * @param orientation    JSplitPane Orientation.
     *                       JSplitPane.HORIZONTAL_SPLIT or
     *                       JSplitPane.VERTICAL_SPLIT.
     * @param initialMode    Initial Mode.
     *                       MODE_SHOW_SPLIT or
     *                       MODE_HIDE_SPLIT.
     * @param leftComponent  Left/Top Component.
     * @param rightComponent Right/Bottom Component.
     */
    public BiModalJSplitPane(JFrame f, int orientation, int initialMode,
            Component leftComponent, Component rightComponent) {
        super(orientation, leftComponent, rightComponent);

		// init some member vars
        currentMode = initialMode;
		frame = f;

        //  remove the border
        setBorder(null);
        setOneTouchExpandable(false);

        //  store the default divider size
        defaultDividerSize = this.getDividerSize();

        //  hide split
        if (initialMode == MODE_HIDE_SPLIT) {
            this.setDividerSize(0);
        }
    }

    /**
     * Inserts CytoPanel at desired compass direction.
     *
     * @param cytoPanel        CytoPanel reference.
     * @param compassDirection SwingConstants integer value.
     */
	public void insertCytoPanel(CytoPanelImp cytoPanel, int compassDirection) {
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
     * Gets the location of the applications mainframe.
     *
     * @return Point object.
     */
    public Point getLocationOnScreen() {
		return frame.getLocationOnScreen();
	}

    /**
     * Gets the bounds of the applications mainframe.
     *
     * @return Rectangle Object.
     */
    public Rectangle getBounds() {
		return frame.getBounds();
	}

    /**
     * Sets the BiModalJSplitframe mode.
	 *
     * @param newMode MODE_SHOW_SPLIT or MODE_HIDE_SPLIT.
     */
    public void setMode(int newMode) {

        //  check args
        if (newMode != MODE_SHOW_SPLIT && newMode != MODE_HIDE_SPLIT) {
            throw new IllegalArgumentException("Illegal Argument:  "
                    + newMode + ".  Must be one of:  MODE_SHOW_SPLIT or "
                    + " MODE_HIDE_SPLIT.");
        }
        int oldMode = currentMode;

        //  only process if the mode has changed
        if (newMode != currentMode) {
            if (newMode == MODE_HIDE_SPLIT) {
                hideSplit();
            } else if (newMode == MODE_SHOW_SPLIT) {
                showSplit();
            }
            this.currentMode = newMode;

            //  fire a property change
            this.firePropertyChange(MODE_PROPERTY, oldMode, newMode);
        }
    }

    /**
     * Gets the current mode.
	 *
     * @return MODE_SHOW_SPLIT or MODE_HIDE_SPLIT.
     */
    public int getMode() {
        return currentMode;
    }

    /**
     * Shows the split.
     */
    private void showSplit() {
        setDividerSize(defaultDividerSize);
        resetToPreferredSizes();
        validateParent();
    }

    /**
     * Hides the split.
     */
    private void hideSplit() {
        setDividerSize(0);
        resetToPreferredSizes();
        validateParent();
    }

    /**
     * Validates the parent container.
     */
    private void validateParent() {
        Container container = this.getParent();
        if (container != null) {
            container.validate();
        }
    }
}
