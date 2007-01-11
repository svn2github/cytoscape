package ding.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class BirdsEyeView extends Component {
    private final double[] m_extents = new double[4];
    private DGraphView m_view;
    private final ContentChangeListener m_cLis;
    private final ViewportChangeListener m_vLis;
    private Image m_img = null;
    private boolean m_contentChanged = false;
    private double m_myXCenter;
    private double m_myYCenter;
    private double m_myScaleFactor;
    private int m_viewWidth;
    private int m_viewHeight;
    private double m_viewXCenter;
    private double m_viewYCenter;
    private double m_viewScaleFactor;

    /**
     * Creates a new BirdsEyeView object.
     *
     * @param view DOCUMENT ME!
     */
    public BirdsEyeView(DGraphView view) {
        super();
        m_cLis = new InnerContentChangeListener();
        m_vLis = new InnerViewportChangeListener();
        addMouseListener(new InnerMouseListener());
        addMouseMotionListener(new InnerMouseMotionListener());
        changeView(view);
    }

    /**
     * DOCUMENT ME!
     *
     * @param view DOCUMENT ME!
     */
    public void changeView(DGraphView view) {
        destroy();
        m_view = view;

        if (m_view != null) {
            m_view.addContentChangeListener(m_cLis);
            m_view.addViewportChangeListener(m_vLis);
            m_viewWidth = m_view.getComponent()
                                .getWidth();
            m_viewHeight = m_view.getComponent()
                                 .getHeight();

            final Point2D pt = m_view.getCenter();
            m_viewXCenter = pt.getX();
            m_viewYCenter = pt.getY();
            m_viewScaleFactor = m_view.getZoom();
            m_contentChanged = true;
        }

        repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void destroy() {
        if (m_view == null)
            return;

        m_view.removeContentChangeListener(m_cLis);
        m_view.removeViewportChangeListener(m_vLis);
    }

    /**
     * This used to be called reshape, which is deprecated, so I've changed
     * it to setBounds.  Not sure if this will break anything!
     *
     * @param x 
     * @param y 
     * @param width 
     * @param height 
     */
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        if ((width > 0) && (height > 0))
            m_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        m_contentChanged = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void update(Graphics g) {
        if (m_img == null)
            return;

        if (m_view == null) {
            g.setColor(Color.white);
            g.fillRect(
                0,
                0,
                getWidth(),
                getHeight());

            return;
        }

        if (m_contentChanged) {
            if (m_view.getExtents(m_extents)) {
                m_myXCenter = (m_extents[0] + m_extents[2]) / 2.0d;
                m_myYCenter = (m_extents[1] + m_extents[3]) / 2.0d;
                m_myScaleFactor = 0.8d * Math.min(((double) getWidth()) / (m_extents[2] -
                        m_extents[0]),
                        ((double) getHeight()) / (m_extents[3] - m_extents[1]));
            } else {
                m_myXCenter = 0.0d;
                m_myYCenter = 0.0d;
                m_myScaleFactor = 1.0d;
            }
            
            m_view.drawSnapshot(
                m_img,
                m_view.getGraphLOD(),
                m_view.m_backgroundCanvas.getBackground(),
                m_myXCenter,
                m_myYCenter,
                m_myScaleFactor);
            m_contentChanged = false;
        }
        
        g.drawImage(m_img, 0, 0, null);

        final double rectWidth = m_myScaleFactor * (((double) m_viewWidth) / m_viewScaleFactor);
        final double rectHeight = m_myScaleFactor * (((double) m_viewHeight) / m_viewScaleFactor);
        final double rectXCenter = (((double) getWidth()) / 2.0d) +
            (m_myScaleFactor * (m_viewXCenter - m_myXCenter));
        final double rectYCenter = (((double) getHeight()) / 2.0d) +
            (m_myScaleFactor * (m_viewYCenter - m_myYCenter));
        final Rectangle2D rect = new Rectangle2D.Double(rectXCenter -
                (rectWidth / 2), rectYCenter - (rectHeight / 2), rectWidth,
                rectHeight);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(63, 63, 255, 63));
        g2.fill(rect);
        g2.setColor(Color.blue);
        g2.draw(rect);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void paint(Graphics g) {
        update(g);
    }

    private final class InnerContentChangeListener
        implements ContentChangeListener {
        public void contentChanged() {
            m_contentChanged = true;
            repaint();
        }
    }

    private final class InnerViewportChangeListener
        implements ViewportChangeListener {
        public void viewportChanged(int w, int h, double newXCenter,
            double newYCenter, double newScaleFactor) {
            m_viewWidth = w;
            m_viewHeight = h;
            m_viewXCenter = newXCenter;
            m_viewYCenter = newYCenter;
            m_viewScaleFactor = newScaleFactor;
            repaint();
        }
    }

    private int m_currMouseButton = 0;
    private int m_lastXMousePos = 0;
    private int m_lastYMousePos = 0;

    private final class InnerMouseListener
        implements MouseListener {
        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                m_currMouseButton = 1;
                m_lastXMousePos = e.getX();
                m_lastYMousePos = e.getY();
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (m_currMouseButton == 1)
                    m_currMouseButton = 0;
            }
        }
    }

    private final class InnerMouseMotionListener
        implements MouseMotionListener {
        public void mouseDragged(MouseEvent e) {
            if (m_currMouseButton == 1) {
                final int currX = e.getX();
                final int currY = e.getY();
                final double deltaX = (currX - m_lastXMousePos) / m_myScaleFactor;
                final double deltaY = (currY - m_lastYMousePos) / m_myScaleFactor;
                m_lastXMousePos = currX;
                m_lastYMousePos = currY;

                if (m_view != null) {
                    final Point2D pt = m_view.getCenter();
                    m_view.setCenter(pt.getX() + deltaX, pt.getY() + deltaY);
                }
            }
        }

        public void mouseMoved(MouseEvent e) {
        }
    }
}
