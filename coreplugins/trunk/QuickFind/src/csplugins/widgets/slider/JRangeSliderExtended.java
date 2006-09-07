package csplugins.widgets.slider;

import prefuse.util.ui.JRangeSlider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Extension of the Prefuse JRangeSlider.
 *
 * @see prefuse.util.ui.JRangeSlider
 * @author Ethan Cerami.
 */
public class JRangeSliderExtended extends JRangeSlider {
    private Dimension preferredSize;

    /**
     * Create a new range slider.
     *
     * @param model - a BoundedRangeModel specifying the slider's range
     * @param orientation - construct a horizontal or vertical slider?
     * @param direction - Is the slider left-to-right/top-to-bottom or
     * right-to-left/bottom-to-top
     */
    public JRangeSliderExtended (BoundedRangeModel model, int orientation,
            int direction) {
        super (model, orientation, direction);
    }

    /**
     * Overrides default preferred size of JRangeSlider.
     * <P>JRangeSlider is hard-coded to always be 300 px wide, and that is
     * a bit constrained.
     * @return Preferred Dimension.
     */
    public Dimension getPreferredSize() {
        if (preferredSize == null) {
            return super.getPreferredSize();
        } else {
            return preferredSize;
        }
    }

    /**
     * Sets the preferred size of the component.
     * @param preferredSize Preferred Size.
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    /**
     * Place-holder.
     * @param mouseEvent Mouse Event Object.
     */
    public void mouseReleased(MouseEvent mouseEvent) {
        super.mouseReleased(mouseEvent);
    }
}