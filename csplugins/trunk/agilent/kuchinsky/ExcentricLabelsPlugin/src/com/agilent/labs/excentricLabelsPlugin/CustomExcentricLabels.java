package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.magicLens.DefaultExcentricLabels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class CustomExcentricLabels extends DefaultExcentricLabels {

    public void paint (Graphics2D graphics, Rectangle2D bounds) {
        Font currentFont = graphics.getFont();
        Font newFont = new Font(currentFont.getName(), currentFont.getStyle(),
                currentFont.getSize()-2);
        graphics.setFont(newFont);

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
                        sb.getHeight());
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
                // Tweaked by Ethan Cerami to take into account size of label
                if (x - sb.getWidth() < bounds.getMinX()) {
                    insertRight(itemPos, sb, linkPos, labelPos);
                    right[rightCount++] = linkPos;
                }
                else {
                    left[leftCount++] = linkPos;
                }
            }
            else {
                double x = insertRight(itemPos, sb, linkPos, labelPos);
                //  Tweaked by Ethan Ceramit to take into account size of label
                if (x + sb.getWidth() >= bounds.getMaxX()) {
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
}
