package cytoscape.visual.ui.editors.continuous;

import cytoscape.visual.ui.icon.VisualPropertyIcon;
import cytoscape.visual.NodeShape;

import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.Thumb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class DiscreteTrackRenderer extends JComponent
    implements VizMapperTrackRenderer {
    /*
     * Constants for diagram.
     */
    private static final int ICON_SIZE = VisualPropertyIcon.DEFAULT_ICON_SIZE;
    private static int TRACK_HEIGHT = 70;
    private static final int THUMB_WIDTH = 12;
    private final Font defFont = new Font("SansSerif", Font.BOLD, 12);
    private final Font largeFont = new Font("SansSerif", Font.BOLD, 18);
    private static final int V_PADDING = 20;
    private static int ARROW_BAR_Y_POSITION = TRACK_HEIGHT + 50;
    private static final String TITLE1 = "Mapping: ";

    /*
     * Define Colors used in this diagram.
     */
    private static final Color BORDER_COLOR = Color.DARK_GRAY;

    // private static final int stringPosition = TRACK_HEIGHT + 20;
    private double valueRange;
    private double minValue;
    private double maxValue;
    private ContinuousMapping cm;

    // Mainly for Icons
    private List<Object> rangeObjects;
    private Object lastObject;

    // HTML document fot tooltip text.
    private List<String> rangeTooltips;
    private JXMultiThumbSlider slider;

    /**
     * Creates a new DiscreteTrackRenderer object.
     *
     * @param minValue DOCUMENT ME!
     * @param maxValue DOCUMENT ME!
     * @param lastRegionObject DOCUMENT ME!
     * @param cm DOCUMENT ME!
     */
    public DiscreteTrackRenderer(double minValue, double maxValue,
        Object lastRegionObject, ContinuousMapping cm) {
        rangeObjects = new ArrayList<Object>();
        rangeTooltips = new ArrayList<String>();

        this.lastObject = lastRegionObject;
        this.minValue = minValue;
        this.maxValue = maxValue;

        this.cm = cm;

        valueRange = Math.abs(maxValue - minValue);

        this.setBackground(Color.white);
        this.setForeground(Color.white);
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

        // Turn AA on
        Graphics2D g = (Graphics2D) gfx;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        // get the list of tumbs
        List<Thumb> sortedThumbs = slider.getModel()
                                         .getSortedThumbs();
        int numPoints = sortedThumbs.size();

        final Float[] fractions = new Float[numPoints];

        final int track_width = slider.getWidth() - (THUMB_WIDTH / 2);

        g.translate(THUMB_WIDTH / 2, 12);

        /*
         * Find min, max, and ranges
         */
        int i = 0;

        for (Thumb thumb : sortedThumbs) {
            fractions[i] = thumb.getPosition();
            //rangeObjects.add(thumb.getObject());
            i++;
        }

        //rangeObjects.add(lastObject);
        rangeObjects = this.buildIconArray(sortedThumbs.size() + 1);

        // g.setStroke(new BasicStroke(3.0f));
        // g.drawLine(-10, 0, -10, TRACK_HEIGHT);
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

        // g.drawLine(0, TRACK_HEIGHT+55, 0, TRACK_HEIGHT+60);
        // g.drawLine(track_width, TRACK_HEIGHT+55, track_width,
        // TRACK_HEIGHT+60);
        g.setColor(Color.DARK_GRAY);
        g.setFont(largeFont);

        //		String leftTitle = TITLE1 + "gal3RG";
        //		String rightTitle = "Node Shape";
        //		
        //		int leftWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), leftTitle);
        //		int rightWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), rightTitle);
        //		int startX = (track_width/2) - (60+leftWidth+rightWidth)/2;
        //		int startY = TRACK_HEIGHT + 120;
        //		
        //		g.drawString(leftTitle, startX, startY);
        //		g.setStroke(new BasicStroke(4.0f));
        //		g.drawLine(startX + leftWidth + 10, startY-5, startX + leftWidth + 10 + 50, startY-5);
        //		g.drawLine(startX + leftWidth + 10 + 50, startY-5, startX + leftWidth + 10 + 50-12, startY-15);
        //		g.drawLine(startX + leftWidth + 10 + 50, startY-5, startX + leftWidth + 10 + 50-12, startY+5);
        //		
        //		g.drawString(rightTitle, startX + leftWidth + 10 + 50 + 10, startY);
        //		
        //		g.setStroke(new BasicStroke(1.0f));
        //		g.setColor(Color.blue);
        //		g.drawRoundRect(startX-10, startY-20, leftWidth + rightWidth + 95, 30, 10, 10);

        //		g.drawString("Node Attribute: gal3RG", (track_width / 2) - 100,
        //				TRACK_HEIGHT + 110);
        Rectangle2D rect2;

        Point2D p1 = new Point2D.Float(0, 5);
        Point2D p2 = new Point2D.Float(0, 5);
        g.setStroke(new BasicStroke(1.0f));

        int iconLocX;
        int iconLocY;

        for (i = 0; i < sortedThumbs.size(); i++) {
            newX = (int) (track_width * (fractions[i] / 100)) - (i / 2) - 2;

            // newX = (int) (track_width * (fractions[i] / 100));
            p2.setLocation(newX, 5);
            g.setColor(Color.black);
            g.setStroke(new BasicStroke(1.0f));
            rect1 = new Rectangle((int) p1.getX(), 5, newX, TRACK_HEIGHT);
            // segment.setLine(p1, p2);
            g.draw(rect1);
            // g.setColor(new Color(255, 255, 255, 100));
            g.setColor(Color.white);
            rect2 = new Rectangle((int) p1.getX() + 1, 6, newX - 1,
                    TRACK_HEIGHT - 1);
            g.fill(rect2);

            // g.setColor(Color.blue);
            // int newY = (5 + TRACK_HEIGHT)
            // - (int) ((floatProperty[i].intValue() / max) * TRACK_HEIGHT);
            // if (i == 0) {
            // g.drawLine((int) p1.getX(), newY, newX, newY);
            // } else {
            // g.drawLine((int) p1.getX(), lastY, newX, newY);
            // }
            // lastY = newY;

            // g.setColor(Color.black);
            // g.setStroke(new BasicStroke(1.0f));
            //
            // if (fractions[i] < 10) {
            // g.drawLine(newX, newY, newX + 25, newY - 35);
            // g.setColor(Color.blue);
            // g.setFont(new Font("SansSerif", Font.BOLD, 12));
            //
            // g.drawString(floatProperty[i].toString(), newX + 40,
            // newY - 48);
            // } else {
            // g.drawLine(newX, newY, newX - 25, newY + 35);
            // g.setColor(Color.blue);
            // g.setFont(new Font("SansSerif", Font.BOLD, 12));
            // g.drawString(floatProperty[i].toString(), newX - 40,
            // newY + 48);
            // }
            // g.drawLine(newX-20, newY-25, newX-40, newY-25);
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));

            Float curPositionValue = ((Double) (((fractions[i] / 100) * valueRange) -
                Math.abs(minValue))).floatValue();
            String valueString = String.format("%10.5f", curPositionValue);

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

            // curPositionValue = (Float)(curPositionValue -
            // curPositionValue%0.001);
            // g.drawString(Float.toString(curPositionValue), newX+33,
            // ARROW_BAR_Y_POSITION-11);
            iconLocX = newX -
                (((newX - (int) p1.getX()) / 2) + (ICON_SIZE / 2));
            iconLocY = ((TRACK_HEIGHT) / 2) - (ICON_SIZE / 2) + 5;

            if (ICON_SIZE < (newX - p1.getX()))
                g.drawImage(
                    ((ImageIcon) rangeObjects.get(i)).getImage(),
                    iconLocX,
                    iconLocY,
                    this);

            p1.setLocation(p2);
        }

        p2.setLocation(track_width, 5);

        rect1 = new Rectangle((int) p1.getX(), 5,
                track_width - (THUMB_WIDTH / 2), TRACK_HEIGHT);
        // segment.setLine(p1, p2);
        g.setStroke(new BasicStroke(1.0f));
        g.setColor(Color.black);
        g.draw(rect1);
        g.setColor(Color.white);
        rect2 = new Rectangle((int) p1.getX() + 1, 6,
                (track_width - (THUMB_WIDTH / 2)) - 5, TRACK_HEIGHT - 1);
        g.fill(rect2);

        iconLocX = track_width -
            (((track_width - (int) p1.getX()) / 2) + (ICON_SIZE / 2));
        iconLocY = ((TRACK_HEIGHT) / 2) - (ICON_SIZE / 2) + 5;
        g.drawImage(
            ((ImageIcon) rangeObjects.get(i)).getImage(),
            iconLocX,
            iconLocY,
            this);

        // g.setColor(Color.blue);
        // g.drawLine((int) p1.getX(), lastY, track_width, lastY);
        // g.setColor(Color.black);
        // g.draw(rect);

        /*
         * Finally, draw border line (rectangle)
         */
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(4.0f));
        g.drawRect(0, 5, track_width - 2, TRACK_HEIGHT);

        g.translate(-THUMB_WIDTH / 2, -12);

        //buildIconArray(null);
    }

    private void drawIcon(Graphics g) {
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

        return this;
    }

    protected List getRanges() {
        List range = new ArrayList();

        return range;
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
        int oldX = 0;
        int newX;

        final List<Thumb> stops = slider.getModel()
                                        .getSortedThumbs();

        int i = 1;

        for (Thumb thumb : stops) {
            newX = (int) (slider.getWidth() * (thumb.getPosition() / 100));

            if ((oldX <= x) && (x <= newX) && (V_PADDING < y) &&
                    (y < (V_PADDING + TRACK_HEIGHT)))
                return "This is region " + i;

            i++;
            oldX = newX + 1;
        }

        if ((oldX <= x) && (x <= slider.getWidth()) && (V_PADDING < y) &&
                (y < (V_PADDING + TRACK_HEIGHT)))
            return "Last Area: " + oldX + " - " + slider.getWidth() +
            " (x, y) = " + x + ", " + y;

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
        final int range = getRangeID(x, y);

        if (range != -1)
            return rangeObjects.get(getRangeID(x, y));
        else
            return null;
    }

    /*
     * Get region id.
     */
    private int getRangeID(int x, int y) {
        int oldX = 0;
        int newX;

        final List<Thumb> stops = slider.getModel()
                                        .getSortedThumbs();
        Thumb thumb;
        int i;

        for (i = 0; i < stops.size(); i++) {
            thumb = stops.get(i);
            newX = (int) (slider.getWidth() * (thumb.getPosition() / 100));

            if ((oldX <= x) && (x <= newX) && (V_PADDING < y) &&
                    (y < (V_PADDING + TRACK_HEIGHT)))
                return i;

            oldX = newX + 1;
        }

        if ((oldX <= x) && (x <= slider.getWidth()) && (V_PADDING < y) &&
                (y < (V_PADDING + TRACK_HEIGHT)))
            return i + 1;

        // Invalid range
        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param iconWidth DOCUMENT ME!
     * @param iconHeight DOCUMENT ME!
     * @param mapping DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static ImageIcon getTrackGraphicIcon(int iconWidth, int iconHeight,
        ContinuousMapping mapping) {
        final BufferedImage bi = new BufferedImage(iconWidth, iconHeight,
                BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2 = bi.createGraphics();

        // Turn Anti-alias on
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        final int leftSpace = 2;
        int trackHeight = iconHeight - 15;
        int trackWidth = iconWidth - leftSpace - 5;

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

        //List<ImageIcon> iconList = buildIconArray(objValues);
        final Point2D start = new Point2D.Float(10, 0);
        final Point2D end = new Point2D.Float(trackWidth, trackHeight);

        //		int i=1;
        //		
        //		g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        //		int strWidth;
        //		for(ContinuousMappingPoint point: points) {
        //			String p = Double.toString(point.getValue());
        //			g2.setColor(Color.black);
        //			strWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), p);
        //			g2.drawString(p, fractions[i]*iconWidth - strWidth/2, iconHeight -7);
        //			i++;
        //		}
        return new ImageIcon(bi);
    }

    private static List buildIconArray(int size) {
        List<ImageIcon> icons = new ArrayList<ImageIcon>();

        Map iconMap = NodeShape.getIconSet();

        Object[] keys = iconMap.keySet()
                               .toArray();

        for (int i = 0; i < size; i++)
            icons.add((ImageIcon) iconMap.get(keys[i]));

        return icons;
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
}
