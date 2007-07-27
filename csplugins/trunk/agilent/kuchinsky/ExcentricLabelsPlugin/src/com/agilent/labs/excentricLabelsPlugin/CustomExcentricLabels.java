package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.magicLens.DefaultExcentricLabels;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class CustomExcentricLabels extends DefaultExcentricLabels {

    public void paint (Graphics2D graphics, Rectangle2D rectangle2D) {
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
}
