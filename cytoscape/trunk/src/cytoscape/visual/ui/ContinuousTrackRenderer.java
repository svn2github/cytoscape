package cytoscape.visual.ui;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.Thumb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class ContinuousTrackRenderer extends JComponent
    implements VizMapperTrackRenderer {
    /*
     * Constants for diagram.
     */
    private int TRACK_HEIGHT = 120;
    private static final int THUMB_WIDTH = 12;
    private final Font smallFont = new Font("SansSerif", Font.BOLD, 9);
    private final Font defFont = new Font("SansSerif", Font.BOLD, 12);
    private final Font largeFont = new Font("SansSerif", Font.BOLD, 18);
    private static final Color VALUE_AREA_COLOR = new Color(0, 80, 255, 80);
    private static final int V_PADDING = 20;
    private int ARROW_BAR_Y_POSITION = TRACK_HEIGHT + 50;
    private static final String TITLE1 = "Mapping: ";
    private Map<Integer, Double> valueMap;

    /*
     * Define Colors used in this diagram.
     */
    private static final Color BORDER_COLOR = Color.DARK_GRAY;
    private double valueRange;
    private double minValue;
    private double maxValue;
    private float min = 0;
    private float max = 0;
    private boolean clickFlag = false;
    private Point curPoint;
    private JXMultiThumbSlider slider;
    private CMouseListener listener = null;
    private Map<Integer, Point> verticesList;
    private int selectedIdx;
    private Point dragOrigin;

    /**
     * Creates a new ContinuousTrackRenderer object.
     *
     * @param minValue DOCUMENT ME!
     * @param maxValue DOCUMENT ME!
     */
    public ContinuousTrackRenderer(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;

        valueRange = Math.abs(maxValue - minValue);

        // verticesList = new ArrayList<Point>();
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
        TRACK_HEIGHT = slider.getHeight() - 100;
        ARROW_BAR_Y_POSITION = TRACK_HEIGHT + 50;

        // AA on
        Graphics2D g = (Graphics2D) gfx;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        // get the list of tumbs
        List<Thumb> stops = slider.getModel()
                                  .getSortedThumbs();

        // verticesList.clear();
        Point listP = new Point();

        int numPoints = stops.size();

        System.out.println("###List len start = " + verticesList.size() +
            ", Stops = " + numPoints);

        // set up the data for the gradient
        float[] fractions = new float[numPoints];
        Float[] floatProperty = new Float[numPoints];
        int i = 0;

        for (Thumb thumb : stops) {
            floatProperty[i] = (Float) thumb.getObject();
            fractions[i] = thumb.getPosition();

            if (min >= floatProperty[i])
                min = floatProperty[i];

            if (max <= floatProperty[i])
                max = floatProperty[i];

            i++;
        }

        int track_width = slider.getWidth() - (THUMB_WIDTH / 2);

        g.translate(THUMB_WIDTH / 2, 12);

        int newX = 0;

        // Line2D segment = new Line2D.Float();
        Rectangle2D rect1 = new Rectangle(0, 0, track_width, 5);
        int lastY = 0;

        g.setStroke(new BasicStroke(1.0f));

        // Draw arrow bar
        g.setColor(Color.black);
        g.drawLine(0, ARROW_BAR_Y_POSITION, track_width, ARROW_BAR_Y_POSITION);

        Polygon arrow = new Polygon();
        arrow.addPoint(track_width, ARROW_BAR_Y_POSITION);
        arrow.addPoint(track_width - 20, ARROW_BAR_Y_POSITION - 8);
        arrow.addPoint(track_width - 20, ARROW_BAR_Y_POSITION);
        g.fill(arrow);

        g.setColor(Color.gray);
        g.drawLine(0, ARROW_BAR_Y_POSITION, 15, ARROW_BAR_Y_POSITION - 30);
        g.drawLine(15, ARROW_BAR_Y_POSITION - 30, 25, ARROW_BAR_Y_POSITION -
            30);

        g.setFont(defFont);
        g.drawString("Min=" + minValue, 28, ARROW_BAR_Y_POSITION - 25);

        g.drawLine(track_width, ARROW_BAR_Y_POSITION, track_width - 15,
            ARROW_BAR_Y_POSITION + 30);
        g.drawLine(track_width - 15, ARROW_BAR_Y_POSITION + 30,
            track_width - 25, ARROW_BAR_Y_POSITION + 30);
        g.drawString("Max=" + maxValue, track_width - 85,
            ARROW_BAR_Y_POSITION + 35);

        Rectangle2D rect2;
        Polygon valueArea;

        Point2D p1 = new Point2D.Float(0, 5);
        Point2D p2 = new Point2D.Float(0, 5);
        g.setStroke(new BasicStroke(1.0f));

        /*
         * Draw background
         */
        g.setColor(Color.white);
        // g.setStroke(new BasicStroke(4.0f));
        g.fillRect(0, 5, track_width - 2, TRACK_HEIGHT);

        for (i = 0; i < floatProperty.length; i++) {
            newX = (int) (track_width * (fractions[i] / 100)) - (i / 2) - 2;

            p2.setLocation(newX, 5);
            // g.setColor(Color.black);
            // g.setStroke(new BasicStroke(1.0f));
            // rect1 = new Rectangle((int) p1.getX(), 5, newX, TRACK_HEIGHT);
            //
            // g.draw(rect1);

            // g.setColor(new Color(255, 255, 255, 100));
            g.setColor(Color.white);
            rect2 = new Rectangle((int) p1.getX() + 1, 6, newX - 1,
                    TRACK_HEIGHT - 1);
            g.fill(rect2);

            g.setColor(Color.blue);

            int newY = (5 + TRACK_HEIGHT) -
                (int) ((floatProperty[i].intValue() / max) * TRACK_HEIGHT);

            valueArea = new Polygon();

            if (i == 0) {
                // g.drawLine((int) p1.getX(), newY, newX, newY);
                g.setColor(VALUE_AREA_COLOR);
                valueArea.addPoint((int) p1.getX(), newY);
                valueArea.addPoint(newX, newY);
                valueArea.addPoint(newX, TRACK_HEIGHT + 5);
                valueArea.addPoint((int) p1.getX(), TRACK_HEIGHT + 5);
                g.fill(valueArea);
            } else {
                // g.drawLine((int) p1.getX(), lastY, newX, newY);
                g.setColor(VALUE_AREA_COLOR);
                valueArea.addPoint((int) p1.getX(), lastY);
                valueArea.addPoint(newX, newY);
                valueArea.addPoint(newX, TRACK_HEIGHT + 5);
                valueArea.addPoint((int) p1.getX(), TRACK_HEIGHT + 5);
                g.fill(valueArea);
            }

            for (int j = 0; j < stops.size(); j++) {
                if (slider.getModel()
                              .getThumbAt(j)
                              .getObject() == floatProperty[i]) {
                    //					boolean flag = false;
                    //					for(Integer key: verticesList.keySet()) {
                    //						
                    //						if(verticesList.get(key).x == newX && verticesList.get(key).y == newY ) {
                    //							flag = true;
                    //							System.out.println("FOUND!");
                    //							break;
                    //						}
                    //					}
                    //					if(flag == false) {
                    //						verticesList.put(j, new Point(newX, newY));
                    //					}
                    Point newPoint = new Point(newX, newY);

                    if (verticesList.containsValue(newPoint) == false)
                        verticesList.put(
                            j,
                            new Point(newX, newY));

                    break;
                }
            }

            lastY = newY;

            g.setColor(Color.black);
            g.setStroke(new BasicStroke(1.0f));
            g.setFont(smallFont);

            int numberWidth = SwingUtilities.computeStringWidth(
                    g.getFontMetrics(),
                    floatProperty[i].toString());

            g.setColor(Color.DARK_GRAY);

            if (fractions[i] < 10) {
                g.drawLine(newX, newY, newX + 15, newY - 35);
                g.drawString(
                    floatProperty[i].toString(),
                    newX + numberWidth,
                    newY - 48);
            } else {
                g.drawLine(newX, newY, newX - 15, newY + 35);
                g.drawString(
                    floatProperty[i].toString(),
                    newX - (numberWidth + 5),
                    newY + 48);
            }

            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));

            Float curPositionValue = ((Double) (((fractions[i] / 100) * valueRange) -
                Math.abs(minValue))).floatValue();
            String valueString = String.format("%4.4f", curPositionValue);

            int flipLimit = 90;
            int borderVal = track_width - newX;

            if (((i % 2) == 0) && (flipLimit < borderVal)) {
                g.drawLine(newX, ARROW_BAR_Y_POSITION, newX + 20,
                    ARROW_BAR_Y_POSITION - 15);
                g.drawLine(newX + 20, ARROW_BAR_Y_POSITION - 15, newX + 30,
                    ARROW_BAR_Y_POSITION - 15);
                g.setColor(Color.black);
                g.drawString(valueString, newX + 33, ARROW_BAR_Y_POSITION - 11);
            } else if (((i % 2) == 1) && (flipLimit < borderVal)) {
                g.drawLine(newX, ARROW_BAR_Y_POSITION, newX + 20,
                    ARROW_BAR_Y_POSITION + 15);
                g.drawLine(newX + 20, ARROW_BAR_Y_POSITION + 15, newX + 30,
                    ARROW_BAR_Y_POSITION + 15);
                g.setColor(Color.black);
                g.drawString(valueString, newX + 33, ARROW_BAR_Y_POSITION + 19);
            } else if (((i % 2) == 0) && (flipLimit >= borderVal)) {
                g.drawLine(newX, ARROW_BAR_Y_POSITION, newX - 20,
                    ARROW_BAR_Y_POSITION - 15);
                g.drawLine(newX - 20, ARROW_BAR_Y_POSITION - 15, newX - 30,
                    ARROW_BAR_Y_POSITION - 15);
                g.setColor(Color.black);
                g.drawString(valueString, newX - 90, ARROW_BAR_Y_POSITION - 11);
            } else {
                g.drawLine(newX, ARROW_BAR_Y_POSITION, newX - 20,
                    ARROW_BAR_Y_POSITION + 15);
                g.drawLine(newX - 20, ARROW_BAR_Y_POSITION + 15, newX - 30,
                    ARROW_BAR_Y_POSITION + 15);
                g.setColor(Color.black);
                g.drawString(valueString, newX - 90, ARROW_BAR_Y_POSITION + 19);
            }

            g.setColor(Color.black);
            g.fillOval(newX - 3, ARROW_BAR_Y_POSITION - 3, 6, 6);

            // g.setColor(Color.red);
            // g.fillOval(newX - 5, TRACK_HEIGHT + 60 - 5, 10, 10);
            // g.setFont(new Font("SansSerif", Font.BOLD, 12));
            //
            // g
            // .drawString(
            // Float
            // .toString(((Double) (((fractions[i] / 100) * valueRange) - Math
            // .abs(minValue))).floatValue()),
            // newX - 10, TRACK_HEIGHT + 78);
            p1.setLocation(p2);
        }

        p2.setLocation(track_width, 5);

        // rect1 = new Rectangle((int) p1.getX(), 5, track_width
        // - (THUMB_WIDTH / 2), TRACK_HEIGHT);
        // // segment.setLine(p1, p2);
        // g.setStroke(new BasicStroke(1.0f));
        // g.setColor(Color.black);
        // g.draw(rect1);
        g.setColor(Color.white);
        rect2 = new Rectangle((int) p1.getX() + 1, 6,
                (track_width - (THUMB_WIDTH / 2)) - 5, TRACK_HEIGHT - 1);
        g.fill(rect2);

        g.setColor(VALUE_AREA_COLOR);
        valueArea = new Polygon();
        valueArea.addPoint((int) p1.getX(), lastY);
        valueArea.addPoint(track_width, lastY);
        valueArea.addPoint(track_width, TRACK_HEIGHT + 5);
        valueArea.addPoint((int) p1.getX(), TRACK_HEIGHT + 5);
        // g.drawLine((int) p1.getX(), lastY, track_width, lastY);
        // g.setColor(Color.black);
        g.fill(valueArea);

        /*
         * Finally, draw border line (rectangle)
         */
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(4.0f));
        g.drawRect(0, 5, track_width - 2, TRACK_HEIGHT);

        g.setColor(Color.red);
        g.setStroke(new BasicStroke(1.5f));

        System.out.println("Check new len: " + verticesList.size());

        for (Integer key : verticesList.keySet()) {
            Point p = verticesList.get(key);

            if (clickFlag) {
                int diffX = Math.abs(p.x - (curPoint.x - 6));
                int diffY = Math.abs(p.y - (curPoint.y - 12));

                if (((diffX < 6) && (diffY < 6)) || (key == selectedIdx)) {
                    g.setColor(Color.green);
                    g.setStroke(new BasicStroke(2.5f));
                } else {
                    g.setColor(Color.red);
                    g.setStroke(new BasicStroke(1.5f));
                }
            }

            System.out.println("---Drawing = " + key + ", " + p.x + "---" +
                p.y);
            g.drawRect(p.x - 5, p.y - 5, 10, 10);
        }

        System.out.println("====================================\n");

        // if (clickFlag) {
        // g.setColor(Color.black);
        // g.setStroke(new BasicStroke(2.5f));
        // g.fillRect(curPoint.x - 6 - 5, curPoint.y - 12 - 5, 10, 10);
        // }

        // for(Object t : slider.getModel().getSortedThumbs()) {
        // Float fval = (Float) slider.getModel().getThumbAt(count).getObject();
        // int yposition = (5 + TRACK_HEIGHT)
        // - (int) ((fval.intValue() / max) * TRACK_HEIGHT);
        // g.drawLine(0, yposition, track_width, yposition);
        // count++;
        // System.out.println("Ypos = " + yposition);
        //		
        // }

        // if(slider.getSelectedThumb() != null) {
        //			
        // Float fval = (Float)
        // slider.getModel().getThumbAt(slider.getSelectedIndex()).getObject();
        //			
        // int yposition = (5 + TRACK_HEIGHT)
        // - (int) ((fval.intValue() / max) * TRACK_HEIGHT);
        // g.fillRect(slider.getSelectedThumb().getX()-5,
        // yposition-5, 10, 10);
        // }
        AffineTransform af = new AffineTransform();
        af.setToTranslation(40.0d, 30.0d);

        // final Map<Byte, Shape> shapeMap = GraphGraphics.getNodeShapes();
        // int test = 1;
        //
        // System.out.println("********** Shape Size = " + shapeMap.size());
        //
        // for (Byte key : shapeMap.keySet()) {
        // System.out.println("********** Shape " + shapeMap.get(key));
        // g.draw(shapeMap.get(key));
        // test++;
        // }
        g.translate(-THUMB_WIDTH / 2, -12);
    }

    protected void removePoint(int i) {
        System.out.println("Deleting: " + i);
        verticesList.remove(i);
        System.out.println("---List len = " + verticesList.size());
        slider.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Double getSelectedThumbValue() {
        final float position = slider.getModel()
                                     .getThumbAt(slider.getSelectedIndex())
                                     .getPosition();
        final double thumbVal = (((position / 100) * valueRange) -
            Math.abs(minValue));

        return thumbVal;
    }

    /**
     * DOCUMENT ME!
     *
     * @param slider DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JComponent getRendererComponent(JXMultiThumbSlider slider) {
        this.slider = slider;
        // this.slider.addMouseWheelListener(new MouseWheelListener() {
        //
        // public void mouseWheelMoved(MouseWheelEvent e) {
        // // TODO Auto-generated method stub
        System.out.println("rc called!: " + slider.getModel().getThumbCount());

        //			
        // });
        if (listener == null) {
            listener = new CMouseListener();
            this.slider.addMouseListener(listener);
            this.slider.addMouseMotionListener(new CMouseMotionListener());
        }

        if (verticesList == null)
            verticesList = new HashMap<Integer, Point>();

        if (valueMap == null)
            valueMap = new HashMap<Integer, Double>();

        return this;
    }

    protected List getRanges() {
        List range = new ArrayList();

        return range;
    }

    protected String getToolTipForCurrentPosition() {
        return "AAAAAAAAA";
    }

    /**
     * DOCUMENT ME!
     *
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getToolTipForCurrentLocation(int x, int y) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getObjectInRange(int x, int y) {
        // TODO Auto-generated method stub
        return null;
    }

    private void fractionlocation() {
    }

    class CMouseMotionListener
        implements MouseMotionListener {
        public void mouseDragged(MouseEvent e) {
            curPoint = e.getPoint();

            /*
             * If beyond the bottom lin
             */
            if (clickFlag == true) {
                Thumb selectedThumb = slider.getModel()
                                            .getThumbAt(selectedIdx);

                if (curPoint.getY() >= (TRACK_HEIGHT + 5)) {
                    selectedThumb.setObject(0f);

                    return;
                }

                Float oldVal = (Float) selectedThumb.getObject();
                double curY = curPoint.getY();
                float fraction = (float) (curY / (TRACK_HEIGHT + THUMB_WIDTH));
                float newY = (float) ((((TRACK_HEIGHT + 5) - curY) * max) / (TRACK_HEIGHT +
                    5));

                selectedThumb.setObject(newY);
            }

            dragOrigin = e.getPoint();
            slider.repaint();
        }

        public void mouseMoved(MouseEvent arg0) {
            // TODO Auto-generated method stub
        }
    }

    class CMouseListener
        implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            if (isPointerInSquare(e) && (e.getClickCount() == 2)) {
                final String val = JOptionPane.showInputDialog(slider,
                        "Please type new value for this pivot.");
                final Float newVal = Float.valueOf(val);
                slider.getModel()
                      .getThumbAt(selectedIdx)
                      .setObject(newVal);

                updateMax();
                repaint();
                slider.repaint();
                repaint();
            }
        }

        public void mouseEntered(MouseEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void mouseExited(MouseEvent arg0) {
            // TODO Auto-generated method stub
        }

        public void mousePressed(MouseEvent e) {
            curPoint = e.getPoint();
            dragOrigin = e.getPoint();

            for (Integer key : verticesList.keySet()) {
                Point p = verticesList.get(key);
                int diffY = Math.abs((p.y + 12) - curPoint.y);
                int diffX = Math.abs((p.x + (THUMB_WIDTH / 2)) - curPoint.x);

                if ((diffX < 6) && (diffY < 6)) {
                    selectedIdx = key;
                    clickFlag = true;
                }
            }
        }

        public void mouseReleased(MouseEvent arg0) {
            clickFlag = false;
            updateMax();

            if (slider.getSelectedThumb() == null)
                slider.repaint();

            //			
            repaint();
        }

        private boolean isPointerInSquare(MouseEvent e) {
            curPoint = e.getPoint();
            dragOrigin = e.getPoint();

            for (Integer key : verticesList.keySet()) {
                Point p = verticesList.get(key);
                int diffY = Math.abs((p.y + 12) - curPoint.y);
                int diffX = Math.abs((p.x + (THUMB_WIDTH / 2)) - curPoint.x);

                if ((diffX < 6) && (diffY < 6)) {
                    System.out.println("\n" + "\nHIT!!!!!!!!!!!!" + curPoint +
                        ", " + p);
                    selectedIdx = key;

                    return true;
                }
            }

            return false;
        }

        private void updateMax() {
            Float val;
            Float curMax = 0f;

            for (Object thumb : slider.getModel()
                                      .getSortedThumbs()) {
                val = (Float) ((Thumb) thumb).getObject();

                if (val > curMax)
                    curMax = val;
            }

            max = curMax;
            System.out.println("New Max = " + max);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param iconWidth DOCUMENT ME!
     * @param iconHeight DOCUMENT ME!
     * @param mapping DOCUMENT ME!
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static ImageIcon getTrackGraphicIcon(int iconWidth, int iconHeight,
        ContinuousMapping mapping, VisualPropertyType type) {
        final BufferedImage bi = new BufferedImage(iconWidth, iconHeight,
                BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2 = bi.createGraphics();

        // Turn Anti-alias on
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        final int leftSpace = 10;
        int trackHeight = iconHeight - 15;
        int trackWidth = iconWidth - leftSpace - 5;

        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        g2.setBackground(Color.white);

        /*
         * Draw background
         */
        g2.setColor(Color.white);
        g2.fillRect(0, 0, iconWidth, iconHeight);
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(Color.black);

        /*
         * Compute fractions from mapping
         */
        List<ContinuousMappingPoint> points = mapping.getAllPoints();
        final int pointCount = points.size();

        /*
         * If no points, just return empty rectangle.
         */
        if (pointCount == 0) {
            g2.drawRect(leftSpace, 0, trackWidth, trackHeight);

            return new ImageIcon(bi);
        }

        float[] fractions = new float[pointCount + 2];
        double[] values = new double[pointCount];

        Object[] objValues = new Object[pointCount + 2];

        objValues[0] = points.get(0)
                             .getRange().lesserValue;

        if (pointCount == 1) {
            objValues[1] = points.get(0)
                                 .getRange().equalValue;
            objValues[2] = points.get(0)
                                 .getRange().greaterValue;
        } else {
            // "Above" value
            objValues[objValues.length - 1] = points.get(points.size() - 1)
                                                    .getRange().greaterValue;

            for (int i = 0; i < pointCount; i++)
                objValues[i + 1] = points.get(i)
                                         .getRange().equalValue;
        }

        for (int i = 0; i < objValues.length; i++) {
            if ((Float)objValues[i] < min)
                min = (Float) objValues[i];

            if ((Float) objValues[i] > max)
                max = (Float) objValues[i];
        }

        final Point2D start = new Point2D.Float(10, 0);
        final Point2D end = new Point2D.Float(trackWidth, trackHeight);

        g2.setColor(Color.black);
        g2.setFont(new Font("SansSerif", Font.BOLD, 8));

        final String maxString = Double.toString(max);
        g2.drawString(maxString, 0, 8);
        g2.drawString("0", 0, trackHeight);

        int strWidth = SwingUtilities.computeStringWidth(
                g2.getFontMetrics(),
                maxString);
        trackWidth = iconWidth - strWidth - 5;
        g2.drawRect(strWidth + 1, 0, trackWidth, trackHeight);

        g2.setColor(VALUE_AREA_COLOR);

        int startX = strWidth + 1;
        g2.fillRect(strWidth + 1, 0, (int) (trackWidth * 0.05), trackHeight);
        g2.fillRect(startX + (int) (trackWidth * 0.95), 0,
            (int) (trackWidth * 0.05) + 1, trackHeight);

        // int i=1;
        //		
        // g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        // int strWidth;
        // for(ContinuousMappingPoint point: points) {
        // String p = Double.toString(point.getValue());
        // g2.setColor(Color.black);
        // strWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), p);
        // g2.drawString(p, fractions[i]*iconWidth - strWidth/2, iconHeight -7);
        // i++;
        // }
        return new ImageIcon(bi);
    }
}
