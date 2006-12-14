package csplugins.widgets.slider;

import prefuse.data.query.NumberRangeModel;
import prefuse.util.ui.JRangeSlider;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * Extension of the Prefuse JRangeSlider.
 *
 * @author Ethan Cerami.
 * @see prefuse.util.ui.JRangeSlider
 */
public class JRangeSliderExtended extends JRangeSlider
        implements ChangeListener {
    private Dimension preferredSize;
    private Popup popup;
    private JLabel popupLow;
    private JLabel popupHigh;
    private PopupDaemon popupDaemon;

    /**
     * Create a new range slider.
     *
     * @param model       - a BoundedRangeModel specifying the slider's range
     * @param orientation - construct a horizontal or vertical slider?
     * @param direction   - Is the slider left-to-right/top-to-bottom or
     *                    right-to-left/bottom-to-top
     */
    public JRangeSliderExtended(BoundedRangeModel model, int orientation,
            int direction) {
        super(model, orientation, direction);
        addChangeListener(this);
    }

    /**
     * Overrides default preferred size of JRangeSlider.
     * <P>JRangeSlider is hard-coded to always be 300 px wide, and that is
     * a bit constrained.
     *
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
     *
     * @param preferredSize Preferred Size.
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    /**
     * Placeholder.
     *
     * @param mouseEvent Mouse Event Object.
     */
    public void mouseReleased(MouseEvent mouseEvent) {
        super.mouseReleased(mouseEvent);
    }

    /**
     * Resets / hides Popup window.
     */
    void resetPopup() {
        if (popup != null) {
            popup.hide();
        }
        this.popup = null;
    }

    /**
     * Upon state change, pop-up a tiny window with low-high.
     *
     * @param e ChangeEvent Object.
     */
    public void stateChanged(ChangeEvent e) {
        NumberRangeModel model = (NumberRangeModel) getModel();
        Number low = (Number) model.getLowValue();
        Number high = (Number) model.getHighValue();
        Number min = (Number) model.getMinValue();
        Number max = (Number) model.getMaxValue();

        DecimalFormat format;
        if (max.doubleValue() - min.doubleValue() < .001) {
            format = new DecimalFormat("0.###E0");
        } else if (max.doubleValue() - min.doubleValue() > 100000) {
            format = new DecimalFormat("0.###E0");
        } else {
            format = new DecimalFormat("###,###.000");
        }
        String lowStr = format.format(low);
        String highStr = format.format(high);
        if (isVisible()) {
            if (popup == null) {
                PopupFactory popupFactory = PopupFactory.getSharedInstance();
                JPanel panel = new JPanel();
                panel.setBorder(new LineBorder (Color.LIGHT_GRAY, 1));
                panel.setPreferredSize(getPreferredSize());
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                popupLow = new JLabel(lowStr);
                popupLow.setBorder(new EmptyBorder(6, 2, 6, 2));
                popupHigh = new JLabel(highStr);
                popupHigh.setBorder(new EmptyBorder(6, 2, 6, 2));
                panel.add(popupLow);
                panel.add(Box.createHorizontalGlue());
                panel.add(popupHigh);
                popup = popupFactory.getPopup(this, panel,
                        getLocationOnScreen().x,
                        getLocationOnScreen().y
                                + getPreferredSize().height + 2);
                popupDaemon = new PopupDaemon(this, 1000);
                popup.show();
            } else {
                popupLow.setText(lowStr);
                popupHigh.setText(highStr);
                popupDaemon.restart();
            }
        }
    }
}

/**
 * Daemon Thread to automatically hide Pop-up Window after xxx milliseconds.
 *
 * @author Ethan Cerami
 */
class PopupDaemon implements ActionListener {
    private Timer timer;
    private JRangeSliderExtended slider;

    /**
     * Constructor.
     *
     * @param slider JRangeSliderExtended Object.
     * @param delay  Delay until pop-up window is hidden.
     */
    public PopupDaemon(JRangeSliderExtended slider, int delay) {
        timer = new Timer(delay, this);
        timer.setRepeats(false);
        this.slider = slider;
    }

    /**
     * Restart timer.
     */
    public void restart() {
        timer.restart();
    }

    /**
     * Timer Event:  Hide popup now.
     *
     * @param e ActionEvent Object.
     */
    public void actionPerformed(ActionEvent e) {
        slider.resetPopup();
    }
}