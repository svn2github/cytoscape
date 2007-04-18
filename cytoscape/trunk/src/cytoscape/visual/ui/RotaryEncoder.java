package cytoscape.visual.ui;

import cytoscape.visual.ui.ContinuousTrackRenderer.CMouseListener;
import cytoscape.visual.ui.ContinuousTrackRenderer.CMouseMotionListener;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.TrackRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Arc2D;

import java.util.HashMap;

import javax.swing.JComponent;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class RotaryEncoder extends JComponent
    implements TrackRenderer {
    private JXMultiThumbSlider slider;
    private RMouseMotionListener listener = null;
    private double location = 240;

    /**
     * DOCUMENT ME!
     *
     * @param slider DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JComponent getRendererComponent(JXMultiThumbSlider slider) {
        this.slider = slider;

        if (listener == null) {
            listener = new RMouseMotionListener();
            this.slider.addMouseWheelListener(listener);

            //this.slider.addMouseMotionListener(new CMouseMotionListener());
        }

        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void paint(Graphics g) {
        super.paint(g);
        paintComponent(g);
    }

    protected void paintComponent(Graphics gfx) {
        // AA on
        Graphics2D g = (Graphics2D) gfx;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(2f));
        g.setColor(Color.DARK_GRAY);

        g.draw(new Arc2D.Double(5, 50, 20, 20, 300, 300, Arc2D.OPEN));
        g.draw(new Arc2D.Double(5, 50, 20, 20, location, 0, Arc2D.PIE));
    }

    class RMouseMotionListener
        implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            location = location + (e.getWheelRotation() * 10);
            slider.repaint();
            repaint();
        }
    }
}
