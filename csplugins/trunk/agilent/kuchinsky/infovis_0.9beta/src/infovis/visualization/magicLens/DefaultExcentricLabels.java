/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Display excentric labels around items in a labeledComponent.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class DefaultExcentricLabels
    extends DefaultMagicLens
    implements ExcentricLabels, Comparator {
    protected Set hits;
    protected LabeledComponent.LabeledItem items[];
    protected Point2D.Double[] itemPosition;
    protected Point2D.Double[] linkPosition;
    protected Point2D.Double[] labelPosition;
    protected Point2D.Double[] left;
    protected int leftCount;
    protected Point2D.Double[] right;
    protected int rightCount;
    protected boolean xStable;
    protected boolean yStable;
    protected LabeledComponent labeledComponent;
    protected boolean visible;
    protected int maxLabels;
    protected int labelCount;
    protected boolean opaque;
    protected Color backgroundColor = Color.WHITE;
    protected Stroke wideStroke = new BasicStroke(3);
    protected Fisheye fisheye;
    protected boolean drawBounds = true;
    protected SwingPropertyChangeSupport changeSupport;

    /**
     * Constructor for DefaultExcentricLabels.
     */
    public DefaultExcentricLabels() {
        lensRadius = 50;
        bounds =
            new Rectangle2D.Float(0, 0, lensRadius, lensRadius);
        setMaxLabels(20);
    }

    public void setVisualization(LabeledComponent labeledComponent) {
        this.labeledComponent = labeledComponent;
    }

    public LabeledComponent.LabeledItem getItem(int index) {
        if (items == null) {
            items = new LabeledComponent.LabeledItem[hits.size()];
            hits.toArray(items);
        }
        return items[index];
    }

    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        if (labeledComponent == null 
                || !visible
                || !isEnabled()) {
            return;
        }
        FontMetrics fm = graphics.getFontMetrics();
        computeExcentricLabels(graphics, bounds);

        Line2D.Double line = new Line2D.Double();
        for (int i = 0; i < labelCount; i++) {
            String lab = getItem(i).getLabel();
            Shape s;
            if (lab == null) {
                lab = "item" + getItem(i);
            }

            Point2D.Double pos = labelPosition[i];
//            if (fisheye != null) {
//                pos = (Point2D.Double)fisheye.transform(pos);
//            }
            if (opaque) {
                graphics.setColor(backgroundColor);
                Rectangle2D sb = fm.getStringBounds(lab, graphics);
                
                sb.setRect(
                        pos.x + sb.getX() - 2,
                        pos.y + sb.getY() - 2,
                        sb.getWidth() + 4,
                        sb.getHeight() + 4);
                s = sb;
                if (fisheye != null) {
                    s = fisheye.transform(s);
                }
                graphics.fill(s);
                graphics.setColor(getItem(i).getColor());
                graphics.draw(s);
            }
            graphics.setColor(Color.BLACK);
            if (fisheye != null) {
                pos = (Point2D.Double)fisheye.transform(pos);
            }
            graphics.drawString(lab, (int) (pos.x), (int) (pos.y));
            line.setLine(
                itemPosition[i],
                linkPosition[i]);
            if (fisheye != null) {
                s = fisheye.transform(line);
            }
            else {
                s = line;
            }
            graphics.setColor(backgroundColor);
            Stroke save = graphics.getStroke();
            graphics.setStroke(wideStroke);
            graphics.draw(s);
            graphics.setColor(Color.BLACK);
            graphics.setStroke(save);
            graphics.draw(s);
        }
        graphics.setColor(Color.RED);
        if (drawBounds)
            graphics.draw(getBounds());
    }

    protected void computeExcentricLabels(
        Graphics2D graphics,
        Rectangle2D bounds) {
        if (labeledComponent == null
                || !isEnabled()) {
            return;
        }
        items = null;
        if (hits != null)
            hits.clear();
        else
            hits = new HashSet();
        hits = labeledComponent.pickAll(getBounds(), bounds, hits);

        labelCount = Math.min(maxLabels, hits.size());
        if (labelCount != 0) {
            computeItemPositions(graphics, bounds);
            projectLeftRight(graphics, bounds);
        }
    }

    protected void computeItemPositions(
        Graphics2D graphics,
        Rectangle2D bounds) {

        for (int i = 0; i < labelCount; i++) {
            Shape s = getItem(i).getShape();
            if (s == null) {
                itemPosition[i].setLocation(0, 0);
            }
            else {
                getItem(i).getCenterIn(getBounds(), itemPosition[i]);
            }
        }
    }

    protected double comparableValueLeft(Point2D.Double pos) {
        if (yStable)
            return pos.y;
        else
            return Math.atan2(pos.y - lensY, lensX - pos.x);
    }

    protected double comparableValueRight(Point2D.Double pos) {
        if (yStable)
            return pos.getY();
        else
            return Math.atan2(pos.y - lensY, pos.x - lensX);
    }

    protected void projectLeftRight(
        Graphics2D graphics,
        Rectangle2D bounds) {
        int i;

        leftCount = 0;
        rightCount = 0;
        double maxHeight = 0;
        FontMetrics fm = graphics.getFontMetrics();

        for (i = 0; i < labelCount; i++) {
            Point2D.Double itemPos = itemPosition[i];
            String lab = getItem(i).getLabel();
            if (lab == null)
                lab = "item" + getItem(i);
            Rectangle2D sb = fm.getStringBounds(lab, graphics);
            Point2D.Double linkPos = linkPosition[i];
            Point2D.Double labelPos = labelPosition[i];

            maxHeight = Math.max(sb.getHeight(), maxHeight);
            if (itemPosition[i].getX() < lensX) {
                double x = insertLeft(itemPos, sb, linkPos, labelPos);
                if (x < bounds.getMinX()) {
                    insertRight(itemPos, sb, linkPos, labelPos);
                    right[rightCount++] = linkPos;
                }
                else {
                    left[leftCount++] = linkPos;
                }
            }
            else {
                double x = insertRight(itemPos, sb, linkPos, labelPos);
                if (x >= bounds.getMaxX()) {
                    insertLeft(itemPos, sb, linkPos, labelPos);
                    left[leftCount++] = linkPos;
                }
                else {
                    right[rightCount++] = linkPos;
                }
            }
        }

        Arrays.sort(left, 0, leftCount, this);
        Arrays.sort(right, 0, rightCount, this);
        double yMidLeft = leftCount * maxHeight / 2;
        double yMidRight = rightCount * maxHeight / 2;
        int ascent = fm.getAscent();

        double ymin, ymax, offset;
        ymin = lensY - yMidLeft;
        ymax = lensY + leftCount * maxHeight - yMidLeft + fm.getHeight();
        if (ymin < bounds.getMinY()) {
            offset = bounds.getMinY()-ymin;
        }
        else if (ymax > bounds.getMaxY()) {
            offset = bounds.getMaxY() - ymax;
        }
        else {
            offset = 0;
        }
        for (i = 0; i < leftCount; i++) {
            Point2D.Double pos = left[i];
            pos.y = i * maxHeight + lensY - yMidLeft + ascent + offset;
        }
        ymin = lensY - yMidRight;
        ymax = lensY + rightCount * maxHeight - yMidRight+ fm.getHeight();
        if (ymin < bounds.getMinY()) {
            offset = bounds.getMinY()-ymin;
        }
        else if (ymax > bounds.getMaxY()) {
            offset = bounds.getMaxY() - ymax;
        }
        else {
            offset = 0;
        }
        for (i = 0; i < rightCount; i++) {
            Point2D.Double pos = right[i];
            pos.y = i * maxHeight + lensY - yMidRight + ascent + offset;
        }
        for (i = 0; i < linkPosition.length; i++) {
            labelPosition[i].y = linkPosition[i].y;
        }
    }

    private double insertRight(
            Point2D.Double itemPos,
            Rectangle2D sb,
            Point2D.Double linkPos,
            Point2D.Double labelPos) {
        float radius = 1.5f*lensRadius;
        if (xStable)
            linkPos.x = itemPos.x + radius;
        else
            linkPos.x = lensX + radius;
        labelPos.x = linkPos.x;
        linkPos.y = comparableValueRight(itemPos);
        return labelPos.x;
    }

    private double insertLeft(
            Point2D.Double itemPos,
            Rectangle2D sb,
            Point2D.Double linkPos,
            Point2D.Double labelPos) {
        float radius = 1.5f*lensRadius;        
        if (xStable)
            linkPos.x = itemPos.x - radius;
        else
            linkPos.x = lensX - radius;
        labelPos.x = linkPos.x - sb.getWidth();
        linkPos.y = comparableValueLeft(itemPos);
        return linkPos.x;
    }

    /**
     * Returns the visible.
     * @return boolean
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visible.
     * @param visible The visible to set
     */
    public void setVisible(boolean visible) {
        if (!isEnabled()) return;
        if (this.visible != visible) {
            this.visible = visible;
            labeledComponent.getComponent().repaint();
        }
    }
    
    public void setEnabled(boolean set) {
        if (! set) {
            setVisible(false);
        }
        super.setEnabled(set);
        labeledComponent.getComponent().repaint();
    }

    /**
     * For sorting points vertically.
     *
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare(Object o1, Object o2) {
        double d =
            ((Point2D.Double) o1).getY() - ((Point2D.Double) o2).getY();
        if (d < 0)
            return -1;
        else if (d == 0)
            return 0;
        else
            return 1;
    }
//    
//    public void restart() {
//        insideTimer.restart();
//    }
//    
//    public void stop() {
//        insideTimer.stop();
//    }
//    public static float dist2(float dx, float dy) {
//        return dx * dx + dy * dy;
//    }

 

    /**
     * Returns the maxLabels.
     * @return int
     */
    public int getMaxLabels() {
        return maxLabels;
    }

    void allocatePoints(Point2D.Double[] array) {
        for (int i = 0; i < array.length; i++)
            array[i] = new Point2D.Double();
    }

    /**
     * Sets the maxLabels.
     * @param maxLabels The maxLabels to set
     */
    public void setMaxLabels(int maxLabels) {
        this.maxLabels = maxLabels;
        itemPosition = new Point2D.Double[maxLabels];
        allocatePoints(itemPosition);
        linkPosition = new Point2D.Double[maxLabels];
        allocatePoints(linkPosition);
        labelPosition = new Point2D.Double[maxLabels];
        allocatePoints(labelPosition);
        left = new Point2D.Double[maxLabels];
        right = new Point2D.Double[maxLabels];
    }

    /**
     * Sets the focusRadius.
     *
     * @param focusSize The focusRadius to set
     */
    public void setFocusRadius(int focusSize) {
        super.setLensRadius(focusSize);
        bounds =
            new Rectangle2D.Float(0, 0, lensRadius, lensRadius);
    }

    /**
     * Returns the backgroundColor.
     * @return Color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the opaque.
     * @return boolean
     */
    public boolean isOpaque() {
        return opaque;
    }

    /**
     * Sets the backgroundColor.
     * @param backgroundColor The backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Sets the opaque.
     * @param opaque The opaque to set
     */
    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }

    public Fisheye getFisheye() {
        return fisheye;
    }

    public void setFisheye(Fisheye fisheye) {
        this.fisheye = fisheye;
    }

    public boolean isDrawBounds() {
        return drawBounds;
    }

    public void setDrawBounds(boolean b) {
        drawBounds = b;
    }

//
//    /**
//     * @see java.awt.event.MouseAdapter#mouseEntered(MouseEvent)
//     */
//    public void mouseEntered(MouseEvent e) {
//        restart();
//    }
//
//    /**
//     * @see java.awt.event.MouseAdapter#mouseExited(MouseEvent)
//     */
//    public void mouseExited(MouseEvent e) {
//        if (e.getModifiers() != 0) {
//            return;
//        }
//        stop();
//        setVisible(false);
//    }
//
//    /**
//     * @see java.awt.event.MouseAdapter#mousePressed(MouseEvent)
//     */
//    public void mousePressed(MouseEvent e) {
//        setVisible(false);
//    }
//
//    /**
//     * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
//     */
//    public void mouseMoved(MouseEvent e) {
//        if (isVisible()) {
//            if (e.getModifiers() != 0)
//                return;
//            if (dist2(lensX - e.getX(), lensY - e.getY())
//                > threshold * threshold) {
//                setVisible(false);
//                insideTimer.restart();
//            }
//        }
//        setLens(e.getX(), e.getY());
//    }
//
//    /**
//     * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
//     */
//    public void mouseDragged(MouseEvent e) {
//    }
//
//    public void mouseClicked(MouseEvent e) {
//    }
//
//    public void mouseReleased(MouseEvent e) {
//    }

}
