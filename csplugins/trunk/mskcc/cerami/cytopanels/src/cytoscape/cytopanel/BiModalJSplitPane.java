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
 * @author Ethan Cerami.
 */
public class BiModalJSplitPane extends JSplitPane {
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
     * The Current Mode Value.
     */
    private int currentMode;

    /**
     * The Default Divider Size, as this may vary by platform.
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
    public BiModalJSplitPane(int orientation, int initialMode,
            Component leftComponent, Component rightComponent) {
        super(orientation, leftComponent, rightComponent);
        currentMode = initialMode;

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
                hideSplit();
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
     * Shows the Split.
     */
    private void showSplit() {
        setDividerSize(defaultDividerSize);
        resetToPreferredSizes();
        validateParent();
    }

    /**
     * Hides the Split.
     */
    private void hideSplit() {
        setDividerSize(0);
        resetToPreferredSizes();
        validateParent();
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